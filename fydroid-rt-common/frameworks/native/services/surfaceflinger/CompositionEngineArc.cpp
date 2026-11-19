/*
 * Copyright (C) 2024 The FydeOS Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// CompositionEngineArc: Per-task and per-standalone GPU composition for ARC.
//
// Discovers active tasks by scanning layer metadata (TaskId) from the
// previous frame. Each task gets an HWC virtual display and a
// WaylandTaskOutput that GPU-composites its layers into a task-sized
// buffer, routed through HWC's setClientTarget/presentDisplay path.
//
// Standalone layers (IME, toast, overlay, notification) that have no TaskId
// get their own virtual display + WaylandTaskOutput in standalone mode.
//
// Task output lifecycle:
//   - syncTaskOutputs() scans args.layers for TaskId metadata
//   - New tasks → allocateVirtualDisplay + create WaylandTaskOutput
//   - Removed tasks → destroy WaylandTaskOutput + disconnectDisplay
//   - Resized tasks → update bounds
//   - Second pass: standalone layers → individual virtual displays
//   - present() PREPENDS all outputs (before ArcDisplay) so they claim
//     layers first; ArcDisplay gets remaining SKIP layers (dropped)

#undef LOG_TAG
#define LOG_TAG "CompositionEngineArc"

#include "CompositionEngineArc.h"
#include "TaskRenderSurface.h"
#include "WaylandTaskOutput.h"

#include "DisplayHardware/HWComposer.h"
#include "hwc2/WaylandHwc2Composer.h"

#include <algorithm>
#include <cutils/properties.h>
#include <compositionengine/CompositionRefreshArgs.h>
#include <compositionengine/LayerFECompositionState.h>
#include <compositionengine/Output.h>
#include <compositionengine/impl/OutputCompositionState.h>
#include <compositionengine/impl/OutputLayer.h>
#include <log/log.h>
#include <renderengine/RenderEngine.h>
#include <renderengine/impl/ExternalTexture.h>
#include <ui/FloatRect.h>
#include <utils/Timers.h>

#include <unordered_set>

#include <cstring>
#include <limits>

namespace android::compositionengine::impl {

// Metadata keys, as std::string rather than literals.
//
// LayerFECompositionState::metadata is an unordered_map keyed by std::string,
// so find("literal") has to materialise a temporary std::string first. Every
// one of these keys is longer than libc++'s short-string buffer, which makes
// that temporary a heap allocation — paid per key, per layer, per frame in
// syncTaskOutputs(). Holding them as strings makes the lookups allocation-free.
static const std::string kKeyTaskId       = "org.chromium.arc.V1_0.TaskId";
static const std::string kKeyIsHome       = "org.chromium.arc.V1_0.IsHome";
static const std::string kKeyIsDialogTask = "org.chromium.arc.V1_0.IsDialogTask";
static const std::string kKeyWindowType   = "org.chromium.arc.V1_0.WindowType";

// TYPE_INPUT_METHOD. The Android IME never reaches the screen here: fydroid's
// IME is headless and Chrome OS composites the virtual keyboard itself.
// FydroidIME still puts up an empty window, purely so apps get correct IME
// insets, and that window arrives stamped with a TaskId (metadata back-fill
// from its re-parented animation leash). Left alone it earns a task output of
// its own, whose render target then grows to the full display and paints black
// over whatever is behind it — a fullscreen game went dark the moment the
// keyboard appeared. Nothing downstream wants these pixels, so drop the layer.
static constexpr int32_t kTypeInputMethod = 2011;

// FIRST_SUB_WINDOW / LAST_SUB_WINDOW: windows attached to a parent window —
// overflow menus, spinner dropdowns, autocomplete lists (PopupWindow lands on
// TYPE_APPLICATION_SUB_PANEL = 1002). By definition they paint inside their
// parent, so they never define the parent's size (see the footprint fold).
static constexpr int32_t kTypeApplicationOverlay = 2038;
static constexpr int32_t kTypeFirstSubWindow = 1000;
static constexpr int32_t kTypeLastSubWindow = 1999;

// The layer's Android window type, 0 when unstamped. Prefers the ARC generic
// metadata over LayerFECompositionState::type: both come from the same WM
// METADATA_WINDOW_TYPE, but a re-parented animation leash can carry the
// back-filled generic value with no composition-state type of its own.
static int32_t arcWindowType(const LayerFECompositionState& state) {
    auto it = state.metadata.find(kKeyWindowType);
    if (it != state.metadata.end() && it->second.value.size() >= sizeof(int32_t)) {
        int32_t windowType = 0;
        memcpy(&windowType, it->second.value.data(), sizeof(int32_t));
        if (windowType != 0) return windowType;
    }
    return static_cast<int32_t>(state.type);
}

static bool arcIsInputMethodLayer(const LayerFECompositionState& state) {
    return arcWindowType(state) == kTypeInputMethod;
}

static bool arcIsSubWindowLayer(const LayerFECompositionState& state) {
    const int32_t windowType = arcWindowType(state);
    return windowType >= kTypeFirstSubWindow && windowType <= kTypeLastSubWindow;
}

// An overlay that has been deliberately joined to a task (it carries a TaskId)
// is decoration ON that task's window, not a window of its own: the PIP menu is
// the case that matters, stamped into the pinned task by ArcSystemUI's
// ArcPipShellMenu so it rides inside the PIP window's buffer.
//
// It has to be contained by the task footprint for the same reason a sub-window
// is. Left in the main union it can DEFINE the window's size, and then every
// show/hide/hover repaint of the menu recomputes the union and resizes the task
// VD — measured as the PIP window flashing while the cursor moved over the menu
// buttons, and before that as the footprint jumping 900x506 -> 2532x1572 when
// the menu was still laid out at the Shell's stale rectangle.
//
// Only overlays WITH a TaskId are affected; a standalone TYPE_APPLICATION_OVERLAY
// has no task to be contained by and still takes the Pass 2 standalone path.
static bool arcIsTaskOverlayLayer(const LayerFECompositionState& state) {
    return arcWindowType(state) == kTypeApplicationOverlay;
}

// Layer-space content rect of a floating/dialog layer: the BUFFER rect deflated
// by the surfaceInset (the transparent shadow-padding border the app pads its
// window with), clamped to the layer bounds.
//
// Deriving this from the buffer rect rather than subtracting the inset from
// geomLayerBounds is what makes it idempotent. geomLayerBounds does not arrive
// in a single consistent shape: sometimes it is the raw buffer rect, sometimes
// it already carries the inset (in one observed frame it carried it on
// left/top/right but not bottom). Subtracting the inset from whatever shape
// turned up double-cropped the card on the frames that were already inset —
// SystemUI's brightness slider (buffer 2676x230, inset 58, WM frame 2560x114)
// came out 2560x114 or 2444x56 depending on which shape won the frame, and at
// 56px the bar was too short to render. Intersecting a buffer-derived content
// rect with the bounds converges on the WM frame either way.
//
// Returns false (leave the bounds alone) when there is no inset, no usable
// buffer rect — a buffer-less layer reports geomBufferSize [0,0,-1,-1] — or the
// inset would consume the whole card.
static bool arcContentLayerRect(const LayerFECompositionState& state, FloatRect* out) {
    const int32_t in = state.arcFloatingInset;
    if (in <= 0) return false;

    const Rect& buf = state.geomBufferSize;
    if (buf.getWidth() <= 2 * in || buf.getHeight() <= 2 * in) return false;

    const FloatRect& bounds = state.geomLayerBounds;
    FloatRect content(std::max(static_cast<float>(buf.left + in), bounds.left),
                      std::max(static_cast<float>(buf.top + in), bounds.top),
                      std::min(static_cast<float>(buf.right - in), bounds.right),
                      std::min(static_cast<float>(buf.bottom - in), bounds.bottom));
    if (content.right <= content.left || content.bottom <= content.top) return false;

    *out = content;
    return true;
}

void ArcDisplay::setConfiguration(
        const compositionengine::DisplayCreationArgs& args) {
    // AOSP-13: DisplayCreationArgs no longer carries a `physical` struct or
    // `useHwcVirtualDisplays`. Physical-ness is derived from the DisplayId type
    // (a PhysicalDisplayId); HWC-vs-GPU backing for a virtual display is now
    // decided upstream by SurfaceFlinger via the DisplayId kind
    // (HalVirtualDisplayId vs GpuVirtualDisplayId), so the old forced flag is
    // obsolete — ARC's notification VDs get HWC routing by being created with a
    // Hal virtual id. (TODO: confirm Java VirtualDisplays still land on HWC.)
    mIsPhysical = PhysicalDisplayId::tryCast(args.id).has_value();
    Display::setConfiguration(args);
}

bool ArcDisplay::chooseCompositionStrategy(
        std::optional<android::HWComposer::DeviceRequestedChanges>* outChanges) {
    if (mIsPhysical) {
        // Physical: force device-only composition — skip HWC validate.
        // No composable layers after reform — just triggers HWC present → flush.
        auto& state = editState();
        state.usesClientComposition = false;
        state.usesDeviceComposition = true;
        return true;
    }
    // Virtual: use normal AOSP strategy — runs HWC validate, enables
    // GPU composition so setClientTarget is called with rendered buffer.
    return Display::chooseCompositionStrategy(outChanges);
}

bool ArcDisplay::includesLayer(ui::LayerFilter filter) const {
    // AOSP-13: belongsInOutput(layerStackId, internalOnly) -> includesLayer(filter).
    // We MUST return the default (layerStack match) for physical, otherwise
    // SurfaceFlinger::computeLayerBounds skips layers on the ARC layerStack and
    // every layer ends up with mBounds = (0,0,0,0), breaking our TaskId bounds
    // scan in syncTaskOutputs().
    return Display::includesLayer(filter);
}

bool ArcDisplay::includesLayer(const sp<compositionengine::LayerFE>& layerFE) const {
    // Physical ArcDisplay claims no layers during composition — wallpaper,
    // Home, and app task containers are all rendered by WaylandTaskOutputs
    // (or dropped).  The primary HWC display just ticks present → flush.
    // The 2-arg belongsInOutput above still returns true so computeLayerBounds
    // runs; this override only prevents the layers from being claimed as
    // output layers in Output::collectVisibleLayers.
    if (mIsPhysical) return false;
    return Display::includesLayer(layerFE);
}

// Process-wide singleton — surfaceflinger creates exactly one instance via
// FydroidSurfaceFlingerFactory. Used so the wayland_service module (linked
// into the same binary) can reach the per-task outputs without a header
// dependency on this class.
static CompositionEngineArc* sInstance = nullptr;

CompositionEngineArc::CompositionEngineArc() {
    sInstance = this;
    // The HWC2-side callback registration is done by FydroidFactory in
    // createHWComposer() — at this point WaylandHwc2Composer doesn't exist
    // yet because SF calls createCompositionEngine() before createHWComposer().
}

CompositionEngineArc::~CompositionEngineArc() {
    if (auto* hwc = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
        hwc->setTaskRenderTargetSizeCallback(nullptr);
        hwc->setTaskResizeMaskHooks({});
    }
    if (sInstance == this) sInstance = nullptr;
}

CompositionEngineArc* CompositionEngineArc::getInstance() {
    return sInstance;
}

void CompositionEngineArc::setTaskRenderTargetSize(int32_t taskId,
                                                    int32_t targetW,
                                                    int32_t targetH) {
    if (taskId <= 0 || targetW <= 0 || targetH <= 0) return;
    auto it = mTaskOutputs.find(taskId);
    if (it == mTaskOutputs.end()) {
        ALOGW("setTaskRenderTargetSize: no output for taskId=%d (%dx%d)",
              taskId, targetW, targetH);
        return;
    }
    if (it->second.output) {
        it->second.output->setRenderTargetSize(targetW, targetH);
    }
}

void CompositionEngineArc::requestTaskResizeMask(int32_t taskId) {
    std::lock_guard<std::mutex> lock(mResizeMaskMutex);
    mMaskRequests.insert(taskId);
}

void CompositionEngineArc::awaitTaskResizeContent(int32_t taskId, int32_t w,
                                                  int32_t h) {
    std::lock_guard<std::mutex> lock(mResizeMaskMutex);
    if (w > 0 && h > 0) {
        mMaskAwaits[taskId] = {w, h};
    } else {
        mMaskAwaits.erase(taskId);
    }
}

// The mask is the frame on screen at drag start, diffused:
//  1. box-downsample by halves until the long side is at most kMaskMinSide —
//     each halving with linear filtering averages exactly 2x2 texels;
//  2. at that size, sResizeMaskBlur Kawase-style passes, each the average of
//     four diagonally offset copies, with the offset growing per pass — this
//     is what spreads colour across the window rather than just softening
//     edges;
//  3. bilinear-upsample by 2 kMaskUpsamples times, so Chrome's final stretch
//     to the window starts from a smooth image instead of visible blocks.
// Plain RenderEngine draws, so it needs no blur support from the GPU or
// ro.surface_flinger.supports_background_blur, and every pass after the first
// few halvings touches a few thousand texels: paid once per drag.
static constexpr int32_t kMaskMinSide = 32;
static constexpr int32_t kMaskUpsamples = 3;
static constexpr int32_t kMaskAllocGranularity = 64;
// Same export bit as the task pool (TaskRenderSurface.cpp): Chrome imports
// this buffer too, and on the Pi it must be CMA-backed to be importable.
static constexpr uint64_t kMaskUsageArcComposerExport = 1ULL << 55;

static std::shared_ptr<renderengine::ExternalTexture> allocMaskTexture(
        renderengine::RenderEngine& re, int32_t w, int32_t h, bool exported,
        sp<GraphicBuffer>* outBuffer) {
    const uint64_t usage = GraphicBuffer::USAGE_HW_RENDER |
            GraphicBuffer::USAGE_HW_TEXTURE |
            (exported ? (GraphicBuffer::USAGE_HW_COMPOSER |
                         kMaskUsageArcComposerExport) : 0);
    sp<GraphicBuffer> buffer = new GraphicBuffer(
            static_cast<uint32_t>(w), static_cast<uint32_t>(h),
            PIXEL_FORMAT_RGBA_8888, /*layerCount=*/1u, usage,
            "ArcResizeMask");
    if (buffer->initCheck() != NO_ERROR) {
        ALOGE("resize mask: allocation of %dx%d failed", w, h);
        return nullptr;
    }
    if (outBuffer) *outBuffer = buffer;
    return std::make_shared<renderengine::impl::ExternalTexture>(
            buffer, re,
            renderengine::impl::ExternalTexture::Usage::READABLE |
                    renderengine::impl::ExternalTexture::Usage::WRITEABLE);
}

// One copy of the source in a pass: where the source's IMAGE (not its whole
// buffer) lands in the destination, and its layer alpha.
struct MaskTap {
    float left, top, right, bottom;
    float alpha;
};

// Draw `src` once per tap into dst (cleared first). The source image may
// occupy only the top-left imageW x imageH of its buffer (the task pool rounds
// allocations up), so each tap's rect is widened to the whole buffer and the
// slack falls outside the clip.
//
// Taps are layered source-over with alphas 1, 1/2, 1/3, ... which makes the
// result their exact average (for opaque content): RenderEngine has no
// additive blend, and this needs none.
static std::future<renderengine::RenderEngineResult> drawMaskPass(
        renderengine::RenderEngine& re,
        const std::shared_ptr<renderengine::ExternalTexture>& src,
        int32_t imageW, int32_t imageH, const std::vector<MaskTap>& taps,
        const std::shared_ptr<renderengine::ExternalTexture>& dst,
        int32_t dstW, int32_t dstH) {
    renderengine::DisplaySettings display;
    display.physicalDisplay = Rect(dstW, dstH);
    display.clip = Rect(dstW, dstH);
    display.outputDataspace = ui::Dataspace::V0_SRGB;

    const float growX = static_cast<float>(src->getBuffer()->getWidth()) /
            static_cast<float>(imageW);
    const float growY = static_cast<float>(src->getBuffer()->getHeight()) /
            static_cast<float>(imageH);
    std::vector<renderengine::LayerSettings> layers;
    layers.reserve(taps.size());
    for (const MaskTap& tap : taps) {
        renderengine::LayerSettings layer;
        layer.geometry.boundaries = FloatRect(
                tap.left, tap.top,
                tap.left + (tap.right - tap.left) * growX,
                tap.top + (tap.bottom - tap.top) * growY);
        layer.source.buffer.buffer = src;
        layer.source.buffer.useTextureFiltering = true;
        layer.source.buffer.usePremultipliedAlpha = true;
        layer.source.buffer.isOpaque = false;
        layer.sourceDataspace = ui::Dataspace::V0_SRGB;
        layer.alpha = tap.alpha;
        layers.push_back(layer);
    }
    return re.drawLayers(display, layers, dst, /*useFramebufferCache=*/false,
                         base::unique_fd());
}

// A Kawase-style blur pass over a w x h image: four copies offset diagonally
// by `o` texels. Each copy is also grown by `o` on every side so that it still
// covers the whole target — a plain shift would leave a strip at the edges
// that only some copies reach, and the edges would come out faded. The four
// growths are symmetric, so the average does not move the image.
static std::vector<MaskTap> kawaseTaps(int32_t w, int32_t h, float o) {
    const float fw = static_cast<float>(w);
    const float fh = static_cast<float>(h);
    static constexpr float kDirs[4][2] = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
    std::vector<MaskTap> taps;
    for (int i = 0; i < 4; i++) {
        const float dx = kDirs[i][0] * o;
        const float dy = kDirs[i][1] * o;
        taps.push_back({dx - o, dy - o, fw + dx + o, fh + dy + o,
                        1.0f / static_cast<float>(i + 1)});
    }
    return taps;
}

bool CompositionEngineArc::buildResizeMask(int32_t taskId, TaskOutputEntry& entry) {
    auto* surface = entry.output ? entry.output->taskRenderSurface() : nullptr;
    ui::Size content;
    auto src = surface ? surface->lastQueuedTexture(&content) : nullptr;
    if (!src || content.width <= 0 || content.height <= 0) {
        ALOGW("resize mask: taskId=%d has no composed frame yet", taskId);
        return false;
    }
    // RenderEngine aborts SF on a source it may not sample. The pool has
    // carried USAGE_HW_TEXTURE since this was added; a frame from a buffer
    // allocated before that is simply not masked.
    if (!(src->getBuffer()->getUsage() & GraphicBuffer::USAGE_HW_TEXTURE)) {
        ALOGW("resize mask: taskId=%d source not GPU-readable, skipped", taskId);
        return false;
    }
    auto& re = getRenderEngine();
    const nsecs_t startNs = systemTime(SYSTEM_TIME_MONOTONIC);

    // persist.fydroid.resize_mask_blur: Kawase passes at the smallest size,
    // i.e. how far colour diffuses (0 = downsample/upsample only).
    static const int32_t sBlurPasses = std::clamp<int32_t>(
            property_get_int32("persist.fydroid.resize_mask_blur", 2), 0, 8);

    // Pass plan: halve down to kMaskMinSide, blur there, double back up.
    // blurOffset < 0 marks a plain resample.
    struct MaskStep {
        int32_t w, h;
        float blurOffset;
    };
    std::vector<MaskStep> steps;
    int32_t w = content.width, h = content.height;
    int32_t downs = 0;
    while (std::max(w, h) > kMaskMinSide) {
        w = std::max(1, (w + 1) / 2);
        h = std::max(1, (h + 1) / 2);
        steps.push_back({w, h, -1.0f});
        downs++;
    }
    for (int32_t i = 0; i < sBlurPasses; i++) {
        steps.push_back({w, h, 0.5f + static_cast<float>(i)});
    }
    const int32_t ups = std::min(kMaskUpsamples, downs);
    for (int32_t i = 0; i < ups; i++) {
        w *= 2;
        h *= 2;
        steps.push_back({w, h, -1.0f});
    }
    if (steps.empty()) steps.push_back({w, h, -1.0f});  // already tiny: one copy

    // The last pass lands in the exported buffer.
    const int32_t maskW = steps.back().w;
    const int32_t maskH = steps.back().h;
    if (!entry.maskBuffer ||
        static_cast<int32_t>(entry.maskBuffer->getWidth()) < maskW ||
        static_cast<int32_t>(entry.maskBuffer->getHeight()) < maskH) {
        auto roundUp = [](int32_t v) {
            return (v + kMaskAllocGranularity - 1) / kMaskAllocGranularity *
                    kMaskAllocGranularity;
        };
        const int32_t allocW = std::max(
                roundUp(maskW), entry.maskBuffer ?
                        static_cast<int32_t>(entry.maskBuffer->getWidth()) : 0);
        const int32_t allocH = std::max(
                roundUp(maskH), entry.maskBuffer ?
                        static_cast<int32_t>(entry.maskBuffer->getHeight()) : 0);
        entry.maskTexture = allocMaskTexture(re, allocW, allocH,
                                             /*exported=*/true, &entry.maskBuffer);
        if (!entry.maskTexture) {
            entry.maskBuffer = nullptr;
            return false;
        }
    }

    // First pass reads the pool buffer, whose image is its content region.
    std::shared_ptr<renderengine::ExternalTexture> cur = src;
    int32_t curW = content.width, curH = content.height;
    std::future<renderengine::RenderEngineResult> last;
    for (size_t i = 0; i < steps.size(); i++) {
        const MaskStep& step = steps[i];
        const bool final = (i + 1 == steps.size());
        auto dst = final ? entry.maskTexture
                         : allocMaskTexture(re, step.w, step.h, false, nullptr);
        if (!dst) return false;
        const std::vector<MaskTap> taps = (step.blurOffset < 0.0f)
                ? std::vector<MaskTap>{{0.0f, 0.0f, static_cast<float>(step.w),
                                        static_cast<float>(step.h), 1.0f}}
                : kawaseTaps(step.w, step.h, step.blurOffset);
        // Passes run in order on RenderEngine's thread and context, so each
        // one sees the previous pass's pixels; only the final fence matters.
        last = drawMaskPass(re, cur, curW, curH, taps, dst, step.w, step.h);
        cur = dst;
        curW = step.w;
        curH = step.h;
    }
    renderengine::RenderEngineResult result = last.get();
    if (result.status != NO_ERROR) {
        ALOGE("resize mask: taskId=%d draw failed: %d", taskId, result.status);
        return false;
    }

    auto* hwc = ::android::Hwc2::WaylandHwc2Composer::getInstance();
    const bool shown = hwc && hwc->presentTaskResizeMask(
            taskId, entry.maskBuffer, maskW, maskH, result.drawFence.release());
    ALOGI("resize mask: taskId=%d %dx%d -> %dx%d in %zu passes (%d blur), "
          "%.1f ms%s",
          taskId, content.width, content.height, maskW, maskH, steps.size(),
          sBlurPasses,
          ns2us(systemTime(SYSTEM_TIME_MONOTONIC) - startNs) / 1000.0f,
          shown ? "" : " (not shown)");
    return shown;
}

void CompositionEngineArc::serviceResizeMasks(const CompositionRefreshArgs& args,
                                              bool statePrepared) {
    std::unordered_set<int32_t> requests;
    std::unordered_map<int32_t, std::pair<int32_t, int32_t>> awaits;
    {
        std::lock_guard<std::mutex> lock(mResizeMaskMutex);
        requests.swap(mMaskRequests);
        awaits = mMaskAwaits;
    }
    for (int32_t taskId : requests) {
        auto it = mTaskOutputs.find(taskId);
        const bool built = it != mTaskOutputs.end() &&
                buildResizeMask(taskId, it->second);
        // A transition holds live frames back until its mask shows: tell the
        // container there will be none (a no-op if it already has one).
        if (!built) {
            if (auto* hwc = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
                hwc->presentTaskResizeMask(taskId, nullptr, 0, 0, -1);
            }
        }
    }
    if (awaits.empty()) return;

    // The resized content has arrived once one of the task's app windows
    // (TYPE_BASE_APPLICATION..LAST_APPLICATION_WINDOW) has LATCHED a buffer of
    // the new task size: freeform ARC windows have no Android caption or
    // insets, so the activity window's buffer is exactly the task (measured
    // on device: 460x756 buffer, task bounds [0,0][460,756]). The layer bounds
    // would not do — WM crops them to the new size before the app redraws.
    //
    // Read after refreshing the composition state: syncTaskOutputs only does
    // that on geometry frames, and a stale state would report the new buffer
    // one frame late — or never, if the app then goes idle.
    std::vector<int32_t> ready;
    for (const auto& layerFE : args.layers) {
        if (!statePrepared) {
            layerFE->prepareCompositionState(
                    compositionengine::LayerFE::StateSubset::Content);
        }
        const auto* state = layerFE->getCompositionState();
        if (!state || !state->buffer) continue;
        auto tit = state->metadata.find(kKeyTaskId);
        if (tit == state->metadata.end() ||
            tit->second.value.size() < sizeof(int32_t)) {
            continue;
        }
        int32_t taskId = 0;
        memcpy(&taskId, tit->second.value.data(), sizeof(int32_t));
        auto ait = awaits.find(taskId);
        if (ait == awaits.end()) continue;
        const int32_t type = arcWindowType(*state);
        if (type < 1 || type > 99) continue;
        const int32_t bw = static_cast<int32_t>(state->buffer->getWidth());
        const int32_t bh = static_cast<int32_t>(state->buffer->getHeight());
        if (std::abs(bw - ait->second.first) <= 2 &&
            std::abs(bh - ait->second.second) <= 2) {
            ready.push_back(taskId);
            awaits.erase(ait);
        }
    }
    if (ready.empty()) return;
    {
        std::lock_guard<std::mutex> lock(mResizeMaskMutex);
        for (int32_t taskId : ready) mMaskAwaits.erase(taskId);
    }
    auto* hwc = ::android::Hwc2::WaylandHwc2Composer::getInstance();
    for (int32_t taskId : ready) {
        ALOGI("resize mask: taskId=%d resized content latched", taskId);
        if (hwc) hwc->notifyTaskResizeContentReady(taskId);
    }
}

std::shared_ptr<compositionengine::Display> CompositionEngineArc::createDisplay(
        const compositionengine::DisplayCreationArgs& args) {
    return createDisplayTemplated<ArcDisplay>(*this, args);
}

bool CompositionEngineArc::syncTaskOutputs(const CompositionRefreshArgs& args) {
    // Ensure every layer's composition state (including metadata and type)
    // is fresh BEFORE we scan for TaskId.  AOSP's Output::updateLayerStateFromFE
    // only refreshes layers already claimed by an output, but ArcDisplay
    // (physical) claims none — and WaylandTaskOutputs don't exist yet on
    // the first frame a task appears.  Without this, metadata is stale/empty
    // and we never see TaskId → task VDs are never allocated.
    if (args.updatingGeometryThisFrame) {
        for (const auto& layerFE : args.layers) {
            layerFE->prepareCompositionState(
                    compositionengine::LayerFE::StateSubset::GeometryAndContent);
        }
    }

    // --- Pass 1: Scan layers for TaskId metadata → build active task map ---
    struct TaskInfo {
        float left = std::numeric_limits<float>::max();
        float top = std::numeric_limits<float>::max();
        float right = std::numeric_limits<float>::lowest();
        float bottom = std::numeric_limits<float>::lowest();
        // Sub-window (overflow menu / dropdown) bounds, accumulated apart from
        // the window bounds above and only used when the task has no window
        // layer at all this frame — see the fold in the create/update loop.
        float subLeft = std::numeric_limits<float>::max();
        float subTop = std::numeric_limits<float>::max();
        float subRight = std::numeric_limits<float>::lowest();
        float subBottom = std::numeric_limits<float>::lowest();
        bool hasSubLayer = false;
        // True once a non-sub-window layer (activity window, caption, decor)
        // has been unioned into left/top/right/bottom.
        bool hasWindowLayer = false;
        // True if this task has at least one layer carrying a real buffer.
        // Placeholder tasks (only SF/WM stack-background SOLID_COLOR layers,
        // no activity) get no VD; otherwise we'd allocate a full-display
        // 30 MB TaskRenderSurface for nothing.
        bool hasBufferLayer = false;
        // The ARC layer stack this task's layers live on = the display it is
        // shown on. Used to clamp the footprint to that display's size.
        uint32_t layerStack = 0;
    };
    std::unordered_map<int32_t, TaskInfo> activeTaskIds;
    // Tasks that still have an application window whose layer has no buffer
    // yet — an activity that has started but not drawn its first frame. See
    // the teardown below.
    std::unordered_set<int32_t> tasksAwaitingFirstBuffer;

    // Track standalone layers (no TaskId) — keyed by LayerFE pointer address
    struct StandaloneInfo {
        float left, top, right, bottom;
        int32_t width, height;
        sp<compositionengine::LayerFE> layerFE;
        // Secondary-display routing: the layer's ARC layer stack and the
        // wayland output index it maps to (0 = primary). Non-zero for an
        // external-HDMI Presentation.
        uint32_t layerStack = 0;
        int32_t outputIndex = 0;
    };
    std::unordered_map<uintptr_t, StandaloneInfo> activeStandalone;

    // Map each connected physical display's ARC layer stack to its wayland
    // output index, so Pass 2 can route a standalone window living on a
    // secondary display (e.g. external HDMI, layer stack 2) to the matching
    // host wl_output. Derived from live SF state every frame: each physical
    // Output carries its layerStack, and HWComposer maps its PhysicalDisplayId
    // to the HWC display id the wayland composer minted as (output index + 1).
    // Virtual displays (notification VD, capture) are not physical, so they are
    // skipped — a window on such a stack stays unrouted (dropped) below. The
    // internal panel is stack 0 → index 0, so this also re-confirms the primary.
    std::unordered_map<uint32_t, int32_t> layerStackToOutput;
    // Oriented (logical) pixel size of each connected physical display, keyed by
    // its ARC layer stack. Used to clamp a task footprint that an in-task
    // activity-transition slide pushes off the display edge — against the display
    // the task is actually on, not always the primary (an external HDMI panel is
    // a different size). orientedDisplaySpace, not displaySpace: the per-layer
    // displayBounds are in the logical (layer-stack) orientation, so a
    // portrait-native panel rotated to landscape (fydetab 1600x2560 -> 2560x1600)
    // reports 2560-wide content; displaySpace is the native 1600-wide panel and
    // would clamp the landscape content in half. A task whose stack is not found
    // (unlikely) is left unclamped — fail-open.
    std::unordered_map<uint32_t, ui::Size> layerStackToSize;
    {
        auto& hwcomposer = getHwComposer();
        for (const auto& out : args.outputs) {
            if (!out) continue;
            const auto did = out->getDisplayId();
            if (!did) continue;
            const auto physId = PhysicalDisplayId::tryCast(*did);
            if (!physId) continue;
            const auto hwcId = hwcomposer.fromPhysicalDisplayId(*physId);
            if (!hwcId) continue;
            const int32_t outIdx = static_cast<int32_t>(*hwcId) - 1;
            if (outIdx < 0) continue;
            const uint32_t stackId = out->getState().layerFilter.layerStack.id;
            layerStackToOutput[stackId] = outIdx;
            layerStackToSize[stackId] = out->getState().orientedDisplaySpace.getBounds();
        }
    }

    // Pre-scan: collect taskIds that are home tasks.  The IsHome metadata
    // is only set on *some* layers in a home task (e.g. the activity window),
    // but NOT on transient layers like rotation snapshots.  We need to block
    // the entire task, so first identify home taskIds, then skip all their layers.
    std::unordered_set<int32_t> homeTaskIds;
    // Pre-scan: collect dialog taskIds (IsDialogTask metadata). For these the
    // VD is sized to the dialog content only, and the no-buffer backing layers
    // (WM task background + DIM_BEHIND scrim, both SOLID_COLOR and full-task
    // sized) are excluded — otherwise the dialog card floats inside a large
    // opaque/dim empty window. The flag is set on a single layer of the task
    // (the dim layer), so scan all layers to catch the whole task.
    std::unordered_set<int32_t> dialogTaskIds;
    // Dialog tasks in which a real dialog CARD was identified this frame — a
    // floating window carrying a surfaceInset (shadow padding), which is what
    // arcContentLayerRect() keys on. For these the footprint is restricted to
    // the card(s) below; see the drop in the fold.
    std::unordered_set<int32_t> dialogCardTaskIds;
    // Android's DIM_BEHIND for the dialog on screen. WM's Dimmer puts a
    // buffer-less black colour layer named "Dim Layer for - <host>" under a
    // dimming dialog and sets its alpha to the window's dimAmount. That layer
    // is dropped from every task VD (see WaylandTaskOutput::includesLayer) and
    // the dialog itself becomes its own Wayland modal, so nothing drew it: the
    // modal window paints this amount as the dim around its card instead.
    // Matched before the TaskId test — a system dialog's dim has no task.
    float dialogDim = 0.0f;
    // The dim belongs to the task that HOSTS the dialog — the app for a dialog
    // split out of it, or the dialog's own task for a whole-task dialog. The
    // modal takes clicks on the dim only over that task's window, so the rest
    // of the desktop stays usable while the dialog is up.
    int32_t dialogDimTaskId = 0;
    for (const auto& lfe : args.layers) {
        const auto* st = lfe->getCompositionState();
        if (!st) continue;
        const char* dn = lfe->getDebugName();
        if (!st->buffer && dn && strncmp(dn, "Dim Layer for ", 14) == 0) {
            if (st->alpha >= dialogDim) {
                dialogDim = st->alpha;
                dialogDimTaskId = 0;
                auto itD = st->metadata.find(kKeyTaskId);
                if (itD != st->metadata.end() && itD->second.value.size() >= sizeof(int32_t)) {
                    memcpy(&dialogDimTaskId, itD->second.value.data(), sizeof(int32_t));
                }
            }
        }
        auto itT = st->metadata.find(kKeyTaskId);
        if (itT == st->metadata.end() || itT->second.value.size() < sizeof(int32_t))
            continue;
        auto itH = st->metadata.find(kKeyIsHome);
        if (itH != st->metadata.end() && itH->second.value.size() >= sizeof(int32_t)) {
            int32_t isHome = 0;
            memcpy(&isHome, itH->second.value.data(), sizeof(int32_t));
            if (isHome != 0) {
                int32_t tid = 0;
                memcpy(&tid, itT->second.value.data(), sizeof(int32_t));
                homeTaskIds.insert(tid);
            }
        }
        auto itD = st->metadata.find(kKeyIsDialogTask);
        if (itD != st->metadata.end() && itD->second.value.size() >= sizeof(int32_t)) {
            int32_t isDialog = 0;
            memcpy(&isDialog, itD->second.value.data(), sizeof(int32_t));
            if (isDialog != 0) {
                int32_t tid = 0;
                memcpy(&tid, itT->second.value.data(), sizeof(int32_t));
                dialogTaskIds.insert(tid);
                FloatRect cardRect;
                if (arcContentLayerRect(*st, &cardRect)) dialogCardTaskIds.insert(tid);
            }
        }
    }
    if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
        hwc2->setModalScrimAlpha(dialogDim, dialogDimTaskId);
    }

    // Scan for SurfaceView to trigger logging
    bool hasSurfaceView = false;
    for (const auto& lfe : args.layers) {
        const char* dn = lfe->getDebugName();
        if (dn && strstr(dn, "SurfaceView")) { hasSurfaceView = true; break; }
    }
    static int sSvLogCount = 0;
    bool doSyncLog = hasSurfaceView && (sSvLogCount++ < 10);

    if (doSyncLog) {
        ALOGD("syncTaskOutputs: %zu layers (SurfaceView present)", args.layers.size());
    }
    for (const auto& layerFE : args.layers) {
        const auto* state = layerFE->getCompositionState();
        if (!state) continue;

        if (doSyncLog) {
            auto it2 = state->metadata.find(kKeyTaskId);
            int32_t tid = 0;
            if (it2 != state->metadata.end() && it2->second.value.size() >= sizeof(int32_t)) {
                memcpy(&tid, it2->second.value.data(), sizeof(int32_t));
            }
            ALOGD("  layer '%s' layerStack=%s type=%d TaskId=%d "
                  "bounds=[%.0f,%.0f,%.0f,%.0f]",
                  layerFE->getDebugName(),
                  std::to_string(state->outputFilter.layerStack.id).c_str(),
                  state->type, tid,
                  state->geomLayerBounds.left, state->geomLayerBounds.top,
                  state->geomLayerBounds.right, state->geomLayerBounds.bottom);
        }

        // The IME window belongs to no task, whatever its metadata says (see
        // arcIsInputMethodLayer). It arrives stamped with a TaskId back-filled
        // from its re-parented animation leash; take that at face value and it
        // earns a task output of its own, whose render target then grows to the
        // full display and paints black over whatever is behind it — a
        // fullscreen game went dark the moment the keyboard appeared.
        //
        // So skip Pass 1 entirely and fall through to Pass 2, where it gets a
        // standalone virtual display and a zcr_input_method_surface_v2 like any
        // other non-task window. The composer-side half of ignoring that TaskId
        // is VirtualDisplayData::isStandalone.
        const bool isInputMethod = arcIsInputMethodLayer(*state);

        // Check for TaskId metadata
        auto it = isInputMethod ? state->metadata.end()
                                : state->metadata.find(kKeyTaskId);
        if (it != state->metadata.end() && it->second.value.size() >= sizeof(int32_t)) {
            int32_t taskId;
            memcpy(&taskId, it->second.value.data(), sizeof(int32_t));
            ALOGV("syncTaskOutputs: layer has TaskId=%d bounds=[%.0f,%.0f,%.0f,%.0f] "
                  "layerStack=%s type=%d",
                  taskId,
                  state->geomLayerBounds.left, state->geomLayerBounds.top,
                  state->geomLayerBounds.right, state->geomLayerBounds.bottom,
                  std::to_string(state->outputFilter.layerStack.id).c_str(),
                  state->type);
            if (taskId > 0) {
                // Filter out Home tasks — Chrome OS provides its own launcher.
                // Uses pre-scanned homeTaskIds to catch ALL layers in the task,
                // including transient ones (rotation snapshots) that lack IsHome.
                if (homeTaskIds.count(taskId)) continue;

                // Skip a degenerate 1x1 task window. The IME placeholder
                // (org.chromium.arc.applauncher/.PlaceholderActivity) is
                // exactly that: an invisible 1x1 editor that exists only so an
                // Android IME has something to bind to while a CHROME OS text
                // field is focused — see ArcInputMethodManagerService. It is a
                // normal activity with a normal task, so without this it mints
                // a task VD, a wl_surface and a Chrome OS window for something
                // no user can ever see or reach. (ArcAppService already keeps
                // it out of Chrome's running-app list; this is the compositor
                // half of the same exclusion.)
                //
                // Sized, not named, because the composer only ever sees TaskId
                // — never a package. Nothing real is 1x1: an app window that
                // small cannot be interacted with, and a task that reports it
                // transiently at startup simply joins a frame later.
                const float taskW =
                        state->geomLayerBounds.right - state->geomLayerBounds.left;
                const float taskH =
                        state->geomLayerBounds.bottom - state->geomLayerBounds.top;
                if (taskW <= 1.0f && taskH <= 1.0f) continue;

                // Dialog tasks: a floating dialog window in the task (e.g. the
                // PackageInstaller card) carries a transparent surfaceInset
                // (shadow) border. Crop it out of the task footprint so the
                // centered modal — and its surface-relative input — covers the
                // card only, with no empty padding margin or matching touch
                // offset. Cropping in LAYER space (see arcContentLayerRect) and
                // transforming afterwards keeps this idempotent, and lets the
                // transform scale the inset instead of assuming 1:1 display px.
                FloatRect layerRect = state->geomLayerBounds;
                FloatRect contentRect;
                const bool isDialogTaskLayer = dialogTaskIds.count(taskId) > 0;
                if (isDialogTaskLayer && arcContentLayerRect(*state, &contentRect)) {
                    layerRect = contentRect;
                } else if (isDialogTaskLayer && dialogCardTaskIds.count(taskId)) {
                    // Once a dialog task's card has been identified, ONLY the
                    // card defines the footprint. A dialog-themed activity may
                    // also own a full-screen TYPE_BASE_APPLICATION host window
                    // (ACRA's CrashReportDialog, android:theme=Theme.Dialog,
                    // shows its AlertDialog in a centered TYPE_APPLICATION
                    // window while its own base window fills the display).
                    // Unioned in, that host sized the dialog VD to the whole
                    // screen, so applyState() captured the full display instead
                    // of the card and the card kept its on-screen offset inside
                    // the buffer. Layer::fillInputFrameInfo anchors an IS_DIALOG
                    // input frame AT the buffer origin, so every pointer event
                    // then landed displaced by exactly that offset: the dialog
                    // drew correctly, took focus and accepted typing, but its
                    // buttons were unclickable (FYDE: "F-Droid has crashed"
                    // dialog, measured displacement 479,631 on 2736x1824).
                    //
                    // Only engages on frames where a card is actually present,
                    // so a task whose card has not arrived yet still sizes to
                    // its host window as before, and a dialog task with no
                    // inset-bearing window anywhere is left untouched.
                    continue;
                }

                // Compute display-space bounds from layer geometry.
                FloatRect displayBounds =
                        state->geomLayerTransform.transform(layerRect);

                // Diagnostic: identify any layer with an "endless" footprint
                // (beyond the GPU max texture size) before filtering. Known
                // culprits: WM "Dim layer" scrims with boundless crop
                // (±16000,±25600). Throttled — they recur every frame.
                constexpr float kEndlessPx = 16384.0f;
                if (displayBounds.right - displayBounds.left > kEndlessPx ||
                    displayBounds.bottom - displayBounds.top > kEndlessPx) {
                    static int sEndlessLogBudget = 32;
                    if (sEndlessLogBudget > 0) {
                        sEndlessLogBudget--;
                        ALOGW("syncTaskOutputs: endless layer '%s' taskId=%d "
                              "type=%d buffer=%d raw=[%.0f,%.0f,%.0f,%.0f] "
                              "display=[%.0f,%.0f,%.0f,%.0f]%s",
                              layerFE->getDebugName(), taskId, state->type,
                              state->buffer ? 1 : 0,
                              state->geomLayerBounds.left, state->geomLayerBounds.top,
                              state->geomLayerBounds.right, state->geomLayerBounds.bottom,
                              displayBounds.left, displayBounds.top,
                              displayBounds.right, displayBounds.bottom,
                              sEndlessLogBudget == 0 ? " (further logs squelched)" : "");
                    }
                }

                // Skip buffer-less SF/WM-internal layers (type <= 0): Task
                // containers, stack backgrounds, dim scrims. A container can
                // transiently go boundless at window creation/resize (the
                // ~32000x51200 "endless" footprint), which would inflate the
                // VD here and then bounce forever between Chrome and the
                // task resizer as a screen-covering window geometry. Real
                // windows (activity content, captions) carry buffers and
                // define the rendered footprint on their own.
                // An application window (TYPE_BASE_APPLICATION..LAST_APPLICATION
                // _WINDOW) with no buffer yet: the task is alive, between
                // activities. Remembered before the no-buffer skip below, which
                // would otherwise make it indistinguishable from a closed task.
                if (!state->buffer) {
                    const int32_t wt = arcWindowType(*state);
                    if (wt >= 1 && wt <= 99) tasksAwaitingFirstBuffer.insert(taskId);
                }

                if (state->type <= 0 && !state->buffer) continue;

                // Dialog tasks: also drop the buffer-less dim/background scrims
                // from the footprint (Chrome's system-modal window supplies the
                // dialog scrim). Mirrors the dialog filter in
                // WaylandTaskOutput::includesLayer; keeps the dialog VD sized to
                // the real dialog windows instead of the full-task dim.
                if (dialogTaskIds.count(taskId) && !state->buffer) continue;

                // Dialog tasks: also drop the freeform window CAPTION/DECOR
                // (title bar + resize border). These carry buffers (so the
                // buffer-less filters above miss them) and span the full task
                // width, which inflates the dialog VD footprint to the whole
                // task — leaving the card offset INSIDE the buffer and then
                // double-offset against the centered modal window. A frameless
                // modal has no caption, so keep the footprint = the card only.
                if (dialogTaskIds.count(taskId)) {
                    const char* dn = layerFE->getDebugName();
                    if (dn && (strstr(dn, "Caption") || strstr(dn, "Decor"))) {
                        continue;
                    }
                }

                auto& info = activeTaskIds[taskId];
                // Record the display this task is on (all its layers share one
                // stack) so the footprint can be clamped to that display's size.
                info.layerStack = state->outputFilter.layerStack.id;
                // TEMP INSTRUMENTATION: every layer that feeds the task
                // footprint, with its classification and whether it carries a
                // buffer. Gated on a property so it can be turned on for one
                // reproduction and left compiled in.
                static const bool kLogTaskBounds =
                        property_get_bool("persist.fydroid.log_task_bounds", false);
                if (kLogTaskBounds) {
                    const char* dn = layerFE->getDebugName();
                    ALOGI("taskBounds: taskId=%d layer=%s bounds=[%.0f,%.0f,%.0f,%.0f] "
                          "%dx%d sub=%d ovl=%d buf=%d",
                          taskId, dn ? dn : "?",
                          displayBounds.left, displayBounds.top,
                          displayBounds.right, displayBounds.bottom,
                          static_cast<int32_t>(displayBounds.right - displayBounds.left),
                          static_cast<int32_t>(displayBounds.bottom - displayBounds.top),
                          arcIsSubWindowLayer(*state) ? 1 : 0,
                          arcIsTaskOverlayLayer(*state) ? 1 : 0,
                          state->buffer ? 1 : 0);
                }
                // Union the task's window layers into the bounds. Whether the
                // task gets a VD at all is gated below by hasBufferLayer.
                // Sub-windows (overflow menu, dropdown) are kept in their own
                // rect: they live inside the parent window and must not be able
                // to enlarge the task footprint (see the fold below). Dialog
                // tasks keep the raw union — their card geometry is WMS's.
                if ((arcIsSubWindowLayer(*state) || arcIsTaskOverlayLayer(*state))
                        && !dialogTaskIds.count(taskId)) {
                    if (displayBounds.left < info.subLeft) info.subLeft = displayBounds.left;
                    if (displayBounds.top < info.subTop) info.subTop = displayBounds.top;
                    if (displayBounds.right > info.subRight) info.subRight = displayBounds.right;
                    if (displayBounds.bottom > info.subBottom) info.subBottom = displayBounds.bottom;
                    info.hasSubLayer = true;
                } else {
                    if (displayBounds.left < info.left) info.left = displayBounds.left;
                    if (displayBounds.top < info.top) info.top = displayBounds.top;
                    if (displayBounds.right > info.right) info.right = displayBounds.right;
                    if (displayBounds.bottom > info.bottom) info.bottom = displayBounds.bottom;
                    info.hasWindowLayer = true;
                }
                if (state->buffer) info.hasBufferLayer = true;
                continue;
            }
        }

        // --- Pass 2: Standalone layers (no TaskId) ---
        // Non-task layers with a real Android window type (TYPE_TOAST/IME/
        // OVERLAY) on layerStack 0 get their own virtual display. The WM window
        // type arrives as generic metadata ("WindowType") — state->type is
        // declared for this but never populated on AOSP-13, so we must read the
        // metadata. Internal SF layers (ColorLayer, dim, container) carry no
        // WindowType metadata and are skipped. WaylandHwc2Composer classifies
        // the type and creates the matching Wayland surface.

        // Must carry an actual graphic buffer — skips SF/WM hierarchy nodes
        // (per-display SOLID_COLOR background, dim layers, ContainerLayers)
        // which exist for SF's own bookkeeping but have no content. Without
        // this filter, every Android display+stack would allocate a
        // full-display 30 MB TaskRenderSurface for a layer that never
        // renders anything Chrome can use.
        if (!state->buffer) continue;

        // Resolve which wayland output this layer's stack belongs to.
        // AOSP-13: LayerFECompositionState.layerStackId -> outputFilter.layerStack.id
        // (UINT32_MAX == unset). The primary ARC stack is 0 → output index 0.
        // A secondary physical display (external HDMI Presentation) has its own
        // stack; map it via layerStackToOutput. A stack that is not a connected
        // physical display (notification VD etc.) is not in the map → skip, so
        // this stays a no-op for every non-secondary standalone layer.
        const uint32_t layerStackId = state->outputFilter.layerStack.id;
        int32_t outputIndex = 0;
        if (layerStackId != 0) {
            auto mit = layerStackToOutput.find(layerStackId);
            if (mit == layerStackToOutput.end()) continue;
            outputIndex = mit->second;
        }

        // Resolve the WM window type from generic metadata.
        int32_t windowType = 0;
        {
            auto wit = state->metadata.find(kKeyWindowType);
            if (wit != state->metadata.end()
                    && wit->second.value.size() >= sizeof(int32_t)) {
                memcpy(&windowType, wit->second.value.data(), sizeof(int32_t));
            }
        }

        // Must have a real Android window type (not internal SF layer).
        if (windowType <= 0) continue;

        // An IME window is HIDDEN, not destroyed, when the keyboard goes away:
        // WM keeps the window and its last buffer around for the next show. It
        // therefore still satisfies every other test below (buffer, stack, type,
        // non-degenerate size), so without this it stays in activeStandalone
        // forever, its standalone output is never removed, and the
        // zcr_input_method_surface_v2 is never destroyed. Each show then mints a
        // fresh layer and a fresh surface while the stale ones pile up in
        // Chrome's ArcVirtualKeyboardContainer — measured at 5 creates / 0
        // destroys over 5 show/hide cycles.
        //
        // That container stacks ABOVE app windows, so the leak is not merely
        // untidy: once one of the orphans is full-screen it blankets the
        // desktop and swallows every touch, and only restarting the compositor
        // clears it.
        //
        // Scoped to the IME because it is the one standalone category that
        // cycles hidden/shown continuously. Toast, overlay and notification
        // windows are short-lived and are torn down by being destroyed, so they
        // keep their current behaviour rather than gaining a per-frame
        // visibility test that could tear down and rebuild a surface mid
        // animation.
        if (windowType == kTypeInputMethod && !state->isVisible) continue;

        // Filter out wallpaper windows — Chrome OS provides its own wallpaper,
        // so we never render Android wallpapers.  Also filters any custom
        // wallpaper apps a user might install (TYPE_WALLPAPER = 2013 is a
        // privileged system window type, so this is the canonical marker).
        static constexpr int TYPE_WALLPAPER = 2013;
        if (windowType == TYPE_WALLPAPER) continue;

        // Filter out the status bar for the same reason: ChromeOS draws the
        // shelf and system tray itself, so an Android status bar presented on
        // top of it is a stray bar floating over the desktop. It is not a
        // window we ever want on screen, but it is one SystemUI keeps around
        // and occasionally draws into — which is why the bar appeared only now
        // and then, whenever something happened to put a frame in it.
        //
        // The window itself is left alone: WM still creates it and SF still
        // gives it a surface to draw into, so nothing on the Android side
        // changes. Only the composer stops presenting it. That distinction
        // matters — classifyWindowType() hands TYPE_STATUS_BAR a
        // zcr_toast_surface_v2 as a "buffer sink" so ArcSystemUI's
        // AlertWindowNotification path has somewhere to render instead of
        // crashing with "Width must be positive", and dropping the layer here
        // rather than there keeps that sink intact.
        static constexpr int TYPE_STATUS_BAR = 2000;
        if (windowType == TYPE_STATUS_BAR) continue;

        // fydroid-rt: a floating-activity/dialog buffer carries a transparent
        // surfaceInset (shadow) border on every side. Crop the standalone output
        // to the content (card) so the visible modal and the origin-anchored
        // input frame both cover the card only — no empty padding margin, and no
        // ~inset touch offset. Cropped in LAYER space (see arcContentLayerRect)
        // so it stays idempotent whether or not geomLayerBounds already carries
        // the inset, and so the transform scales it instead of assuming 1:1
        // display px.
        FloatRect standaloneRect = state->geomLayerBounds;
        FloatRect standaloneContent;
        if (arcContentLayerRect(*state, &standaloneContent)) {
            standaloneRect = standaloneContent;
        }

        // Compute bounds
        FloatRect displayBounds =
                state->geomLayerTransform.transform(standaloneRect);

        // NOT cropped to the keys, deliberately. An IME window is the full
        // display with the keys in a bottom strip, and ash places the
        // input-method surface at its container origin — it does not honour the
        // y we send. The keyboard therefore lands where it sits IN THE BUFFER,
        // so the surface has to stay full-display or the strip is drawn at the
        // top of the screen. (Measured: cropping to 2560x576 at y=944 rendered
        // the keys at y~0.) Where the keys are is instead published to Chrome
        // through the surface's INPUT REGION — see
        // WaylandInputMethodWindow::setContentTop, which is also what stops the
        // surface swallowing every tap on the desktop.

        int32_t w = static_cast<int32_t>(displayBounds.right - displayBounds.left);
        int32_t h = static_cast<int32_t>(displayBounds.bottom - displayBounds.top);

        // Skip 1x1 stubs
        if (w <= 1 || h <= 1) continue;

        uintptr_t key = reinterpret_cast<uintptr_t>(layerFE.get());

        StandaloneInfo si;
        si.left = displayBounds.left;
        si.top = displayBounds.top;
        si.right = displayBounds.right;
        si.bottom = displayBounds.bottom;
        si.width = w;
        si.height = h;
        si.layerFE = layerFE;
        si.layerStack = layerStackId;
        si.outputIndex = outputIndex;
        activeStandalone[key] = si;
    }

    bool changed = false;
    auto& hwc = getHwComposer();

    // --- Create/update task outputs ---
    ALOGV("syncTaskOutputs: %zu active tasks, %zu standalone",
          activeTaskIds.size(), activeStandalone.size());
    for (auto& [taskId, info] : activeTaskIds) {
        // Skip placeholder tasks: WM may keep TaskId-tagged stack-background
        // SOLID_COLOR layers around for tasks that have no live activity.
        // Without a real buffer-bearing layer, there is nothing to render.
        if (!info.hasBufferLayer) continue;

        // Fold in the sub-window rect (overflow PopupWindow / dropdown / spinner
        // menu). A sub-window is CONTAINED by its parent window by definition,
        // so it can never be what defines the window's size: with any real
        // window layer present its bounds are simply dropped, and it only
        // supplies the footprint when the task has nothing else this frame
        // (the bounds the plain union used to produce).
        //
        // A sub-window surface is the menu PLUS its transparent shadow border
        // (surfaceInsets, 29px on the observed overflow menu), so a menu
        // anchored near the window's right edge puts its surface a few px PAST
        // the task — KangTester's sat at x=1379 with a 411px-wide surface =
        // 1790 against a 1782px task. Unioned raw, those 8px make the footprint
        // wider than Chrome's window and applyState's same-aspect projection
        // scales the WHOLE window, content and caption, by 1782/1790 for as
        // long as the menu is open (measured on device: content 1782px → 1774px).
        // Clipping an overhanging shadow at the window edge is what a desktop
        // window does anyway — nothing outside the window is presentable, since
        // the window IS the task's Wayland surface.
        if (info.hasSubLayer && !info.hasWindowLayer) {
            info.left = info.subLeft;
            info.top = info.subTop;
            info.right = info.subRight;
            info.bottom = info.subBottom;
        }

        // Clamp the footprint origin to (0,0). A sub-window container can sit at
        // a slightly negative offset (shadow / popup placement adjustment —
        // observed pos.y = -11), which pulls the task footprint origin above the
        // content. Because the task composites with a 1:1 projection anchored at
        // (dispLeft, dispTop), a negative origin shifts the whole window
        // down/right by that many px — a gap at the top (and left) of the
        // content. fydroid always positions task content at the task origin
        // (resizeTask 0,0,w,h), so pin the origin. The fold above keeps popups
        // out of the footprint in the first place; this stays as the backstop
        // for the popup-only frames it still lets through. Dialog tasks are
        // excluded: WMS deliberately centers their card at a non-zero
        // mSrcLeft/mSrcTop and applyState relies on it.
        //
        // Also bound the footprint to the display the task is on. During an
        // in-task activity transition the exiting activity slides fully off
        // the display edge (measured: a splash exiting to display
        // x=2873..5609 on a 2736 panel), and unioning its off-screen bounds
        // inflates the task footprint — and thus the VD / render target sized
        // from it below — to ~2x the panel for the ~0.5s the slide runs,
        // before it settles. The window-grow debounce below already ignores
        // that transient for the Chrome window, but the VD size is taken
        // straight from this footprint and was otherwise unbounded.
        //
        // Both clamps must bound the footprint's SIZE, never shrink a task whose
        // content has merely been TRANSLATED past a display edge. A task brought
        // back to the front plays task_open_enter (translate 105% -> 0) or
        // task_close_enter (-105% -> 0), which moves all of its content off one
        // edge at the same size. Clipping that to the display turns the first
        // on-screen frame into a sliver — and that frame is exactly the one the
        // composer creates the Chrome window from, because the task VD shows
        // nothing (so registers no container) until some content is on screen.
        // Measured on device: Play Store 2736x1664 came back from behind a
        // fullscreen game at x=2615, was clipped to 120px, and Chrome made a
        // 60x832 DIP window that resizeTask then wrote back into the task. The
        // unclipped footprint keeps its real size, and before Chrome has sent a
        // render target applyState projects the source at the footprint origin,
        // so the new window is sized from the whole task.
        //
        // A negative origin is a translation only when most of the footprint is
        // past the edge; a shadow/popup overhang is a thin border around content
        // that is still on screen, and keeps being pinned to 0 as above.
        // Popup-only frames (no window layer) always take the pin.
        if (!dialogTaskIds.count(taskId)) {
            int32_t dispW = 0;
            int32_t dispH = 0;
            auto szit = layerStackToSize.find(info.layerStack);
            if (szit != layerStackToSize.end()) {
                dispW = szit->second.getWidth();
                dispH = szit->second.getHeight();
            }
            const bool hasWindowLayer = info.hasWindowLayer;
            auto boundAxis = [hasWindowLayer](float& lo, float& hi, int32_t disp) {
                const float raw = hi - lo;
                const bool fitsDisplay = disp <= 0 || raw <= static_cast<float>(disp);
                const bool translated = hasWindowLayer && fitsDisplay &&
                        lo < 0 && -lo > raw / 2;
                if (lo < 0 && !translated) lo = 0;
                if (disp > 0 && hi - lo > static_cast<float>(disp)) {
                    hi = lo + static_cast<float>(disp);
                }
            };
            boundAxis(info.left, info.right, dispW);
            boundAxis(info.top, info.bottom, dispH);
        }

        int32_t width = static_cast<int32_t>(info.right - info.left);
        int32_t height = static_cast<int32_t>(info.bottom - info.top);
        ALOGV("syncTaskOutputs: taskId=%d display bounds=[%.0f,%.0f,%.0f,%.0f] "
              "size=%dx%d", taskId, info.left, info.top, info.right, info.bottom,
              width, height);
        if (width <= 1 || height <= 1) continue;  // skip 1x1 stubs

        // Buffer = content size. RenderEngine maps sourceClip → destinationClip,
        // so the buffer only needs to be (width × height).
        int32_t bufW = width;
        int32_t bufH = height;
        // Display-space position of the task (from layer transform)
        int32_t dispLeft = static_cast<int32_t>(info.left);
        int32_t dispTop = static_cast<int32_t>(info.top);

        auto it = mTaskOutputs.find(taskId);
        if (it == mTaskOutputs.end()) {
            // New task — allocate HWC virtual display.
            // AOSP-13: mint the HalVirtualDisplayId ourselves; allocateVirtualDisplay
            // now takes (id, ui::Size, format*) and returns bool.
            ui::PixelFormat format = ui::PixelFormat::RGBA_8888;
            const HalVirtualDisplayId displayId{mNextVdBaseId++};
            if (!hwc.allocateVirtualDisplay(displayId, ui::Size(bufW, bufH), &format)) {
                ALOGE("CompositionEngineArc: failed to allocate virtual display "
                      "for taskId=%d (%dx%d)", taskId, bufW, bufH);
                continue;
            }

            auto output = createOutputTemplated<WaylandTaskOutput>(
                    *this, taskId);
            output->configure(*this, hwc, displayId, bufW, bufH,
                              dispLeft, dispTop);

            TaskOutputEntry entry;
            entry.output = output;
            entry.displayId = displayId;
            entry.bufWidth = bufW;
            entry.bufHeight = bufH;
            entry.displayLeft = dispLeft;
            entry.displayTop = dispTop;
            entry.createdNs = systemTime(SYSTEM_TIME_MONOTONIC);
            mTaskOutputs[taskId] = std::move(entry);

            changed = true;
            ALOGI("CompositionEngineArc: created task output for taskId=%d "
                  "buf=%dx%d at (%d,%d) displayId=%" PRIu64,
                  taskId, bufW, bufH, dispLeft, dispTop, displayId.value);
        } else {
            // Existing task — update bounds and position if changed.
            auto& entry = it->second;
            if (bufW != entry.bufWidth || bufH != entry.bufHeight ||
                dispLeft != entry.displayLeft || dispTop != entry.displayTop) {
                auto* taskOutput = static_cast<WaylandTaskOutput*>(
                        entry.output.get());
                taskOutput->updateBounds(bufW, bufH, dispLeft, dispTop);
                // Keep the HWC virtual-display record in step too — it is
                // written once at creation and answers
                // getDisplayAttribute(WIDTH/HEIGHT), so it otherwise keeps
                // the size the task had when it first appeared.
                if (auto* wh = Hwc2::WaylandHwc2Composer::getInstance()) {
                    // The size that took EFFECT, not the sampled bufW/bufH:
                    // updateBounds clamps the footprint up to the render
                    // target, and handing the raw sample on from here just
                    // re-creates the disagreement one record further along.
                    wh->setTaskVirtualDisplaySize(taskId, taskOutput->srcWidth(),
                                                  taskOutput->srcHeight());
                }
                entry.bufWidth = bufW;
                entry.bufHeight = bufH;
                entry.displayLeft = dispLeft;
                entry.displayTop = dispTop;
            }
        }

        // Track the high-water content footprint and, when it grows, make
        // sure the Chrome window is at least that large. A freeform window can
        // open at a stale launch size (narrower than the activity's laid-out
        // size); the activity then renders full-size but the host window and
        // SF render-target stay pinned narrow, squishing the content. Growing
        // the window triggers Chrome's geometry_changed echo, which realigns
        // the render target and Android task bounds. Grow-only.
        auto& entry = mTaskOutputs[taskId];
        // Tell the output whether this is a dialog task so belongsInOutput
        // drops the no-buffer backing layers from composition (keeps the
        // dialog window transparent around the card instead of an opaque box).
        bool isDialogTask = dialogTaskIds.count(taskId) > 0;
        if (auto* taskOutput = static_cast<WaylandTaskOutput*>(entry.output.get())) {
            taskOutput->setIsDialogTask(isDialogTask);
        }
        // Feed the composer's per-frame dialog flag so it routes the task to a
        // frameless WaylandModalWindow. Required because the dim layer that
        // carries IsDialogTask is excluded from the dialog VD (above), so the
        // metadata never reaches the composer via setLayerGenericMetadata.
        if (isDialogTask) {
            if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
                hwc2->markTaskDialogThisFrame(taskId);

                // Keep the modal window the same size as the card it hosts.
                // Chrome never echoes geometry for a frameless modal, so
                // without this the window keeps the size the card happened to
                // have on its first frame: the finished (larger) card then
                // overflows its own background/shadow frame and reads as a
                // card offset from — and clipped by — the dialog background.
                // Debounced like the app-window grow above: the card sweeps
                // through many sizes while the dialog animates open, and each
                // intermediate size would otherwise thrash Chrome's bounds.
                // Follows both directions (a settled card that shrank must
                // shrink the window too, or the shadow frame stays oversized).
                static constexpr int32_t kDialogStableFrames = 4;
                if (width == entry.dlgPendW && height == entry.dlgPendH) {
                    entry.dlgPendFrames++;
                } else {
                    entry.dlgPendW = width;
                    entry.dlgPendH = height;
                    entry.dlgPendFrames = 1;
                }
                if (entry.dlgPendFrames >= kDialogStableFrames &&
                    (width != entry.dlgAppliedW || height != entry.dlgAppliedH) &&
                    hwc2->resizeTaskModalWindow(taskId, width, height)) {
                    // Only latch once it actually landed — the modal window is
                    // created lazily on the first present, so an early call
                    // finds no window and must be retried next frame.
                    entry.dlgAppliedW = width;
                    entry.dlgAppliedH = height;
                }
            }
        }
        // Grow the Chrome window to fit content that lays out larger than the
        // host window opened at — but DEBOUNCED: a larger footprint must hold
        // steady for kGrowStableFrames consecutive frames first. Window/page
        // animations (e.g. Settings sliding a wider page in from off-screen)
        // sweep the footprint a few px larger every frame; without the debounce
        // the grow-only high-water mark latches that transient peak and the
        // window stays permanently wider than its settled content. An animation
        // never holds one size for several frames, so it is ignored; a genuine
        // stale launch size is stable and still grows (after a ~100ms settle).
        static constexpr int32_t kGrowStableFrames = 6;
        // The grow only repairs the stale launch size; once the window has had
        // a few seconds to settle, freeze the high-water mark. Otherwise every
        // wider in-app screen (e.g. navigating between calculator fragments)
        // permanently ratchets the host window larger and it never shrinks back
        // — the user sees the window "expand for no reason" after backing out of
        // a wide sub-screen. After the launch window, wider content scrolls/
        // clips inside the settled window like any normal desktop window.
        static constexpr nsecs_t kGrowLaunchWindowNs = 3 * 1000 * 1000 * 1000LL;
        const bool inLaunchWindow =
                entry.createdNs != 0 &&
                (systemTime(SYSTEM_TIME_MONOTONIC) - entry.createdNs)
                        < kGrowLaunchWindowNs;
        if (inLaunchWindow && (width > entry.maxW || height > entry.maxH)) {
            if (width == entry.pendW && height == entry.pendH) {
                entry.pendFrames++;
            } else {
                entry.pendW = width;
                entry.pendH = height;
                entry.pendFrames = 1;
            }
            if (entry.pendFrames >= kGrowStableFrames) {
                const int32_t markW = std::max(entry.maxW, width);
                const int32_t markH = std::max(entry.maxH, height);
                entry.pendFrames = 0;
                // Only latch once it actually landed, like the dialog path above.
                // The window is created lazily on the first present, so a
                // footprint that settles before it exists would otherwise use up
                // the mark against no window at all — measured on device: the
                // pre-window 2736x1664 frames latched the mark, so the 576px
                // content later drawn into a 120px window never grew it.
                auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance();
                if (hwc2 && hwc2->ensureTaskWindowFitsContent(taskId, markW, markH)) {
                    entry.maxW = markW;
                    entry.maxH = markH;
                }
            }
        } else {
            // Footprint no longer exceeds the mark — drop any pending candidate.
            entry.pendFrames = 0;
        }
    }

    // Remove task outputs for tasks no longer active.
    // Use a grace period (several frames) before removing, because windowing
    // mode transitions (e.g. fullscreen→split-screen) briefly remove the
    // task's layers during reparenting.
    static constexpr int32_t kMissingFrameGrace = 5;
    bool hasPendingCleanup = false;
    for (auto it = mTaskOutputs.begin(); it != mTaskOutputs.end(); ) {
        // A task counts as present only while it still has a buffer-bearing
        // layer. On close, WM removes the activity's window buffer but keeps
        // the Task=N#0 SOLID_COLOR stack-background layer for a few frames;
        // that no-buffer layer would keep the task "active" here, so the VD
        // would recompose to a bare background (a black/solid flash) before
        // teardown. Keying off hasBufferLayer makes the VD freeze its last
        // real frame (via the missingFrames skip in present()) and tear down.
        auto ait = activeTaskIds.find(it->first);
        const bool present = (ait != activeTaskIds.end() && ait->second.hasBufferLayer);
        // Still alive, just not drawn: the task switched to an activity whose
        // window exists but has not presented yet. Freeze the last frame (the
        // missingFrames skip in present()) but do NOT count down to teardown.
        // Destroying the window here made Chrome activate another window, which
        // moved a different Android task to the front and backgrounded this one
        // mid-launch: 3DMark's Vulkan NativeActivity loads its scene before its
        // first frame, quit when backgrounded, and hung in onDestroy.
        // Bounded, so a task whose window never draws cannot leave a zombie
        // window behind.
        static constexpr nsecs_t kAwaitFirstBufferMaxNs = s2ns(10);
        if (!present && tasksAwaitingFirstBuffer.count(it->first)) {
            const nsecs_t now = systemTime(SYSTEM_TIME_MONOTONIC);
            if (it->second.awaitingFirstBufferSinceNs == 0) {
                it->second.awaitingFirstBufferSinceNs = now;
                ALOGI("CompositionEngineArc: taskId=%d window not drawn yet — "
                      "keeping its output (last frame frozen)", it->first);
            }
            if (now - it->second.awaitingFirstBufferSinceNs < kAwaitFirstBufferMaxNs) {
                // No forced refresh (unlike the countdown below): the
                // window's first buffer schedules a composition by itself.
                it->second.missingFrames = 1;
                ++it;
                continue;
            }
        }
        if (present) it->second.awaitingFirstBufferSinceNs = 0;
        if (!present) {
            it->second.missingFrames++;
            if (it->second.missingFrames > kMissingFrameGrace) {
                ALOGI("CompositionEngineArc: removing task output for taskId=%d "
                      "(displayId=%" PRIu64 ")",
                      it->first, it->second.displayId.value);
                hwc.disconnectDisplay(*HalDisplayId::tryCast(it->second.displayId));
                it = mTaskOutputs.erase(it);
                changed = true;
            } else {
                hasPendingCleanup = true;
                ++it;
            }
        } else {
            it->second.missingFrames = 0;
            ++it;
        }
    }

    // If any task is in the grace period, force SF to keep composing —
    // otherwise the display goes idle (the dead task's layers no longer
    // dirty anything) and missingFrames never advances past 1, so the
    // task VD plus its TaskRenderSurface buffer leaks forever.  Same
    // trick used by setTaskRenderTargetSize() for the live-resize case.
    if (hasPendingCleanup) {
        if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
            hwc2->scheduleRefresh();
        }
    }

    // --- Create/update standalone outputs ---
    for (auto& [key, si] : activeStandalone) {
        int32_t bufW = si.width;
        int32_t bufH = si.height;
        int32_t dispLeft = static_cast<int32_t>(si.left);
        int32_t dispTop = static_cast<int32_t>(si.top);

        auto it = mStandaloneOutputs.find(key);
        if (it == mStandaloneOutputs.end()) {
            // New standalone layer — allocate HWC virtual display.
            // AOSP-13: mint the HalVirtualDisplayId; allocateVirtualDisplay returns bool.
            ui::PixelFormat format = ui::PixelFormat::RGBA_8888;
            const HalVirtualDisplayId displayId{mNextVdBaseId++};
            // Secondary display (external HDMI Presentation): arm the composer
            // so the VD it is about to create places its container on the
            // matching host output. The hint is consumed synchronously inside
            // allocateVirtualDisplay → createVirtualDisplay (same SF thread), so
            // there is no window for another VD to steal it.
            if (si.outputIndex >= 1) {
                if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
                    hwc2->setPendingStandaloneTargetOutput(si.outputIndex);
                }
            }
            // Arm the on-screen geometry the same way. The push below is keyed
            // by the output's HWC layer id, which is 0 until the output has
            // composited once, so it only lands from the second frame on — a
            // window that produces a single frame would be committed with no
            // bounds and never appear.
            if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
                hwc2->setPendingStandaloneGeometry(dispLeft, dispTop,
                                                   si.width, si.height);
            }
            const bool allocated =
                    hwc.allocateVirtualDisplay(displayId, ui::Size(bufW, bufH), &format);
            // Disarm immediately: the hint's whole life is this one call, so it
            // can never be picked up by an unrelated VD allocated later.
            if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
                hwc2->setPendingStandaloneGeometry(0, 0, 0, 0);
            }
            if (!allocated) {
                ALOGE("CompositionEngineArc: failed to allocate virtual display "
                      "for standalone layer %p (%dx%d)",
                      si.layerFE.get(), bufW, bufH);
                // allocateVirtualDisplay can reject (bad/oversized size) before
                // reaching createVirtualDisplay, which is what normally consumes
                // the hint. Clear it so a later VD does not inherit this output.
                // (The geometry hint is already disarmed above.)
                if (si.outputIndex >= 1) {
                    if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
                        hwc2->setPendingStandaloneTargetOutput(-1);
                    }
                }
                continue;
            }

            auto output = createOutputTemplated<WaylandTaskOutput>(
                    *this, si.layerFE);
            output->configure(*this, hwc, displayId, bufW, bufH,
                              dispLeft, dispTop, si.layerStack);

            StandaloneOutputEntry entry;
            entry.output = output;
            entry.displayId = displayId;
            entry.bufWidth = bufW;
            entry.bufHeight = bufH;
            entry.displayLeft = dispLeft;
            entry.displayTop = dispTop;
            entry.layerStack = si.layerStack;
            entry.outputIndex = si.outputIndex;
            mStandaloneOutputs[key] = std::move(entry);

            changed = true;
            ALOGI("CompositionEngineArc: created standalone output for layer %p "
                  "buf=%dx%d at (%d,%d) displayId=%" PRIu64 " stack=%u outIdx=%d",
                  si.layerFE.get(), bufW, bufH, dispLeft, dispTop,
                  displayId.value, si.layerStack, si.outputIndex);
        } else {
            // Existing — update bounds and position if changed
            auto& entry = it->second;
            if (bufW != entry.bufWidth || bufH != entry.bufHeight ||
                dispLeft != entry.displayLeft || dispTop != entry.displayTop) {
                auto* taskOutput = static_cast<WaylandTaskOutput*>(
                        entry.output.get());
                taskOutput->updateBounds(bufW, bufH, dispLeft, dispTop);
                entry.bufWidth = bufW;
                entry.bufHeight = bufH;
                entry.displayLeft = dispLeft;
                entry.displayTop = dispTop;
            }
        }

        // Push the standalone window's on-screen geometry (display-0 space) to
        // the composer so it can place the Wayland surface where Android put it.
        // The composed buffer always sits at the VD origin, so the composer
        // cannot derive this from setLayerDisplayFrame. Keyed by the output's
        // HWC layer id (== the composer's standaloneLayerId); that id is 0 until
        // the output first composites, so this lands from the second frame on.
        auto soIt = mStandaloneOutputs.find(key);
        if (soIt != mStandaloneOutputs.end()) {
            auto* wto = static_cast<WaylandTaskOutput*>(soIt->second.output.get());
            const uint64_t layerId = wto ? wto->hwcLayerId() : 0;
            if (layerId != 0) {
                if (auto* hwc2 = ::android::Hwc2::WaylandHwc2Composer::getInstance()) {
                    hwc2->setStandaloneScreenGeometry(
                            layerId, static_cast<int32_t>(si.left),
                            static_cast<int32_t>(si.top), si.width, si.height);
                }
            }
        }
    }

    // Remove standalone outputs for layers no longer active.
    for (auto it = mStandaloneOutputs.begin(); it != mStandaloneOutputs.end(); ) {
        if (activeStandalone.count(it->first) == 0) {
            ALOGI("CompositionEngineArc: removing standalone output "
                  "(displayId=%" PRIu64 ")",
                  it->second.displayId.value);
            hwc.disconnectDisplay(*HalDisplayId::tryCast(it->second.displayId));
            it = mStandaloneOutputs.erase(it);
            changed = true;
        } else {
            ++it;
        }
    }

    return changed;
}

void CompositionEngineArc::present(CompositionRefreshArgs& args) {
    // Sync task and standalone outputs with active layers.
    syncTaskOutputs(args);
    // Before the flag is forced below: it says whether syncTaskOutputs
    // already refreshed every layer's composition state this frame.
    serviceResizeMasks(args, args.updatingGeometryThisFrame);

    // PREPEND all outputs before ArcDisplay so they claim layers first.
    if (!mTaskOutputs.empty() || !mStandaloneOutputs.empty()) {
        std::vector<std::shared_ptr<compositionengine::Output>> newOutputs;
        newOutputs.reserve(mTaskOutputs.size() + mStandaloneOutputs.size() +
                           args.outputs.size());

        for (auto& [taskId, entry] : mTaskOutputs) {
            // A task whose layers vanished this frame is in the grace period
            // (closing, or mid-reparent). Don't recompose its VD: with no
            // claimed layers it would render an empty (black) buffer and
            // present it to Chrome, flashing the window black just before it
            // disappears. Skipping keeps the last good frame on the Wayland
            // surface until the VD is destroyed after the grace period.
            if (entry.missingFrames > 0) continue;
            newOutputs.push_back(entry.output);
        }
        for (auto& [seq, entry] : mStandaloneOutputs) {
            newOutputs.push_back(entry.output);
        }
        for (auto& output : args.outputs) {
            newOutputs.push_back(std::move(output));
        }
        args.outputs = std::move(newOutputs);
    }

    // Force geometry update whenever task/standalone outputs exist.
    // Without this, rebuildLayerStacks is skipped on frames where
    // updatingOutputGeometryThisFrame is false, which means a layer
    // that becomes visible (e.g., camera SurfaceView getting its first
    // buffer) won't be picked up by WaylandTaskOutput until the next
    // geometry-dirty event. Forcing rebuild every frame ensures layers
    // are always correctly assigned to task outputs.
    if (!mTaskOutputs.empty() || !mStandaloneOutputs.empty()) {
        args.updatingOutputGeometryThisFrame = true;
        args.updatingGeometryThisFrame = true;
    }

    // Call base class: iterates all outputs.
    CompositionEngine::present(args);
}

} // namespace android::compositionengine::impl

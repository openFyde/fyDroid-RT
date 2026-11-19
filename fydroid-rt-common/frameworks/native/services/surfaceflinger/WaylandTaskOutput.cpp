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

// WaylandTaskOutput: Per-task (or per-standalone-layer) GPU composition output.
//
// Two modes:
// 1. Task mode: filters SF layers by TaskId metadata, composites into buffer.
// 2. Standalone mode: filters by LayerFE pointer (single layer), composites.
//
// Routes composed buffer through HWC's virtual display path.
// WaylandHwc2Composer handles the actual Wayland presentation.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#undef LOG_TAG
#define LOG_TAG "WaylandTaskOutput"

#include "WaylandTaskOutput.h"
#include "TaskRenderSurface.h"

#include "DisplayHardware/HWComposer.h"

#include <compositionengine/DisplayColorProfileCreationArgs.h>
#include <compositionengine/LayerFECompositionState.h>
#include <compositionengine/impl/DisplayColorProfile.h>
#include <compositionengine/impl/OutputCompositionState.h>
#include <compositionengine/impl/OutputLayer.h>
#include <compositionengine/impl/OutputLayerCompositionState.h>

#include <log/log.h>
#include <ui/Rect.h>

#include <algorithm>
#include <cstring>

namespace android {

WaylandTaskOutput::WaylandTaskOutput(int32_t taskId)
    : mTaskId(taskId), mStandalone(false) {
    mDisplayId.value = 0;
}

WaylandTaskOutput::WaylandTaskOutput(const sp<compositionengine::LayerFE>& targetLayer)
    : mTaskId(0), mStandalone(true), mTargetLayerFE(targetLayer) {
    mDisplayId.value = 0;
}

WaylandTaskOutput::~WaylandTaskOutput() = default;

void WaylandTaskOutput::configure(const compositionengine::CompositionEngine& /*engine*/,
                                  HWComposer& hwComposer, DisplayId displayId,
                                  int32_t width, int32_t height,
                                  int32_t displayLeft, int32_t displayTop,
                                  uint32_t layerStack) {
    mDisplayId = displayId;
    mSrcWidth = width;
    mSrcHeight = height;
    mSrcLeft = displayLeft;
    mSrcTop = displayTop;
    mLayerStack = layerStack;

    // Set up the render surface (GraphicBuffer pool, 1:1 with source until
    // setRenderTargetSize pins it to Chrome's window pixel size).
    int32_t destW = width;
    int32_t destH = height;
    ui::Size size(destW, destH);
    auto renderSurface = std::make_unique<TaskRenderSurface>(
            size, hwComposer, getCompositionEngine().getRenderEngine(), displayId,
            mTaskId);
    setRenderSurface(std::move(renderSurface));

    // Set up a minimal display color profile (sRGB, no HDR).
    compositionengine::DisplayColorProfileCreationArgs colorArgs;
    colorArgs.hasWideColorGamut = false;
    colorArgs.supportedPerFrameMetadata = 0;
    setDisplayColorProfile(
            compositionengine::impl::createDisplayColorProfile(colorArgs));

    auto& state = editState();
    state.isEnabled = true;
    // A task output is an on-screen path: its pixels go to the ChromeOS
    // compositor for display on the physical panel, which SF already marks
    // secure. Only impl::Display sets this (from DisplayCreationArgs), so an
    // Output subclass defaults to isSecure=false — and a non-secure output turns
    // every FLAG_SECURE window black: OutputLayer::updateCompositionState sees
    // `layerFEState->isSecure && !outputState.isSecure` and forces client
    // composition, then BufferLayer::prepareClientComposition computes
    // blackOutLayer from the same mismatch and clears the layer instead of
    // sampling its buffer. The app renders normally and SF holds a valid buffer,
    // so nothing looks wrong anywhere except on screen.
    //
    // Marking the output secure only stops SF from blacking the content out; it
    // does NOT protect it downstream, because the composed buffer is handed to
    // Chrome. The protection is requested per-window over Wayland — OutputLayer
    // sends IsSecure metadata for a FLAG_SECURE layer and WaylandLayerContainer::
    // setSecure turns that into zcr_secure_output_v1 only_visible_on_secure_output,
    // which is what makes viz drop the quad from screen-capture copy requests.
    // The two must stay together: this line without that plumbing is a
    // FLAG_SECURE bypass.
    //
    // Capture is otherwise unaffected: screen recording and MediaProjection
    // mirror through separate SF virtual displays, which are impl::Display and
    // take isSecure from their VirtualDisplay flags. WaylandTaskOutputs are only
    // ever created for ARC task containers and standalone windows
    // (CompositionEngineArc), never for a capture sink.
    state.isSecure = true;
    // AOSP-13: layerStackId/layerStackInternal -> setLayerFilter(ui::LayerFilter).
    // Claim the requested ARC layer stack. toInternalDisplay is true only for
    // the primary stack (stack 0) — a secondary/external stack must not carry
    // the internal-only bit, or internal-only layers (display-cutout overlays)
    // would leak onto it; more importantly LayerFilter::includes only matches
    // when the output stack equals the layer stack, so this is what routes a
    // secondary-display Presentation layer to this output.
    setLayerFilter({ui::LayerStack{layerStack}, layerStack == 0 /* toInternalDisplay */});
    state.needsFiltering = false;

    if (mStandalone) {
        setName("WaylandStandaloneOutput[" + std::to_string(displayId.value) + "]");
    } else {
        setName("WaylandTaskOutput[" + std::to_string(mTaskId) + "]");
    }

    applyState();

    ALOGI("%s: configured src=%dx%d dst=%dx%d at (%d,%d) displayId=%" PRIu64,
          getName().c_str(), width, height, destW, destH,
          displayLeft, displayTop, displayId.value);
}

void WaylandTaskOutput::updateBounds(int32_t width, int32_t height,
                                     int32_t displayLeft, int32_t displayTop) {
    if (width <= 0 || height <= 0) return;

    // Never let the task footprint fall below the render target.
    //
    // The footprint is sampled from the task's layer bounds once per
    // composition, so a layer caught mid-resize contributes an intermediate
    // size — and because an idle app stops producing frames, whatever the LAST
    // composition happened to see is what the footprint keeps. Measured on
    // device growing a window from 1440 to 1520: the app layer was sampled at
    // 2560x1494 and stayed there, so the output composed 1494 rows into a
    // 1520-row buffer and the remaining 26 came out black above the shelf.
    // The same thing at 1600 froze at 1558. It only ever bites while growing —
    // shrinking happens to end on the final sample.
    //
    // The render target is not sampled, it is told to us: it comes from the
    // window geometry Chrome echoes back, so it is the authoritative size and
    // the footprint has no business being smaller. Clamping up here keeps the
    // two in sync no matter which composition the footprint was sampled on.
    if (mRenderTargetW > 0 && width < mRenderTargetW) width = mRenderTargetW;
    if (mRenderTargetH > 0 && height < mRenderTargetH) height = mRenderTargetH;

    bool sizeChanged = (width != mSrcWidth || height != mSrcHeight);
    bool posChanged = (displayLeft != mSrcLeft || displayTop != mSrcTop);
    if (!sizeChanged && !posChanged) return;

    mSrcWidth = width;
    mSrcHeight = height;
    mSrcLeft = displayLeft;
    mSrcTop = displayTop;

    applyState();

    if (sizeChanged) {
        ALOGV("%s: resized to %dx%d at (%d,%d)",
              getName().c_str(), width, height, displayLeft, displayTop);
    }
}

void WaylandTaskOutput::setIsDialogTask(bool isDialog) {
    if (mIsDialogTask == isDialog) return;
    mIsDialogTask = isDialog;
    // Re-apply the projection now that the dialog flag is known. At launch the
    // render target settles to the full window size BEFORE the task is detected
    // as a dialog (the IS_DIALOG metadata re-stamp arrives a few frames later),
    // so applyState last ran with mIsDialogTask=false (the normal top-left
    // projection). Without this re-apply, the dialog-centering branch in
    // applyState never runs until an unrelated re-layout (e.g. a screen rotation)
    // calls applyState again — which is exactly the "only centers after rotating"
    // symptom. Re-running here centers the card on first appearance.
    if (mSrcWidth > 0 && mSrcHeight > 0) applyState();
}

void WaylandTaskOutput::setRenderTargetSize(int32_t targetW, int32_t targetH) {
    if (targetW == mRenderTargetW && targetH == mRenderTargetH) return;
    ALOGI("%s: setRenderTargetSize %dx%d → %dx%d", getName().c_str(),
          mRenderTargetW, mRenderTargetH, targetW, targetH);
    mRenderTargetW = targetW;
    mRenderTargetH = targetH;
    // Same invariant as updateBounds, applied from the other side: a target
    // that grows past a footprint sampled mid-resize must pull the footprint
    // with it, or an idle app leaves the two disagreeing until something
    // recomposes.
    if (targetW > 0 && mSrcWidth > 0 && mSrcWidth < targetW) mSrcWidth = targetW;
    if (targetH > 0 && mSrcHeight > 0 && mSrcHeight < targetH) mSrcHeight = targetH;
    if (mSrcWidth > 0 && mSrcHeight > 0) applyState();
}

void WaylandTaskOutput::applyState() {
    if (mSrcWidth <= 0 || mSrcHeight <= 0) return;

    // Destination = the render target (Chrome's output-DSF window pixel size),
    // or the source footprint if Chrome hasn't set one yet.
    int32_t destW = (mRenderTargetW > 0) ? mRenderTargetW : mSrcWidth;
    int32_t destH = (mRenderTargetH > 0) ? mRenderTargetH : mSrcHeight;

    // Source rect. Default to a 1:1 projection (src == dest). Using the Android
    // task bounds (mSrcWidth/mSrcHeight) as source while dest differs makes SF
    // STRETCH content during the async split→fullscreen transition — resizeTask
    // runs on a background thread, so the task bounds in layer-stack space
    // briefly disagree with the render-target size in a way that changes the
    // aspect ratio (a non-uniform stretch). 1:1 keeps task layers filling only
    // the portion of the buffer they occupy; the remainder stays transparent.
    //
    // EXCEPTION — physical-display crossover: the source is w×primary-DSF while
    // the render target is w×output-DSF, so on a display with a different DSF
    // the two differ by a UNIFORM scale (same aspect ratio). A 1:1 projection
    // would then CLIP the source to the render-target size — on a lower-DSF
    // display the content is larger than the buffer, so the overflow is cut
    // off. When the aspect ratios match, project the FULL source into the dest
    // so SF scales the whole content down/up with no distortion and no clip.
    int32_t srcW = destW;
    int32_t srcH = destH;
    if (mRenderTargetW > 0 && mSrcWidth > 0 && mSrcHeight > 0) {
        // Same-aspect test via integer cross-multiply. The tolerance is the
        // worst case of the rounding this is here to absorb: each of the four
        // dimensions is an independent float DSF multiplication rounded to an
        // int, and a ±1px error on each moves the cross-product by at most
        // (mSrcWidth + mSrcHeight + destW + destH). A relative tolerance (this
        // was ~1%) is far too loose at these sizes and turns any small
        // src-vs-window mismatch into a silent full-window rescale: an overflow
        // menu whose shadow spilled 8px past a 1782px task made the source
        // 1790px wide, cleared a 1% test, and squeezed the whole window to
        // 1782/1790 of its size. Anything that fails this test is not a DSF
        // crossover, so it takes the 1:1 projection and clips instead.
        int64_t a = static_cast<int64_t>(mSrcWidth) * destH;
        int64_t b = static_cast<int64_t>(destW) * mSrcHeight;
        int64_t diff = (a > b) ? (a - b) : (b - a);
        const int64_t tolerance = static_cast<int64_t>(mSrcWidth) + mSrcHeight +
                destW + destH;
        if (diff <= tolerance) {
            srcW = mSrcWidth;
            srcH = mSrcHeight;
        }
    }
    // Anchor the source at the task's WINDOW origin, not at its content
    // footprint. fydroid lays every task out at the task origin (resizeTask
    // 0,0,w,h), so for an ordinary window the two are the same point and this
    // changes nothing. They part company when WMS letterboxes a
    // fixed-orientation activity inside a fullscreen task: WMS centers the
    // activity, so the footprint origin becomes the letterbox inset (measured
    // on device: Duolingo is portrait-locked and sits at x=780 of a 2560-wide
    // landscape display, 1000x1600).
    //
    // Anchoring on that inset translates the content to the buffer origin, and
    // because the source still spans the whole window it then reads 780px PAST
    // the end of the window, where there is nothing: the app renders hard
    // against the left edge with all of the letterbox slack piled up on the
    // right as black. WMS meanwhile leaves the activity's INPUT frame at the
    // centered position, so the pixels and the touch target sit in different
    // halves of the screen.
    //
    // Keeping the window origin preserves the inset as on-screen position: the
    // app composites where WMS actually put it — centered, filling whichever
    // axis constrains it — and taps land on it with no input transform. Only
    // once Chrome has given us a render target does the buffer represent the
    // window; before that it is content-sized, and the footprint origin is
    // still the right anchor.
    const int32_t srcLeft = (mRenderTargetW > 0) ? 0 : mSrcLeft;
    const int32_t srcTop = (mRenderTargetH > 0) ? 0 : mSrcTop;
    Rect srcRect(srcLeft, srcTop, srcLeft + srcW, srcTop + srcH);

    // Backstop: never size the render surface beyond the GPU's max texture size
    // (GL_MAX_TEXTURE_SIZE = 16384). Window creation/resize can briefly hand us a
    // boundless task footprint (~32000x51200, from a type=0 "infinite" container
    // layer) which Chrome echoes back as the render-target size. Allocating that
    // buffer crashes RenderEngine (MakeFromBackendTexture rejects an over-limit
    // backend texture). The over-limit value is always a one-frame transient that
    // settles to a sane size the next frame, so just keep the current valid size.
    constexpr int32_t kMaxRenderTargetPx = 16384;
    if (destW > kMaxRenderTargetPx || destH > kMaxRenderTargetPx) {
        ALOGW("%s applyState: ignoring over-limit render target %dx%d (max %d)",
              getName().c_str(), destW, destH, kMaxRenderTargetPx);
        return;
    }

    // Fullscreen dialog task (tablet mode): the dialog window (render target) is
    // the full screen, but the dialog's content is a small card that WMS already
    // laid out CENTERED on display 0 (at mSrcLeft/mSrcTop, card-sized
    // mSrcWidth×mSrcHeight). The normal projection above reads the card footprint
    // and stretches/maps it to the buffer origin, so the card ends up at the
    // top-left of the fullscreen window. Instead, composite the card at its own
    // display-0 position inside the full-size render target: it stays centered on
    // screen, and — because the card keeps its display-0 coordinates — it lines
    // up with the stock input frame, so taps land without any input-transform
    // change. Centering is therefore an SF compositing job, not a Wayland-window
    // move (the window itself is fullscreen). On rotation WMS re-centers the card
    // and updates mSrcLeft/Top + the render target, so it follows automatically.
    if (mIsDialogTask) {
        // Dialog card. Capture the card from its display-0 position
        // (mSrcLeft/mSrcTop, where WMS centered it in the fullscreen task) and
        // render it 1:1 at the BUFFER ORIGIN in a card-sized target. The
        // on-screen centering is done exactly once, by WaylandLayerContainer
        // placing the modal window at the centered offset (setBoundsInOutput).
        //
        // Previously this composited the card at its display coords inside the
        // full-size render target and relied on the modal window staying at the
        // origin (the CrOS r144 path). On r150 the modal window centers
        // itself, so keeping the card at display coords double-offset it
        // (card_display_offset + modal_offset → the card landed at ~2x the
        // centered position). Rendering to the buffer origin makes the modal's
        // offset the single source of centering.
        Rect src(mSrcLeft, mSrcTop, mSrcLeft + mSrcWidth, mSrcTop + mSrcHeight);
        Rect dst(0, 0, mSrcWidth, mSrcHeight);
        setDisplaySize(ui::Size(mSrcWidth, mSrcHeight));
        setProjection(ui::ROTATION_0, src /* layerStackSpaceRect */,
                      dst /* orientedDisplaySpaceRect */);
        ALOGI("%s: dialog card %dx%d captured from (%d,%d) to buffer origin",
              getName().c_str(), mSrcWidth, mSrcHeight, mSrcLeft, mSrcTop);
        return;
    }

    Rect destRect(destW, destH);

    ALOGI("%s applyState: src=%dx%d@(%d,%d) dest=%dx%d srcRect=[%d,%d,%d,%d] "
          "(%s)", getName().c_str(), mSrcWidth, mSrcHeight, mSrcLeft, mSrcTop,
          destW, destH, srcRect.left, srcRect.top, srcRect.right,
          srcRect.bottom,
          (srcW == mSrcWidth && srcH == mSrcHeight) ? "full-source" : "1:1");

    // AOSP-13: the raw bounds/viewport/sourceClip/destinationClip/frame/transform
    // fields were folded into ProjectionSpaces; set them via setProjection (maps
    // layerStackSpace -> orientedDisplaySpace) + setDisplaySize.
    setDisplaySize(ui::Size(destW, destH));
    setProjection(ui::ROTATION_0, srcRect /* layerStackSpaceRect */,
                  destRect /* orientedDisplaySpaceRect */);
}

TaskRenderSurface* WaylandTaskOutput::taskRenderSurface() const {
    return static_cast<TaskRenderSurface*>(getRenderSurface());
}

std::optional<DisplayId> WaylandTaskOutput::getDisplayId() const {
    return mDisplayId;
}

std::unique_ptr<compositionengine::OutputLayer> WaylandTaskOutput::createOutputLayer(
        const sp<compositionengine::LayerFE>& layerFE) const {
    auto result = compositionengine::impl::createOutputLayer(*this, layerFE);
    if (result) {
        auto& hwc = getCompositionEngine().getHwComposer();
        // AOSP-13: createLayer(HalDisplayId) returns a managed shared_ptr<HWC2::Layer>
        // (no manual destroyLayer). mDisplayId is the HWC virtual display id.
        const auto halDisplayId = HalDisplayId::tryCast(mDisplayId);
        std::shared_ptr<HWC2::Layer> hwcLayer =
                halDisplayId ? hwc.createLayer(*halDisplayId) : nullptr;
        ALOGE_IF(!hwcLayer, "Failed to create HWC layer for %s", getName().c_str());
        if (hwcLayer) mHwcLayerId = hwcLayer->getId();
        result->setHwcLayer(std::move(hwcLayer));
    }
    return result;
}

bool WaylandTaskOutput::includesLayer(
        const sp<compositionengine::LayerFE>& layerFE) const {
    const auto* state = layerFE->getCompositionState();
    if (!state) return false;
    // Must be on this output's ARC layer stack (0 = primary; a secondary
    // display's standalone output carries that display's stack). AOSP-13:
    // LayerFECompositionState.layerStackId -> outputFilter.layerStack.id.
    if (state->outputFilter.layerStack.id != mLayerStack) return false;

    // --- Standalone mode: match by LayerFE pointer ---
    // Identity, so it can only ever claim the one layer CompositionEngineArc
    // built this output for. That includes the IME window, which now gets a
    // standalone output of its own (see the task-mode filter below).
    if (mStandalone) {
        sp<compositionengine::LayerFE> target = mTargetLayerFE.promote();
        return target.get() != nullptr && layerFE.get() == target.get();
    }

    // The IME window never composites into a TASK output. It carries a TaskId
    // back-filled from its re-parented animation leash, and task mode matches
    // on exactly that, so without this it would paint itself — black and
    // full-width — into that task's buffer. Mirrors the Pass 1 skip in
    // CompositionEngineArc::syncTaskOutputs.
    //
    // Scoped to task mode deliberately: this used to reject the IME from every
    // output, which silently defeated the standalone path above — the output
    // was created but never claimed its layer, so it got no HWC layer, no
    // generic metadata, and therefore no zcr_input_method_surface_v2 and no
    // pixels. That was the last of three places "the IME is Chrome's business"
    // was baked in.
    {
        auto wit = state->metadata.find("org.chromium.arc.V1_0.WindowType");
        if (wit != state->metadata.end()
                && wit->second.value.size() >= sizeof(int32_t)) {
            int32_t windowType = 0;
            memcpy(&windowType, wit->second.value.data(), sizeof(int32_t));
            static constexpr int32_t kTypeInputMethod = 2011;
            if (windowType == kTypeInputMethod) return false;
        }
    }

    // --- Task mode: match by TaskId metadata ---
    auto it = state->metadata.find("org.chromium.arc.V1_0.TaskId");
    if (it != state->metadata.end() && it->second.value.size() >= sizeof(int32_t)) {
        int32_t taskId;
        memcpy(&taskId, it->second.value.data(), sizeof(int32_t));
        if (taskId != mTaskId) return false;
        // Dialog tasks render into a frameless modal window that paints the
        // dim itself, around the card and across the whole output, from the
        // DIM_BEHIND layer's alpha (CompositionEngineArc::syncTaskOutputs ->
        // WaylandModalWindow::setScrimAlpha). Composite only the actual dialog
        // window here and drop everything behind it:
        //  - buffer-less SOLID_COLOR layers: the DIM_BEHIND scrim + task
        //    background (the dim carries TaskId+IsDialogTask metadata and a
        //    non-zero window type, so the generic type<=0 filter misses it);
        //  - the empty TYPE_BASE_APPLICATION host window: an AlertDialog-style
        //    activity shows its dialog in a centered TYPE_APPLICATION window,
        //    while its translucent base window paints a gray fill that shows
        //    through the card's rounded/shadow margins as an empty frame.
        if (mIsDialogTask) {
            if (!state->buffer) return false;
            constexpr uint32_t kTypeBaseApplication = 1;  // WM LayoutParams type
            if (state->type == kTypeBaseApplication) return false;
            // Drop the freeform CAPTION/DECOR (title bar + resize border) from
            // the dialog VD: a frameless centered modal has none, and the
            // full-width caption would otherwise inflate the buffer and push
            // the card off its centered position. Mirrors the footprint filter
            // in CompositionEngineArc::syncTaskOutputs.
            const char* dn = layerFE->getDebugName();
            if (dn && (strstr(dn, "Caption") || strstr(dn, "Decor"))) {
                return false;
            }
        }
        // Skip buffer-less layers that would paint an "endless" SOLID_COLOR
        // rect over/under the client-composed content: WM Shell's split
        // "Dim layer" scrims and containers that transiently go boundless
        // (~32000x51200). Bounded ones stay: `type` is never populated, so a
        // blanket buffer-less skip also dropped the SurfaceView "Background
        // for" black fill, leaving the SurfaceView's punched hole transparent
        // and showing other windows' pixels through it. The footprint filter
        // in CompositionEngineArc::syncTaskOutputs still ignores all of them.
        if (state->type <= 0 && !state->buffer) {
            constexpr float kEndlessPx = 16384.0f;
            const FloatRect bounds =
                    state->geomLayerTransform.transform(state->geomLayerBounds);
            if (bounds.right - bounds.left > kEndlessPx ||
                bounds.bottom - bounds.top > kEndlessPx) {
                return false;
            }
        }
        return true;
    }

    // No TaskId — layer not claimed by this task output.
    return false;
}

bool WaylandTaskOutput::chooseCompositionStrategy(
        std::optional<android::HWComposer::DeviceRequestedChanges>* /*outChanges*/) {
    // Force full GPU composition — layers are composited into a single
    // buffer and routed through HWC's virtual display path.
    auto& state = editState();
    state.usesClientComposition = true;
    state.usesDeviceComposition = false;

    // Mark all layers as CLIENT composition type so generateClientCompositionRequests()
    // includes them in the GPU render.
    for (auto* layer : getOutputLayersOrderedByZ()) {
        auto& layerState = layer->editState();
        if (layerState.hwc) {
            // AOSP-13: hwcCompositionType is the composer3 AIDL Composition enum.
            layerState.hwc->hwcCompositionType =
                    aidl::android::hardware::graphics::composer3::Composition::CLIENT;
        }
    }
    return true;
}

bool WaylandTaskOutput::getSkipColorTransform() const {
    return true;  // No HWC color transform support.
}

compositionengine::Output::FrameFences WaylandTaskOutput::presentAndGetFrameFences() {
    size_t layerCount = getOutputLayerCount();
    ALOGV("%s: present with %zu layers (displayId=%" PRIu64 ")",
          getName().c_str(), layerCount, mDisplayId.value);

    // Route through HWC: presentAndGetReleaseFences calls
    // WaylandHwc2Composer::presentDisplay(virtualDisplayId) which presents
    // the composed buffer via the task's Wayland container.
    auto& hwc = getCompositionEngine().getHwComposer();
    // AOSP-13: presentAndGetReleaseFences takes HalDisplayId + earliestPresentTime
    // + previousPresentFence; getPresentFence takes HalDisplayId.
    const auto halDisplayId = HalDisplayId::tryCast(mDisplayId);
    FrameFences fences;
    if (!halDisplayId) {
        ALOGW("%s: display id is not a HAL display id", getName().c_str());
        return fences;
    }
    status_t err = hwc.presentAndGetReleaseFences(
            *halDisplayId, getState().earliestPresentTime, FenceTime::NO_FENCE);

    if (err == NO_ERROR) {
        fences.presentFence = hwc.getPresentFence(*halDisplayId);
    } else {
        ALOGW("%s: presentAndGetReleaseFences failed: %d",
              getName().c_str(), err);
    }

    // Client target acquire fence for layer release.
    auto* rs = getRenderSurface();
    if (rs) {
        fences.clientTargetAcquireFence = rs->getClientTargetAcquireFence();
    }

    return fences;
}

} // namespace android

#pragma clang diagnostic pop

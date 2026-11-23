/*
 * Copyright (C) 2024 Fyde Innovations
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

#pragma once

#include <atomic>
#include <climits>
#include <functional>
#include <memory>
#include <mutex>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <vector>

#include <utils/StrongPointer.h>

#include "DisplayHardware/ComposerHal.h"
#include "DisplayHardware/HWC2.h"
#include "../window/WaylandWindowTypes.h"

namespace android {

class WaylandClient;
class WaylandDisplay;
class WaylandLayerContainer;

namespace Hwc2 {

// Wayland-based implementation of the Hwc2::Composer interface.
//
// Physical displays: just vsync + flush. No layer presentation.
// Virtual displays: Each active Android task (and standalone layer) gets a
// virtual display. WaylandTaskOutput GPU-composites layers into a buffer,
// routes it via setClientTarget(), and presentDisplay() presents it through
// the task's Wayland container.
//
// Plugs into impl::HWComposer via its constructor:
//   impl::HWComposer(std::make_unique<WaylandHwc2Composer>())
class WaylandHwc2Composer : public Composer {
public:
    WaylandHwc2Composer();
    ~WaylandHwc2Composer() override;

    // Process-wide accessor — exactly one WaylandHwc2Composer is constructed
    // by FydroidSurfaceFlingerFactory. Used by wayland_service code paths
    // (e.g. WaylandLayerContainer) to invoke the registered SF-side
    // setTaskRenderScale callback without a header dependency on
    // CompositionEngineArc.
    static WaylandHwc2Composer* getInstance();

    // Per-task SF render target-size knob. WaylandHwc2Composer is the
    // bridge between the wayland_service event thread (which detects
    // display crossover and bounds_changed_in_output) and SF's
    // CompositionEngineArc (which owns WaylandTaskOutput). SF registers a
    // callback via setTaskRenderTargetSizeCallback() at startup; the
    // wayland event thread calls setTaskRenderTargetSize() with the
    // pixel-space target size (Chrome bounds × output DSF) so the
    // composed buffer always matches Chrome's window pixel bounds.
    using TaskRenderTargetSizeCallback =
            std::function<void(int32_t taskId, int32_t targetW, int32_t targetH)>;
    void setTaskRenderTargetSizeCallback(TaskRenderTargetSizeCallback cb);
    void setTaskRenderTargetSize(int32_t taskId, int32_t targetW, int32_t targetH);

    // Drag-resize mask bridge. While the user drags an ARC window's edge the
    // container shows a blurred snapshot of the window, stretched to the
    // window by Chrome (wp_viewport), instead of making SF recompose at every
    // intermediate size; see WaylandLayerContainer::onDragStarted.
    //
    // container -> SF (wayland event thread), via hooks SF registers:
    //   requestTaskResizeMask   build a mask from the frame on screen now; SF
    //                           answers with presentTaskResizeMask.
    //   awaitTaskResizeContent  the drag ended and the task was resized to
    //                           w x h task pixels; SF answers with
    //                           notifyTaskResizeContentReady once an app
    //                           window buffer of that size is latched.
    //                           0x0 cancels.
    // SF -> container (SF composition thread):
    //   presentTaskResizeMask, notifyTaskResizeContentReady.
    struct TaskResizeMaskHooks {
        std::function<void(int32_t taskId)> requestMask;
        std::function<void(int32_t taskId, int32_t w, int32_t h)> awaitContent;
    };
    void setTaskResizeMaskHooks(TaskResizeMaskHooks hooks);
    void requestTaskResizeMask(int32_t taskId);
    void awaitTaskResizeContent(int32_t taskId, int32_t w, int32_t h);
    // maskW/maskH: the region of `mask` that holds the image (the buffer may
    // be larger). Takes ownership of acquireFence. False if the container is
    // gone or no longer wants a mask.
    bool presentTaskResizeMask(int32_t taskId, const sp<GraphicBuffer>& mask,
                               int32_t maskW, int32_t maskH, int acquireFence);
    void notifyTaskResizeContentReady(int32_t taskId);

    // Keep a task's virtual-display record in step with the size its content
    // is actually composed at. VirtualDisplayData::width/height are written
    // once at createVirtualDisplay and answer
    // getDisplayAttribute(WIDTH/HEIGHT), so without this they keep the size
    // the task happened to have when it first appeared — a window restored to
    // freeform and then maximized left the record at 1744x1106 while the task,
    // its layer, its buffer and the render target had all moved on to
    // 2560x1520. No-op if the task has no virtual display.
    void setTaskVirtualDisplaySize(int32_t taskId, int32_t width, int32_t height);

    // Grow a task's Chrome window so it is at least as large as the Android
    // content. Called from SF (CompositionEngineArc::syncTaskOutputs) with the
    // task's content footprint in primary-DSF pixels. Fixes the case where a
    // freeform window opens at a stale/launch size narrower than the activity's
    // laid-out size — the activity renders full-size but the host window and
    // SF buffer stay pinned narrow, squishing the content. Grow-only; routes to
    // the container's ensureWindowFitsContent(). Same SF composition thread as
    // presentDisplay(), so direct Wayland calls are safe.
    // Returns false when the task has no container with a live window yet, i.e.
    // the size was not checked against any window and the caller must retry.
    bool ensureTaskWindowFitsContent(int32_t taskId, int32_t contentW,
                                     int32_t contentH);

    // Mark a task's virtual display as a dialog for the current frame. Called
    // from SF (CompositionEngineArc::syncTaskOutputs) which detects dialog
    // tasks via IsDialogTask layer metadata. presentDisplay() consumes the flag
    // to drive container->setIsDialog(), which routes the task to a frameless
    // WaylandModalWindow. This SF-fed path is required because the dialog's dim
    // layer (which carries IsDialogTask) is excluded from the dialog VD, so the
    // metadata never reaches setLayerGenericMetadata().
    void markTaskDialogThisFrame(int32_t taskId);

    // Resize a dialog task's frameless modal window to exactly the composed
    // card size (primary-DSF pixels) and re-centre it. Routes to the
    // container's updateModalCardSize(). Called from SF once the card size has
    // settled, so the modal's background/shadow frame always contains — and
    // stays concentric with — the card the dialog VD renders. Returns false if
    // the task has no modal container yet, so the caller can retry next frame.
    bool resizeTaskModalWindow(int32_t taskId, int32_t cardW, int32_t cardH);

    // The strongest Android DIM_BEHIND on screen this frame (0..1, 0 = none)
    // and the task hosting it, from SF's composition. Modal windows paint the
    // dim around their card and take clicks on it only over that task's
    // window. Pushed to them only when it changes; called every frame from SF.
    void setModalScrimAlpha(float alpha, int32_t dimTaskId);

    // True while the ChromeOS compositor still holds the wl_buffer imported for
    // this GraphicBuffer — i.e. between the commit that attached it and
    // wl_buffer.release.
    //
    // SF's per-task client composition renders straight into a small pool of
    // GraphicBuffers (TaskRenderSurface) with no BufferQueue in front of it, so
    // nothing else stops it from painting over a buffer Chrome is still
    // sampling. This is that back-pressure. Called from the SF composition
    // thread, the same thread that imports buffers, so the cache lookup needs no
    // lock; the held flag itself is atomic because the release arrives on the
    // Wayland event thread.
    bool isBufferHeldByCompositor(uint64_t bufferId) const;

    // Forces SF to wake up and run another composition cycle.  Used by
    // CompositionEngineArc when it has dead-task cleanup pending — without
    // this nudge, the display goes idle and the missingFrame grace counter
    // never advances, so dead VDs (and their TaskRenderSurface buffers)
    // leak forever.
    void scheduleRefresh();

    // Pushes a standalone window's on-screen geometry (display-0 space) to the
    // VD whose standaloneLayerId == hwcLayerId. CompositionEngineArc computes
    // this from the Android window's display frame; the composer feeds it to
    // zcr_toast_surface set_bounds_in_output so toast/IME/overlay windows land
    // where Android placed them instead of the VD origin (top-left).
    void setStandaloneScreenGeometry(uint64_t hwcLayerId, int32_t x, int32_t y,
                                     int32_t w, int32_t h);

    // Arm the wayland output index that the NEXT created standalone virtual
    // display should place its container on. CompositionEngineArc calls this
    // immediately before hwc.allocateVirtualDisplay() for a window living on a
    // secondary physical display (e.g. an external-HDMI Presentation on layer
    // stack 2 → output index 1). createVirtualDisplay() consumes it into
    // VirtualDisplayData::targetOutputIndex and resets it to -1. Both calls run
    // synchronously on the SF main thread with nothing in between, so the hint
    // is race-free (same pattern as the notification-VD size flag). -1 (unset)
    // means "primary" (index 0), preserving today's behavior for every window
    // that does not arm it.
    void setPendingStandaloneTargetOutput(int32_t outputIndex);

    // Arm the on-screen geometry for the NEXT created standalone virtual
    // display, in the same synchronous window as the target-output hint above.
    // setStandaloneScreenGeometry() cannot cover the first frame: it is keyed by
    // the output's HWC layer id, which is still 0 until the output has
    // composited once. A window that only ever produces one frame — an overlay
    // put up and taken down again, like a lyrics bar — therefore presented with
    // no bounds at all, and Chrome never placed (so never showed) its surface.
    void setPendingStandaloneGeometry(int32_t x, int32_t y, int32_t w, int32_t h);

    // Drop every cached pointer to `container` — called from
    // ~WaylandLayerContainer, so a container can never outlive the caches that
    // reference it. VirtualDisplayData::container and NotifLayerData::container
    // are looked-up-once raw pointers to memory owned by a WaylandLayerManager,
    // and the container is destroyed from half a dozen places (destroyContainer,
    // destroyStandaloneContainer, drainClosedContainers, and the category-change
    // and duplicate-notifKey rebuilds in registerStandaloneContainer). Only the
    // destructor is common to all of them.
    //
    // The isClosed() re-lookup guard those caches use is not a substitute: it is
    // itself a read through the stale pointer. A freed container whose block has
    // been recycled reads back as live and not-closed, and presentDisplay then
    // walks into it — SurfaceFlinger SIGSEGV'd in ensureWindow() on a garbage
    // mClient this way.
    void forgetContainer(const WaylandLayerContainer* container);

    // Composer interface (AOSP-13 Hwc2::Composer; V3_0 = composer3 AIDL)
    bool isSupported(OptionalFeature) const override;
    std::vector<V3_0::Capability> getCapabilities() override;
    std::string dumpDebugInfo() override;

    void registerCallback(HWC2::ComposerCallback& callback) override;

    void resetCommands() override;
    Error executeCommands() override;

    uint32_t getMaxVirtualDisplayCount() override;
    Error createVirtualDisplay(uint32_t width, uint32_t height, PixelFormat* format,
                               Display* outDisplay) override;
    Error destroyVirtualDisplay(Display display) override;

    Error acceptDisplayChanges(Display display) override;

    Error createLayer(Display display, Layer* outLayer) override;
    Error destroyLayer(Display display, Layer layer) override;

    Error getActiveConfig(Display display, Config* outConfig) override;
    Error getChangedCompositionTypes(
            Display display, std::vector<Layer>* outLayers,
            std::vector<V3_0::Composition>* outTypes) override;
    Error getColorModes(Display display, std::vector<ColorMode>* outModes) override;
    Error getDisplayAttribute(Display display, Config config,
                              IComposerClient::Attribute attribute, int32_t* outValue) override;
    Error getDisplayConfigs(Display display, std::vector<Config>* outConfigs) override;
    Error getDisplayName(Display display, std::string* outName) override;

    Error getDisplayRequests(Display display, uint32_t* outDisplayRequestMask,
                             std::vector<Layer>* outLayers,
                             std::vector<uint32_t>* outLayerRequestMasks) override;

    Error getDozeSupport(Display display, bool* outSupport) override;
    Error hasDisplayIdleTimerCapability(Display display, bool* outSupport) override;
    Error getHdrCapabilities(Display display, std::vector<Hdr>* outTypes,
                             float* outMaxLuminance, float* outMaxAverageLuminance,
                             float* outMinLuminance) override;

    Error getReleaseFences(Display display, std::vector<Layer>* outLayers,
                           std::vector<int>* outReleaseFences) override;

    Error presentDisplay(Display display, int* outPresentFence) override;

    Error setActiveConfig(Display display, Config config) override;
    Error setClientTarget(Display display, uint32_t slot, const sp<GraphicBuffer>& target,
                          int acquireFence, Dataspace dataspace,
                          const std::vector<IComposerClient::Rect>& damage) override;
    Error setColorMode(Display display, ColorMode mode, RenderIntent renderIntent) override;
    Error setColorTransform(Display display, const float* matrix) override;
    Error setOutputBuffer(Display display, const native_handle_t* buffer,
                          int releaseFence) override;
    Error setPowerMode(Display display, IComposerClient::PowerMode mode) override;
    Error setVsyncEnabled(Display display, IComposerClient::Vsync enabled) override;

    Error setClientTargetSlotCount(Display display) override;

    Error validateDisplay(Display display, nsecs_t expectedPresentTime, uint32_t* outNumTypes,
                          uint32_t* outNumRequests) override;

    Error presentOrValidateDisplay(Display display, nsecs_t expectedPresentTime,
                                   uint32_t* outNumTypes, uint32_t* outNumRequests,
                                   int* outPresentFence, uint32_t* state) override;

    Error setCursorPosition(Display display, Layer layer, int32_t x, int32_t y) override;
    Error setLayerBuffer(Display display, Layer layer, uint32_t slot,
                         const sp<GraphicBuffer>& buffer, int acquireFence) override;
    Error setLayerSurfaceDamage(Display display, Layer layer,
                                const std::vector<IComposerClient::Rect>& damage) override;
    Error setLayerBlendMode(Display display, Layer layer,
                            IComposerClient::BlendMode mode) override;
    Error setLayerColor(Display display, Layer layer,
                        const V3_0::Color& color) override;
    Error setLayerCompositionType(Display display, Layer layer,
                                  V3_0::Composition type) override;
    Error setLayerDataspace(Display display, Layer layer, Dataspace dataspace) override;
    Error setLayerDisplayFrame(Display display, Layer layer,
                               const IComposerClient::Rect& frame) override;
    Error setLayerPlaneAlpha(Display display, Layer layer, float alpha) override;
    Error setLayerSidebandStream(Display display, Layer layer,
                                 const native_handle_t* stream) override;
    Error setLayerSourceCrop(Display display, Layer layer,
                             const IComposerClient::FRect& crop) override;
    Error setLayerTransform(Display display, Layer layer, Transform transform) override;
    Error setLayerVisibleRegion(Display display, Layer layer,
                                const std::vector<IComposerClient::Rect>& visible) override;
    Error setLayerZOrder(Display display, Layer layer, uint32_t z) override;

    // Composer HAL 2.2
    Error setLayerPerFrameMetadata(
            Display display, Layer layer,
            const std::vector<IComposerClient::PerFrameMetadata>& perFrameMetadatas) override;
    std::vector<IComposerClient::PerFrameMetadataKey> getPerFrameMetadataKeys(
            Display display) override;
    Error getRenderIntents(Display display, ColorMode colorMode,
            std::vector<RenderIntent>* outRenderIntents) override;
    Error getDataspaceSaturationMatrix(Dataspace dataspace, mat4* outMatrix) override;

    // Composer HAL 2.3
    Error getDisplayIdentificationData(Display display, uint8_t* outPort,
                                       std::vector<uint8_t>* outData) override;
    Error setLayerColorTransform(Display display, Layer layer, const float* matrix) override;
    Error getDisplayedContentSamplingAttributes(Display display, PixelFormat* outFormat,
                                                Dataspace* outDataspace,
                                                uint8_t* outComponentMask) override;
    Error setDisplayContentSamplingEnabled(Display display, bool enabled,
                                           uint8_t componentMask, uint64_t maxFrames) override;
    Error getDisplayedContentSample(Display display, uint64_t maxFrames, uint64_t timestamp,
                                    DisplayedFrameStats* outStats) override;
    Error setLayerPerFrameMetadataBlobs(
            Display display, Layer layer,
            const std::vector<PerFrameMetadataBlob>& metadata) override;
    Error setDisplayBrightness(Display display, float brightness, float brightnessNits,
                               const DisplayBrightnessOptions& options) override;

    // Composer HAL 2.4
    Error getDisplayCapabilities(Display display,
                                 std::vector<V3_0::DisplayCapability>* outCapabilities) override;
    V2_4::Error getDisplayConnectionType(
            Display display, IComposerClient::DisplayConnectionType* outType) override;
    V2_4::Error getDisplayVsyncPeriod(Display display,
                                      VsyncPeriodNanos* outVsyncPeriod) override;
    V2_4::Error setActiveConfigWithConstraints(
            Display display, Config config,
            const IComposerClient::VsyncPeriodChangeConstraints& vsyncPeriodChangeConstraints,
            VsyncPeriodChangeTimeline* outTimeline) override;

    V2_4::Error setAutoLowLatencyMode(Display displayId, bool on) override;
    V2_4::Error getSupportedContentTypes(
            Display displayId,
            std::vector<IComposerClient::ContentType>* outSupportedContentTypes) override;
    V2_4::Error setContentType(Display displayId,
                               IComposerClient::ContentType contentType) override;
    V2_4::Error setLayerGenericMetadata(Display display, Layer layer,
                                        const std::string& key, bool mandatory,
                                        const std::vector<uint8_t>& value) override;
    V2_4::Error getLayerGenericMetadataKeys(
            std::vector<IComposerClient::LayerGenericMetadataKey>* outKeys) override;
    Error getClientTargetProperty(
            Display display,
            V3_0::ClientTargetPropertyWithBrightness* outClientTargetProperty) override;

    // AIDL Composer (new in AOSP-13)
    Error setLayerBrightness(Display display, Layer layer, float brightness) override;
    Error setLayerBlockingRegion(Display display, Layer layer,
                                 const std::vector<IComposerClient::Rect>& blocking) override;
    Error setBootDisplayConfig(Display displayId, Config) override;
    Error clearBootDisplayConfig(Display displayId) override;
    Error getPreferredBootDisplayConfig(Display displayId, Config*) override;
    Error getDisplayDecorationSupport(
            Display display,
            std::optional<::aidl::android::hardware::graphics::common::DisplayDecorationSupport>*
                    support) override;
    Error setIdleTimerEnabled(Display displayId, std::chrono::milliseconds timeout) override;
    Error getPhysicalDisplayOrientation(Display displayId,
                                        AidlTransform* outDisplayOrientation) override;

private:
    // Each config is ID 1 (single config per display)
    static constexpr Config kConfigId = 1;

    // AOSP-13: HWC2::ComposerCallback (onComposerHal*) replaces HIDL IComposerCallback.
    // Raw pointer — SF owns the callback for the composer's lifetime.
    HWC2::ComposerCallback* mCallback = nullptr;
    std::unique_ptr<WaylandClient> mClient;

    // --- Physical display support ---
    // HWC display IDs are 1-based (index + 1). Each maps to a WaylandDisplay.
    struct DisplayEntry {
        Display hwcDisplayId;          // HWC display ID (index + 1)
        WaylandDisplay* waylandDisplay; // not owned — lives in WaylandGlobals
        // Size SF was given at the last CONNECTED for this id. SF loads a
        // display's modes only on hotplug, so this is what it still believes.
        int32_t announcedWidth = 0;
        int32_t announcedHeight = 0;
    };
    std::vector<DisplayEntry> mDisplays;  // populated in registerCallback()

    // Look up a WaylandDisplay by HWC display ID. Returns nullptr if invalid.
    WaylandDisplay* findDisplay(Display id) const;

    // fydroid-rt (AOSP-13): formerly the HWC2 setLayerInfo override (removed from
    // Hwc2::Composer in 12). Retained as a private helper; classifies a layer's
    // window type and creates the standalone Wayland container. TODO: re-wire the
    // trigger (window type now flows via setLayerGenericMetadata).
    Error setLayerInfoInternal(Display display, Layer layer, uint32_t type, uint32_t appId);

    // Runtime hotplug handler — called by WaylandGlobals when display is
    // connected or disconnected.
    void onWaylandHotplug(size_t displayIndex, bool connected);

    // Output-change handler — called by WaylandGlobals after each
    // zaura_output_manager_v2 done. Re-announces the display when its size
    // no longer matches what SF was last told (a resolution change on a live
    // output).
    void onWaylandDisplayChanged(size_t displayIndex);

    // Fire CONNECTED for `entry` and record the size SF will load.
    void announceDisplay(DisplayEntry& entry);

    // presentDisplay() helpers — one per virtual-display kind. These only
    // manage composer-side VD state (lookups, per-frame flags, buffer
    // hand-off); the per-object work (fence wait, DMA-BUF import, dialog
    // state, window positioning) lives on WaylandLayerContainer.
    struct VirtualDisplayData;
    Error presentNotificationVirtualDisplay(Display display, VirtualDisplayData& vd);
    Error presentTaskVirtualDisplay(Display display, VirtualDisplayData& vd);

    std::atomic<Layer> mNextLayerId{1};

    // --- Virtual display support (per-task and per-standalone GPU composition) ---
    struct VirtualDisplayData {
        Display hwcDisplayId;
        int32_t width, height;
        int32_t taskId = -1;                        // associated task (from layer metadata)
        WaylandLayerContainer* container = nullptr;  // cached pointer (looked up once)
        int32_t containerDisplayIndex = -1;          // which WaylandDisplay owns the container
        // Wayland output index this standalone VD's container is created on and
        // placed against — set at createVirtualDisplay from the pending hint
        // (setPendingStandaloneTargetOutput). -1 = primary (index 0). Used to
        // route a secondary-display window (external-HDMI Presentation) to the
        // right host wl_output instead of the primary.
        int32_t targetOutputIndex = -1;
        WaylandWindowCategory windowCategory = WaylandWindowCategory::APP;
        // Standalone layer ID (for standalone containers)
        uint64_t standaloneLayerId = 0;
        bool dialogDetectedThisFrame = false;
        // IsGame can arrive before TaskId (SurfaceFlinger walks the generic
        // metadata keys in unordered_map order), and the container only exists
        // once TaskId has been seen. Remember the value so it can be applied
        // when the container is resolved. -1 = not yet reported.
        int32_t pendingIsGame = -1;
        // Sticky: an IsSecure that arrived before the container existed, so it
        // can be replayed once the TaskId creates one.
        bool pendingIsSecure = false;
        // METADATA_ACCESSIBILITY_ID of a standalone VD's own layer, kept so it
        // can be replayed onto the container the WindowType key creates (the
        // keys arrive in unordered_map order). -1 = none.
        int32_t standaloneA11yId = -1;
        uint64_t standaloneA11yLayer = 0;
        sp<GraphicBuffer> clientTarget;              // composed buffer from setClientTarget
        int clientTargetFence = -1;                  // acquire fence for composed buffer
        std::unordered_set<Layer> layers;            // tracked layer IDs
        // Per-display slot cache for null-buffer resolution
        static constexpr size_t kSlotCount = 8;
        sp<GraphicBuffer> slotCache[kSlotCount];
        // Content bounds — union of all layer display frames.
        // Used for standalone window positioning.
        int32_t contentLeft = INT32_MAX, contentTop = INT32_MAX;
        int32_t contentRight = INT32_MIN, contentBottom = INT32_MIN;
        // On-screen geometry of a standalone window in display-0 space, pushed
        // by CompositionEngineArc (which knows it; the composed buffer always
        // sits at the VD origin so contentLeft/Top are 0). screenW>0 == valid.
        int32_t screenX = 0, screenY = 0, screenW = 0, screenH = 0;
        // Shared notification display flag — DEVICE composition, per-layer surfaces
        bool isNotificationDisplay = false;
        // External output-backed VD (MediaProjection / screen capture). Set when
        // SF's VirtualDisplaySurface calls setOutputBuffer — fydroid's own per-task
        // VDs composite through setClientTarget and never get an output buffer.
        // Such a VD AUTO_MIRRORs display 0, so its mirrored layers carry foreign
        // TaskId metadata; we must NOT latch that taskId, route the VD into a task
        // container, or (on teardown) destroy the mirrored task's real window.
        bool isOutputBacked = false;
        // Standalone (non-task) window VD — toast, overlay, IME. Set at
        // createVirtualDisplay from the geometry hint CompositionEngineArc arms
        // for exactly this allocation. Like isOutputBacked, this suppresses the
        // TaskId latch: the IME window arrives stamped with a TaskId (metadata
        // back-filled from its re-parented animation leash), and latching it
        // would route the keyboard's buffer into that task's app container.
        bool isStandalone = false;
        // Top of the IME's keys within its window, from METADATA_IME_CONTENT_TOP.
        // 0 = not reported yet, which means "the whole surface" — the historical
        // behaviour, and wrong, but never worse than it already was.
        int32_t imeContentTop = 0;
        // The IME's TOUCHABLE_INSETS_REGION when it reported one, appended to
        // the same metadata: flattened left,top,right,bottom in window px.
        std::vector<int32_t> imeTouchRects;
        // Per-layer data for DEVICE composition (notification VD only)
        struct NotifLayerData {
            std::string notifKey;                   // from generic metadata (stable)
            sp<GraphicBuffer> buffer;               // per-frame
            int acquireFence = -1;                  // per-frame
            WaylandLayerContainer* container = nullptr;  // cached
            int32_t frameLeft = 0, frameTop = 0;    // display frame on VD (for input)
            // Per-layer slot cache — SF uses same slot numbers across different
            // layers, so the per-VD slotCache would cause buffer cross-contamination.
            sp<GraphicBuffer> layerSlotCache[kSlotCount];
        };
        std::unordered_map<Layer, NotifLayerData> notifLayers;
        // Layers requesting composition type change (populated in validateDisplay)
        std::vector<Layer> pendingClientLayers;
        // Layers already reported as DEVICE (to avoid re-requesting)
        std::unordered_set<Layer> deviceCompositionLayers;
        // Composition type SF asked for, per layer, recorded from
        // setLayerCompositionType. getChangedCompositionTypes may only report a
        // type that actually differs from this: SF treats a reported type equal
        // to the one it requested as an invalid no-op change and logs
        // "Invalid device requested composition type change" (see
        // OutputLayer::detectDisallowedCompositionTypeChange).
        std::unordered_map<Layer, V3_0::Composition> requestedCompositionTypes;
    };
    std::unordered_map<Display, VirtualDisplayData> mVirtualDisplays;
    Display mNextVirtualDisplayId = 1000;  // start high to avoid collision

    // Armed by setPendingStandaloneTargetOutput() right before SF allocates a
    // secondary-display standalone VD; consumed (and reset to -1) by the next
    // createVirtualDisplay(). -1 = none pending (VD lands on the primary).
    int32_t mPendingStandaloneTargetOutput = -1;

    // Armed by setPendingStandaloneGeometry() alongside the hint above and
    // consumed by the same createVirtualDisplay(). w == 0 means none pending.
    int32_t mPendingStandaloneX = 0, mPendingStandaloneY = 0;
    int32_t mPendingStandaloneW = 0, mPendingStandaloneH = 0;

    bool isVirtualDisplay(Display id) const {
        return mVirtualDisplays.count(id) > 0;
    }

    // Single-lookup accessor. The per-frame entry points (setLayerBuffer,
    // setLayerDisplayFrame, setLayerGenericMetadata, setClientTarget,
    // validateDisplay, presentDisplay, ...) previously paired
    // isVirtualDisplay() with mVirtualDisplays[id], hashing the key twice —
    // three times in setLayerBuffer — for every layer of every frame. Prefer
    // this in anything on the frame path. Returns nullptr for physical
    // displays, and unlike operator[] it never default-constructs an entry.
    VirtualDisplayData* findVd(Display id) {
        auto it = mVirtualDisplays.find(id);
        return it == mVirtualDisplays.end() ? nullptr : &it->second;
    }

    // SF→CompositionEngineArc bridge (registered by SF at startup).
    // Locked because the callback can be replaced from the SF thread while
    // the wayland event thread invokes it; std::function is not atomic.
    std::mutex mTaskRenderMutex;
    TaskRenderTargetSizeCallback mTaskRenderTargetSizeCb;
    TaskResizeMaskHooks mTaskResizeMaskHooks;  // guarded by mTaskRenderMutex
};

} // namespace Hwc2
} // namespace android

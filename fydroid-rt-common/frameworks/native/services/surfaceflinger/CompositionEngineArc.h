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

#pragma once

#include <compositionengine/impl/CompositionEngine.h>
#include <compositionengine/impl/Display.h>

#include <renderengine/ExternalTexture.h>
#include <ui/DisplayId.h>
#include <ui/GraphicBuffer.h>
#include <ui/LayerStack.h>

#include <memory>
#include <mutex>
#include <unordered_map>
#include <unordered_set>

namespace android {
class WaylandTaskOutput;
}

namespace android::compositionengine {
struct CompositionRefreshArgs;
}

namespace android::compositionengine::impl {

// Display subclass for ARC Wayland composition.
//
// Physical display: forces device-only composition — has no composable
// layers after reform. Just triggers HWC present → flush.
//
// Virtual displays: forces HWC backing so Java-created VirtualDisplays
// (notification surfaces) are routed through WaylandHwc2Composer →
// zcr_notification_surface_v2.  Without this, AOSP defaults to GPU-only
// composition for Java VDs (debug.sf.enable_hwc_vds is unset).
class ArcDisplay : public Display {
public:
    void setConfiguration(
            const compositionengine::DisplayCreationArgs& args) override;
    // AOSP-13: chooseCompositionStrategy now returns bool and takes the
    // device-requested-changes out-param (was a void no-arg method).
    bool chooseCompositionStrategy(
            std::optional<android::HWComposer::DeviceRequestedChanges>*) override;

    // Physical ArcDisplay claims no layers — every ARC surface is rendered
    // by a per-task WaylandTaskOutput virtual display. The physical display
    // just ticks HWC present → flush, with no composable layers.
    // AOSP-13: belongsInOutput(optional<layerStackId>, internalOnly) /
    // belongsInOutput(LayerFE) became includesLayer(ui::LayerFilter) /
    // includesLayer(LayerFE).
    bool includesLayer(ui::LayerFilter) const override;
    bool includesLayer(const sp<compositionengine::LayerFE>& layerFE) const override;

private:
    bool mIsPhysical = false;
};

// CompositionEngine subclass for ARC per-task GPU composition.
//
// Discovers active tasks by scanning layer metadata (TaskId), creates HWC
// virtual displays for each task, and manages WaylandTaskOutput instances.
// Also creates virtual displays for standalone layers (IME, toast, overlay,
// notification) that don't have a TaskId.
//
// Task and standalone outputs are PREPENDED before ArcDisplay so they claim
// layers first; ArcDisplay gets remaining SKIP-type layers (dropped).
//
// No Wayland internal objects — all Wayland interaction happens inside
// WaylandHwc2Composer through the HWC virtual display path.
class CompositionEngineArc : public CompositionEngine {
public:
    CompositionEngineArc();
    ~CompositionEngineArc() override;

    std::shared_ptr<compositionengine::Display> createDisplay(
            const compositionengine::DisplayCreationArgs& args) override;

    void present(CompositionRefreshArgs& args) override;

    // Set the per-task SF render target buffer size. The composed buffer
    // is sized to (targetW, targetH) — typically Chrome's window pixel
    // size — so the GPU output always aligns with the host window
    // regardless of any shadow padding in the layer bbox. No-op if no
    // output exists for the given taskId. Threadsafe; called from the
    // wayland event thread.
    void setTaskRenderTargetSize(int32_t taskId, int32_t targetW, int32_t targetH);

    // Drag-resize mask, see WaylandHwc2Composer::TaskResizeMaskHooks.
    // Threadsafe; called from the wayland event thread. Serviced on the next
    // present().
    void requestTaskResizeMask(int32_t taskId);
    void awaitTaskResizeContent(int32_t taskId, int32_t w, int32_t h);

    // Global accessor — there's exactly one CompositionEngineArc per
    // surfaceflinger process. Returns nullptr if not yet constructed.
    static CompositionEngineArc* getInstance();

private:
    // Scan args.layers for TaskId metadata and sync task outputs.
    // Creates/destroys HWC virtual displays as tasks appear/disappear.
    // Also creates/destroys standalone outputs for non-task layers.
    // Returns true if any outputs were created or destroyed.
    bool syncTaskOutputs(const CompositionRefreshArgs& args);

    struct TaskOutputEntry;
    // Before the outputs compose: build requested masks from the frame on
    // screen, and report tasks whose resized app window buffer is latched.
    void serviceResizeMasks(const CompositionRefreshArgs& args, bool statePrepared);
    bool buildResizeMask(int32_t taskId, TaskOutputEntry& entry);

    struct TaskOutputEntry {
        std::shared_ptr<WaylandTaskOutput> output;
        DisplayId displayId;
        int32_t bufWidth = 0, bufHeight = 0;
        int32_t displayLeft = 0, displayTop = 0;  // display-space position
        int32_t missingFrames = 0;  // frames with no layers — grace period for reparenting
        // When the task started waiting for a window that has not drawn yet
        // (see syncTaskOutputs' teardown); 0 when not waiting.
        nsecs_t awaitingFirstBufferSinceNs = 0;
        // High-water mark of the task's content footprint (display-space px).
        // Used to grow the Chrome window when an activity lays out larger than
        // its host window opened at (stale launch/animation bounds).
        int32_t maxW = 0, maxH = 0;
        // Debounce for the grow above: a larger footprint must persist for
        // several consecutive frames before it grows the window. This rejects
        // transient peaks from page-slide / window animations (e.g. Settings
        // sliding a wider page in from off-screen), which otherwise latch the
        // window permanently wider via the grow-only high-water mark.
        int32_t pendW = 0, pendH = 0;     // candidate footprint awaiting stability
        int32_t pendFrames = 0;           // consecutive frames the candidate held
        // Monotonic time (ns) the task output was created. The content-driven
        // grow above is only meant to repair the stale launch-size race, so it
        // is restricted to a short settling window after creation; afterwards a
        // wider in-app screen must not ratchet the host window larger.
        int64_t createdNs = 0;
        // Dialog tasks only: the card footprint last pushed to the frameless
        // modal window, plus its own debounce. A dialog's window geometry is
        // owned entirely by SF (no Chrome geometry echo), so the modal must
        // follow the card exactly — grow AND shrink — or the card overflows a
        // stale, smaller window. Debounced because the card sweeps through a
        // dozen sizes while the dialog animates open.
        int32_t dlgAppliedW = 0, dlgAppliedH = 0;
        int32_t dlgPendW = 0, dlgPendH = 0;
        int32_t dlgPendFrames = 0;
        // Drag-resize mask target. Handed to Chrome as a wl_buffer, so it is
        // reused (grow-only) rather than re-imported on every drag.
        sp<GraphicBuffer> maskBuffer;
        std::shared_ptr<renderengine::ExternalTexture> maskTexture;
    };
    std::unordered_map<int32_t, TaskOutputEntry> mTaskOutputs;

    struct StandaloneOutputEntry {
        std::shared_ptr<WaylandTaskOutput> output;
        DisplayId displayId;
        int32_t bufWidth = 0, bufHeight = 0;
        int32_t displayLeft = 0, displayTop = 0;
        // ARC layer stack and wayland output index this standalone window lives
        // on. 0/0 = primary; a secondary physical display (external HDMI
        // Presentation) has a non-zero stack routed to output index >= 1.
        uint32_t layerStack = 0;
        int32_t outputIndex = 0;
    };
    // Keyed by LayerFE pointer address (stable while layer exists)
    std::unordered_map<uintptr_t, StandaloneOutputEntry> mStandaloneOutputs;

    // AOSP-13: the caller now mints the HalVirtualDisplayId for
    // allocateVirtualDisplay (it no longer returns one). Sequence base id.
    uint32_t mNextVdBaseId = 1000;

    std::mutex mResizeMaskMutex;
    std::unordered_set<int32_t> mMaskRequests;                  // taskIds
    std::unordered_map<int32_t, std::pair<int32_t, int32_t>> mMaskAwaits;  // taskId -> task px
};

} // namespace android::compositionengine::impl

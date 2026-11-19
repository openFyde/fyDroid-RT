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

#include <compositionengine/impl/Output.h>

#include <ui/DisplayId.h>

#include <cstdint>
#include <memory>

namespace android {

class HWComposer;
class TaskRenderSurface;

namespace compositionengine {
class CompositionEngine;
class LayerFE;
}

// Per-task (or per-standalone-layer) output for GPU composition.
//
// Two modes:
// 1. Task mode: filters layers by TaskId metadata. Created with taskId.
// 2. Standalone mode: filters by LayerFE pointer. Created with a LayerFE sp.
//
// GPU-composites matching layers via RenderEngine into a buffer (managed by
// TaskRenderSurface), then routes the composed buffer through HWC's
// setClientTarget/presentDisplay path to reach the Wayland compositor.
//
// All Wayland interaction happens inside WaylandHwc2Composer — this class
// only interacts with HWC through standard AOSP interfaces.
class WaylandTaskOutput : public compositionengine::impl::Output {
public:
    // Task mode: match layers by TaskId metadata
    explicit WaylandTaskOutput(int32_t taskId);

    // Standalone mode: match a single layer by LayerFE pointer
    explicit WaylandTaskOutput(const sp<compositionengine::LayerFE>& targetLayer);

    ~WaylandTaskOutput() override;

    int32_t taskId() const { return mTaskId; }

    // The render surface configure() installs; always a TaskRenderSurface.
    TaskRenderSurface* taskRenderSurface() const;
    bool isStandalone() const { return mStandalone; }

    // HWC layer id of this output's (single, for standalone) composited layer.
    // Equals the composer's standaloneLayerId, so CompositionEngineArc can key
    // the on-screen geometry it pushes to WaylandHwc2Composer. 0 until the
    // output first composites.
    uint64_t hwcLayerId() const { return mHwcLayerId; }

    // Set up the render surface and color profile.
    // Must be called after createOutputTemplated().
    //
    // layerStack selects which ARC layer stack this output claims layers from.
    // 0 = the primary internal stack (default; every task / primary standalone
    // window). A secondary physical display (external HDMI) has its own layer
    // stack (e.g. 2), so a standalone window living there — an
    // android.app.Presentation — must filter on that stack or the base
    // Output machinery (LayerFilter::includes) never routes its layer here.
    void configure(const compositionengine::CompositionEngine& engine,
                   HWComposer& hwComposer, DisplayId displayId,
                   int32_t width, int32_t height,
                   int32_t displayLeft, int32_t displayTop,
                   uint32_t layerStack = 0);

    // Update bounds when the task resizes or moves.
    void updateBounds(int32_t width, int32_t height,
                      int32_t displayLeft, int32_t displayTop);

    // The footprint actually in effect, after updateBounds/setRenderTargetSize
    // have clamped it up to the render target. Callers that need to keep other
    // records in step (the HWC virtual-display size) must use these rather
    // than the raw sampled bounds they passed in, or they re-introduce the
    // disagreement the clamp exists to remove.
    int32_t srcWidth() const { return mSrcWidth; }
    int32_t srcHeight() const { return mSrcHeight; }

    // Set the target buffer pixel size. SF's RenderEngine projects the
    // source layer bbox into a buffer of exactly (targetW, targetH), so
    // the composed buffer always matches Chrome's window pixel size
    // regardless of any shadow padding that bumps the bbox above the
    // logical task bounds. 0 = unset (1:1 fallback).
    void setRenderTargetSize(int32_t targetW, int32_t targetH);

    // Mark this task as a modal dialog. When set, belongsInOutput() drops the
    // task's no-buffer backing layers (the WM SOLID_COLOR task background and
    // the DIM_BEHIND scrim) so the dialog window only composites the real
    // content and stays transparent around the card.
    void setIsDialogTask(bool isDialog);

    // compositionengine::impl::Output overrides
    std::optional<DisplayId> getDisplayId() const override;
    // AOSP-13: belongsInOutput(LayerFE) -> includesLayer(LayerFE).
    bool includesLayer(const sp<compositionengine::LayerFE>& layerFE) const override;

    // Shadows impl::Output::createOutputLayer() so the template's
    // ensureOutputLayer() creates HWC layers on our virtual display.
    std::unique_ptr<compositionengine::OutputLayer> createOutputLayer(
            const sp<compositionengine::LayerFE>& layerFE) const;

protected:
    // AOSP-13: chooseCompositionStrategy returns bool + takes device-requested-changes.
    bool chooseCompositionStrategy(
            std::optional<android::HWComposer::DeviceRequestedChanges>*) override;
    bool getSkipColorTransform() const override;
    compositionengine::Output::FrameFences presentAndGetFrameFences() override;

private:
    // Rebuild output state from the cached source dimensions and the
    // current render target size. Shared by configure(), updateBounds()
    // and setRenderTargetSize().
    void applyState();

    int32_t mTaskId;
    bool mStandalone = false;
    wp<compositionengine::LayerFE> mTargetLayerFE;  // for standalone mode
    DisplayId mDisplayId;
    // ARC layer stack this output claims from (0 = primary). A standalone
    // output for a secondary display (external HDMI Presentation) is set to
    // that display's stack so includesLayer() and the output layerFilter match.
    uint32_t mLayerStack = 0;
    // Cached in createOutputLayer() (which is const) — the HWC layer id of the
    // composited layer, used to key standalone on-screen geometry.
    mutable uint64_t mHwcLayerId = 0;

    // Source bounds (Android task pixel space) — what layers project from.
    int32_t mSrcWidth = 0, mSrcHeight = 0;
    int32_t mSrcLeft = 0, mSrcTop = 0;

    // Target buffer pixel size. When >0, applyState sizes the
    // destinationClip / buffer to exactly (target). sourceClip stays at the
    // source bbox so SF's RenderEngine projects layer pixels into the
    // target via its ortho projection. 0 = unset; buffer mirrors source 1:1.
    int32_t mRenderTargetW = 0;
    int32_t mRenderTargetH = 0;

    // True for modal dialog tasks — see setIsDialogTask().
    bool mIsDialogTask = false;
};

} // namespace android

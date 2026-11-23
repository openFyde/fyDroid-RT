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

#include <binder/Binder.h>
#include <mutex>

namespace android {

class WaylandGlobals;

// Outbound proxy for org.chromium.arc.wayland.IWaylandShellHandler.
//
// ArcSystemUI / arc-services register their IWaylandShellHandler binder
// with us via IWaylandService.registerWaylandShellHandler. We store the
// IBinder here and use it to push display layout / task lifecycle events
// back into Android, matching what the proprietary wayland-service does.
//
// All methods on the underlying interface are oneway. We transact with
// FLAG_ONEWAY and a null reply parcel.
class WaylandShellHandlerProxy {
public:
    static WaylandShellHandlerProxy* instance();

    // Store the IWaylandShellHandler binder registered by ArcSystemUI.
    // Passing nullptr clears the registration.
    void setHandler(const sp<IBinder>& handler);

    // Push the current display layout (built from `globals`) to the
    // registered handler via IWaylandShellHandler.setDisplayLayout(layout).
    // Drops the call silently if no handler is registered or if globals
    // have not yet been populated with displays.
    void pushDisplayLayout(WaylandGlobals* globals);

    // Call the fydroid-rt extension TX_SH_RESIZE_TASK_WITH_AR_STEP on
    // the registered Java handler. Java decides whether the task's
    // activity is resizable: if so it just resizes; if not it snaps to
    // the closest supported aspect ratio (ArcAspectRatioStepper) and
    // ATMS relaunches the activity. Returns true if the request was
    // dispatched and Java reported success, false otherwise (handler
    // missing, transact failure, Java rejected).
    // outApplied{X,Y,W,H} (optional) receive the bounds Java actually
    // applied — equal to the requested rect for resizable activities,
    // snapped to the next aspect ratio for unresizable ones. C++ callers
    // use these to drive the matching setBoundsInOutput on the Wayland
    // window so Chrome's frame stays in sync with Android's input region.
    bool resizeTaskWithArStep(int32_t taskId, int32_t x, int32_t y,
                              int32_t w, int32_t h, int32_t displayId,
                              int32_t* outAppliedX = nullptr,
                              int32_t* outAppliedY = nullptr,
                              int32_t* outAppliedW = nullptr,
                              int32_t* outAppliedH = nullptr);

    // Tell Java how much of a task's window is covered, so it can decide
    // whether the app should drop into PIP. `fraction` is 0..1; `reason` comes
    // straight from zaura_surface.occlusion_changed (1 = a user action changed
    // which window is active — the only one auto-PIP acts on).
    //
    // Oneway and best-effort: occlusion is a hint, and dropping one costs
    // nothing because the next activation change sends another.
    void taskOcclusionChanged(int32_t taskId, float fraction, int32_t reason);

private:
    WaylandShellHandlerProxy() = default;

    // IWaylandShellHandler is a oneway interface with sequential transaction
    // codes 1..12. setDisplayLayout is method #3.
    static constexpr uint32_t TRANSACTION_setDisplayLayout = 3;
    // ...and taskOcclusionChanged is method #10.
    static constexpr uint32_t TRANSACTION_taskOcclusionChanged = 10;
    // fydroid-rt extension, kept well above the proprietary range so
    // future additions to IWaylandShellHandler don't collide.
    static constexpr uint32_t TRANSACTION_resizeTaskWithArStep = 100;

    std::mutex mMutex;
    sp<IBinder> mHandler;
};

} // namespace android

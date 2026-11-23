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

#include <cstdint>
#include <binder/IBinder.h>
#include <utils/StrongPointer.h>

namespace io::fydeos::arc::wm {
class IArcTaskController;
}

namespace android {

// Manages Android tasks. Task operations (resize/remove/focus/move/windowing-mode)
// go through arc-services' IArcTaskController (the "arc_task_controller" binder),
// whose C++ stub is generated from the AIDL so transaction codes are always correct
// — no hardcoded IActivityTaskManager codes (those shifted across AOSP versions and
// called the wrong methods).
class WaylandTaskResizer {
public:
    // Resize a task. Coordinates are in the display's pixel/DIP space.
    // Returns true on success.
    bool resizeTask(int32_t taskId, int32_t left, int32_t top,
                    int32_t right, int32_t bottom);

    // Set a task's bounds directly, bypassing the windowing-mode gate that
    // makes resizeTask() refuse a pinned (PIP) task. Use ONLY where resizeTask
    // is known to refuse; it skips the reparenting and resize-mode handling
    // resizeTask does. See IArcTaskController.aidl.
    bool setTaskBoundsUnchecked(int32_t taskId, int32_t left, int32_t top,
                                int32_t right, int32_t bottom);

    // Remove (close) a task. Returns true on success.
    bool removeTask(int32_t taskId);

    // Move a task's stack to a different display. Asynchronous — see
    // setFocusedTask for why. Always returns true.
    bool moveStackToDisplay(int32_t taskId, int32_t displayId);

    // Tell Android whether any ARC window still holds Chrome OS activation.
    // false makes ordinary app/game activities unfocusable and hides the IME;
    // true restores them. Focus only — see IArcTaskController.aidl.
    //
    // Asynchronous, on the same worker and for the same deadlock reason as
    // setFocusedTask — and through the same queue, so an activation change
    // always lands before a focus request made after it.
    void setArcActivated(bool activated);

    // Bring a task to the front of the stack and give it input focus.
    //
    // Asynchronous: the request is handed to WaylandArcTaskWorker and applied
    // on its thread. The binder call's service side takes the WM global lock,
    // which system_server holds across relayoutWindow() while that blocks in a
    // callback into SurfaceFlinger — so making this call from a SurfaceFlinger
    // thread deadlocks both processes until the watchdog kills system_server
    // (docs/surfaceflinger-arctaskcontroller-binder-deadlock.md).
    //
    // Always returns true; the request is queued, not completed. Callers that
    // must not proceed until focus has actually moved — i.e. anything about to
    // inject an input event into the newly focused task — use
    // setFocusedTaskAwait() instead.
    bool setFocusedTask(int32_t taskId);

    // setFocusedTask() plus a bounded wait for it to be applied. Returns
    // whether focus was confirmed applied before returning; false means either
    // the wait timed out or the calling thread is not allowed to block (see
    // allowBlockingAwaitOnThisThread). Either way the caller should carry on:
    // the cost is the pre-existing "first tap on the other half of a split goes
    // to the previously focused task" bug for one event, which is a far better
    // failure than stalling a compositor thread.
    bool setFocusedTaskAwait(int32_t taskId);

    // Opt the calling thread in to blocking in setFocusedTaskAwait(). Only the
    // Wayland event threads do this. Wayland input callbacks also run on
    // SurfaceFlinger's main thread — it dispatches pending events during
    // composition — and that thread must never block on system_server, which is
    // exactly the deadlock this whole split exists to avoid.
    static void allowBlockingAwaitOnThisThread();

    // Performs the focus binder call inline. Only WaylandArcTaskWorker calls
    // this — everything else must go through setFocusedTask{,Await}().
    static bool setFocusedTaskBlocking(int32_t taskId);

    // Upper bound on how long setFocusedTaskAwait() blocks its caller. Long
    // enough to cover a normal round trip (sub-millisecond) plus a system_server
    // that is briefly busy, short enough that no user-visible stall results.
    static constexpr int64_t kFocusAwaitTimeoutMs = 250;

    // Change a task's windowing mode (e.g. fullscreen → freeform).
    // Returns true on success.
    bool setTaskWindowingMode(int32_t taskId, int32_t windowingMode, bool toTop);

    // Whether the task's activity can actually be resized. Drives the resize
    // limits advertised to Chrome, which is what decides whether the window may
    // be snapped into split view. Defaults to true (today's behaviour) when the
    // controller is unreachable, so a missing service cannot make every window
    // unsnappable.
    bool isTaskResizable(int32_t taskId);

    // The activity's declared minimum size in px, or -1 each when it declares
    // none. Returns true if either value is valid.
    bool getTaskMinSize(int32_t taskId, int32_t* outW, int32_t* outH);

    // Windowing mode constants (from WindowConfiguration.java).
    // AOSP-13 removed SPLIT_SCREEN_PRIMARY(3)/SECONDARY(4) — snapped tablet
    // tasks run in MULTI_WINDOW (visual split is owned by Chrome/exo).
    static constexpr int32_t WINDOWING_MODE_FULLSCREEN = 1;
    static constexpr int32_t WINDOWING_MODE_FREEFORM = 5;
    static constexpr int32_t WINDOWING_MODE_MULTI_WINDOW = 6;

private:
    // arc-services' "arc_task_controller" binder (looked up fresh each call —
    // checkService() is non-blocking and the handle goes stale across stop/start ui).
    static sp<::io::fydeos::arc::wm::IArcTaskController> getController();
};

} // namespace android

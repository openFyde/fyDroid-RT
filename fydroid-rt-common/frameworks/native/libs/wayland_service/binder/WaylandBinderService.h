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

#include <binder/Binder.h>
#include <cstdint>
#include <mutex>

namespace android {

class WaylandGlobals;

// Binder service registered as "Wayland" implementing
// org.chromium.arc.wayland.IWaylandService.
// Provides display layout info to ArcWindowController/ArcInsetsHandler.
// Queries live WaylandDisplay state on each request — no cached data.
// Receives input pipe fd from InputReaderArc via transaction 29.
// Other methods return success with default values.
class WaylandBinderService : public BBinder {
public:
    static void publish();

    // Set the globals to query all displays from. Called once after finalize().
    static void setGlobals(WaylandGlobals* globals);

    status_t onTransact(uint32_t code, const Parcel& data,
                        Parcel* reply, uint32_t flags) override;

private:
    // Transaction codes (from AIDL-generated IWaylandService)
    static constexpr uint32_t TRANSACTION_getDisplayLayout = 6;
    // Android turned pointer capture on or off for the focused window, pushed
    // by InputReaderArc when InputDispatcher asks the reader to change mode.
    // One-way: it must never block the input reader thread.
    static constexpr uint32_t TRANSACTION_requestPointerCapture = 24;
    static constexpr uint32_t TRANSACTION_registerWaylandShellHandler = 25;
    static constexpr uint32_t TRANSACTION_notifyInputBridgePipeOpening = 29;
    static constexpr uint32_t TRANSACTION_setClipboardText = 30;
    static constexpr uint32_t TRANSACTION_registerClipboardCallback = 31;
    // Data device transactions (used by ArcDataDeviceService)
    static constexpr uint32_t TRANSACTION_startDrag = 32;
    static constexpr uint32_t TRANSACTION_notifyDropResult = 33;
    static constexpr uint32_t TRANSACTION_setClipboardMimes = 34;
    static constexpr uint32_t TRANSACTION_receiveDragData = 36;
    static constexpr uint32_t TRANSACTION_receiveClipboardData = 37;
    static constexpr uint32_t TRANSACTION_releaseDataOffer = 38;
    static constexpr uint32_t TRANSACTION_dragRecipientEntered = 39;
    static constexpr uint32_t TRANSACTION_dragRecipientExited = 40;
    static constexpr uint32_t TRANSACTION_notifyTaskCreated = 41;
    static constexpr uint32_t TRANSACTION_getFocusedWindowOffset = 42;
    // Notification surface support
    static constexpr uint32_t TRANSACTION_setNotificationDisplayId = 50;
    // Window caption colours, pushed by ArcSystemUiService when Chrome's
    // colour scheme or dynamic-colour seed changes. One-way.
    static constexpr uint32_t TRANSACTION_setFrameColors = 51;
    // Per-task caption colour from an app's TaskDescription, pushed by
    // ArcWindowManagerService. Overrides the header above. One-way.
    static constexpr uint32_t TRANSACTION_setTaskFrameColor = 52;
    // A task entered or left WINDOWING_MODE_PINNED (Android PIP), pushed by
    // ArcWindowManagerService's task-stack listener. The windowing mode lives
    // entirely on the Android side, so this is the only way the composer learns
    // a task wants to be a ChromeOS PIP window. One-way.
    static constexpr uint32_t TRANSACTION_setTaskPipState = 53;
    // The Android accessibility window id a surface currently shows, pushed by
    // ArcAccessibilityHelperService (the only process that sees the
    // accessibility window list). Becomes zaura_surface.set_accessibility_id on
    // that surface, which is the only way an accessibility window id reaches
    // Chrome. Arguments are (target, taskId, key, windowId) — see
    // WaylandGlobals::AccessibilityTarget. One-way.
    static constexpr uint32_t TRANSACTION_setAccessibilityWindowId = 54;

    // system_server's verdict on a key: (long eventTimeNanos, boolean handled).
    // Sent as not handled when the focused app did not use the key, so exo
    // gives it to ash's accelerators; keys the app used are never acked and
    // exo drops them as handled. Not the Google ackKeyboardKey (13), which
    // takes the wayland serial system_server never sees. One-way.
    static constexpr uint32_t TRANSACTION_ackKeyboardKeyByEventTime = 55;

    static std::mutex sGlobalsMutex;
    static WaylandGlobals* sGlobals;
};

} // namespace android

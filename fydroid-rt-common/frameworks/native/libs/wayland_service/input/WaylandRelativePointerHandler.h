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

struct wl_pointer;
struct wl_surface;
struct zwp_relative_pointer_manager_v1;
struct zwp_relative_pointer_v1;
struct zwp_pointer_constraints_v1;
struct zwp_locked_pointer_v1;

namespace android {

class InputBridge;

// Handles zwp_relative_pointer_v1 events (relative mouse deltas) and
// zwp_pointer_constraints_v1 (pointer lock). Sends BridgeInputEvent
// with BRIDGE_DEVICE_MOUSE_RELATIVE for raw delta motion.
class WaylandRelativePointerHandler {
public:
    WaylandRelativePointerHandler();
    ~WaylandRelativePointerHandler();

    WaylandRelativePointerHandler(const WaylandRelativePointerHandler&) = delete;
    WaylandRelativePointerHandler& operator=(const WaylandRelativePointerHandler&) = delete;

    // Device scale factor (DIP -> Android pixels). exo computes the relative
    // delta from ROOT coordinates, which are DIP, while wl_pointer.motion
    // arrives already in physical pixels -- so unlike the absolute path this
    // one needs the DSF applied or captured motion runs at 1/DSF speed.
    void setScaleFactor(float scale) { mScaleFactor = (scale > 0.0f) ? scale : 1.0f; }

    // Initialize from manager + existing wl_pointer
    bool initialize(zwp_relative_pointer_manager_v1* manager,
                    zwp_pointer_constraints_v1* constraints,
                    wl_pointer* pointer, InputBridge* bridge);

    // Relative motion event handler
    void onRelativeMotion(uint32_t utimeHi, uint32_t utimeLo,
                          int32_t dx, int32_t dy,
                          int32_t dxUnaccel, int32_t dyUnaccel);

    // Pointer lock management
    void lockPointer(wl_surface* surface);
    void unlockPointer();
    // Whether exo has confirmed the lock (zwp_locked_pointer_v1::locked).
    bool isLocked() const { return mLocked; }
    // Whether a lock request is outstanding. Distinct from isLocked(): a
    // LIFETIME_PERSISTENT request that exo refuses (the window was not active
    // yet) stays alive and is activated when the window next becomes active,
    // so the object outliving a refused attempt is the point of asking once.
    bool hasLockRequest() const { return mLockedPointer != nullptr; }

    // Lock state event handlers
    void onLocked();
    void onUnlocked();

private:
    int64_t now() const;

    zwp_relative_pointer_v1* mRelativePointer = nullptr;
    zwp_pointer_constraints_v1* mConstraints = nullptr;
    zwp_locked_pointer_v1* mLockedPointer = nullptr;
    wl_pointer* mWlPointer = nullptr;  // not owned
    InputBridge* mBridge = nullptr;

    bool mLocked = false;
    float mScaleFactor = 1.0f;
};

} // namespace android

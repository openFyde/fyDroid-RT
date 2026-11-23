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

// WaylandRelativePointerHandler: zwp_relative_pointer_v1 → BridgeInputEvent.
// Sends raw unaccelerated mouse deltas for FPS/3D game mouse capture.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandRelativePointer"

#include "WaylandRelativePointerHandler.h"
#include "InputBridge.h"
#include "BridgeInputEvent.h"

#include <relative-pointer-unstable-v1-client-protocol.h>
#include <pointer-constraints-unstable-v1-client-protocol.h>
#include <wayland-client.h>

#include <android/input.h>
#include <time.h>
#include <cstring>

#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

// --- Static relative pointer listener ---

static void relativeMotion(void* data, struct zwp_relative_pointer_v1*,
                            uint32_t utimeHi, uint32_t utimeLo,
                            wl_fixed_t dx, wl_fixed_t dy,
                            wl_fixed_t dxUnaccel, wl_fixed_t dyUnaccel) {
    static_cast<WaylandRelativePointerHandler*>(data)->onRelativeMotion(
            utimeHi, utimeLo, dx, dy, dxUnaccel, dyUnaccel);
}

static const struct zwp_relative_pointer_v1_listener sRelativePointerListener = {
    .relative_motion = relativeMotion,
};

// --- Static locked pointer listener ---

static void lockedPointerLocked(void* data, struct zwp_locked_pointer_v1*) {
    static_cast<WaylandRelativePointerHandler*>(data)->onLocked();
}

static void lockedPointerUnlocked(void* data, struct zwp_locked_pointer_v1*) {
    static_cast<WaylandRelativePointerHandler*>(data)->onUnlocked();
}

static const struct zwp_locked_pointer_v1_listener sLockedPointerListener = {
    .locked = lockedPointerLocked,
    .unlocked = lockedPointerUnlocked,
};

// --- WaylandRelativePointerHandler implementation ---

WaylandRelativePointerHandler::WaylandRelativePointerHandler() = default;

WaylandRelativePointerHandler::~WaylandRelativePointerHandler() {
    if (mLockedPointer) {
        zwp_locked_pointer_v1_destroy(mLockedPointer);
    }
    if (mRelativePointer) {
        zwp_relative_pointer_v1_destroy(mRelativePointer);
    }
}

int64_t WaylandRelativePointerHandler::now() const {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return static_cast<int64_t>(ts.tv_sec) * 1000000000LL + ts.tv_nsec;
}

bool WaylandRelativePointerHandler::initialize(
        zwp_relative_pointer_manager_v1* manager,
        zwp_pointer_constraints_v1* constraints,
        wl_pointer* pointer, InputBridge* bridge) {
    if (!manager || !pointer) {
        ALOGW("Relative pointer manager or wl_pointer not available");
        return false;
    }

    mConstraints = constraints;
    mWlPointer = pointer;
    mBridge = bridge;

    mRelativePointer = zwp_relative_pointer_manager_v1_get_relative_pointer(
            manager, pointer);
    if (!mRelativePointer) {
        ALOGE("Failed to create zwp_relative_pointer_v1");
        return false;
    }
    zwp_relative_pointer_v1_add_listener(mRelativePointer,
                                          &sRelativePointerListener, this);

    ALOGI("Relative pointer handler initialized (constraints=%p, scale=%.2f)",
          constraints, mScaleFactor);
    return true;
}

void WaylandRelativePointerHandler::onRelativeMotion(
        uint32_t /*utimeHi*/, uint32_t /*utimeLo*/,
        int32_t /*dx*/, int32_t /*dy*/,
        int32_t dxUnaccel, int32_t dyUnaccel) {
    if (!mBridge || !mLocked) return;

    float relX = static_cast<float>(wl_fixed_to_double(dxUnaccel)) * mScaleFactor;
    float relY = static_cast<float>(wl_fixed_to_double(dyUnaccel)) * mScaleFactor;

    // Skip zero-delta events
    if (relX == 0.0f && relY == 0.0f) return;

    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));

    event.type = BRIDGE_EVENT_MOTION;
    event.timestampNanos = now();
    event.deviceType = BRIDGE_DEVICE_MOUSE_RELATIVE;
    event.displayId = 0;
    event.action = AMOTION_EVENT_ACTION_MOVE;
    event.pointerCount = 1;
    event.pointers[0].id = 0;
    event.relativeX = relX;
    event.relativeY = relY;

    ALOGVT("Relative motion: dx=%.2f dy=%.2f", relX, relY);
    mBridge->writeEvent(event);
}

void WaylandRelativePointerHandler::lockPointer(wl_surface* surface) {
    if (!mConstraints || !mWlPointer || !surface) {
        ALOGW("Cannot lock pointer: missing constraints/pointer/surface");
        return;
    }
    if (mLockedPointer) {
        ALOGW("Pointer already locked");
        return;
    }

    mLockedPointer = zwp_pointer_constraints_v1_lock_pointer(
            mConstraints, surface, mWlPointer, nullptr,
            ZWP_POINTER_CONSTRAINTS_V1_LIFETIME_PERSISTENT);
    if (mLockedPointer) {
        zwp_locked_pointer_v1_add_listener(mLockedPointer,
                                            &sLockedPointerListener, this);
        ALOGI("Pointer lock requested on surface %p", surface);
    } else {
        ALOGE("Failed to create locked pointer");
    }
}

void WaylandRelativePointerHandler::unlockPointer() {
    if (mLockedPointer) {
        zwp_locked_pointer_v1_destroy(mLockedPointer);
        mLockedPointer = nullptr;
        mLocked = false;
        ALOGI("Pointer unlocked");
    }
}

void WaylandRelativePointerHandler::onLocked() {
    mLocked = true;
    ALOGI("Pointer lock activated");
}

void WaylandRelativePointerHandler::onUnlocked() {
    mLocked = false;
    ALOGI("Pointer lock deactivated");
}

} // namespace android

#pragma clang diagnostic pop

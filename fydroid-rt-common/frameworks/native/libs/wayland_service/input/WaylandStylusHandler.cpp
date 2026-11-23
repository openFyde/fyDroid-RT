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

// WaylandStylusHandler: zcr_stylus_v2 protocol events → per-touch/pointer
// stylus data (tool type, force, tilt). Queried by touch/pointer handlers.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandStylusHandler"

#include "WaylandStylusHandler.h"

#include <stylus-unstable-v2-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

// --- Static touch stylus listeners ---

static void touchStylusTool(void* data, struct zcr_touch_stylus_v2*,
                             uint32_t id, uint32_t type) {
    static_cast<WaylandStylusHandler*>(data)->onTouchTool(id, type);
}

static void touchStylusForce(void* data, struct zcr_touch_stylus_v2*,
                              uint32_t time, uint32_t id, wl_fixed_t force) {
    static_cast<WaylandStylusHandler*>(data)->onTouchForce(time, id, force);
}

static void touchStylusTilt(void* data, struct zcr_touch_stylus_v2*,
                             uint32_t time, uint32_t id,
                             wl_fixed_t tiltX, wl_fixed_t tiltY) {
    static_cast<WaylandStylusHandler*>(data)->onTouchTilt(time, id, tiltX, tiltY);
}

static const struct zcr_touch_stylus_v2_listener sTouchStylusListener = {
    .tool = touchStylusTool,
    .force = touchStylusForce,
    .tilt = touchStylusTilt,
};

// --- Static pointer stylus listeners ---

static void pointerStylusTool(void* data, struct zcr_pointer_stylus_v2*,
                               uint32_t type) {
    static_cast<WaylandStylusHandler*>(data)->onPointerTool(type);
}

static void pointerStylusForce(void* data, struct zcr_pointer_stylus_v2*,
                                uint32_t time, wl_fixed_t force) {
    static_cast<WaylandStylusHandler*>(data)->onPointerForce(time, force);
}

static void pointerStylusTilt(void* data, struct zcr_pointer_stylus_v2*,
                               uint32_t time, wl_fixed_t tiltX, wl_fixed_t tiltY) {
    static_cast<WaylandStylusHandler*>(data)->onPointerTilt(time, tiltX, tiltY);
}

static const struct zcr_pointer_stylus_v2_listener sPointerStylusListener = {
    .tool = pointerStylusTool,
    .force = pointerStylusForce,
    .tilt = pointerStylusTilt,
};

// --- WaylandStylusHandler implementation ---

WaylandStylusHandler::WaylandStylusHandler() = default;

WaylandStylusHandler::~WaylandStylusHandler() {
    if (mTouchStylus) {
        zcr_touch_stylus_v2_destroy(mTouchStylus);
    }
    if (mPointerStylus) {
        zcr_pointer_stylus_v2_destroy(mPointerStylus);
    }
}

bool WaylandStylusHandler::initialize(zcr_stylus_v2* stylus,
                                       wl_touch* touch,
                                       wl_pointer* pointer) {
    if (!stylus) {
        ALOGW("zcr_stylus_v2 not available");
        return false;
    }

    if (touch) {
        mTouchStylus = zcr_stylus_v2_get_touch_stylus(stylus, touch);
        if (mTouchStylus) {
            zcr_touch_stylus_v2_add_listener(mTouchStylus,
                                              &sTouchStylusListener, this);
            ALOGI("Touch stylus handler initialized");
        }
    }

    if (pointer) {
        mPointerStylus = zcr_stylus_v2_get_pointer_stylus(stylus, pointer);
        if (mPointerStylus) {
            zcr_pointer_stylus_v2_add_listener(mPointerStylus,
                                                &sPointerStylusListener, this);
            ALOGI("Pointer stylus handler initialized");
        }
    }

    return mTouchStylus || mPointerStylus;
}

// --- Touch stylus events ---

void WaylandStylusHandler::onTouchTool(uint32_t id, uint32_t type) {
    ALOGVT("Touch stylus tool: id=%u type=%u", id, type);
    mTouchData[id].toolType = static_cast<int32_t>(type);
}

void WaylandStylusHandler::onTouchForce(uint32_t /*time*/, uint32_t id,
                                          int32_t force) {
    float f = wl_fixed_to_double(force);
    ALOGVT("Touch stylus force: id=%u force=%.3f", id, f);
    mTouchData[id].force = f;
}

void WaylandStylusHandler::onTouchTilt(uint32_t /*time*/, uint32_t id,
                                         int32_t tiltX, int32_t tiltY) {
    float tx = static_cast<float>(wl_fixed_to_double(tiltX));
    float ty = static_cast<float>(wl_fixed_to_double(tiltY));
    ALOGVT("Touch stylus tilt: id=%u tiltX=%.1f tiltY=%.1f", id, tx, ty);
    mTouchData[id].tiltX = tx;
    mTouchData[id].tiltY = ty;
}

// --- Pointer stylus events ---

void WaylandStylusHandler::onPointerTool(uint32_t type) {
    ALOGVT("Pointer stylus tool: type=%u", type);
    mPointerToolType = static_cast<int32_t>(type);
    // Reset force/tilt when tool changes
    mPointerForce = -1.0f;
    mPointerTiltX = 0.0f;
    mPointerTiltY = 0.0f;
}

void WaylandStylusHandler::onPointerForce(uint32_t /*time*/, int32_t force) {
    mPointerForce = static_cast<float>(wl_fixed_to_double(force));
}

void WaylandStylusHandler::onPointerTilt(uint32_t /*time*/,
                                           int32_t tiltX, int32_t tiltY) {
    mPointerTiltX = static_cast<float>(wl_fixed_to_double(tiltX));
    mPointerTiltY = static_cast<float>(wl_fixed_to_double(tiltY));
}

// --- Query methods ---

int32_t WaylandStylusHandler::touchToolType(int32_t id) const {
    auto it = mTouchData.find(id);
    return (it != mTouchData.end()) ? it->second.toolType : 0;
}

float WaylandStylusHandler::touchForce(int32_t id) const {
    auto it = mTouchData.find(id);
    return (it != mTouchData.end()) ? it->second.force : -1.0f;
}

float WaylandStylusHandler::touchTiltX(int32_t id) const {
    auto it = mTouchData.find(id);
    return (it != mTouchData.end()) ? it->second.tiltX : 0.0f;
}

float WaylandStylusHandler::touchTiltY(int32_t id) const {
    auto it = mTouchData.find(id);
    return (it != mTouchData.end()) ? it->second.tiltY : 0.0f;
}

bool WaylandStylusHandler::hasTouchStylusData(int32_t id) const {
    auto it = mTouchData.find(id);
    return it != mTouchData.end() && it->second.toolType > 0;
}

void WaylandStylusHandler::clearTouchId(int32_t id) {
    mTouchData.erase(id);
}

void WaylandStylusHandler::resetPointerTool() {
    mPointerToolType = 0;
    mPointerForce = -1.0f;
    mPointerTiltX = 0.0f;
    mPointerTiltY = 0.0f;
}

} // namespace android

#pragma clang diagnostic pop

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
#include <unordered_map>

struct zcr_stylus_v2;
struct zcr_touch_stylus_v2;
struct zcr_pointer_stylus_v2;
struct wl_touch;
struct wl_pointer;

namespace android {

// Handles zcr_stylus_v2 protocol events (touch_stylus and pointer_stylus).
// Stores pending stylus data (tool type, force, tilt) that is queried by
// WaylandTouchHandler and WaylandPointerHandler when building BridgeInputEvent.
class WaylandStylusHandler {
public:
    WaylandStylusHandler();
    ~WaylandStylusHandler();

    WaylandStylusHandler(const WaylandStylusHandler&) = delete;
    WaylandStylusHandler& operator=(const WaylandStylusHandler&) = delete;

    // Initialize from zcr_stylus_v2 global + existing touch/pointer objects
    bool initialize(zcr_stylus_v2* stylus, wl_touch* touch, wl_pointer* pointer);

    // --- Touch stylus event handlers ---
    void onTouchTool(uint32_t id, uint32_t type);
    void onTouchForce(uint32_t time, uint32_t id, int32_t force);  // wl_fixed_t
    void onTouchTilt(uint32_t time, uint32_t id, int32_t tiltX, int32_t tiltY);

    // --- Pointer stylus event handlers ---
    void onPointerTool(uint32_t type);
    void onPointerForce(uint32_t time, int32_t force);  // wl_fixed_t
    void onPointerTilt(uint32_t time, int32_t tiltX, int32_t tiltY);

    // --- Query methods (called by touch/pointer handlers) ---

    // Touch: per-touch-id stylus data
    int32_t touchToolType(int32_t id) const;
    float touchForce(int32_t id) const;     // -1.0 = not set
    float touchTiltX(int32_t id) const;
    float touchTiltY(int32_t id) const;
    bool hasTouchStylusData(int32_t id) const;
    void clearTouchId(int32_t id);

    // Pointer: single stylus state
    int32_t pointerToolType() const { return mPointerToolType; }
    float pointerForce() const { return mPointerForce; }
    float pointerTiltX() const { return mPointerTiltX; }
    float pointerTiltY() const { return mPointerTiltY; }
    bool hasPointerStylusData() const { return mPointerToolType > 0; }
    void resetPointerTool();

private:
    struct TouchStylusData {
        int32_t toolType = 0;   // zcr_touch_stylus_v2 tool_type enum
        float force = -1.0f;    // -1.0 = not set
        float tiltX = 0.0f;
        float tiltY = 0.0f;
    };

    zcr_touch_stylus_v2* mTouchStylus = nullptr;
    zcr_pointer_stylus_v2* mPointerStylus = nullptr;

    // Per-touch-id stylus data
    std::unordered_map<int32_t, TouchStylusData> mTouchData;

    // Pointer stylus state
    int32_t mPointerToolType = 0;  // 0=none, 1=touch, 2=pen, 3=eraser
    float mPointerForce = -1.0f;
    float mPointerTiltX = 0.0f;
    float mPointerTiltY = 0.0f;
};

} // namespace android

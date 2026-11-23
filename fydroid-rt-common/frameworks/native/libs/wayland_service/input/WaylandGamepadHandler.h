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
#include <string>
#include <unordered_map>
#include <vector>

struct wl_seat;
struct zcr_gaming_input_v2;
struct zcr_gaming_seat_v2;
struct zcr_gamepad_v2;

namespace android {

class InputBridge;

// Number of gamepad axis slots carried over the bridge, in Android order:
// X, Y, Z, RZ, LTRIGGER, RTRIGGER, HAT_X, HAT_Y.
static constexpr int kMaxGamepadAxes = 8;

// Handles zcr_gaming_input_v2 protocol events. Manages multiple gamepads
// via zcr_gaming_seat_v2, batches axis/button events per frame, and sends
// BridgeInputEvent with BRIDGE_DEVICE_GAMEPAD.
//
// Note on the wire format: gaming-input-unstable-v2.xml documents button and
// axis ids as "defined by the W3C 'Standard Gamepad'", but exo does not
// implement that. components/exo/gamepad.cc forwards ui::GamepadEvent::code()
// verbatim, which ui/events/ozone/evdev/gamepad_event_converter_evdev.cc built
// straight from evdev_ev.code -- so the ids on the wire are raw Linux evdev
// codes (BTN_A == 0x130, ABS_HAT0X == 0x10), and values are raw evdev values.
// Decode them as evdev, not as W3C indices.
class WaylandGamepadHandler {
public:
    WaylandGamepadHandler();
    ~WaylandGamepadHandler();

    WaylandGamepadHandler(const WaylandGamepadHandler&) = delete;
    WaylandGamepadHandler& operator=(const WaylandGamepadHandler&) = delete;

    // Initialize from zcr_gaming_input_v2 global + seat
    bool initialize(zcr_gaming_input_v2* gamingInput, wl_seat* seat,
                    InputBridge* bridge);

    // Re-emit BRIDGE_EVENT_DEVICE_ADDED for every currently-known gamepad.
    // Used by WaylandInputManager when the bridge pipe fd arrives, since
    // any DEVICE_ADDED published before the fd was set was silently dropped.
    void publishCurrentGamepads();

    // Gaming seat event handlers
    void onGamepadAdded(zcr_gamepad_v2* gamepad);
    void onGamepadAddedWithDeviceInfo(zcr_gamepad_v2* gamepad,
                                      const char* name, uint32_t bus,
                                      uint32_t vendorId, uint32_t productId,
                                      uint32_t version);

    // Gamepad event handlers
    void onGamepadRemoved(zcr_gamepad_v2* gamepad);
    void onGamepadAxis(zcr_gamepad_v2* gamepad, uint32_t time,
                       uint32_t axis, int32_t value);  // wl_fixed_t
    void onGamepadButton(zcr_gamepad_v2* gamepad, uint32_t time,
                          uint32_t button, uint32_t state, int32_t analog);
    void onGamepadFrame(zcr_gamepad_v2* gamepad, uint32_t time);
    void onGamepadAxisAdded(zcr_gamepad_v2* gamepad, uint32_t index,
                            int32_t minValue, int32_t maxValue,
                            int32_t flat, int32_t fuzz, int32_t resolution);
    void onGamepadActivated(zcr_gamepad_v2* gamepad);

private:
    // Normalised range for one axis slot, built from zcr_gamepad_v2::axis_added
    // (which carries the device's raw input_absinfo).
    struct AxisRange {
        bool present = false;
        bool centered = false;      // true: normalise to -1..1; false: 0..1
        float rawMin = 0.0f;
        float rawMax = 0.0f;
        float flat = 0.0f;          // deadzone, already in normalised units
        float fuzz = 0.0f;
    };

    // A button transition waiting for the next frame event. More than one
    // button can change within a single frame -- chords like A+B, or a face
    // button pressed while the d-pad moves -- so they queue rather than
    // overwrite a single pending slot.
    struct PendingButton {
        int32_t keyCode = 0;
        int32_t scanCode = 0;
        int32_t action = 0;
    };

    struct GamepadInfo {
        int32_t id;                // Unique gamepad ID for Android
        std::string name;          // Real device name, as reported by Chrome
        uint32_t vendorId = 0;
        uint32_t productId = 0;
        uint32_t busType = 0;
        uint32_t version = 0;
        bool activated = false;
        bool published = false;    // DEVICE_ADDED already sent to the bridge

        // Pending per-frame state
        float axisValues[kMaxGamepadAxes] = {};
        AxisRange axisRanges[kMaxGamepadAxes];
        bool axisChanged = false;

        std::vector<PendingButton> pendingButtons;
    };

    int64_t now() const;
    void sendAxisEvent(const GamepadInfo& info);
    void sendButtonEvents(GamepadInfo& info);
    // Announce the gamepad to Android. Deferred until the activated event,
    // because exo sends every axis_added between gamepad_added_with_device_info
    // and activated -- publishing any earlier would describe a gamepad with no
    // axis ranges.
    void publishDeviceAdded(GamepadInfo& info);

    zcr_gaming_seat_v2* mGamingSeat = nullptr;
    InputBridge* mBridge = nullptr;

    // Map from zcr_gamepad_v2 pointer to gamepad info
    std::unordered_map<zcr_gamepad_v2*, GamepadInfo> mGamepads;
    int32_t mNextGamepadId = 0;
};

} // namespace android

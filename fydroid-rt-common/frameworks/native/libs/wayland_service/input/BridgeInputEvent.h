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

// BridgeInputEvent: Fixed-size binary struct written to a named pipe.
// A custom InputReaderArc (future task) reads these and feeds events
// into Android's InputDispatcher.
//
// ABI WARNING: the writer (wayland_service, inside SurfaceFlinger) and the
// reader (inputreader_arc, inside system_server) exchange this struct as raw
// bytes with no length prefix or version field. Changing its layout desyncs the
// stream, so both binaries must be rebuilt and deployed together.

#pragma once

#include <cstdint>

namespace android {

enum BridgeEventType : int32_t {
    BRIDGE_EVENT_CYCLED_TIMESTAMP = 0,  // Keepalive/sync (unused for now)
    BRIDGE_EVENT_KEY = 1,
    BRIDGE_EVENT_MOTION = 2,
    // Device lifecycle. deviceType carries the BridgeDeviceType. For
    // BRIDGE_DEVICE_GAMEPAD, gamepadId identifies the specific instance;
    // for singleton classes (pointer/keyboard/touch/stylus/relative mouse)
    // gamepadId is unused (0).
    BRIDGE_EVENT_DEVICE_ADDED = 3,
    BRIDGE_EVENT_DEVICE_REMOVED = 4,
    // Chrome OS told us whether a PHYSICAL keyboard is in use, via
    // zcr_keyboard_device_configuration_v1::type_change. `action` carries a
    // BridgeKeyboardType.
    //
    // This decides Configuration.keyboard on the Android side, and through it
    // hardKeyboardHidden — which is what every third-party IME consults before
    // deciding whether to draw an on-screen keyboard. Without it fydroid claims
    // a built-in alphabetic keyboard forever (the wl_seat always advertises
    // wl_keyboard, physical hardware or not) and Gboard stays hidden even on a
    // tablet with no keyboard attached.
    BRIDGE_EVENT_KEYBOARD_TYPE = 5,
};

enum BridgeKeyboardType : int32_t {
    BRIDGE_KEYBOARD_PHYSICAL = 0,
    BRIDGE_KEYBOARD_VIRTUAL = 1,
};

enum BridgeDeviceType : int32_t {
    BRIDGE_DEVICE_MOUSE = 0,
    BRIDGE_DEVICE_TOUCHSCREEN = 1,
    BRIDGE_DEVICE_KEYBOARD = 2,
    BRIDGE_DEVICE_STYLUS = 3,
    BRIDGE_DEVICE_MOUSE_RELATIVE = 4,
    BRIDGE_DEVICE_GAMEPAD = 5,
};

struct BridgeInputEvent {
    BridgeEventType type;
    int64_t timestampNanos;     // Event timestamp (CLOCK_MONOTONIC)

    int32_t deviceType;         // BridgeDeviceType
    int32_t displayId;          // Target display (0 = primary)
    int32_t action;             // KEY_DOWN/UP or MOTION_DOWN/UP/MOVE
    int32_t flags;

    // Key event fields
    int32_t keyCode;            // Android keycode (AKEYCODE_*)
    int32_t scanCode;           // Linux evdev scancode
    int32_t metaState;          // Modifier state bitmask

    // Motion event fields
    int32_t actionButton;       // For button press/release (AMOTION_EVENT_BUTTON_*)
    int32_t buttonState;        // Currently pressed buttons bitmask
    uint32_t pointerCount;      // Number of active pointers (1 for mouse)

    struct Pointer {
        int32_t id;             // Pointer/touch ID
        float x;                // Screen-space X coordinate
        float y;                // Screen-space Y coordinate
        float pressure;         // 0.0-1.0
    };
    Pointer pointers[16];       // Up to 16 simultaneous touch points

    // Cursor position for mouse events
    float cursorX;
    float cursorY;

    // Stylus fields (Phase 1)
    int32_t toolType;           // AMOTION_EVENT_TOOL_TYPE_* (0=unknown)
    float tiltX;                // Wayland tilt X in degrees [-90,90]
    float tiltY;                // Wayland tilt Y in degrees [-90,90]

    // Relative pointer fields (Phase 2)
    float relativeX;            // Unaccelerated relative X delta
    float relativeY;            // Unaccelerated relative Y delta

    // Gamepad fields (Phase 3)
    int32_t gamepadId;          // Gamepad instance ID
    // Slot order: X, Y, Z, RZ, LTRIGGER, RTRIGGER, HAT_X, HAT_Y.
    // Already normalised by WaylandGamepadHandler -- sticks and hats are
    // -1..1, triggers 0..1 -- because Chrome forwards raw evdev values
    // (a stick reads +-32767) and Android expects the normalised form.
    float gamepadAxisValues[8];

    // Gamepad identity and axis ranges. Only meaningful on
    // BRIDGE_EVENT_DEVICE_ADDED with deviceType == BRIDGE_DEVICE_GAMEPAD.
    // Chrome names the real evdev device behind the Wayland gamepad in
    // zcr_gaming_seat_v2::gamepad_added_with_device_info, so Android can
    // describe an actual controller rather than an anonymous virtual one.
    char    gamepadName[80];        // NUL-terminated real device name
    int32_t gamepadVendorId;
    int32_t gamepadProductId;
    // Linux BUS_* (BUS_USB 0x03 / BUS_BLUETOOTH 0x05), mapped by the sender
    // in busTypeFromProtocol(). exo puts the PROTOCOL enum on the wire
    // (usb = 0, bluetooth = 1), which is not a BUS_* value -- do not
    // forward it raw. 0 here means a bus type the protocol did not define.
    int32_t gamepadBusType;
    int32_t gamepadVersion;
    // Per-slot ranges, same slot order as gamepadAxisValues. These describe the
    // NORMALISED range, so they can be published as InputDeviceInfo motion
    // ranges: apps that follow Android's own gamepad sample call
    // getMotionRange(axis, source) and ignore any axis that returns null.
    int32_t gamepadAxisPresent[8];
    float   gamepadAxisMin[8];
    float   gamepadAxisMax[8];
    float   gamepadAxisFlat[8];     // Deadzone, in normalised units
    float   gamepadAxisFuzz[8];
};

} // namespace android

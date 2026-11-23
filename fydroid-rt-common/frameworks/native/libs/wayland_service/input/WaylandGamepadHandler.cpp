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

// WaylandGamepadHandler: zcr_gaming_input_v2 → BridgeInputEvent.
// Manages multiple gamepads, batches axis/button per frame.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandGamepadHandler"

#include "WaylandGamepadHandler.h"
#include "InputBridge.h"
#include "BridgeInputEvent.h"

#include <gaming-input-unstable-v2-client-protocol.h>
#include <wayland-client.h>

#include <android/input.h>
#include <android/keycodes.h>
#include <linux/input.h>
#include <time.h>
#include <algorithm>
#include <cmath>
#include <cstring>

#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

// Linux evdev button code → Android keycode.
//
// The ids arriving over zcr_gamepad_v2::button are raw evdev codes, not the
// W3C indices the protocol XML advertises (see the note in the header). Decoded
// as W3C, every BTN_* landed in the default case and was dropped, so no gamepad
// button ever reached Android. This table mirrors AOSP's own gamepad key
// layouts (frameworks/base/data/keyboards/Generic.kl).
static int32_t evdevButtonToKeyCode(uint32_t button) {
    switch (button) {
        case BTN_A:         return AKEYCODE_BUTTON_A;       // 0x130
        case BTN_B:         return AKEYCODE_BUTTON_B;       // 0x131
        case BTN_C:         return AKEYCODE_BUTTON_C;       // 0x132
        case BTN_X:         return AKEYCODE_BUTTON_X;       // 0x133
        case BTN_Y:         return AKEYCODE_BUTTON_Y;       // 0x134
        case BTN_Z:         return AKEYCODE_BUTTON_Z;       // 0x135
        case BTN_TL:        return AKEYCODE_BUTTON_L1;      // 0x136
        case BTN_TR:        return AKEYCODE_BUTTON_R1;      // 0x137
        case BTN_TL2:       return AKEYCODE_BUTTON_L2;      // 0x138
        case BTN_TR2:       return AKEYCODE_BUTTON_R2;      // 0x139
        case BTN_SELECT:    return AKEYCODE_BUTTON_SELECT;  // 0x13a
        case BTN_START:     return AKEYCODE_BUTTON_START;   // 0x13b
        case BTN_MODE:      return AKEYCODE_BUTTON_MODE;    // 0x13c
        case BTN_THUMBL:    return AKEYCODE_BUTTON_THUMBL;  // 0x13d
        case BTN_THUMBR:    return AKEYCODE_BUTTON_THUMBR;  // 0x13e
        // Pads that report the d-pad as discrete buttons rather than a hat.
        case BTN_DPAD_UP:    return AKEYCODE_DPAD_UP;       // 0x220
        case BTN_DPAD_DOWN:  return AKEYCODE_DPAD_DOWN;     // 0x221
        case BTN_DPAD_LEFT:  return AKEYCODE_DPAD_LEFT;     // 0x222
        case BTN_DPAD_RIGHT: return AKEYCODE_DPAD_RIGHT;    // 0x223
        default: return AKEYCODE_UNKNOWN;
    }
}

// Linux evdev axis code → bridge axis slot, or -1 to ignore.
//
// Slots are in Android order (see gamepadAxisToAndroid in InputReaderArc):
// 0=X 1=Y 2=Z 3=RZ 4=LTRIGGER 5=RTRIGGER 6=HAT_X 7=HAT_Y. This is the layout
// AOSP itself uses for Xbox-style pads in
// frameworks/base/data/keyboards/Vendor_045e_Product_028e.kl: the right stick
// is Z/RZ and the triggers are LTRIGGER/RTRIGGER, which is also what the W3C
// "Standard Gamepad" the old code aimed at describes.
//
// The d-pad hat (ABS_HAT0X/Y == 16/17) used to be compared against the slot
// count of 8 and discarded, which is why the d-pad did nothing at all.
static int evdevAxisToSlot(uint32_t axis) {
    switch (axis) {
        case ABS_X:       return 0;  // left stick X
        case ABS_Y:       return 1;  // left stick Y
        case ABS_RX:      return 2;  // right stick X → AXIS_Z
        case ABS_RY:      return 3;  // right stick Y → AXIS_RZ
        case ABS_Z:       return 4;  // left trigger  → AXIS_LTRIGGER
        case ABS_RZ:      return 5;  // right trigger → AXIS_RTRIGGER
        case ABS_HAT0X:   return 6;  // d-pad X
        case ABS_HAT0Y:   return 7;  // d-pad Y
        default:          return -1;
    }
}

// Sticks and hats rest at centre and swing both ways (-1..1); triggers rest at
// zero and only rise (0..1). Android's axis conventions expect exactly that.
static bool slotIsCentered(int slot) {
    return slot != 4 && slot != 5;
}

// --- Static gaming seat listeners ---

static void gamingSeatGamepadAdded(void* data, struct zcr_gaming_seat_v2*,
                                    struct zcr_gamepad_v2* gamepad) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadAdded(gamepad);
}

static void gamingSeatGamepadAddedWithDeviceInfo(
        void* data, struct zcr_gaming_seat_v2*,
        struct zcr_gamepad_v2* gamepad, const char* name, uint32_t bus,
        uint32_t vendorId, uint32_t productId, uint32_t version) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadAddedWithDeviceInfo(
            gamepad, name, bus, vendorId, productId, version);
}

static const struct zcr_gaming_seat_v2_listener sGamingSeatListener = {
    .gamepad_added = gamingSeatGamepadAdded,
    .gamepad_added_with_device_info = gamingSeatGamepadAddedWithDeviceInfo,
};

// --- Static gamepad listeners ---

static void gamepadRemoved(void* data, struct zcr_gamepad_v2* gamepad) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadRemoved(gamepad);
}

static void gamepadAxis(void* data, struct zcr_gamepad_v2* gamepad,
                         uint32_t time, uint32_t axis, wl_fixed_t value) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadAxis(
            gamepad, time, axis, value);
}

static void gamepadButton(void* data, struct zcr_gamepad_v2* gamepad,
                           uint32_t time, uint32_t button,
                           uint32_t state, wl_fixed_t analog) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadButton(
            gamepad, time, button, state, analog);
}

static void gamepadFrame(void* data, struct zcr_gamepad_v2* gamepad,
                          uint32_t time) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadFrame(gamepad, time);
}

static void gamepadAxisAdded(void* data, struct zcr_gamepad_v2* gamepad,
                              uint32_t index, int32_t minValue,
                              int32_t maxValue, int32_t flat,
                              int32_t fuzz, int32_t resolution) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadAxisAdded(
            gamepad, index, minValue, maxValue, flat, fuzz, resolution);
}

static void gamepadActivated(void* data, struct zcr_gamepad_v2* gamepad) {
    static_cast<WaylandGamepadHandler*>(data)->onGamepadActivated(gamepad);
}

static void gamepadVibratorAdded(void*, struct zcr_gamepad_v2*,
                                  struct zcr_gamepad_vibrator_v2*) {
    // Vibration support deferred
}

static void gamepadSupportedKeyBits(void*, struct zcr_gamepad_v2*,
                                     struct wl_array*) {
    // Key bits info — not needed for basic gamepad support
}

static const struct zcr_gamepad_v2_listener sGamepadListener = {
    .removed = gamepadRemoved,
    .axis = gamepadAxis,
    .button = gamepadButton,
    .frame = gamepadFrame,
    .axis_added = gamepadAxisAdded,
    .activated = gamepadActivated,
    .vibrator_added = gamepadVibratorAdded,
    .supported_key_bits = gamepadSupportedKeyBits,
};

// --- WaylandGamepadHandler implementation ---

WaylandGamepadHandler::WaylandGamepadHandler() = default;

WaylandGamepadHandler::~WaylandGamepadHandler() {
    for (auto& [gamepad, info] : mGamepads) {
        zcr_gamepad_v2_destroy(gamepad);
    }
    mGamepads.clear();

    if (mGamingSeat) {
        zcr_gaming_seat_v2_destroy(mGamingSeat);
    }
}

int64_t WaylandGamepadHandler::now() const {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return static_cast<int64_t>(ts.tv_sec) * 1000000000LL + ts.tv_nsec;
}

bool WaylandGamepadHandler::initialize(zcr_gaming_input_v2* gamingInput,
                                        wl_seat* seat, InputBridge* bridge) {
    if (!gamingInput || !seat) {
        ALOGW("Gaming input or seat not available");
        return false;
    }

    mBridge = bridge;

    mGamingSeat = zcr_gaming_input_v2_get_gaming_seat(gamingInput, seat);
    if (!mGamingSeat) {
        ALOGE("Failed to create zcr_gaming_seat_v2");
        return false;
    }
    zcr_gaming_seat_v2_add_listener(mGamingSeat, &sGamingSeatListener, this);

    ALOGI("Gamepad handler initialized");
    return true;
}

void WaylandGamepadHandler::onGamepadAdded(zcr_gamepad_v2* gamepad) {
    ALOGI("Gamepad added (no device info)");
    GamepadInfo info;
    info.id = mNextGamepadId++;
    info.name = "Gamepad";
    info.activated = true;  // No axis_added/activated sequence for this path
    mGamepads[gamepad] = info;
    zcr_gamepad_v2_add_listener(gamepad, &sGamepadListener, this);
    // Nothing further is coming on this path, so announce immediately. The
    // gamepad is described with no axis ranges, which is all the older
    // gamepad_added event can tell us.
    publishDeviceAdded(mGamepads[gamepad]);
}

// exo's `bus` is the *protocol's* enum (gaming-input-unstable-v2.xml: usb = 0,
// bluetooth = 1), NOT a Linux BUS_* constant (BUS_USB = 0x03, BUS_BLUETOOTH =
// 0x05) -- zcr_gaming_input.cc's GetGamepadBusType returns
// ZCR_GAMING_SEAT_V2_BUS_TYPE_*. The two namespaces look interchangeable and
// are not: the same trap as the button/axis codes being raw evdev rather than
// the W3C ids that same XML documents. Forwarding it unmapped put a protocol
// enum in InputDeviceIdentifier.bus, where a USB pad reported 0 -- not a bus
// type at all, and indistinguishable from "unknown". Nothing errors and the pad
// works; only the identity is wrong, which is what controller-profile databases
// match on alongside name and VID:PID. Found while
// rebuilding an evdev device from these fields for the Notialis container.
static uint16_t busTypeFromProtocol(uint32_t bus) {
    switch (bus) {
        case ZCR_GAMING_SEAT_V2_BUS_TYPE_USB:
            return BUS_USB;
        case ZCR_GAMING_SEAT_V2_BUS_TYPE_BLUETOOTH:
            return BUS_BLUETOOTH;
        default:
            // A bus type this protocol version does not define. 0 is not a
            // valid BUS_*, so it reads as genuinely unknown.
            return 0;
    }
}

void WaylandGamepadHandler::onGamepadAddedWithDeviceInfo(
        zcr_gamepad_v2* gamepad, const char* name, uint32_t bus,
        uint32_t vendorId, uint32_t productId, uint32_t version) {
    ALOGI("Gamepad added: name=%s bus=%u (proto %u) vendor=0x%04x product=0x%04x version=%u",
          name, busTypeFromProtocol(bus), bus, vendorId, productId, version);
    GamepadInfo info;
    info.id = mNextGamepadId++;
    info.name = name ? name : "Gamepad";
    info.vendorId = vendorId;
    info.productId = productId;
    info.busType = busTypeFromProtocol(bus);
    info.version = version;
    info.activated = false;  // Wait for activated event
    mGamepads[gamepad] = info;
    zcr_gamepad_v2_add_listener(gamepad, &sGamepadListener, this);
    // Deliberately not announced yet: exo sends every axis_added between this
    // event and activated, and the announcement carries the axis ranges.
}

void WaylandGamepadHandler::onGamepadRemoved(zcr_gamepad_v2* gamepad) {
    auto it = mGamepads.find(gamepad);
    if (it != mGamepads.end()) {
        ALOGI("Gamepad removed: id=%d name=%s",
              it->second.id, it->second.name.c_str());
        if (mBridge) {
            BridgeInputEvent event = {};
            event.type = BRIDGE_EVENT_DEVICE_REMOVED;
            event.deviceType = BRIDGE_DEVICE_GAMEPAD;
            event.gamepadId = it->second.id;
            mBridge->writeEvent(event);
        }
        mGamepads.erase(it);
    }
    zcr_gamepad_v2_destroy(gamepad);
}

void WaylandGamepadHandler::publishCurrentGamepads() {
    if (!mBridge) return;
    for (auto& [gp, info] : mGamepads) {
        (void)gp;
        // Anything published before the bridge fd existed was dropped on the
        // floor, so re-announce it with the identity and ranges we have by now.
        info.published = false;
        publishDeviceAdded(info);
    }
}

void WaylandGamepadHandler::onGamepadAxis(zcr_gamepad_v2* gamepad,
                                           uint32_t /*time*/,
                                           uint32_t axis, int32_t value) {
    auto it = mGamepads.find(gamepad);
    if (it == mGamepads.end() || !it->second.activated) return;

    // `axis` is an evdev ABS code, not a W3C index. Treating it as an index
    // routed the right stick and triggers to the wrong Android axes and threw
    // the d-pad hat away entirely for being >= 8.
    const int slot = evdevAxisToSlot(axis);
    if (slot < 0) return;

    GamepadInfo& info = it->second;
    const AxisRange& range = info.axisRanges[slot];

    // Values on the wire are raw evdev too -- a stick reads +-32767 where
    // Android wants +-1 -- so normalise against the range axis_added gave us.
    // Without a range (the bare gamepad_added path) pass the value through;
    // there is nothing better to do with it.
    float normalized = static_cast<float>(wl_fixed_to_double(value));
    if (range.present) {
        const float span = range.rawMax - range.rawMin;
        if (range.centered) {
            normalized = 2.0f * (normalized - range.rawMin) / span - 1.0f;
            normalized = std::max(-1.0f, std::min(1.0f, normalized));
        } else {
            normalized = (normalized - range.rawMin) / span;
            normalized = std::max(0.0f, std::min(1.0f, normalized));
        }
        // Apply the device's own deadzone. Android normally leaves this to the
        // app via MotionRange.getFlat(), but a worn stick that never quite
        // returns to centre otherwise reads as constant drift.
        if (range.centered && range.flat > 0.0f &&
            std::fabs(normalized) <= range.flat) {
            normalized = 0.0f;
        }
    }

    if (info.axisValues[slot] != normalized) {
        info.axisValues[slot] = normalized;
        info.axisChanged = true;
    }
}

void WaylandGamepadHandler::onGamepadButton(zcr_gamepad_v2* gamepad,
                                             uint32_t /*time*/,
                                             uint32_t button,
                                             uint32_t state,
                                             int32_t /*analog*/) {
    auto it = mGamepads.find(gamepad);
    if (it == mGamepads.end() || !it->second.activated) return;

    // `button` is an evdev BTN_* code, not a W3C index -- decoding it as an
    // index sent every real button to AKEYCODE_UNKNOWN and dropped it here.
    int32_t keyCode = evdevButtonToKeyCode(button);
    if (keyCode == AKEYCODE_UNKNOWN) {
        ALOGVT("Gamepad button: evdev 0x%x unmapped, ignoring", button);
        return;
    }

    PendingButton pending;
    pending.keyCode = keyCode;
    pending.scanCode = static_cast<int32_t>(button);
    pending.action = (state == ZCR_GAMEPAD_V2_BUTTON_STATE_PRESSED)
                             ? AKEY_EVENT_ACTION_DOWN
                             : AKEY_EVENT_ACTION_UP;
    it->second.pendingButtons.push_back(pending);
}

void WaylandGamepadHandler::onGamepadFrame(zcr_gamepad_v2* gamepad,
                                            uint32_t /*time*/) {
    auto it = mGamepads.find(gamepad);
    if (it == mGamepads.end() || !it->second.activated) return;

    if (it->second.axisChanged) {
        sendAxisEvent(it->second);
        it->second.axisChanged = false;
    }
    sendButtonEvents(it->second);
}

void WaylandGamepadHandler::onGamepadAxisAdded(zcr_gamepad_v2* gamepad,
                                                uint32_t index,
                                                int32_t minValue,
                                                int32_t maxValue,
                                                int32_t flat,
                                                int32_t fuzz,
                                                int32_t /*resolution*/) {
    auto it = mGamepads.find(gamepad);
    if (it == mGamepads.end()) return;

    // `index` is an evdev ABS code, and the range is the device's raw
    // input_absinfo. Keeping it is what lets the raw values be normalised and
    // real motion ranges be published; it used to be logged and thrown away.
    const int slot = evdevAxisToSlot(index);
    if (slot < 0) {
        ALOGVT("Gamepad axis added: evdev axis 0x%x unmapped, ignoring", index);
        return;
    }
    if (maxValue <= minValue) {
        ALOGW("Gamepad axis 0x%x has empty range [%d, %d], ignoring",
              index, minValue, maxValue);
        return;
    }

    AxisRange& range = it->second.axisRanges[slot];
    range.present = true;
    range.centered = slotIsCentered(slot);
    range.rawMin = static_cast<float>(minValue);
    range.rawMax = static_cast<float>(maxValue);
    // flat/fuzz are raw units; scale them the same way values are scaled.
    const float span = range.rawMax - range.rawMin;
    const float scale = range.centered ? 2.0f / span : 1.0f / span;
    range.flat = static_cast<float>(flat) * scale;
    range.fuzz = static_cast<float>(fuzz) * scale;

    ALOGVT("Gamepad axis added: evdev=0x%x slot=%d range=[%d, %d] flat=%d",
          index, slot, minValue, maxValue, flat);
}

void WaylandGamepadHandler::onGamepadActivated(zcr_gamepad_v2* gamepad) {
    auto it = mGamepads.find(gamepad);
    if (it != mGamepads.end()) {
        it->second.activated = true;
        ALOGI("Gamepad activated: id=%d name=%s",
              it->second.id, it->second.name.c_str());
        // Every axis_added has arrived by now, so the gamepad can be described
        // to Android in full.
        publishDeviceAdded(it->second);
    }
}

void WaylandGamepadHandler::publishDeviceAdded(GamepadInfo& info) {
    if (!mBridge || info.published) return;

    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));
    event.type = BRIDGE_EVENT_DEVICE_ADDED;
    event.timestampNanos = now();
    event.deviceType = BRIDGE_DEVICE_GAMEPAD;
    event.gamepadId = info.id;

    strncpy(event.gamepadName, info.name.c_str(), sizeof(event.gamepadName) - 1);
    event.gamepadName[sizeof(event.gamepadName) - 1] = '\0';
    event.gamepadVendorId = static_cast<int32_t>(info.vendorId);
    event.gamepadProductId = static_cast<int32_t>(info.productId);
    event.gamepadBusType = static_cast<int32_t>(info.busType);
    event.gamepadVersion = static_cast<int32_t>(info.version);

    for (int i = 0; i < kMaxGamepadAxes; i++) {
        const AxisRange& range = info.axisRanges[i];
        if (!range.present) continue;
        event.gamepadAxisPresent[i] = 1;
        event.gamepadAxisMin[i] = range.centered ? -1.0f : 0.0f;
        event.gamepadAxisMax[i] = 1.0f;
        event.gamepadAxisFlat[i] = range.flat;
        event.gamepadAxisFuzz[i] = range.fuzz;
    }

    info.published = true;
    mBridge->writeEvent(event);
}

void WaylandGamepadHandler::sendAxisEvent(const GamepadInfo& info) {
    if (!mBridge) return;

    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));

    event.type = BRIDGE_EVENT_MOTION;
    event.timestampNanos = now();
    event.deviceType = BRIDGE_DEVICE_GAMEPAD;
    event.displayId = 0;
    event.action = AMOTION_EVENT_ACTION_MOVE;
    event.pointerCount = 1;
    event.pointers[0].id = 0;
    event.gamepadId = info.id;

    for (int i = 0; i < kMaxGamepadAxes; i++) {
        event.gamepadAxisValues[i] = info.axisValues[i];
    }

    ALOGVT("Gamepad axis: id=%d axes=[%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f]",
          info.id,
          info.axisValues[0], info.axisValues[1],
          info.axisValues[2], info.axisValues[3],
          info.axisValues[4], info.axisValues[5],
          info.axisValues[6], info.axisValues[7]);
    mBridge->writeEvent(event);
}

void WaylandGamepadHandler::sendButtonEvents(GamepadInfo& info) {
    if (!mBridge) {
        info.pendingButtons.clear();
        return;
    }

    for (const PendingButton& pending : info.pendingButtons) {
        BridgeInputEvent event;
        memset(&event, 0, sizeof(event));

        event.type = BRIDGE_EVENT_KEY;
        event.timestampNanos = now();
        event.deviceType = BRIDGE_DEVICE_GAMEPAD;
        event.displayId = 0;
        event.action = pending.action;
        event.keyCode = pending.keyCode;
        event.scanCode = pending.scanCode;
        event.gamepadId = info.id;

        ALOGVT("Gamepad button: id=%d keyCode=%d action=%d",
              info.id, pending.keyCode, pending.action);
        mBridge->writeEvent(event);
    }
    info.pendingButtons.clear();
}

} // namespace android

#pragma clang diagnostic pop

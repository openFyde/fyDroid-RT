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

// WaylandKeyboardHandler: wl_keyboard events → BridgeInputEvent structs.
// Evdev scancodes are passed through directly.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandKeyboardHandler"

#include "WaylandKeyboardHandler.h"

#include <keyboard-configuration-unstable-v1-client-protocol.h>
#include <keyboard-extension-unstable-v1-client-protocol.h>
#include <wayland-client.h>
#include "WaylandInputManager.h"
#include "InputBridge.h"
#include "BridgeInputEvent.h"
#include "../WaylandDataDevice.h"
#include "../window/WaylandSurfaceData.h"
#include "../window/WaylandWindow.h"

#include <android/input.h>
#include <android/keycodes.h>
#include <linux/input.h>
#include <time.h>
#include <unistd.h>
#include <algorithm>
#include <cinttypes>
#include <cstring>
#include <vector>
#include <wayland-client.h>

#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

// --- Static Wayland listeners ---

static void keyboardKeymap(void* data, struct wl_keyboard*, uint32_t format,
                           int32_t fd, uint32_t size) {
    static_cast<WaylandKeyboardHandler*>(data)->onKeymap(format, fd, size);
}

static void keyboardEnter(void* data, struct wl_keyboard*, uint32_t serial,
                          struct wl_surface* surface, struct wl_array* keys) {
    static_cast<WaylandKeyboardHandler*>(data)->onEnter(serial, surface, keys);
}

static void keyboardLeave(void* data, struct wl_keyboard*, uint32_t serial,
                          struct wl_surface* surface) {
    static_cast<WaylandKeyboardHandler*>(data)->onLeave(serial, surface);
}

static void keyboardKey(void* data, struct wl_keyboard*, uint32_t serial,
                        uint32_t time, uint32_t key, uint32_t state) {
    static_cast<WaylandKeyboardHandler*>(data)->onKey(serial, time, key, state);
}

static void keyboardModifiers(void* data, struct wl_keyboard*, uint32_t serial,
                              uint32_t modsDepressed, uint32_t modsLatched,
                              uint32_t modsLocked, uint32_t group) {
    static_cast<WaylandKeyboardHandler*>(data)->onModifiers(
            serial, modsDepressed, modsLatched, modsLocked, group);
}

static void keyboardRepeatInfo(void* data, struct wl_keyboard*,
                               int32_t rate, int32_t delay) {
    static_cast<WaylandKeyboardHandler*>(data)->onRepeatInfo(rate, delay);
}

static const struct wl_keyboard_listener sKeyboardListener = {
    .keymap = keyboardKeymap,
    .enter = keyboardEnter,
    .leave = keyboardLeave,
    .key = keyboardKey,
    .modifiers = keyboardModifiers,
    .repeat_info = keyboardRepeatInfo,
};

// --- Convert Wayland/XKB modifier bitmask to Android meta state ---
// Wayland modsDepressed uses XKB modifier indices. Common mappings:
// Bit 0 = Shift, Bit 1 = CapsLock, Bit 2 = Control, Bit 3 = Mod1 (Alt),
// Bit 4 = Mod2 (NumLock), Bit 6 = Mod4 (Meta/Super)

static int32_t xkbModsToMetaState(uint32_t modsDepressed, uint32_t modsLocked) {
    int32_t meta = 0;
    if (modsDepressed & (1 << 0)) meta |= AMETA_SHIFT_ON;
    if (modsLocked & (1 << 1))    meta |= AMETA_CAPS_LOCK_ON;
    if (modsDepressed & (1 << 2)) meta |= AMETA_CTRL_ON;
    if (modsDepressed & (1 << 3)) meta |= AMETA_ALT_ON;
    if (modsLocked & (1 << 4))    meta |= AMETA_NUM_LOCK_ON;
    if (modsDepressed & (1 << 6)) meta |= AMETA_META_ON;
    return meta;
}

// --- WaylandKeyboardHandler implementation ---

// exo forgets an unacked key after 1s (treating it as handled), so an ack
// later than that has nothing to match. Keep a little longer, then drop.
static constexpr int64_t kPendingAckLifetimeNanos = 2000000000LL;
static constexpr size_t kMaxPendingAcks = 256;

std::atomic<WaylandKeyboardHandler*> WaylandKeyboardHandler::sInstance{nullptr};

WaylandKeyboardHandler* WaylandKeyboardHandler::instance() {
    return sInstance.load();
}

WaylandKeyboardHandler::WaylandKeyboardHandler() = default;

WaylandKeyboardHandler::~WaylandKeyboardHandler() {
    WaylandKeyboardHandler* self = this;
    sInstance.compare_exchange_strong(self, nullptr);
    {
        std::lock_guard<std::mutex> lock(mAckMutex);
        if (mExtendedKeyboard) {
            zcr_extended_keyboard_v1_destroy(mExtendedKeyboard);
            mExtendedKeyboard = nullptr;
        }
        mPendingAcks.clear();
    }
    if (mDeviceConfig) {
        zcr_keyboard_device_configuration_v1_destroy(mDeviceConfig);
        mDeviceConfig = nullptr;
    }
    if (mKeyboard) {
        wl_keyboard_destroy(mKeyboard);
    }
}

int64_t WaylandKeyboardHandler::now() const {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return static_cast<int64_t>(ts.tv_sec) * 1000000000LL + ts.tv_nsec;
}

void WaylandKeyboardHandler::clearFocusIfWindow(WaylandWindow* window) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    // Match the surface as well as the window. A task holds focus through
    // mFocusContainer with mFocusWindow left null, so on the one path that
    // destroys a window WITHOUT destroying its container — the app-window to
    // modal conversion in WaylandLayerContainer::setIsDialog — matching on the
    // window alone left mFocusSurface pointing at a destroyed wl_surface proxy.
    if (!window) return;
    if (mFocusWindow == window ||
        (window->surface() && mFocusSurface == window->surface())) {
        ALOGI("clearFocusIfWindow: clearing dangling focus window %p", window);
        mFocusWindow = nullptr;
        mFocusSurface = nullptr;
    }
}

void WaylandKeyboardHandler::sendKeyEvent(uint32_t scanCode, int32_t action,
                                          std::optional<uint32_t> serial) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));

    event.type = BRIDGE_EVENT_KEY;
    event.timestampNanos = now();
    if (serial) {
        std::lock_guard<std::mutex> ackLock(mAckMutex);
        if (mExtendedKeyboard) {
            while (!mPendingAcks.empty() &&
                   (mPendingAcks.size() >= kMaxPendingAcks ||
                    event.timestampNanos - mPendingAcks.front().eventTimeNanos >
                            kPendingAckLifetimeNanos)) {
                mPendingAcks.pop_front();
            }
            mPendingAcks.push_back({event.timestampNanos, *serial});
        }
    }
    event.deviceType = BRIDGE_DEVICE_KEYBOARD;
    event.displayId = mFocusWindow ? mFocusWindow->displayId() : 0;
    event.action = action;
    event.scanCode = static_cast<int32_t>(scanCode);
    event.keyCode = 0;  // Reader converts scanCode → Android keyCode
    event.metaState = mMetaState;

    mBridge->writeEvent(event);
}

// zcr_keyboard_device_configuration_v1 listener. Only type_change matters to
// us; the layout/key-bits events exist for clients that render their own
// keyboard, which fydroid does not.
static void deviceConfigTypeChange(void* data,
                                   struct zcr_keyboard_device_configuration_v1*,
                                   uint32_t type) {
    static_cast<WaylandKeyboardHandler*>(data)->onKeyboardTypeChanged(type);
}
static void deviceConfigLayoutChange(void*,
                                     struct zcr_keyboard_device_configuration_v1*,
                                     const char*) {}
static void deviceConfigSupportedKeyBits(void*,
                                         struct zcr_keyboard_device_configuration_v1*,
                                         struct wl_array*) {}
static void deviceConfigLayoutInstall(void*,
                                      struct zcr_keyboard_device_configuration_v1*,
                                      const char*, uint32_t, int32_t fd, uint32_t) {
    // Ships a keymap fd we have no use for — fydroid passes evdev scancodes
    // straight through. Close it or it leaks on every installed layout.
    if (fd >= 0) ::close(fd);
}

static const struct zcr_keyboard_device_configuration_v1_listener
        sDeviceConfigListener = {
    .type_change = deviceConfigTypeChange,
    .layout_change = deviceConfigLayoutChange,
    .supported_key_bits = deviceConfigSupportedKeyBits,
    .layout_install = deviceConfigLayoutInstall,
};

bool WaylandKeyboardHandler::initialize(wl_seat* seat, InputBridge* bridge) {
    mBridge = bridge;

    mKeyboard = wl_seat_get_keyboard(seat);
    if (!mKeyboard) {
        ALOGE("wl_seat_get_keyboard failed");
        return false;
    }
    wl_keyboard_add_listener(mKeyboard, &sKeyboardListener, this);

    ALOGI("Keyboard handler initialized");
    return true;
}

void WaylandKeyboardHandler::attachKeyboardExtension(
        zcr_keyboard_extension_v1* extension, wl_display* display) {
    if (!extension || !mKeyboard || !display) {
        if (!extension) {
            ALOGW("no zcr_keyboard_extension_v1; ash will see every key");
        }
        return;
    }
    {
        std::lock_guard<std::mutex> lock(mAckMutex);
        if (mExtendedKeyboard) return;
        mExtendedKeyboard =
                zcr_keyboard_extension_v1_get_extended_keyboard(extension, mKeyboard);
        if (!mExtendedKeyboard) return;
        mDisplay = display;
    }
    sInstance.store(this);
    ALOGI("acking keys through zcr_extended_keyboard_v1");
}

void WaylandKeyboardHandler::ackKey(int64_t eventTimeNanos, bool handled) {
    std::lock_guard<std::mutex> lock(mAckMutex);
    if (!mExtendedKeyboard) return;
    auto it = std::find_if(mPendingAcks.begin(), mPendingAcks.end(),
                           [eventTimeNanos](const PendingAck& p) {
                               return p.eventTimeNanos == eventTimeNanos;
                           });
    if (it == mPendingAcks.end()) {
        ALOGVT("ackKey: no pending key at %" PRId64, eventTimeNanos);
        return;
    }
    uint32_t serial = it->serial;
    mPendingAcks.erase(it);
    ALOGVT("ackKey: serial=%u %s", serial, handled ? "handled" : "not handled");
    zcr_extended_keyboard_v1_ack_key(
            mExtendedKeyboard, serial,
            handled ? ZCR_EXTENDED_KEYBOARD_V1_HANDLED_STATE_HANDLED
                    : ZCR_EXTENDED_KEYBOARD_V1_HANDLED_STATE_NOT_HANDLED);
    // The event thread flushes only between polls; ash is waiting on this.
    wl_display_flush(mDisplay);
}

void WaylandKeyboardHandler::attachDeviceConfiguration(
        zcr_keyboard_configuration_v1* keyboardConfig) {
    if (!keyboardConfig || !mKeyboard || mDeviceConfig) {
        if (!keyboardConfig) {
            // Per the protocol: with no type_change the client must assume a
            // physical keyboard — which is the pre-existing fydroid behaviour.
            ALOGW("no zcr_keyboard_configuration_v1; assuming a physical keyboard");
        }
        return;
    }
    mDeviceConfig = zcr_keyboard_configuration_v1_get_keyboard_device_configuration(
            keyboardConfig, mKeyboard);
    if (mDeviceConfig) {
        zcr_keyboard_device_configuration_v1_add_listener(
                mDeviceConfig, &sDeviceConfigListener, this);
        ALOGI("tracking Chrome OS keyboard type");
    }
}

void WaylandKeyboardHandler::onKeyboardTypeChanged(uint32_t type) {
    mKeyboardPhysical =
            type == ZCR_KEYBOARD_DEVICE_CONFIGURATION_V1_KEYBOARD_TYPE_PHYSICAL;
    mHasKeyboardType = true;
    ALOGI("Chrome OS keyboard type: %s", mKeyboardPhysical ? "PHYSICAL" : "VIRTUAL");
    resendKeyboardType();
}

void WaylandKeyboardHandler::resendKeyboardType() {
    if (!mBridge || !mHasKeyboardType) return;
    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));
    event.type = BRIDGE_EVENT_KEYBOARD_TYPE;
    event.timestampNanos = now();
    event.deviceType = BRIDGE_DEVICE_KEYBOARD;
    event.action = mKeyboardPhysical ? BRIDGE_KEYBOARD_PHYSICAL
                                     : BRIDGE_KEYBOARD_VIRTUAL;
    mBridge->writeEvent(event);
}

void WaylandKeyboardHandler::onKeymap(uint32_t format, int32_t fd, uint32_t size) {
    ALOGI("Keyboard keymap: format=%u size=%u", format, size);
    // We don't need the XKB keymap — key events already use evdev scancodes.
    // Close the fd to avoid leaking it.
    ::close(fd);
}

void WaylandKeyboardHandler::onEnter(uint32_t serial, wl_surface* surface,
                                     wl_array* keys) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    auto* window = WaylandSurfaceData::getSurfaceWindow(surface);

    // Never move keyboard focus onto an input-transparent window (a toast):
    // keep whatever holds focus now, so a toast popping up mid-typing does not
    // redirect keystrokes to a window that cannot consume them.
    if (window && !window->acceptsInput()) {
        ALOGVT("Keyboard enter on input-transparent window %p ignored, "
               "keeping focus surface=%p", window, mFocusSurface);
        return;
    }

    mFocusSurface = surface;
    mFocusWindow = window;

    // Track serial for clipboard set_selection
    if (auto* dd = WaylandDataDevice::instance()) {
        dd->updateSerial(serial);
    }

    if (!mBridge || !keys) return;

    // Make Android's pressed keys match what Chrome says is down right now.
    // `keys` is the whole truth at this moment: anything Android still holds
    // that is missing from it was released while another client had focus,
    // and anything in it that Android does not hold was pressed out there. Only
    // the difference is sent — re-sending DOWN for a key Android already holds
    // is a second press, not a no-op.
    auto* begin = static_cast<const uint32_t*>(keys->data);
    auto* end = reinterpret_cast<const uint32_t*>(
            static_cast<const char*>(keys->data) + keys->size);
    const std::vector<uint32_t> nowHeld(begin, end);
    for (uint32_t key : std::vector<uint32_t>(mHeldKeys)) {
        if (std::find(nowHeld.begin(), nowHeld.end(), key) == nowHeld.end()) {
            ALOGVT("Keyboard enter: releasing key %u let go elsewhere", key);
            sendKeyEvent(key, AKEY_EVENT_ACTION_UP);
        }
    }
    for (uint32_t key : nowHeld) {
        if (std::find(mHeldKeys.begin(), mHeldKeys.end(), key) == mHeldKeys.end()) {
            ALOGVT("Keyboard enter: injecting held key %u", key);
            sendKeyEvent(key, AKEY_EVENT_ACTION_DOWN);
        }
    }
    mHeldKeys = nowHeld;

    ALOGVT("Keyboard enter: serial=%u surface=%p, %zu keys held",
          serial, surface, nowHeld.size());
}

void WaylandKeyboardHandler::onLeave(uint32_t serial, wl_surface* surface) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    ALOGVT("Keyboard leave: serial=%u surface=%p", serial, surface);
    // Ignore the leave for a surface we never focused (see onEnter).
    if (surface && mFocusSurface && surface != mFocusSurface) return;
    mFocusSurface = nullptr;
    mFocusWindow = nullptr;
    releaseHeldKeys();
}

void WaylandKeyboardHandler::releaseHeldKeys() {
    // After wl_keyboard.leave the compositor sends this client no more key
    // events — not even the releases — until the next enter. A key held across
    // the leave (the Alt of an Alt+Tab, the Ctrl of a Ctrl+Alt shortcut that
    // opens a system surface) therefore stayed down in Android forever:
    // InputReader kept it pressed, the dispatcher key-repeated it every 50 ms,
    // and every later key in any Android window became an Alt chord
    // (KEYCODE_ALT_LEFT held for seconds, cancelled over and over).
    // Protocol-wise the client must treat every key as released at leave.
    if (!mBridge || mHeldKeys.empty()) return;
    mMetaState = xkbModsToMetaState(0, mModsLocked);
    for (uint32_t key : mHeldKeys) {
        ALOGVT("Keyboard leave: releasing held key %u", key);
        sendKeyEvent(key, AKEY_EVENT_ACTION_UP);
    }
    mHeldKeys.clear();
}

void WaylandKeyboardHandler::onKey(uint32_t serial, uint32_t /*time*/,
                                   uint32_t key, uint32_t state) {
    if (auto* dd = WaylandDataDevice::instance()) {
        dd->updateSerial(serial);
    }
    if (!mBridge) return;

    // Wayland key state: 0 = released, 1 = pressed
    // Android: AKEY_EVENT_ACTION_DOWN = 0, AKEY_EVENT_ACTION_UP = 1
    int32_t action = (state == WL_KEYBOARD_KEY_STATE_PRESSED)
            ? AKEY_EVENT_ACTION_DOWN
            : AKEY_EVENT_ACTION_UP;

    ALOGVT("Keyboard key: scanCode=%u action=%d", key, action);
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    auto held = std::find(mHeldKeys.begin(), mHeldKeys.end(), key);
    if (action == AKEY_EVENT_ACTION_DOWN) {
        if (held == mHeldKeys.end()) mHeldKeys.push_back(key);
    } else if (held != mHeldKeys.end()) {
        mHeldKeys.erase(held);
    }
    sendKeyEvent(key, action, serial);
}

void WaylandKeyboardHandler::onModifiers(uint32_t /*serial*/,
                                         uint32_t modsDepressed,
                                         uint32_t modsLatched,
                                         uint32_t modsLocked,
                                         uint32_t group) {
    ALOGVT("Keyboard modifiers: depressed=0x%x latched=0x%x locked=0x%x group=%u",
          modsDepressed, modsLatched, modsLocked, group);
    mModsLocked = modsLocked;
    mMetaState = xkbModsToMetaState(modsDepressed, modsLocked);
}

void WaylandKeyboardHandler::onRepeatInfo(int32_t rate, int32_t delay) {
    ALOGI("Keyboard repeat_info: rate=%d delay=%d", rate, delay);
    // Android handles key repeat in InputDispatcher, so we ignore
    // the compositor's repeat settings.
}

} // namespace android

#pragma clang diagnostic pop

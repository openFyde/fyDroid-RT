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

#include <atomic>
#include <cstdint>
#include <deque>
#include <mutex>
#include <optional>
#include <vector>

struct wl_keyboard;
struct wl_seat;
struct zcr_keyboard_configuration_v1;
struct zcr_keyboard_device_configuration_v1;
struct zcr_keyboard_extension_v1;
struct zcr_extended_keyboard_v1;
struct wl_display;
struct wl_surface;
struct wl_array;

namespace android {

class InputBridge;
class WaylandWindow;

// Handles wl_keyboard events and writes BridgeInputEvent structs to an
// InputBridge pipe. Evdev scancodes are passed through directly; the
// reader side converts them to Android keycodes.
class WaylandKeyboardHandler {
public:
    WaylandKeyboardHandler();
    ~WaylandKeyboardHandler();

    WaylandKeyboardHandler(const WaylandKeyboardHandler&) = delete;
    WaylandKeyboardHandler& operator=(const WaylandKeyboardHandler&) = delete;

    // Bind wl_keyboard from seat. bridge must outlive this handler.
    bool initialize(wl_seat* seat, InputBridge* bridge);

    // Drop the cached focus window if it is `window`, which is about to be
    // destroyed. The pointer and touch handlers have had this since their focus
    // caches were found dangling; the keyboard's was missed, and it is the one
    // that is dereferenced on every keystroke (sendKeyEvent reads displayId()
    // off it). Chrome does not always send wl_keyboard.leave before a surface
    // goes away — a task torn down under the cursor simply stops existing — so
    // onLeave() cannot be relied on to clear it.
    void clearFocusIfWindow(WaylandWindow* window);

    // Start tracking whether a PHYSICAL keyboard is in use, so Android can
    // report Configuration.keyboard honestly. Separate from initialize()
    // because the protocol globals only arrive afterwards.
    void attachDeviceConfiguration(zcr_keyboard_configuration_v1* keyboardConfig);

    // Start acking keys through zcr_extended_keyboard_v1. Once exo knows we
    // ack, it keeps every key it sends us away from ash's accelerators until
    // the ack says not_handled; a key never acked counts as handled after 1s.
    // Without this exo offered each key to ash as well as to Android, so a
    // Ctrl+C an Android app used for copy still ran ash's Ctrl+C accelerator —
    // twice, in fact.
    void attachKeyboardExtension(zcr_keyboard_extension_v1* extension,
                                 wl_display* display);

    // Android's verdict on a key we sent, found by its event time:
    // InputReaderArc uses BridgeInputEvent::timestampNanos as the KeyEvent's
    // eventTime, which is what system_server reports back. Unknown or already
    // acked times are ignored. Called from a binder thread.
    void ackKey(int64_t eventTimeNanos, bool handled);

    // For the binder service; null until a keyboard extension is attached.
    static WaylandKeyboardHandler* instance();

    // zcr_keyboard_device_configuration_v1::type_change
    void onKeyboardTypeChanged(uint32_t type);

    // Re-send the last known keyboard type. Chrome OS reports it once, at
    // connect — which is before InputReaderArc has handed us its pipe, so the
    // first notification is written to a dead bridge and lost. Same reason
    // WaylandInputManager replays DEVICE_ADDED.
    void resendKeyboardType();

    // Wayland event handlers (called from static listeners)
    void onKeymap(uint32_t format, int32_t fd, uint32_t size);
    void onEnter(uint32_t serial, wl_surface* surface, wl_array* keys);
    void onLeave(uint32_t serial, wl_surface* surface);
    void onKey(uint32_t serial, uint32_t time, uint32_t key, uint32_t state);
    void onModifiers(uint32_t serial, uint32_t modsDepressed,
                     uint32_t modsLatched, uint32_t modsLocked, uint32_t group);
    void onRepeatInfo(int32_t rate, int32_t delay);

private:
    // `serial` is the wl_keyboard.key serial, when the key came from Chrome
    // (rather than being synthesized on enter/leave); it is remembered for
    // ackKey() before the event can reach Android.
    void sendKeyEvent(uint32_t scanCode, int32_t action,
                      std::optional<uint32_t> serial = std::nullopt);
    // Send UP for every key Android still holds (see onLeave).
    void releaseHeldKeys();
    int64_t now() const;

    wl_keyboard* mKeyboard = nullptr;
    zcr_keyboard_device_configuration_v1* mDeviceConfig = nullptr;
    bool mHasKeyboardType = false;
    bool mKeyboardPhysical = true;
    InputBridge* mBridge = nullptr;
    wl_surface* mFocusSurface = nullptr;
    WaylandWindow* mFocusWindow = nullptr;
    int32_t mMetaState = 0;
    uint32_t mModsLocked = 0;  // last xkb locked mods: what survives a release-all
    // Scancodes Android has been sent DOWN for and not UP — its idea of which
    // keys are pressed. Reconciled against Chrome's on every enter and emptied
    // on leave, since no key events arrive in between.
    std::vector<uint32_t> mHeldKeys;

    // Protects mFocusSurface/mFocusWindow against concurrent destruction from
    // the SurfaceFlinger thread, which is where clearFocusIfWindow() is called
    // from; everything else here runs on the wayland dispatch thread. Recursive
    // because sendKeyEvent (locked) is called from onEnter (locked). Also held
    // around every mHeldKeys update.
    std::recursive_mutex mFocusMutex;

    zcr_extended_keyboard_v1* mExtendedKeyboard = nullptr;
    wl_display* mDisplay = nullptr;
    struct PendingAck {
        int64_t eventTimeNanos;
        uint32_t serial;
    };
    // Keys sent to Android and not acked yet, oldest first. Guarded by
    // mAckMutex: appended on the wayland thread, consumed on binder threads.
    std::deque<PendingAck> mPendingAcks;
    std::mutex mAckMutex;
    static std::atomic<WaylandKeyboardHandler*> sInstance;
};

} // namespace android

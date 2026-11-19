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

// InputReaderArc: Custom InputReaderInterface that reads BridgeInputEvent
// structs from a pipe (written by SurfaceFlinger's wayland_service) and
// converts them to Android input events for InputDispatcher.

#pragma once

// Stored by value in mDeviceAdded, so the complete type is needed here.
// Resolved via the module's include_dirs (wayland_service/input).
#include <BridgeInputEvent.h>

#include <InputReaderBase.h>
#include <InputListener.h>
#include <InputThread.h>
#include <input/Input.h>
#include <input/InputDevice.h>
#include <input/KeyLayoutMap.h>
#include <utils/Mutex.h>

#include <chrono>
#include <memory>
#include <optional>
#include <atomic>
#include <unordered_map>
#include <unordered_set>

namespace android {

struct BridgeInputEvent;

class InputReaderArc : public InputReaderInterface {
public:
    // AOSP-13: InputListenerInterface is no longer RefBase; passed by reference
    // (QueuedInputListener wraps it as a value member).
    InputReaderArc(const sp<InputReaderPolicyInterface>& policy,
                   InputListenerInterface& listener);
    ~InputReaderArc() override;

    // InputReaderInterface
    void dump(std::string& dump) override;
    void monitor() override;
    bool isInputDeviceEnabled(int32_t deviceId) override;
    status_t start() override;
    status_t stop() override;
    // AOSP-13: getInputDevices returns by value (const).
    std::vector<InputDeviceInfo> getInputDevices() const override;
    int32_t getScanCodeState(int32_t deviceId, uint32_t sourceMask,
                             int32_t scanCode) override;
    int32_t getKeyCodeState(int32_t deviceId, uint32_t sourceMask,
                            int32_t keyCode) override;
    int32_t getSwitchState(int32_t deviceId, uint32_t sourceMask,
                           int32_t sw) override;
    int32_t getKeyCodeForKeyLocation(int32_t deviceId,
                                     int32_t locationKeyCode) const override;
    void toggleCapsLockState(int32_t deviceId) override;
    bool hasKeys(int32_t deviceId, uint32_t sourceMask, size_t numCodes,
                 const int32_t* keyCodes, uint8_t* outFlags) override;
    void requestRefreshConfiguration(uint32_t changes) override;
    // AOSP-13: vibrate takes a VibrationSequence.
    void vibrate(int32_t deviceId, const VibrationSequence& sequence,
                 ssize_t repeat, int32_t token) override;
    void cancelVibrate(int32_t deviceId, int32_t token) override;
    bool isVibrating(int32_t deviceId) override;
    std::vector<int32_t> getVibratorIds(int32_t deviceId) override;
    std::optional<int32_t> getBatteryCapacity(int32_t deviceId) override;
    std::optional<int32_t> getBatteryStatus(int32_t deviceId) override;
    std::vector<InputDeviceLightInfo> getLights(int32_t deviceId) override;
    std::vector<InputDeviceSensorInfo> getSensors(int32_t deviceId) override;
    bool canDispatchToDisplay(int32_t deviceId, int32_t displayId) override;
    // AOSP-13: sensor/light pure virtuals — ARC has none of these devices.
    bool enableSensor(int32_t deviceId, InputDeviceSensorType sensorType,
                      std::chrono::microseconds samplingPeriod,
                      std::chrono::microseconds maxBatchReportLatency) override;
    void disableSensor(int32_t deviceId, InputDeviceSensorType sensorType) override;
    void flushSensor(int32_t deviceId, InputDeviceSensorType sensorType) override;
    bool setLightColor(int32_t deviceId, int32_t lightId, int32_t color) override;
    bool setLightPlayerId(int32_t deviceId, int32_t lightId, int32_t playerId) override;
    std::optional<int32_t> getLightColor(int32_t deviceId, int32_t lightId) override;
    std::optional<int32_t> getLightPlayerId(int32_t deviceId, int32_t lightId) override;

private:
    void loopOnce();
    void tryConnectToWayland();
    // Acknowledge a pointer capture change to the dispatcher and pass the
    // request on to the composer. Reader thread only: it queues on
    // mQueuedListener, which no other thread may touch.
    void processPendingPointerCapture();
    void processPendingKeyboardLayouts();
    // Wake the reader thread out of poll().
    void wake();
    void processBridgeEvent(const BridgeInputEvent& event);
    void processKeyEvent(const BridgeInputEvent& event);
    void processMotionEvent(const BridgeInputEvent& event);
    int32_t mapScanCodeToKeyCode(int32_t scanCode);

    // Re-read the display viewports from the reader configuration
    // (rotation + logical size, used for logical→physical input mapping).
    void refreshDisplayViewport();
    // Map a point on the given display from logical (rotated) display space
    // to the physical (panel-native) space InputDispatcher expects on 13+.
    void mapToPhysicalLocked(int32_t displayId, float& x, float& y) const
            REQUIRES(mLock);

    // Size of the internal display in the PHYSICAL (panel-native) space that
    // pointer coordinates are reported in, so it is what motion ranges must
    // describe. The viewport's deviceWidth/deviceHeight are logical (rotated),
    // hence the swap at 90/270. Zero when no viewport is known yet.
    struct DisplayBounds {
        float width = 0.0f;
        float height = 0.0f;
        bool operator==(const DisplayBounds& o) const {
            return width == o.width && height == o.height;
        }
    };
    DisplayBounds physicalDisplayBoundsLocked() const REQUIRES(mLock);

    // Device list maintenance. The device map is the source of truth for
    // getInputDevices(); mutations are followed by a policy notification.
    void addVirtualKeyboardLocked() REQUIRES(mLock);
    void handleDeviceAdded(const BridgeInputEvent& event);
    void handleKeyboardTypeChanged(const BridgeInputEvent& event);
    void handleDeviceRemoved(const BridgeInputEvent& event);
    // `added` is the DEVICE_ADDED event that announced the device, or null when
    // re-describing one already known (the keyboard type change). Gamepads take
    // their name, USB identity and axis ranges from it. `bounds` sizes the
    // pointer axes; pass the current physical display size.
    // `controllerNumber` is the pad's player slot (0 for anything that is not a
    // gamepad); it is allocated by the caller because this runs const and the
    // allocation is lock-guarded state.
    bool buildDeviceInfo(int32_t bridgeDeviceType, int32_t gamepadId,
                         const BridgeInputEvent* added,
                         const DisplayBounds& bounds,
                         int32_t controllerNumber,
                         InputDeviceInfo* out) const;
    // Player number: 1-based, lowest free slot first, and handed back when the
    // pad disconnects so the next one reuses it -- what stock does in
    // EventHub::getNextControllerNumberLocked/releaseControllerNumberLocked.
    // Keyed by device id, and idempotent, so re-describing a device on a
    // geometry change keeps the number it already has.
    int32_t acquireControllerNumberLocked(int32_t deviceId) REQUIRES(mLock);
    void releaseControllerNumberLocked(int32_t deviceId) REQUIRES(mLock);
    // Motion ranges for the axes a given device actually emits. Advertising an
    // axis that is never populated is worse than advertising none, so this
    // mirrors exactly what processMotionEvent() sets.
    void addMotionRangesFor(int32_t bridgeDeviceType,
                            const DisplayBounds& bounds,
                            InputDeviceInfo* out) const;
    // Re-describe every known device (used when the display rotates or
    // resizes, which changes the pointer axis ranges). Returns true if any
    // description changed. Caller notifies outside the lock.
    bool rebuildDeviceInfosLocked() REQUIRES(mLock);
    int32_t androidDeviceIdFor(int32_t bridgeDeviceType, int32_t gamepadId) const;
    void notifyDevicesChanged();

    sp<InputReaderPolicyInterface> mPolicy;
    // AOSP-13: QueuedInputListener is a plain class (not RefBase) wrapping the
    // next listener by reference — hold it as a value member.
    QueuedInputListener mQueuedListener;
    std::unique_ptr<InputThread> mThread;

    int mPipeReadFd = -1;
    int mPipeWriteFd = -1;   // write end, sent to SF via binder then closed
    int mWakeEventFd = -1;

    // mutable: locked in the const getInputDevices() override (AOSP-13).
    mutable Mutex mLock;
    IdGenerator mIdGenerator;

    // Key state tracking
    std::unordered_set<int32_t> mPressedScanCodes GUARDED_BY(mLock);
    std::unordered_set<int32_t> mPressedKeyCodes GUARDED_BY(mLock);
    nsecs_t mKeyDownTime GUARDED_BY(mLock) = 0;

    // Mouse/touch state tracking
    int32_t mButtonState GUARDED_BY(mLock) = 0;
    float mCursorX GUARDED_BY(mLock) = 0;
    float mCursorY GUARDED_BY(mLock) = 0;
    nsecs_t mMotionDownTime GUARDED_BY(mLock) = 0;

    // Reader-configuration snapshot — holds every display's viewport
    // (rotation + logical size, looked up per event by displayId). The
    // wayland bridge delivers coordinates in logical display space, but
    // the AOSP-13 dispatcher expects physical-space input (each window
    // carries a physical→window transform from WM). Kept in sync with WM
    // via requestRefreshConfiguration(CHANGE_DISPLAY_INFO).
    InputReaderConfiguration mConfig GUARDED_BY(mLock);

    // Pointer capture. The dispatcher does not hand a window capture until
    // the reader acknowledges the config change with a NotifyPointerCapture-
    // ChangedArgs carrying the same sequence number -- stock does this from
    // InputReader::refreshConfigurationLocked. requestRefreshConfiguration()
    // runs on the dispatcher's thread, so it only parks the request here and
    // wakes the reader thread to send the acknowledgement.
    PointerCaptureRequest mPendingPointerCaptureRequest GUARDED_BY(mLock);
    bool mHasPendingPointerCapture GUARDED_BY(mLock) = false;
    PointerCaptureRequest mCurrentPointerCaptureRequest GUARDED_BY(mLock);

    // Key layout for scancode -> keycode mapping.
    // AOSP-13: KeyLayoutMap is held via std::shared_ptr (no longer RefBase).
    std::shared_ptr<KeyLayoutMap> mKeyLayout;

    // Dynamic device list: keyed by Android device id. Built from
    // BRIDGE_EVENT_DEVICE_ADDED/REMOVED events sent by wayland_service
    // when wl_seat capabilities are observed (pointer/keyboard/touch),
    // when stylus / relative-pointer / gaming-input globals bind, and as
    // gamepads connect/disconnect. The Virtual keyboard (id = -1) is the
    // only fixed entry — stock AOSP's EventHub always provides it and
    // KeyCharacterMap.load(VIRTUAL_KEYBOARD) falls back to it.
    std::unordered_map<int32_t, InputDeviceInfo> mDevices GUARDED_BY(mLock);

    // The DEVICE_ADDED event that announced each device, kept so a device can
    // be described again from the original source of truth when the display
    // geometry changes. Without it a rebuild would lose everything the event
    // carried that InputDeviceInfo does not round-trip (a gamepad's axis
    // ranges, in particular).
    std::unordered_map<int32_t, BridgeInputEvent> mDeviceAdded GUARDED_BY(mLock);

    // Device id -> player number currently held by that device.
    std::unordered_map<int32_t, int32_t> mControllerNumbers GUARDED_BY(mLock);
    // Display size the current descriptions were built against, so a viewport
    // refresh that does not change it does no work.
    DisplayBounds mDescribedBounds GUARDED_BY(mLock);

    // Whether Chrome OS says a PHYSICAL keyboard is in use
    // (zcr_keyboard_device_configuration_v1::type_change). The protocol says
    // to assume physical until told otherwise, which is also the historical
    // fydroid behaviour.
    //
    // Atomic rather than lock-guarded: buildDeviceInfo() reads it and is called
    // both with and without mLock held.
    std::atomic<bool> mKeyboardIsPhysical{true};

    // The user picked a different physical keyboard layout in Android
    // Settings (CHANGE_KEYBOARD_LAYOUTS). Parked by requestRefreshConfiguration
    // and applied on the reader thread, like stock InputReader.
    std::atomic<bool> mPendingKeyboardLayouts{false};

    // Every description gets a fresh generation. Java's InputManager only
    // re-reads an InputDevice -- and with it the KeyCharacterMap apps type
    // with -- when the generation changes, so a constant one hid every
    // re-description from apps. Atomic: buildDeviceInfo() is const and runs
    // with and without mLock.
    mutable std::atomic<int32_t> mNextGeneration{1};

    // Virtual device IDs
    static constexpr int32_t DEVICE_ID_KEYBOARD = 1;
    static constexpr int32_t DEVICE_ID_MOUSE = 2;
    static constexpr int32_t DEVICE_ID_TOUCHSCREEN = 3;
    static constexpr int32_t DEVICE_ID_STYLUS = 4;
    static constexpr int32_t DEVICE_ID_MOUSE_RELATIVE = 5;
    static constexpr int32_t DEVICE_ID_GAMEPAD_BASE = 10;  // 10, 11, 12, ... per gamepad
};

} // namespace android

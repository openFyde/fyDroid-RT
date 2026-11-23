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

// InputReaderArc: Reads BridgeInputEvent structs from a pipe and converts
// them to Android NotifyKeyArgs/NotifyMotionArgs for InputDispatcher.
//
// Architecture:
//   SurfaceFlinger (wayland_service) writes BridgeInputEvent to pipe
//   -> InputReaderArc reads from pipe, converts, dispatches
//   -> InputClassifier -> InputDispatcher -> Apps
//
// The pipe write fd is sent to SurfaceFlinger's Wayland binder service
// via transaction 29 (notifyInputBridgePipeOpening).

#define LOG_TAG "InputReaderArc"

#include "InputReaderArc.h"
#include "WaylandServiceProxy.h"
#include "BridgeInputEvent.h"

#include <android/input.h>
#include <android/keycodes.h>
#include <input/InputDevice.h>
#include <input/KeyLayoutMap.h>
#include <input/Keyboard.h>
#include <log/log.h>
#include <utils/Timers.h>

#include <openssl/sha.h>

#include <cmath>
#include <cstring>
#include <fcntl.h>
#include <poll.h>
#include <sys/eventfd.h>
#include <unistd.h>

namespace android {

// InputDevice.getDescriptor() -- the identity Android documents as stable
// across reboots and reconnections, and the key an engine matches its
// controller profiles on. Stock builds it in EventHub::generateDescriptor()
// and every device gets one; nothing here ever assigned it, so every fydroid
// device reported an empty descriptor and a known pad could not be told from
// any other device. Same recipe as stock, so a pad hashes to the same
// descriptor it would have on any other Android machine.
static std::string sha1Hex(const std::string& in) {
    SHA_CTX ctx;
    SHA1_Init(&ctx);
    SHA1_Update(&ctx, reinterpret_cast<const unsigned char*>(in.c_str()), in.size());
    unsigned char digest[SHA_DIGEST_LENGTH];
    SHA1_Final(digest, &ctx);

    std::string out;
    char byte[3];
    for (size_t i = 0; i < SHA_DIGEST_LENGTH; i++) {
        snprintf(byte, sizeof(byte), "%02x", digest[i]);
        out += byte;
    }
    return out;
}

static void assignDescriptor(InputDeviceIdentifier& identifier) {
    char head[32];
    snprintf(head, sizeof(head), ":%04x:%04x:", identifier.vendor, identifier.product);
    std::string raw = head;
    if (!identifier.uniqueId.empty()) {
        raw += "uniqueId:";
        raw += identifier.uniqueId;
    }
    // Stock only falls back to the name when the USB ids say nothing, which is
    // what keeps a real pad's descriptor tied to its VID:PID alone.
    if (identifier.vendor == 0 && identifier.product == 0 && !identifier.name.empty()) {
        raw += "name:";
        raw += identifier.name;
    }
    identifier.descriptor = sha1Hex(raw);
}

InputReaderArc::InputReaderArc(const sp<InputReaderPolicyInterface>& policy,
                               InputListenerInterface& listener)
    : mPolicy(policy),
      mQueuedListener(listener),
      mIdGenerator(IdGenerator::Source::INPUT_READER) {
    // AOSP-13: KeyLayoutMap::load returns base::Result<std::shared_ptr<KeyLayoutMap>>.
    auto ret = KeyLayoutMap::load(std::string("/system/usr/keylayout/Generic.kl"));
    if (ret.ok()) {
        mKeyLayout = *ret;
        ALOGI("Loaded Generic.kl for key mapping");
    } else {
        ALOGW("Failed to load Generic.kl, key mapping will be limited");
    }
}

InputReaderArc::~InputReaderArc() {
    if (mPipeReadFd >= 0) close(mPipeReadFd);
    if (mPipeWriteFd >= 0) close(mPipeWriteFd);
    if (mWakeEventFd >= 0) close(mWakeEventFd);
}

status_t InputReaderArc::start() {
    ALOGI("InputReaderArc starting");

    // Create wake eventfd for thread shutdown signaling
    mWakeEventFd = eventfd(0, EFD_NONBLOCK | EFD_CLOEXEC);
    if (mWakeEventFd < 0) {
        ALOGE("Failed to create wake eventfd: %s", strerror(errno));
        return -errno;
    }

    // Create input bridge pipe
    int pipeFds[2];
    if (pipe2(pipeFds, O_CLOEXEC) < 0) {
        ALOGE("Failed to create pipe: %s", strerror(errno));
        close(mWakeEventFd);
        mWakeEventFd = -1;
        return -errno;
    }
    mPipeReadFd = pipeFds[0];
    mPipeWriteFd = pipeFds[1];

    // Make read end non-blocking so we can drain all pending events
    int flags = fcntl(mPipeReadFd, F_GETFL);
    fcntl(mPipeReadFd, F_SETFL, flags | O_NONBLOCK);

    ALOGI("Pipe created, read_fd=%d write_fd=%d", mPipeReadFd, mPipeWriteFd);

    // Start reader thread
    mThread = std::make_unique<InputThread>(
        "InputReaderArc",
        [this]() { loopOnce(); },
        [this]() {
            uint64_t val = 1;
            write(mWakeEventFd, &val, sizeof(val));
        });

    // Seed the device list with the always-present Virtual keyboard
    // (id = -1). All other devices arrive dynamically via
    // BRIDGE_EVENT_DEVICE_ADDED from wayland_service.
    {
        AutoMutex _l(mLock);
        addVirtualKeyboardLocked();
    }
    notifyDevicesChanged();

    // Pick up the internal display viewport if it's already configured
    // (later changes arrive via requestRefreshConfiguration).
    refreshDisplayViewport();

    return OK;
}

status_t InputReaderArc::stop() {
    ALOGI("InputReaderArc stopping");
    mThread.reset();  // InputThread destructor calls wake + join

    if (mPipeReadFd >= 0) {
        close(mPipeReadFd);
        mPipeReadFd = -1;
    }
    if (mPipeWriteFd >= 0) {
        close(mPipeWriteFd);
        mPipeWriteFd = -1;
    }
    if (mWakeEventFd >= 0) {
        close(mWakeEventFd);
        mWakeEventFd = -1;
    }

    return OK;
}

void InputReaderArc::loopOnce() {
    // If we still hold the write fd, we haven't connected to SF yet — retry
    if (mPipeWriteFd >= 0) {
        tryConnectToWayland();
    }

    struct pollfd fds[2];
    fds[0].fd = mPipeReadFd;
    fds[0].events = POLLIN;
    fds[1].fd = mWakeEventFd;
    fds[1].events = POLLIN;

    // Use timeout for connection retry if not yet connected
    int timeout = (mPipeWriteFd >= 0) ? 1000 : -1;

    int ret = poll(fds, 2, timeout);
    if (ret < 0) {
        if (errno != EINTR) {
            ALOGE("poll failed: %s", strerror(errno));
        }
        return;
    }
    // Drain wake eventfd
    if (ret > 0 && (fds[1].revents & POLLIN)) {
        uint64_t val;
        read(mWakeEventFd, &val, sizeof(val));
    }

    // Before the pipe, and on the timeout path too: a capture change parked by
    // the dispatcher's thread is only ever sent from here.
    processPendingPointerCapture();
    processPendingKeyboardLayouts();

    if (ret == 0) return;  // timeout — will retry connection next loop

    // Read and process all pending events from pipe
    if (fds[0].revents & POLLIN) {
        BridgeInputEvent event;
        while (true) {
            ssize_t n = read(mPipeReadFd, &event, sizeof(event));
            if (n < 0) {
                if (errno == EAGAIN || errno == EWOULDBLOCK) break;
                ALOGE("Pipe read error: %s", strerror(errno));
                break;
            }
            if (n == 0) {
                ALOGW("Pipe closed by writer");
                break;
            }
            if (static_cast<size_t>(n) != sizeof(event)) {
                ALOGW("Partial read: %zd/%zu bytes, discarding", n, sizeof(event));
                break;
            }
            processBridgeEvent(event);
        }
        mQueuedListener.flush();
    }
}

void InputReaderArc::tryConnectToWayland() {
    WaylandServiceProxy proxy;
    if (!proxy.connectToWayland()) {
        return;  // Service not up yet, will retry
    }

    // Dup the write fd — binder takes ownership of the passed fd.
    // AOSP-13 clang-tidy (android-cloexec-dup) requires F_DUPFD_CLOEXEC over dup().
    android::base::unique_fd dupFd(fcntl(mPipeWriteFd, F_DUPFD_CLOEXEC, 0));
    if (dupFd.get() < 0) {
        ALOGE("Failed to dup pipe write fd: %s", strerror(errno));
        return;
    }

    if (proxy.notifyInputBridgePipeOpening(std::move(dupFd))) {
        ALOGI("Connected to Wayland binder service, sent pipe write fd");
        // Close our copy — SurfaceFlinger now owns the write end
        close(mPipeWriteFd);
        mPipeWriteFd = -1;
    } else {
        ALOGE("Failed to send pipe fd to Wayland service");
    }
}

void InputReaderArc::processBridgeEvent(const BridgeInputEvent& event) {
    switch (event.type) {
        case BRIDGE_EVENT_KEY:
            processKeyEvent(event);
            break;
        case BRIDGE_EVENT_MOTION:
            processMotionEvent(event);
            break;
        case BRIDGE_EVENT_DEVICE_ADDED:
            handleDeviceAdded(event);
            break;
        case BRIDGE_EVENT_DEVICE_REMOVED:
            handleDeviceRemoved(event);
            break;
        case BRIDGE_EVENT_KEYBOARD_TYPE:
            handleKeyboardTypeChanged(event);
            break;
        case BRIDGE_EVENT_CYCLED_TIMESTAMP:
            break;  // Keepalive, ignore
        default:
            ALOGW("Unknown bridge event type: %d", event.type);
            break;
    }
}

void InputReaderArc::processKeyEvent(const BridgeInputEvent& event) {
    AutoMutex _l(mLock);

    int32_t deviceId;
    uint32_t source;
    int32_t keyCode;

    if (event.deviceType == BRIDGE_DEVICE_GAMEPAD) {
        // Gamepad buttons: keyCode is already mapped by WaylandGamepadHandler
        deviceId = DEVICE_ID_GAMEPAD_BASE + event.gamepadId;
        source = AINPUT_SOURCE_GAMEPAD;
        keyCode = event.keyCode;
    } else {
        // Keyboard
        deviceId = DEVICE_ID_KEYBOARD;
        source = AINPUT_SOURCE_KEYBOARD;
        keyCode = mapScanCodeToKeyCode(event.scanCode);
    }

    // Track key down time and pressed state
    if (event.action == AKEY_EVENT_ACTION_DOWN) {
        if (mPressedScanCodes.empty()) {
            mKeyDownTime = event.timestampNanos;
        }
        mPressedScanCodes.insert(event.scanCode);
        mPressedKeyCodes.insert(keyCode);
    } else {
        mPressedScanCodes.erase(event.scanCode);
        mPressedKeyCodes.erase(keyCode);
    }

    ALOGV("Key event: scanCode=%d keyCode=%d action=%d meta=0x%x device=%d",
          event.scanCode, keyCode, event.action, event.metaState, deviceId);

    // AOSP-13: NotifyKeyArgs gained readTime (after eventTime); use the same ts.
    NotifyKeyArgs args(mIdGenerator.nextId(), event.timestampNanos,
                       event.timestampNanos /* readTime */,
                       deviceId,
                       source,
                       event.displayId,
                       POLICY_FLAG_TRUSTED | POLICY_FLAG_PASS_TO_USER,
                       event.action,
                       event.flags | AKEY_EVENT_FLAG_FROM_SYSTEM,
                       keyCode,
                       event.scanCode,
                       event.metaState,
                       mKeyDownTime);
    mQueuedListener.notifyKey(&args);
}

// Map Wayland stylus tool_type to Android AMOTION_EVENT_TOOL_TYPE_*
static int32_t mapStylusToolType(int32_t waylandToolType) {
    switch (waylandToolType) {
        case 2:  return AMOTION_EVENT_TOOL_TYPE_STYLUS;  // pen
        case 3:  return AMOTION_EVENT_TOOL_TYPE_ERASER;  // eraser
        case 1:  return AMOTION_EVENT_TOOL_TYPE_FINGER;  // touch
        default: return AMOTION_EVENT_TOOL_TYPE_STYLUS;
    }
}

// Convert Wayland tiltX/tiltY (degrees, [-90,90]) to Android AXIS_TILT (radians)
// and AXIS_ORIENTATION (radians).
static void convertTilt(float tiltXDeg, float tiltYDeg,
                        float& outTilt, float& outOrientation) {
    static constexpr float kDegToRad = M_PI / 180.0f;
    float tx = tiltXDeg * kDegToRad;
    float ty = tiltYDeg * kDegToRad;
    // AXIS_TILT = acos(cos(tx) * cos(ty)) — magnitude of tilt from vertical
    float cosTilt = cosf(tx) * cosf(ty);
    cosTilt = std::max(-1.0f, std::min(1.0f, cosTilt));  // clamp for acos
    outTilt = acosf(cosTilt);
    // AXIS_ORIENTATION = atan2(-tiltX, tiltY) — direction of tilt
    outOrientation = atan2f(-tx, ty);
}

// W3C Standard Gamepad axis index → Android AMOTION_EVENT_AXIS_*
static int32_t gamepadAxisToAndroid(int index) {
    switch (index) {
        case 0: return AMOTION_EVENT_AXIS_X;
        case 1: return AMOTION_EVENT_AXIS_Y;
        case 2: return AMOTION_EVENT_AXIS_Z;
        case 3: return AMOTION_EVENT_AXIS_RZ;
        case 4: return AMOTION_EVENT_AXIS_LTRIGGER;
        case 5: return AMOTION_EVENT_AXIS_RTRIGGER;
        case 6: return AMOTION_EVENT_AXIS_HAT_X;
        case 7: return AMOTION_EVENT_AXIS_HAT_Y;
        default: return -1;
    }
}

void InputReaderArc::processMotionEvent(const BridgeInputEvent& event) {
    AutoMutex _l(mLock);

    int32_t deviceId;
    uint32_t source;
    int32_t toolType;
    float cursorX = event.cursorX;
    float cursorY = event.cursorY;

    switch (event.deviceType) {
        case BRIDGE_DEVICE_TOUCHSCREEN:
            deviceId = DEVICE_ID_TOUCHSCREEN;
            source = AINPUT_SOURCE_TOUCHSCREEN;
            toolType = AMOTION_EVENT_TOOL_TYPE_FINGER;
            break;
        case BRIDGE_DEVICE_STYLUS:
            // On-screen stylus (e.g. Fydetab digitizer): use touchscreen device/source
            // so Android treats it like a normal touch for focus/dispatch purposes,
            // but with TOOL_TYPE_STYLUS so apps get pressure/tilt.
            deviceId = DEVICE_ID_TOUCHSCREEN;
            source = AINPUT_SOURCE_TOUCHSCREEN;
            toolType = mapStylusToolType(event.toolType);
            break;
        case BRIDGE_DEVICE_MOUSE_RELATIVE:
            deviceId = DEVICE_ID_MOUSE_RELATIVE;
            source = AINPUT_SOURCE_MOUSE_RELATIVE;
            toolType = AMOTION_EVENT_TOOL_TYPE_MOUSE;
            cursorX = INFINITY;
            cursorY = INFINITY;
            break;
        case BRIDGE_DEVICE_GAMEPAD:
            deviceId = DEVICE_ID_GAMEPAD_BASE + event.gamepadId;
            source = AINPUT_SOURCE_JOYSTICK | AINPUT_SOURCE_GAMEPAD;
            toolType = AMOTION_EVENT_TOOL_TYPE_UNKNOWN;
            cursorX = INFINITY;
            cursorY = INFINITY;
            break;
        default:
            deviceId = DEVICE_ID_MOUSE;
            source = AINPUT_SOURCE_MOUSE;
            toolType = AMOTION_EVENT_TOOL_TYPE_MOUSE;
            break;
    }

    // Track button and cursor state (for mouse/stylus)
    mButtonState = event.buttonState;
    if (event.deviceType == BRIDGE_DEVICE_MOUSE ||
        event.deviceType == BRIDGE_DEVICE_STYLUS) {
        mCursorX = event.cursorX;
        mCursorY = event.cursorY;
    }

    // Track down time for gesture start
    int32_t maskedAction = event.action & AMOTION_EVENT_ACTION_MASK;
    if (maskedAction == AMOTION_EVENT_ACTION_DOWN ||
        maskedAction == AMOTION_EVENT_ACTION_HOVER_ENTER) {
        mMotionDownTime = event.timestampNanos;
    }

    uint32_t pointerCount = event.pointerCount;
    if (pointerCount == 0) pointerCount = 1;
    if (pointerCount > MAX_POINTERS) pointerCount = MAX_POINTERS;

    // The bridge delivers absolute coordinates in LOGICAL display space
    // (Chrome's rotated desktop space, which the per-task window bounds
    // live in). The AOSP-13 dispatcher hit-tests and transforms in
    // PHYSICAL (panel-native) space — map before dispatch, using the
    // target display's viewport (identity for unrotated displays).
    const bool mapToPhysical =
            event.deviceType != BRIDGE_DEVICE_MOUSE_RELATIVE &&
            event.deviceType != BRIDGE_DEVICE_GAMEPAD;

    PointerProperties props[MAX_POINTERS];
    PointerCoords coords[MAX_POINTERS];

    for (uint32_t i = 0; i < pointerCount; i++) {
        props[i].clear();
        props[i].id = event.pointers[i].id;
        props[i].toolType = toolType;

        float px = event.pointers[i].x;
        float py = event.pointers[i].y;
        if (mapToPhysical) mapToPhysicalLocked(event.displayId, px, py);

        coords[i].clear();
        coords[i].setAxisValue(AMOTION_EVENT_AXIS_X, px);
        coords[i].setAxisValue(AMOTION_EVENT_AXIS_Y, py);
        coords[i].setAxisValue(AMOTION_EVENT_AXIS_PRESSURE, event.pointers[i].pressure);
    }
    if (mapToPhysical && cursorX != INFINITY) {
        mapToPhysicalLocked(event.displayId, cursorX, cursorY);
    }

    // Stylus: add tilt and orientation axes
    if (event.deviceType == BRIDGE_DEVICE_STYLUS &&
        (event.tiltX != 0.0f || event.tiltY != 0.0f)) {
        float tilt, orientation;
        convertTilt(event.tiltX, event.tiltY, tilt, orientation);
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_TILT, tilt);
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_ORIENTATION, orientation);
    }

    // Relative pointer: set relative X/Y axes. The delta is delivered EXACTLY
    // as the compositor sent it -- in logical (Chrome desktop) space, which is
    // also the app's own view space, so it is already what the app wants.
    //
    // It must not be rotated into physical space the way absolute coordinates
    // are. Absolute coordinates need that because the dispatcher hit-tests in
    // physical space and the window's physical->window transform undoes the
    // rotation on delivery; a relative-mouse event gets no such transform:
    // Input.cpp's shouldDisregardTransformation() returns true for
    // AINPUT_SOURCE_MOUSE_RELATIVE, so every axis reaches the app untouched.
    // Rotating here therefore never cancels -- on a 90-degree display it turns
    // "mouse right" into "camera up" and the view moves along the wrong axis.
    if (event.deviceType == BRIDGE_DEVICE_MOUSE_RELATIVE) {
        const float rdx = event.relativeX;
        const float rdy = event.relativeY;
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_RELATIVE_X, rdx);
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_RELATIVE_Y, rdy);
        // In capture mode stock's CursorInputMapper puts the delta in X and Y
        // as well, and that is what a game reads: getX()/getY() on a captured
        // event is the movement, not a position. Leaving them at the zero the
        // bridge sent is a mouse that reports every frame and never moves.
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_X, rdx);
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_Y, rdy);
    }

    // Gamepad: set joystick axes
    if (event.deviceType == BRIDGE_DEVICE_GAMEPAD) {
        for (int i = 0; i < 8; i++) {
            int32_t axis = gamepadAxisToAndroid(i);
            if (axis >= 0) {
                coords[0].setAxisValue(axis, event.gamepadAxisValues[i]);
            }
        }
    }

    // For scroll events, pressure and actionButton are repurposed to carry
    // VSCROLL and HSCROLL values from WaylandPointerHandler
    int32_t actionButton = event.actionButton;
    if (maskedAction == AMOTION_EVENT_ACTION_SCROLL) {
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_VSCROLL, event.pointers[0].pressure);
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_HSCROLL, static_cast<float>(event.actionButton));
        coords[0].setAxisValue(AMOTION_EVENT_AXIS_PRESSURE, 0.0f);
        actionButton = 0;
    }

    if (event.deviceType == BRIDGE_DEVICE_GAMEPAD) {
        // Axes are the whole payload for a gamepad, and they are normalised on
        // the way in, so log them rather than the meaningless x/y pointer pair.
        ALOGV("Motion: dev=%d src=0x%x gamepad axes "
              "x=%.3f y=%.3f z=%.3f rz=%.3f lt=%.3f rt=%.3f hatx=%.1f haty=%.1f",
              deviceId, source,
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_X),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_Y),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_Z),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_RZ),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_LTRIGGER),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_RTRIGGER),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_HAT_X),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_HAT_Y));
    } else {
        ALOGV("Motion: dev=%d src=0x%x action=0x%x display=%d ptrs=%u "
              "x=%.1f y=%.1f cursor=(%.1f,%.1f) tool=%d btn=0x%x",
              deviceId, source, event.action, event.displayId, pointerCount,
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_X),
              coords[0].getAxisValue(AMOTION_EVENT_AXIS_Y),
              cursorX, cursorY, toolType, event.buttonState);
    }

    // AOSP-13: NotifyMotionArgs gained readTime (after eventTime); use the same ts.
    NotifyMotionArgs args(mIdGenerator.nextId(), event.timestampNanos,
                          event.timestampNanos /* readTime */,
                          deviceId,
                          source,
                          event.displayId,
                          POLICY_FLAG_TRUSTED | POLICY_FLAG_PASS_TO_USER,
                          event.action,
                          actionButton,
                          event.flags,
                          event.metaState,
                          event.buttonState,
                          MotionClassification::NONE,
                          AMOTION_EVENT_EDGE_FLAG_NONE,
                          pointerCount,
                          props, coords,
                          0 /*xPrecision*/, 0 /*yPrecision*/,
                          cursorX, cursorY,
                          mMotionDownTime,
                          {} /*videoFrames*/);
    mQueuedListener.notifyMotion(&args);
}

int32_t InputReaderArc::mapScanCodeToKeyCode(int32_t scanCode) {
    if (mKeyLayout) {
        int32_t keyCode;
        uint32_t flags;
        if (mKeyLayout->mapKey(scanCode, 0 /*usageCode*/, &keyCode, &flags) == OK) {
            return keyCode;
        }
    }
    return AKEYCODE_UNKNOWN;
}

// --- InputReaderInterface query methods ---

void InputReaderArc::dump(std::string& dump) {
    AutoMutex _l(mLock);
    dump += "InputReaderArc State:\n";
    dump += "  Pipe read fd: " + std::to_string(mPipeReadFd) + "\n";
    dump += "  Connected to SF: " + std::string(mPipeWriteFd < 0 ? "yes" : "no") + "\n";

    // The devices registered with InputManagerService. AOSP's InputReader
    // prints this and things downstream depend on it — Configuration's
    // keyboard/touchscreen fields are derived from the registered set, and apps
    // enumerate it through InputDevice. Printing nothing made "dumpsys input"
    // look like there were no input devices at all, which is indistinguishable
    // from actually having none.
    dump += "  Input Devices: " + std::to_string(mDevices.size()) + "\n";
    for (const auto& [id, info] : mDevices) {
        char line[320];
        const InputDeviceIdentifier& ident = info.getIdentifier();
        snprintf(line, sizeof(line),
                 "    id=%d name='%s' sources=0x%08x keyboardType=%d external=%d"
                 " vendor=0x%04x product=0x%04x controller=%d descriptor=%s\n",
                 id, ident.name.c_str(), info.getSources(),
                 info.getKeyboardType(), info.isExternal() ? 1 : 0,
                 ident.vendor, ident.product, info.getControllerNumber(),
                 ident.descriptor.empty() ? "<none>" : ident.descriptor.c_str());
        dump += line;
        // Joystick axes. An app that finds no motion range for an axis treats
        // it as absent, so this is the difference between a gamepad Android
        // can actually use and one that only looks present.
        for (const InputDeviceInfo::MotionRange& range : info.getMotionRanges()) {
            snprintf(line, sizeof(line),
                     "      axis=%d source=0x%08x range=[%.2f, %.2f] flat=%.3f fuzz=%.3f\n",
                     range.axis, range.source, range.min, range.max,
                     range.flat, range.fuzz);
            dump += line;
        }
    }
    dump += "  Pointer capture: " +
            std::string(mCurrentPointerCaptureRequest.enable ? "enabled" : "disabled") +
            ", seq=" + std::to_string(mCurrentPointerCaptureRequest.seq) + "\n";
    dump += "  Pressed keys: " + std::to_string(mPressedScanCodes.size()) + "\n";
    dump += "  Button state: " + std::to_string(mButtonState) + "\n";
    dump += "  Cursor: (" + std::to_string(mCursorX) + ", "
            + std::to_string(mCursorY) + ")\n";
    std::optional<DisplayViewport> internal =
            mConfig.getDisplayViewportByType(ViewportType::INTERNAL);
    if (internal) {
        dump += "  Internal viewport: displayId=" + std::to_string(internal->displayId)
                + " orientation=" + std::to_string(internal->orientation)
                + " logical=" + std::to_string(internal->deviceWidth) + "x"
                + std::to_string(internal->deviceHeight) + "\n";
    } else {
        dump += "  Internal viewport: <none>\n";
    }
}

void InputReaderArc::monitor() {
    AutoMutex _l(mLock);
}

bool InputReaderArc::isInputDeviceEnabled(int32_t /*deviceId*/) {
    return true;
}

std::vector<InputDeviceInfo> InputReaderArc::getInputDevices() const {
    AutoMutex _l(mLock);
    std::vector<InputDeviceInfo> out;
    out.reserve(mDevices.size());
    for (const auto& [id, info] : mDevices) {
        (void)id;
        out.push_back(info);
    }
    return out;
}

void InputReaderArc::addVirtualKeyboardLocked() {
    // Stock AOSP's EventHub::createVirtualKeyboardLocked() unconditionally
    // registers id = VIRTUAL_KEYBOARD_ID (-1), and KeyCharacterMap.load(-1)
    // falls back to it whenever the source deviceId isn't found. Without
    // it, callers like WebView's ImeAdapterImpl —
    // KeyEvent.getUnicodeChar() → KeyCharacterMap.load(VIRTUAL_KEYBOARD) —
    // raise UnavailableException and Chromium's JNI bridge FATAL-aborts
    // the process. Naming the identifier "Virtual" makes KeyMap::load()
    // pick up the system Virtual.kcm.
    InputDeviceIdentifier ident;
    ident.name = "Virtual";
    ident.uniqueId = "<virtual>";
    assignDescriptor(ident);
    InputDeviceInfo info;
    info.initialize(ReservedInputDeviceId::VIRTUAL_KEYBOARD_ID,
                    1 /*generation*/, 0 /*controllerNumber*/,
                    ident, "Virtual",
                    false /*isExternal*/, false /*hasMic*/);
    info.addSource(AINPUT_SOURCE_KEYBOARD);
    info.setKeyboardType(AINPUT_KEYBOARD_TYPE_ALPHABETIC);
    KeyMap keyMap;
    if (keyMap.load(ident, nullptr) == OK) {
        info.setKeyCharacterMap(keyMap.keyCharacterMap);
    }
    mDevices[ReservedInputDeviceId::VIRTUAL_KEYBOARD_ID] = std::move(info);
}

int32_t InputReaderArc::androidDeviceIdFor(int32_t bridgeDeviceType,
                                            int32_t gamepadId) const {
    switch (bridgeDeviceType) {
        case BRIDGE_DEVICE_MOUSE:           return DEVICE_ID_MOUSE;
        case BRIDGE_DEVICE_TOUCHSCREEN:     return DEVICE_ID_TOUCHSCREEN;
        case BRIDGE_DEVICE_KEYBOARD:        return DEVICE_ID_KEYBOARD;
        case BRIDGE_DEVICE_STYLUS:          return DEVICE_ID_STYLUS;
        case BRIDGE_DEVICE_MOUSE_RELATIVE:  return DEVICE_ID_MOUSE_RELATIVE;
        case BRIDGE_DEVICE_GAMEPAD:         return DEVICE_ID_GAMEPAD_BASE + gamepadId;
        default:                            return -1;
    }
}

InputReaderArc::DisplayBounds InputReaderArc::physicalDisplayBoundsLocked() const {
    DisplayBounds bounds;
    std::optional<DisplayViewport> v =
            mConfig.getDisplayViewportByType(ViewportType::INTERNAL);
    if (!v) return bounds;
    // deviceWidth/deviceHeight are the LOGICAL (rotated) size; pointer
    // coordinates are physical, so undo the rotation -- the same swap
    // mapToPhysicalLocked() relies on.
    const bool swapped = v->orientation == DISPLAY_ORIENTATION_90 ||
                         v->orientation == DISPLAY_ORIENTATION_270;
    bounds.width = static_cast<float>(swapped ? v->deviceHeight : v->deviceWidth);
    bounds.height = static_cast<float>(swapped ? v->deviceWidth : v->deviceHeight);
    return bounds;
}

void InputReaderArc::addMotionRangesFor(int32_t bridgeDeviceType,
                                         const DisplayBounds& bounds,
                                         InputDeviceInfo* out) const {
    // Only the axes processMotionEvent() actually populates are advertised. An
    // app that finds a range for an axis assumes the device reports it, so a
    // range for an axis we never set is a lie -- and a missing range makes an
    // axis we do set invisible, which is what this used to be: nothing at all
    // was published, so InputDevice.getMotionRanges() was empty for every
    // device and any app following Android's own sample (which skips an axis
    // whose getMotionRange returns null) saw no pointer or stylus axes.
    const bool haveDisplay = bounds.width > 0.0f && bounds.height > 0.0f;

    auto addPointerAxes = [&](uint32_t source) {
        if (haveDisplay) {
            out->addMotionRange(AMOTION_EVENT_AXIS_X, source,
                                0.0f, bounds.width - 1.0f, 0.0f, 0.0f, 0.0f);
            out->addMotionRange(AMOTION_EVENT_AXIS_Y, source,
                                0.0f, bounds.height - 1.0f, 0.0f, 0.0f, 0.0f);
        }
        out->addMotionRange(AMOTION_EVENT_AXIS_PRESSURE, source,
                            0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
    };
    // Tilt magnitude is acos(cos(tiltX) * cos(tiltY)), so 0..PI/2 for the
    // +-90 degree inputs Wayland gives us; orientation is atan2, so -PI..PI.
    auto addStylusAxes = [&](uint32_t source) {
        out->addMotionRange(AMOTION_EVENT_AXIS_TILT, source,
                            0.0f, static_cast<float>(M_PI_2), 0.0f, 0.0f, 0.0f);
        out->addMotionRange(AMOTION_EVENT_AXIS_ORIENTATION, source,
                            static_cast<float>(-M_PI), static_cast<float>(M_PI),
                            0.0f, 0.0f, 0.0f);
    };

    switch (bridgeDeviceType) {
        case BRIDGE_DEVICE_TOUCHSCREEN:
            addPointerAxes(AINPUT_SOURCE_TOUCHSCREEN);
            // Stylus events are reported on the touchscreen device (see the
            // BRIDGE_DEVICE_STYLUS case in processMotionEvent), so the tilt
            // axes belong here too.
            addStylusAxes(AINPUT_SOURCE_TOUCHSCREEN);
            break;
        case BRIDGE_DEVICE_STYLUS:
            addPointerAxes(AINPUT_SOURCE_STYLUS);
            addStylusAxes(AINPUT_SOURCE_STYLUS);
            break;
        case BRIDGE_DEVICE_MOUSE:
            addPointerAxes(AINPUT_SOURCE_MOUSE);
            // Scroll is delivered as ACTION_SCROLL on this device. AOSP's
            // CursorInputMapper publishes the same -1..1 for a wheel.
            out->addMotionRange(AMOTION_EVENT_AXIS_VSCROLL, AINPUT_SOURCE_MOUSE,
                                -1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
            out->addMotionRange(AMOTION_EVENT_AXIS_HSCROLL, AINPUT_SOURCE_MOUSE,
                                -1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
            break;
        case BRIDGE_DEVICE_MOUSE_RELATIVE:
            // No absolute position: the cursor is reported as INFINITY and X/Y
            // carry the same delta as the relative axes, which is the shape
            // stock's CursorInputMapper reports in capture mode. A delta
            // cannot usefully exceed the screen, which is the bound AOSP uses
            // for relative axes as well.
            out->addMotionRange(AMOTION_EVENT_AXIS_PRESSURE,
                                AINPUT_SOURCE_MOUSE_RELATIVE,
                                0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
            if (haveDisplay) {
                for (int32_t axis : {AMOTION_EVENT_AXIS_X,
                                     AMOTION_EVENT_AXIS_RELATIVE_X}) {
                    out->addMotionRange(axis, AINPUT_SOURCE_MOUSE_RELATIVE,
                                        -bounds.width, bounds.width,
                                        0.0f, 0.0f, 0.0f);
                }
                for (int32_t axis : {AMOTION_EVENT_AXIS_Y,
                                     AMOTION_EVENT_AXIS_RELATIVE_Y}) {
                    out->addMotionRange(axis, AINPUT_SOURCE_MOUSE_RELATIVE,
                                        -bounds.height, bounds.height,
                                        0.0f, 0.0f, 0.0f);
                }
            }
            // While capture is on the mouse reports its buttons and wheel here
            // rather than on the absolute device, so this device carries the
            // scroll ranges too.
            out->addMotionRange(AMOTION_EVENT_AXIS_VSCROLL,
                                AINPUT_SOURCE_MOUSE_RELATIVE,
                                -1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
            out->addMotionRange(AMOTION_EVENT_AXIS_HSCROLL,
                                AINPUT_SOURCE_MOUSE_RELATIVE,
                                -1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
            break;
        default:
            // Keyboard has no motion axes; gamepad ranges come from the
            // DEVICE_ADDED event and are added by buildDeviceInfo().
            break;
    }
}

int32_t InputReaderArc::acquireControllerNumberLocked(int32_t deviceId) {
    auto held = mControllerNumbers.find(deviceId);
    if (held != mControllerNumbers.end()) return held->second;

    // Lowest free slot. Stock keeps this in a BitSet32 and so caps at 32 pads,
    // handing out 0 ("not a controller") once full; match that rather than
    // inventing a 33rd player.
    for (int32_t candidate = 1; candidate <= 32; candidate++) {
        bool taken = false;
        for (const auto& [id, number] : mControllerNumbers) {
            (void)id;
            if (number == candidate) {
                taken = true;
                break;
            }
        }
        if (!taken) {
            mControllerNumbers[deviceId] = candidate;
            return candidate;
        }
    }
    ALOGI("all controller numbers in use; device %d gets 0", deviceId);
    return 0;
}

void InputReaderArc::releaseControllerNumberLocked(int32_t deviceId) {
    mControllerNumbers.erase(deviceId);
}

bool InputReaderArc::buildDeviceInfo(int32_t bridgeDeviceType, int32_t gamepadId,
                                      const BridgeInputEvent* added,
                                      const DisplayBounds& bounds,
                                      int32_t controllerNumber,
                                      InputDeviceInfo* out) const {
    const int32_t id = androidDeviceIdFor(bridgeDeviceType, gamepadId);
    if (id < 0 && bridgeDeviceType != -1) return false;

    const char* name = nullptr;
    uint32_t sources = 0;
    bool isExternal = false;
    int32_t keyboardType = AINPUT_KEYBOARD_TYPE_NONE;
    // Every device is named "ChromeOS ...", not "ARC ...". Apps ported for
    // Chromebooks routinely special-case Chrome OS input by device name,
    // because these are virtual devices that report no system or navigation
    // keys and no USB identity. Asphalt 8 is one: it classifies the keyboard
    // correctly, then demotes it to "unknown" for exactly that reason and drops
    // every key -- unless the name says Chrome OS. Keep "chrome" in each name,
    // and keep "remote" out of it; that pair is the test such apps apply.
    switch (bridgeDeviceType) {
        case BRIDGE_DEVICE_MOUSE:
            name = "ChromeOS Virtual Mouse";
            sources = AINPUT_SOURCE_MOUSE;
            break;
        case BRIDGE_DEVICE_TOUCHSCREEN:
            name = "ChromeOS Virtual Touchscreen";
            sources = AINPUT_SOURCE_TOUCHSCREEN;
            break;
        case BRIDGE_DEVICE_KEYBOARD:
            name = "ChromeOS Virtual Keyboard";
            sources = AINPUT_SOURCE_KEYBOARD;
            // Only claim a full alphabetic keyboard while Chrome OS says a
            // physical one is in use. WM turns an ALPHABETIC device into
            // Configuration.keyboard = QWERTY, and PhoneWindowManager only sets
            // hardKeyboardHidden = YES when keyboard == NOKEYS (fydroid has no
            // lid) — so claiming it unconditionally tells every third-party IME
            // that the user has a real keyboard, and Gboard et al. refuse to
            // draw an on-screen one even on a tablet with nothing attached.
            //
            // The device itself stays present either way: it is how wl_keyboard
            // input reaches Android, and key delivery does not depend on the
            // keyboard type.
            keyboardType = mKeyboardIsPhysical ? AINPUT_KEYBOARD_TYPE_ALPHABETIC
                                               : AINPUT_KEYBOARD_TYPE_NON_ALPHABETIC;
            break;
        case BRIDGE_DEVICE_STYLUS:
            name = "ChromeOS Virtual Stylus";
            sources = AINPUT_SOURCE_STYLUS;
            break;
        case BRIDGE_DEVICE_MOUSE_RELATIVE:
            name = "ChromeOS Virtual Relative Mouse";
            sources = AINPUT_SOURCE_MOUSE_RELATIVE;
            break;
        case BRIDGE_DEVICE_GAMEPAD:
            // A gamepad is the one device here that is NOT virtual: Chrome
            // hands us the real controller's name and USB identity, and games
            // routinely map controllers by name or VID:PID. Reporting them all
            // as one anonymous "ChromeOS Virtual Gamepad" made every pad
            // indistinguishable and matched no controller profile anywhere.
            // The Chrome-OS-name reasoning above is about the keyboard, and
            // does not apply: apps detect gamepads by SOURCE_JOYSTICK.
            name = "ChromeOS Virtual Gamepad";  // fallback only
            sources = AINPUT_SOURCE_JOYSTICK | AINPUT_SOURCE_GAMEPAD;
            isExternal = true;
            break;
        default:
            return false;
    }

    InputDeviceIdentifier ident;
    ident.name = name;
    std::string displayName = name;

    if (bridgeDeviceType == BRIDGE_DEVICE_GAMEPAD && added != nullptr) {
        if (added->gamepadName[0] != '\0') {
            // strnlen guards a name that filled the buffer without a NUL.
            displayName.assign(added->gamepadName,
                               strnlen(added->gamepadName,
                                       sizeof(added->gamepadName)));
            ident.name = displayName;
        }
        ident.bus = static_cast<uint16_t>(added->gamepadBusType);
        ident.vendor = static_cast<uint16_t>(added->gamepadVendorId);
        ident.product = static_cast<uint16_t>(added->gamepadProductId);
        ident.version = static_cast<uint16_t>(added->gamepadVersion);
    }

    assignDescriptor(ident);
    out->initialize(id, mNextGeneration++, controllerNumber,
                    ident, displayName, isExternal, false /*hasMic*/);
    out->addSource(sources);
    if (keyboardType != AINPUT_KEYBOARD_TYPE_NONE) {
        out->setKeyboardType(keyboardType);
    }

    // The keyboard needs a KeyCharacterMap, as stock EventHub gives every
    // real keyboard (no device-specific file → Generic.kcm). Without one,
    // KeyEvent.getUnicodeChar() is 0 for every wl_keyboard key, and IMEs that
    // read characters from it — Gboard, SwiftKey — silently drop all hardware
    // typing. FydroidIME never noticed: it carries its own Virtual map.
    //
    // On top of it goes the layout picked in Android's physical keyboard
    // settings, which is how Gboard/SwiftKey type Arabic, Cyrillic, AZERTY...
    // from a hardware keyboard (stock: InputDevice::configure). combine()
    // edits the map in place, so start from a freshly loaded base each time
    // or a layout change would stack on the previous overlay.
    if (bridgeDeviceType == BRIDGE_DEVICE_KEYBOARD) {
        KeyMap keyMap;
        if (keyMap.load(ident, nullptr) == OK && keyMap.keyCharacterMap) {
            std::shared_ptr<KeyCharacterMap> overlay =
                    mPolicy->getKeyboardLayoutOverlay(ident);
            if (overlay) {
                keyMap.keyCharacterMap->combine(*overlay);
                ALOGI("Keyboard layout overlay applied to %s", name);
            }
            out->setKeyCharacterMap(keyMap.keyCharacterMap);
        } else {
            ALOGW("No KeyCharacterMap for %s", name);
        }
    }

    // Deliberately no KeyCharacterMap on a gamepad. Generic.kcm defines
    // fallback actions for the buttons (BUTTON_B -> BACK, BUTTON_X -> DEL,
    // BUTTON_A -> DPAD_CENTER, BUTTON_Y -> SPACE), and the dispatcher
    // synthesises those whenever the focused app declines the button. On a pad
    // whose buttons an app does not consume that turns one press into two
    // events -- a real B plus a BACK -- which is worse than the missing map.

    // Publish the joystick axes. Apps following Android's own gamepad sample
    // call getMotionRange(axis, source) and skip any axis that returns null, so
    // without these the sticks, triggers and d-pad are invisible however
    // correctly the events are routed.
    if (bridgeDeviceType == BRIDGE_DEVICE_GAMEPAD && added != nullptr) {
        for (int i = 0; i < 8; i++) {
            if (!added->gamepadAxisPresent[i]) continue;
            const int32_t axis = gamepadAxisToAndroid(i);
            if (axis < 0) continue;
            out->addMotionRange(axis, AINPUT_SOURCE_JOYSTICK,
                                added->gamepadAxisMin[i],
                                added->gamepadAxisMax[i],
                                added->gamepadAxisFlat[i],
                                added->gamepadAxisFuzz[i],
                                0.0f /*resolution*/);
        }
    } else {
        // Pointer, touch and stylus axes, sized to the current display.
        addMotionRangesFor(bridgeDeviceType, bounds, out);
    }
    return true;
}

void InputReaderArc::handleKeyboardTypeChanged(const BridgeInputEvent& event) {
    const bool physical = event.action == BRIDGE_KEYBOARD_PHYSICAL;
    bool changed = false;
    {
        AutoMutex _l(mLock);
        if (mKeyboardIsPhysical == physical) return;
        mKeyboardIsPhysical = physical;

        // Re-describe the keyboard if it is already present, so
        // Configuration.keyboard follows without waiting for a re-add.
        const int32_t id = androidDeviceIdFor(BRIDGE_DEVICE_KEYBOARD, 0);
        auto it = mDevices.find(id);
        if (it != mDevices.end()) {
            InputDeviceInfo info;
            if (buildDeviceInfo(BRIDGE_DEVICE_KEYBOARD, 0, nullptr,
                                physicalDisplayBoundsLocked(),
                                0 /*controllerNumber*/, &info)) {
                it->second = std::move(info);
                changed = true;
            }
        }
    }
    ALOGI("Chrome OS keyboard type: %s%s", physical ? "PHYSICAL" : "VIRTUAL",
          changed ? " (device re-described)" : "");
    if (changed) notifyDevicesChanged();
}

void InputReaderArc::handleDeviceAdded(const BridgeInputEvent& event) {
    bool changed = false;
    {
        AutoMutex _l(mLock);
        const int32_t addedId = androidDeviceIdFor(event.deviceType, event.gamepadId);
        const int32_t controllerNumber =
                (event.deviceType == BRIDGE_DEVICE_GAMEPAD && addedId >= 0)
                        ? acquireControllerNumberLocked(addedId)
                        : 0;
        InputDeviceInfo info;
        if (!buildDeviceInfo(event.deviceType, event.gamepadId, &event,
                             physicalDisplayBoundsLocked(), controllerNumber,
                             &info)) {
            ALOGW("DEVICE_ADDED: unknown bridge device type %d", event.deviceType);
            releaseControllerNumberLocked(addedId);
            return;
        }
        const int32_t id = info.getId();
        auto it = mDevices.find(id);
        if (it == mDevices.end()) {
            ALOGI("DEVICE_ADDED id=%d bridgeType=%d gamepadId=%d name='%s' "
                  "vendor=0x%04x product=0x%04x axes=%zu",
                  id, event.deviceType, event.gamepadId,
                  info.getIdentifier().name.c_str(),
                  info.getIdentifier().vendor, info.getIdentifier().product,
                  info.getMotionRanges().size());
            mDevices.emplace(id, std::move(info));
            // Keep the announcement so the device can be described again from
            // source when the display geometry changes.
            mDeviceAdded[id] = event;
            changed = true;
            // A device announced before any viewport was known has no pointer
            // axis ranges yet; this brings it (and any earlier one) in line
            // with the current geometry, and is a no-op once they agree.
            rebuildDeviceInfosLocked();
        }
    }
    if (changed) notifyDevicesChanged();
}

void InputReaderArc::handleDeviceRemoved(const BridgeInputEvent& event) {
    const int32_t id = androidDeviceIdFor(event.deviceType, event.gamepadId);
    if (id < 0) {
        ALOGW("DEVICE_REMOVED: unknown bridge device type %d", event.deviceType);
        return;
    }
    bool changed = false;
    {
        AutoMutex _l(mLock);
        auto it = mDevices.find(id);
        if (it != mDevices.end()) {
            ALOGI("DEVICE_REMOVED id=%d bridgeType=%d gamepadId=%d",
                  id, event.deviceType, event.gamepadId);
            mDevices.erase(it);
            mDeviceAdded.erase(id);
            // Hand the player slot back so the next pad reuses it.
            releaseControllerNumberLocked(id);
            changed = true;
        }
    }
    if (changed) notifyDevicesChanged();
}

void InputReaderArc::notifyDevicesChanged() {
    // AOSP-13: getInputDevices() returns by value.
    std::vector<InputDeviceInfo> snapshot = getInputDevices();
    mPolicy->notifyInputDevicesChanged(snapshot);
}

int32_t InputReaderArc::getScanCodeState(int32_t /*deviceId*/, uint32_t /*sourceMask*/,
                                          int32_t scanCode) {
    AutoMutex _l(mLock);
    return mPressedScanCodes.count(scanCode) ? AKEY_STATE_DOWN : AKEY_STATE_UP;
}

int32_t InputReaderArc::getKeyCodeState(int32_t /*deviceId*/, uint32_t /*sourceMask*/,
                                         int32_t keyCode) {
    AutoMutex _l(mLock);
    return mPressedKeyCodes.count(keyCode) ? AKEY_STATE_DOWN : AKEY_STATE_UP;
}

int32_t InputReaderArc::getSwitchState(int32_t /*deviceId*/, uint32_t /*sourceMask*/,
                                        int32_t /*sw*/) {
    return AKEY_STATE_UNKNOWN;
}

void InputReaderArc::toggleCapsLockState(int32_t /*deviceId*/) {}

bool InputReaderArc::hasKeys(int32_t deviceId, uint32_t /*sourceMask*/,
                              size_t numCodes, const int32_t* keyCodes,
                              uint8_t* outFlags) {
    memset(outFlags, 0, numCodes);
    // Stock AOSP answers this from the evdev key bitmap. There is no evdev
    // behind this reader, so answer from the same key layout the keyboard
    // events are decoded with: the keyboard has a key exactly when some scan
    // code maps to it. Reporting nothing (the previous behaviour) makes every
    // KeyCharacterMap.deviceHasKey() call return false, which apps read as
    // "this machine has no real keyboard" -- Asphalt 8 classifies the keyboard
    // correctly and then discards it on that basis, dropping all key input.
    if (!mKeyLayout) {
        return false;
    }
    // A gamepad answers from the other half of that same layout. Refusing it
    // here (the previous behaviour) described a pad with no buttons at all:
    // axes are discovered through getMotionRanges(), which is populated, but
    // buttons are discovered through this call, so a game mapped the sticks
    // and nothing else while the button events themselves arrived normally.
    // Gamepad ids start at DEVICE_ID_GAMEPAD_BASE and nothing else uses that
    // range.
    const bool isGamepad = (deviceId >= DEVICE_ID_GAMEPAD_BASE);
    // deviceId < 0 means "any device", which the virtual keyboard's character
    // map asks with; that keeps answering for the keyboard as it always has.
    if (deviceId >= 0 && deviceId != DEVICE_ID_KEYBOARD && !isGamepad) {
        return false;
    }

    // A pad whose d-pad is a hat -- ABS_HAT0X/Y, which is how an Xbox pad and
    // most others report it -- carries no BTN_DPAD_* codes at all, so the key
    // layout has nothing to answer DPAD_UP..DPAD_RIGHT with. The d-pad is real
    // and does reach apps: the framework turns AXIS_HAT_X/Y into exactly those
    // key events in ViewRootImpl's SyntheticJoystickHandler. Answering "no"
    // therefore describes a pad that has no d-pad when it plainly does, and an
    // engine that gates the buttons on a required d-pad set (Rewired asks for
    // 19/20/21/22/23 before it will treat a pad as key-mapped) then drops every
    // button while keeping the sticks.
    bool hatDpad = false;
    if (isGamepad) {
        AutoMutex _l(mLock);
        auto it = mDeviceAdded.find(deviceId);
        hatDpad = it != mDeviceAdded.end() && it->second.gamepadAxisPresent[6] &&
                  it->second.gamepadAxisPresent[7];
    }

    bool haveAll = true;
    for (size_t i = 0; i < numCodes; i++) {
        std::vector<int32_t> scanCodes;
        bool found = false;
        if (hatDpad && (keyCodes[i] == AKEYCODE_DPAD_UP ||
                        keyCodes[i] == AKEYCODE_DPAD_DOWN ||
                        keyCodes[i] == AKEYCODE_DPAD_LEFT ||
                        keyCodes[i] == AKEYCODE_DPAD_RIGHT)) {
            found = true;
        } else if (mKeyLayout->findScanCodesForKey(keyCodes[i], &scanCodes) == OK) {
            for (int32_t scanCode : scanCodes) {
                if (scanCode <= 0) continue;
                // 0x100 (BTN_MISC) is the boundary in Generic.kl between the
                // keyboard's scan codes and the BTN_* button codes: a keyboard
                // must not claim the buttons, and a gamepad has only those.
                if ((scanCode >= 0x100) == isGamepad) {
                    found = true;
                    break;
                }
            }
        }
        outFlags[i] = found ? 1 : 0;
        if (!found) {
            haveAll = false;
        }
    }
    return haveAll;
}

void InputReaderArc::requestRefreshConfiguration(uint32_t changes) {
    if (changes & InputReaderConfiguration::CHANGE_KEYBOARD_LAYOUTS) {
        mPendingKeyboardLayouts = true;
        wake();
    }
    if (changes & InputReaderConfiguration::CHANGE_DISPLAY_INFO) {
        refreshDisplayViewport();
    }
    if (changes & InputReaderConfiguration::CHANGE_POINTER_CAPTURE) {
        // Called on the dispatcher's thread; the acknowledgement has to be
        // queued from the reader thread, which owns mQueuedListener.
        InputReaderConfiguration config;
        mPolicy->getReaderConfiguration(&config);
        {
            AutoMutex _l(mLock);
            mPendingPointerCaptureRequest = config.pointerCaptureRequest;
            mHasPendingPointerCapture = true;
        }
        wake();
    }
}

void InputReaderArc::wake() {
    if (mWakeEventFd < 0) return;
    uint64_t val = 1;
    ssize_t written = write(mWakeEventFd, &val, sizeof(val));
    if (written != sizeof(val)) {
        ALOGW("failed to wake reader thread: %s", strerror(errno));
    }
}

void InputReaderArc::processPendingKeyboardLayouts() {
    if (!mPendingKeyboardLayouts.exchange(false)) return;
    bool changed = false;
    {
        AutoMutex _l(mLock);
        // Re-describing the keyboard re-reads the overlay from the policy.
        const int32_t id = androidDeviceIdFor(BRIDGE_DEVICE_KEYBOARD, 0);
        auto it = mDevices.find(id);
        auto added = mDeviceAdded.find(id);
        InputDeviceInfo info;
        if (it != mDevices.end() &&
            buildDeviceInfo(BRIDGE_DEVICE_KEYBOARD, 0,
                            added != mDeviceAdded.end() ? &added->second : nullptr,
                            physicalDisplayBoundsLocked(),
                            0 /*controllerNumber*/, &info)) {
            it->second = std::move(info);
            changed = true;
        }
    }
    ALOGI("keyboard layouts changed%s", changed ? "; keyboard re-described" : "");
    if (changed) notifyDevicesChanged();
}

void InputReaderArc::processPendingPointerCapture() {
    PointerCaptureRequest request;
    {
        AutoMutex _l(mLock);
        if (!mHasPendingPointerCapture) return;
        mHasPendingPointerCapture = false;
        if (mPendingPointerCaptureRequest == mCurrentPointerCaptureRequest) {
            return;
        }
        mCurrentPointerCaptureRequest = mPendingPointerCaptureRequest;
        request = mCurrentPointerCaptureRequest;
    }

    ALOGI("pointer capture %s (seq=%u)", request.enable ? "enabled" : "disabled",
          request.seq);

    // Without this the dispatcher never hands the focused window capture: it
    // waits for the reader to confirm the mode change, and until it does, a
    // game that asked for the mouse gets neither onPointerCaptureChange(true)
    // nor any captured event.
    const NotifyPointerCaptureChangedArgs args(mIdGenerator.nextId(),
                                               systemTime(SYSTEM_TIME_MONOTONIC),
                                               request);
    mQueuedListener.notifyPointerCaptureChanged(&args);
    mQueuedListener.flush();

    // And the other half: the deltas themselves only exist if the compositor
    // locks the pointer, which only SurfaceFlinger's wayland client can ask
    // for. Failing to reach it is not fatal -- Android stays in captured mode
    // and the request is retried on the next toggle.
    WaylandServiceProxy proxy;
    if (proxy.connectToWayland()) {
        proxy.requestPointerCapture(request.enable);
    } else {
        ALOGW("Wayland service unavailable; pointer lock not %s",
              request.enable ? "taken" : "dropped");
    }
}

void InputReaderArc::refreshDisplayViewport() {
    InputReaderConfiguration config;
    mPolicy->getReaderConfiguration(&config);
    std::optional<DisplayViewport> internal =
            config.getDisplayViewportByType(ViewportType::INTERNAL);
    if (internal) {
        ALOGI("display viewports refreshed; internal: displayId=%d "
              "orientation=%d logical=%dx%d",
              internal->displayId, internal->orientation,
              internal->deviceWidth, internal->deviceHeight);
    } else {
        ALOGI("display viewports refreshed; no internal viewport yet");
    }
    bool changed = false;
    {
        AutoMutex _l(mLock);
        mConfig = config;
        // Pointer axis ranges are display-sized, so a rotation or resize
        // invalidates every description built against the old geometry.
        changed = rebuildDeviceInfosLocked();
    }
    if (changed) notifyDevicesChanged();
}

bool InputReaderArc::rebuildDeviceInfosLocked() {
    const DisplayBounds bounds = physicalDisplayBoundsLocked();
    if (bounds == mDescribedBounds) return false;
    if (bounds.width <= 0.0f || bounds.height <= 0.0f) return false;

    bool changed = false;
    for (auto& [id, info] : mDevices) {
        auto added = mDeviceAdded.find(id);
        if (added == mDeviceAdded.end()) continue;  // e.g. the Virtual keyboard
        InputDeviceInfo rebuilt;
        // Idempotent for a device that already holds a number, so a rotation
        // does not renumber the pads.
        const int32_t controllerNumber =
                (added->second.deviceType == BRIDGE_DEVICE_GAMEPAD)
                        ? acquireControllerNumberLocked(id)
                        : 0;
        if (!buildDeviceInfo(added->second.deviceType, added->second.gamepadId,
                             &added->second, bounds, controllerNumber,
                             &rebuilt)) {
            continue;
        }
        info = std::move(rebuilt);
        changed = true;
    }
    ALOGI("display geometry %gx%g -> %gx%g; %s",
          mDescribedBounds.width, mDescribedBounds.height,
          bounds.width, bounds.height,
          changed ? "devices re-described" : "no devices to re-describe");
    mDescribedBounds = bounds;
    return changed;
}

// Logical→physical point mapping — the inverse of the physical→logical
// display transform WM hands the dispatcher (verified against the
// `dumpsys input` per-window transforms):
//   ROTATION_90:  logical = (py, physW - px)  →  physical = (H - ly, lx)
//   ROTATION_270: logical = (physH - py, px)  →  physical = (ly, W - lx)
// where W,H are the viewport's LOGICAL (rotated) deviceWidth/deviceHeight
// (physW = H, physH = W when rotated 90/270 —
// see LogicalDisplay.configureDisplayLocked()).
// Unknown displays and orientation-0 viewports are identity.
void InputReaderArc::mapToPhysicalLocked(int32_t displayId,
                                          float& x, float& y) const {
    std::optional<DisplayViewport> v = mConfig.getDisplayViewportById(displayId);
    if (!v) return;
    const float lx = x, ly = y;
    switch (v->orientation) {
        case DISPLAY_ORIENTATION_90:
            x = static_cast<float>(v->deviceHeight) - ly;
            y = lx;
            break;
        case DISPLAY_ORIENTATION_180:
            x = static_cast<float>(v->deviceWidth) - lx;
            y = static_cast<float>(v->deviceHeight) - ly;
            break;
        case DISPLAY_ORIENTATION_270:
            x = ly;
            y = static_cast<float>(v->deviceWidth) - lx;
            break;
        default:
            break;  // ROTATION_0: identity
    }
}

// AOSP-13: vibrate takes a VibrationSequence (ARC devices don't vibrate).
void InputReaderArc::vibrate(int32_t /*deviceId*/, const VibrationSequence& /*sequence*/,
                              ssize_t /*repeat*/, int32_t /*token*/) {}

void InputReaderArc::cancelVibrate(int32_t /*deviceId*/, int32_t /*token*/) {}

bool InputReaderArc::canDispatchToDisplay(int32_t /*deviceId*/, int32_t /*displayId*/) {
    return true;
}

// AOSP-13: new InputReaderInterface pure virtuals. The ARC input bridge exposes
// no per-input-device vibrators/lights, and no input-device battery/sensors —
// real battery + sensor data flows through the ARC sensor HAL, not the InputReader,
// so these return empty/nullopt. Key-location mapping is identity here.
int32_t InputReaderArc::getKeyCodeForKeyLocation(int32_t /*deviceId*/,
                                                 int32_t locationKeyCode) const {
    return locationKeyCode;
}

bool InputReaderArc::isVibrating(int32_t /*deviceId*/) { return false; }

std::vector<int32_t> InputReaderArc::getVibratorIds(int32_t /*deviceId*/) { return {}; }

std::optional<int32_t> InputReaderArc::getBatteryCapacity(int32_t /*deviceId*/) {
    return std::nullopt;
}

std::optional<int32_t> InputReaderArc::getBatteryStatus(int32_t /*deviceId*/) {
    return std::nullopt;
}

std::vector<InputDeviceLightInfo> InputReaderArc::getLights(int32_t /*deviceId*/) { return {}; }

std::vector<InputDeviceSensorInfo> InputReaderArc::getSensors(int32_t /*deviceId*/) { return {}; }

bool InputReaderArc::enableSensor(int32_t /*deviceId*/, InputDeviceSensorType /*sensorType*/,
                                  std::chrono::microseconds /*samplingPeriod*/,
                                  std::chrono::microseconds /*maxBatchReportLatency*/) {
    return false;
}

void InputReaderArc::disableSensor(int32_t /*deviceId*/, InputDeviceSensorType /*sensorType*/) {}

void InputReaderArc::flushSensor(int32_t /*deviceId*/, InputDeviceSensorType /*sensorType*/) {}

bool InputReaderArc::setLightColor(int32_t /*deviceId*/, int32_t /*lightId*/, int32_t /*color*/) {
    return false;
}

bool InputReaderArc::setLightPlayerId(int32_t /*deviceId*/, int32_t /*lightId*/,
                                      int32_t /*playerId*/) {
    return false;
}

std::optional<int32_t> InputReaderArc::getLightColor(int32_t /*deviceId*/, int32_t /*lightId*/) {
    return std::nullopt;
}

std::optional<int32_t> InputReaderArc::getLightPlayerId(int32_t /*deviceId*/,
                                                        int32_t /*lightId*/) {
    return std::nullopt;
}

} // namespace android

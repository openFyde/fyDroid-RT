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

// WaylandInputManager: Coordinates keyboard, pointer, and touch handlers.
// Owns the InputBridge (pipe) and passes it to handlers.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandInputManager"

#include "WaylandInputManager.h"
#include "InputBridge.h"
#include "WaylandPointerHandler.h"
#include "WaylandKeyboardHandler.h"
#include "WaylandTouchHandler.h"
#include "WaylandStylusHandler.h"
#include "WaylandRelativePointerHandler.h"
#include "WaylandGamepadHandler.h"
#include "../window/WaylandWindow.h"

#include <wayland-client.h>

#include <log/log.h>

namespace android {

WaylandInputManager* WaylandInputManager::sInstance = nullptr;

WaylandInputManager::WaylandInputManager() = default;

// Destructor must be in .cpp where handler types are complete.
WaylandInputManager::~WaylandInputManager() = default;

WaylandInputManager* WaylandInputManager::instance() {
    return sInstance;
}

void WaylandInputManager::initialize(wl_seat* seat, uint32_t capabilities,
                                      float scaleFactor) {
    ALOGI("Initializing input: capabilities=0x%x (pointer=%d keyboard=%d touch=%d) "
          "scaleFactor=%.2f",
          capabilities,
          !!(capabilities & WL_SEAT_CAPABILITY_POINTER),
          !!(capabilities & WL_SEAT_CAPABILITY_KEYBOARD),
          !!(capabilities & WL_SEAT_CAPABILITY_TOUCH),
          scaleFactor);

    mScaleFactor = (scaleFactor > 0.0f) ? scaleFactor : 1.0f;

    // Create the input bridge (waits for pipe fd from binder)
    mBridge = std::make_unique<InputBridge>();

    sInstance = this;

    if (capabilities & WL_SEAT_CAPABILITY_POINTER) {
        mPointer = std::make_unique<WaylandPointerHandler>();
        if (!mPointer->initialize(seat, mBridge.get(), scaleFactor)) {
            ALOGE("Failed to initialize pointer handler");
            mPointer.reset();
        } else {
            mActiveSingletons |= (1u << BRIDGE_DEVICE_MOUSE);
            publishDeviceEvent(BRIDGE_EVENT_DEVICE_ADDED, BRIDGE_DEVICE_MOUSE);
        }
    }

    if (capabilities & WL_SEAT_CAPABILITY_KEYBOARD) {
        mKeyboard = std::make_unique<WaylandKeyboardHandler>();
        if (!mKeyboard->initialize(seat, mBridge.get())) {
            ALOGE("Failed to initialize keyboard handler");
            mKeyboard.reset();
        } else {
            mActiveSingletons |= (1u << BRIDGE_DEVICE_KEYBOARD);
            publishDeviceEvent(BRIDGE_EVENT_DEVICE_ADDED, BRIDGE_DEVICE_KEYBOARD);
        }
    }

    if (capabilities & WL_SEAT_CAPABILITY_TOUCH) {
        mTouch = std::make_unique<WaylandTouchHandler>();
        if (!mTouch->initialize(seat, mBridge.get(), scaleFactor)) {
            ALOGE("Failed to initialize touch handler");
            mTouch.reset();
        } else {
            mActiveSingletons |= (1u << BRIDGE_DEVICE_TOUCHSCREEN);
            publishDeviceEvent(BRIDGE_EVENT_DEVICE_ADDED, BRIDGE_DEVICE_TOUCHSCREEN);
        }
    }
}

void WaylandInputManager::initializeExtended(
        zcr_stylus_v2* stylus,
        zwp_relative_pointer_manager_v1* relPtrMgr,
        zwp_pointer_constraints_v1* ptrConstraints,
        zcr_gaming_input_v2* gamingInput,
        wl_seat* seat,
        zcr_keyboard_configuration_v1* keyboardConfig,
        zcr_cursor_shapes_v1* cursorShapes) {
    // Needed before the first capture: it is what puts the arrow back.
    if (mPointer) mPointer->setCursorShapes(cursorShapes);
    // Chrome OS tells us here whether a physical keyboard is actually in use;
    // without it Android claims one forever (see BRIDGE_EVENT_KEYBOARD_TYPE).
    if (mKeyboard) {
        mKeyboard->attachDeviceConfiguration(keyboardConfig);
    }

    // Phase 1: Stylus
    if (stylus) {
        wl_touch* touch = mTouch ? mTouch->wlTouch() : nullptr;
        wl_pointer* pointer = mPointer ? mPointer->wlPointer() : nullptr;
        if (touch || pointer) {
            mStylus = std::make_unique<WaylandStylusHandler>();
            if (mStylus->initialize(stylus, touch, pointer)) {
                if (mTouch) mTouch->setStylusHandler(mStylus.get());
                if (mPointer) mPointer->setStylusHandler(mStylus.get());
                ALOGI("Stylus handler initialized and wired to touch/pointer");
                mActiveSingletons |= (1u << BRIDGE_DEVICE_STYLUS);
                publishDeviceEvent(BRIDGE_EVENT_DEVICE_ADDED, BRIDGE_DEVICE_STYLUS);
            } else {
                mStylus.reset();
            }
        }
    }

    // Phase 2: Relative pointer
    if (relPtrMgr && mPointer) {
        mRelativePointer = std::make_unique<WaylandRelativePointerHandler>();
        mRelativePointer->setScaleFactor(mScaleFactor);
        if (!mRelativePointer->initialize(relPtrMgr, ptrConstraints,
                                           mPointer->wlPointer(), mBridge.get())) {
            mRelativePointer.reset();
        } else {
            mActiveSingletons |= (1u << BRIDGE_DEVICE_MOUSE_RELATIVE);
            publishDeviceEvent(BRIDGE_EVENT_DEVICE_ADDED, BRIDGE_DEVICE_MOUSE_RELATIVE);
        }
    }

    // Phase 3: Gamepad. The handler publishes DEVICE_ADDED/REMOVED itself
    // as zcr_gaming_input_v2 gamepad add/remove events arrive.
    if (gamingInput && seat) {
        mGamepad = std::make_unique<WaylandGamepadHandler>();
        if (!mGamepad->initialize(gamingInput, seat, mBridge.get())) {
            mGamepad.reset();
        }
    }
}

void WaylandInputManager::setInputBridgePipeFd(int fd) {
    if (mBridge) {
        mBridge->setFd(fd);
        // The bridge's fd is now live. Any DEVICE_ADDED published earlier
        // (before fd was set) was dropped, so replay the current device
        // set: singletons here, gamepads via the gamepad handler.
        // Before the device replay, so the keyboard is described with the right
        // type the first time rather than being re-described straight after.
        if (mKeyboard) {
            mKeyboard->resendKeyboardType();
        }
        replaySingletonDevices();
        if (mGamepad) {
            mGamepad->publishCurrentGamepads();
        }
    } else {
        ALOGW("setInputBridgePipeFd called but InputBridge not yet created (fd=%d)", fd);
        ::close(fd);
    }
}

void WaylandInputManager::attachKeyboardExtension(
        zcr_keyboard_extension_v1* extension, wl_display* display) {
    if (mKeyboard) {
        mKeyboard->attachKeyboardExtension(extension, display);
    }
}

void WaylandInputManager::requestPointerCapture(bool enable) {
    std::lock_guard<std::mutex> lock(mCaptureMutex);
    if (mCaptureRequested == enable) return;
    mCaptureRequested = enable;
    ALOGI("android %s pointer capture", enable ? "requested" : "released");
}

void WaylandInputManager::processPendingPointerCapture() {
    if (!mRelativePointer) return;

    std::lock_guard<std::mutex> lock(mCaptureMutex);

    if (mCaptureRequested) {
        // exo grants the constraint only on a surface whose toplevel is the
        // active window, so there is nothing to ask on until the pointer is
        // over the app. Capture is sticky, so retrying on the next turn of the
        // event loop costs a null check.
        wl_surface* surface = mPointer ? mPointer->focusSurface() : nullptr;
        if (surface) {
            // exo re-activates a persistent constraint by looking it up under
            // the surface that gained focus, so a request left pending on a
            // surface the pointer has since left would never be granted.
            if (mRelativePointer->hasLockRequest() &&
                !mRelativePointer->isLocked() && surface != mCaptureSurface) {
                mRelativePointer->unlockPointer();
            }
            if (!mRelativePointer->hasLockRequest()) {
                mRelativePointer->lockPointer(surface);
                mCaptureSurface = surface;
            }
        }
    } else if (mRelativePointer->hasLockRequest()) {
        mRelativePointer->unlockPointer();
        mCaptureSurface = nullptr;
    }

    // Follow the lock exo actually granted, not the request: a refused
    // constraint leaves the mouse absolute, which is what an app that never
    // entered captured mode is still reading.
    const bool active = mRelativePointer->isLocked();
    if (active != mCaptureActive) {
        mCaptureActive = active;
        if (mPointer) mPointer->setPointerCaptured(active);
        ALOGI("pointer lock %s", active ? "active" : "inactive");
    }
}

void WaylandInputManager::publishDeviceEvent(int32_t bridgeEventType,
                                              int32_t bridgeDeviceType,
                                              int32_t gamepadId) {
    if (!mBridge) return;
    BridgeInputEvent event = {};
    event.type = static_cast<BridgeEventType>(bridgeEventType);
    event.deviceType = bridgeDeviceType;
    event.gamepadId = gamepadId;
    mBridge->writeEvent(event);
}

void WaylandInputManager::replaySingletonDevices() {
    for (int32_t t : {BRIDGE_DEVICE_MOUSE, BRIDGE_DEVICE_TOUCHSCREEN,
                       BRIDGE_DEVICE_KEYBOARD, BRIDGE_DEVICE_STYLUS,
                       BRIDGE_DEVICE_MOUSE_RELATIVE}) {
        if (mActiveSingletons & (1u << t)) {
            publishDeviceEvent(BRIDGE_EVENT_DEVICE_ADDED, t);
        }
    }
}

void WaylandInputManager::clearFocusIfWindow(WaylandWindow* window) {
    if (mPointer) mPointer->clearFocusIfWindow(window);
    if (mTouch) mTouch->clearFocusIfWindow(window);
    // The keyboard was missing from this list, and it is the handler that
    // dereferences its focus window most often — once per keystroke.
    if (mKeyboard) mKeyboard->clearFocusIfWindow(window);
}

void WaylandInputManager::clearFocusIfContainer(WaylandLayerContainer* container) {
    if (mPointer) mPointer->clearFocusIfContainer(container);
    if (mTouch) mTouch->clearFocusIfContainer(container);
    // No keyboard equivalent: WaylandKeyboardHandler tracks only the window.
}

void WaylandInputManager::updateOutputInfo(int32_t transform) {
    ALOGI("updateOutputInfo: transform=%d", transform);
    mOutputTransform = transform;
}

} // namespace android

#pragma clang diagnostic pop

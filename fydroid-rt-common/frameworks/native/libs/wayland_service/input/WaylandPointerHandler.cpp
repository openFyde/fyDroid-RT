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

// WaylandPointerHandler: wl_pointer events → BridgeInputEvent structs.
// Motion uses absolute coordinates. Button events include action button
// and cumulative button state.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandPointerHandler"

#include "WaylandPointerHandler.h"
#include "WaylandStylusHandler.h"
#include "InputBridge.h"
#include "../window/WaylandSurfaceData.h"
#include "BridgeInputEvent.h"
#include "../layer/WaylandLayerContainer.h"
#include "../window/WaylandWindow.h"
#include "../WaylandDataDevice.h"
#include "../WaylandGlobals.h"

#include <android/input.h>
#include <linux/input.h>
#include <time.h>
#include <cstring>
#include <wayland-client.h>
#include <cursor-shapes-unstable-v1-client-protocol.h>

#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

// --- Static Wayland listeners ---

static void pointerEnter(void* data, struct wl_pointer*, uint32_t serial,
                         struct wl_surface* surface,
                         wl_fixed_t surfaceX, wl_fixed_t surfaceY) {
    static_cast<WaylandPointerHandler*>(data)->onEnter(
            serial, surface, surfaceX, surfaceY);
}

static void pointerLeave(void* data, struct wl_pointer*, uint32_t serial,
                         struct wl_surface* surface) {
    static_cast<WaylandPointerHandler*>(data)->onLeave(serial, surface);
}

static void pointerMotion(void* data, struct wl_pointer*, uint32_t time,
                          wl_fixed_t surfaceX, wl_fixed_t surfaceY) {
    static_cast<WaylandPointerHandler*>(data)->onMotion(time, surfaceX, surfaceY);
}

static void pointerButton(void* data, struct wl_pointer*, uint32_t serial,
                          uint32_t time, uint32_t button, uint32_t state) {
    static_cast<WaylandPointerHandler*>(data)->onButton(serial, time, button, state);
}

static void pointerAxis(void* data, struct wl_pointer*, uint32_t time,
                        uint32_t axis, wl_fixed_t value) {
    static_cast<WaylandPointerHandler*>(data)->onAxis(time, axis, value);
}

static void pointerFrame(void* data, struct wl_pointer*) {
    static_cast<WaylandPointerHandler*>(data)->onFrame();
}

static void pointerAxisSource(void* data, struct wl_pointer*,
                              uint32_t axisSource) {
    static_cast<WaylandPointerHandler*>(data)->onAxisSource(axisSource);
}

static void pointerAxisStop(void* data, struct wl_pointer*,
                            uint32_t time, uint32_t axis) {
    static_cast<WaylandPointerHandler*>(data)->onAxisStop(time, axis);
}

static void pointerAxisDiscrete(void* data, struct wl_pointer*,
                                uint32_t axis, int32_t discrete) {
    static_cast<WaylandPointerHandler*>(data)->onAxisDiscrete(axis, discrete);
}

static const struct wl_pointer_listener sPointerListener = {
    .enter = pointerEnter,
    .leave = pointerLeave,
    .motion = pointerMotion,
    .button = pointerButton,
    .axis = pointerAxis,
    .frame = pointerFrame,
    .axis_source = pointerAxisSource,
    .axis_stop = pointerAxisStop,
    .axis_discrete = pointerAxisDiscrete,
};

// --- Map Wayland/evdev button codes to AMOTION_EVENT_BUTTON_* ---

static int32_t waylandButtonToMotionButton(uint32_t button) {
    switch (button) {
        case BTN_LEFT:   return AMOTION_EVENT_BUTTON_PRIMARY;
        case BTN_RIGHT:  return AMOTION_EVENT_BUTTON_SECONDARY;
        case BTN_MIDDLE: return AMOTION_EVENT_BUTTON_TERTIARY;
        case BTN_SIDE:   return AMOTION_EVENT_BUTTON_BACK;
        case BTN_EXTRA:  return AMOTION_EVENT_BUTTON_FORWARD;
        default:         return 0;
    }
}

// --- WaylandPointerHandler implementation ---

WaylandPointerHandler::WaylandPointerHandler() = default;

void WaylandPointerHandler::dropFocus() {
    // The surface under the cursor is going away (a task moving into PIP
    // destroys its container), so no leave will come for it. End the hover
    // here: the next enter's HOVER_ENTER usually lands on the same Android
    // window, and one still counted as hovered aborts system_server.
    closeHover();
    mFocusContainer = nullptr;
    mFocusWindow = nullptr;
    mFocusSurface = nullptr;
    mDraggingOverlay = false;
    mOverlayPendingClick = false;
    mPipPendingMove = false;
    mButtonState = 0;
    mHasPosition = false;
}

void WaylandPointerHandler::clearFocusIfWindow(WaylandWindow* window) {
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
        dropFocus();
    }
}

void WaylandPointerHandler::clearFocusIfContainer(WaylandLayerContainer* container) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    if (container && mFocusContainer == container) {
        ALOGI("clearFocusIfContainer: clearing dangling focus container %p",
              container);
        dropFocus();
    }
}

WaylandPointerHandler::~WaylandPointerHandler() {
    if (mPointer) {
        wl_pointer_destroy(mPointer);
    }
}

int64_t WaylandPointerHandler::now() const {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return static_cast<int64_t>(ts.tv_sec) * 1000000000LL + ts.tv_nsec;
}

void WaylandPointerHandler::setPointerCaptured(bool captured) {
    if (mPointerCaptured.load() == captured) return;

    // Only take the cursor away if we can give it back. exo keeps whatever the
    // client last set across focus changes, so hiding without zcr_cursor_shapes
    // would leave every ARC window cursor-less for good.
    if (mPointer && mCursorShapes) {
        if (captured) {
            // The wayland way to hide: attach no surface to the pointer.
            wl_pointer_set_cursor(mPointer, mEnterSerial, nullptr, 0, 0);
            ALOGI("pointer captured: cursor hidden");
        } else {
            zcr_cursor_shapes_v1_set_cursor_shape(
                    mCursorShapes, mPointer,
                    ZCR_CURSOR_SHAPES_V1_CURSOR_SHAPE_TYPE_POINTER);
            ALOGI("pointer released: cursor restored");
        }
    } else if (captured) {
        ALOGW("zcr_cursor_shapes_v1 unavailable — leaving the cursor visible "
              "rather than hiding it with no way to restore it");
    }

    mPointerCaptured = captured;
}

wl_surface* WaylandPointerHandler::focusSurface() {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    return mFocusSurface;
}

// Ends the hover Android has open, on the device and display it was opened on.
// Needs no focus: it is what has to run once the focus is already gone.
void WaylandPointerHandler::closeHover() {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    if (!mHoverOpen) return;
    const int32_t buttonState = mButtonState;
    mButtonState = 0;
    writeMotionEvent(AMOTION_EVENT_ACTION_HOVER_EXIT, 0, mHoverDeviceType,
                     mHoverDisplayId, mHoverX, mHoverY, false);
    mButtonState = buttonState;
    mHoverOpen = false;
}

void WaylandPointerHandler::sendMotionEvent(int32_t action, int32_t actionButton) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    if (!mFocusContainer && !mFocusWindow) return;

    // Captured mode has no cursor, so it has no hover. Stock's relative mode
    // reports none either -- the pointer controller is faded out and only
    // motion, buttons and the wheel are produced.
    const bool captured = mPointerCaptured.load();
    if (captured && (action == AMOTION_EVENT_ACTION_HOVER_ENTER ||
                     action == AMOTION_EVENT_ACTION_HOVER_EXIT ||
                     action == AMOTION_EVENT_ACTION_HOVER_MOVE)) {
        return;
    }

    // Get coordinate offset: from container frame or standalone window bounds.
    // Cursor coords arrive in surface (buffer) pixels = output_DSF × DIP, but
    // the container offset is in Android task pixels = primary_DSF × DIP.
    // Scale the cursor up by androidDSF/outputDSF before combining so both
    // operands are in the same Android-pixel space.
    float offsetX = 0, offsetY = 0;
    float scale = 1.0f;
    if (mFocusContainer) {
        auto frame = mFocusContainer->containerFrame();
        offsetX = frame.left;
        offsetY = frame.top;
        scale = mFocusContainer->inputScale();
    } else if (mFocusWindow) {
        offsetX = static_cast<float>(mFocusWindow->displayFrameX());
        offsetY = static_cast<float>(mFocusWindow->displayFrameY());
        scale = mFocusWindow->inputScale();
    }
    float absX = static_cast<float>(mCursorX) * scale + offsetX;
    float absY = static_cast<float>(mCursorY) * scale + offsetY;

    // The device and display this event goes to. A hover is per device and
    // per display in InputDispatcher, so one left open on another device (the
    // pointer switched between mouse and stylus: on VMware the tablet's
    // proximity re-latch and its pen-button clicks flip it) or display must be
    // closed there first. Otherwise the next non-HOVER_MOVE event on that device
    // reaches a window it still counts as hovered and aborts system_server
    // ("Expected ACTION_HOVER_MOVE instead of MotionEvent").
    const bool isStylus = mStylusHandler && mStylusHandler->hasPointerStylusData();
    int32_t deviceType = isStylus ? BRIDGE_DEVICE_STYLUS : BRIDGE_DEVICE_MOUSE;
    // While the pointer is locked the cursor does not move, so an absolute
    // position here is a stale point the dispatcher would hit-test a click
    // against. Report on the relative device instead, with no position: this
    // is the button/wheel half of captured mode, the motion half arrives on
    // the same device from WaylandRelativePointerHandler.
    if (captured && !isStylus) {
        deviceType = BRIDGE_DEVICE_MOUSE_RELATIVE;
        absX = 0.0f;
        absY = 0.0f;
    }
    const int32_t displayId = mFocusWindow ? mFocusWindow->displayId() : 0;

    if (mHoverOpen && action != AMOTION_EVENT_ACTION_HOVER_EXIT &&
        (deviceType != mHoverDeviceType || displayId != mHoverDisplayId)) {
        writeMotionEvent(AMOTION_EVENT_ACTION_HOVER_EXIT, 0, mHoverDeviceType,
                         mHoverDisplayId, mHoverX, mHoverY, false);
        mHoverOpen = false;
    }
    if (action == AMOTION_EVENT_ACTION_HOVER_EXIT) {
        closeHover();
        return;
    }

    writeMotionEvent(action, actionButton, deviceType, displayId, absX, absY, isStylus);

    // Track whether Android counts this pointer as hovering, from what it was
    // actually sent: a hover enter/move opens it on this device and display,
    // and a DOWN (InputDispatcher turns a DOWN into the hover's exit) or a
    // CANCEL closes it.
    switch (action) {
        case AMOTION_EVENT_ACTION_HOVER_ENTER:
        case AMOTION_EVENT_ACTION_HOVER_MOVE:
            mHoverOpen = true;
            mHoverDeviceType = deviceType;
            mHoverDisplayId = displayId;
            mHoverX = absX;
            mHoverY = absY;
            break;
        case AMOTION_EVENT_ACTION_DOWN:
        case AMOTION_EVENT_ACTION_CANCEL:
            mHoverOpen = false;
            break;
        default:
            break;
    }
}

void WaylandPointerHandler::writeMotionEvent(int32_t action, int32_t actionButton,
                                             int32_t deviceType, int32_t displayId,
                                             float absX, float absY, bool isStylus) {
    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));

    event.type = BRIDGE_EVENT_MOTION;
    event.timestampNanos = now();
    event.displayId = displayId;
    event.action = action;
    event.actionButton = actionButton;
    event.buttonState = mButtonState;
    event.pointerCount = 1;
    event.deviceType = deviceType;

    event.pointers[0].id = 0;
    event.pointers[0].x = absX;
    event.pointers[0].y = absY;

    if (isStylus) {
        float force = mStylusHandler->pointerForce();
        event.pointers[0].pressure = (force >= 0.0f) ? force
                                     : ((mButtonState != 0) ? 1.0f : 0.0f);
        event.toolType = mStylusHandler->pointerToolType();
        event.tiltX = mStylusHandler->pointerTiltX();
        event.tiltY = mStylusHandler->pointerTiltY();
    } else {
        event.pointers[0].pressure = (mButtonState != 0) ? 1.0f : 0.0f;
    }

    event.cursorX = absX;
    event.cursorY = absY;

    mBridge->writeEvent(event);
}

bool WaylandPointerHandler::initialize(wl_seat* seat, InputBridge* bridge,
                                       float scaleFactor) {
    mBridge = bridge;
    mScaleFactor = (scaleFactor > 0.0f) ? scaleFactor : 1.0f;

    mPointer = wl_seat_get_pointer(seat);
    if (!mPointer) {
        ALOGE("wl_seat_get_pointer failed");
        return false;
    }
    mSeatVersion = wl_pointer_get_version(mPointer);
    wl_pointer_add_listener(mPointer, &sPointerListener, this);

    ALOGI("Pointer handler initialized (seat version %u, scaleFactor=%.2f)",
          mSeatVersion, mScaleFactor);
    return true;
}

void WaylandPointerHandler::onEnter(uint32_t serial, wl_surface* surface,
                                    int32_t surfaceX, int32_t surfaceY) {
    if (auto* dd = WaylandDataDevice::instance()) {
        dd->updateSerial(serial);
    }
    // Look up which container or standalone window owns this surface
    auto* container = WaylandSurfaceData::getSurfaceContainer(surface);
    auto* window = container ? nullptr
                             : WaylandSurfaceData::getSurfaceWindow(surface);

    // Never move focus onto an input-transparent window (a toast). Its surface
    // already declares an empty input region so Chrome should not send an enter
    // at all, but if one arrives, ignore it completely: the previously focused
    // surface stays focused, so a toast appearing under the cursor neither
    // steals the pointer nor drops the focus of the window it covers.
    if (window && !window->acceptsInput()) {
        ALOGVT("Pointer enter on input-transparent window %p ignored, "
               "keeping focus surface=%p", window, mFocusSurface);
        return;
    }

    // Close a hover Android still has open before starting another. An enter can
    // arrive without the previous surface's leave having reached us (the leave
    // of an ignored toast is filtered above, and with it the one that would
    // have ended the hover), and every ARC window sits at the display origin,
    // so a second HOVER_ENTER can land on the window the dispatcher already
    // counts as hovered: InputDispatcher aborts system_server on that
    // ("Expected ACTION_HOVER_MOVE instead of MotionEvent"). Close it directly:
    // sendMotionEvent() needs a focus, and after dropFocus() there is none.
    closeHover();
    // Buttons are per surface: a press Android saw has been ended by the leave
    // (see onLeave), and one it did not see must not turn this surface's
    // motion into a drag or its release into an unpaired UP.
    mButtonState = 0;

    mFocusSurface = surface;
    mEnterSerial = serial;
    // Re-define the cursor on every enter, which is what the protocol asks for
    // ("the cursor image is undefined until the client sets it") and what exo's
    // own SetFocus() comment says it requires. It also repairs the one case the
    // capture teardown cannot: if the pointer had already left the surface when
    // capture ended, exo's SetCursorType() early-returns on a null focus and
    // our restore is dropped, leaving cursor_ == kNone to be re-applied by the
    // next SetFocus() — an ARC window the cursor vanishes over for good.
    if (mCursorShapes && !mPointerCaptured.load()) {
        zcr_cursor_shapes_v1_set_cursor_shape(
                mCursorShapes, mPointer,
                ZCR_CURSOR_SHAPES_V1_CURSOR_SHAPE_TYPE_POINTER);
    }
    // On a modal's dim the pointer is reported relative to the dialog card, so
    // a click there lands outside the dialog, as Android expects.
    WaylandSurfaceData::getInputOrigin(surface, &mFocusOriginX, &mFocusOriginY);
    mCursorX = wl_fixed_to_double(surfaceX) - mFocusOriginX;
    mCursorY = wl_fixed_to_double(surfaceY) - mFocusOriginY;
    mHasPosition = true;
    mFocusContainer = container;
    mFocusWindow = window;

    ALOGVT("Pointer enter: serial=%u surface=%p container=%p window=%p pos=(%.1f, %.1f)",
           serial, surface, mFocusContainer, mFocusWindow, mCursorX, mCursorY);

    // Focus-follows-mouse: when the cursor enters a real task container's
    // surface, route through the same activation path that
    // zaura_shell.activated uses so Android brings the task to front and
    // gives it input focus. Without this, Chrome OS click-to-focus is the
    // only way to hand off focus between ARC windows, and apps that show
    // ARC overlays / dialogs over a different task end up with stale
    // focus pointing at the previously-focused task. Standalone/overlay
    // wl_surfaces (notifications, IME) are gated out by the taskId > 0
    // check inside onSurfaceActivated.
    if (mFocusContainer) {
        if (auto* globals = WaylandGlobals::instance()) {
            globals->onSurfaceActivated(surface, /*lost=*/nullptr);
        }
    }

    if (mFocusContainer || (mFocusWindow && !mFocusWindow->isOverlay())) {
        sendMotionEvent(AMOTION_EVENT_ACTION_HOVER_ENTER);
    }
}

void WaylandPointerHandler::onLeave(uint32_t serial, wl_surface* surface) {
    ALOGVT("Pointer leave: serial=%u surface=%p", serial, surface);

    // A leave for a surface we never focused (an input-transparent toast whose
    // enter we ignored) must not tear down the focus we kept instead.
    if (surface && mFocusSurface && surface != mFocusSurface) return;

    if (mHasPosition && (mFocusContainer || (mFocusWindow && !mFocusWindow->isOverlay()))) {
        if (mButtonState != 0) {
            // Leaving mid-press (ash took the pointer for a window move, or the
            // press's release lands elsewhere): end the gesture Android has in
            // progress. A HOVER_EXIT here would be rejected (the pointer is
            // down) and leave the gesture open.
            sendMotionEvent(AMOTION_EVENT_ACTION_CANCEL);
        } else if (mHoverOpen) {
            sendMotionEvent(AMOTION_EVENT_ACTION_HOVER_EXIT);
        }
    }
    mHoverOpen = false;
    mButtonState = 0;

    mFocusSurface = nullptr;
    mFocusContainer = nullptr;
    mFocusWindow = nullptr;
    mHasPosition = false;
    mDraggingOverlay = false;
    mOverlayPendingClick = false;
    // ash takes the pointer away when it starts a move, which arrives here as a
    // leave. Nothing to unwind — the drag is entirely ash's from that point —
    // but the arming must not survive to the next window this pointer enters.
    mPipPendingMove = false;
}

void WaylandPointerHandler::onMotion(uint32_t /*time*/,
                                     int32_t surfaceX, int32_t surfaceY) {
    if (!mHasPosition || (!mFocusContainer && !mFocusWindow)) return;

    // Re-read the origin on every motion, not only on enter: a modal's window
    // starts card-sized (origin 0) and grows to the whole output once its dim
    // fades in, which moves the card to (cardX, cardY) inside it. A dialog that
    // opened under a still cursor kept origin 0, so every click on it reached
    // Android in output coordinates, outside the dialog (WPS's Grant on device).
    WaylandSurfaceData::getInputOrigin(mFocusSurface, &mFocusOriginX, &mFocusOriginY);
    mCursorX = wl_fixed_to_double(surfaceX) - mFocusOriginX;
    mCursorY = wl_fixed_to_double(surfaceY) - mFocusOriginY;

    // PIP: promote a press-and-move into an ash-run window move. Small
    // movements are left alone and keep flowing into Android, so a slightly
    // unsteady finger still presses a PIP menu button rather than shoving the
    // window.
    //
    // Unlike the overlay drag below, the compositor does NOT move anything
    // here: start_move hands the whole gesture to ash, which runs its own
    // PipWindowResizer and brings tuck, fling and collision avoidance with it.
    // One request per press, no round trip per pointer frame.
    if (mPipPendingMove && mFocusContainer) {
        static constexpr double kPipMoveThreshold = 10.0;
        double dx = mCursorX - mPipPressX;
        double dy = mCursorY - mPipPressY;
        if (dx * dx + dy * dy > kPipMoveThreshold * kPipMoveThreshold) {
            mPipPendingMove = false;
            // Anchor at the PRESS, not at the current position: ash treats the
            // point as where the pointer grabbed the window, so using the
            // current one would jump the window by the slop distance.
            mFocusContainer->pipStartMove(static_cast<int32_t>(mPipPressX),
                                          static_cast<int32_t>(mPipPressY));
            // Android has had a DOWN and some MOVEs by now, and ash is about to
            // take the pointer away (it grabs capture), so no UP would ever
            // arrive. Cancel rather than just stopping: a menu button that saw
            // the press would otherwise stay latched.
            sendMotionEvent(AMOTION_EVENT_ACTION_CANCEL);
            mButtonState = 0;
            ALOGI("PIP press → ash move: moved (%.1f,%.1f) from press at (%.0f,%.0f)",
                  dx, dy, mPipPressX, mPipPressY);
            return;
        }
    }

    // Overlay: pending click → check if should convert to drag
    if (mOverlayPendingClick && mFocusWindow) {
        static constexpr double kDragThreshold = 10.0;
        double dx = mCursorX - mClickStartX;
        double dy = mCursorY - mClickStartY;
        if (dx * dx + dy * dy > kDragThreshold * kDragThreshold) {
            // Cancel the touch in Android and start drag
            sendMotionEvent(AMOTION_EVENT_ACTION_CANCEL);
            mButtonState = 0;
            mOverlayPendingClick = false;
            mDraggingOverlay = true;
            ALOGVT("Overlay click → drag: moved (%.1f,%.1f) from start", dx, dy);
        } else {
            return;  // Within tap zone, ignore motion
        }
    }

    // Overlay drag: move the window instead of sending input to Android
    // Use screen coords (bounds + surfaceLocal) which are stable after moveTo
    if (mDraggingOverlay && mFocusWindow) {
        double screenX = mFocusWindow->boundsX() + mCursorX;
        double screenY = mFocusWindow->boundsY() + mCursorY;
        int32_t newX = mDragStartBoundsX + static_cast<int32_t>(screenX - mDragStartScreenX);
        int32_t newY = mDragStartBoundsY + static_cast<int32_t>(screenY - mDragStartScreenY);
        ALOGVT("Overlay drag: screen=(%.0f,%.0f) startScreen=(%.0f,%.0f) -> (%d,%d)",
               screenX, screenY, mDragStartScreenX, mDragStartScreenY, newX, newY);
        mFocusWindow->moveTo(newX, newY);
        return;
    }

    if (!mBridge) return;

    // Skip hover events for overlay windows (only click + drag are needed)
    if (mFocusWindow && mFocusWindow->isOverlay() && mButtonState == 0) return;

    int32_t action = (mButtonState != 0)
            ? AMOTION_EVENT_ACTION_MOVE
            : AMOTION_EVENT_ACTION_HOVER_MOVE;
    sendMotionEvent(action);
}

void WaylandPointerHandler::onButton(uint32_t serial, uint32_t /*time*/,
                                     uint32_t button, uint32_t state) {
    if (auto* dd = WaylandDataDevice::instance()) {
        dd->updateSerial(serial);
    }
    if (!mFocusContainer && !mFocusWindow) return;

    // Overlay: left-click on overlay window — send DOWN immediately so
    // Android can detect long-press. If drag starts, send CANCEL.
    if (mFocusWindow && mFocusWindow->isOverlay() && button == BTN_LEFT) {
        if (state == WL_POINTER_BUTTON_STATE_PRESSED) {
            mOverlayPendingClick = true;
            mDraggingOverlay = false;
            mClickStartX = mCursorX;
            mClickStartY = mCursorY;
            mDragStartBoundsX = mFocusWindow->boundsX();
            mDragStartBoundsY = mFocusWindow->boundsY();
            mDragStartScreenX = mDragStartBoundsX + mCursorX;
            mDragStartScreenY = mDragStartBoundsY + mCursorY;
            ALOGVT("Overlay press: cursor=(%.1f,%.1f) bounds=(%d,%d)",
                   mCursorX, mCursorY, mDragStartBoundsX, mDragStartBoundsY);
            // Send DOWN immediately (enables long-press detection)
            // Only DOWN+UP for overlay — skip BUTTON_PRESS/RELEASE to avoid
            // the app seeing multiple click events per physical click.
            mButtonState = AMOTION_EVENT_BUTTON_PRIMARY;
            sendMotionEvent(AMOTION_EVENT_ACTION_DOWN);
            return;
        } else {
            if (mOverlayPendingClick) {
                // Release without drag — complete the click
                ALOGVT("Overlay click: pos=(%.1f,%.1f) offset=(%d,%d)",
                       mCursorX, mCursorY,
                       mFocusWindow->displayFrameX(), mFocusWindow->displayFrameY());
                mButtonState = 0;
                sendMotionEvent(AMOTION_EVENT_ACTION_UP);
                mOverlayPendingClick = false;
                return;
            }
            if (mDraggingOverlay) {
                ALOGVT("Overlay drag end: bounds=(%d,%d)",
                       mFocusWindow->boundsX(), mFocusWindow->boundsY());
                mDraggingOverlay = false;
            }
            return;
        }
    }

    // PIP: arm a press on a PIP window so a later move can be handed to ash.
    // The press itself is NOT swallowed — it falls through to the normal path
    // so Android still gets its DOWN, because a tap on this window has to reach
    // the PIP menu (it summons the controls, or presses one of them). Only
    // when the pointer travels far enough does onMotion call start_move.
    if (mFocusContainer && button == BTN_LEFT) {
        if (state == WL_POINTER_BUTTON_STATE_PRESSED) {
            const bool target = mFocusContainer->isPipDragTarget();
            if (mFocusContainer->isPip() || target) {
                ALOGI("PIP press: pip=%d dragTarget=%d at (%.0f,%.0f)",
                      mFocusContainer->isPip(), target, mCursorX, mCursorY);
            }
            if (target) {
                mPipPendingMove = true;
                mPipPressX = mCursorX;
                mPipPressY = mCursorY;
            }
        } else {
            mPipPendingMove = false;
        }
    }

    if (!mBridge) return;

    int32_t motionButton = waylandButtonToMotionButton(button);
    if (motionButton == 0) {
        ALOGVT("Ignoring unknown button 0x%x", button);
        return;
    }

    ALOGVT("Pointer button: button=0x%x state=%u motionButton=0x%x",
          button, state, motionButton);

    if (state == WL_POINTER_BUTTON_STATE_PRESSED) {
        bool wasHovering = (mButtonState == 0);
        mButtonState |= motionButton;
        if (wasHovering) {
            sendMotionEvent(AMOTION_EVENT_ACTION_DOWN);
        }
        sendMotionEvent(AMOTION_EVENT_ACTION_BUTTON_PRESS, motionButton);
    } else {
        if ((mButtonState & motionButton) == 0) {
            // A release for a press this surface never got (it happened
            // before the enter): Android has no gesture to end.
            ALOGVT("Ignoring release of button 0x%x not pressed here", motionButton);
            return;
        }
        mButtonState &= ~motionButton;
        sendMotionEvent(AMOTION_EVENT_ACTION_BUTTON_RELEASE, motionButton);
        if (mButtonState == 0) {
            sendMotionEvent(AMOTION_EVENT_ACTION_UP);
        }
    }
}

void WaylandPointerHandler::onAxis(uint32_t /*time*/, uint32_t axis,
                                   int32_t value) {
    double amount = wl_fixed_to_double(value);
    if (axis == WL_POINTER_AXIS_VERTICAL_SCROLL) {
        mAccumScrollY += amount;
    } else if (axis == WL_POINTER_AXIS_HORIZONTAL_SCROLL) {
        mAccumScrollX += amount;
    }
    mHasPendingScroll = true;
}

void WaylandPointerHandler::onFrame() {
    if (!mBridge || (!mFocusContainer && !mFocusWindow)) return;

    if (!mHasPendingScroll && !mHasDiscreteInFrame) return;

    // Same buffer→Android-pixel scale as sendMotionEvent above.
    float offsetX = 0, offsetY = 0;
    float scale = 1.0f;
    if (mFocusContainer) {
        auto frame = mFocusContainer->containerFrame();
        offsetX = frame.left;
        offsetY = frame.top;
        scale = mFocusContainer->inputScale();
    } else if (mFocusWindow) {
        offsetX = static_cast<float>(mFocusWindow->displayFrameX());
        offsetY = static_cast<float>(mFocusWindow->displayFrameY());
        scale = mFocusWindow->inputScale();
    }
    float absX = static_cast<float>(mCursorX) * scale + offsetX;
    float absY = static_cast<float>(mCursorY) * scale + offsetY;

    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));

    event.type = BRIDGE_EVENT_MOTION;
    event.timestampNanos = now();
    event.deviceType = BRIDGE_DEVICE_MOUSE;
    event.displayId = mFocusWindow ? mFocusWindow->displayId() : 0;
    event.action = AMOTION_EVENT_ACTION_SCROLL;
    event.buttonState = mButtonState;
    event.pointerCount = 1;

    // The wheel follows the buttons onto the relative device while captured
    // (see sendMotionEvent) -- an app reading its scroll from the captured
    // stream gets nothing otherwise.
    if (mPointerCaptured.load()) {
        event.deviceType = BRIDGE_DEVICE_MOUSE_RELATIVE;
        absX = 0.0f;
        absY = 0.0f;
    }

    event.pointers[0].id = 0;
    event.pointers[0].x = absX;
    event.pointers[0].y = absY;

    event.cursorX = absX;
    event.cursorY = absY;

    if (mHasDiscreteInFrame) {
        // Discrete scroll (mouse wheel): each discrete unit = one scroll event.
        // Wayland vertical positive = towards user = scroll down.
        // Android AXIS_VSCROLL positive = scroll up. Invert vertical.
        if (mPendingDiscreteVertical != 0 || mPendingDiscreteHorizontal != 0) {
            // Encode scroll amounts in pointer pressure/coords fields.
            // Convention: pointers[0].pressure encodes AXIS_VSCROLL,
            // cursorX/cursorY still carry position. We use the flags field
            // to carry scroll values since BridgeInputEvent doesn't have
            // dedicated scroll fields.
            //
            // Actually, for ACTION_SCROLL the reader expects axis values
            // in the PointerCoords AXIS_VSCROLL and AXIS_HSCROLL slots.
            // We'll encode vertical scroll in pointers[0].pressure (repurposed)
            // and horizontal scroll in the flags field as a simple encoding.
            // The reader side will decode these.
            //
            // Simpler approach: use the existing fields creatively.
            // pointers[0].pressure = vertical scroll (negative = down)
            // actionButton = horizontal scroll (negative = left)
            event.pointers[0].pressure = static_cast<float>(-mPendingDiscreteVertical);
            event.actionButton = -mPendingDiscreteHorizontal;
            mBridge->writeEvent(event);
        }
        // Clear continuous accumulators to avoid double-counting
        mAccumScrollY = 0.0;
        mAccumScrollX = 0.0;
    } else {
        // Continuous scroll (trackpad smooth scrolling).
        // Convert accumulated DIP to wheel ticks.
        static constexpr double kDipPerTick = 10.0;
        float vScroll = 0.0f;
        float hScroll = 0.0f;

        if (mAccumScrollY >= kDipPerTick || mAccumScrollY <= -kDipPerTick) {
            // Invert: Wayland positive = towards user = down, Android positive = up
            vScroll = static_cast<float>(-mAccumScrollY / kDipPerTick);
            int ticks = static_cast<int>(vScroll);
            mAccumScrollY += ticks * kDipPerTick;  // remove consumed amount
            vScroll = static_cast<float>(ticks);
        }
        if (mAccumScrollX >= kDipPerTick || mAccumScrollX <= -kDipPerTick) {
            hScroll = static_cast<float>(mAccumScrollX / kDipPerTick);
            int ticks = static_cast<int>(hScroll);
            mAccumScrollX -= ticks * kDipPerTick;
            hScroll = static_cast<float>(ticks);
        }

        if (vScroll != 0.0f || hScroll != 0.0f) {
            event.pointers[0].pressure = vScroll;
            event.actionButton = static_cast<int32_t>(hScroll);
            mBridge->writeEvent(event);
        }
    }

    // Reset per-frame state
    mPendingDiscreteVertical = 0;
    mPendingDiscreteHorizontal = 0;
    mHasDiscreteInFrame = false;
    mHasPendingScroll = false;
}

void WaylandPointerHandler::onAxisSource(uint32_t axisSource) {
    ALOGVT("Pointer axis_source: %u", axisSource);
}

void WaylandPointerHandler::onAxisStop(uint32_t /*time*/, uint32_t axis) {
    ALOGVT("Pointer axis_stop: axis=%u", axis);
    if (axis == WL_POINTER_AXIS_VERTICAL_SCROLL) {
        mAccumScrollY = 0.0;
    } else if (axis == WL_POINTER_AXIS_HORIZONTAL_SCROLL) {
        mAccumScrollX = 0.0;
    }
}

void WaylandPointerHandler::onAxisDiscrete(uint32_t axis, int32_t discrete) {
    ALOGVT("Pointer axis_discrete: axis=%u discrete=%d", axis, discrete);
    mHasDiscreteInFrame = true;
    if (axis == WL_POINTER_AXIS_VERTICAL_SCROLL) {
        mPendingDiscreteVertical += discrete;
    } else if (axis == WL_POINTER_AXIS_HORIZONTAL_SCROLL) {
        mPendingDiscreteHorizontal += discrete;
    }
}

} // namespace android

#pragma clang diagnostic pop

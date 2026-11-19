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

// WaylandTouchHandler: wl_touch events → BridgeInputEvent structs.
// Supports multi-touch with slot-based tracking. Events are batched
// per wl_touch.frame and flushed as a single BridgeInputEvent.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandTouchHandler"

#include "WaylandTouchHandler.h"
#include "WaylandStylusHandler.h"
#include "InputBridge.h"
#include "../window/WaylandSurfaceData.h"
#include "BridgeInputEvent.h"
#include "../layer/WaylandLayerContainer.h"
#include "../window/WaylandWindow.h"
#include "../WaylandDataDevice.h"
#include "../WaylandGlobals.h"

#include <android/input.h>
#include <time.h>
#include <cstring>
#include <wayland-client.h>

#include <cutils/properties.h>
#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

// --- Static Wayland listeners ---

static void touchDown(void* data, struct wl_touch*, uint32_t serial,
                      uint32_t time, struct wl_surface* surface,
                      int32_t id, wl_fixed_t x, wl_fixed_t y) {
    static_cast<WaylandTouchHandler*>(data)->onDown(serial, time, surface, id, x, y);
}

static void touchUp(void* data, struct wl_touch*, uint32_t serial,
                    uint32_t time, int32_t id) {
    static_cast<WaylandTouchHandler*>(data)->onUp(serial, time, id);
}

static void touchMotion(void* data, struct wl_touch*, uint32_t time,
                        int32_t id, wl_fixed_t x, wl_fixed_t y) {
    static_cast<WaylandTouchHandler*>(data)->onMotion(time, id, x, y);
}

static void touchFrame(void* data, struct wl_touch*) {
    static_cast<WaylandTouchHandler*>(data)->onFrame();
}

static void touchCancel(void* data, struct wl_touch*) {
    static_cast<WaylandTouchHandler*>(data)->onCancel();
}

// shape and orientation are not used
static void touchShape(void*, struct wl_touch*, int32_t, wl_fixed_t, wl_fixed_t) {}
static void touchOrientation(void*, struct wl_touch*, int32_t, wl_fixed_t) {}

static const struct wl_touch_listener sTouchListener = {
    .down = touchDown,
    .up = touchUp,
    .motion = touchMotion,
    .frame = touchFrame,
    .cancel = touchCancel,
    .shape = touchShape,
    .orientation = touchOrientation,
};

// --- WaylandTouchHandler implementation ---

WaylandTouchHandler::WaylandTouchHandler() = default;

void WaylandTouchHandler::dropFocus() {
    mFocusContainer = nullptr;
    mFocusWindow = nullptr;
    mFocusSurface = nullptr;
    mDraggingOverlay = false;
    mOverlayPendingTap = false;
    mPipPendingMove = false;
    mPendingAction = -1;
    mPendingMotion = false;
    for (int i = 0; i < kMaxTouchSlots; i++) {
        if (mSlots[i].active) freeSlot(i);
    }
}

void WaylandTouchHandler::clearFocusIfWindow(WaylandWindow* window) {
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

void WaylandTouchHandler::clearFocusIfContainer(WaylandLayerContainer* container) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    if (container && mFocusContainer == container) {
        ALOGI("clearFocusIfContainer: clearing dangling focus container %p",
              container);
        dropFocus();
    }
}

WaylandTouchHandler::~WaylandTouchHandler() {
    if (mTouch) {
        wl_touch_destroy(mTouch);
    }
}

int64_t WaylandTouchHandler::now() const {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return static_cast<int64_t>(ts.tv_sec) * 1000000000LL + ts.tv_nsec;
}

int WaylandTouchHandler::allocateSlot(int32_t id) {
    for (int i = 0; i < kMaxTouchSlots; i++) {
        if (!mSlots[i].active) {
            mSlots[i].id = id;
            mSlots[i].active = true;
            return i;
        }
    }
    ALOGW("No free touch slot for id %d", id);
    return -1;
}

int WaylandTouchHandler::findSlot(int32_t id) const {
    for (int i = 0; i < kMaxTouchSlots; i++) {
        if (mSlots[i].active && mSlots[i].id == id) {
            return i;
        }
    }
    return -1;
}

void WaylandTouchHandler::freeSlot(int index) {
    mSlots[index].active = false;
    mSlots[index].id = -1;
}

int WaylandTouchHandler::activeCount() const {
    int count = 0;
    for (int i = 0; i < kMaxTouchSlots; i++) {
        if (mSlots[i].active) count++;
    }
    return count;
}

void WaylandTouchHandler::sendTouchEvent(int32_t action) {
    std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
    if (!mBridge || (!mFocusContainer && !mFocusWindow)) return;

    // Coordinate offset + scale: touch slot coords arrive in surface (buffer)
    // pixels = output_DSF × DIP, but the container offset is in Android task
    // pixels = primary_DSF × DIP. Scale by androidDSF/outputDSF so the final
    // pointer coords are in Android pixel space.
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

    BridgeInputEvent event;
    memset(&event, 0, sizeof(event));

    event.type = BRIDGE_EVENT_MOTION;
    event.timestampNanos = now();
    event.displayId = mFocusWindow ? mFocusWindow->displayId() : 0;
    event.action = action;

    // Check if any active touch has stylus data — if so, use BRIDGE_DEVICE_STYLUS
    bool hasStylus = false;
    if (mStylusHandler) {
        for (int i = 0; i < kMaxTouchSlots; i++) {
            if (mSlots[i].active && mStylusHandler->hasTouchStylusData(mSlots[i].id)) {
                hasStylus = true;
                break;
            }
        }
    }

    event.deviceType = hasStylus ? BRIDGE_DEVICE_STYLUS : BRIDGE_DEVICE_TOUCHSCREEN;

    uint32_t count = 0;
    for (int i = 0; i < kMaxTouchSlots; i++) {
        if (mSlots[i].active && count < 16) {
            event.pointers[count].id = i;
            event.pointers[count].x = static_cast<float>(mSlots[i].x) * scale + offsetX;
            event.pointers[count].y = static_cast<float>(mSlots[i].y) * scale + offsetY;

            // Use stylus force if available, otherwise default to 1.0
            if (mStylusHandler && mStylusHandler->hasTouchStylusData(mSlots[i].id)) {
                float force = mStylusHandler->touchForce(mSlots[i].id);
                event.pointers[count].pressure = (force >= 0.0f) ? force : 1.0f;

                // Set stylus-specific fields from first stylus touch
                if (count == 0) {
                    event.toolType = mStylusHandler->touchToolType(mSlots[i].id);
                    event.tiltX = mStylusHandler->touchTiltX(mSlots[i].id);
                    event.tiltY = mStylusHandler->touchTiltY(mSlots[i].id);
                }
            } else {
                event.pointers[count].pressure = 1.0f;
            }
            count++;
        }
    }
    event.pointerCount = count;

    if (mFocusWindow) {
        ALOGVT("sendTouchEvent(standalone): action=0x%x pointerCount=%u "
              "surfLocal=(%.1f,%.1f) offset=(%.0f,%.0f) abs=(%.1f,%.1f)",
              action, count,
              count > 0 ? static_cast<float>(mSlots[0].x) : 0.f,
              count > 0 ? static_cast<float>(mSlots[0].y) : 0.f,
              offsetX, offsetY,
              count > 0 ? event.pointers[0].x : 0.f,
              count > 0 ? event.pointers[0].y : 0.f);
    } else {
        ALOGVT("sendTouchEvent: action=0x%x pointerCount=%u stylus=%d", action, count, hasStylus);
    }
    mBridge->writeEvent(event);
}

bool WaylandTouchHandler::initialize(wl_seat* seat, InputBridge* bridge,
                                     float scaleFactor) {
    mBridge = bridge;
    mScaleFactor = (scaleFactor > 0.0f) ? scaleFactor : 1.0f;

    mTouch = wl_seat_get_touch(seat);
    if (!mTouch) {
        ALOGE("wl_seat_get_touch failed");
        return false;
    }
    wl_touch_add_listener(mTouch, &sTouchListener, this);

    ALOGI("Touch handler initialized (scaleFactor=%.2f)", mScaleFactor);
    return true;
}

void WaylandTouchHandler::onDown(uint32_t serial, uint32_t /*time*/,
                                 wl_surface* surface, int32_t id,
                                 int32_t x, int32_t y) {
    if (auto* dd = WaylandDataDevice::instance()) {
        dd->updateSerial(serial);
    }

    // Look up container or standalone window on first touch
    if (activeCount() == 0) {
        auto* container = WaylandSurfaceData::getSurfaceContainer(surface);
        auto* window = container ? nullptr
                                 : WaylandSurfaceData::getSurfaceWindow(surface);
        // Never move focus onto an input-transparent window (a toast): ignore
        // the touch and leave the previous focus in place, rather than
        // retargeting to a window that cannot accept it.
        if (window && !window->acceptsInput()) {
            ALOGVT("Touch down on input-transparent window %p ignored", window);
            return;
        }
        mFocusSurface = surface;
        mFocusContainer = container;
        mFocusWindow = window;
        ALOGVT("Touch down: serial=%u surface=%p container=%p window=%p",
               serial, surface, mFocusContainer, mFocusWindow);

        // Hand Android focus to the task being touched, the same way the
        // pointer does on enter (WaylandPointerHandler::onEnter). Touch has no
        // enter event, so nothing did this for it.
        //
        // Every task container reports its frame at the origin — Android always
        // sees a task at (0,0,w,h) — so two side-by-side snapped tasks occupy
        // the same rectangle in Android's display space. The dispatcher cannot
        // tell them apart by coordinates and delivers to whichever is focused
        // and on top, which is why tapping the other half of a split did
        // nothing the first time: Chrome's own click-to-focus only activated it
        // afterwards, so the second tap landed. Activating up front makes the
        // first tap land where it was aimed. Standalone/overlay surfaces are
        // gated out by the taskId > 0 check inside onSurfaceActivated.
        //
        // This is the one activation path that has to wait for the focus change
        // to land, because the DOWN is injected right below it — hence
        // awaitFocus. The wait is bounded and happens on the Wayland event
        // thread, never on SurfaceFlinger's main thread.
        //
        // Skipping this when the task already holds focus looks safe — a touch
        // on the focused window has no other task to be confused with — and it
        // does fix the tablet multitask menu, whose drag starts with a DOWN
        // here. But it wrecked split view on device: the active task flip-flopped
        // between the two halves and one of them stopped following its snap
        // bounds (`resizeTask ... result=0`). mFocused is the layer manager's
        // bookkeeping, not Android's focus, and in a split the two disagree
        // often enough that "already focused" is not a safe reason to skip.
        // Reverted rather than left in; the menu needs a fix that does not run
        // through this path.
        //
        // persist.fydroid.touch_focus=0 disables the call outright. That is the
        // escape hatch that proved this path is what makes the menu hard to
        // raise — with it off the panel opens easily — but it costs the
        // first-tap-on-the-other-half behaviour above, so it is not a fix.
        // Read once: flipping it needs setprop inside the container then
        // kill -9 surfaceflinger (see sf-persist-prop-static-init).
        // ...except in the top strip, where the tablet multitask menu gesture
        // starts. Asserting focus on that DOWN is what made the panel so hard
        // to raise — proven on device by disabling the assert outright, after
        // which it opens easily. The exemption is a band across the top of the
        // window rather than ash's centred rectangle; see
        // inTabletMenuGestureStrip() for why the horizontal half is not
        // reproduced. The cost is a first tap in that band on the far half of
        // a split, which is the trade taken deliberately over the menu being
        // effectively unreachable.
        static const bool kTouchFocus =
                property_get_bool("persist.fydroid.touch_focus", true);
        // The exemption applies whether or not the window currently holds
        // activation. It was once also gated on isFocused(), because skipping
        // this call appeared to stop a tap in the top strip from raising an
        // unfocused ARC window ("the focus is chaos"). That diagnosis was
        // wrong: the culprits were Android's freeform decor claiming logical
        // y 0..76 (base 0052) and our input region covering the whole output
        // (see applyInputRegion), which between them meant the tap was not
        // reaching the window the user aimed at. With both fixed the guard is
        // redundant — ash activates a tapped window itself and then sends the
        // activation event, which runs setActiveTask, so nothing here is
        // needed to raise it. Removal verified on device: the menu still drags
        // down, a tap in the top strip of an unfocused window still raises it,
        // and split view stays sane.
        const bool menuStrip =
                mFocusContainer &&
                mFocusContainer->inTabletMenuGestureStrip(wl_fixed_to_double(y));
        if (mFocusContainer && kTouchFocus && !menuStrip) {
            if (auto* globals = WaylandGlobals::instance()) {
                globals->onSurfaceActivated(surface, /*lost=*/nullptr,
                                            /*awaitFocus=*/true);
            }
        }
    }

    int slot = allocateSlot(id);
    if (slot < 0) return;

    // A touch on a modal's dim is delivered relative to the dialog card, so it
    // lands outside the dialog — Android then cancels a cancelable dialog.
    WaylandSurfaceData::getInputOrigin(surface, &mSlots[slot].originX,
                                       &mSlots[slot].originY);
    mSlots[slot].x = wl_fixed_to_double(x) - mSlots[slot].originX;
    mSlots[slot].y = wl_fixed_to_double(y) - mSlots[slot].originY;

    int count = activeCount();

    // Start overlay interaction on first touch on an overlay window.
    // Send DOWN immediately so Android can detect long-press.
    if (count == 1 && mFocusWindow && mFocusWindow->isOverlay()) {
        mDraggingOverlay = true;
        mOverlayPendingTap = true;
        mTapStartX = mSlots[slot].x;
        mTapStartY = mSlots[slot].y;
        mDragStartBoundsX = mFocusWindow->boundsX();
        mDragStartBoundsY = mFocusWindow->boundsY();
        mDragStartScreenX = mDragStartBoundsX + mSlots[slot].x;
        mDragStartScreenY = mDragStartBoundsY + mSlots[slot].y;
        ALOGVT("Overlay touch start: touch=(%.1f,%.1f) bounds=(%d,%d) screen=(%.0f,%.0f)",
               mSlots[slot].x, mSlots[slot].y, mDragStartBoundsX, mDragStartBoundsY,
               mDragStartScreenX, mDragStartScreenY);
        // Send DOWN immediately (enables long-press detection in Android)
        sendTouchEvent(AMOTION_EVENT_ACTION_DOWN);
        // Don't set mPendingAction — already sent
        ALOGVT("Touch down: id=%d slot=%d pos=(%.1f, %.1f) active=%d",
              id, slot, mSlots[slot].x, mSlots[slot].y, count);
        return;
    }

    // PIP: arm the first finger so a later move can be handed to ash as a
    // window move. Single finger only — a second finger on a PIP window is the
    // app's business. The DOWN is not swallowed: a tap still has to reach
    // the PIP menu, so only crossing the slop in onMotion starts the move.
    if (count == 1 && mFocusContainer) {
        const bool target = mFocusContainer->isPipDragTarget();
        if (mFocusContainer->isPip() || target) {
            ALOGI("PIP touch: pip=%d dragTarget=%d at (%.0f,%.0f)",
                  mFocusContainer->isPip(), target, mSlots[slot].x, mSlots[slot].y);
        }
        mPipPendingMove = target;
        mPipPressX = mSlots[slot].x;
        mPipPressY = mSlots[slot].y;
    } else if (count != 1) {
        mPipPendingMove = false;
    }

    if (count == 1) {
        mPendingAction = AMOTION_EVENT_ACTION_DOWN;
    } else {
        mPendingAction = AMOTION_EVENT_ACTION_POINTER_DOWN |
                         (slot << AMOTION_EVENT_ACTION_POINTER_INDEX_SHIFT);
    }

    ALOGVT("Touch down: id=%d slot=%d pos=(%.1f, %.1f) active=%d",
          id, slot, mSlots[slot].x, mSlots[slot].y, count);
}

void WaylandTouchHandler::onUp(uint32_t /*serial*/, uint32_t /*time*/,
                               int32_t id) {
    int slot = findSlot(id);
    if (slot < 0) {
        ALOGW("Touch up for unknown id %d", id);
        return;
    }

    int count = activeCount();

    // Overlay tap: release without drag — DOWN was already sent, just send UP
    if (count == 1 && mOverlayPendingTap) {
        std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
        if (!mFocusWindow) {
            mDraggingOverlay = false;
            mOverlayPendingTap = false;
            if (mStylusHandler) mStylusHandler->clearTouchId(mSlots[slot].id);
            freeSlot(slot);
            mPendingAction = -1;
            mPendingMotion = false;
            return;
        }
        ALOGVT("Overlay tap up: pos=(%.1f,%.1f) offset=(%d,%d)",
               mSlots[slot].x, mSlots[slot].y,
               mFocusWindow->displayFrameX(), mFocusWindow->displayFrameY());
        mDraggingOverlay = false;
        mOverlayPendingTap = false;
        sendTouchEvent(AMOTION_EVENT_ACTION_UP);
        // Clean up
        if (mStylusHandler) mStylusHandler->clearTouchId(mSlots[slot].id);
        freeSlot(slot);
        mPendingAction = -1;
        mPendingMotion = false;
        mFocusSurface = nullptr;
        mFocusContainer = nullptr;
        mFocusWindow = nullptr;
        return;
    }

    if (count == 1) {
        // Last finger — primary up
        mPendingAction = AMOTION_EVENT_ACTION_UP;
        // Note: do NOT set mDraggingOverlay=false here — let onFrame's
        // drag block handle cleanup (otherwise the normal path sends a
        // stray UP after CANCEL was already sent to Android).
    } else {
        // Removing one of multiple fingers — pointer up with index
        mPendingAction = AMOTION_EVENT_ACTION_POINTER_UP |
                         (slot << AMOTION_EVENT_ACTION_POINTER_INDEX_SHIFT);
    }

    ALOGVT("Touch up: id=%d slot=%d active=%d", id, slot, count);
}

void WaylandTouchHandler::onMotion(uint32_t /*time*/, int32_t id,
                                   int32_t x, int32_t y) {
    int slot = findSlot(id);
    if (slot < 0) return;

    mSlots[slot].x = wl_fixed_to_double(x) - mSlots[slot].originX;
    mSlots[slot].y = wl_fixed_to_double(y) - mSlots[slot].originY;

    // PIP: hand the gesture to ash once it is clearly a drag. Same touch slop
    // as the overlay path below — a finger is less steady than a mouse, and
    // under-shooting here would shove the window on every tap of a menu button.
    if (mPipPendingMove && mFocusContainer && activeCount() == 1) {
        static constexpr double kPipMoveThreshold = 16.0;  // ~8dp
        double dx = mSlots[slot].x - mPipPressX;
        double dy = mSlots[slot].y - mPipPressY;
        if (dx * dx + dy * dy > kPipMoveThreshold * kPipMoveThreshold) {
            mPipPendingMove = false;
            // Anchor at the press, not the current point: ash treats it as
            // where the finger grabbed the window.
            mFocusContainer->pipStartMove(static_cast<int32_t>(mPipPressX),
                                          static_cast<int32_t>(mPipPressY));
            // ash takes the gesture from here, so Android will never see an UP.
            sendTouchEvent(AMOTION_EVENT_ACTION_CANCEL);
            ALOGI("PIP touch → ash move: moved (%.1f,%.1f) from press at (%.0f,%.0f)",
                  dx, dy, mPipPressX, mPipPressY);
            return;
        }
        // Within the slop the events keep flowing to Android, exactly as on the
        // pointer path — a PIP window still hosts a live app, and swallowing
        // small movements would break anything it does with them.
    }

    // Overlay: pending tap → check if should convert to drag
    // Use larger threshold for touch (fingers are less precise than mouse)
    if (mOverlayPendingTap && mFocusWindow && activeCount() == 1) {
        static constexpr double kDragThreshold = 16.0;  // ~8dp touch slop
        double dx = mSlots[slot].x - mTapStartX;
        double dy = mSlots[slot].y - mTapStartY;
        if (dx * dx + dy * dy > kDragThreshold * kDragThreshold) {
            // Cancel the touch in Android and start drag
            sendTouchEvent(AMOTION_EVENT_ACTION_CANCEL);
            mOverlayPendingTap = false;
            ALOGVT("Overlay tap → drag: moved (%.1f,%.1f) from start", dx, dy);
            // Fall through to drag handling below
        } else {
            return;  // Within tap zone, ignore motion
        }
    }

    // Overlay drag: move the window instead of sending touch to Android
    // Use screen coords (bounds + surfaceLocal) which are stable after moveTo
    if (mDraggingOverlay && activeCount() == 1) {
        std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
        if (!mFocusWindow) return;  // window destroyed
        double screenX = mFocusWindow->boundsX() + mSlots[slot].x;
        double screenY = mFocusWindow->boundsY() + mSlots[slot].y;
        int32_t newX = mDragStartBoundsX + static_cast<int32_t>(screenX - mDragStartScreenX);
        int32_t newY = mDragStartBoundsY + static_cast<int32_t>(screenY - mDragStartScreenY);
        ALOGVT("Overlay drag: screen=(%.0f,%.0f) startScreen=(%.0f,%.0f) -> (%d,%d)",
               screenX, screenY, mDragStartScreenX, mDragStartScreenY, newX, newY);
        mFocusWindow->moveTo(newX, newY);
        return;  // consumed by drag
    }

    mPendingMotion = true;
}

void WaylandTouchHandler::onFrame() {
    if (!mFocusContainer && !mFocusWindow) return;

    // During overlay drag, only process UP to end the drag — skip motion/down
    if (mDraggingOverlay) {
        std::lock_guard<std::recursive_mutex> lock(mFocusMutex);
        if (!mFocusWindow) {
            mDraggingOverlay = false;
            mOverlayPendingTap = false;
            mPendingAction = -1;
            mPendingMotion = false;
            return;
        }
        if (mPendingAction >= 0) {
            int maskedAction = mPendingAction & AMOTION_EVENT_ACTION_MASK;
            if (maskedAction == AMOTION_EVENT_ACTION_UP) {
                ALOGVT("Overlay touch drag end: bounds=(%d,%d)",
                       mFocusWindow->boundsX(), mFocusWindow->boundsY());
                mDraggingOverlay = false;
                mOverlayPendingTap = false;
                // Free all slots
                for (int i = 0; i < kMaxTouchSlots; i++) {
                    if (mSlots[i].active) {
                        if (mStylusHandler) mStylusHandler->clearTouchId(mSlots[i].id);
                        freeSlot(i);
                    }
                }
                mFocusSurface = nullptr;
                mFocusContainer = nullptr;
                mFocusWindow = nullptr;
            }
            mPendingAction = -1;
        }
        mPendingMotion = false;
        return;
    }

    if (!mBridge) return;

    if (mPendingAction >= 0) {
        sendTouchEvent(mPendingAction);

        // Free slot after sending POINTER_UP or ACTION_UP
        int maskedAction = mPendingAction & AMOTION_EVENT_ACTION_MASK;
        if (maskedAction == AMOTION_EVENT_ACTION_POINTER_UP) {
            int index = (mPendingAction & AMOTION_EVENT_ACTION_POINTER_INDEX_MASK)
                        >> AMOTION_EVENT_ACTION_POINTER_INDEX_SHIFT;
            if (mStylusHandler) mStylusHandler->clearTouchId(mSlots[index].id);
            freeSlot(index);
        } else if (maskedAction == AMOTION_EVENT_ACTION_UP) {
            // Free all remaining slots
            for (int i = 0; i < kMaxTouchSlots; i++) {
                if (mSlots[i].active) {
                    if (mStylusHandler) mStylusHandler->clearTouchId(mSlots[i].id);
                    freeSlot(i);
                }
            }
            mFocusSurface = nullptr;
            mFocusContainer = nullptr;
            mFocusWindow = nullptr;
            // Last finger up: whatever this gesture was, it was not a PIP move.
            mPipPendingMove = false;
        }

        mPendingAction = -1;
        mPendingMotion = false;
    } else if (mPendingMotion) {
        sendTouchEvent(AMOTION_EVENT_ACTION_MOVE);
        mPendingMotion = false;
    }
}

void WaylandTouchHandler::onCancel() {
    ALOGVT("Touch cancel");
    mPipPendingMove = false;
    if (mBridge && (mFocusContainer || mFocusWindow) && activeCount() > 0) {
        sendTouchEvent(AMOTION_EVENT_ACTION_CANCEL);
    }

    for (int i = 0; i < kMaxTouchSlots; i++) {
        if (mSlots[i].active) freeSlot(i);
    }
    mPendingAction = -1;
    mPendingMotion = false;
    mDraggingOverlay = false;
    mOverlayPendingTap = false;
    mFocusSurface = nullptr;
    mFocusContainer = nullptr;
    mFocusWindow = nullptr;
}

} // namespace android

#pragma clang diagnostic pop

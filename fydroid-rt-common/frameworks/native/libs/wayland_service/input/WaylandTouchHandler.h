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
#include <mutex>

struct wl_seat;
struct wl_surface;
struct wl_touch;

namespace android {

class InputBridge;
class WaylandLayerContainer;
class WaylandStylusHandler;
class WaylandWindow;

static constexpr int kMaxTouchSlots = 16;

// Handles wl_touch events and writes BridgeInputEvent structs to an
// InputBridge pipe. Supports multi-touch with up to 16 simultaneous
// contact points. Touch coordinates are converted from surface-local
// to absolute display coordinates via the focus container's frame.
class WaylandTouchHandler {
public:
    WaylandTouchHandler();
    ~WaylandTouchHandler();

    WaylandTouchHandler(const WaylandTouchHandler&) = delete;
    WaylandTouchHandler& operator=(const WaylandTouchHandler&) = delete;

    bool initialize(wl_seat* seat, InputBridge* bridge, float scaleFactor = 1.0f);

    // Stylus handler integration
    void setStylusHandler(WaylandStylusHandler* handler) { mStylusHandler = handler; }
    wl_touch* wlTouch() const { return mTouch; }

    // Clear focus if the given window is being destroyed
    void clearFocusIfWindow(WaylandWindow* window);

    // Same, for the task container behind the focus surface. Touch focus on a
    // task lives in mFocusContainer with mFocusWindow left null, so
    // clearFocusIfWindow() never matched it — a finger down on a task whose
    // container was then destroyed left every later touch reading its frame,
    // input scale and PIP state out of freed memory.
    void clearFocusIfContainer(WaylandLayerContainer* container);

    // Wayland event handlers (called from static listeners)
    void onDown(uint32_t serial, uint32_t time, wl_surface* surface,
                int32_t id, int32_t x, int32_t y);      // wl_fixed_t
    void onUp(uint32_t serial, uint32_t time, int32_t id);
    void onMotion(uint32_t time, int32_t id, int32_t x, int32_t y);  // wl_fixed_t
    void onFrame();
    void onCancel();

private:
    struct TouchPoint {
        int32_t id = -1;    // Wayland touch ID, -1 = unused
        double x = 0.0;
        double y = 0.0;
        // Subtracted from the down surface's coordinates: non-zero only on a
        // modal's dim, whose points are relative to the dialog card.
        double originX = 0.0;
        double originY = 0.0;
        bool active = false;
    };

    int allocateSlot(int32_t id);
    int findSlot(int32_t id) const;
    void freeSlot(int index);
    int activeCount() const;
    void sendTouchEvent(int32_t action);
    int64_t now() const;
    // Reset every piece of state that refers to the focused surface, releasing
    // any live touch slots with it. Shared by both clearFocusIf*() paths so
    // neither can drift from the other. Caller holds mFocusMutex.
    void dropFocus();

    wl_touch* mTouch = nullptr;
    InputBridge* mBridge = nullptr;
    WaylandStylusHandler* mStylusHandler = nullptr;
    float mScaleFactor = 1.0f;

    TouchPoint mSlots[kMaxTouchSlots];

    // Focus tracking (set on first touch down)
    wl_surface* mFocusSurface = nullptr;
    WaylandLayerContainer* mFocusContainer = nullptr;
    WaylandWindow* mFocusWindow = nullptr;  // standalone window (overlay, toast, etc.)

    // Frame batching: pending action to send on next frame event
    int32_t mPendingAction = -1;
    bool mPendingMotion = false;

    // Overlay interaction: tap detection + drag-to-move for standalone windows
    bool mDraggingOverlay = false;
    bool mOverlayPendingTap = false;         // true between down and up/drag
    double mTapStartX = 0.0, mTapStartY = 0.0;  // surface-local at first touch
    double mDragStartScreenX = 0.0, mDragStartScreenY = 0.0;
    int32_t mDragStartBoundsX = 0, mDragStartBoundsY = 0;

    // PIP: the same tap-or-drag split, for a task container rather than a
    // standalone window — but the drag belongs to ash, not to us, so there is
    // no "dragging" state to hold. Once start_move goes out ash takes the
    // gesture and the next thing we hear is drag_started/bounds/drag_finished.
    // Single finger only.
    bool mPipPendingMove = false;                 // true between down and move/up
    double mPipPressX = 0.0, mPipPressY = 0.0;    // surface-local at first touch

    // Protects mFocusWindow against concurrent destruction from SF thread.
    // Recursive because sendTouchEvent (locked) is called from onUp (locked).
    std::recursive_mutex mFocusMutex;
};

} // namespace android

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
#include <mutex>

struct wl_pointer;
struct wl_seat;
struct wl_surface;
struct zcr_cursor_shapes_v1;

namespace android {

class InputBridge;
class WaylandLayerContainer;
class WaylandStylusHandler;
class WaylandWindow;

// Handles wl_pointer events and writes BridgeInputEvent structs to an
// InputBridge pipe. Motion uses absolute coordinates. Button and scroll
// events include button state tracking.
class WaylandPointerHandler {
public:
    WaylandPointerHandler();
    ~WaylandPointerHandler();

    WaylandPointerHandler(const WaylandPointerHandler&) = delete;
    WaylandPointerHandler& operator=(const WaylandPointerHandler&) = delete;

    // Bind wl_pointer from seat. bridge must outlive this handler.
    // scaleFactor is the device scale factor (DIP → pixels).
    bool initialize(wl_seat* seat, InputBridge* bridge, float scaleFactor = 1.0f);

    // Stylus handler integration
    void setStylusHandler(WaylandStylusHandler* handler) { mStylusHandler = handler; }
    wl_pointer* wlPointer() const { return mPointer; }

    // Clear focus if the given window is being destroyed
    void clearFocusIfWindow(WaylandWindow* window);

    // Same, for the task container behind the focus surface. A task's pointer
    // focus is held in mFocusContainer with mFocusWindow left null, so
    // clearFocusIfWindow() cannot reach it — when a task container was
    // destroyed the cursor kept a pointer into freed memory and the next motion
    // event read its frame and input scale out of it.
    void clearFocusIfContainer(WaylandLayerContainer* container);

    // The surface the pointer is currently over, or null. This is the surface
    // a pointer lock has to be requested on -- exo only grants the constraint
    // on a surface whose toplevel is the active window.
    wl_surface* focusSurface();

    // Pointer capture (Android) / pointer lock (Wayland) is active. While it
    // is, the mouse reports on the relative device instead of the absolute
    // one, which is the same switch stock CursorInputMapper makes between
    // MODE_POINTER and MODE_POINTER_RELATIVE: an app in captured mode reads
    // its buttons and wheel from the captured stream, so leaving them on the
    // absolute device would hit-test them against the frozen cursor position
    // and deliver them to the window as ordinary taps.
    void setPointerCaptured(bool captured);

    // zcr_cursor_shapes_v1, used to put the arrow back after capture hid it.
    // Without it the cursor is never hidden at all: wl_pointer.set_cursor(NULL)
    // is sticky (exo's SetFocus explicitly does not reset the cursor), so
    // hiding with no way to restore would lose the cursor over every ARC
    // surface for the rest of the session.
    void setCursorShapes(zcr_cursor_shapes_v1* shapes) { mCursorShapes = shapes; }

    // Wayland event handlers (called from static listeners)
    void onEnter(uint32_t serial, wl_surface* surface,
                 int32_t surfaceX, int32_t surfaceY);  // wl_fixed_t
    void onLeave(uint32_t serial, wl_surface* surface);
    void onMotion(uint32_t time, int32_t surfaceX, int32_t surfaceY);
    void onButton(uint32_t serial, uint32_t time, uint32_t button, uint32_t state);
    void onAxis(uint32_t time, uint32_t axis, int32_t value);  // wl_fixed_t
    void onFrame();
    void onAxisSource(uint32_t axisSource);
    void onAxisStop(uint32_t time, uint32_t axis);
    void onAxisDiscrete(uint32_t axis, int32_t discrete);

private:
    void sendMotionEvent(int32_t action, int32_t actionButton = 0);
    void writeMotionEvent(int32_t action, int32_t actionButton, int32_t deviceType,
                          int32_t displayId, float absX, float absY, bool isStylus);
    int64_t now() const;
    // Reset every piece of state that refers to the focused surface. Shared by
    // both clearFocusIf*() paths so neither can drift from the other. Caller
    // holds mFocusMutex.
    void dropFocus();
    void closeHover();

    wl_pointer* mPointer = nullptr;
    InputBridge* mBridge = nullptr;
    WaylandStylusHandler* mStylusHandler = nullptr;

    wl_surface* mFocusSurface = nullptr;
    double mCursorX = 0.0;
    double mCursorY = 0.0;
    // Subtracted from the focused surface's coordinates: non-zero only on a
    // modal's dim, whose positions are relative to the dialog card. Refreshed
    // on every motion: the dim moves the card when it grows to the output.
    double mFocusOriginX = 0.0;
    double mFocusOriginY = 0.0;
    bool mHasPosition = false;
    // A HOVER_ENTER went to Android and no HOVER_EXIT has followed yet.
    bool mHoverOpen = false;
    // Where the open hover lives: InputDispatcher tracks a hover per device
    // and per display, so it has to be closed on the same one.
    int32_t mHoverDeviceType = 0;
    int32_t mHoverDisplayId = 0;
    float mHoverX = 0.0f;
    float mHoverY = 0.0f;

    // Device scale factor for DIP → pixel conversion
    float mScaleFactor = 1.0f;

    // Container that owns the current focus surface (nullptr if unknown)
    WaylandLayerContainer* mFocusContainer = nullptr;
    WaylandWindow* mFocusWindow = nullptr;  // standalone window (overlay, toast, etc.)

    // Button state tracking
    int32_t mButtonState = 0;   // AMOTION_EVENT_BUTTON_* bitmask

    // Set by WaylandInputManager from the wayland event thread, read on the
    // same thread by the event handlers -- atomic only because the input
    // manager's own caller may differ.
    std::atomic<bool> mPointerCaptured{false};

    // Serial of the last wl_pointer.enter — wl_pointer.set_cursor needs one.
    uint32_t mEnterSerial = 0;
    zcr_cursor_shapes_v1* mCursorShapes = nullptr;  // not owned

    // Overlay interaction: click detection + drag-to-move for standalone windows
    bool mDraggingOverlay = false;
    bool mOverlayPendingClick = false;       // true between press and release/drag
    double mClickStartX = 0.0, mClickStartY = 0.0;  // surface-local at press
    double mDragStartScreenX = 0.0, mDragStartScreenY = 0.0;
    int32_t mDragStartBoundsX = 0, mDragStartBoundsY = 0;

    // PIP: the same press-or-drag split, for a task container rather than a
    // standalone window — but the drag itself belongs to ash, not to us, so
    // there is no "dragging" state to hold. Once start_move goes out, ash grabs
    // the pointer and the next thing we hear is the drag_started/bounds/
    // drag_finished sequence on the wayland stream.
    bool mPipPendingMove = false;                   // true between press and move/release
    double mPipPressX = 0.0, mPipPressY = 0.0;      // surface-local at press

    // Protects mFocusWindow against concurrent destruction from SF thread
    std::recursive_mutex mFocusMutex;

    // Pending scroll state (batched until frame event)
    int32_t mPendingDiscreteVertical = 0;
    int32_t mPendingDiscreteHorizontal = 0;
    bool mHasDiscreteInFrame = false;
    double mAccumScrollY = 0.0;
    double mAccumScrollX = 0.0;
    bool mHasPendingScroll = false;

    // Seat version (determines if frame events are available)
    uint32_t mSeatVersion = 0;
};

} // namespace android

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
#include <memory>
#include <mutex>

struct wl_seat;
struct wl_surface;
struct zcr_stylus_v2;
struct zwp_relative_pointer_manager_v1;
struct zwp_pointer_constraints_v1;
struct zcr_gaming_input_v2;
struct zcr_cursor_shapes_v1;
struct zcr_keyboard_configuration_v1;
struct zcr_keyboard_extension_v1;
struct wl_display;

namespace android {

class InputBridge;
class WaylandPointerHandler;
class WaylandWindow;
class WaylandKeyboardHandler;
class WaylandTouchHandler;
class WaylandStylusHandler;
class WaylandRelativePointerHandler;
class WaylandGamepadHandler;
class WaylandLayerContainer;

// Orchestrates input handling for Wayland seat capabilities.
// Owns the InputBridge (pipe writer) and passes it to handlers.
class WaylandInputManager {
public:
    WaylandInputManager();
    ~WaylandInputManager();

    WaylandInputManager(const WaylandInputManager&) = delete;
    WaylandInputManager& operator=(const WaylandInputManager&) = delete;

    // Called after wl_seat is bound and capabilities are known.
    // Creates InputBridge and Wayland event handlers.
    // scaleFactor is the device scale factor (DIP → pixels).
    void initialize(wl_seat* seat, uint32_t capabilities, float scaleFactor = 1.0f);

    // Called after initialize() when protocol globals are available.
    // Creates stylus, relative pointer, and gamepad handlers.
    void initializeExtended(zcr_stylus_v2* stylus,
                            zwp_relative_pointer_manager_v1* relPtrMgr,
                            zwp_pointer_constraints_v1* ptrConstraints,
                            zcr_gaming_input_v2* gamingInput,
                            wl_seat* seat,
                            zcr_keyboard_configuration_v1* keyboardConfig,
                            zcr_cursor_shapes_v1* cursorShapes);

    // Start acking keys to exo (see WaylandKeyboardHandler::ackKey).
    void attachKeyboardExtension(zcr_keyboard_extension_v1* extension,
                                 wl_display* display);

    // Pass a pipe fd (from InputReaderArc via binder) to the InputBridge.
    void setInputBridgePipeFd(int fd);

    // Android turned pointer capture on or off (View.requestPointerCapture()
    // -> InputDispatcher -> InputReaderArc -> IWaylandService.requestPointer-
    // Capture). Called from a binder thread: it only records the wish, because
    // the wayland request that implements it creates a proxy and a listener
    // and so has to leave from the event thread.
    void requestPointerCapture(bool enable);

    // Apply a pending capture request. Called from the wayland event thread,
    // which is also the thread the lock's own state events arrive on.
    void processPendingPointerCapture();

    // Clear dangling focus pointers in input handlers when a window is
    // about to be destroyed. Must be called before the WaylandWindow is freed.
    void clearFocusIfWindow(WaylandWindow* window);

    // The same for a task container. Pointer and touch focus on a task is held
    // as a WaylandLayerContainer* with the window pointer left null, so
    // clearFocusIfWindow() cannot reach it. Must be called before the container
    // is freed — WaylandLayerContainer's destructor does it for every teardown
    // path.
    void clearFocusIfContainer(WaylandLayerContainer* container);

    // Update output info (called when display transform changes).
    void updateOutputInfo(int32_t transform);
    int32_t outputTransform() const { return mOutputTransform; }

    // Singleton accessor for binder service to reach the manager.
    static WaylandInputManager* instance();

private:
    // Emit a BRIDGE_EVENT_DEVICE_ADDED/REMOVED so InputReaderArc can build
    // its device list from real seat capabilities.
    void publishDeviceEvent(int32_t bridgeEventType, int32_t bridgeDeviceType,
                            int32_t gamepadId = 0);
    // Replay the current singleton device set (called when the pipe fd
    // arrives so any earlier publishes — which dropped on a fd-less bridge
    // — are recovered).
    void replaySingletonDevices();

    std::unique_ptr<InputBridge> mBridge;
    std::unique_ptr<WaylandPointerHandler> mPointer;
    std::unique_ptr<WaylandKeyboardHandler> mKeyboard;
    std::unique_ptr<WaylandTouchHandler> mTouch;
    std::unique_ptr<WaylandStylusHandler> mStylus;
    std::unique_ptr<WaylandRelativePointerHandler> mRelativePointer;
    std::unique_ptr<WaylandGamepadHandler> mGamepad;

    // Bitmask of active singleton BridgeDeviceTypes (excluding gamepad).
    // Bit (1 << BridgeDeviceType) is set when the corresponding handler is
    // initialized; cleared if it ever tears down.
    uint32_t mActiveSingletons = 0;

    // Display output transform (WL_OUTPUT_TRANSFORM_*) for coordinate rotation
    int32_t mOutputTransform = 0;

    // Device scale factor, kept from initialize() so initializeExtended() can
    // give it to the relative-pointer handler (see setScaleFactor there).
    float mScaleFactor = 1.0f;

    // Pointer capture. mCaptureRequested is what Android asked for;
    // mCaptureActive is what exo has confirmed and what the pointer handler
    // has been told about. The mutex also serialises processPendingPointer-
    // Capture(), which every display's event thread calls.
    std::mutex mCaptureMutex;
    bool mCaptureRequested = false;
    bool mCaptureActive = false;
    // The surface the outstanding lock was asked on, so a request that exo has
    // not granted can be moved to the surface the pointer is on now.
    wl_surface* mCaptureSurface = nullptr;

    static WaylandInputManager* sInstance;
};

} // namespace android

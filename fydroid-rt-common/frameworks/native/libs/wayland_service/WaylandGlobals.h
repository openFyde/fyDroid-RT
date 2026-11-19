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

#pragma once

#include <atomic>
#include <functional>
#include <memory>
#include <mutex>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <vector>

#include "display/WaylandDisplay.h"
#include "window/WaylandWindowTypes.h"

struct wl_display;
struct wl_output;
struct wl_registry;
struct wl_surface;
struct wl_compositor;
struct wl_subcompositor;
struct wp_viewporter;
struct wl_seat;
struct zcr_keyboard_configuration_v1;
struct zcr_keyboard_extension_v1;
struct zwp_linux_dmabuf_v1;
struct zcr_remote_shell_v2;
struct zcr_vsync_feedback_v1;
struct zaura_output_manager_v2;
struct zaura_shell;
struct surface_augmenter;
struct zcr_stylus_v2;
struct zwp_relative_pointer_manager_v1;
struct zwp_pointer_constraints_v1;
struct wl_data_device_manager;
struct zcr_gaming_input_v2;
struct zcr_cursor_shapes_v1;
struct zcr_secure_output_v1;

namespace android {

class WaylandClient;
class WaylandOutput;

// Handles wl_registry global binding and output manager events.
// Creates WaylandDisplay instances (with LayerManager + VSync) per wl_output.
class WaylandGlobals {
public:
    explicit WaylandGlobals(wl_display* display);
    ~WaylandGlobals();

    // Singleton accessor — set in finalize(), cleared in destructor.
    // Used by CompositionEngineArc to reach container list.
    static WaylandGlobals* instance() { return sInstance; }

    WaylandGlobals(const WaylandGlobals&) = delete;
    WaylandGlobals& operator=(const WaylandGlobals&) = delete;

    // Bind the registry and add listener.
    void bindRegistry();

    // Validate required globals and create WaylandDisplay instances.
    // Call after roundtrips have completed. Pass the owning WaylandClient
    // so displays/layers can access globals through it.
    bool finalize(WaylandClient* client);

    // Bound globals
    wl_compositor* compositor() const { return mCompositor; }
    wl_subcompositor* subcompositor() const { return mSubcompositor; }
    // Optional. Only the drag-resize mask uses it (a fixed buffer Chrome
    // stretches to the window, see WaylandLayerContainer); null disables that.
    wp_viewporter* viewporter() const { return mViewporter; }
    zcr_remote_shell_v2* remoteShell() const { return mRemoteShell; }
    zwp_linux_dmabuf_v1* dmabuf() const { return mDmabuf; }
    zcr_vsync_feedback_v1* vsyncFeedback() const { return mVsyncFeedback; }
    zaura_shell* auraShell() const { return mAuraShell; }
    surface_augmenter* surfaceAugmenter() const { return mSurfaceAugmenter; }
    wl_seat* seat() const { return mSeat; }
    uint32_t seatCapabilities() const { return mSeatCapabilities; }
    zcr_stylus_v2* stylus() const { return mStylus; }
    // Chrome OS extension that reports whether a PHYSICAL keyboard is in use.
    zcr_keyboard_configuration_v1* keyboardConfiguration() const {
        return mKeyboardConfiguration;
    }
    // Chrome OS extension that lets us ack each key as handled or not, so ash
    // runs its accelerators only for keys Android did not use.
    zcr_keyboard_extension_v1* keyboardExtension() const {
        return mKeyboardExtension;
    }
    zwp_relative_pointer_manager_v1* relativePointerManager() const { return mRelativePointerManager; }
    zwp_pointer_constraints_v1* pointerConstraints() const { return mPointerConstraints; }
    zcr_gaming_input_v2* gamingInput() const { return mGamingInput; }
    zcr_cursor_shapes_v1* cursorShapes() const { return mCursorShapes; }
    wl_data_device_manager* dataDeviceManager() const { return mDataDeviceManager; }
    // Chrome's secure-output extension. A surface marked through this is kept
    // out of screenshots/casts (viz drops the quad from copy requests) and asks
    // ash for HDCP on the output. Null if the compositor doesn't advertise it —
    // callers must treat that as "cannot protect" (see WaylandLayerContainer::
    // setSecure), never as "no protection needed".
    zcr_secure_output_v1* secureOutput() const { return mSecureOutput; }

    // Display access
    WaylandDisplay* primaryDisplay() const;
    WaylandDisplay* display(size_t index) const;
    size_t displayCount() const;

    // Return the 0-based display index for a given wl_output, or -1 if not found.
    int32_t displayIndexForOutput(wl_output* output) const;

    // Registry callback (forwarded from static listener)
    void onRegistryGlobal(struct wl_registry* registry, uint32_t name,
                          const char* interface, uint32_t version);

    // Output manager event handlers (forwarded from static listener).
    // Each receives the output_name (wl_registry global name) for per-output routing.
    void onOutputManagerLogicalSize(uint32_t outputName, int32_t w, int32_t h);
    void onOutputManagerPhysicalSize(uint32_t outputName, int32_t w, int32_t h);
    void onOutputManagerDeviceScaleFactor(uint32_t outputName, uint32_t scaleBits);
    void onOutputManagerLogicalTransform(uint32_t outputName, int32_t transform);
    void onOutputManagerPanelTransform(uint32_t outputName, int32_t transform);
    void onOutputManagerWorkAreaInsets(uint32_t outputName, int32_t top, int32_t left,
                                       int32_t bottom, int32_t right);
    void onOutputManagerDisplayId(uint32_t outputName, uint32_t hi, uint32_t lo);
    void onOutputManagerLogicalPosition(uint32_t outputName, int32_t x, int32_t y);
    void onOutputManagerActivated(uint32_t outputName);
    void onOutputManagerDone();

    // Seat capabilities handler (forwarded from static listener)
    void onSeatCapabilities(uint32_t capabilities);

    // Remote shell event handlers
    void onRemoteShellDefaultScale(int32_t fixedScale);
    void onRemoteShellLayoutMode(uint32_t mode);

    // Default device scale factor from zcr_remote_shell_v2 (8.24 fixed point).
    // This is Chrome's effective scale including display zoom — used for
    // computing Android overrideDpi = defaultDeviceScaleFactor * 160.
    float defaultDeviceScaleFactor() const { return mDefaultDeviceScaleFactor; }

    // Layout mode from zcr_remote_shell_v2 / zaura_shell.
    ChromeLayoutMode layoutMode() const { return mLayoutMode; }

    // Surface activation handler (zaura_shell.activated).
    // Forwards to InputManager (setFocusedTask) and LayerManager (top layer).
    //
    // The focus hand-off is asynchronous (see WaylandTaskResizer::setFocusedTask
    // — doing it inline deadlocks SurfaceFlinger against system_server). Pass
    // awaitFocus=true from a path that is about to inject an input event into
    // the activated task, to wait, briefly and off the main thread, for the new
    // focus to reach the InputDispatcher first. Only WaylandTouchHandler::onDown
    // needs this today; it runs on the Wayland event thread.
    void onSurfaceActivated(wl_surface* gained, wl_surface* lost,
                            bool awaitFocus = false);

    // Push "ARC does / does not hold Chrome OS activation" to Android WM, once
    // per transition. See WaylandTaskResizer::setArcActivated.
    void setArcActivationState(bool activated);

    // Android's DIM_BEHIND amount for the dialog on screen, 0 if none — what a
    // modal window paints as the dim around its card — plus the on-screen
    // rectangle of the window hosting it, which is the only part of the dim
    // that takes clicks (an empty rect: only the card does). Written once per
    // frame by SurfaceFlinger's composition; read by a modal when it is
    // created, so a dialog that appears after its dim started does not wait
    // for a change. setModalScrim returns whether anything moved.
    bool setModalScrim(float alpha, int32_t x, int32_t y, int32_t w, int32_t h) {
        // Every exchange runs: they all have to be written, whether or not an
        // earlier one already showed the state moved.
        bool changed = mModalScrimAlpha.exchange(alpha) != alpha;
        changed |= mModalScrimX.exchange(x) != x;
        changed |= mModalScrimY.exchange(y) != y;
        changed |= mModalScrimW.exchange(w) != w;
        changed |= mModalScrimH.exchange(h) != h;
        return changed;
    }
    float modalScrimAlpha() const { return mModalScrimAlpha.load(); }
    int32_t modalScrimX() const { return mModalScrimX.load(); }
    int32_t modalScrimY() const { return mModalScrimY.load(); }
    int32_t modalScrimW() const { return mModalScrimW.load(); }
    int32_t modalScrimH() const { return mModalScrimH.load(); }

    // --- Notification display flag ---
    // Java calls setNotificationDisplayId() via binder BEFORE creating the
    // shared notification VirtualDisplay, passing the VD's pixel size. The flag
    // is consumed by the createVirtualDisplay() whose dimensions MATCH that
    // size — matching deterministically defeats the race where a task VD
    // created in the same window would otherwise steal a plain "next VD" flag.
    void setNotificationDisplayId(int32_t displayId, int32_t width, int32_t height);
    // Consume the notification flag iff (width,height) matches the armed notif
    // VD size. Returns true and clears the flag on a match.
    bool consumeNotificationFlagIfMatch(int32_t width, int32_t height);
    // Non-consuming peek: true iff the flag is armed AND (width,height) matches
    // the armed notif VD size. Used by SurfaceFlinger::acquireVirtualDisplay to
    // route the notification VD to a fixed HAL id (deterministic, never GPU).
    bool matchesNotificationSize(int32_t width, int32_t height) const;
    // True iff the notification flag is currently armed (a notification VD is
    // expected next). The armed size (below) is the VD's intended pixel size.
    bool isNotificationArmed() const { return mNextVirtualDisplayIsNotification.load(); }
    int32_t armedNotifWidth() const { return mNotifVdWidth.load(); }
    int32_t armedNotifHeight() const { return mNotifVdHeight.load(); }
    int32_t notifDisplayId() const { return mNotifDisplayId.load(); }

    // Reserved HAL virtual-display base id for the fixed notification display.
    // Far above SurfaceFlinger's generator range (0..maxVDs) and
    // CompositionEngineArc's task-VD ids (mNextVdBaseId starts at 1000), so it
    // can never collide with either.
    static constexpr uint32_t kNotificationHalVdId = 0x4E4F5449;  // 'NOTI'

    // --- Window caption colours ---
    //
    // ChromeOS themes its window title bars from the wallpaper, and ash will
    // not do that for an ARC caption on its own — it leaves those on a static
    // token until the client sends zaura_surface.set_frame_colors. So the
    // colours are derived on the Android side (ArcSystemUiService, which is
    // where Chrome's dynamic-colour seed lands) and pushed down here.

    // Store the palette Chrome frames its own windows with and re-assert it on
    // every live app window. Called on a binder thread — the application
    // itself is deferred to the Wayland event thread, which is the only one
    // that may touch surfaces (cf. WaylandDataDevice::processPending).
    void setFrameColors(uint32_t active, uint32_t inactive);

    // Override one task's caption with the colour its app asked for (a
    // TaskDescription colour). Pass active == 0 to drop the override and put
    // the task back on the ChromeOS header. Called on a binder thread; applied
    // on the event thread like setFrameColors above.
    void setTaskFrameColor(int32_t taskId, uint32_t active, uint32_t inactive);

    // Apply a pending setFrameColors() / setTaskFrameColor() to the windows
    // they affect. Called from the Wayland event loop; a no-op unless
    // something has changed since last time.
    void processPendingFrameColors();

    // The caption palette, for a window created after it arrived. False until
    // Android has pushed one, in which case a new window is left on ash's
    // default rather than being given a guess.
    bool hasFrameColors() const { return mHasFrameColors.load(); }
    uint32_t frameColorActive() const { return mFrameColorActive.load(); }
    uint32_t frameColorInactive() const { return mFrameColorInactive.load(); }

    // The app-declared caption colours for a task, if it has declared any.
    // Read when a window is created, since the TaskDescription that carries
    // them usually arrives before the window does.
    bool taskFrameColor(int32_t taskId, uint32_t* active, uint32_t* inactive) const;

    // How ArcAccessibilityHelperService names the surface an accessibility
    // window id belongs to. Wire values — the helper sends them; do not
    // renumber. A surface needs a name both sides already agree on, and each
    // window category has a different one:
    //
    //   kTask         the task id, for APP and MODAL containers. A dialog is
    //                 its own task in fydroid, so both are covered by this.
    //   kNotification the notification key, which the compositor holds in
    //                 WaylandNotificationWindow and the helper reads back off
    //                 the window's accessibilityTitle.
    //   kInputMethod  nothing: there is exactly one IME surface.
    //
    // TOAST, OVERLAY and task-less MODAL windows have no such name: they sit
    // on their own standalone virtual display and nothing an accessibility
    // service can see identifies which one. They are not addressed through
    // here at all — the composer reads their id straight off the layer
    // (METADATA_ACCESSIBILITY_ID, stamped by AccessibilityWindowManager), see
    // WaylandLayerContainer::setStandaloneAccessibilityId.
    enum class AccessibilityTarget : int32_t {
        kTask = 0,
        kNotification = 1,
        kInputMethod = 2,
    };

    // Record the Android accessibility window id of the window a surface is
    // showing, pushed by ArcAccessibilityHelperService on a binder thread. That
    // service is the only process that sees the accessibility window list, and
    // Chrome needs the id to attribute an accessibility event that carries no
    // task id — see WaylandAccessibilityId.h. `taskId` is meaningful for
    // kTask, `key` for kNotification. A negative windowId drops the mapping.
    //
    // Held as state, not just queued, for the same reason as the caption
    // colours: the helper names a surface as soon as Android reports a window
    // for it, which is normally before that surface exists.
    void setAccessibilityWindowId(AccessibilityTarget target, int32_t taskId,
                                  const std::string& key, int32_t windowId);

    // The accessibility window id recorded for a surface, or -1 if none. Read
    // when a window is created, to catch up on an id that arrived first.
    int32_t accessibilityWindowId(AccessibilityTarget target, int32_t taskId,
                                  const std::string& key) const;

    // Apply pending setAccessibilityWindowId() calls to the containers they
    // name. Called from the Wayland event loop, because the zaura request has
    // to leave from that thread. Same shape as processPendingFrameColors().
    void processPendingAccessibilityIds();

    // A task entered or left Android's WINDOWING_MODE_PINNED (PIP), pushed by
    // ArcWindowManagerService on a binder thread. Recorded here because the
    // window that has to become a ChromeOS PIP window may not exist yet — an
    // app can enter PIP as it launches — so the state has to outlive the
    // message and be readable at window-creation time, like the caption
    // colours above.
    // `l/t/r/b` is the pinned-task rectangle WM computed, in display pixels,
    // and is where the PIP window's SIZE comes from: ash's PipPositioner only
    // chooses where a PIP window sits and keeps the size the client reports.
    void setTaskPipState(int32_t taskId, bool inPip,
                         int32_t l, int32_t t, int32_t r, int32_t b);

    // Whether Android currently has this task in PIP.
    bool taskInPip(int32_t taskId) const;

    // WM's PIP rectangle for a task, in display pixels. False if not in PIP or
    // WM gave no usable bounds.
    bool taskPipBounds(int32_t taskId, int32_t* w, int32_t* h) const;

    // Apply pending setTaskPipState() calls to the containers they name.
    // Called from the Wayland event loop, because the requests it ends up
    // sending (zcr_remote_surface_v2_pip) must go out on the event thread,
    // while setTaskPipState arrives on a binder thread. A no-op unless
    // something changed. Same shape as processPendingFrameColors().
    void processPendingPipState();

    // The task ash has (trusted-)pinned for an ARC kiosk session, recorded
    // here rather than only on the container that observed the pin.
    //
    // A kiosk pin is announced exactly once, as the state_type_changed that
    // follows ArcWindowManagerService::maybePinKioskTask at launch. The
    // container that sees it does not live as long as the session: suspending
    // the board powers the display down, which tears the task's virtual
    // display and its whole layer/container/window stack down with it, and the
    // rebuild on resume produces a container whose mKioskPinned is false. No
    // second state change ever arrives to correct it — Android is still in
    // LOCK_TASK_MODE_PINNED and the task was never re-created, so there is
    // nothing for ash to re-announce — and the new window therefore takes
    // ash's caption-inset placement verbatim and comes back as an ordinary
    // captioned app. Measured on a kiosk board: every resume logged
    // "container created ... taskId=6" with no "state changed" behind it, and
    // left the app at (0,32) under a caption.
    //
    // Task-scoped rather than a session-wide "is kiosk" flag: only the one
    // task ArcWindowManagerService pins owns the screen. Dialogs and any later
    // task in the same session must keep their normal treatment.
    void setTaskKioskPinned(int32_t taskId, bool pinned);

    // Whether that task is the pinned kiosk one. Read at window-creation time,
    // the same way taskInPip() is.
    bool taskKioskPinned(int32_t taskId) const;

    // --- Hotplug callback (WaylandGlobals → WaylandHwc2Composer) ---
    using HotplugCallback = std::function<void(size_t displayIndex, bool connected)>;
    void setHotplugCallback(HotplugCallback cb) { mHotplugCallback = std::move(cb); }

    // --- Display-changed callback (WaylandGlobals → WaylandHwc2Composer) ---
    // Fired for every display after output-manager info has been applied, so
    // the composer can re-announce one whose size changed underneath SF.
    using DisplayChangedCallback = std::function<void(size_t displayIndex)>;
    void setDisplayChangedCallback(DisplayChangedCallback cb) {
        mDisplayChangedCallback = std::move(cb);
    }

    // Find a WaylandDisplay by its wl_registry global name.
    WaylandDisplay* findDisplayByGlobalName(uint32_t name) const;

    // Registry global remove handler (forwarded from static listener)
    void onRegistryGlobalRemove(uint32_t name);

    // Move `replacement` into the primary slot (mDisplays[0] / HWC display
    // id 1), migrating containers off the outgoing display and re-announcing
    // the id so SF rebuilds its primary DisplayDevice against the new panel.
    // Takes ownership; the outgoing display is destroyed off the event thread.
    void replacePrimaryDisplay(std::unique_ptr<WaylandDisplay> replacement);

    // Active output (target for new windows)
    uint32_t activeOutputName() const { return mActiveOutputName; }

private:
    // Property carrying the primary panel's fixed mount rotation, in degrees.
    // Read by the camera HAL (camera.bertha_v3) to express Chrome OS's
    // sensor_orientation in Android's natural-orientation frame.
    static constexpr const char* kPanelRotationProperty = "sys.fydroid.panel_rotation";

    // Publishes kPanelRotationProperty when the primary panel's mount rotation
    // becomes known or changes. No-op while the value is unchanged.
    void publishPanelRotation();

    // Called when a WaylandOutput receives mode/geometry changes
    void onOutputChanged(WaylandDisplay* display);

    wl_display* mWlDisplay;  // not owned
    wl_registry* mRegistry = nullptr;
    wl_compositor* mCompositor = nullptr;
    wl_subcompositor* mSubcompositor = nullptr;
    wp_viewporter* mViewporter = nullptr;
    zwp_linux_dmabuf_v1* mDmabuf = nullptr;
    zcr_remote_shell_v2* mRemoteShell = nullptr;
    zcr_vsync_feedback_v1* mVsyncFeedback = nullptr;
    zaura_shell* mAuraShell = nullptr;
    zaura_output_manager_v2* mOutputManager = nullptr;
    surface_augmenter* mSurfaceAugmenter = nullptr;
    wl_seat* mSeat = nullptr;
    zcr_keyboard_configuration_v1* mKeyboardConfiguration = nullptr;
    zcr_keyboard_extension_v1* mKeyboardExtension = nullptr;
    uint32_t mSeatCapabilities = 0;
    zcr_stylus_v2* mStylus = nullptr;
    zwp_relative_pointer_manager_v1* mRelativePointerManager = nullptr;
    zwp_pointer_constraints_v1* mPointerConstraints = nullptr;
    zcr_gaming_input_v2* mGamingInput = nullptr;
    zcr_cursor_shapes_v1* mCursorShapes = nullptr;
    wl_data_device_manager* mDataDeviceManager = nullptr;
    zcr_secure_output_v1* mSecureOutput = nullptr;

    // From zcr_remote_shell_v2 events
    float mDefaultDeviceScaleFactor = 0.0f;
    ChromeLayoutMode mLayoutMode = ChromeLayoutMode::kClamshell;

    // Whether Chrome's activated window is one of ours. Written from whichever
    // Wayland event thread saw the change (and from the pointer/touch
    // handlers), so the read-modify-write that makes the push once-per-
    // transition has to be atomic. Starts true: ARC is the only thing on screen
    // while it boots, and the first activation event corrects it either way.
    std::atomic<bool> mArcActivated{true};
    std::atomic<float> mModalScrimAlpha{0.0f};
    std::atomic<int32_t> mModalScrimX{0}, mModalScrimY{0};
    std::atomic<int32_t> mModalScrimW{0}, mModalScrimH{0};

    // Pending outputs from registry binding (before finalize)
    struct PendingOutput {
        std::unique_ptr<WaylandOutput> output;
        uint32_t globalName = 0;  // wl_registry global name
    };
    std::vector<PendingOutput> mPendingOutputs;

    // Per-output accumulated output manager info, keyed by wl_registry global name.
    std::unordered_map<uint32_t, OutputManagerInfo> mPendingInfoPerOutput;

    // Legacy single pending info (for pre-finalize fallback)
    bool mHasPendingInfo = false;

    // Last value written to kPanelRotationProperty; -1 = never published.
    int32_t mPublishedPanelRotation = -1;

    // True after finalize() has run — new wl_output globals trigger runtime hotplug.
    bool mFinalized = false;

    // WaylandClient — stored after finalize for runtime display creation.
    WaylandClient* mFinalizedClient = nullptr;

    // Active output name (from zaura_output_manager_v2 activated event)
    uint32_t mActiveOutputName = 0;

    // Hotplug callback (notifies WaylandHwc2Composer of connect/disconnect)
    HotplugCallback mHotplugCallback;

    // Display-changed callback (notifies WaylandHwc2Composer after output info)
    DisplayChangedCallback mDisplayChangedCallback;

    // Created displays (one per wl_output)
    std::vector<std::unique_ptr<WaylandDisplay>> mDisplays;

    // True when the primary display's wl_output went away and no replacement
    // has arrived yet. SF must always have a primary display, so the dead
    // WaylandDisplay is kept in mDisplays[0] and the next wl_output to arrive
    // replaces it in place rather than being appended as a secondary.
    bool mPrimaryOrphaned = false;

    // Notification display flag (thread-safe: binder thread writes, HWC2 thread reads)
    std::atomic<bool> mNextVirtualDisplayIsNotification{false};
    std::atomic<int32_t> mNotifVdWidth{0};   // armed notif VD pixel size (for dim match)
    std::atomic<int32_t> mNotifVdHeight{0};
    std::atomic<int32_t> mNotifDisplayId{-1};  // Android display ID for input routing

    // Caption colours (binder thread writes, Wayland event thread reads/applies)
    std::atomic<uint32_t> mFrameColorActive{0};
    std::atomic<uint32_t> mFrameColorInactive{0};
    std::atomic<bool> mHasFrameColors{false};
    std::atomic<bool> mFrameColorsDirty{false};

    // Per-task caption overrides. Held as state, not just a queue: an app
    // reports its TaskDescription before the task's window is created, so a
    // drain-and-discard queue loses the colour every time (the container is
    // not there yet to receive it) and the window comes up on the header.
    // The map is what a newly created window reads; the queue only carries
    // changes to windows that already exist. Pruned when Android reports the
    // task gone, which it does by pushing a zero colour.
    struct TaskFrameColor {
        uint32_t active;
        uint32_t inactive;
    };
    mutable std::mutex mTaskFrameColorMutex;
    std::unordered_map<int32_t, TaskFrameColor> mTaskFrameColors;
    std::vector<int32_t> mPendingTaskFrameColors;

    // Android accessibility window ids, from ArcAccessibilityHelperService.
    // Same state-plus-queue shape as the caption colours above, and for the
    // same reason. One map per way of naming a surface (AccessibilityTarget).
    struct PendingAccessibilityId {
        AccessibilityTarget target;
        int32_t taskId;
        std::string key;
    };
    mutable std::mutex mA11yIdMutex;
    std::unordered_map<int32_t, int32_t> mTaskA11yIds;
    std::unordered_map<std::string, int32_t> mNotifA11yIds;
    int32_t mImeA11yId = -1;
    std::vector<PendingAccessibilityId> mPendingA11yIds;

    // Tasks Android currently has in PIP. A set rather than a single id even
    // though WM allows only one pinned task: the enter for a replacing PIP can
    // arrive before the leave for the one it replaced, and a set cannot be left
    // holding the wrong task by that ordering.
    mutable std::mutex mTaskPipMutex;
    std::unordered_set<int32_t> mPipTasks;
    std::vector<int32_t> mPendingPipTasks;
    struct PipBounds { int32_t w, h; };
    std::unordered_map<int32_t, PipBounds> mPipBounds;

    // Tasks pinned as an ARC kiosk. A set for the same reason mPipTasks is
    // one, and because it costs nothing: WM pins a single kiosk task, but
    // nothing here needs to assume that.
    mutable std::mutex mKioskTaskMutex;
    std::unordered_set<int32_t> mKioskTasks;

    static WaylandGlobals* sInstance;
};

} // namespace android

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
#include <memory>
#include <mutex>
#include <string>
#include <vector>

#include <utils/StrongPointer.h>
#include <utils/Timers.h>

#include "../hwc2/WaylandTaskResizer.h"
#include "WaylandPipController.h"
#include "../window/WaylandWindow.h"
#include "../window/WaylandWindowTypes.h"

struct wl_buffer;
struct wl_callback;
struct wl_output;
struct wp_viewport;
struct zcr_security_v1;

namespace android {

class GraphicBuffer;
class WaylandClient;
class WaylandLayerDrmBufferManager;

// An origin-and-extent rectangle in an output's PIXEL coordinate space — the
// space zcr_remote_surface_v2_set_bounds_in_output takes ("Bounds are set in
// pixels, and should not be scaled", zcr_remote_shell_impl.cc). Deliberately
// not WaylandLayerContainer::Rect, which is a left/top/right/bottom box used
// zero-origin for Android task frames.
struct OutputRect {
    int32_t x = 0, y = 0, w = 0, h = 0;
    bool operator==(const OutputRect& o) const {
        return x == o.x && y == o.y && w == o.w && h == o.h;
    }
    bool operator!=(const OutputRect& o) const { return !(*this == o); }
};

// A container maps one Android task (or standalone layer) to one Chrome window.
// Owns the WaylandWindow (zcr_remote_surface_v2 or toast/ime surface).
//
// All presentation flows through presentComposedBuffer() — GPU-composed
// buffers from HWC virtual displays. No per-layer subsurfaces.
//
// The window is created lazily via ensureWindowForCategory(). Task containers
// create APP or DIALOG windows; standalone containers create INPUT_METHOD,
// TOAST, OVERLAY, or NOTIFICATION windows.
class WaylandLayerContainer : public WaylandWindow::EventListener {
public:
    struct Rect {
        int32_t left = 0, top = 0, right = 0, bottom = 0;
    };

    // Snap state carried across a container recreation. An app that does not
    // handle the orientation configuration change is relaunched on every
    // rotation: its virtual display, container and remote surface are torn down
    // and rebuilt, while ash keeps the *task* snapped and re-applies the snap to
    // the new surface. A container that comes up not knowing this is a fresh
    // unsnapped tablet window and asks Chrome to maximize it — see
    // WaylandLayerManager::destroyContainer / registerContainer.
    struct SnapCarry {
        bool snapped = false;
        Rect bounds;
    };
    SnapCarry snapCarry() const { return {mSnapped, mSnappedBounds}; }
    void restoreSnapCarry(const SnapCarry& carry);

    // A carried snap is provisional until Chrome re-states it. Between the
    // window being mapped and that reason 3/4, Chrome sends the transient
    // rectangles a freshly mapped window always gets (the caption-inset one,
    // the pre-snap one) as plain moves — and the reason-5 drag-out test in
    // onBoundsChanged would read the first of those as leaving the split.
    static constexpr int64_t kSnapProvisionalNs = 3LL * 1000 * 1000 * 1000;
    int64_t mSnapProvisionalUntilNs = 0;
    bool snapIsProvisional() const;

    // client: Wayland globals (not owned — outlives this container)
    // output: per-display wl_output (not owned)
    WaylandLayerContainer(WaylandClient* client, wl_output* output,
                          uint64_t containerLayerId, int32_t taskId,
                          const Rect& frame);
    ~WaylandLayerContainer();

    WaylandLayerContainer(const WaylandLayerContainer&) = delete;
    WaylandLayerContainer& operator=(const WaylandLayerContainer&) = delete;

    int32_t taskId() const { return mTaskId; }

    // Returns true if the container was recently restored from minimized state.
    // Used to delay setFocusedTask during Chrome's restore animation.
    bool wasRecentlyMinimized() const;
    uint64_t containerLayerId() const { return mContainerLayerId; }
    int32_t sessionId() const { return mSessionId; }
    void setSessionId(int32_t sessionId) { mSessionId = sessionId; }
    void setIsGame(bool isGame);
    bool isGame() const { return mIsGame; }
    // Mark this window's surface as carrying FLAG_SECURE content, so Chrome
    // keeps it out of screenshots and casts. Latched on: a task that has ever
    // composited a secure layer stays protected for the life of the window,
    // because SF composites all of a task's layers into one buffer and the
    // secure layer can come and go (a login screen dismissed and re-shown)
    // faster than we could safely unprotect and reprotect the surface.
    void setSecure(bool secure);
    bool isSecure() const { return mSecure; }
    bool isGameFullScreen() const { return mGameFullScreen; }

    // Called when this task gains or loses Chrome focus. A focused fullscreen
    // game asks Chrome to auto-hide the shelf so it owns the whole screen;
    // anything else hands the shelf back.
    void onFocusChanged(bool focused);
    void setIsDialog(bool isDialog);
    bool isDialog() const { return mIsDialog; }
    const Rect& containerFrame() const { return mContainerFrame; }
    void setContainerFrame(const Rect& frame) { mContainerFrame = frame; }

    // Grow the Chrome window so it is at least as large as the Android content.
    // contentW/H are in primary-DSF pixels (SF display space, same space as
    // mContainerFrame). Grow-only: corrects the case where the activity laid
    // out larger than its host window (stale launch bounds), which otherwise
    // squishes the content. Shrinking stays owned by Chrome's geometry events.
    // Called from the SF composition thread via WaylandHwc2Composer.
    // Returns false only while there is no window to check against yet; a
    // request this container deliberately declines (dialog, game, drag, tablet)
    // still counts as handled.
    bool ensureWindowFitsContent(int32_t contentW, int32_t contentH);

    // Resize the frameless modal (dialog) window to exactly the composed card
    // and re-centre it on the output. cardW/H are in primary-DSF pixels — the
    // same space as mContainerFrame and the SF content footprint. Unlike an app
    // window, a modal's geometry is owned entirely by SF (Chrome sends no
    // geometry_changed echo), so the bounds set at window creation stay frozen
    // at whatever the card measured on its first frame. The dialog then grows
    // to its final size and the buffer overflows the (smaller) window, which
    // reads as a card offset from — and clipped by — its own shadow.
    // Called from the SF composition thread via WaylandHwc2Composer.
    // Returns true once the modal window exists and the size was applied.
    bool updateModalCardSize(int32_t cardW, int32_t cardH);
    // Android's DIM_BEHIND amount for the dialog on screen (0 = none) and the
    // part of the dim that takes clicks (the host app's window; empty = only
    // the card). A MODAL window draws the dim around its card; no-op for other
    // categories. Called from the SF composition thread via WaylandHwc2Composer.
    void setModalScrim(float alpha, int32_t x, int32_t y, int32_t w, int32_t h);
    bool isClosed() const { return mClosed; }
    WaylandWindowCategory windowCategory() const { return mWindowCategory; }
    void setNotifKey(const std::string& key) { mNotifKey = key; }
    const std::string& notifKey() const { return mNotifKey; }

    // Access the window (may be null if not yet created).
    WaylandWindow* window() const { return mWindow.get(); }

    // Create the Wayland window for the given category. Called from
    // WaylandHwc2Composer when a virtual display first needs to present.
    // Returns true if window exists (already created or newly created).
    bool ensureWindowForCategory(WaylandWindowCategory cat, uint64_t layerId);

    // Present a GPU-composed buffer with viewport crop (per-task GPU composition).
    // The buffer may be larger than the content — wp_viewport crops
    // (srcX, srcY, srcW, srcH) from the buffer and scales to (srcW, srcH).
    // Does NOT flush — caller must flush after all containers have presented.
    // Returns true if presented.
    // bufWidth/bufHeight are the wl_buffer's real pixel extent. They are what
    // the damage is stated in: an INT32_MAX "everything" rectangle is not safe
    // here, because wl_surface.damage is in SURFACE coordinates and the
    // compositor transforms it into buffer space by buffer_scale and the
    // viewport before applying it. Pass the real size and state it in buffer
    // coordinates where the compositor is new enough to accept that.
    bool presentComposedBuffer(wl_buffer* composedBuffer, int32_t bufWidth,
                               int32_t bufHeight);

    // Present a GPU-composed GraphicBuffer through this container: waits on
    // and consumes *acquireFence (set to -1), imports the DMA-BUF via the
    // buffer manager, marks it busy for release fencing, and presents via
    // presentComposedBuffer(). Does NOT flush. Returns true if presented.
    bool presentGraphicBuffer(WaylandLayerDrmBufferManager* bufMgr,
                              const sp<GraphicBuffer>& buffer,
                              int* acquireFence);

    // Drag-resize mask (see onDragStarted). SF answers the mask request with
    // the blurred snapshot: attach it, stretched to the current window by a
    // wp_viewport. Consumes acquireFence. Called on the SF composition thread;
    // false if the drag is already over or no mask is wanted any more.
    bool presentResizeMask(WaylandLayerDrmBufferManager* bufMgr,
                           const sp<GraphicBuffer>& mask, int32_t maskW,
                           int32_t maskH, int acquireFence);
    // SF latched an app window buffer at the size the drag resized the task
    // to: the next composed frame is real content, so it replaces the mask.
    void onResizeContentReady();

    // Rate-limited report of how many composed frames this container presented
    // versus dropped. Only speaks up while frames are actually being refused.
    void logPresentStats();

    // Apply this frame's dialog flag (from IsDialogTask layer metadata).
    // On the dialog's first detection, claims Android focus for the task —
    // Chrome may have activated the parent app before the modal surface
    // existed, and this counteracts that initial steal.
    void applyDialogState(bool isDialog);

    // Ask Chrome to activate this container's window. Used when a dialog that
    // displaced this task is destroyed — Chrome does not re-activate the task
    // by itself, and ash reads the shelf auto-hide flag off the active window.
    void requestActivation();

    // Carry the game's shelf auto-hide onto this (modal) window, so a dialog
    // taking activation over a fullscreen game does not reset the shelf.
    void inheritShelfAutoHide();

    // The app window a MODAL window belongs to (its zaura_surface). Set before
    // ensureWindowForCategory() it decides where the window is created; after
    // it, it re-parents the window. See WaylandModalWindow.
    void setModalParent(zaura_surface* parent);

    // Whether this container currently holds the shelf auto-hide assertion.
    bool shelfAutoHide() const { return mShelfAutoHide; }

    // Whether the layer manager last reported this task as focused.
    bool isFocused() const { return mFocused; }

    // Whether a surface-local touch point is in the top strip where the tablet
    // multitask menu gesture starts. Surface-local coordinates are buffer
    // pixels — DIP x the surface scale — so this converts with mScaleFactor,
    // which has a single writer, and compares in DIP.
    //
    // Height only, deliberately. ash's region is also bounded horizontally
    // (kHitRegionSize, centred), but reproducing that needs the window width,
    // and the only width to hand — mServerBoundsW — is written in DIP by
    // window_geometry_changed and in PIXELS by bounds_changed_in_output. In a
    // snapped half the pixel writer usually goes last, so a width-aware test
    // put the strip at the window's right edge instead of its centre. A taller
    // strip than needed costs a first tap; a misplaced one costs the split.
    bool inTabletMenuGestureStrip(double surfaceY) const;

    // Standalone containers: position the window at the given content
    // bounds on the output (no-op without a window).
    void updateStandaloneBounds(wl_output* output, int32_t left, int32_t top,
                                int32_t width, int32_t height);

    // IME containers: on-screen bounds and the Android display frame are set
    // separately — the surface is lifted clear of the shelf, but touch offsets
    // must keep using where Android actually put the window.
    void updateInputMethodBounds(wl_output* output, int32_t left, int32_t top,
                                 int32_t width, int32_t height,
                                 int32_t frameLeft, int32_t frameTop,
                                 int32_t contentTop,
                                 const std::vector<int32_t>& touchRects);

    // Toast containers: hand the WaylandToastWindow the raw screen + toast
    // size; the toast window owns its placement policy (centered, 4/5 down).
    void updateToastBounds(wl_output* output, int32_t screenW, int32_t screenH,
                           int32_t toastW, int32_t toastH);

    // Notification containers: set the Android display id (input routing)
    // and the layer's display frame on the WaylandNotificationWindow.
    void configureNotificationWindow(int32_t androidDisplayId,
                                     int32_t frameLeft, int32_t frameTop);
    // Per-frame display-frame update (position may change between frames).
    void updateNotificationFrame(int32_t frameLeft, int32_t frameTop);

    // Update output info. `workArea` is the output's work area in output
    // pixels — the rectangle ash clamps a maximized window into; see
    // allowedWindowRect().
    void updateOutputInfo(int32_t width, int32_t height, int32_t transform,
                          float scaleFactor, const OutputRect& workArea);

    // Update the wl_output pointer (for container migration on hotplug disconnect).
    void setOutput(wl_output* output) { mOutput = output; }

    // Apply the Chrome OS layout mode to this container's app window.
    // Tablet mode is a system-wide state — frame type goes to NONE and the
    // bounds become allowedWindowRect() (the work area, since a tablet window
    // is ash-maximized); clamshell restores NORMAL frame and saved bounds.
    void applyLayoutMode(ChromeLayoutMode mode, int32_t outputW, int32_t outputH);

    // Paint this task's caption in the ChromeOS header colours. A no-op for
    // anything that is not a decorated app window: a dialog task is a
    // frameless MODAL, and a window whose frame is currently NONE (tablet,
    // kiosk, game fullscreen) has no caption on screen to colour — it keeps
    // the value anyway, so switching back to a NORMAL frame shows it.
    //
    // This is the fallback half: it loses to a colour the app asked for.
    void applyFrameColors(uint32_t active, uint32_t inactive);

    // Paint this task's caption in the colour its app asked for, which wins
    // over the ChromeOS header above so the title bar reads as part of the
    // app. Pass active == 0 to drop the override and fall back again.
    void applyTaskFrameColor(uint32_t active, uint32_t inactive);

    // Publish the Android accessibility window id of the window this container
    // renders. Works for every category — see WaylandAccessibilityId.h.
    void applyAccessibilityId(int32_t windowId);

    // The accessibility window id of a STANDALONE window (toast, overlay,
    // task-less modal), read by the composer from the layer's
    // METADATA_ACCESSIBILITY_ID — which stock AccessibilityWindowManager
    // stamps on every window's SurfaceControl. These windows have no task
    // and no key, so ArcAccessibilityHelperService has no way to name them;
    // the layer itself is the only exact name. Remembered so a window created
    // after the metadata arrived still publishes it. Negative clears.
    void setStandaloneAccessibilityId(int32_t windowId);

    // Android put this task into (or took it out of) WINDOWING_MODE_PINNED.
    // Asks ash for a ChromeOS PIP window, or restores the window when PIP
    // ends. Called on the Wayland event thread from
    // WaylandGlobals::processPendingPipState.
    //
    // Latched in the controller even when there is no window yet: an app can
    // enter PIP while still launching, in which case the request is re-sent
    // from ensureWindow() once the surface exists.
    void applyPipState(bool inPip);

    // WM's pinned rectangle for this task, in display pixels. Pushed from
    // ArcWindowManagerService's bounds listener once WM has resolved it.
    void setPipBounds(int32_t wPx, int32_t hPx) { mPip.setBounds(wPx, hPx); }

    // Whether Android has this task in PIP. Distinct from mCurrentState ==
    // PIP, which is what ASH thinks: the two are apart for the handful of
    // frames between asking and being told, and asserting window policy off
    // the wrong one is how a window ends up half in PIP.
    bool isPip() const { return mPip.isPip(); }

    // Whether a press on this container should be offered to ash as a window
    // drag. True only for a PIP window ash will actually build a resizer for.
    bool isPipDragTarget() const;

    // Hand the current press to ash as an interactive move. `surfaceLocalX/Y`
    // are the pointer's position within the surface, in output pixels; the
    // window origin is added here to give exo the display-relative point it
    // wants. Must be called while the pointer is still down.
    void pipStartMove(int32_t surfaceLocalX, int32_t surfaceLocalY);

private:
    // Advertise this window's real resize limits to Chrome. Called at window
    // creation and again on leaving PIP, which replaces them for its own
    // purposes (see WaylandAppWindow::setPipSizeConstraints).
    void applyResizeLimits(WaylandAppWindow* window);

    // Send the controller's current state to ash. Split out of applyPipState so
    // ensureWindow() can re-send it for a task that entered PIP before its
    // window existed.
    void pushPipState(bool stateChanged);

public:

    // Put a kiosk window into real fullscreen: no caption frame at all, bounds
    // covering the whole output, and the Android task in FULLSCREEN windowing
    // mode. A kiosk app owns the screen, so this holds regardless of Chrome's
    // layout mode — clamshell must not restore a caption or saved bounds.
    // Safe to call repeatedly; every step is idempotent.
    void applyKioskFullscreen(int32_t outputW, int32_t outputH);

    // Put an unresizable app into fullscreen and keep it there, in either
    // layout mode. An activity that declares it cannot be resized has exactly
    // one sensible window size — the whole screen — so it is never handed back
    // a freeform rectangle, never offered a restore button, and never snapped.
    // This is the fullscreen state, not the maximized one: the caption is
    // AUTOHIDE (revealable at the top, so close and minimize stay reachable)
    // rather than the permanent title bar of a maximized window, and rather
    // than the kiosk path's NONE. With can_maximize dropped (see
    // WaylandAppWindow::setResizeLimits) there is no restore affordance, and
    // Chrome has no room to snap a window whose min == max == the output.
    // Safe to call repeatedly; every step is idempotent.
    void applyUnresizableFullscreen(int32_t outputW, int32_t outputH);

    // Push the caption's button set for whatever this window currently is.
    // Chrome's default mask (minimize | maximize_restore | close) is wrong in
    // two of our cases: a floated window wants the float button — which ash
    // renders as "exit float" — where maximize_restore would be, and an
    // unresizable window wants neither. Call after anything that changes float
    // state or resizability; it is idempotent.
    void applyFrameButtons();

    // Called from the Wayland event thread when Chrome fires the frame callback.
    void onFrameCallbackDone();

    // Take the release fence fd for this container's last present().
    // Returns -1 if nothing was presented. Caller owns the fd.
    int takePresentFence();

    // WaylandWindow::EventListener
    void onBoundsChanged(WaylandWindow* window,
                         int32_t x, int32_t y, int32_t w, int32_t h,
                         uint32_t reason) override;
    void onGeometryChanged(WaylandWindow* window,
                           int32_t x, int32_t y,
                           int32_t w, int32_t h) override;
    void onStateChanged(WaylandWindow* window,
                        WaylandWindow::WindowState state) override;
    void onWindowClosed(WaylandWindow* window) override;
    void onOutputChanged(WaylandWindow* window, wl_output* output) override;
    void onDragStarted(WaylandWindow* window, uint32_t direction) override;
    void onDragFinished(WaylandWindow* window, int32_t x, int32_t y,
                        bool cancelled) override;

    // The rectangle this window may occupy on its output, in output pixels.
    //
    // A window ash considers *fullscreen* — kiosk, an unresizable app, an app
    // that asked for real fullscreen — owns the whole output: every branch of
    // ShelfLayoutManager::CalculateShelfVisibility for kFullscreen insets the
    // work area by 0. A window ash merely *maximizes* — which is what a tablet
    // window is — is clamped by ash into the work area, so asking for the full
    // output only starts an argument we lose: ash hands back its own smaller
    // rectangle, and if we refuse that we spend the reassert budget on every
    // tablet entry and every rotation for nothing.
    //
    // The work area already accounts for whether the shelf is hidden (a game
    // asserting AUTOHIDE sees zero insets), so nothing here needs to branch on
    // the shelf itself.
    OutputRect allowedWindowRect() const;

    // Height of Chrome's window caption for the frame this window currently
    // has, in output pixels — the space exo adds ON TOP of the client
    // rectangle we hand it (see allowedWindowRect()). Zero unless the frame is
    // NORMAL.
    int32_t captionHeightPx() const;

private:
    // ChromeOS's caption height for a non-browser (ARC) window, in DIP.
    //
    // Chrome does not publish this over any protocol we speak — it is not in
    // remote-shell v2, aura-shell, zaura_output_manager_v2 or
    // zcr_remote_output_v2 (whose `insets`/`stable_insets` are both work-area
    // insets). It is a Chrome-side layout value:
    //
    //   FrameViewAsh::NonClientTopBorderHeight() -> HeaderView::GetPreferredHeight()
    //   -> FrameHeader caption button container height
    //   -> views::GetCaptionButtonLayoutSize(kNonBrowserCaption).height() == 32
    //      (ui/views/window/caption_button_layout_constants.cc)
    //
    // Confirmed on device: the caption measured exactly 64px at DSF 2.0.
    //
    // CAVEAT: that same function returns 40 instead of 32 when
    // chromeos::features::IsRoundedWindowsEnabled(). If a board ever turns
    // rounded windows on, every maximized window loses 8 dip off its bottom
    // and this constant has to follow — the durable fix is a Chrome-side
    // change to send NonClientTopBorderHeight() to the client, at which point
    // only captionHeightPx() changes.
    static constexpr int32_t kCaptionHeightDip = 32;

    // Height of ash's multitask menu hit region, in DIP: kHitRegionSize in
    // ash/wm/tablet_mode/tablet_mode_multitask_menu_controller.cc, pinned to
    // the window's top edge. Upstream is 10; FydeOS patch 0201 restores 100
    // for windows with no tabstrip, which is every ARC window. Keep this in
    // step with that patch — too small and we go on stealing the gesture, too
    // large and we stop asserting focus for ordinary taps near a window's top.
    static constexpr double kMenuHitRegionHeightDip = 100.0;
    // Resize the task to physW×physH (Android task pixels, origin 0,0) through
    // the Java AR-step path: resizable activities are resized, unresizable ones
    // are relaunched at the closest supported aspect ratio so they come back
    // fitting the new size rather than letterboxed inside it. Adopts whatever
    // Java applied for the task frame / input region / render target. Falls
    // back to a plain resizeTask if Java is unavailable. Returns true if the
    // AR-step request succeeded.
    bool applyBoundsWithArStep(int32_t physW, int32_t physH);

    bool ensureWindow(uint64_t layerId);

    // Drop mWindow, telling the input handlers to forget it first. Every path
    // that destroys the window must go through here, or the pointer, touch and
    // keyboard focus caches are left holding freed memory.
    void releaseWindow();

    // Put the Android task in `windowingMode`. `focusAfter` additionally makes
    // the task the focused one once the switch has landed — for the snap and
    // float paths, where Chrome has just handed the window a rectangle it must
    // be raised into. Everything else must leave focus alone: the tablet
    // steady state runs this for every open task at once.
    void switchWindowingModeAsync(int32_t windowingMode, bool focusAfter = false);

    // The Android windowing mode a tablet-mode task sits in — its steady
    // state, and what snap, float and unsnap return it to.
    //
    // MULTI_WINDOW, not FULLSCREEN. A tablet window is ash-*maximized*, and
    // ash clamps a maximized window to the work area, so with a visible shelf
    // the window is 40 dip shorter than the display. WINDOWING_MODE_FULLSCREEN
    // takes its bounds from the display config and rejects resizeTask, so the
    // Android task stayed display-sized inside that shorter window and
    // SurfaceFlinger composited the bottom 40 dip of every app away.
    // MULTI_WINDOW accepts resizeTask, so onGeometryChanged can keep the task,
    // the SF render target and the input frame on the same rectangle as the
    // window — which is the whole point.
    //
    // Not FREEFORM: that is the clamshell mode and it brings an Android
    // caption with it. Not a problem MULTI_WINDOW has.
    //
    // The apps that really do own the whole output — kiosk and unresizable —
    // do not come through here: applyKioskFullscreen and
    // applyUnresizableFullscreen keep them in FULLSCREEN, which is also what
    // keeps an unresizable app out of split view.
    static int32_t tabletWindowingMode();

    // Whether `state` should count as "filling the screen" — drives the shelf
    // for any app, and the game-only efficiency mode. Tablet mode always does:
    // applyLayoutMode gives a tablet window FRAME_NONE, its allowed rect and
    // maximize() directly, and Chrome sends no state_type_changed back for it —
    // so an app launched while already in tablet mode would otherwise never be
    // seen as fullscreen.
    bool stateFillsScreen(WaylandWindow::WindowState state) const {
        return state == WaylandWindow::WindowState::FULLSCREEN ||
               state == WaylandWindow::WindowState::MAXIMIZED ||
               mAppliedLayoutMode == ChromeLayoutMode::kTablet;
    }

    WaylandClient* mClient;   // not owned — Wayland globals
    wl_output* mOutput;        // not owned — per-display output
    uint64_t mContainerLayerId;
    int32_t mTaskId;
    int32_t mSessionId = 0;
    Rect mContainerFrame;
    bool mClosed = false;
    bool mIsGame = false;
    // Caption colours. The task pair is what the app asked for and wins; the
    // ChromeOS header pair is the fallback. 0 in the active slot means "not
    // set" for either — an opaque colour always has a non-zero alpha byte, so
    // 0 cannot collide with a real one.
    uint32_t mTaskFrameActive = 0;
    uint32_t mTaskFrameInactive = 0;
    uint32_t mHeaderFrameActive = 0;
    uint32_t mHeaderFrameInactive = 0;

    // Send whichever pair currently wins to the window.
    void pushFrameColors();
    bool mSecure = false;
    // zcr_security_v1 for this window's surface; owned for the window's life.
    zcr_security_v1* mSecurity = nullptr;
    bool mFillsScreen = false;     // stateFillsScreen() for the current state — drives the shelf
    bool mGameFullScreen = false;  // mFillsScreen AND isGame — drives efficiency mode only
    bool mGameEfficiency = false;  // mGameFullScreen AND persist.fydroid.game_efficiency
    bool mShelfAutoHide = false;   // last set_systemui_visibility we sent
    zaura_surface* mModalParent = nullptr;
    // ...and what ash is actually still holding. mShelfAutoHide is dropped on
    // unfocus without any push (so the assert stays repeatable — see
    // onFocusChanged), which makes it a record of our intent, not of ash's
    // state. Revoking an assert needs the latter: a window maximized in
    // clamshell carries a live AUTOHIDE flag into tablet mode, where declining
    // to re-assert is not enough to get the shelf back.
    bool mShelfAutoHideSent = false;
    bool mFocused = false;         // last focus state reported by the layer manager
    bool mIsDialog = false;
    // Consecutive frames the per-frame IS_DIALOG detection has held true, used
    // by setIsDialog() to debounce the one-way latch to a MODAL window so a
    // launch-time transient mis-flag cannot convert a real app into a modal.
    int mDialogTrueFrames = 0;
    std::string mNotifKey;  // notification key for NOTIFICATION windows
    int32_t mStandaloneA11yId = -1;  // see setStandaloneAccessibilityId

    WaylandWindowCategory mWindowCategory = WaylandWindowCategory::APP;
    std::unique_ptr<WaylandWindow> mWindow;

    // Task resizer for synchronizing Android task bounds with Chrome windows
    WaylandTaskResizer mTaskResizer;

    // Track last resized bounds to avoid feedback loops
    struct ResizedBounds {
        int32_t left = 0, top = 0, right = 0, bottom = 0;
    };
    ResizedBounds mLastResizedBounds;

    WaylandWindow::WindowState mCurrentState = WaylandWindow::WindowState::NORMAL;
    bool mKioskPinned = false;  // window is (trusted-)pinned; keep caption hidden
    // Chrome has floated this window. Float is not a state_type in this
    // protocol version (the enum stops at pip), so bounds_changed reason 8 is
    // the only notice we get, and this flag is what carries it forward: a
    // floated window owns its own small rectangle and must not be maximized
    // back to the output or left in a windowing mode that ignores its bounds.
    bool mFloated = false;
    // PIP policy for this task. Owns the state and the size bookkeeping; see
    // WaylandPipController.h for why this is not a WaylandWindow subclass.
    WaylandPipController mPip{mTaskId};
    // Activity declares itself unresizable (ActivityInfo resizeMode). Latched
    // once at window creation, from the same isTaskResizable() query that sets
    // the window's resize limits — the task's manifest cannot change under us.
    bool mUnresizable = false;
    int64_t mRestoredFromMinimizedNs = 0;  // clock_gettime timestamp

    // Saved pre-maximize output bounds for restore
    struct SavedBounds {
        int32_t x = 0, y = 0, w = 0, h = 0;
        bool valid = false;
    };
    SavedBounds mSavedBounds;

    // A move drag of a snapped window just ended at this cursor position
    // (output px). The NORMAL that follows it is a drag out of the snap, and
    // the restored rectangle goes where the window was dropped rather than
    // back to the pre-snap origin. Consumed by the next onStateChanged.
    bool mSnapDragEnded = false;
    int32_t mSnapDragEndX = 0;

    // The next geometry size change is a restore out of maximize/fullscreen:
    // ash cross-fades it already, and the resize mask on top flashed the
    // window, so it gets none. Consumed by onGeometryChanged.
    bool mSkipMaskForRestore = false;

    // Last layout mode applied via applyLayoutMode.
    ChromeLayoutMode mAppliedLayoutMode = ChromeLayoutMode::kUnknown;

    // Current Android windowing mode for this task (tracked to avoid redundant switches)
    int32_t mCurrentWindowingMode = 0;  // 0 = unknown/not set

    // Whether Chrome's SplitViewController is active for this window
    bool mSnapped = false;
    // Bounds Chrome reported for the snap itself. A later MOVE at exactly these
    // bounds is the settle event, not a drag out of the split.
    Rect mSnappedBounds;
    // Budget for re-asserting a full-output window: when Chrome echoes a
    // caption-inset geometry (maximize raced window creation and was
    // dropped pre-map), and when ash hands back a shrunken rectangle after
    // the output changed shape. Armed on each tablet-mode entry and on
    // every output resize (rotation).
    static constexpr int32_t kTabletReassertBudget = 5;
    int32_t mTabletReassertBudget = 0;
    // ...and the deadline that budget is only spendable before. Both things
    // the reassert repairs happen within a few ms of their trigger (a window
    // being mapped, an output changing shape). Everything that shrinks a
    // maximized window LATER is the user: opening the tablet multitask menu
    // insets the window by the menu height and shows up here identically to a
    // dropped maximize (geometry y=32), so re-asserting on it snapped the
    // window back and dismissed the menu the instant it appeared. A budget
    // alone could not tell the two apart — it survived until spent, however
    // many minutes later that was.
    static constexpr int64_t kReassertWindowNs = 3LL * 1000 * 1000 * 1000;
    int64_t mReassertUntilNs = 0;
    // How far ash's rectangle may sit from allowedWindowRect() before the
    // refusal above treats it as wrong rather than as a work area we have not
    // been told about yet. One shelf height: the clamshell shelf and the
    // tablet in-app shelf are different sizes, so a mode change moves the work
    // area, and ash's new bounds can beat the work_area_insets event that
    // describes it. The failure this path exists for — a rotation clamp — is
    // off by a third of the screen, nowhere near this.
    static constexpr int32_t kAllowedRectSlackDip = 48;

    // Arm the budget + deadline; true while it is armed and unexpired.
    void armReassert();
    bool reassertAllowed() const;

    // True between drag_started(direction != none) and drag_finished —
    // i.e. the user is currently drag-resizing the window from an edge.
    // While this is true, onGeometryChanged updates the cached server
    // bounds but skips the binder resizeTask call, so unresizable
    // activities aren't relaunched on every intermediate frame. Final
    // resize happens in onDragFinished via the AR-step path.
    bool mDragResizing = false;

    // Drag-resize mask. Without it every drag step made SF recompose the
    // window at the new size before Chrome could show it, with the task still
    // at its old size — the content was clipped (shrink) or left a transparent
    // band (grow), and each step lagged behind the frame by a composition.
    //
    //   drag:       kOff ──drag_started──▶ kRequested ──SF mask──▶ kShown
    //               kShown ──drag_finished──▶ kSettling
    //   transition: kOff ──size change──▶ kRequested ──SF mask──▶ kSettling
    //   both:       kSettling ──content ready / timeout──▶ kOff
    //
    // A transition is any window size change outside a drag — maximize /
    // restore, snap and the split divider, fullscreen on/off, rotation. The
    // task is resized at once, so there is no kShown phase.
    //
    // kRequested: SF is building the mask. For a drag live frames still go
    // out (the task has not changed yet); for a transition they are held back,
    // since they are already the old content at the new size.
    // kShown:     the mask is attached; each bounds/geometry step only moves
    //             the viewport destination (one commit, no SF work) and live
    //             frames are dropped. The render target is frozen too.
    // kSettling:  the task has been resized; live frames stay dropped until SF
    //             reports the app's buffer at the new size (or the timeout),
    //             and that frame is committed together with the viewport's
    //             removal, so the mask is replaced in a single commit.
    enum class ResizeMask { kOff, kRequested, kShown, kSettling };
    bool resizeMaskEligible(bool transition) const;
    // The window changed size outside a drag and the task has been resized to
    // mContainerFrame: cover the change with the mask until the app redraws.
    void beginResizeTransition();
    // The pre-mask drag_finished handling (final resize via the AR-step path).
    // Returns the task width it resized to, 0 if it did not resize the task.
    int32_t finishDragResize(WaylandWindow* window, int32_t x, int32_t y,
                             bool cancelled);
    // Moves the viewport destination to w x h surface pixels and commits.
    void setResizeMaskDestinationLocked(int32_t w, int32_t h);
    // Destroys the viewport (removal lands with the caller's next commit).
    void dropResizeMaskLocked(const char* why);
    // Guards the fields below and serialises the mask's surface commits (the
    // wayland event thread) against live presents (the SF thread).
    std::mutex mMaskMutex;
    ResizeMask mMask = ResizeMask::kOff;
    bool mMaskTransition = false;  // this mask covers a transition, not a drag
    wp_viewport* mMaskViewport = nullptr;
    // Kept alive while Chrome may still sample it.
    sp<GraphicBuffer> mMaskBuffer;
    int32_t mMaskDestW = 0, mMaskDestH = 0;
    bool mMaskContentReady = false;
    nsecs_t mMaskStartNs = 0, mMaskReleaseNs = 0, mMaskDeadlineNs = 0;
    uint32_t mMaskDroppedFrames = 0;
    // Last render target onGeometryChanged computed (window px), frozen or not.
    int32_t mGeometryTargetW = 0, mGeometryTargetH = 0;

    // Set on a physical-display crossover (onOutputChanged); consumed on the
    // next onGeometryChanged after the render target has been resized to the
    // new display's DSF. Forces SF to recompose immediately (scheduleRefresh)
    // so the new-size buffer lands promptly, minimising the one-frame gap where
    // Chrome shows the old-size buffer at the new display's scale (a flash).
    bool mPendingCrossoverRefresh = false;

    // Whether updateInputMethodBounds() last logged the degenerate-contentTop
    // fallback band. A headless IME's window can present at 10-30Hz for as
    // long as it holds focus, and the condition is stable across that whole
    // span — logged once on entry/exit instead of every frame.
    bool mImeFallbackBandLogged = false;

    // Same one-shot latch for the "IME takes no touch" branch, which is the
    // steady state for every text field FydroidIME serves.
    bool mImeNoInputLogged = false;

    // Server-assigned bounds (what Chrome thinks the window position/size is)
    int32_t mServerBoundsX = 0;
    int32_t mServerBoundsY = 0;
    int32_t mServerBoundsW = 0;
    int32_t mServerBoundsH = 0;

    // Last window_geometry_changed, in DIP — the dedupe key for that event on
    // its own, kept apart from mServerBounds* which the pixel-space
    // bounds_changed_in_output also writes. -1 so the first echo always runs.
    int32_t mLastGeometryX = -1;
    int32_t mLastGeometryY = -1;
    int32_t mLastGeometryW = -1;
    int32_t mLastGeometryH = -1;

    // Chrome window position in output coords (managed internally, invisible to Android).
    // Exposed via windowOffsetX/Y() for IME cursor position adjustment.
    int32_t mWindowOffsetX = 0;
    int32_t mWindowOffsetY = 0;
public:
    int32_t windowOffsetX() const { return mWindowOffsetX; }
    int32_t windowOffsetY() const { return mWindowOffsetY; }

    // Multiplier to convert wl_pointer/wl_touch surface coords (which arrive
    // in buffer pixels = surface_scale × DIP) into Android task pixels
    // (= primary_DSF × DIP). Normally the surface scale is the output DSF, so
    // this is mAndroidDsf / mScaleFactor — 1.0 when the window is on the
    // primary, ~1.82 on a 1.10-DSF HDMI. Returns 1.0 until a real DSF has
    // been observed.
    //
    // Game efficiency mode is the exception: it sends
    // zcr_remote_surface_v2_set_scale_factor(1.0), so Chrome delivers this
    // surface's coords in DIP rather than physical pixels while the Android
    // task stays sized in primary-DSF pixels. Dividing by mScaleFactor there
    // halves every coordinate at DSF 2.0 and the bottom-right of a fullscreen
    // game becomes unreachable, so use the surface's actual scale of 1.0.
    float inputScale() const {
        if (mAndroidDsf <= 0.0f || mScaleFactor <= 0.0f) return 1.0f;
        const float surfaceScale = mGameEfficiency ? 1.0f : mScaleFactor;
        return mAndroidDsf / surfaceScale;
    }
private:

    // Output info cache for new layers
    int32_t mOutputWidth = 0;
    int32_t mOutputHeight = 0;
    int32_t mOutputTransform = 0;
    // Work area of the current output, in output pixels. Zero-sized until the
    // first output-manager update, which allowedWindowRect() reads as "not
    // known yet, use the whole output".
    OutputRect mWorkArea;
    // Live DSF of the output the window is currently on. Mutated on
    // display crossover (onOutputChanged). Used as the multiplier when
    // sizing the SF render target buffer to Chrome's window pixel size.
    float mScaleFactor = 1.0f;
    // Android-side reference DSF. Captured once at the first
    // updateOutputInfo() call (= primary display's DSF, since windows
    // always start on the primary). Used as the multiplier when sizing
    // the Android task — keeping it constant means Android always
    // rasterizes the same DIP count of content regardless of which
    // physical display the window is on, so a window on a low-DSF HDMI
    // shows the same content density it would on the primary (SF then
    // downscales the high-res source into the smaller output buffer).
    float mAndroidDsf = 0.0f;


    // Frame callback throttling — only commit when Chrome is ready
    wl_callback* mFrameCallback = nullptr;
    std::atomic<bool> mFrameCallbackPending{false};
    // Set when the throttle refused a composed frame, cleared by the frame
    // callback that then asks SF to recompose it. Written from the SF
    // composition thread, consumed on the Wayland event thread.
    std::atomic<bool> mDroppedSinceCommit{false};

    // Per-container sw_sync fence: created in present(), signaled in onFrameCallbackDone()
    int mPresentTimelineFd = -1;  // sw_sync timeline (signaled by event thread)
    int mPresentFenceFd = -1;     // sync fence fd (taken by display for merging)
    // Persistent-timeline bookkeeping: mFenceSeq is the value of the newest
    // fence handed out, mTimelineValue how far the timeline has been advanced.
    // A fence is outstanding while mFenceSeq > mTimelineValue.
    std::atomic<uint32_t> mFenceSeq{0};
    std::atomic<uint32_t> mTimelineValue{0};

    // Present accounting for the client-composition path. A throttled present
    // is a frame SurfaceFlinger fully composed and then threw away, so these
    // are the direct measure of playback stutter. Logged once a second while
    // anything is being refused; silent on an idle window.
    uint32_t mPresentOkCount = 0;
    uint32_t mPresentThrottledCount = 0;
    uint32_t mPresentHiddenCount = 0;
    nsecs_t mPresentStatsNextLogNs = 0;
};

} // namespace android

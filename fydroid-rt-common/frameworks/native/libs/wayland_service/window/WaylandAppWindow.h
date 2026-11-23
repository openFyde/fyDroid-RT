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

#include "WaylandSurfaceData.h"
#include "WaylandWindow.h"

#include <cstdint>

struct wl_compositor;
struct wl_output;
struct wl_surface;
struct zaura_shell;
struct zaura_surface;
struct zcr_remote_shell_v2;
struct zcr_remote_surface_v2;
struct surface_augmenter;
struct augmented_surface;

namespace android {

// Per-app window wrapper for zcr_remote_surface_v2.
//
// Manages the Wayland surface + remote surface lifecycle and window properties
// (frame type, bounds, app_id). Buffer presentation is done externally via
// the wl_surface returned by surface().
class WaylandAppWindow : public WaylandWindow {
public:
    WaylandAppWindow(wl_compositor* compositor, zcr_remote_shell_v2* remoteShell,
                     wl_output* output, uint64_t layerId, uint32_t windowType,
                     int32_t taskId = 0, int32_t bufferScale = 1,
                     zaura_shell* auraShell = nullptr, int32_t sessionId = 0,
                     surface_augmenter* augmenter = nullptr);
    ~WaylandAppWindow() override;

    // Non-copyable
    WaylandAppWindow(const WaylandAppWindow&) = delete;
    WaylandAppWindow& operator=(const WaylandAppWindow&) = delete;

    // WaylandWindow interface
    wl_surface* surface() const override { return mSurface; }
    void setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                           int32_t w, int32_t h) override;
    void setEventListener(EventListener* listener) override { mEventListener = listener; }

    void minimize() override;
    void maximize() override;
    void restore() override;
    void fullscreen() override;

    int32_t boundsX() const override { return mBoundsX; }
    int32_t boundsY() const override { return mBoundsY; }
    int32_t boundsWidth() const override { return mBoundsWidth; }
    int32_t boundsHeight() const override { return mBoundsHeight; }
    WindowState windowState() const override { return mWindowState; }

    bool isVisible() const override {
        return !mClosed && mWindowState != WindowState::MINIMIZED;
    }
    bool isClosed() const override { return mClosed; }

    // Set min/max size for resize. Chrome's CanResize() requires both,
    // and min != max.
    // Advertise the window's resize limits to Chrome (DIP). Chrome decides from
    // these whether the window may be snapped into split view.
    //  - resizable == false: reports min == max and drops can_maximize, so an
    //    activity that cannot be resized is never offered a snap.
    //  - minW/minH > 0: the activity's declared minimum, reported as-is so an
    //    activity needing more than half a screen is not offered one either.
    //    Pass <= 0 to keep the historical 1x1 floor (see the comment in the
    //    implementation for why that floor exists).
    void setResizeLimits(int32_t maxW, int32_t maxH, bool resizable = true,
                         int32_t minW = -1, int32_t minH = -1);

    // Switch the window's frame type (caption bar). Pass values from
    // zcr_remote_surface_v2_frame_type — typically NORMAL (caption bar
    // visible) or NONE (frameless, used in tablet mode).
    void setFrameType(uint32_t frameType);

    // The frame Chrome is currently drawing for this window
    // (ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_*). NORMAL from creation — see the
    // set_frame in the constructor. Only NORMAL costs the client any space;
    // see WaylandLayerContainer::captionHeightPx().
    uint32_t frameType() const { return mFrameType; }

    // Choose which buttons the caption bar carries. Both masks are unions of
    // zcr_remote_surface_v2_frame_button_type; a button that is enabled but not
    // visible is ignored by Chrome. Dropping MAXIMIZE_RESTORE is the only way
    // to be rid of the restore button on a window Chrome is already showing
    // fullscreen — unset_can_maximize governs whether the window may be
    // maximized, but the fullscreen frame still offers restore without this.
    void setFrameButtons(uint32_t visibleButtons, uint32_t enabledButtons);

    // Echo a float back to Chrome. Chrome moves and resizes the window for
    // float on its own (bounds_changed reason 8), but — exactly like the snap
    // acknowledgement above — the window only *becomes* floated when the client
    // asks for it. Without this it merely sits in a small rectangle: other
    // windows cover it, the caption's size button offers no window-layout menu,
    // and there is nothing to exit, because ash never considered it floated.
    void setFloat();

    // Ask ash to make this window a ChromeOS Picture-in-Picture window: a
    // small always-on-top rectangle it positions, drags, tucks to a screen
    // edge and gives its own expand/close affordances.
    //
    // Android is what decides a task is in PIP (WINDOWING_MODE_PINNED); this
    // is how that decision reaches the compositor. There is no "unpip"
    // request — leaving PIP is restore(), like any other window state.
    void setPip();

    // Chrome told us how much of this window is covered (0..1) and why.
    // Forwarded to Java, which decides whether the task should auto-PIP.
    void onOcclusionChanged(float fraction, int32_t reason);

    // Advertise the size limits a PIP window must have for ash to consider it
    // draggable at all, and drop can_maximize so the maximum survives.
    //
    // ash gates EVERY gesture on a PIP window — move included, not just
    // resize — on PipController::CanResizePip() (pip_controller.cc:197), which
    // demands a non-empty maximum, a non-empty minimum, and max strictly
    // greater than min on BOTH axes. Fail it and CreateWindowResizer()
    // (workspace_window_resizer.cc:530) returns nullptr with no fallthrough,
    // so start_move, tuck, fling and collision avoidance all silently do
    // nothing. Sizes are in client PIXELS; exo scales them to DP.
    //
    // Only a PIP window should ever carry a maximum: a non-empty maximum makes
    // a window non-maximizable, which is right for PIP and wrong for
    // everything else. Google's ARC keeps exactly this discipline — maxWidth
    // and maxHeight are 0 for every ordinary ARC window and are filled in only
    // on entering PIP.
    // Returns true if the constraints were accepted and sent.
    bool setPipSizeConstraints(int32_t minW, int32_t minH, int32_t maxW, int32_t maxH);

    // Undo setPipSizeConstraints' can_maximize half on leaving PIP. The caller
    // must re-assert the window's real limits with setResizeLimits().
    void clearPipSizeConstraints();

    // Keep this window above ordinary windows, or stop doing so.
    //
    // A PIP window is by definition always-on-top — it is the one window you
    // keep watching while you work in another. ash does NOT infer this from the
    // pip request: without it the PIP window sits in the normal z-order and the
    // next window you focus covers it. Google's ARC sends the same thing, as
    // ArcAlwaysOnTopMetadata alongside the PIP window state.
    void setAlwaysOnTop(bool onTop);

    // Ask ash to run an interactive move of this window, anchored at (x, y) in
    // CLIENT PIXELS relative to the display origin.
    //
    // This is how a frameless window is dragged. Every other ARC window is
    // moved by its Chrome caption, which is why remoteSurfaceDragStarted says
    // not to call this — but a PIP window has FRAME_TYPE_NONE and its interior
    // hit-tests as HTCLIENT, never HTCAPTION, so nothing initiates a move by
    // itself. Google's ARC does exactly this from PipTouchHandler.onMove.
    //
    // ash answers with drag_started, then bounds_changed events, then
    // drag_finished. It requires the pointer to still be down (it checks
    // aura::Env), so send this from the press/motion path, not asynchronously.
    void startMove(int32_t x, int32_t y);

    // While suppressed the surface takes NO input: applyInputRegion states an
    // empty region instead of the window rectangle. Used for PIP, where ash
    // draws the controls (expand, close, the tuck handle) and hit-tests them
    // itself — an Android window claiming the same pixels would swallow them.
    //
    // Note "empty", not null: null means the whole surface, and our surface is
    // output-sized with a viewport crop, so null donates the entire output.
    void setInputSuppressed(bool suppressed);

    // Ask Chrome to auto-hide (or show) the shelf while this window is the
    // active one. Pass values from zcr_remote_surface_v2_systemui_visibility_state;
    // anything other than VISIBLE makes ash treat the shelf as auto-hidden
    // whenever the window is maximized or fullscreen.
    void setSystemUiVisibility(uint32_t visibility);

    // Ask Chrome to make this window active (zaura_surface.activate).
    void activate();

    zaura_surface* auraSurface() const override { return mAuraSurface; }

    // Set a background color on this surface (requires surface_augmenter).
    // Color is opaque black by default. Must be called before first commit.
    void setBackgroundColor(surface_augmenter* augmenter,
                            float r = 0.0f, float g = 0.0f,
                            float b = 0.0f, float a = 1.0f);

    // Paint this window's caption in the given colours (32-bit ARGB, not
    // premultiplied — ash forces the alpha opaque either way).
    //
    // Sending this at all is the point: it flips chromeos::kTrackDefaultFrameColors
    // off, and until it does, ash keeps the caption on its own default,
    // cros_tokens::kDialogTitleBarColor. That token is blended out of the
    // legacy static cros_colors.json5 and does not follow the wallpaper seed,
    // whereas Chrome's own title bar is cros.sys.header off the dynamic
    // secondary palette — which is why an untouched ARC caption reads grey
    // next to a browser window. See ArcSystemUiService.header().
    void setFrameColors(uint32_t active, uint32_t inactive);

    void setAccessibilityId(int32_t id) override;

    // Tell Chrome to snap this window left/right (activates SplitViewController)
    // or unsnap (deactivates split view). Uses zaura_surface protocol v16.
    void setSnapLeft();
    void setSnapRight();
    void unsetSnap();

    // Expose remote surface for direct protocol calls (e.g. set_scale_factor)
    zcr_remote_surface_v2* remoteSurface() const { return mRemoteSurface; }

    // Called by static Wayland listener callbacks
    void onBoundsChanged(int32_t x, int32_t y, int32_t w, int32_t h, uint32_t reason);
    void onBoundsChangedInOutput(wl_output* output, int32_t x, int32_t y,
                                 int32_t w, int32_t h, uint32_t reason);
    void onGeometryChanged(int32_t x, int32_t y, int32_t w, int32_t h);
    void onStateTypeChanged(uint32_t state);
    void onClose();
    void onSurfaceEnter(wl_output* output);
    void onSurfaceLeave(wl_output* output);
    void onDragStarted(uint32_t direction);
    void onDragFinished(int32_t x, int32_t y, bool cancelled);

    WaylandSurfaceData* surfaceData() { return &mSurfaceData; }

private:
    // Per-edge inset (in surface-local units = DIP when set_scale_factor=DSF)
    // applied to wl_surface input region so Chrome's FrameViewAsh can detect
    // resize-edge hits on all four sides. 8 DIP matches Ash's default
    // kResizeInsideBoundsSize.
    static constexpr int32_t kArcInputInsetDip = 8;

    WaylandSurfaceData mSurfaceData;
    wl_compositor* mCompositor = nullptr;
    wl_surface* mSurface = nullptr;
    zaura_surface* mAuraSurface = nullptr;
    zcr_remote_surface_v2* mRemoteSurface = nullptr;
    augmented_surface* mAugmentedSurface = nullptr;
    wl_output* mOutput = nullptr;
    uint64_t mLayerId = 0;
    // Every window logged as "layer 0", so two snapped windows were
    // indistinguishable in logcat. The taskId is already passed to the
    // constructor; keep it so each line says which window it is about.
    int32_t mTaskId = 0;
    EventListener* mEventListener = nullptr;
    int32_t mBoundsX = 0, mBoundsY = 0, mBoundsWidth = 0, mBoundsHeight = 0;
    // The window size in OUTPUT PIXELS, which is the space a wl_region is in.
    // mBounds* cannot be used for that: it is written by both
    // bounds_changed_in_output (pixels) and window_geometry_changed (DIP), so
    // on a DSF-2 output it is half the size half the time — an input region
    // built from it would cover a quarter of the window.
    int32_t mInputRegionWidth = 0, mInputRegionHeight = 0;
    bool mInputSuppressed = false;
    // ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NORMAL, spelled numerically because the
    // remote-shell protocol header is not pulled into this one.
    static constexpr uint32_t kFrameTypeNormal = 1;
    // Mirrors the set_frame in the constructor.
    uint32_t mFrameType = kFrameTypeNormal;
    // Last window_geometry_changed, in DIP — the dedupe key for that event on
    // its own. mBounds* above cannot serve: the pixel-space
    // bounds_changed_in_output writes it too. -1 so the first echo always
    // gets through. See WaylandAppWindow::onGeometryChanged.
    int32_t mGeometryX = -1, mGeometryY = -1;
    int32_t mGeometryWidth = -1, mGeometryHeight = -1;
    WindowState mWindowState = WindowState::NORMAL;
    bool mClosed = false;
    bool mFrameButtonsApplied = false;
    // Last caption colours handed to ash, so re-asserting the palette on every
    // window costs one comparison rather than a protocol round of requests.
    uint32_t mFrameColorActive = 0;
    uint32_t mFrameColorInactive = 0;
    bool mHasFrameColors = false;
    // Last accessibility window id published — see publishAccessibilityId().
    int32_t mAccessibilityId = -1;

    void applyFrameButtons();

    // Whether dragging an edge can resize this window. Only a NORMAL (freeform)
    // window qualifies — everything else is sized by Chrome.
    // Whether this window may be resized by dragging its edges, and so whether
    // it should donate the input ring below.
    //
    // The frame type is checked as well as the state, and it is the load-
    // bearing half: Chrome sends NO state_type_changed for a state we asked
    // for ourselves, so mWindowState is still NORMAL for the whole of a tablet
    // session even though applyLayoutMode maximized the window on the way in.
    // The ring was therefore donated to every tablet window — 8 dip along the
    // top edge, hit-tested by Chrome as HTTOP — directly on top of the tablet
    // multitask menu's hit region, which is only 10 dip tall
    // (kHitRegionSize{200, 10}, tablet_mode_multitask_menu_controller.cc).
    // 8 of those 10 dip started a window resize instead of the menu, which is
    // why dragging the panel down almost never worked. A frameless (tablet) or
    // immersive (fullscreen, unresizable) window cannot be edge-dragged
    // anyway, so keying on the frame both fixes the menu and stops us taking
    // input the app should have had.
    bool isEdgeResizable() const {
        return mWindowState == WindowState::NORMAL &&
               mFrameType == kFrameTypeNormal;
    }

    // Set wl_surface input region to (inset, inset, w-2*inset, h-2*inset)
    // so a kArcInputInsetDip-wide ring around the surface is reported as
    // HTLEFT/HTRIGHT/HTTOP/HTBOTTOM by Chrome's NonClientHitTest, enabling
    // drag-to-resize on the buffer edges. Does NOT commit — caller must.
    // A no-op (region reset to default) unless isEdgeResizable().
    void applyInputRegion(int32_t w, int32_t h);
};

} // namespace android

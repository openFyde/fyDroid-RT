/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

// WaylandAppWindow: One per Android task/layer.
// Wraps zcr_remote_surface_v2 for window management.
// Handles frame type, bounds, app_id setup and Chrome callbacks.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandAppWindow"

#include "WaylandAppWindow.h"

#include <inttypes.h>
#include <aura-shell-client-protocol.h>
#include <remote-shell-unstable-v2-client-protocol.h>
#include <surface-augmenter-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include "WaylandAccessibilityId.h"
#include "../WaylandLog.h"
#include "../WaylandGlobals.h"
#include "../binder/WaylandShellHandlerProxy.h"

namespace android {

// --- Remote surface event callbacks ---

static void remoteSurfaceClose(void* data, struct zcr_remote_surface_v2*) {
    static_cast<WaylandAppWindow*>(data)->onClose();
}
static void remoteSurfaceStateTypeChanged(void* data, struct zcr_remote_surface_v2*,
                                          uint32_t state) {
    static_cast<WaylandAppWindow*>(data)->onStateTypeChanged(state);
}
static void remoteSurfaceWindowGeometryChanged(void* data, struct zcr_remote_surface_v2*,
                                               int32_t x, int32_t y,
                                               int32_t w, int32_t h) {
    static_cast<WaylandAppWindow*>(data)->onGeometryChanged(x, y, w, h);
}
static void remoteSurfaceBoundsChanged(void* data, struct zcr_remote_surface_v2*,
                                       uint32_t /*displayIdHi*/, uint32_t /*displayIdLo*/,
                                       int32_t x, int32_t y,
                                       int32_t w, int32_t h, uint32_t reason) {
    static_cast<WaylandAppWindow*>(data)->onBoundsChanged(x, y, w, h, reason);
}
static void remoteSurfaceDragStarted(void* data, struct zcr_remote_surface_v2*,
                                     uint32_t dir) {
    ALOGI("remote_surface: drag_started dir=%u", dir);
    // Chrome already handles the drag (it detected HTCAPTION).
    // Do NOT call start_move — Chrome manages positioning. We forward
    // the event so the container can defer Android-side resizes until
    // drag_finished (avoids relaunching unresizable activities on every
    // intermediate frame).
    static_cast<WaylandAppWindow*>(data)->onDragStarted(dir);
}
static void remoteSurfaceDragFinished(void* data, struct zcr_remote_surface_v2*,
                                      int32_t x, int32_t y, int32_t cancelled) {
    ALOGI("remote_surface: drag_finished x=%d y=%d cancelled=%d", x, y, cancelled);
    static_cast<WaylandAppWindow*>(data)->onDragFinished(x, y, cancelled != 0);
}
static void remoteSurfaceChangeZoomLevel(void*, struct zcr_remote_surface_v2*,
                                         int32_t level) {
    ALOGI("remote_surface: change_zoom_level=%d", level);
}
static void remoteSurfaceBoundsChangedInOutput(void* data, struct zcr_remote_surface_v2*,
                                               struct wl_output* output,
                                               int32_t x, int32_t y,
                                               int32_t w, int32_t h, uint32_t reason) {
    static_cast<WaylandAppWindow*>(data)->onBoundsChangedInOutput(output, x, y, w, h, reason);
}

static const struct zcr_remote_surface_v2_listener sRemoteSurfaceListener = {
    .close = remoteSurfaceClose,
    .state_type_changed = remoteSurfaceStateTypeChanged,
    .window_geometry_changed = remoteSurfaceWindowGeometryChanged,
    .bounds_changed = remoteSurfaceBoundsChanged,
    .drag_started = remoteSurfaceDragStarted,
    .drag_finished = remoteSurfaceDragFinished,
    .change_zoom_level = remoteSurfaceChangeZoomLevel,
    .bounds_changed_in_output = remoteSurfaceBoundsChangedInOutput,
};

// --- zaura_surface event callbacks ---

// How much of this window is covered, 0..1, plus why it changed.
//
// This is the auto-PIP trigger. On a phone, switching apps PAUSES the previous
// activity and the app gets onUserLeaveHint(); in fydroid every visible window
// stays resumed, so that never fires and no app can auto-PIP by itself
// (measured with VLC: Home produced no onUserLeaveHint and no
// enterPictureInPictureMode, and the video just played on). Occlusion is the
// signal Google's ARC uses instead, and Chrome computes it specifically before
// activation changes so the client can react in time.
static void auraSurfaceOcclusionChanged(void* data, struct zaura_surface*,
                                        wl_fixed_t occlusionFraction,
                                        uint32_t occlusionReason) {
    static_cast<WaylandAppWindow*>(data)->onOcclusionChanged(
            static_cast<float>(wl_fixed_to_double(occlusionFraction)),
            static_cast<int32_t>(occlusionReason));
}
static void auraSurfaceLockFrameNormal(void*, struct zaura_surface*) {}
static void auraSurfaceUnlockFrameNormal(void*, struct zaura_surface*) {}
static void auraSurfaceOcclusionStateChanged(void*, struct zaura_surface*, uint32_t) {}
static void auraSurfaceDeskChanged(void*, struct zaura_surface*, int32_t) {}
static void auraSurfaceStartThrottle(void*, struct zaura_surface*) {}
static void auraSurfaceEndThrottle(void*, struct zaura_surface*) {}
static void auraSurfaceTooltipShown(void*, struct zaura_surface*, const char*,
                                    int32_t, int32_t, int32_t, int32_t) {}
static void auraSurfaceTooltipHidden(void*, struct zaura_surface*) {}

static const struct zaura_surface_listener sAuraSurfaceListener = {
    .occlusion_changed = auraSurfaceOcclusionChanged,
    .lock_frame_normal = auraSurfaceLockFrameNormal,
    .unlock_frame_normal = auraSurfaceUnlockFrameNormal,
    .occlusion_state_changed = auraSurfaceOcclusionStateChanged,
    .desk_changed = auraSurfaceDeskChanged,
    .start_throttle = auraSurfaceStartThrottle,
    .end_throttle = auraSurfaceEndThrottle,
    .tooltip_shown = auraSurfaceTooltipShown,
    .tooltip_hidden = auraSurfaceTooltipHidden,
};

// --- wl_surface event callbacks (output enter/leave) ---

static void surfaceEnter(void* data, struct wl_surface*, struct wl_output* output) {
    auto* sd = static_cast<WaylandSurfaceData*>(data);
    static_cast<WaylandAppWindow*>(sd->window)->onSurfaceEnter(output);
}
static void surfaceLeave(void* data, struct wl_surface*, struct wl_output* output) {
    auto* sd = static_cast<WaylandSurfaceData*>(data);
    static_cast<WaylandAppWindow*>(sd->window)->onSurfaceLeave(output);
}

static const struct wl_surface_listener sSurfaceListener = {
    .enter = surfaceEnter,
    .leave = surfaceLeave,
};

// --- WaylandAppWindow implementation ---

WaylandAppWindow::WaylandAppWindow(wl_compositor* compositor,
                                   zcr_remote_shell_v2* remoteShell,
                                   wl_output* output, uint64_t layerId,
                                   uint32_t windowType, int32_t taskId,
                                   int32_t bufferScale,
                                   zaura_shell* auraShell, int32_t sessionId,
                                   surface_augmenter* augmenter)
    : mCompositor(compositor), mOutput(output), mLayerId(layerId),
      mTaskId(taskId) {

    mSurfaceData.layerId = layerId;
    mSurfaceData.window = this;

    ALOGI("WaylandAppWindow: creating for layer %" PRIu64 " windowType=%u taskId=%d sessionId=%d",
          layerId, windowType, taskId, sessionId);

    // 1. Create wl_surface
    mSurface = wl_compositor_create_surface(compositor);
    if (!mSurface) {
        ALOGE("failed to create wl_surface for layer %" PRIu64, layerId);
        return;
    }

    wl_surface_add_listener(mSurface, &sSurfaceListener, &mSurfaceData);

    // 2. Create zaura_surface BEFORE remote_surface.
    //    Needed for snap protocol (set_snap_left/right) and session_id.
    //    Chrome reads session_id when processing get_remote_surface to match
    //    ghost windows from previous sessions.
    if (auraShell) {
        mAuraSurface = zaura_shell_get_aura_surface(auraShell, mSurface);
        if (mAuraSurface && sessionId > 0) {
            zaura_surface_set_window_session_id(mAuraSurface, sessionId);
            ALOGI("zaura_surface: set session_id=%d for layer %" PRIu64,
                  sessionId, layerId);
        }
        if (mAuraSurface) {
            zaura_surface_add_listener(mAuraSurface, &sAuraSurfaceListener, this);
            // Ask to be told how covered this window is. Only real app windows
            // want this — it is the auto-PIP trigger, and a task is the unit
            // that enters PIP. v8 introduced it and we bind far above that.
            if (taskId > 0) {
                zaura_surface_set_occlusion_tracking(mAuraSurface);
            }
        }
    }

    // 2.5. Create augmented_surface BEFORE the role is assigned.
    //      Chrome (v12) requires get_augmented_surface before role creation.
    //      Color is set later via setBackgroundColor() when SF provides it
    //      through setLayerColor (SOLID_COLOR composition).
    if (augmenter) {
        mAugmentedSurface = surface_augmenter_get_augmented_surface(augmenter, mSurface);
        if (mAugmentedSurface) {
            ALOGI("layer %" PRIu64 ": augmented_surface created", layerId);
        }
    }

    // 3. Create remote surface role (Chrome reads session_id here)
    mRemoteSurface = zcr_remote_shell_v2_get_remote_surface(
            remoteShell, mSurface, ZCR_REMOTE_SHELL_V2_CONTAINER_DEFAULT);
    if (!mRemoteSurface) {
        ALOGE("failed to create zcr_remote_surface_v2 for layer %" PRIu64, layerId);
        wl_surface_destroy(mSurface);
        mSurface = nullptr;
        return;
    }

    zcr_remote_surface_v2_add_listener(mRemoteSurface, &sRemoteSurfaceListener, this);

    // ARC++ window setup:
    // 1. Frame type NORMAL — Chrome draws window decorations (title bar,
    //    close/minimize/maximize buttons).
    zcr_remote_surface_v2_set_frame(mRemoteSurface,
                                     ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NORMAL);

    // 2. Window capabilities — tell Chrome this window supports maximize.
    //    Without this, Chrome won't show maximize button or allow maximizing.
    zcr_remote_surface_v2_set_can_maximize(mRemoteSurface);

    // 3. Resizability — deferred to setResizeLimits() after creation,
    //    since display dimensions are needed for max_size.

    // 4. Frame buttons are deferred to applyFrameButtons() — Chrome's
    //    SetFrameButtons early-returns if masks match, but the button model
    //    isn't initialized until the widget exists. We re-send after Chrome
    //    fires the first geometry_changed event (proving the widget is live).

    // 5. App ID — org.chromium.arc. prefix for Chrome to recognize as ARC++
    char appId[64];
    if (taskId > 0) {
        snprintf(appId, sizeof(appId), "org.chromium.arc.%d", taskId);
    } else {
        snprintf(appId, sizeof(appId), "org.chromium.arc.%" PRIu64, layerId);
    }
    zcr_remote_surface_v2_set_app_id(mRemoteSurface, appId);

    // 6. Title
    char title[64];
    if (taskId > 0) {
        snprintf(title, sizeof(title), "fydroid-task-%d", taskId);
    } else {
        snprintf(title, sizeof(title), "fydroid-layer-%" PRIu64, layerId);
    }
    zcr_remote_surface_v2_set_title(mRemoteSurface, title);

    // Apply frame buttons once during construction.
    applyFrameButtons();

    // NOTE: no commit here — caller must call setBoundsInOutput() then commit.
    // Chrome needs bounds before the first commit to properly set up the window.

    ALOGI("WaylandAppWindow: created layer %" PRIu64 " app_id=%s",
          layerId, appId);
}

void WaylandAppWindow::applyFrameButtons() {
    if (mFrameButtonsApplied || !mRemoteSurface) return;
    mFrameButtonsApplied = true;

    // Chrome's SetFrameButtons early-returns when masks match cached values.
    // The initial set_frame_buttons (from constructor) may have been cached
    // before the widget was created, so the CaptionButtonModel was never
    // initialized. Send (0,0) first to reset Chrome's cache, then the real
    // values so Chrome sees a change and calls UpdateCaptionButtonModel.
    constexpr uint32_t kFrameButtons =
            ZCR_REMOTE_SURFACE_V2_FRAME_BUTTON_TYPE_BACK |
            ZCR_REMOTE_SURFACE_V2_FRAME_BUTTON_TYPE_MINIMIZE |
            ZCR_REMOTE_SURFACE_V2_FRAME_BUTTON_TYPE_MAXIMIZE_RESTORE |
            ZCR_REMOTE_SURFACE_V2_FRAME_BUTTON_TYPE_CLOSE;

    zcr_remote_surface_v2_set_frame_buttons(mRemoteSurface, 0, 0);
    zcr_remote_surface_v2_set_frame_buttons(mRemoteSurface,
                                             kFrameButtons, kFrameButtons);
    ALOGI("layer %" PRIu64 " task=%d: applied frame buttons mask=0x%x", mLayerId, mTaskId, kFrameButtons);
}

WaylandAppWindow::~WaylandAppWindow() {
    ALOGI("WaylandAppWindow: destroying layer %" PRIu64, mLayerId);
    if (mAugmentedSurface) {
        augmented_surface_destroy(mAugmentedSurface);
    }
    if (mRemoteSurface) {
        zcr_remote_surface_v2_destroy(mRemoteSurface);
    }
    if (mAuraSurface) {
        zaura_surface_destroy(mAuraSurface);
    }
    if (mSurface) {
        wl_surface_destroy(mSurface);
    }
}

void WaylandAppWindow::setBackgroundColor(surface_augmenter* /*augmenter*/,
                                           float r, float g, float b, float a) {
    if (!mAugmentedSurface) return;

    wl_array color;
    wl_array_init(&color);
    float* rgba = static_cast<float*>(wl_array_add(&color, 4 * sizeof(float)));
    rgba[0] = r; rgba[1] = g; rgba[2] = b; rgba[3] = a;
    augmented_surface_set_background_color(mAugmentedSurface, &color);
    wl_array_release(&color);

    ALOGI("layer %" PRIu64 " task=%d: set background color (%.2f, %.2f, %.2f, %.2f)",
          mLayerId, mTaskId, r, g, b, a);
}

void WaylandAppWindow::minimize() {
    if (mRemoteSurface) {
        ALOGI("layer %" PRIu64 " task=%d: requesting minimize", mLayerId, mTaskId);
        zcr_remote_surface_v2_minimize(mRemoteSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::maximize() {
    if (mRemoteSurface) {
        ALOGI("layer %" PRIu64 " task=%d: requesting maximize", mLayerId, mTaskId);
        zcr_remote_surface_v2_maximize(mRemoteSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::restore() {
    if (mRemoteSurface) {
        ALOGI("layer %" PRIu64 " task=%d: requesting restore", mLayerId, mTaskId);
        zcr_remote_surface_v2_restore(mRemoteSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::fullscreen() {
    if (mRemoteSurface) {
        ALOGI("layer %" PRIu64 " task=%d: requesting fullscreen", mLayerId, mTaskId);
        zcr_remote_surface_v2_fullscreen(mRemoteSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::activate() {
    // Ask Chrome to make this window the active one. Needed when a task-less
    // surface (a dialog) that displaced us is destroyed: Chrome does not
    // re-activate the task on its own, and until it is active again ash has no
    // fullscreen window to read the shelf auto-hide flag off, so the shelf
    // stays up no matter what we set on our own surface.
    if (mAuraSurface) {
        ALOGI("layer %" PRIu64 " task=%d: aura activate", mLayerId, mTaskId);
        zaura_surface_activate(mAuraSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::setSystemUiVisibility(uint32_t visibility) {
    if (mRemoteSurface) {
        ALOGI("layer %" PRIu64 " task=%d: set_systemui_visibility %u", mLayerId, mTaskId, visibility);
        zcr_remote_surface_v2_set_systemui_visibility(mRemoteSurface, visibility);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::setFrameColors(uint32_t active, uint32_t inactive) {
    if (!mAuraSurface) return;
    if (mHasFrameColors && active == mFrameColorActive &&
        inactive == mFrameColorInactive) {
        return;
    }
    mFrameColorActive = active;
    mFrameColorInactive = inactive;
    mHasFrameColors = true;
    ALOGI("layer %" PRIu64 " task=%d: set_frame_colors active=#%08X inactive=#%08X",
          mLayerId, mTaskId, active, inactive);
    zaura_surface_set_frame_colors(mAuraSurface, active, inactive);
    // Deliberately no wl_surface_commit, unlike the snap/activate requests
    // below. exo applies set_frame_colors the moment it reads the request
    // (Surface::SetFrameColors calls the delegate directly, it is not part of
    // the pending state), so a commit would buy nothing — and this is also
    // called before a freshly created window's first commit, where an extra
    // one would map the surface early.
}

void WaylandAppWindow::setAccessibilityId(int32_t id) {
    publishAccessibilityId(mAuraSurface, id, mAccessibilityId, "app", mLayerId);
}

void WaylandAppWindow::setSnapLeft() {
    if (mAuraSurface) {
        ALOGI("layer %" PRIu64 " task=%d: set_snap_left", mLayerId, mTaskId);
        zaura_surface_set_snap_left(mAuraSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::setSnapRight() {
    if (mAuraSurface) {
        ALOGI("layer %" PRIu64 " task=%d: set_snap_right", mLayerId, mTaskId);
        zaura_surface_set_snap_right(mAuraSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::unsetSnap() {
    if (mAuraSurface) {
        ALOGI("layer %" PRIu64 " task=%d: unset_snap", mLayerId, mTaskId);
        zaura_surface_unset_snap(mAuraSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::setResizeLimits(int32_t maxW, int32_t maxH, bool resizable,
                                       int32_t minW, int32_t minH) {
    if (!mRemoteSurface) return;
    if (resizable && (minW > 0 || minH > 0)) {
        // The activity declares a minimum (ActivityInfo.windowLayout). Report
        // it: Chrome measures the window's minimum against half the screen to
        // decide whether it may be snapped, so an activity that genuinely needs
        // more than half a screen stops being offered split view.
        const int32_t mw = (minW > 0) ? minW : 1;
        const int32_t mh = (minH > 0) ? minH : 1;
        ALOGI("layer %" PRIu64 " task=%d: resize limits %dx%d..%dx%d",
              mLayerId, mTaskId, mw, mh, maxW, maxH);
        zcr_remote_surface_v2_set_min_size(mRemoteSurface, mw, mh);
        zcr_remote_surface_v2_set_max_size(mRemoteSurface, maxW, maxH);
        return;
    }
    if (!resizable) {
        // The activity cannot be resized, so say so: Chrome decides whether a
        // window may be snapped from the limits we advertise here, and claiming
        // 1×1..max made it offer split view for activities that cannot take a
        // half-screen bound. Reporting min == max leaves it no room to snap
        // into. Also drop the maximize affordance, for the same reason.
        ALOGI("layer %" PRIu64 " task=%d: resize limits fixed at %dx%d (unresizable)",
              mLayerId, mTaskId, maxW, maxH);
        zcr_remote_surface_v2_set_min_size(mRemoteSurface, maxW, maxH);
        zcr_remote_surface_v2_set_max_size(mRemoteSurface, maxW, maxH);
        zcr_remote_surface_v2_unset_can_maximize(mRemoteSurface);
        return;
    }
    // Don't enforce a minimum window size: Chrome enforces this at the
    // window-frame level (the visible content area can't shrink below it),
    // but keeps reporting the smaller user-requested size via
    // geometry_changed. Our buffer ends up smaller than Chrome's content
    // rect, leaving a transparent fringe (visible e.g. when WPS is
    // user-resized below 320 DIP). Pass 1×1 instead — effectively no
    // floor — and let Chrome's content area always match what it reports.
    zcr_remote_surface_v2_set_min_size(mRemoteSurface, 1, 1);
    zcr_remote_surface_v2_set_max_size(mRemoteSurface, maxW, maxH);
}

void WaylandAppWindow::setFrameType(uint32_t frameType) {
    if (!mRemoteSurface) return;
    ALOGI("layer %" PRIu64 " task=%d: set_frame type=%u", mLayerId, mTaskId, frameType);
    const bool wasEdgeResizable = isEdgeResizable();
    mFrameType = frameType;
    zcr_remote_surface_v2_set_frame(mRemoteSurface, frameType);

    // The frame is half of isEdgeResizable(), and going frameless for tablet
    // mode is the transition that has to drop the ring — there is no state
    // change to piggy-back on, because Chrome does not echo one for a state we
    // requested. See isEdgeResizable().
    if (wasEdgeResizable != isEdgeResizable() && mSurface &&
        mInputRegionWidth > 0 && mInputRegionHeight > 0) {
        applyInputRegion(mInputRegionWidth, mInputRegionHeight);
        wl_surface_commit(mSurface);
        ALOGI("layer %" PRIu64 " task=%d: resize ring %s (frame=%u)", mLayerId, mTaskId,
              isEdgeResizable() ? "on" : "off", frameType);
    }
}

void WaylandAppWindow::setFloat() {
    if (mRemoteSurface) {
        ALOGI("layer %" PRIu64 " task=%d: set_float", mLayerId, mTaskId);
        zcr_remote_surface_v2_set_float(mRemoteSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::onOcclusionChanged(float fraction, int32_t reason) {
    if (mTaskId <= 0) return;
    // A minimized window is 100% occluded by definition, and auto-PIP must not
    // read that as "the user switched away from a video". Minimizing is the
    // user putting the window away; PIP-ing it would bring it straight back as
    // an always-on-top rectangle, which is the opposite of what they asked for.
    // The compositor is the only side that knows — a minimized ARC window is
    // still visible=true to WM (see the snap/minimized ledger entry).
    if (mWindowState == WindowState::MINIMIZED) return;

    // No auto-PIP in tablet mode.
    //
    // Occlusion is a good "the user switched away" proxy on a windowed desktop,
    // where windows are inset and covering one is a deliberate act. In tablet
    // mode every window fills the work area, so ANY app switch fully occludes
    // whatever was there — every video app would drop into PIP every time the
    // user opened anything else, uninvited.
    //
    // It also removes the oscillation that made this loop: with a full-screen
    // window, the PIP appearing UNCOVERS the source task, so the fraction falls
    // back under the threshold and the next event is a fresh rising edge
    // (measured 0.996 → 0.789 → 1.0 in 15ms). The refuse-while-pinned and
    // cooldown guards in the Java policy still stand as backstops; this stops
    // the signal being generated in the first place.
    //
    // In-app PIP buttons are unaffected — this only gates the automatic trigger.
    if (auto* globals = WaylandGlobals::instance()) {
        if (globals->layoutMode() == ChromeLayoutMode::kTablet) return;
    }
    ALOGVT("layer %" PRIu64 " task=%d: occlusion %.2f reason=%d",
           mLayerId, mTaskId, fraction, reason);
    WaylandShellHandlerProxy::instance()->taskOcclusionChanged(mTaskId, fraction, reason);
}

void WaylandAppWindow::setPip() {
    if (mRemoteSurface) {
        ALOGI("layer %" PRIu64 " task=%d: pip", mLayerId, mTaskId);
        zcr_remote_surface_v2_pip(mRemoteSurface);
        if (mSurface) wl_surface_commit(mSurface);
    }
}

bool WaylandAppWindow::setPipSizeConstraints(int32_t minW, int32_t minH,
                                             int32_t maxW, int32_t maxH) {
    if (!mRemoteSurface) return false;
    if (minW <= 0 || minH <= 0 || maxW <= minW || maxH <= minH) {
        ALOGE("layer %" PRIu64 " task=%d: refusing pip constraints %dx%d..%dx%d "
              "— ash needs max STRICTLY greater than min on BOTH axes",
              mLayerId, mTaskId, minW, minH, maxW, maxH);
        return false;
    }
    ALOGI("layer %" PRIu64 " task=%d: pip constraints %dx%d..%dx%d px "
          "(+unset_can_maximize)", mLayerId, mTaskId, minW, minH, maxW, maxH);
    // Order matters only in that all three must be committed together; the
    // interesting one is unset_can_maximize. ClientControlledShellSurface
    // overrides GetMaximumSize() to return an EMPTY size whenever
    // can_maximize_ is true (client_controlled_shell_surface.cc:953), so with
    // it set every set_max_size we send is thrown away — and an empty maximum
    // makes ash::PipController::CanResizePip() false, which makes
    // CreateWindowResizer() return nullptr for this window, which makes ash
    // drop start_move on the floor without a log line. That is the whole
    // reason a PIP window could not be dragged. See
    // docs/pip-chromium-side-investigate.md §3.
    zcr_remote_surface_v2_unset_can_maximize(mRemoteSurface);
    zcr_remote_surface_v2_set_min_size(mRemoteSurface, minW, minH);
    zcr_remote_surface_v2_set_max_size(mRemoteSurface, maxW, maxH);
    if (mSurface) wl_surface_commit(mSurface);
    return true;
}

void WaylandAppWindow::clearPipSizeConstraints() {
    if (!mRemoteSurface) return;
    ALOGI("layer %" PRIu64 " task=%d: pip constraints cleared (set_can_maximize)",
          mLayerId, mTaskId);
    // can_maximize_ also drives CanMaximize() and hence the caption's
    // maximize button, so a window that left PIP without this comes back
    // missing it. The real min/max are re-asserted by the container calling
    // setResizeLimits() straight after.
    zcr_remote_surface_v2_set_can_maximize(mRemoteSurface);
    if (mSurface) wl_surface_commit(mSurface);
}

void WaylandAppWindow::setAlwaysOnTop(bool onTop) {
    if (!mRemoteSurface) return;
    ALOGI("layer %" PRIu64 " task=%d: %s_always_on_top", mLayerId, mTaskId,
          onTop ? "set" : "unset");
    if (onTop) {
        zcr_remote_surface_v2_set_always_on_top(mRemoteSurface);
    } else {
        zcr_remote_surface_v2_unset_always_on_top(mRemoteSurface);
    }
    if (mSurface) wl_surface_commit(mSurface);
}

void WaylandAppWindow::startMove(int32_t x, int32_t y) {
    if (!mRemoteSurface) return;
    ALOGI("layer %" PRIu64 " task=%d: start_move at (%d,%d) px", mLayerId, mTaskId, x, y);
    // x/y are CLIENT PIXELS relative to the display origin. exo scales them by
    // GetClientToDpScale() (zcr_remote_shell_impl.cc:1244) and, because
    // fydroid binds zcr_remote_shell_v2 — whose
    // use_default_scale_cancellation default is false
    // (zcr_remote_shell_v2.cc:308) — does NOT convert from screen afterwards,
    // so what ash receives is display DP. Note this is the opposite convention
    // to set_bounds_in_output, which takes raw pixels and is not scaled.
    zcr_remote_surface_v2_start_move(mRemoteSurface, x, y);
    if (mSurface) wl_surface_commit(mSurface);
}

void WaylandAppWindow::setFrameButtons(uint32_t visibleButtons,
                                       uint32_t enabledButtons) {
    if (!mRemoteSurface) return;
    ALOGI("layer %" PRIu64 " task=%d: set_frame_buttons visible=0x%x enabled=0x%x",
          mLayerId, mTaskId, visibleButtons, enabledButtons);
    zcr_remote_surface_v2_set_frame_buttons(mRemoteSurface, visibleButtons,
                                            enabledButtons);
}

void WaylandAppWindow::setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                                         int32_t w, int32_t h) {
    if (mRemoteSurface && output && w > 0 && h > 0) {
        zcr_remote_surface_v2_set_bounds_in_output(mRemoteSurface, output,
                                                    x, y, w, h);
        // Store as initial bounds (will be updated by Chrome's bounds_changed events)
        mBoundsX = x;
        mBoundsY = y;
        mBoundsWidth = w;
        mBoundsHeight = h;
        applyInputRegion(w, h);
    }
}

void WaylandAppWindow::setInputSuppressed(bool suppressed) {
    if (mInputSuppressed == suppressed) return;
    mInputSuppressed = suppressed;
    ALOGI("layer %" PRIu64 " task=%d: input %s", mLayerId, mTaskId,
          suppressed ? "SUPPRESSED (pip)" : "restored");
    if (mSurface && mInputRegionWidth > 0 && mInputRegionHeight > 0) {
        applyInputRegion(mInputRegionWidth, mInputRegionHeight);
        wl_surface_commit(mSurface);
    }
}

void WaylandAppWindow::applyInputRegion(int32_t w, int32_t h) {
    if (!mCompositor || !mSurface) return;
    mInputRegionWidth = w;
    mInputRegionHeight = h;
    if (mInputSuppressed) {
        // An EMPTY region — created and never added to. See setInputSuppressed
        // for why this must not be the null shortcut below.
        wl_region* empty = wl_compositor_create_region(mCompositor);
        if (empty) {
            wl_surface_set_input_region(mSurface, empty);
            wl_region_destroy(empty);
        }
        return;
    }
    if (w <= 0 || h <= 0) {
        wl_surface_set_input_region(mSurface, nullptr);
        return;
    }
    // Always state the region, never pass null.
    //
    // Null means "the whole surface", and our surface is not our window: the
    // task buffer stays output-sized and wp_viewport crops it down to the
    // content (WaylandLayerContainer.h). So null donates the whole OUTPUT.
    // While the window was maximized that was the same rectangle and nothing
    // showed; snapped, it is twice the window. Measured on device with the kang
    // tester snapped left at 1274px wide: ash delivered taps at x=1846..2027 —
    // the neighbour's half — into our surface, and each one re-asserted focus
    // to the ARC task, so the window beside it could never be typed into.
    //
    // The ring costs the app 8 DIP of input on all four edges, so only donate
    // it when the window can actually be resized by dragging an edge. A
    // maximized / fullscreen / snapped / pinned window is sized by Chrome, and
    // a fullscreen game would otherwise lose its edges for an affordance it can
    // never use. Without the ring the region is simply the window.
    const bool ring = isEdgeResizable() &&
                      w > 2 * kArcInputInsetDip && h > 2 * kArcInputInsetDip;
    const int32_t inset = ring ? kArcInputInsetDip : 0;
    wl_region* region = wl_compositor_create_region(mCompositor);
    if (!region) {
        // Better the whole output than no input at all.
        wl_surface_set_input_region(mSurface, nullptr);
        return;
    }
    wl_region_add(region, inset, inset, w - 2 * inset, h - 2 * inset);
    wl_surface_set_input_region(mSurface, region);
    wl_region_destroy(region);
}

void WaylandAppWindow::onBoundsChanged(int32_t x, int32_t y, int32_t w, int32_t h,
                                       uint32_t reason) {
    // Ignored — use bounds_changed_in_output instead (Chrome sends both).
    (void)x; (void)y; (void)w; (void)h; (void)reason;
}

void WaylandAppWindow::onBoundsChangedInOutput(wl_output* output, int32_t x, int32_t y,
                                                int32_t w, int32_t h, uint32_t reason) {
    ALOGI("layer %" PRIu64 " task=%d: bounds_changed_in_output x=%d y=%d %dx%d reason=%u output=%p (cur=%p)",
          mLayerId, mTaskId, x, y, w, h, reason, output, mOutput);

    if (output && output != mOutput) {
        ALOGI("layer %" PRIu64 " task=%d: output changed %p -> %p via bounds_changed_in_output",
              mLayerId, mTaskId, mOutput, output);
        mOutput = output;
        if (mEventListener) {
            mEventListener->onOutputChanged(this, output);
        }
    }

    // Real bounds prove the window is not minimized any more.
    //
    // Chrome sends state_type_changed only for a state IT originates, so a
    // window minimized on the way into split view is never told it was
    // restored: it is simply handed new bounds and a snap. mWindowState stayed
    // MINIMIZED, isVisible() stayed false, and presentComposedBuffer discarded
    // every composed frame before it even reached the throttle — no presents,
    // no drops, nothing logged. The window kept whatever Chrome last had while
    // the app carried on drawing and receiving touches, which is exactly the
    // "left window is stale" seen on device: task 108 got state 2 at 10:51:07,
    // was snapped left at 10:51:13, and presented 0 frames from then on while
    // the right-hand window presented hundreds.
    //
    // A minimized window has no bounds to report, so a non-empty rect is
    // unambiguous. NORMAL is the safe landing state: isVisible() only asks
    // whether we are minimized, and a genuine maximize/fullscreen/snap that
    // follows will set its own state (or is tracked by mSnapped in the
    // container). See arc-window-state-echo-handshake.
    if (mWindowState == WindowState::MINIMIZED && w > 0 && h > 0) {
        ALOGI("layer %" PRIu64 " task=%d: bounds while MINIMIZED (%dx%d reason=%u)"
              " — Chrome restored us without a state change; clearing",
              mLayerId, mTaskId, w, h, reason);
        mWindowState = WindowState::NORMAL;
    }

    mBoundsX = x;
    mBoundsY = y;
    mBoundsWidth = w;
    mBoundsHeight = h;

    // Ack bounds for all reasons so Chrome commits the new geometry.
    // Protocol: "The client responds with set_bounds_in_output request,
    // with the bounds it is resized to."
    if (mRemoteSurface && mOutput && w > 0 && h > 0) {
        zcr_remote_surface_v2_set_bounds_in_output(mRemoteSurface, mOutput, x, y, w, h);
        applyInputRegion(w, h);
        wl_surface_commit(mSurface);
    }

    if (mEventListener) {
        mEventListener->onBoundsChanged(this, x, y, w, h, reason);
    }
}

void WaylandAppWindow::onGeometryChanged(int32_t x, int32_t y, int32_t w, int32_t h) {
    // Dedupe against the last GEOMETRY, not against mBounds*. Chrome sends
    // this on every widget commit and mBounds* is written by both events, in
    // two different spaces: a geometry is DIP, a bounds_changed_in_output is
    // output pixels ("Bounds are set in pixels, and should not be scaled",
    // exo). On a DSF-1.0 output the two coincide, so a real geometry change
    // looked identical to the bounds event that had just overwritten mBounds*
    // and was dropped here — before the container ever saw it. The Android
    // task now takes its size from this event (the tablet steady state is
    // WINDOWING_MODE_MULTI_WINDOW), so a dropped one leaves the task at the
    // wrong size for good.
    if (x == mGeometryX && y == mGeometryY &&
        w == mGeometryWidth && h == mGeometryHeight) {
        return;  // No change
    }
    mGeometryX = x;
    mGeometryY = y;
    mGeometryWidth = w;
    mGeometryHeight = h;

    ALOGI("layer %" PRIu64 " task=%d: window_geometry_changed x=%d y=%d %dx%d",
          mLayerId, mTaskId, x, y, w, h);

    mBoundsX = x;
    mBoundsY = y;
    mBoundsWidth = w;
    mBoundsHeight = h;
    if (mEventListener) {
        mEventListener->onGeometryChanged(this, x, y, w, h);
    }
}

void WaylandAppWindow::onStateTypeChanged(uint32_t state) {
    ALOGI("layer %" PRIu64 " task=%d: state_type_changed=%u", mLayerId, mTaskId, state);
    const bool wasEdgeResizable = isEdgeResizable();
    mWindowState = static_cast<WindowState>(state);

    // The resize ring is only donated while the window is freeform, so a
    // state change alone can add or remove it — Chrome does not always follow
    // with a bounds update we could piggy-back on. Compared against the
    // previous *resizability*, not the previous state: the frame is the other
    // half of that test, so "was NORMAL" is not the same question.
    if (wasEdgeResizable != isEdgeResizable() && mSurface &&
        mInputRegionWidth > 0 && mInputRegionHeight > 0) {
        applyInputRegion(mInputRegionWidth, mInputRegionHeight);
        wl_surface_commit(mSurface);
        ALOGI("layer %" PRIu64 " task=%d: resize ring %s (state=%u)", mLayerId, mTaskId,
              isEdgeResizable() ? "on" : "off", state);
    }

    if (mEventListener) {
        mEventListener->onStateChanged(this, mWindowState);
    }
}

void WaylandAppWindow::onClose() {
    ALOGI("layer %" PRIu64 " task=%d: window closed by Chrome", mLayerId, mTaskId);
    mClosed = true;
    if (mEventListener) {
        mEventListener->onWindowClosed(this);
    }
}

void WaylandAppWindow::onDragStarted(uint32_t direction) {
    if (mEventListener) {
        mEventListener->onDragStarted(this, direction);
    }
}

void WaylandAppWindow::onDragFinished(int32_t x, int32_t y, bool cancelled) {
    if (mEventListener) {
        mEventListener->onDragFinished(this, x, y, cancelled);
    }
}

void WaylandAppWindow::onSurfaceEnter(wl_output* output) {
    ALOGI("layer %" PRIu64 " task=%d: surface entered output %p (was %p)",
          mLayerId, mTaskId, output, mOutput);
    if (output && output != mOutput) {
        mOutput = output;
        if (mEventListener) {
            mEventListener->onOutputChanged(this, output);
        }
    }
}

void WaylandAppWindow::onSurfaceLeave(wl_output* output) {
    ALOGI("layer %" PRIu64 " task=%d: surface left output %p", mLayerId, mTaskId, output);
}

} // namespace android

#pragma clang diagnostic pop

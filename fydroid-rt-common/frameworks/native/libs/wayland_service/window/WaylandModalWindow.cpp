/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

// WaylandModalWindow: Frameless dialog window (FRAME_TYPE_NONE) drawing
// Android's DIM_BEHIND around its card: a transient child of its app window in
// ash's app container, or in the system modal container (CONTAINER_OVERLAY)
// when it has no app window. Chrome renders it with no caption/decoration. SF
// controls geometry, so most event callbacks are no-ops.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandModalWindow"

#include "WaylandModalWindow.h"

#include <inttypes.h>

#include <algorithm>
#include <cmath>
#include <cstdlib>

#include <cutils/properties.h>

#include <remote-shell-unstable-v2-client-protocol.h>
#include <surface-augmenter-client-protocol.h>
#include <aura-shell-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include "WaylandAccessibilityId.h"

namespace android {

// --- Remote surface event callbacks (minimal — SF controls geometry) ---

static void modalSurfaceClose(void* data, struct zcr_remote_surface_v2*) {
    static_cast<WaylandModalWindow*>(data)->onClose();
}
static void modalSurfaceNoop1(void*, struct zcr_remote_surface_v2*, uint32_t) {}
// SF owns the geometry, so Chrome's geometry echoes stay no-ops. If you ever
// compare one against the bounds we asked for, scale first: Chrome reports in
// DIP and setBoundsInOutput() takes physical pixels, so on a DSF-2 panel a
// perfectly obedient Chrome answers 1280x800 to our 2560x1600.
static void modalSurfaceNoop4(void*, struct zcr_remote_surface_v2*,
                              int32_t, int32_t, int32_t, int32_t) {}
static void modalSurfaceNoop5u(void*, struct zcr_remote_surface_v2*,
                               uint32_t, uint32_t,
                               int32_t, int32_t, int32_t, int32_t, uint32_t) {}
static void modalSurfaceNoop1u(void*, struct zcr_remote_surface_v2*, uint32_t) {}
static void modalSurfaceNoopBoundsInOutput(void*, struct zcr_remote_surface_v2*,
                                           struct wl_output*,
                                           int32_t, int32_t, int32_t, int32_t,
                                           uint32_t) {}

static const struct zcr_remote_surface_v2_listener sModalRemoteSurfaceListener = {
    .close = modalSurfaceClose,
    .state_type_changed = modalSurfaceNoop1,
    .window_geometry_changed = modalSurfaceNoop4,
    .bounds_changed = modalSurfaceNoop5u,
    .drag_started = modalSurfaceNoop1u,
    .drag_finished = [](void*, struct zcr_remote_surface_v2*,
                        int32_t, int32_t, int32_t) {},
    .change_zoom_level = [](void*, struct zcr_remote_surface_v2*, int32_t) {},
    .bounds_changed_in_output = modalSurfaceNoopBoundsInOutput,
};

// --- WaylandModalWindow implementation ---

WaylandModalWindow::WaylandModalWindow(wl_compositor* compositor,
                                       zcr_remote_shell_v2* remoteShell,
                                       wl_output* output, uint64_t layerId,
                                       int32_t taskId,
                                       surface_augmenter* augmenter,
                                       zaura_shell* auraShell,
                                       wl_subcompositor* subcompositor,
                                       zaura_surface* parent)
    : mCompositor(compositor), mAugmenter(augmenter), mLayerId(layerId) {

    mSurfaceData.layerId = layerId;
    mSurfaceData.taskId = taskId;
    mSurfaceData.window = this;

    ALOGI("WaylandModalWindow: creating for layer %" PRIu64 " taskId=%d",
          layerId, taskId);

    // 1. Create wl_surface
    mSurface = wl_compositor_create_surface(compositor);
    if (!mSurface) {
        ALOGE("failed to create wl_surface for modal layer %" PRIu64, layerId);
        return;
    }

    wl_surface_set_user_data(mSurface, &mSurfaceData);

    // The zaura_surface exists only to carry the accessibility window id, and
    // it is created before the role object below because that is the order exo
    // documents for aura state that must be in place when the role is assigned
    // (WaylandAppWindow does the same). It sends nothing on its own and takes
    // no listener: this window type has no zaura events to act on.
    if (auraShell) {
        mAuraSurface = zaura_shell_get_aura_surface(auraShell, mSurface);
        if (!mAuraSurface) {
            ALOGW("failed to create zaura_surface for layer %" PRIu64
                  "; this window will not be readable by a screen "
                  "reader", layerId);
        }
    }

    // Which ash container this dialog goes in — see step 3. Decided here
    // because it also decides step 2.
    const bool inDeskContainer = parent && mAuraSurface &&
            zaura_surface_get_version(mAuraSurface) >= ZAURA_SURFACE_SET_PARENT_SINCE_VERSION;

    // 2. Create augmented_surface BEFORE role (Chrome v12 requirement)
    //
    // Not on the root of a dialog in the system modal container that will get
    // a card subsurface. exo only lets a window there become active when its
    // ROOT surface has a hit-test region (ShellSurfaceBase::OnSurfaceCommit,
    // "ShellSurfaces in system modal container are only activatable if input
    // region is non-empty"), and Surface::HasHitTestRegion() is false for any
    // augmented surface, whatever its input region. Augmenting the root made
    // the dialog permanently inactive: no click and no zaura activate could
    // focus it, so Chrome never sent it a key (the ANR / AppWarnings dialogs,
    // measured on device). The root only needs the augmenter's solid-colour
    // buffers, which cover the whole window, so the transparent background
    // colour set below is not needed on it.
    const bool rootNeedsHitTest = !inDeskContainer && subcompositor && augmenter;
    if (augmenter && !rootNeedsHitTest) {
        mAugmentedSurface = surface_augmenter_get_augmented_surface(augmenter, mSurface);
    }

    // 3. Create the remote surface: next to its app window when it has one,
    // otherwise in ash's system modal container (OVERLAY).
    //
    // A dialog with an app window goes in the app container as that window's
    // transient child (set_parent below; Chrome honours it for remote surfaces
    // with project-fydroid chromium patch 0210). ash then stacks it with the
    // app, so a window activated above the app covers the dialog too. In the
    // system modal container it would float above every app window: when the
    // dialog opened a page in a new task — the TapTap login in Phigros — the
    // page came up behind the dialog, while Android, which routes input by its
    // own stacking, sent every click and key on the dialog to that page.
    // A transient child is not an app window of its own, so the
    // tablet-mode problem below does not arise for it.
    //
    // For a dialog without one, the container decides whether the app behind
    // the dialog is still drawn.
    // In tablet mode ash shows one app window at a time, so a dialog window in
    // CONTAINER_DEFAULT becomes the top app window and everything behind it
    // stops being drawn: the dialog card on pure black, with the app gone —
    // reported from a POS tablet. The system
    // modal container is not part of that policy, so the app keeps rendering
    // underneath and the dim below lands on the app instead of on nothing.
    // Measured on device with the same dialog: DEFAULT gives (0,0,0) behind the
    // card, OVERLAY gives the app dimmed to Android's 0.6.
    //
    // This reverses 8eda937, which moved dialogs to DEFAULT to stop exo
    // logging "A surface in system modal container should not have a frame:2"
    // once per frame: exo rejects every frame type but NONE here, from the
    // commit path. FRAME_TYPE_NONE below is what makes that quiet. The shadow
    // that rejection used to preserve is gone with it — the dim now separates
    // the card from what is behind it, which is what Android does anyway.
    //
    // Still NOT system modal (no set_system_modal): a dialog left behind by a
    // crashed activity must not block input to the rest of the system.
    mInDeskContainer = inDeskContainer;
    mRemoteSurface = zcr_remote_shell_v2_get_remote_surface(
            remoteShell, mSurface,
            mInDeskContainer ? ZCR_REMOTE_SHELL_V2_CONTAINER_DEFAULT
                             : ZCR_REMOTE_SHELL_V2_CONTAINER_OVERLAY);
    if (!mRemoteSurface) {
        ALOGE("failed to create zcr_remote_surface_v2 (%s) for layer %" PRIu64,
              mInDeskContainer ? "DEFAULT" : "OVERLAY", layerId);
        if (mAuraSurface) {
        zaura_surface_destroy(mAuraSurface);
    }
    if (mAugmentedSurface) {
            augmented_surface_destroy(mAugmentedSurface);
            mAugmentedSurface = nullptr;
        }
        wl_surface_destroy(mSurface);
        mSurface = nullptr;
        return;
    }

    zcr_remote_surface_v2_add_listener(mRemoteSurface, &sModalRemoteSurfaceListener, this);
    // Before the first commit, so the window is created as the transient child.
    if (mInDeskContainer) setParent(parent);

    // 4. No frame at all: no caption, and no shadow either. Anything else in
    // the system modal container is rejected during commit and logged at the
    // window's own frame rate (see above); in the app container it keeps the
    // two placements looking the same.
    zcr_remote_surface_v2_set_frame(mRemoteSurface,
                                     ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE);

    // Dialog presented as a regular floating overlay — no set_system_modal,
    // so Chrome does NOT block input to other windows. A dialog left open
    // (e.g. by a crashed activity) won't lock up the whole system.
    // The parent app handles its own input dispatching to the dialog layer.

    // 6. Transparent background
    if (mAugmentedSurface) {
        wl_array color;
        wl_array_init(&color);
        float* rgba = static_cast<float*>(wl_array_add(&color, 4 * sizeof(float)));
        rgba[0] = 0.0f; rgba[1] = 0.0f; rgba[2] = 0.0f; rgba[3] = 0.0f;
        augmented_surface_set_background_color(mAugmentedSurface, &color);
        wl_array_release(&color);
    }

    // 6b. The card goes on a subsurface so that the root can grow into the dim
    // around it (see the class comment). Desynchronized: the composed dialog
    // frames are committed to the card on their own cadence, independent of
    // the root. Without a subcompositor or the solid-colour buffers that paint
    // the dim, stay a single card-sized surface — the pre-dim behaviour.
    if (subcompositor && augmenter) {
        mCardSurface = wl_compositor_create_surface(compositor);
        if (mCardSurface) {
            mCardSubsurface = wl_subcompositor_get_subsurface(subcompositor,
                                                              mCardSurface, mSurface);
            if (mCardSubsurface) {
                wl_subsurface_set_desync(mCardSubsurface);
            } else {
                wl_surface_destroy(mCardSurface);
                mCardSurface = nullptr;
            }
        }
    }
    if (mCardSurface) {
        // Everything keyed off a surface (focus, activation, the container
        // back-pointer set on surface()) belongs to the card; the root only
        // redirects to it, offset by where the card sits.
        wl_surface_set_user_data(mCardSurface, &mSurfaceData);
        mScrimSurfaceData.layerId = layerId;
        mScrimSurfaceData.taskId = taskId;
        mScrimSurfaceData.window = this;
        mScrimSurfaceData.inputTarget = &mSurfaceData;
        wl_surface_set_user_data(mSurface, &mScrimSurfaceData);
    } else {
        ALOGW("layer %" PRIu64 ": no card subsurface — dialog will not dim", layerId);
    }

    // 7. App ID (same format as ARC++)
    char appId[64];
    if (taskId > 0) {
        snprintf(appId, sizeof(appId), "org.chromium.arc.%d", taskId);
    } else {
        snprintf(appId, sizeof(appId), "org.chromium.arc.%" PRIu64, layerId);
    }
    zcr_remote_surface_v2_set_app_id(mRemoteSurface, appId);

    // NOTE: no commit — caller does setBoundsInOutput() then commit.

    ALOGI("WaylandModalWindow: created layer %" PRIu64 " app_id=%s (%s)",
          layerId, appId,
          mInDeskContainer ? "app container, child of its app window"
                           : "system modal container");
}

void WaylandModalWindow::setParent(zaura_surface* parent) {
    if (!mInDeskContainer || parent == mParent) return;
    mParent = parent;
    zaura_surface_set_parent(mAuraSurface, parent, 0, 0);
    ALOGI("layer %" PRIu64 ": parent app window %s", mLayerId, parent ? "set" : "cleared");
}

void WaylandModalWindow::setSystemUiVisibility(uint32_t visibility) {
    if (!mRemoteSurface) return;
    ALOGI("WaylandModalWindow: layer %" PRIu64 ": set_systemui_visibility %u",
          mLayerId, visibility);
    zcr_remote_surface_v2_set_systemui_visibility(mRemoteSurface, visibility);
    if (mSurface) wl_surface_commit(mSurface);
}

WaylandModalWindow::~WaylandModalWindow() {
    ALOGI("WaylandModalWindow: destroying layer %" PRIu64, mLayerId);
    if (mCardSubsurface) {
        wl_subsurface_destroy(mCardSubsurface);
    }
    if (mCardSurface) {
        wl_surface_destroy(mCardSurface);
    }
    if (mAugmentedSurface) {
        augmented_surface_destroy(mAugmentedSurface);
    }
    if (mRemoteSurface) {
        zcr_remote_surface_v2_destroy(mRemoteSurface);
    }
    if (mSurface) {
        wl_surface_destroy(mSurface);
    }
    if (mScrimBuffer) {
        wl_buffer_destroy(mScrimBuffer);
    }
}

void WaylandModalWindow::setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                                            int32_t w, int32_t h) {
    if (!mRemoteSurface || !output || w <= 0 || h <= 0) return;
    mOutput = output;
    mBoundsX = x;
    mBoundsY = y;
    mBoundsW = w;
    mBoundsH = h;
    applyLayout();
}

void WaylandModalWindow::setOutputSize(int32_t w, int32_t h) {
    if (w == mOutputW && h == mOutputH) return;
    mOutputW = w;
    mOutputH = h;
    // The output size only shapes the window while it is dimming.
    if (mCardSurface && mScrimAlpha > 0.0f) applyLayout();
}

void WaylandModalWindow::setScrim(float alpha, int32_t x, int32_t y,
                                  int32_t w, int32_t h) {
    // persist.fydroid.dialog_dim scales Android's dimAmount: 0 turns the dim
    // off (the window stays card-sized, the pre-dim behaviour), 1 is Android's
    // own value. Read once — it is a debug/escape hatch, so changing it needs
    // `setprop` plus `kill -9 surfaceflinger` (see sf-persist-prop-static-init).
    static const float kDimScale = []() {
        char buf[PROPERTY_VALUE_MAX] = {};
        if (property_get("persist.fydroid.dialog_dim", buf, "") <= 0) return 1.0f;
        const float v = strtof(buf, nullptr);
        return (v >= 0.0f && v <= 1.0f) ? v : 1.0f;
    }();
    alpha = std::clamp(alpha * kDimScale, 0.0f, 1.0f);
    // Whole 8-bit steps: the dim's own animation would otherwise re-lay the
    // window out on every frame for changes no one can see.
    alpha = std::round(alpha * 255.0f) / 255.0f;
    if (alpha == mScrimAlpha && x == mScrimHitX && y == mScrimHitY &&
        w == mScrimHitW && h == mScrimHitH) {
        return;
    }
    if (alpha != mScrimAlpha) ALOGI("layer %" PRIu64 ": dialog dim %.3f", mLayerId, alpha);
    mScrimAlpha = alpha;
    mScrimHitX = x;
    mScrimHitY = y;
    mScrimHitW = w;
    mScrimHitH = h;
    applyLayout();
}

void WaylandModalWindow::applyLayout() {
    if (!mRemoteSurface || !mOutput || mBoundsW <= 0 || mBoundsH <= 0) return;

    // Cover the output only while the dialog dims and the output size is
    // known; otherwise the window is just the card.
    const bool dim = mCardSurface && mScrimAlpha > 0.0f && mOutputW > 0 && mOutputH > 0;
    const int32_t winX = dim ? 0 : mBoundsX;
    const int32_t winY = dim ? 0 : mBoundsY;
    const int32_t winW = dim ? mOutputW : mBoundsW;
    const int32_t winH = dim ? mOutputH : mBoundsH;
    const int32_t cardX = mBoundsX - winX;
    const int32_t cardY = mBoundsY - winY;

    // The composer re-places a standalone modal on every present; with the
    // card on its own subsurface that would now be an extra root commit per
    // frame, so skip when nothing moved. (The single-surface path keeps the
    // old behaviour of re-sending with every buffer.)
    const float alpha = dim ? mScrimAlpha : 0.0f;
    // Where the dim takes clicks, in window coordinates.
    const int32_t hitX = mScrimHitX - winX;
    const int32_t hitY = mScrimHitY - winY;
    const Layout layout{mOutput, winX, winY, winW, winH, cardX, cardY,
                        mBoundsW, mBoundsH, hitX, hitY, mScrimHitW, mScrimHitH,
                        alpha};
    if (mCardSurface && mApplied && layout == mAppliedLayout) return;

    zcr_remote_surface_v2_set_bounds_in_output(mRemoteSurface, mOutput,
                                                winX, winY, winW, winH);
    // No set_rectangular_surface_shadow: FRAME_TYPE_NONE resets shadow_bounds_
    // on every commit in this container, so a shadow cannot survive here. The
    // dim is what sets the card apart now.
    // Single-surface window: the caller commits surface(), which is the root.
    if (!mCardSurface) return;

    // Double-buffered state of the root: lands with the commit below.
    wl_subsurface_set_position(mCardSubsurface, cardX, cardY);
    mScrimSurfaceData.inputOriginX = cardX;
    mScrimSurfaceData.inputOriginY = cardY;

    // The root needs a buffer to be mapped at all. While dimming it is the dim
    // itself — black at the dialog's dimAmount, which is exactly how Android
    // composites DIM_BEHIND. Otherwise it is transparent.
    wl_buffer* retired = nullptr;
    if (!mScrimBuffer || winW != mScrimW || winH != mScrimH || alpha != mScrimBufferAlpha) {
        wl_array color;
        wl_array_init(&color);
        float* rgba = static_cast<float*>(wl_array_add(&color, 4 * sizeof(float)));
        rgba[0] = 0.0f; rgba[1] = 0.0f; rgba[2] = 0.0f; rgba[3] = alpha;
        wl_buffer* buffer =
                surface_augmenter_create_solid_color_buffer(mAugmenter, &color, winW, winH);
        wl_array_release(&color);
        if (buffer) {
            wl_surface_attach(mSurface, buffer, 0, 0);
            wl_surface_damage(mSurface, 0, 0, winW, winH);
            retired = mScrimBuffer;
            mScrimBuffer = buffer;
            mScrimW = winW;
            mScrimH = winH;
            mScrimBufferAlpha = alpha;
        }
    }

    // Only the host app's window takes clicks on the dim: over it a click is a
    // touch outside the dialog, as on Android, and everywhere else it reaches
    // whatever window it is over, so the rest of the desktop keeps working
    // while an Android dialog is up. A dialog with no host app behind it (a
    // dialog task) reports an empty rectangle and leaves the dim inert; the
    // card itself has its own surface and is unaffected either way.
    {
        wl_region* region = wl_compositor_create_region(mCompositor);
        if (alpha > 0.0f && mScrimHitW > 0 && mScrimHitH > 0) {
            wl_region_add(region, hitX, hitY, mScrimHitW, mScrimHitH);
        }
        // In the system modal container the root must have a hit-test region
        // for exo to let the window become active (see the constructor). The
        // card's own rectangle adds no click target: the card subsurface sits
        // above the root there and takes those events itself.
        if (!mInDeskContainer) {
            wl_region_add(region, cardX, cardY, mBoundsW, mBoundsH);
        }
        wl_surface_set_input_region(mSurface, region);
        wl_region_destroy(region);
    }
    wl_surface_commit(mSurface);
    // Only once the replacement is committed.
    if (retired) wl_buffer_destroy(retired);
    mAppliedLayout = layout;
    mApplied = true;
    activateOnceMapped();
}

void WaylandModalWindow::activateOnceMapped() {
    // A dialog in ash's system modal container never becomes the active
    // window on its own: ash activates windows on click or when a client asks,
    // and nothing asked. Chrome hands wl_keyboard focus to the active window's
    // surface only, so with the dialog inactive every key went to whatever was
    // active before — a Chrome window, or no ARC surface at all — while Android
    // had already focused the dialog (it is the top focusable window). The
    // keyboard was dead on exactly the dialogs that most need it: the ANR /
    // crash dialog and the AppWarnings dialogs (measured on device: Enter on
    // DeprecatedTargetSdkVersionDialog reached InputDispatcher 0 times).
    //
    // A dialog parented to an app window (mInDeskContainer) needs none of
    // this: Chrome keeps the app window active, its surface gets the keys, and
    // Android routes them to its own focused window — the dialog.
    //
    // Once, after the root's first commit: activating an unmapped window is a
    // no-op in ash, and repeating it would pull focus back every time the
    // dialog re-lays out after the user has clicked elsewhere. Still not
    // set_system_modal: the rest of the desktop stays usable.
    if (mActivationRequested || mInDeskContainer || !mAuraSurface) return;
    if (zaura_surface_get_version(mAuraSurface) < ZAURA_SURFACE_ACTIVATE_SINCE_VERSION) {
        return;
    }
    mActivationRequested = true;
    ALOGI("layer %" PRIu64 ": aura activate (system modal container)", mLayerId);
    zaura_surface_activate(mAuraSurface);
    wl_surface_commit(mSurface);
}

void WaylandModalWindow::onClose() {
    ALOGI("layer %" PRIu64 ": modal window closed by Chrome", mLayerId);
    mClosed = true;
    if (mEventListener) {
        mEventListener->onWindowClosed(this);
    }
}


void WaylandModalWindow::setAccessibilityId(int32_t id) {
    publishAccessibilityId(mAuraSurface, id, mAccessibilityId, "modal",
                           mLayerId);
}

} // namespace android

#pragma clang diagnostic pop

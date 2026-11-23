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

// WaylandLayerContainer: One Android task (or standalone layer) → one Chrome
// window. Owns the WaylandWindow and handles Chrome events. All presentation
// flows through presentComposedBuffer() — GPU-composed buffers from HWC
// virtual displays.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandLayerContainer"

#include "WaylandLayerContainer.h"
#include "WaylandLayerDrmBufferManager.h"
#include "../WaylandClient.h"
#include "../WaylandGlobals.h"
#include "../hwc2/WaylandHwc2Composer.h"
#include "../hwc2/WaylandSessionIdResolver.h"
#include "../window/WaylandAppWindow.h"
#include "../window/WaylandModalWindow.h"
#include "../window/WaylandInputMethodWindow.h"
#include "../window/WaylandToastWindow.h"
#include "../window/WaylandOverlayWindow.h"
#include "../window/WaylandNotificationWindow.h"
#include "../window/WaylandSurfaceData.h"
#include "../binder/WaylandShellHandlerProxy.h"
#include "../input/WaylandInputManager.h"

#include <cutils/properties.h>

#include <remote-shell-unstable-v2-client-protocol.h>
#include <secure-output-unstable-v1-client-protocol.h>
#include <viewporter-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include <sync/sync.h>
#include <ui/GraphicBuffer.h>
#include <thread>
#include "../WaylandLog.h"
#include <algorithm>
#include <cmath>
#include <cstdlib>
#include <unistd.h>

// sw_sync lives in libsync but the header isn't in the default export path
extern "C" {
int sw_sync_timeline_create(void);
int sw_sync_timeline_inc(int fd, unsigned count);
int sw_sync_fence_create(int fd, const char *name, unsigned value);
}

namespace android {

static constexpr int64_t kMinimizeGraceNs = 200000000LL;  // 200ms — Chrome restore animation

// Tunables, read once at first use rather than on every focus change and every
// window state transition. These select behaviour for the whole session, so a
// per-event read only pays a syscall to re-learn a value that has not changed.
//
// Consequence: changing either property now requires a `stop ui; start ui` to
// take effect, instead of applying at the next state change.
// Shelf auto-hide applies to windows that own the screen — fullscreen apps,
// games, kiosk — never to merely maximized ones (see onFocusChanged).
// persist.fydroid.game_shelf_autohide is the legacy name for the same switch and
// is still honoured as the default, so a board that had turned it off keeps the
// shelf.
static const bool kShelfAutohide =
        property_get_bool("persist.fydroid.shelf_autohide",
                          property_get_bool("persist.fydroid.game_shelf_autohide", true));
static const bool kGameEfficiency =
        property_get_bool("persist.fydroid.game_efficiency", false);

// Ask SF to recompose after the throttle has refused a frame, so the content it
// composed and dropped still reaches the screen (see onFrameCallbackDone). On
// by default; set persist.fydroid.sf.retry_dropped_frames=0 to get the old
// drop-and-forget behaviour back for comparison.
static const bool sRetryDroppedFrames =
        property_get_bool("persist.fydroid.sf.retry_dropped_frames", true);

// --- wl_surface_frame callback ---

static void frameCallbackDone(void* data, wl_callback* /*cb*/, uint32_t /*time*/) {
    static_cast<WaylandLayerContainer*>(data)->onFrameCallbackDone();
}

static const wl_callback_listener sFrameListener = { .done = frameCallbackDone };

WaylandLayerContainer::WaylandLayerContainer(WaylandClient* client,
                                             wl_output* output,
                                             uint64_t containerLayerId,
                                             int32_t taskId,
                                             const Rect& frame)
    : mClient(client),
      mOutput(output),
      mContainerLayerId(containerLayerId),
      mTaskId(taskId),
      mContainerFrame(frame) {
    ALOGI("container created: containerLayer=%" PRIu64 " taskId=%d frame=(%d,%d,%d,%d)",
          containerLayerId, taskId,
          frame.left, frame.top, frame.right, frame.bottom);
}

bool WaylandLayerContainer::snapIsProvisional() const {
    if (mSnapProvisionalUntilNs == 0) return false;
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    const int64_t now = ts.tv_sec * 1000000000LL + ts.tv_nsec;
    return now < mSnapProvisionalUntilNs;
}

void WaylandLayerContainer::restoreSnapCarry(const SnapCarry& carry) {
    if (!carry.snapped) return;
    mSnapped = true;
    mSnappedBounds = carry.bounds;
    {
        struct timespec ts;
        clock_gettime(CLOCK_MONOTONIC, &ts);
        mSnapProvisionalUntilNs =
                ts.tv_sec * 1000000000LL + ts.tv_nsec + kSnapProvisionalNs;
    }
    ALOGI("container taskId=%d: restored snap across recreation (%d,%d %dx%d)",
          mTaskId, mSnappedBounds.left, mSnappedBounds.top,
          mSnappedBounds.right, mSnappedBounds.bottom);
    // Deliberately no set_snap_left/right echo here. The direction is Chrome's
    // to state: ash re-applies the snap to the new surface and reports it as a
    // reason 3/4 bounds change, which echoes through the normal path. All this
    // flag has to do in the meantime is stop us asking for something else.
}

bool WaylandLayerContainer::wasRecentlyMinimized() const {
    if (mRestoredFromMinimizedNs == 0) return false;
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    int64_t now = ts.tv_sec * 1000000000LL + ts.tv_nsec;
    return (now - mRestoredFromMinimizedNs) < kMinimizeGraceNs;
}

void WaylandLayerContainer::setIsGame(bool isGame) {
    // Re-delivered on every metadata push (each frame) — only act on a change.
    if (mIsGame == isGame) return;
    mIsGame = isGame;
    ALOGI("container taskId=%d: isGame=%d", mTaskId, isGame);
    // The classification usually lands after the window settled into its state,
    // so re-evaluate now rather than waiting for the next change. Only the
    // classification and the shelf — replaying the whole onStateChanged would
    // re-issue maximize()/fullscreen() and re-assert full-output bounds.
    // (The shelf no longer depends on isGame, but efficiency mode does.)
    mFillsScreen = stateFillsScreen(mCurrentState);
    mGameFullScreen = mIsGame && mFillsScreen;
    onFocusChanged(mFocused);
}

void WaylandLayerContainer::setSecure(bool secure) {
    // Re-delivered on every metadata push (each frame) — only act on a change,
    // and only ever latch ON (see the header note on why we never unprotect).
    if (!secure || mSecure) return;

    auto* globals = mClient ? mClient->globals() : nullptr;
    auto* secureOutput = globals ? globals->secureOutput() : nullptr;
    wl_surface* surf = mWindow ? mWindow->surface() : nullptr;
    if (!secureOutput || !surf) {
        // Nothing we can do but say so loudly: SF has already stopped blacking
        // out the layer (WaylandTaskOutput marks its output secure), so this is
        // the point where FLAG_SECURE content becomes capturable by ChromeOS.
        ALOGE("container taskId=%d: FLAG_SECURE content but cannot protect the "
              "surface (secure_output=%p surface=%p) — content is exposed to "
              "screen capture",
              mTaskId, secureOutput, surf);
        return;
    }

    mSecurity = zcr_secure_output_v1_get_security(secureOutput, surf);
    if (!mSecurity) {
        ALOGE("container taskId=%d: get_security failed — FLAG_SECURE content "
              "is exposed to screen capture", mTaskId);
        return;
    }
    zcr_security_v1_only_visible_on_secure_output(mSecurity);
    // The request only takes effect on the surface's next commit; SF drives a
    // frame right after the metadata push, so no explicit commit is needed here.
    mSecure = true;
    ALOGI("container taskId=%d: marked only_visible_on_secure_output", mTaskId);
}

void WaylandLayerContainer::setIsDialog(bool isDialog) {
    // Debounce the transition to MODAL. applyDialogState() feeds this the
    // per-frame dialog detection on every present. A genuine dialog task carries
    // IS_DIALOG on every frame, so the flag holds true continuously; but a normal
    // app that is TRANSIENTLY mis-flagged during its own launch flickers it for
    // only a frame or two — the task reads translucent for an instant while the
    // underlying opaque activity has not drawn yet (e.g. a splash that fires a
    // runtime-permission request and then navigates on). Latching MODAL on the
    // first true frame converted such an app into a modal that never reverts
    // (the latch below is one-way), and the permission dialog's own enter
    // animation then inflated the whole-task footprint far past the panel width,
    // leaving the app an oversized off-screen card. Require the flag to hold for
    // kDialogStableFrames consecutive frames first: a launch transient never
    // reaches it and stays a normal APP window; a real dialog reaches it in a
    // few ms and converts as before.
    static constexpr int kDialogStableFrames = 4;
    if (isDialog) {
        if (mDialogTrueFrames < kDialogStableFrames) mDialogTrueFrames++;
    } else {
        mDialogTrueFrames = 0;
    }
    const bool stableDialog = mDialogTrueFrames >= kDialogStableFrames;
    if (mIsDialog == stableDialog) return;
    ALOGI("container taskId=%d: isDialog=%d->%d (stable after %d frames)",
          mTaskId, mIsDialog, stableDialog, mDialogTrueFrames);
    mIsDialog = stableDialog;
    // A dialog renders as a frameless modal (WaylandModalWindow: no
    // caption/decoration, draws the dialog's own dim) via the
    // MODAL window category, rather than a decorated app window. Switch the
    // category and, if the window was already created as a regular app window
    // before the dialog was detected, drop it so the next present recreates it
    // as a modal (same frame — setIsDialog runs before presentComposedBuffer in
    // WaylandHwc2Composer::presentDisplay).
    //
    // Latch on the transition to dialog and never revert to APP: IsDialogTask
    // flickers on frame cadence (dim-layer transitions), and toggling the
    // window type would thrash Chrome bounds. Once modal, stay modal.
    if (stableDialog && mWindowCategory != WaylandWindowCategory::MODAL) {
        mWindowCategory = WaylandWindowCategory::MODAL;
        if (mWindow) {
            ALOGI("container taskId=%d: dropping app window to recreate as modal",
                  mTaskId);
            releaseWindow();
        }
    }
}

bool WaylandLayerContainer::presentGraphicBuffer(
        WaylandLayerDrmBufferManager* bufMgr,
        const sp<GraphicBuffer>& buffer, int* acquireFence) {
    if (!bufMgr || !buffer) return false;

    // Wait for the GPU to finish rendering into the buffer.
    if (acquireFence && *acquireFence >= 0) {
        sync_wait(*acquireFence, 1000);
        close(*acquireFence);
        *acquireFence = -1;
    }

    // Convert GraphicBuffer → wl_buffer via DMA-BUF import.
    wl_buffer* wlBuf = bufMgr->getOrCreateBuffer(buffer);
    if (!wlBuf) {
        ALOGE("container taskId=%d: failed to create wl_buffer", mTaskId);
        return false;
    }
    // Mark AFTER the commit, not before: presentComposedBuffer refuses the
    // frame when the window is hidden or the previous frame's callback has not
    // arrived, and a buffer that was never attached is not Chrome's — marking it
    // held up front would retire it from SF's pool for good.
    if (!presentComposedBuffer(wlBuf, static_cast<int32_t>(buffer->getWidth()),
                               static_cast<int32_t>(buffer->getHeight()))) {
        return false;
    }
    bufMgr->markPresented(buffer->getId());
    return true;
}

void WaylandLayerContainer::onFocusChanged(bool focused) {
    mFocused = focused;
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP || !mWindow) return;

    // Assert auto-hide while this is the focused fullscreen app. ash reads this
    // flag off the *active* window and only inside the maximized/fullscreen
    // branches of ShelfLayoutManager::CalculateShelfVisibility(), so the shelf
    // comes back on its own as soon as the window leaves those states or
    // another app takes focus — no revert push is needed for either. That is
    // also why asserting it for every app rather than only games is safe:
    // outside those two states ash does not consult the flag at all.
    //
    // Sending the VISIBLE counterpart on the way OUT of maximize is actively
    // harmful: it lands exactly while the window is leaving that state, ash
    // recomputes shelf visibility, and the resulting work-area change makes
    // Chrome re-lay-out the window straight back to maximized — restore
    // appeared to do nothing. The one place VISIBLE is pushed deliberately is
    // the tablet rule below, which is the opposite moment.
    //
    // mShelfAutoHide only suppresses redundant pushes, so it must be dropped
    // when we stop being that focused fullscreen app: since ash reads the flag
    // off whatever is active, anything else taking focus (an app's own Google
    // sign-in dialog during startup, say) legitimately restores the shelf, and
    // the assert has to be repeatable afterwards. Latching it for the lifetime
    // of the container meant the shelf never hid again once such a dialog had
    // closed and the app came back.
    if (!focused || !mFillsScreen) {
        mShelfAutoHide = false;
        return;
    }

    // Only a window that actually OWNS the screen hides the shelf: a game
    // (the original reason this assert exists — full-bleed matters more to it
    // than the shelf), kiosk and unresizable apps (both live in real ash
    // fullscreen), or a window the user put fullscreen. A merely MAXIMIZED
    // window keeps the shelf, in both layout modes:
    //
    // - Clamshell: a resizable app maximized is still a desktop window; the
    //   user asked for it to fill the work area, not to take the shelf away.
    //   (This deliberately narrows the earlier any-maximized-app behaviour.)
    // - Tablet: hiding the shelf with this flag is unstable in a specific,
    //   damaging manner. ash reads the flag off the ACTIVE window
    //   (ShelfLayoutManager::IsShelfAutoHideForFullscreenMaximized ->
    //   WindowState::ForActiveWindow) and a tablet window is only MAXIMIZED —
    //   so the multitask menu (an activatable widget) moves that lookup onto
    //   itself the moment it opens, CalculateShelfVisibility's kMaximized
    //   branch falls through to auto_hide_behavior() (kNever), the shelf
    //   returns, its 40dip inset moves the work area, and
    //   TabletModeMultitaskMenu::OnDisplayMetricsChanged fades the menu out
    //   on ANY metrics change — the panel vanished ~175ms after appearing.
    //   A shelf that simply stays visible cannot do that.
    //
    // Real ash fullscreen has neither problem: CalculateShelfVisibility's
    // kFullscreen branch returns SHELF_AUTO_HIDE or SHELF_HIDDEN on every
    // path, both insetting the work area by 0, stable under activation
    // changes. The assert on top of it selects the revealable AUTO_HIDE
    // flavour rather than fully HIDDEN.
    //
    // mCurrentState is trusted in both layout modes — applyLayoutMode records
    // the maximize it performs, so a FULLSCREEN left over from clamshell can
    // no longer stick to a window ash has since maximized for tablet (same
    // reasoning as allowedWindowRect()).
    const bool ownsScreen =
            mIsGame || mKioskPinned || mUnresizable ||
            mCurrentState == WaylandWindow::WindowState::FULLSCREEN;
    if (!ownsScreen) {
        // Declining to assert is not enough — the flag may already be latched
        // on the ash window from a state that did own the screen: a window
        // maximized after a fullscreen stint, or maximized in clamshell (under
        // the old policy) and folded into tablet. ash keeps the flag across
        // those transitions, so the shelf would stay hidden. Take it back.
        //
        // The "sending VISIBLE is actively harmful" note above is about the
        // moment a window LEAVES maximize for a restored rectangle — ash
        // recomputes shelf visibility, the work area moves, and Chrome
        // re-lays-the-window-out straight back to maximized. This push happens
        // only while the window is focused and still screen-filling (the
        // !focused/!fillsScreen early-return above ran first), where the
        // work-area change is absorbed by the maximized layout itself.
        //
        // Keyed on mShelfAutoHideSent, not mShelfAutoHide: the latter is
        // cleared on unfocus with nothing pushed, so it does not record what
        // ash is holding.
        if (mShelfAutoHideSent) {
            mShelfAutoHideSent = false;
            mShelfAutoHide = false;
            static_cast<WaylandAppWindow*>(mWindow.get())
                    ->setSystemUiVisibility(
                            ZCR_REMOTE_SURFACE_V2_SYSTEMUI_VISIBILITY_STATE_VISIBLE);
            ALOGI("container taskId=%d: shelf autohide revoked (window no "
                  "longer owns the screen)", mTaskId);
        }
        return;
    }

    if (mShelfAutoHide) return;
    if (!kShelfAutohide) return;
    mShelfAutoHide = true;
    mShelfAutoHideSent = true;

    static_cast<WaylandAppWindow*>(mWindow.get())
            ->setSystemUiVisibility(
                    ZCR_REMOTE_SURFACE_V2_SYSTEMUI_VISIBILITY_STATE_AUTOHIDE_NON_STICKY);
    ALOGI("container taskId=%d: shelf autohide asserted (fillsScreen=%d isGame=%d)",
          mTaskId, mFillsScreen, mIsGame);
}

void WaylandLayerContainer::inheritShelfAutoHide() {
    if (!mWindow || mWindowCategory != WaylandWindowCategory::MODAL) return;
    if (mShelfAutoHide) return;
    mShelfAutoHide = true;
    mShelfAutoHideSent = true;
    static_cast<WaylandModalWindow*>(mWindow.get())
            ->setSystemUiVisibility(
                    ZCR_REMOTE_SURFACE_V2_SYSTEMUI_VISIBILITY_STATE_AUTOHIDE_NON_STICKY);
    ALOGI("container layer=%" PRIu64 ": inherited shelf autohide (modal over a "
          "fullscreen app)", mContainerLayerId);
}

void WaylandLayerContainer::setModalParent(zaura_surface* parent) {
    mModalParent = parent;
    if (mWindow && mWindowCategory == WaylandWindowCategory::MODAL) {
        static_cast<WaylandModalWindow*>(mWindow.get())->setParent(parent);
    }
}

void WaylandLayerContainer::requestActivation() {
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP || !mWindow) return;
    static_cast<WaylandAppWindow*>(mWindow.get())->activate();
}

void WaylandLayerContainer::applyDialogState(bool isDialog) {
    const bool wasDialog = mIsDialog;
    setIsDialog(isDialog);
    // When the dialog is first (stably) detected, claim Android focus. Chrome
    // may activate the parent app before the modal surface exists; this
    // counteracts that initial steal. Keyed off the debounced mIsDialog
    // transition rather than the raw per-frame flag, so a transient mis-flag on
    // a launching app does not steal focus.
    if (mIsDialog && !wasDialog && mTaskId > 0) {
        mTaskResizer.setFocusedTask(mTaskId);
        ALOGI("container taskId=%d: claimed focus for dialog task", mTaskId);
    }
}

void WaylandLayerContainer::updateStandaloneBounds(wl_output* output,
                                                   int32_t left, int32_t top,
                                                   int32_t width, int32_t height) {
    if (!mWindow || !output || width <= 0 || height <= 0) return;
    mWindow->setBoundsInOutput(output, left, top, width, height);
}

// Bottom band, in dp, to treat as the keyboard when an IME gives us no usable
// content top. Sized from the two consoles that need it (224dp landscape,
// 246dp portrait); a little generous, so the dead strip above the console is
// small and the console itself is never clipped.
static constexpr float kFallbackImeBandDp = 260.0f;

// METADATA_IME_CONTENT_TOP sentinel for "this IME takes no touch anywhere".
// Must match SurfaceControl.IME_CONTENT_TOP_NO_INPUT.
static constexpr int32_t kImeContentTopNoInput = -1;

void WaylandLayerContainer::updateInputMethodBounds(wl_output* output,
                                                    int32_t left, int32_t top,
                                                    int32_t width, int32_t height,
                                                    int32_t frameLeft, int32_t frameTop,
                                                    int32_t contentTop,
                                                    const std::vector<int32_t>& touchRects) {
    if (!mWindow || mWindowCategory != WaylandWindowCategory::INPUT_METHOD) return;
    auto* imw = static_cast<WaylandInputMethodWindow*>(mWindow.get());
    imw->setDisplayFrame(frameLeft, frameTop);
    if (output && width > 0 && height > 0) {
        imw->setBoundsInOutput(output, left, top, width, height);
    }

    // The IME told WM outright that it takes no touch anywhere
    // (TOUCHABLE_INSETS_REGION + an empty touchable region, relayed as
    // METADATA_IME_CONTENT_TOP == IME_CONTENT_TOP_NO_INPUT). That is the whole
    // answer — an empty input region, no band, nothing derived from geometry.
    //
    // This is FydroidIME, whose window draws nothing at all: it carries the
    // app's IME inset while Chrome's own virtual keyboard, stacked above the
    // ARC surface in ash, takes the key presses. Its content top is the full
    // window height, indistinguishable from the degenerate value Voice Typing
    // reports while it IS drawing a console — so before the sentinel existed
    // this window fell into the fallback band below and put an invisible
    // 520px-tall hit-test strip across the bottom of the desktop, over the
    // shelf, for as long as any text field had focus. Clicks there hit the
    // carrier and went nowhere: windows would not activate and the caret stayed
    // in the field the user was trying to leave.
    if (contentTop == kImeContentTopNoInput) {
        if (!mImeNoInputLogged) {
            ALOGI("IME declares an empty touchable region on a %dx%d window; "
                  "surface takes no input", width, height);
            mImeNoInputLogged = true;
        }
        mImeFallbackBandLogged = false;
        imw->setNoInputRegion();
        return;
    }
    mImeNoInputLogged = false;

    // The IME named the exact rects it takes touch in (TOUCHABLE_INSETS_REGION
    // with a non-empty region, appended to the ImeContentTop metadata). That
    // beats anything derived from a content top: Gboard with a hardware
    // keyboard keeps a full-display window, reports contentTop == its height,
    // and draws only a small toolbar at the LEFT edge — the fallback band below
    // missed it entirely and its buttons could not be clicked.
    if (!touchRects.empty()) {
        if (mImeFallbackBandLogged) {
            ALOGI("IME touchable region reported; fallback band no longer in effect");
            mImeFallbackBandLogged = false;
        }
        imw->setInputRects(touchRects);
        return;
    }

    // A DEGENERATE contentTop on a full-display IME window means the IME is
    // telling us nothing usable. Google Voice Typing does exactly this: in
    // landscape it keeps a full-display (2560x1600) window, draws its ~403px
    // "tap to speak" console bottom-anchored inside it, and reports
    // mGivenContentInsets.top = the WHOLE window height ("I reserve no space
    // for myself"). 0 means the mirror image ("I am the whole window").
    // Android is content with either, because there its IME window only ever
    // covers the app being typed into; here the same surface covers the whole
    // Chrome OS desktop, so no region = every desktop tap swallowed.
    //
    // Nothing in Android describes the drawn console: mGivenVisibleInsets is
    // the same degenerate value, the dispatcher's touchableRegion is the whole
    // frame (TOUCHABLE_INSETS_FRAME), and the layer's transparentRegionHint is
    // empty. So fall back to a bottom band of kFallbackImeBandDp. Measured on
    // device: the console is 224dp in landscape and 246dp in portrait, and a
    // keyboard IME never lands here at all (Gboard reports a real contentTop).
    //
    // Only for a full-display window. When the IME sizes its window to its own
    // content — Voice Typing in PORTRAIT is 1600x443 with contentTop 0 — the
    // surface already IS the band and restricting it further would cut the
    // console in half.
    int32_t effectiveContentTop = contentTop;
    const bool degenerate = (contentTop <= 0 || contentTop >= height);
    bool usingFallbackBand = false;
    if (degenerate && mOutputHeight > 0 && height >= mOutputHeight) {
        const int32_t band = static_cast<int32_t>(
                kFallbackImeBandDp * (mAndroidDsf > 0.0f ? mAndroidDsf : 1.0f));
        if (band > 0 && band < height) {
            effectiveContentTop = height - band;
            usingFallbackBand = true;
            // A headless/full-display IME holds this state for as long as it
            // has focus (10-30Hz of frames for minutes at a time) — log the
            // transition, not every frame.
            if (!mImeFallbackBandLogged) {
                ALOGI("IME contentTop=%d unusable on a %dx%d full-display "
                      "window; restricting input to a %dpx bottom band",
                      contentTop, width, height, band);
            }
        }
    }
    if (usingFallbackBand != mImeFallbackBandLogged) {
        if (!usingFallbackBand) {
            ALOGI("IME contentTop=%d usable again on a %dx%d window; "
                  "fallback band no longer in effect", contentTop, width, height);
        }
        mImeFallbackBandLogged = usingFallbackBand;
    }
    contentTop = effectiveContentTop;

    // After the bounds: the region is computed against them.
    //
    // This is the Wayland counterpart of what Chrome's own virtual keyboard
    // does with chrome.virtualKeyboardPrivate.setHitTestBounds(), which ends in
    // KeyboardUIController::SetHitTestBounds() installing a ShapedWindowTargeter
    // so clicks outside the keys fall through to the UI beneath — that is why
    // Chrome OS buttons keep working under Chrome's keyboard. zcr_input_method
    // _surface_v2 has no such request; the only equivalent is the surface input
    // region, which exo stores as Surface::hit_test_region_ (surface.cc: unset
    // => the WHOLE surface) and consults from CustomWindowTargeter::
    // EventLocationInsideBounds(). It is also what InputMethodSurface::
    // OnSurfaceCommit() converts to DIP for the bounds it reports to ash.
    imw->setContentTop(contentTop);
}

void WaylandLayerContainer::updateToastBounds(wl_output* output,
                                              int32_t screenW, int32_t screenH,
                                              int32_t toastW, int32_t toastH) {
    if (!mWindow || mWindowCategory != WaylandWindowCategory::TOAST) return;
    static_cast<WaylandToastWindow*>(mWindow.get())
            ->setCenteredBoundsInOutput(output, screenW, screenH, toastW, toastH);
}

void WaylandLayerContainer::configureNotificationWindow(int32_t androidDisplayId,
                                                        int32_t frameLeft,
                                                        int32_t frameTop) {
    if (!mWindow) return;
    auto* nw = static_cast<WaylandNotificationWindow*>(mWindow.get());
    nw->setDisplayId(androidDisplayId);
    nw->setDisplayFrame(frameLeft, frameTop);
}

void WaylandLayerContainer::updateNotificationFrame(int32_t frameLeft,
                                                    int32_t frameTop) {
    if (!mWindow) return;
    static_cast<WaylandNotificationWindow*>(mWindow.get())
            ->setDisplayFrame(frameLeft, frameTop);
}

WaylandLayerContainer::~WaylandLayerContainer() {
    // Tell the HWC composer to drop its cached pointers to us BEFORE anything
    // else, so no path can reach a half-destroyed container either.
    //
    // VirtualDisplayData caches the container it resolved and only re-resolves
    // when the cached one reports isClosed() — but that report is a read through
    // the stale pointer, and a container is freed the moment its owning map
    // erases it, which happens from several places that have no idea a virtual
    // display is still pointing at them (destroyContainer, the drain in
    // presentDisplay, the rebuild paths in registerStandaloneContainer). A
    // recycled heap block reads back as live and not-closed, so the next
    // presentTaskVirtualDisplay ran straight into presentGraphicBuffer ->
    // ensureWindow and dereferenced a garbage mClient: SIGSEGV in SurfaceFlinger
    // at a fault address of 0x3f800000, the float 1.0f the block had been reused
    // for. Clearing here covers every teardown path, including future ones.
    if (auto* hwc2 = Hwc2::WaylandHwc2Composer::getInstance()) {
        hwc2->forgetContainer(this);
    }
    // Same problem, same reasoning, in the input handlers: WaylandPointerHandler
    // and WaylandTouchHandler cache the container behind the focused surface as
    // a bare WaylandLayerContainer* and read its frame, input scale and PIP
    // state on every motion or touch event. Their clearFocusIfWindow() hook
    // could not cover it — a task holds focus through mFocusContainer with
    // mFocusWindow left null — and only three of the six container teardown
    // paths called even that. Doing it here covers all of them.
    if (auto* im = WaylandInputManager::instance()) {
        im->clearFocusIfContainer(this);
    }
    if (mFrameCallback) {
        wl_callback_destroy(mFrameCallback);
        mFrameCallback = nullptr;
    }
    if (mPresentTimelineFd >= 0) { close(mPresentTimelineFd); mPresentTimelineFd = -1; }
    if (mPresentFenceFd >= 0) { close(mPresentFenceFd); mPresentFenceFd = -1; }
    // Destroy the security object before the surface it extends.
    if (mSecurity) {
        zcr_security_v1_destroy(mSecurity);
        mSecurity = nullptr;
    }
    releaseWindow();
}

void WaylandLayerContainer::releaseWindow() {
    if (!mWindow) return;
    // The input handlers cache the focused WaylandWindow* as well, and the
    // keyboard dereferences it on every keystroke. Clear it BEFORE the window
    // is destroyed rather than after: clearFocusIfWindow takes each handler's
    // focus mutex, which the wayland dispatch thread holds for the whole of an
    // event, so once this returns no in-flight event can still be inside the
    // window. Clearing afterwards would leave that window open.
    //
    // Every path that drops the window goes through here — the destructor and
    // the app-window-to-modal conversion in applyDialogState() — so a future
    // one gets the teardown right by construction.
    if (auto* im = WaylandInputManager::instance()) {
        im->clearFocusIfWindow(mWindow.get());
    }
    {
        // A wp_viewport outliving its wl_surface is a protocol error on its
        // next request, so it goes first.
        std::lock_guard<std::mutex> lock(mMaskMutex);
        if (mMask != ResizeMask::kOff) dropResizeMaskLocked("window released");
    }
    mWindow.reset();
}

bool WaylandLayerContainer::ensureWindowForCategory(WaylandWindowCategory cat,
                                                     uint64_t layerId) {
    mWindowCategory = cat;
    return ensureWindow(layerId);
}

bool WaylandLayerContainer::ensureWindow(uint64_t layerId) {
    if (mWindow) return true;

    switch (mWindowCategory) {
        case WaylandWindowCategory::INPUT_METHOD: {
            auto window = std::make_unique<WaylandInputMethodWindow>(
                    mClient->getCompositor(), mClient->getRemoteShell(),
                    layerId, mClient->getAuraShell());
            if (!window->surface()) {
                ALOGE("WaylandInputMethodWindow creation failed for layer=%" PRIu64, layerId);
                return false;
            }
            wl_surface_commit(window->surface());
            mWindow = std::move(window);
            ALOGI("container taskId=%d: INPUT_METHOD window created (layer=%" PRIu64 ")",
                  mTaskId, layerId);
            break;
        }

        case WaylandWindowCategory::TOAST: {
            auto window = std::make_unique<WaylandToastWindow>(
                    mClient->getCompositor(), mClient->getRemoteShell(),
                    layerId, mScaleFactor, mClient->getAuraShell());
            if (!window->surface()) {
                ALOGE("WaylandToastWindow creation failed for layer=%" PRIu64, layerId);
                return false;
            }
            wl_surface_commit(window->surface());
            mWindow = std::move(window);
            ALOGI("container taskId=%d: TOAST window created (layer=%" PRIu64 ")",
                  mTaskId, layerId);
            break;
        }

        case WaylandWindowCategory::OVERLAY: {
            auto window = std::make_unique<WaylandOverlayWindow>(
                    mClient->getCompositor(), mClient->getRemoteShell(),
                    mOutput, layerId, mScaleFactor, mClient->getAuraShell());
            if (!window->surface()) {
                ALOGE("WaylandOverlayWindow creation failed for layer=%" PRIu64, layerId);
                return false;
            }
            wl_surface_commit(window->surface());
            mWindow = std::move(window);
            ALOGI("container taskId=%d: OVERLAY window created (layer=%" PRIu64 ")",
                  mTaskId, layerId);
            break;
        }

        case WaylandWindowCategory::NOTIFICATION: {
            auto window = std::make_unique<WaylandNotificationWindow>(
                    mClient->getCompositor(), mClient->getRemoteShell(),
                    layerId, mNotifKey, mScaleFactor, mClient->getAuraShell());
            if (!window->surface()) {
                ALOGE("WaylandNotificationWindow creation failed for layer=%" PRIu64
                      " key=%s", layerId, mNotifKey.c_str());
                return false;
            }
            wl_surface_commit(window->surface());
            mWindow = std::move(window);
            ALOGI("container taskId=%d: NOTIFICATION window created (layer=%" PRIu64
                  " key=%s)", mTaskId, layerId, mNotifKey.c_str());
            break;
        }

        case WaylandWindowCategory::MODAL: {
            // Dialog — a frameless modal (a dialog task, or a dialog split out
            // of its app). SF controls the geometry; the window draws the
            // dialog's DIM_BEHIND itself (WaylandModalWindow::setScrimAlpha).
            int32_t fw = mContainerFrame.right;   // width (left=0)
            int32_t fh = mContainerFrame.bottom;  // height (top=0)
            auto window = std::make_unique<WaylandModalWindow>(
                    mClient->getCompositor(), mClient->getRemoteShell(),
                    mOutput, mContainerLayerId, mTaskId,
                    mClient->getSurfaceAugmenter(), mClient->getAuraShell(),
                    mClient->getSubcompositor(), mModalParent);
            if (!window->surface()) {
                ALOGE("WaylandModalWindow creation failed for taskId=%d", mTaskId);
                return false;
            }
            window->setEventListener(this);
            // Buffer is at DSF resolution — tell Chrome so it maps px<->DIP.
            // Before the bounds: setBoundsInOutput() makes the first commit of
            // the root, and the window should map at the right scale.
            if (window->remoteSurface()) {
                float sf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
                uint32_t asUint;
                memcpy(&asUint, &sf, sizeof(float));
                zcr_remote_surface_v2_set_scale_factor(window->remoteSurface(), asUint);
            }
            window->setOutputSize(mOutputWidth, mOutputHeight);
            if (auto* globals = WaylandGlobals::instance()) {
                window->setScrim(globals->modalScrimAlpha(), globals->modalScrimX(),
                                 globals->modalScrimY(), globals->modalScrimW(),
                                 globals->modalScrimH());
            }
            // Center the (content-sized) card on screen unless Chrome has
            // already assigned an offset.
            if (fw > 0 && fh > 0) {
                int32_t ox = mWindowOffsetX, oy = mWindowOffsetY;
                if (ox == 0 && oy == 0 && mOutputWidth > 0 && mOutputHeight > 0) {
                    ox = (mOutputWidth - fw) / 2; if (ox < 0) ox = 0;
                    oy = (mOutputHeight - fh) / 2; if (oy < 0) oy = 0;
                }
                window->setBoundsInOutput(mOutput, ox, oy, fw, fh);
            }
            wl_surface_commit(window->surface());
            mWindow = std::move(window);

            auto* sd = static_cast<WaylandSurfaceData*>(
                    wl_surface_get_user_data(mWindow->surface()));
            if (sd) { sd->taskId = mTaskId; sd->container = this; }

            ALOGI("container taskId=%d: MODAL dialog window created (layer=%"
                  PRIu64 ")", mTaskId, layerId);
            break;
        }

        case WaylandWindowCategory::APP:
        default: {
            {
                // Normal app task — decorated WaylandAppWindow.
                int32_t sid = mSessionId;
                if (sid <= 0) {
                    sid = WaylandSessionIdResolver::resolve(mTaskId);
                }

                uint32_t windowType = 1; // APPLICATION_STARTING
                auto window = std::make_unique<WaylandAppWindow>(
                        mClient->getCompositor(), mClient->getRemoteShell(), mOutput,
                        mContainerLayerId, windowType, mTaskId, 1 /*bufferScale*/,
                        mClient->getAuraShell(), sid, mClient->getSurfaceAugmenter());

                if (!window->surface()) {
                    ALOGE("WaylandAppWindow creation failed for taskId=%d", mTaskId);
                    return false;
                }

                window->setEventListener(this);
                applyResizeLimits(window.get());

                // Set initial bounds before first commit.
                // Container frame is zero-origin: (0, 0, width, height).
                int32_t fw = mContainerFrame.right;   // width (since left=0)
                int32_t fh = mContainerFrame.bottom;  // height (since top=0)
                if (fw > 0 && fh > 0) {
                    int32_t ox, oy;
                    if (mWindowOffsetX == 0 && mWindowOffsetY == 0 &&
                        mOutputWidth > 0 && mOutputHeight > 0) {
                        // Center on screen for initial placement
                        ox = (mOutputWidth - fw) / 2;
                        oy = (mOutputHeight - fh) / 2;
                        if (ox < 0) ox = 0;
                        if (oy < 0) oy = 0;
                    } else {
                        ox = mWindowOffsetX;
                        oy = mWindowOffsetY;
                    }
                    // Remember where we actually place the window (centered for
                    // a fresh freeform task) so the IME cursor offset reflects
                    // Chrome's on-screen position instead of the (0,0) render
                    // origin. Without this getActiveTaskWindowOffset returns 0
                    // and the candidate panel lands at the window-local point.
                    mWindowOffsetX = ox;
                    mWindowOffsetY = oy;
                    // Seed the PIP restore origin. A window that is PIP'd
                    // without ever being moved first gets no non-PIP
                    // bounds_changed at all, so this placement is the only
                    // record of where it started — without it the restore falls
                    // back to the live (PIP) origin and lands clamped in a
                    // corner.
                    mPip.setRestoreOrigin(ox, oy);
                    if (mGameEfficiency) {
                        float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
                        window->setBoundsInOutput(mOutput,
                                static_cast<int32_t>(ox * dsf),
                                static_cast<int32_t>(oy * dsf),
                                static_cast<int32_t>(fw * dsf),
                                static_cast<int32_t>(fh * dsf));
                    } else {
                        window->setBoundsInOutput(mOutput, ox, oy, fw, fh);
                    }
                }

                // Tell Chrome the buffer is at DSF resolution so it correctly
                // converts between pixel coords and DIP.  In game fullscreen
                // mode, use 1.0 so Chrome upscales (efficiency mode).
                if (window->remoteSurface()) {
                    float sf = mGameEfficiency ? 1.0f
                            : ((mScaleFactor > 0.0f) ? mScaleFactor : 1.0f);
                    uint32_t asUint;
                    memcpy(&asUint, &sf, sizeof(float));
                    zcr_remote_surface_v2_set_scale_factor(window->remoteSurface(), asUint);
                }

                // Caption colours, before the initial commit as the protocol
                // asks. A window created while the colours are already known
                // must not come up in ash's default grey and correct itself a
                // frame later — the pushes elsewhere only reach windows that
                // already exist. The app's colour may already be here too: a
                // TaskDescription can land on the container before its window
                // does, and pushFrameColors() drops it while mWindow is null.
                if (mClient && mClient->globals()) {
                    auto* g = mClient->globals();
                    if (g->hasFrameColors()) {
                        mHeaderFrameActive = g->frameColorActive();
                        mHeaderFrameInactive = g->frameColorInactive();
                    }
                    // An app reports its TaskDescription well before its
                    // window exists, so this is the normal way an app colour
                    // arrives — not a rare catch-up path.
                    g->taskFrameColor(mTaskId, &mTaskFrameActive,
                                      &mTaskFrameInactive);
                }
                const uint32_t initActive =
                        mTaskFrameActive ? mTaskFrameActive : mHeaderFrameActive;
                const uint32_t initInactive =
                        mTaskFrameActive ? mTaskFrameInactive : mHeaderFrameInactive;
                if (initActive) {
                    window->setFrameColors(initActive, initInactive);
                }

                wl_surface_commit(window->surface());
                mWindow = std::move(window);
            }

            // Fill in task/container on the surface's user data
            auto* sd = static_cast<WaylandSurfaceData*>(
                    wl_surface_get_user_data(mWindow->surface()));
            if (sd) {
                sd->taskId = mTaskId;
                sd->container = this;
            }

            ALOGI("container taskId=%d: app window created (layer=%" PRIu64
                  ") isDialog=%d", mTaskId, layerId, mIsDialog ? 1 : 0);

            // Learn whether this task is ALREADY in PIP before anything else
            // touches the window.
            //
            // processPendingPipState drops the state when no container exists
            // yet, so a task that is born pinned — the multi-activity path, e.g.
            // VLC's PiP button — has its pip=1 recorded only in the globals. It
            // must be recovered HERE, ahead of the layout-mode block below,
            // because in tablet mode that block maximizes the window and
            // switches its windowing mode, which takes the task straight back
            // out of PINNED. Measured on device: "requesting maximize" →
            // "switching windowing mode 0 → 5" → "pip replay on window create"
            // → "task 156 pip=false", i.e. the PIP was undone 1ms after being
            // asked for and VLC's PiP button appeared to do nothing at all.
            if (!mPip.isPip() && mClient && mClient->globals() &&
                mClient->globals()->taskInPip(mTaskId)) {
                int32_t pw = 0, ph = 0;
                if (mClient->globals()->taskPipBounds(mTaskId, &pw, &ph)) {
                    mPip.setBounds(pw, ph);
                }
                mPip.setPip(true);
                ALOGI("container taskId=%d: pip state recovered from globals "
                      "(arrived before the window existed)", mTaskId);
            }

            // Same recovery for a kiosk pin, and for the stronger version of
            // the same reason: the PIP state can merely *race* window
            // creation, whereas a kiosk pin is guaranteed to have been
            // announced to a container that no longer exists.
            //
            // Suspending the board powers the display down and takes the
            // task's virtual display with it; resume rebuilds the whole stack,
            // so this window belongs to a brand-new container whose
            // mKioskPinned is false. Nothing re-announces the pin — Android
            // never left LOCK_TASK_MODE_PINNED and the task was never
            // re-created — so without this the kiosk replay below cannot fire,
            // ash's caption-inset bounds (reason=5, y = kCaptionHeightDip) go
            // unrefused for want of both the flag and the budget it arms, and
            // the app comes back as an ordinary captioned window sitting 32dip
            // down the screen. Measured on device across two resumes.
            //
            // Ahead of the layout-mode block below for the same reason the PIP
            // recovery is: applyLayoutMode() reads mKioskPinned.
            if (!mKioskPinned && mTaskId > 0 && mClient && mClient->globals() &&
                mClient->globals()->taskKioskPinned(mTaskId)) {
                mKioskPinned = true;
                ALOGI("container taskId=%d: kiosk pin recovered from globals "
                      "(container rebuilt, e.g. resume from suspend)", mTaskId);
            }

            // If Chrome is in tablet mode, apply it to this newly-created
            // app window so it starts frameless at (0,0). Reset
            // mAppliedLayoutMode so applyLayoutMode actually runs.
            //
            // NOT for a PIP window: tablet mode means "fill the work area",
            // which is the one thing a PIP window must never do, and the
            // maximize it performs un-pins the task outright. The PIP replay
            // below is what configures this window instead.
            if (!mPip.isPip() && mClient && mClient->globals()) {
                ChromeLayoutMode curMode = mClient->globals()->layoutMode();
                if (curMode == ChromeLayoutMode::kTablet) {
                    mAppliedLayoutMode = ChromeLayoutMode::kUnknown;
                    applyLayoutMode(curMode, mOutputWidth, mOutputHeight);
                }
            }

            // Same replay for kiosk: the pin is driven from the Android side
            // (ArcWindowManagerService::maybePinKioskTask on onTaskCreated), so
            // the TRUSTED_PINNED state_type_changed can land before this window
            // exists — onStateChanged's work is all guarded on mWindow and is
            // simply dropped when it does. mKioskPinned survives (it is a plain
            // member set before those guards), so re-apply the kiosk geometry
            // here and the window starts frameless and full-screen instead of
            // waiting for some later state change to correct it. For a
            // container rebuilt mid-session there is no surviving member to
            // read, which is what the globals recovery above restores.
            //
            // PIP needs the same replay for the same reason, and takes
            // precedence: an app can call enterPictureInPictureMode() from
            // onCreate, so ArcWindowManagerService's pip=1 routinely lands
            // before this window exists and pushPipState() drops it while
            // mWindow is null. The controller's state survives, so re-send here. It
            // must win over the two branches below — a PIP window that was
            // also forced fullscreen for being unresizable is neither.
            // ...but the controller only learns the state from applyPipState,
            // and that never ran if the container did not exist yet:
            // processPendingPipState looks the container up by task id and
            // drops the message when there is none (measured 7ms before this
            // window was created, for an app that PIPs from onCreate). Its
            // comment says "the window reads it at creation" — nothing ever
            // did, which is why such an app got an ordinary captioned window
            // and no PIP request was ever sent. Read it back here.
            if (mPip.isPip()) {
                ALOGI("container taskId=%d: pip replay on window create", mTaskId);
                // stateChanged=true: as far as this brand-new window is
                // concerned it is entering PIP now, so it needs the request and
                // the input suppression, not just the size.
                pushPipState(true);
            } else if (mKioskPinned) {
                ALOGI("container taskId=%d: kiosk replay on window create", mTaskId);
                applyKioskFullscreen(mOutputWidth, mOutputHeight);
            } else if (mUnresizable) {
                // An unresizable app opens fullscreen rather than in whatever
                // freeform rectangle the launch bounds picked — otherwise the
                // first thing the user sees is a windowed app they cannot
                // resize out of.
                applyUnresizableFullscreen(mOutputWidth, mOutputHeight);
            }
            break;
        }
    }

    // Catch up on the accessibility window id, for every category. Like the
    // caption colours and the PIP state above, this normally arrives before
    // the surface exists — ArcAccessibilityHelperService names a surface as
    // soon as Android reports a window for it, which is well before the first
    // frame reaches SurfaceFlinger — and processPendingAccessibilityIds drops
    // the message while there is no container to hand it to.
    if (mWindow && mClient && mClient->globals()) {
        auto* g = mClient->globals();
        int32_t a11yId = -1;
        switch (mWindowCategory) {
            case WaylandWindowCategory::NOTIFICATION:
                a11yId = g->accessibilityWindowId(
                        WaylandGlobals::AccessibilityTarget::kNotification, 0,
                        mNotifKey);
                break;
            case WaylandWindowCategory::INPUT_METHOD:
                a11yId = g->accessibilityWindowId(
                        WaylandGlobals::AccessibilityTarget::kInputMethod, 0, "");
                break;
            case WaylandWindowCategory::APP:
            case WaylandWindowCategory::MODAL:
                a11yId = g->accessibilityWindowId(
                        WaylandGlobals::AccessibilityTarget::kTask, mTaskId, "");
                // A task-less MODAL (TYPE_SYSTEM_ALERT: the ANR / crash
                // dialog) is named by its layer, like a toast.
                if (a11yId < 0) a11yId = mStandaloneA11yId;
                break;
            default:
                // TOAST, OVERLAY and task-less MODAL windows have no name the
                // accessibility service could address them by. Their id comes
                // from the layer's own METADATA_ACCESSIBILITY_ID instead — see
                // setStandaloneAccessibilityId.
                a11yId = mStandaloneA11yId;
                break;
        }
        if (a11yId >= 0) {
            mWindow->setAccessibilityId(a11yId);
        }
    }

    return mWindow != nullptr;
}

void WaylandLayerContainer::onFrameCallbackDone() {
    // Signal the sw_sync timeline — Chrome has composited this frame.
    // This unblocks the sync fence held by WaylandDisplay. The timeline itself
    // is persistent (see presentComposedBuffer); advance it by one rather than
    // tearing it down, and only if a fence is actually outstanding.
    if (mPresentTimelineFd >= 0 &&
        mFenceSeq.load(std::memory_order_acquire) >
                mTimelineValue.load(std::memory_order_acquire)) {
        sw_sync_timeline_inc(mPresentTimelineFd, 1);
        mTimelineValue.fetch_add(1, std::memory_order_release);
    }
    mFrameCallbackPending.store(false, std::memory_order_release);

    // A frame the throttle refused was composed by SF and then thrown away, and
    // nothing will bring it back: the layers behind it are unchanged, so SF has
    // no reason to run another composition until the app produces a new frame.
    // For a video that is simply a dropped frame — the playback stutter. Now
    // that the window can accept a commit again, ask SF for one more pass so
    // the content it already rendered actually reaches the screen.
    //
    // Self-limiting: the nudge is only armed by a refusal, and a refusal can
    // only happen while a callback is outstanding, so this fires at most once
    // per Chrome frame and stops as soon as SF catches up.
    if (sRetryDroppedFrames &&
        mDroppedSinceCommit.exchange(false, std::memory_order_acq_rel)) {
        if (auto* hwc2 = Hwc2::WaylandHwc2Composer::getInstance()) {
            hwc2->scheduleRefresh();
        }
    }
}

int WaylandLayerContainer::takePresentFence() {
    int fd = mPresentFenceFd;
    mPresentFenceFd = -1;
    return fd;
}

bool WaylandLayerContainer::presentComposedBuffer(wl_buffer* composedBuffer,
                                                  int32_t bufWidth,
                                                  int32_t bufHeight) {
    if (!composedBuffer) return false;

    // Ensure window exists (lazy creation).
    if (!mWindow) {
        ALOGI("container taskId=%d: presentComposedBuffer creating window",
              mTaskId);
        if (!ensureWindow(mContainerLayerId)) {
            ALOGE("container taskId=%d: ensureWindow failed", mTaskId);
            return false;
        }
    }

    if (!mWindow->isVisible()) {
        ALOGVT("container taskId=%d: presentComposedBuffer window not visible", mTaskId);
        mPresentHiddenCount++;
        logPresentStats();
        return false;
    }

    // Drag-resize mask: while it is up, live frames are the old-size content
    // and are not shown. Held through the commit below so a mask commit from
    // the wayland thread cannot interleave with this attach.
    std::lock_guard<std::mutex> maskLock(mMaskMutex);
    if (mMask == ResizeMask::kRequested && mMaskTransition) {
        // The window already has its new size and this frame is the old
        // content in it; hold it back until the mask covers the window.
        const bool timedOut =
                systemTime(SYSTEM_TIME_MONOTONIC) >= mMaskDeadlineNs;
        if (!mMaskContentReady && !timedOut) {
            mMaskDroppedFrames++;
            return false;
        }
        // The resized content (or the deadline) arrived before any mask did.
        mMask = ResizeMask::kOff;
        mMaskTransition = false;
        mMaskContentReady = false;
    }
    if (mMask == ResizeMask::kShown) {
        mMaskDroppedFrames++;
        return false;
    }
    if (mMask == ResizeMask::kSettling) {
        const bool timedOut =
                systemTime(SYSTEM_TIME_MONOTONIC) >= mMaskDeadlineNs;
        if (!mMaskContentReady && !timedOut) {
            mMaskDroppedFrames++;
            return false;
        }
        // This frame replaces the mask. It must actually go out: dropping the
        // viewport and then being refused by the throttle below would leave
        // the tiny mask buffer shown unscaled at the next commit.
        if (mWindowCategory != WaylandWindowCategory::NOTIFICATION &&
            mFrameCallbackPending.load(std::memory_order_acquire)) {
            mPresentThrottledCount++;
            mDroppedSinceCommit.store(true, std::memory_order_release);
            return false;
        }
        dropResizeMaskLocked(mMaskContentReady ? "content ready" : "timeout");
    }

    // Throttle: skip if Chrome hasn't composited the previous frame.
    // Skip the throttle for notification surfaces — Chrome doesn't fire
    // wl_surface frame callbacks for zcr_notification_surface_v2 while
    // the notification panel is collapsed/hidden, so the pending flag
    // would stay true forever, causing subsequent invalidates (icon
    // toggling on a media notification, for example) to be dropped.
    // Notifications update at human pace, not vsync, so unconditional
    // commit is safe.
    if (mWindowCategory != WaylandWindowCategory::NOTIFICATION &&
        mFrameCallbackPending.load(std::memory_order_acquire)) {
        mPresentThrottledCount++;
        mDroppedSinceCommit.store(true, std::memory_order_release);
        logPresentStats();
        return false;
    }

    // Create the sw_sync fence for this frame. The frame callback signals it.
    //
    // The timeline is per-container and persistent: it used to be created (and
    // closed) on every frame, costing an open() plus one or two close() calls
    // per frame per window. A timeline is designed to be long-lived and
    // advanced through increasing fence values, so create it once and hand out
    // fences at successive points on it.
    if (mPresentTimelineFd < 0) {
        mPresentTimelineFd = sw_sync_timeline_create();
        mTimelineValue = 0;
        mFenceSeq = 0;
    }
    if (mPresentTimelineFd >= 0) {
        // A previous fence can still be outstanding if no frame callback
        // arrived — the NOTIFICATION path deliberately skips the throttle
        // above, so it can present repeatedly without one. Advance the timeline
        // past it first: the old per-frame code released such a fence by
        // closing its timeline, and leaving it unsignaled would stall whoever
        // merged it.
        const uint32_t seq = mFenceSeq.load(std::memory_order_acquire);
        const uint32_t signaled = mTimelineValue.load(std::memory_order_acquire);
        if (seq > signaled) {
            sw_sync_timeline_inc(mPresentTimelineFd, seq - signaled);
            mTimelineValue.store(seq, std::memory_order_release);
        }
        if (mPresentFenceFd >= 0) close(mPresentFenceFd);
        const uint32_t next = seq + 1;
        mFenceSeq.store(next, std::memory_order_release);
        mPresentFenceFd = sw_sync_fence_create(mPresentTimelineFd,
                                               "ContainerRelease", next);
    }

    // Attach composed buffer.
    wl_surface_attach(mWindow->surface(), composedBuffer, 0, 0);

    // Damage the whole buffer.
    //
    // This is what tells the compositor the contents changed, and getting it
    // wrong is invisible from here: the frame is attached and committed, the
    // frame callback comes back, every counter says the frame was delivered —
    // and the compositor keeps showing the previous contents because as far as
    // it knows nothing was dirtied.
    //
    // Two things matter. Use buffer coordinates: wl_surface.damage is in
    // SURFACE coordinates, so the compositor has to transform it by buffer_scale
    // and the viewport before it can apply it to the buffer, and our surface is
    // deliberately not our buffer (the pool allocates on a 256px granularity and
    // Chrome scales the window). wl_surface.damage_buffer, added in wl_surface
    // v4, is stated in buffer pixels and skips that transform entirely.
    //
    // And state a real rectangle: (0, 0, INT32_MAX, INT32_MAX) survives no
    // arithmetic. Anything that scales or intersects it overflows, and an
    // overflowed damage rect can come out empty — the same "delivered but never
    // displayed" failure, just harder to see. The buffer's own extent is exact
    // and always finite.
    wl_surface* surface = mWindow->surface();
    if (bufWidth > 0 && bufHeight > 0 &&
        wl_proxy_get_version(reinterpret_cast<wl_proxy*>(surface)) >=
                WL_SURFACE_DAMAGE_BUFFER_SINCE_VERSION) {
        wl_surface_damage_buffer(surface, 0, 0, bufWidth, bufHeight);
    } else {
        // Pre-v4 compositor: surface coordinates are all there is. Use the
        // window extent rather than an unbounded rectangle.
        const int32_t w = (mOutputWidth > 0) ? mOutputWidth : bufWidth;
        const int32_t h = (mOutputHeight > 0) ? mOutputHeight : bufHeight;
        wl_surface_damage(surface, 0, 0, w > 0 ? w : 1, h > 0 ? h : 1);
    }

    // Frame callback
    if (mFrameCallback) {
        wl_callback_destroy(mFrameCallback);
        mFrameCallback = nullptr;
    }
    mFrameCallback = wl_surface_frame(mWindow->surface());
    wl_callback_add_listener(mFrameCallback, &sFrameListener, this);

    // Commit — do NOT flush here. The physical display's presentDisplay()
    // does one batch wl_display_flush() covering all queued Wayland requests.
    wl_surface_commit(mWindow->surface());
    mFrameCallbackPending.store(true, std::memory_order_release);
    // Whatever was dropped earlier has now been recomposed and sent.
    mDroppedSinceCommit.store(false, std::memory_order_release);

    mPresentOkCount++;
    logPresentStats();
    return true;
}

void WaylandLayerContainer::logPresentStats() {
    const uint32_t refused = mPresentThrottledCount + mPresentHiddenCount;
    if (refused == 0) return;  // nothing to report on a healthy window

    const nsecs_t now = systemTime(SYSTEM_TIME_MONOTONIC);
    if (mPresentStatsNextLogNs == 0) {
        mPresentStatsNextLogNs = now + s2ns(1);
        return;
    }
    if (now < mPresentStatsNextLogNs) return;

    ALOGI("container taskId=%d present stats: ok=%u throttled=%u hidden=%u "
          "(%u%% of composed frames dropped)",
          mTaskId, mPresentOkCount, mPresentThrottledCount, mPresentHiddenCount,
          (100 * refused) / (mPresentOkCount + refused));
    mPresentOkCount = 0;
    mPresentThrottledCount = 0;
    mPresentHiddenCount = 0;
    mPresentStatsNextLogNs = now + s2ns(1);
}

void WaylandLayerContainer::armReassert() {
    mTabletReassertBudget = kTabletReassertBudget;
    mReassertUntilNs = systemTime(SYSTEM_TIME_MONOTONIC) + kReassertWindowNs;
}

bool WaylandLayerContainer::reassertAllowed() const {
    return mTabletReassertBudget > 0 &&
           systemTime(SYSTEM_TIME_MONOTONIC) < mReassertUntilNs;
}

bool WaylandLayerContainer::inTabletMenuGestureStrip(double surfaceY) const {
    // The menu only exists in tablet mode, and only for a window ash is
    // willing to show it for — a real app window, not a dialog or a standalone
    // surface.
    if (mAppliedLayoutMode != ChromeLayoutMode::kTablet) return false;
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP) return false;

    const double scale = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0;
    const double dipY = surfaceY / scale;
    return dipY >= 0.0 && dipY < kMenuHitRegionHeightDip;
}

int32_t WaylandLayerContainer::captionHeightPx() const {
    if (mWindowCategory != WaylandWindowCategory::APP || mIsDialog || !mWindow) {
        return 0;
    }
    // Only the NORMAL frame costs the client anything. NONE has no caption at
    // all, and AUTOHIDE is immersive — ash's
    // FrameViewAsh::NonClientTopBorderHeight() returns 0 for an immersive,
    // fullscreen or frame-disabled window, so those already hand the client
    // the whole rectangle.
    const auto* appWin = static_cast<const WaylandAppWindow*>(mWindow.get());
    if (appWin->frameType() != ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NORMAL) return 0;
    const float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
    return static_cast<int32_t>(kCaptionHeightDip * dsf);
}

OutputRect WaylandLayerContainer::allowedWindowRect() const {
    const OutputRect fullOutput = {0, 0, mOutputWidth, mOutputHeight};

    // Real ash fullscreen owns the output. The two windows we put there
    // ourselves are kiosk and unresizable (applyKioskFullscreen /
    // applyUnresizableFullscreen); mCurrentState covers an app that asked
    // Chrome for fullscreen on its own.
    //
    // mCurrentState is trusted here, in both layout modes. Chrome sends no
    // state_type_changed in reply to a state we requested ourselves, so this
    // used to skip the test in tablet mode: a window that had been
    // ash-fullscreen in clamshell still read as FULLSCREEN after
    // applyLayoutMode maximized it for tablet, and would have gone on asking
    // for the whole output while ash clamped it into the work area.
    //
    // Skipping the test was the wrong half of the problem to fix, because a
    // window that is GENUINELY fullscreen in tablet mode then got the work
    // area too — measured on device: state_type_changed=4 arrived, ash grew the
    // work area to the full output, and the window stayed 2560x1520 with no
    // geometry echo at all, because that is what we had just asked for.
    // applyLayoutMode now records the maximize it performs (see the tablet
    // branch), so mCurrentState no longer goes stale and can be believed.
    if (mKioskPinned || mUnresizable ||
        mCurrentState == WaylandWindow::WindowState::FULLSCREEN) {
        return fullOutput;
    }
    OutputRect r = (mWorkArea.w > 0 && mWorkArea.h > 0) ? mWorkArea : fullOutput;

    // set_bounds_in_output carries the CLIENT rectangle, and exo grows it by
    // the caption to get the widget — for a maximized window explicitly:
    //
    //   // When the widget is maximized in clamshell mode, client sends
    //   // |geometry_| without taking caption height into account.
    //   visible_bounds.Offset(0, frame_view->NonClientTopBorderHeight());
    //   return frame_view->GetWindowBoundsForClientBounds(visible_bounds);
    //     — ClientControlledShellSurface::GetWidgetBounds()
    //
    // The offset-then-expand pair keeps the widget's top at the y we asked for
    // and makes it (our height + caption) tall. So asking for the whole work
    // area produced a widget a caption taller than the work area: on device the
    // 2560x1504 clamshell maximize became a 1568-tall widget, the app surface
    // sat at y=64..1568 with the shelf at 1504, and the bottom 64px of every
    // maximized app — its last row of content — was pushed off the screen.
    // Hand over the work area MINUS the caption and the widget lands exactly
    // on the work area, with the client where the shelf ends.
    const int32_t caption = captionHeightPx();
    if (caption > 0 && r.h > caption) r.h -= caption;
    return r;
}

void WaylandLayerContainer::updateOutputInfo(int32_t width, int32_t height,
                                             int32_t transform, float scaleFactor,
                                             const OutputRect& workArea) {
    const float prevScale = mScaleFactor;
    const int32_t prevOutputW = mOutputWidth;
    const int32_t prevOutputH = mOutputHeight;
    const OutputRect prevWorkArea = mWorkArea;
    mOutputWidth = width;
    mOutputHeight = height;
    mOutputTransform = transform;
    mScaleFactor = scaleFactor;
    mWorkArea = workArea;
    // A modal's dim spans the output, so it follows rotation and resolution.
    if (mWindowCategory == WaylandWindowCategory::MODAL && mWindow) {
        static_cast<WaylandModalWindow*>(mWindow.get())->setOutputSize(width, height);
    }

    // A work-area change with the same output size is the shelf appearing or
    // going away over a maximized window. Deliberately nothing is pushed to
    // Chrome for it: ash re-lays-out its own maximized windows to the new work
    // area and sends the result as bounds_changed, so a push here would only
    // race that. Caching the new rectangle is the whole job — it is what
    // onBoundsChanged's refusal compares against, and refusing ash's correct
    // adjustment because we still held the old rectangle is exactly the
    // ping-pong this path exists to avoid.
    if (prevWorkArea != workArea &&
        (mAppliedLayoutMode == ChromeLayoutMode::kTablet || mFillsScreen)) {
        ALOGI("container taskId=%d: work area %dx%d@(%d,%d) -> %dx%d@(%d,%d)",
              mTaskId, prevWorkArea.w, prevWorkArea.h, prevWorkArea.x,
              prevWorkArea.y, workArea.w, workArea.h, workArea.x, workArea.y);
    }

    // The output changed shape — a rotation, a resolution change, a move to a
    // different display. Whatever the window's bounds were, they were computed
    // for the old output, and ash is about to hand us its own adjustment of
    // them (see the bounds refusal in onBoundsChanged). Arm the budget that
    // path spends, so every rotation gets a fresh set of corrections rather
    // than only the first one after a layout-mode switch — and a deadline, so
    // the arming cannot outlive the adjustment it is meant to catch (which
    // arrives within a few ms) and go on to fight the user later.
    if (prevOutputW > 0 && prevOutputH > 0 &&
        (width != prevOutputW || height != prevOutputH)) {
        armReassert();

        // Ash's PIP size limits were derived from the OLD output and it is
        // still enforcing them. The Shell re-derives the PIP rectangle for the
        // new orientation moments from now (PhonePipMenuController's
        // configuration-change path, ~200ms behind this), so the new limits
        // have to be in ash's hands before that rectangle arrives or ash
        // clamps it — see WaylandPipController::refreshSizeConstraints. This
        // ordering is not incidental: the rotation reaches the compositor
        // first (measured 08:58:22.949 here vs 08:58:23.174 in ArcSystemUI).
        if (mPip.isPip() && mWindow &&
            mWindowCategory == WaylandWindowCategory::APP) {
            auto* appWin = static_cast<WaylandAppWindow*>(mWindow.get());
            if (appWin && mPip.refreshSizeConstraints(appWin, width, height)) {
                wl_surface_commit(appWin->surface());
            }
        }
    }
    // First valid DSF we ever see is the primary display's — capture it
    // as the immutable Android-side reference DSF. Android renders for
    // the primary display; we never want this to change.
    if (mAndroidDsf <= 0.0f && scaleFactor > 0.0f) {
        mAndroidDsf = scaleFactor;
    }

    // A system scale (display zoom) change re-enters here with a new DSF for
    // the *same* output. Chrome was told our buffer scale once, at window
    // creation, so without re-sending it the remote surface keeps mapping the
    // buffer at the old DSF: at 2.0->1.8 Chrome reads a 572x1186 buffer as
    // 286x593 dip and paints it into a 318x659 dip window, leaving the content
    // short of the frame by exactly the DSF ratio. Re-send set_scale_factor so
    // Chrome re-maps immediately, instead of staying wrong until some later
    // event (a drag) happens to shake the geometry loose.
    //
    // Deliberately narrow: mAndroidDsf stays latched (it is the reference DSF
    // the Android task source size and inputScale() are built on, and is meant
    // never to move), so only the output-side mapping moves with the zoom.
    // Mirrors the display-crossover path in onOutputChanged.
    if (prevScale > 0.0f && scaleFactor > 0.0f && scaleFactor != prevScale &&
        mWindow && !mGameEfficiency && !mIsDialog &&
        mWindowCategory == WaylandWindowCategory::APP) {
        auto* appWin = static_cast<WaylandAppWindow*>(mWindow.get());
        if (appWin && appWin->remoteSurface()) {
            uint32_t asUint;
            memcpy(&asUint, &scaleFactor, sizeof(float));
            zcr_remote_surface_v2_set_scale_factor(appWin->remoteSurface(), asUint);
            ALOGI("container taskId=%d: set_scale_factor=%.2f (was %.2f, system scale change)",
                  mTaskId, scaleFactor, prevScale);
        }

        // Telling Chrome the new scale is not enough on its own: the SF render
        // target is sized in onGeometryChanged as geometry(dip) × output DSF,
        // and Chrome sends no geometry_changed for a zoom (it keeps the window
        // at the same dip bounds). So the target keeps its old pixel size and
        // the window shows a stale-size buffer until something unrelated — a
        // focus or a drag — finally produces a geometry event. The crossover
        // path's mPendingCrossoverRefresh can't help here for the same reason:
        // it is armed for the *next* geometry update, which never comes.
        //
        // Recompute the target from the last known dip geometry against the new
        // DSF and force one recompose, so the resize lands on the scale change
        // itself. mServerBoundsW/H are dip (set from window_geometry_changed).
        // The task source size is deliberately untouched — it follows the
        // latched mAndroidDsf, and SF scales source→target on the way out.
        if (mTaskId > 0 && mServerBoundsW > 0 && mServerBoundsH > 0) {
            if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
                int32_t targetW = static_cast<int32_t>(mServerBoundsW * scaleFactor);
                int32_t targetH = static_cast<int32_t>(mServerBoundsH * scaleFactor);
                ALOGI("container taskId=%d: scale change -> render target %dx%d "
                      "(geom=%dx%d dip × %.2f), forcing recompose",
                      mTaskId, targetW, targetH, mServerBoundsW, mServerBoundsH,
                      scaleFactor);
                hwc->setTaskRenderTargetSize(mTaskId, targetW, targetH);
                hwc->scheduleRefresh();
            }
        }
    }
}

void WaylandLayerContainer::onBoundsChanged(WaylandWindow* /*window*/,
                                            int32_t x, int32_t y,
                                            int32_t w, int32_t h,
                                            uint32_t reason) {
    // Ash sends the NORMAL of a drag out of a snap straight after
    // drag_finished; anything later is not that drag's.
    mSnapDragEnded = false;

    // Normalise to the CLIENT rectangle. exo reports client bounds for every
    // state except a maximized one (and a tablet snap), where it reports the
    // whole WINDOW instead:
    //
    //   if (is_maximized || (is_snapped && InTabletMode())) return window_bounds;
    //   ... else GetFrameView()->GetClientBoundsForWindowBounds(window_bounds);
    //     — ClientControlledShellSurface::GetClientBoundsForWindowBoundsAndWindowState()
    //
    // Everything downstream works in client space: the echo below is read back
    // as a client rect (see allowedWindowRect()), and the Android task follows
    // the geometry that echo produces. Passing the window rect through
    // un-normalised was what put a maximized app a caption's worth off the
    // bottom of the screen — it undid the subtraction allowedWindowRect() had
    // just made. The tablet snap needs no adjustment: that frame is NONE, so
    // the caption is 0 anyway.
    // Mirror exo's condition exactly. mSnapped is consulted rather than the
    // snapped state types because a CLAMSHELL snap arrives as bounds_changed
    // alone, with no state change at all (see the reason 3/4 handling below),
    // so mCurrentState still reads MAXIMIZED from before the snap. Keying on
    // that alone stripped a caption exo had never added — a clamshell-snapped
    // window came back 1274x1376 instead of 1274x1440 and sat a caption's
    // worth short of the display. A tablet snap really does report the window
    // rect, but its frame is NONE, so captionHeightPx() is 0 and the second
    // term costs nothing.
    //
    // A snap reason is a snap even before mSnapped is set. Restoring a
    // maximized window that ash remembers as snapped arrives as a reason 3/4
    // with no state change, so mCurrentState still reads MAXIMIZED; exo already
    // has the window snapped and sent the client rect. Stripping a caption from
    // it left the window a caption short (640x688 dip instead of 640x720) and
    // resized the task twice, the second time after the resize mask was gone.
    const bool snapReason = reason == 3 || reason == 4;
    const bool exoSentWindowRect =
            (mCurrentState == WaylandWindow::WindowState::MAXIMIZED &&
             !mSnapped && !snapReason) ||
            ((mSnapped || snapReason) &&
             mAppliedLayoutMode == ChromeLayoutMode::kTablet);
    const int32_t captionPx = exoSentWindowRect ? captionHeightPx() : 0;
    if (captionPx > 0 && h > captionPx) {
        ALOGI("container taskId=%d: bounds %dx%d are the maximized WINDOW; "
              "client is %dx%d (caption %d)",
              mTaskId, w, h, w, h - captionPx, captionPx);
        h -= captionPx;
    }

    ALOGI("container taskId=%d: bounds changed x=%d y=%d %dx%d reason=%u",
          mTaskId, x, y, w, h, reason);

    // A clamshell snap from a free window: remember the free rectangle, so
    // leaving the snap can give it back (see the NORMAL branch of
    // onStateChanged). Chrome does not: it reports the unsnap as a NORMAL state
    // alone and leaves the client-controlled window at the half it snapped to.
    // Taken before mServerBounds* is overwritten with the snap half below. A
    // snap from maximized keeps the pre-maximize rectangle already saved.
    if (snapReason && !mSnapped && !mSavedBounds.valid &&
        mAppliedLayoutMode != ChromeLayoutMode::kTablet &&
        mCurrentState == WaylandWindow::WindowState::NORMAL &&
        mServerBoundsW > 0 && mServerBoundsH > 0) {
        mSavedBounds = {mServerBoundsX, mServerBoundsY,
                        mServerBoundsW, mServerBoundsH, true};
        ALOGI("container taskId=%d: saved pre-snap bounds (%d,%d %dx%d)",
              mTaskId, mSavedBounds.x, mSavedBounds.y,
              mSavedBounds.w, mSavedBounds.h);
    }

    // bounds_changed_in_output is in output pixels; every reader of
    // mServerBounds* (finishDragResize, the saved-bounds snapshot, the
    // display-zoom recompute in updateOutputInfo) takes DIP.
    {
        const float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
        mServerBoundsX = static_cast<int32_t>(std::lround(x / dsf));
        mServerBoundsY = static_cast<int32_t>(std::lround(y / dsf));
        mServerBoundsW = static_cast<int32_t>(std::lround(w / dsf));
        mServerBoundsH = static_cast<int32_t>(std::lround(h / dsf));
    }

    // A move/snap/float changes where Chrome shows the window. Keep the IME
    // cursor offset (getActiveTaskWindowOffset) in sync — FydroidIME renders
    // the cursor at window-local (0,0)-based coords and adds this offset to
    // reach the real screen position. Chrome sends bounds in DIP; scale to the
    // render-pixel space the cursor coords use.
    {
        float sc = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
        mWindowOffsetX = static_cast<int32_t>(x * sc);
        mWindowOffsetY = static_cast<int32_t>(y * sc);
    }

    // Acknowledge the bounds change by echoing set_bounds_in_output — the echo
    // is what commits it, so this is also the last chance to refuse one.
    //
    // A window whose steady state is the screen (tablet mode, kiosk, an
    // unresizable app) must not accept a WM-driven rectangle that is smaller.
    // On a tablet rotation ash re-lays-out the window from its PRE-rotation
    // rect and clamps it into the new display: rotating an 800x1280 dip
    // portrait window into a 1280x800 dip landscape display yields the 800x800
    // dip intersection, delivered here as reason=move (5). Echoed back, that
    // confirmed the square — the app ran in a 1600x1600 render target on a
    // 2560x1600 screen with the rest of the display black.
    //
    // applyLayoutMode() already re-asserts bounds when the output dimensions
    // change, but ash's own adjustment lands *after* that request and
    // overrides it, so the correction has to happen on the event itself.
    // Only WM-driven move/resize is refused: a snap (3/4), float (8), PIP (7)
    // or user drag (1/2) is a deliberate, legitimately smaller rectangle.
    // Budgeted (see mTabletReassertBudget) so a Chrome that disagrees settles
    // instead of ping-ponging with us.
    //
    // The comparison is against allowedWindowRect(), NOT the full output. A
    // tablet window is ash-maximized, so with a visible shelf ash's rectangle
    // is always the work area — testing for the full output could never match,
    // and every tablet entry and every rotation spent the whole budget on five
    // forced resize round-trips, each one flipping the render target.
    //
    // Only a rectangle SMALLER than the allowed one is refused, and only by
    // more than a shelf height. A LARGER one is never the failure this guards
    // against — ash's rotation clamp is an intersection — and refusing one
    // would be actively wrong: when a game asserts shelf auto-hide the work
    // area grows to the whole output, and ash's correct larger rectangle can
    // arrive before the work_area_insets event that explains it. The same
    // lag in the other direction (the clamshell shelf and the tablet in-app
    // shelf are different heights, so a mode change moves the work area) is
    // what the slack covers. The rotation clamp this path exists for is off
    // by a third of the screen, nowhere near either.
    const bool wantsAllowedRect =
            (mAppliedLayoutMode == ChromeLayoutMode::kTablet || mKioskPinned ||
             mUnresizable) &&
            // Never against a PIP window: it is legitimately a small rectangle
            // ash chose, so the "smaller than the allowed rect" test below
            // matches every time and would re-assert the window to full size,
            // fighting the positioner on every PIP move.
            !mSnapped && !mFloated && !mIsDialog && !mPip.isPip() &&
            mWindowCategory == WaylandWindowCategory::APP;
    const bool wmDrivenBounds = (reason == 5 || reason == 6);
    const OutputRect allowed = allowedWindowRect();
    // A kiosk or unresizable window owns the whole output (applyKioskFullscreen /
    // applyUnresizableFullscreen), so anything short of it — or not at its
    // origin — is ash still holding the window's pre-fullscreen placement, not
    // a work-area race; the slack must not let it through. With the slack, the
    // caption-inset rectangle ash sends right after the fullscreen request
    // (x=0 y=64 3200x1936 on a DSF-2 output: 64 px short, within 96 px) was
    // echoed back and pulled the window down under a 32-DIP strip (Crossy Road
    // on device).
    const bool ownsOutput = mUnresizable || mKioskPinned;
    const int32_t slack = ownsOutput ? 0 : static_cast<int32_t>(
            kAllowedRectSlackDip * ((mScaleFactor > 0.0f) ? mScaleFactor : 1.0f));
    const bool tooSmall = (w < allowed.w - slack) || (h < allowed.h - slack) ||
            (ownsOutput && (x != allowed.x || y != allowed.y));
    if (mWindow && wantsAllowedRect && wmDrivenBounds && tooSmall &&
        allowed.w > 0 && allowed.h > 0 && reassertAllowed()) {
        mTabletReassertBudget--;
        ALOGI("container taskId=%d: refusing WM bounds %dx%d@(%d,%d) — "
              "re-asserting %dx%d@(%d,%d) (budget %d left)",
              mTaskId, w, h, x, y, allowed.w, allowed.h, allowed.x, allowed.y,
              mTabletReassertBudget);
        mWindow->setBoundsInOutput(mOutput, allowed.x, allowed.y, allowed.w,
                                   allowed.h);
        auto* appWin = static_cast<WaylandAppWindow*>(mWindow.get());
        if (appWin && appWin->remoteSurface()) {
            // Re-assert the state the window is supposed to be in, not just
            // any screen-filling one: an unresizable app lives in ash
            // fullscreen (applyUnresizableFullscreen) and maximizing it here
            // would hand it back the maximized caption and a restore button it
            // has nowhere to use. A kiosk window's state is Chrome's to own —
            // see applyKioskFullscreen — so nothing is pushed for it.
            if (mUnresizable) appWin->fullscreen();
            else if (!mKioskPinned) appWin->maximize();
        }
    } else if (mWindow && w > 0 && h > 0) {
        mWindow->setBoundsInOutput(mOutput, x, y, w, h);
        // The echo is only applied at the next commit, which during a drag
        // used to be the next composed frame. With the mask up, commit it now
        // together with the stretched mask: the window and its content change
        // size in the same frame and SF is not involved. Bounds are client
        // pixels, the viewport destination's unit here (surface = buffer px).
        std::lock_guard<std::mutex> lock(mMaskMutex);
        if (mMask == ResizeMask::kShown || mMask == ResizeMask::kSettling) {
            setResizeMaskDestinationLocked(w, h);
        }
    }

    // Note: bounds_changed_in_output reports the OUTER window rectangle
    // (Chrome's frame chrome wraps Android content). The SF render target
    // must match Android's CONTENT area — driven from onGeometryChanged
    // where physW/physH = w × DSF align 1:1 with the resizeTask call.

    // Tell Chrome to activate split view. Chrome reports the snap it already
    // performed (reason 3/4); echoing set_snap_left/right back on the
    // zaura_surface is what actually activates SplitViewController, so without
    // it the window merely occupies half the screen and is not a split
    // participant — no snap group, no shared divider, and the opposite half
    // never offers overview to pick a partner.
    //
    // This is NOT tablet-only. Clamshell snapping is a first-class ChromeOS
    // gesture (drag to edge, the maximize-button menu, Alt+[ / Alt+]) and
    // Chrome sends the same reason 3/4 there, so gating the acknowledgement on
    // tablet mode left clamshell split view permanently dead.
    // Remember where the window sits while it is not in PIP; that is the
    // position PIP has to restore it to. Recorded here rather than sampled at
    // the PIP transition, which raced ash — see WaylandPipController.
    mPip.setRestoreOrigin(x, y);

    // reason: 3=SNAP_LEFT, 4=SNAP_RIGHT, 5=MOVE, 6=RESIZE, 7=PIP, 8=FLOAT
    if (mTaskId > 0) {
        WaylandAppWindow* appWin = nullptr;
        if (mWindowCategory == WaylandWindowCategory::APP && !mIsDialog && mWindow) {
            appWin = static_cast<WaylandAppWindow*>(mWindow.get());
        }

        switch (reason) {
            case 3:  // SNAP_TO_LEFT
            case 4:  // SNAP_TO_RIGHT
                // Snapping is a way out of float, and it comes as a bounds
                // reason rather than a state change, so clear the flag here or
                // the window stays "floated" for the rest of its life.
                if (mFloated) {
                    mFloated = false;
                    applyFrameButtons();
                }
                if (mUnresizable) {
                    // Refuse the snap instead of acknowledging it. Advertising
                    // min == max normally stops Chrome offering one at all, but
                    // a keyboard shortcut (Alt+[ / Alt+]) or a stale limit can
                    // still get one through, and a half-screen bound is exactly
                    // what this app cannot take. Don't echo set_snap_*, so
                    // SplitViewController never activates, and put the window
                    // straight back to fullscreen.
                    ALOGI("container taskId=%d: refusing snap (unresizable)",
                          mTaskId);
                    mSnapped = false;
                    if (appWin) appWin->unsetSnap();
                    applyUnresizableFullscreen(mOutputWidth, mOutputHeight);
                    break;
                }
                mSnapped = true;
                // Chrome has now stated the snap itself — a carried one stops
                // being provisional here (see restoreSnapCarry).
                mSnapProvisionalUntilNs = 0;
                // Remember where the snap put us, so the settle event that
                // follows can be told apart from a real drag out (see case 5).
                mSnappedBounds = {x, y, w, h};
                if (appWin) {
                    if (reason == 3) appWin->setSnapLeft();
                    else appWin->setSnapRight();
                }
                // The mode itself is usually already right — MULTI_WINDOW is
                // the tablet steady state now — but the snap still needs the
                // focus half, so ask for it explicitly rather than as a side
                // effect of a mode change that no longer happens. Clamshell
                // tasks are freeform and track the snap bounds through the
                // normal onGeometryChanged path.
                if (mAppliedLayoutMode == ChromeLayoutMode::kTablet) {
                    switchWindowingModeAsync(tabletWindowingMode(),
                                             /*focusAfter=*/true);
                }
                break;

            case 8: {  // FLOAT
                // Float has no state_type in this protocol version (the enum
                // stops at pip), so this reason code is the entire signal that
                // Chrome floated the window — and it is not a snap-only event.
                // Floating straight from maximized is the common case, and it
                // used to fall through this branch untouched because the whole
                // thing was nested inside "was it snapped?".
                ALOGI("container taskId=%d: floated (%d,%d %dx%d)",
                      mTaskId, x, y, w, h);
                mFloated = true;
                if (mSnapped) {
                    mSnapped = false;
                    if (appWin) appWin->unsetSnap();
                }
                // A floated window is a small window that has to follow
                // Chrome's rectangle, which is what MULTI_WINDOW is for —
                // already the tablet steady state, so this is now mostly the
                // focus half. See the snap case above.
                if (mAppliedLayoutMode == ChromeLayoutMode::kTablet) {
                    switchWindowingModeAsync(tabletWindowingMode(),
                                             /*focusAfter=*/true);
                }
                // Acknowledge the float. This is the step that actually makes
                // the window floated — see WaylandAppWindow::setFloat. Chrome
                // had already put it in a float-sized rectangle, but without
                // the echo it was only a small window: other windows drew over
                // it, its size button offered no window-layout menu, and it
                // could never be un-floated because ash held no float state.
                if (appWin) appWin->setFloat();
                // Deliberately no frame-button override here: a genuinely
                // floated window gets its caption from ash, which is what puts
                // the window-layout (snap) panel on the size button and turns
                // that button into "exit float". Overriding the mask replaced
                // exactly the button those live on.
                break;
            }

            case 5:  // MOVE — possibly dragged out of the snap
                // A clamshell snap is released without any state change, so
                // onStateChanged never sees it; the reason code is the only
                // signal. Chrome has already dropped the window from the split
                // on its side, so this just keeps mSnapped honest (the tablet
                // reassert in onGeometryChanged is gated on it) and releases
                // SplitViewController for good measure.
                //
                // A MOVE at the snapped bounds is NOT a drag out. Chrome emits
                // one right after the snap itself — same rectangle, ~20ms later
                // — as the bounds settle. Treating that as leaving the split
                // cancelled every snap the instant it was made: set_snap_right
                // followed by unset_snap, and the window fell back out of split
                // view. (Float is handled above; it always means we left.)
                //
                // A carried snap is exempt until Chrome re-states it. The
                // window was just mapped, and the first rectangle Chrome sends
                // a freshly mapped window is the caption-inset one — on device
                // (644,64 1274x1456) against a carried (1286,0 1274x1520). That
                // is not a drag out, it is Chrome converging: the real snap
                // arrives as a reason 4 three milliseconds later. Unsnapping on
                // it made the right half of every split visibly flash out of
                // and back into split view on each rotation.
                if (mSnapped && !snapIsProvisional() &&
                    (x != mSnappedBounds.left ||
                     y != mSnappedBounds.top || w != mSnappedBounds.right ||
                     h != mSnappedBounds.bottom)) {
                    mSnapped = false;
                    if (appWin) appWin->unsetSnap();
                    if (mAppliedLayoutMode == ChromeLayoutMode::kTablet) {
                        switchWindowingModeAsync(tabletWindowingMode());
                    }
                }
                break;

            case 7:  // PIP — ash is positioning the PIP window
                // ash owns this rectangle completely: PipPositioner places it,
                // PipWindowResizer drags it, collision detection keeps it off
                // the shelf, and it moves again on every work-area change. All
                // of that arrives here, and all of it is legitimate — there is
                // nothing to second-guess, unlike the WM-driven move/resize
                // above. The task follows via onGeometryChanged's PIP branch.
                //
                // Recorded so the drag-out test in case 5 cannot mistake a PIP
                // move for leaving a split, and so a stale snap rectangle from
                // before PIP is not kept alive across the transition.
                if (mSnapped) {
                    mSnapped = false;
                    if (appWin) appWin->unsetSnap();
                }
                mFloated = false;
                ALOGI("container taskId=%d: pip bounds (%d,%d %dx%d)",
                      mTaskId, x, y, w, h);
                // The first of these still holds the pre-PIP rectangle.
                mPip.notePipBounds(x, y);
                break;

            case 6:  // RESIZE — a WM-driven resize, not a re-snap
                // ash re-lays-out both halves of a split when the display
                // rotates (and when the divider is dragged) and delivers the
                // new half as a RESIZE. It never re-issues snap_to_left/right:
                // the window did not change which half it is on, only how big
                // that half is. So this is the only event that carries the new
                // snapped rectangle, and mSnappedBounds has to be re-recorded
                // from it — Chrome still has us in the split.
                //
                // Without this the rectangle stayed at the pre-rotation half
                // and the settle MOVE that follows ~1s later failed the "same
                // rectangle?" test in case 5, which read it as a drag out of
                // the split and sent unset_snap. On device at 11:13:26 portrait's
                // (0,0 1600x1234) was compared against landscape's
                // (0,0 1274x1520) and the left window left the split.
                //
                // The damage is not confined to that window. The unset leaves
                // mSnapped false while Chrome still has the window snapped, and
                // mSnapped is what keeps the allowedRect refusal above off a
                // legitimately half-screen rectangle. On the NEXT rotation the
                // refusal fired on the incoming snap half (11:12:48.513
                // "refusing WM bounds 1274x1520 — re-asserting 2560x1520"),
                // maximized that half, and Chrome broke the snap group and
                // popped the partner to MAXIMIZED too — both windows stacked
                // full-screen from one rotation.
                if (mSnapped) mSnappedBounds = {x, y, w, h};
                break;
        }
    }
}

int32_t WaylandLayerContainer::tabletWindowingMode() {
    // FREEFORM, the same mode clamshell uses — deliberately, so a layout-mode
    // change is not a windowing-mode change at all.
    //
    // Any mode will do for the thing this exists for (FULLSCREEN rejects
    // resizeTask, so the task could not follow a work-area-sized window; both
    // FREEFORM and MULTI_WINDOW accept it). But MULTI_WINDOW made every
    // tablet<->clamshell switch cross the FREEFORM boundary, and that is the
    // one thing Android animates here:
    //
    //     // Only do an animation into and out-of freeform mode for now.
    //     return (prevWinMode == WINDOWING_MODE_FREEFORM)
    //             != (newWinMode == WINDOWING_MODE_FREEFORM);
    //       — Task.shouldStartChangeTransition()
    //
    // A true there runs initializeChangeTransition(mTmpPrevBounds), which
    // hands mSurfaceFreezer the PREVIOUS bounds and crops the Task surface to
    // them, inflated by RESIZE_HANDLE_WIDTH_IN_DP (30dp = 54px at this
    // density). That crop outlives the transition: on device the Task layer sat
    // at crop=[-54,-54,2614,1494] — 1440+54 — while the app's own layer and
    // buffer were the full 2560x1520, so composition clipped the bottom 26px
    // and they came out black above the shelf. Every measurement fits
    // prev_height+54: 1440->1494, 1504->1558, and no clip at all when the
    // stale crop happened to be the larger one, which is why it only ever bit
    // while growing.
    //
    // Staying in FREEFORM makes shouldStartChangeTransition false, so there is
    // no TRANSIT_CHANGE, no surface freeze and no stale crop to leak. Chrome
    // animates the window itself; the Android-side animation bought us
    // nothing.
    return WaylandTaskResizer::WINDOWING_MODE_FREEFORM;
}

void WaylandLayerContainer::switchWindowingModeAsync(int32_t windowingMode,
                                                     bool focusAfter) {
    if (windowingMode == mCurrentWindowingMode) {
        // Already there — but the caller may still need the focus half. A
        // tablet task is in MULTI_WINDOW before it is snapped (that is the
        // steady state now), so the snap would otherwise silently lose the
        // setFocusedTask it has always depended on.
        if (focusAfter && mTaskId > 0) {
            int32_t taskId = mTaskId;
            std::thread([this, taskId]() {
                mTaskResizer.setFocusedTask(taskId);
            }).detach();
        }
        return;
    }
    int32_t prevMode = mCurrentWindowingMode;
    mCurrentWindowingMode = windowingMode;

    int32_t taskId = mTaskId;
    ALOGI("container taskId=%d: switching windowing mode %d → %d", taskId, prevMode, windowingMode);

    // Switching to FULLSCREEN clears the task's bounds override on the Android
    // side (ArcWindowManagerService.setTaskWindowingMode → Task.setBounds(null)),
    // so the task immediately inherits the live display config and re-layouts
    // on rotation with no client-side resizeTask. Only kiosk and unresizable
    // apps go there now — see tabletWindowingMode(). Every other mode takes
    // its size from onGeometryChanged instead.

    // Run on a detached thread — binder IPC can block the Wayland event thread.
    std::thread([this, taskId, windowingMode, focusAfter]() {
        bool ok = mTaskResizer.setTaskWindowingMode(taskId, windowingMode,
                                                     /*toTop=*/true);
        ALOGI("container taskId=%d: setTaskWindowingMode(%d) result=%d",
              taskId, windowingMode, ok);
        if (!ok) return;

        // After entering split view, explicitly focus the task so its stack
        // is above the home stack and the activity resumes; without it the
        // home stack stays on top and occludes the snapped task. Only for the
        // callers that asked: this used to fire for any switch INTO
        // MULTI_WINDOW, which was the same thing while MULTI_WINDOW meant "a
        // snap just happened". It is the tablet steady state now, so entering
        // tablet mode would otherwise have every open task race to claim
        // Android focus and an arbitrary background one win.
        if (focusAfter) mTaskResizer.setFocusedTask(taskId);

        // The mode switch is what makes resizeTask legal, and it lands here,
        // asynchronously — but mCurrentWindowingMode was updated synchronously
        // above. A geometry echo arriving in between therefore believes the
        // resize is legal, sends it, and has it refused by ATMS, which still
        // sees a FULLSCREEN task. onGeometryChanged records what it asked for
        // either way (deliberately: a permanently-refused resize must not
        // re-issue a binder call on every frame), so that refusal would never
        // be retried and the task would keep the display-sized bounds
        // FULLSCREEN gave it — the exact 40-dip-clipped window this whole
        // change is about.
        //
        // Dropping the dedupe keys and asking for a frame is NOT enough on its
        // own, which cost a board: WaylandAppWindow keeps its own geometry
        // dedupe, so an unchanged echo is filtered there and never reaches the
        // container's cleared key. On an idle app the geometry never changes
        // again, so nothing re-runs and the task keeps its launch size — on
        // device it sat at 742x1318 inside a 2560x1520 window with the refusal
        // (`resizeTask ... result=0`) 38ms ahead of the mode landing.
        //
        // So re-apply the size here, now that the mode is genuinely in place.
        // mContainerFrame is authoritative: onGeometryChanged assigns it from
        // the echo BEFORE it attempts the resize, so it holds the right
        // rectangle even when that attempt was refused. The dedupe keys are
        // still dropped and a frame still requested, so a later echo can
        // correct this if Chrome moves the window again.
        if (windowingMode != WaylandTaskResizer::WINDOWING_MODE_FULLSCREEN) {
            const int32_t frameW = mContainerFrame.right;
            const int32_t frameH = mContainerFrame.bottom;
            if (frameW > 0 && frameH > 0 &&
                (mLastResizedBounds.right != frameW ||
                 mLastResizedBounds.bottom != frameH ||
                 mLastResizedBounds.left != 0 || mLastResizedBounds.top != 0)) {
                ALOGI("container taskId=%d: mode landed, re-applying %dx%d",
                      taskId, frameW, frameH);
                if (mTaskResizer.resizeTask(taskId, 0, 0, frameW, frameH)) {
                    mLastResizedBounds = {0, 0, frameW, frameH};
                }
            }
            mLastGeometryW = -1;
            if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
                hwc->scheduleRefresh();
            }
        }
    }).detach();
}

void WaylandLayerContainer::onGeometryChanged(WaylandWindow* /*window*/,
                                              int32_t x, int32_t y,
                                              int32_t w, int32_t h) {
    // Dialog geometry is controlled by SF, not Chrome — skip resize feedback
    if (mIsDialog) return;

    // Refresh the cached server bounds on EVERY geometry echo, before the
    // dedupe below can return. mServerBounds* is written by this event in DIP
    // and by onBoundsChanged in output pixels ("Bounds are set in pixels, and
    // should not be scaled", exo), and its consumers — the saved-bounds
    // snapshot in onStateChanged, the display-zoom render-target recompute in
    // updateOutputInfo — all read it as DIP. Chrome sends a geometry on every
    // widget commit, so writing it here is what keeps the pixel value a
    // bounds event leaves behind from outliving the frame it arrived on.
    mServerBoundsX = x;
    mServerBoundsY = y;
    mServerBoundsW = w;
    mServerBoundsH = h;

    // Most of those per-commit echoes are repeats. Dedupe the work below
    // against the last GEOMETRY rather than against mServerBounds*, which the
    // pixel-space bounds event also writes: when the two spaces coincide (a
    // DSF-1.0 output) a real geometry change looked identical to the bounds
    // event that preceded it and was dropped — no resizeTask, no render-target
    // resize. That was survivable while a tablet task took its size from the
    // display config; it is not now that the task follows this event.
    if (x == mLastGeometryX && y == mLastGeometryY &&
        w == mLastGeometryW && h == mLastGeometryH) {
        return;  // No change
    }
    // A size change against a previous real geometry (not the first one, and
    // not the one after the tablet reassert cleared it to -1).
    const bool sizeChanged = mLastGeometryW > 0 && mLastGeometryH > 0 &&
            (w != mLastGeometryW || h != mLastGeometryH);
    mLastGeometryX = x;
    mLastGeometryY = y;
    mLastGeometryW = w;
    mLastGeometryH = h;

    ALOGI("container taskId=%d: geometry changed x=%d y=%d %dx%d",
          mTaskId, x, y, w, h);

    // TABLET: a caption-inset geometry (y > 0) means the window fell out of
    // maximize — typically the maximize/frame-NONE requests raced window
    // creation and exo dropped them before the widget was mapped. Reassert
    // the tablet state; without this the windowed layout (black caption
    // band) sticks until the user switches apps. Budgeted to rule out any
    // reassert↔configure loop.
    // Not while floated: a floated window legitimately sits inset from the
    // output, and re-maximizing it here would cancel the float on every
    // geometry echo.
    // Time-boxed as well as budgeted (reassertAllowed): opening the tablet
    // multitask menu insets the window by the menu height and arrives here as
    // exactly the same y=32 geometry as a dropped maximize. Re-asserting on
    // that snapped the window back and dismissed the menu as it appeared —
    // the panel "flashed and vanished". The race this repairs happens within
    // a few ms of the window being mapped; the menu is minutes later.
    // Not while in PIP either, and for the same reason as float: a PIP window
    // sits inset from the output by design, so re-maximizing it here would
    // cancel PIP on the first geometry ash sends.
    if (mAppliedLayoutMode == ChromeLayoutMode::kTablet && y > 0 &&
        !mSnapped && !mFloated && !mIsDialog && !mPip.isPip() &&
        mWindowCategory == WaylandWindowCategory::APP &&
        reassertAllowed() && mWindow) {
        auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
        if (appWindow && appWindow->remoteSurface()) {
            mTabletReassertBudget--;
            const OutputRect r = allowedWindowRect();
            ALOGI("container taskId=%d: tablet reassert maximize "
                  "(geometry y=%d, budget %d left, rect %dx%d@(%d,%d))",
                  mTaskId, y, mTabletReassertBudget, r.w, r.h, r.x, r.y);
            appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE);
            appWindow->setBoundsInOutput(mOutput, r.x, r.y, r.w, r.h);
            appWindow->maximize();

            // Do NOT size the Android task from a geometry we have just
            // refused. It describes a window that fell out of maximize, and
            // the corrected geometry is already on its way — sizing the task
            // to the caption-inset rectangle here left a resizeTask for the
            // WRONG height in flight, racing the one the corrected geometry
            // issues moments later. resizeTask is a synchronous binder call
            // made from both the SurfaceFlinger main thread and the Wayland
            // event thread, so those two can complete out of order, and on
            // device the stale one regularly won: a tablet window came up
            // 2560x1456 (the caption-inset height) inside a 2560x1520
            // window, with the app's last rows composited into the gap as
            // black. Only reachable now that a tablet task is MULTI_WINDOW
            // and follows resizeTask at all — FULLSCREEN used to ignore it.
            //
            // The render target is left alone for the same reason: it would
            // just be re-set by the corrected geometry one event later.
            mLastGeometryW = -1;  // let the corrected geometry through
            return;
        }
    }

    // KIOSK: the window must sit at the output origin, always. A non-zero
    // origin means ash placed it with a rectangle of its own — at launch that
    // is the window-open animation's start rect, whose origin survives the
    // animation even though its size does not: on device the final geometry was
    // 1366x768 (right) at (653,32) (wrong), which is the whole visible bug.
    //
    // The test is on the ORIGIN alone, deliberately. The size is already
    // defended by the bounds refusal in onBoundsChanged, and comparing a size
    // here would mean comparing this event's DIP rectangle against
    // mOutputWidth/mOutputHeight, which are output pixels — equal only while
    // the DSF is 1. "A kiosk window is at (0,0)" needs no units.
    //
    // Repaired inline rather than by calling applyKioskFullscreen(), which
    // would re-arm the budget this path is spending (and re-issue a windowing
    // mode binder call the window already has). Same shape as the tablet
    // reassert above; the shared budget and its 3s deadline are what stop a
    // Chrome that disagrees from ping-ponging.
    //
    // An unresizable app's fullscreen (applyUnresizableFullscreen) owns the
    // output the same way, with an AUTOHIDE caption instead of none, and ash
    // can leave it off-origin the same way (a caption-inset y=32 geometry).
    if ((mKioskPinned || mUnresizable) && (x != 0 || y != 0) &&
        !mIsDialog && !mPip.isPip() &&
        mWindowCategory == WaylandWindowCategory::APP &&
        reassertAllowed() && mWindow) {
        auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
        if (appWindow && appWindow->remoteSurface()) {
            mTabletReassertBudget--;
            ALOGI("container taskId=%d: %s geometry off-origin (%d,%d %dx%d)"
                  " — re-asserting 0,0 %dx%d (budget %d left)",
                  mTaskId, mKioskPinned ? "kiosk" : "unresizable", x, y, w, h,
                  mOutputWidth, mOutputHeight, mTabletReassertBudget);
            appWindow->setFrameType(mKioskPinned
                    ? ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE
                    : ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_AUTOHIDE);
            appWindow->setBoundsInOutput(mOutput, 0, 0, mOutputWidth,
                                         mOutputHeight);
            if (!mKioskPinned) appWindow->fullscreen();
            // Deliberately no early return, unlike the tablet repair: only the
            // ORIGIN was refused here. The size in this event is the one the
            // task and render target should already be following, and the
            // corrected echo may carry no size change at all for the dedupe
            // above to let through.
        }
    }

    // geometry_changed provides DIP coords. Scale to physical resolution.
    // In game efficiency mode, pass DIP coords directly — the game renders at
    // logical resolution and Chrome upscales to fill the screen.
    if (w > 0 && h > 0) {
        // The Android task is sized in primary-DSF pixels regardless of
        // which physical display the window is currently on. Android's
        // display configuration never changes — it always renders for
        // the primary — so keeping the task at primary-DSF means the
        // app receives the same DIP count of usable space on every
        // display, and content density stays consistent across moves.
        // Game-fullscreen mode skips DSF entirely and uses logical (DIP)
        // pixels, preserving the existing efficiency-mode contract.
        // Efficiency mode: the game rasterizes at the logical (DIP) resolution
        // and Chrome upscales the buffer to fill the screen — 1/4 the pixels at
        // DSF 2.0. set_scale_factor(1.0) makes Chrome report this window's
        // geometry in physical pixels (2560x1600 rather than the 1280x800 DIP
        // of normal mode), so the logical size is that divided by the DSF.
        const float logicalScale =
                (mScaleFactor > 0.0f) ? (1.0f / mScaleFactor) : 1.0f;
        float taskScale = mGameEfficiency
                ? logicalScale
                : ((mAndroidDsf > 0.0f) ? mAndroidDsf : mScaleFactor);
        // The SF render target follows the *current* output's DSF so the
        // composed buffer matches Chrome's window pixel size on the
        // active display. When the window is on a low-DSF output this is
        // smaller than the source bbox, and SF downscales the
        // primary-DSF rasterization on the way out.
        float targetScale = mGameEfficiency
                ? logicalScale
                : ((mScaleFactor > 0.0f) ? mScaleFactor : 1.0f);

        // NB: do NOT derive the IME window offset from geometry x/y — those are
        // the window-geometry inset (≈0), not the on-screen position. The offset
        // is maintained from the actual placement (creation) and Chrome's
        // bounds_changed_in_output (move); see onBoundsChanged.

        int32_t physW = static_cast<int32_t>(w * taskScale);
        int32_t physH = static_cast<int32_t>(h * taskScale);
        int32_t targetW = static_cast<int32_t>(w * targetScale);
        int32_t targetH = static_cast<int32_t>(h * targetScale);

        // Android always sees task at (0, 0, width, height)
        mContainerFrame = {0, 0, physW, physH};

        if (mLastResizedBounds.left != 0 || mLastResizedBounds.top != 0 ||
            mLastResizedBounds.right != physW || mLastResizedBounds.bottom != physH) {
            mLastResizedBounds = {0, 0, physW, physH};
            // While the user is drag-resizing the window, skip the
            // Android-side resizeTask binder call — unresizable
            // activities would otherwise be relaunched on every
            // intermediate drag frame. onDragFinished does one final
            // resize via the AR-step path, which handles both
            // resizable and unresizable cases.
            if (mDragResizing) {
                ALOGVT("container taskId=%d: drag in progress, deferring "
                       "resizeTask to drag_finished", mTaskId);
            } else if (mPip.isPip()) {
                // PIP is not special: the task follows the window, at origin
                // (0,0,W,H), like every other ARC task. Chrome owns the
                // on-screen position — base patch 0042 forces that origin at
                // launch, but WM's pinned-task resolution never goes through
                // TaskLaunchParamsModifier, so a PIP task keeps WM's offset
                // (measured Rect(1632,1066-2532,1572)) unless it is zeroed
                // here. A non-zero origin puts the WMS frame out of step with
                // the task-local coordinates SF emits, which is the input
                // offset 0042 exists to prevent.
                //
                // resizeTask refuses a pinned task (canResizeTask is FREEFORM
                // || MULTI_WINDOW), so this goes through Task#setBounds — the
                // same thing the WM Shell does for pinned tasks upstream.
                //
                // WM's PIP rectangle is only the SEED for ash's window size;
                // once ash has a PIP window it owns the size, and the task
                // tracks it from here so the two always agree.
                mTaskResizer.setTaskBoundsUnchecked(mTaskId, 0, 0, physW, physH);
            } else if (mSnapped) {
                // Snapping hands the window a half-screen it did not choose.
                // A plain resizeTask only resizes the task, so an activity
                // that cannot handle the new size is left letterboxed inside
                // its half. Route the snap through the Java AR-step path,
                // which resizes resizable activities and relaunches
                // unresizable ones at the closest supported aspect ratio so
                // they come back fitting the split.
                applyBoundsWithArStep(physW, physH);
            } else if (mCurrentWindowingMode !=
                    WaylandTaskResizer::WINDOWING_MODE_FULLSCREEN) {
                // FULLSCREEN: skip — bounds come from the display config and
                // ATMS rejects resizeTask for it. That is only kiosk and
                // unresizable apps now, both of which really do own the whole
                // output. Freeform (clamshell) and MULTI_WINDOW (tablet, incl.
                // snap and float): regular resizeTask — ATMS permits it for
                // these modes (AOSP-13 has no SPLIT_SCREEN modes; no organizer
                // WCT needed here). This is the call that keeps the Android
                // task on the same rectangle as the window when ash clamps a
                // maximized tablet window to the work area.
                mTaskResizer.resizeTask(mTaskId, 0, 0, physW, physH);
            }
        }

        // Always update the SF render target — even if Android's task
        // dimensions didn't change, the live output DSF (and therefore
        // the target buffer size) may have just changed via crossover.
        // Efficiency mode needs this too: skipping it left the render target at
        // whatever the window had before it was maximized, so the game
        // rasterized at a stale size and Chrome upscaled *that* instead of a
        // clean logical-resolution buffer.
        mGeometryTargetW = targetW;
        mGeometryTargetH = targetH;
        bool maskShown = false;
        {
            std::lock_guard<std::mutex> lock(mMaskMutex);
            if (mMask == ResizeMask::kShown || mMask == ResizeMask::kSettling) {
                setResizeMaskDestinationLocked(targetW, targetH);
            }
            maskShown = (mMask == ResizeMask::kShown);
        }
        // Outside a drag the task was just resized above, so the frame on
        // screen is the old content in a window of the new size: cover it.
        // Not on a restore out of maximize/fullscreen: ash cross-fades that
        // one itself, and the mask appearing and lifting underneath the fade
        // read as the screen flashing.
        if (sizeChanged && !mDragResizing) {
            if (mSkipMaskForRestore) {
                ALOGI("container taskId=%d: restore from maximize — no resize "
                      "mask", mTaskId);
            } else {
                beginResizeTransition();
            }
        }
        if (sizeChanged) mSkipMaskForRestore = false;
        // While the mask covers the window every composed frame is dropped, so
        // re-targeting SF at each drag step would only buy compositions nobody
        // sees. onDragFinished pushes the final target.
        if (maskShown) {
            ALOGVT("container taskId=%d: resize mask up, render target %dx%d "
                   "deferred", mTaskId, targetW, targetH);
        } else if (mTaskId > 0 &&
            mWindowCategory == WaylandWindowCategory::APP && !mIsDialog) {
            if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
                ALOGV("container taskId=%d: setTaskRenderTargetSize=%dx%d "
                      "(geom=%dx%d × outputDSF=%.2f, source=%dx%d at "
                      "androidDSF=%.2f)",
                      mTaskId, targetW, targetH, w, h, targetScale,
                      physW, physH, taskScale);
                if (mGameEfficiency) {
                    ALOGV("container taskId=%d: game efficiency render target "
                          "%dx%d, task %dx%d (geom %dx%d, dsf %.2f)",
                          mTaskId, targetW, targetH, physW, physH, w, h,
                          mScaleFactor);
                }
                hwc->setTaskRenderTargetSize(mTaskId, targetW, targetH);
                // First geometry update after a display crossover: the render
                // target was just resized to the new display's DSF. Force an
                // immediate recompose so the new-size buffer is presented now,
                // not on the next natural frame — shrinks the crossover flash.
                if (mPendingCrossoverRefresh) {
                    mPendingCrossoverRefresh = false;
                    hwc->scheduleRefresh();
                }
            }
        }
    }
}

bool WaylandLayerContainer::ensureWindowFitsContent(int32_t contentW,
                                                    int32_t contentH) {
    // No window yet: nothing was checked, so tell SF not to latch this size.
    // The window is created lazily on the first present, and a mark latched
    // before that would disarm the grow for the rest of the launch window.
    if (!mWindow) return false;

    // Only regular app task windows. Dialogs are sized by SF; game windows are
    // efficiency-scaled; non-APP categories (IME/toast/notification) aren't
    // resizable task windows. Skip while the user is drag-resizing.
    // Also skip in TABLET mode: the window is maximized and its bounds belong
    // to Chrome's layout — a grow request (e.g. from a transient over-sized
    // content footprint at launch) kicks the window out of maximize into a
    // captioned normal window larger than the screen, which sticks until the
    // next user interaction re-maximizes it.
    // Nor while the window fills the screen (fullscreen / maximized, including
    // the unresizable-app fullscreen request, which sets mFillsScreen at once
    // because Chrome sends no state change for it). Ash owns those bounds; the
    // content only looks bigger than the window because the window is briefly
    // still at the size it had before the request. Growing it then sent ash a
    // normal-window rectangle that pushed the fullscreen window down off the
    // top of the screen (Crossy Road on device: a 64-DIP black band above the
    // game, the bottom cut off).
    if (mWindowCategory != WaylandWindowCategory::APP || mIsDialog ||
        mGameEfficiency || mDragResizing || mFillsScreen ||
        mAppliedLayoutMode == ChromeLayoutMode::kTablet) {
        return true;
    }
    if (contentW <= 0 || contentH <= 0) return true;

    // mContainerFrame is zero-origin (0,0,w,h) in primary-DSF pixels — the same
    // space as the SF content footprint passed in here.
    int32_t winW = mContainerFrame.right;
    int32_t winH = mContainerFrame.bottom;

    // Grow-only, with a few px of slack to avoid jitter from rounding. Content
    // never legitimately exceeds its own window for long; when it does, the
    // window opened at a stale size and must catch up to the activity.
    constexpr int32_t kSlack = 4;
    if (contentW <= winW + kSlack && contentH <= winH + kSlack) return true;

    int32_t newW = std::max(contentW, winW);
    int32_t newH = std::max(contentH, winH);

    // Keep the window's current position; just enlarge it. set_bounds_in_output
    // takes OUTPUT pixels: mServerBounds* holds the window origin in DIP, so
    // scale it. Not mWindowOffsetX/Y — onBoundsChanged stores that scaled once
    // more for the IME cursor, so on a DSF-2 output it is twice the real origin
    // and the window jumped by its own offset (y=64 px became y=128).
    const float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
    int32_t ox = static_cast<int32_t>(std::lround(mServerBoundsX * dsf));
    int32_t oy = static_cast<int32_t>(std::lround(mServerBoundsY * dsf));

    mWindow->setBoundsInOutput(mOutput, ox, oy, newW, newH);

    // Cache the new frame so we don't re-request every frame before Chrome's
    // geometry_changed echo arrives. That echo (onGeometryChanged) then drives
    // resizeTask() and setTaskRenderTargetSize() to the matching size, which
    // realigns the Android task and the SF render-target buffer.
    mContainerFrame = {0, 0, newW, newH};
    return true;
}

bool WaylandLayerContainer::updateModalCardSize(int32_t cardW, int32_t cardH) {
    if (mWindowCategory != WaylandWindowCategory::MODAL || !mWindow) return false;
    if (cardW <= 0 || cardH <= 0) return false;
    if (mContainerFrame.right == cardW && mContainerFrame.bottom == cardH) return true;

    mContainerFrame = {0, 0, cardW, cardH};

    // Keep the card centred: the modal has no caption and no user-draggable
    // position, so its only meaningful placement is the centre of the output
    // (the same rule window creation applies). Re-centring on every size change
    // keeps the shadow frame concentric with the card instead of anchoring the
    // top-left and letting the card grow off its background.
    int32_t ox = mWindowOffsetX;
    int32_t oy = mWindowOffsetY;
    if (mOutputWidth > 0 && mOutputHeight > 0) {
        ox = (mOutputWidth - cardW) / 2;
        oy = (mOutputHeight - cardH) / 2;
        if (ox < 0) ox = 0;
        if (oy < 0) oy = 0;
    }
    mWindowOffsetX = ox;
    mWindowOffsetY = oy;

    mWindow->setBoundsInOutput(mOutput, ox, oy, cardW, cardH);
    ALOGI("container taskId=%d: modal card resized to %dx%d at (%d,%d)",
          mTaskId, cardW, cardH, ox, oy);
    return true;
}

void WaylandLayerContainer::setModalScrim(float alpha, int32_t x, int32_t y,
                                          int32_t w, int32_t h) {
    if (mWindowCategory != WaylandWindowCategory::MODAL || !mWindow) return;
    static_cast<WaylandModalWindow*>(mWindow.get())->setScrim(alpha, x, y, w, h);
}

void WaylandLayerContainer::onStateChanged(WaylandWindow* /*window*/,
                                           WaylandWindow::WindowState state) {
    ALOGI("container taskId=%d: state changed to %u",
          mTaskId, static_cast<uint32_t>(state));

    // Commit the new state before anything below consults it: the shelf
    // re-evaluation calls onFocusChanged, whose owns-the-screen test reads
    // mCurrentState — evaluating it against the state being LEFT asserted
    // auto-hide for a window that had just stopped being fullscreen.
    const WaylandWindow::WindowState prevState = mCurrentState;
    mCurrentState = state;
    mSkipMaskForRestore = false;  // set again below for a restore from max

    // Reset throttle when becoming visible — allow immediate present
    if (state != WaylandWindow::WindowState::MINIMIZED) {
        mFrameCallbackPending.store(false, std::memory_order_release);
    }

    // Filling the screen drives the shelf for ANY app; combined with the game
    // classification it also gates the opt-in efficiency mode below.
    const bool wasFillsScreen = mFillsScreen;
    bool wasGameFullScreen = mGameFullScreen;
    mFillsScreen = stateFillsScreen(state);
    mGameFullScreen = mIsGame && mFillsScreen;

    // Efficiency mode rasterizes the game at the logical (DIP) resolution and
    // lets Chrome upscale the buffer to fill the screen — a quarter of the
    // pixels at DSF 2.0 — and takes over the render-target/scale decisions
    // throughout this class.
    //
    // Off by default: the quarter-resolution win is not worth the behaviour it
    // drags in. It changes the coordinate space Chrome reports for the surface
    // (set_scale_factor(1.0) below), asserts a task size ATMS refuses for a
    // fullscreen task, and applies to every task of a CATEGORY_GAME app —
    // including auxiliary ones like an in-app webview or verification flow that
    // are not games at all. Set the property to 1 to opt back in.
    mGameEfficiency = mGameFullScreen && kGameEfficiency;

    if (mGameFullScreen != wasGameFullScreen && !mIsDialog &&
        mWindowCategory == WaylandWindowCategory::APP) {
        auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
        if (appWindow && appWindow->remoteSurface() && kGameEfficiency) {
            float sf = mGameFullScreen ? 1.0f : mScaleFactor;
            uint32_t asUint;
            memcpy(&asUint, &sf, sizeof(float));
            zcr_remote_surface_v2_set_scale_factor(appWindow->remoteSurface(), asUint);
            ALOGI("container taskId=%d: game efficiency mode %s (scale=%.1f)",
                  mTaskId, mGameFullScreen ? "ON" : "OFF", sf);
        }
        // Size the task and the render target here rather than waiting for
        // onGeometryChanged: Chrome only echoes geometry_changed on the FIRST
        // maximize: later ones arrive as bounds_changed alone, so the render
        // target would keep whatever the restored window left behind and the
        // game would rasterize at the wrong resolution on re-entry.
        if (mGameEfficiency && mTaskId > 0 && mOutputWidth > 0 &&
            mOutputHeight > 0) {
            const float logicalScale =
                    (mScaleFactor > 0.0f) ? (1.0f / mScaleFactor) : 1.0f;
            int32_t lw = static_cast<int32_t>(mOutputWidth * logicalScale);
            int32_t lh = static_cast<int32_t>(mOutputHeight * logicalScale);
            if (lw > 0 && lh > 0 &&
                (mLastResizedBounds.right != lw || mLastResizedBounds.bottom != lh)) {
                ALOGI("container taskId=%d: game efficiency asserting %dx%d "
                      "(output %dx%d, dsf %.2f)",
                      mTaskId, lw, lh, mOutputWidth, mOutputHeight, mScaleFactor);
                // Record the request either way so this doesn't re-fire on
                // every state transition.
                mLastResizedBounds = {0, 0, lw, lh};
                // ATMS refuses resizeTask on a fullscreen task — and fullscreen
                // is exactly what turns efficiency mode on, so this rejection is
                // the norm, not an edge case (the onGeometryChanged path already
                // skips the call for WINDOWING_MODE_FULLSCREEN for this reason).
                // Only adopt the logical frame as the task frame if Android
                // actually took it: mContainerFrame is the origin/extent the
                // pointer and touch handlers map events into, so committing a
                // 1280x800 frame while WM still has the window at 2560x1600
                // would put the task frame and the real window out of sync.
                if (mTaskResizer.resizeTask(mTaskId, 0, 0, lw, lh)) {
                    mContainerFrame = {0, 0, lw, lh};
                }
                // The render target shrink is the actual efficiency win and is
                // purely SF/Chrome-side (SF scales the source to the target on
                // the way out), so it applies either way.
                if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
                    hwc->setTaskRenderTargetSize(mTaskId, lw, lh);
                    hwc->scheduleRefresh();
                }
            }
        }
    }

    // An app usually enters/leaves fullscreen while already focused, so no
    // activation event follows — re-evaluate the shelf here. This sits OUTSIDE
    // the block above on purpose: that block is gated on mGameFullScreen
    // changing, which for a non-game never happens, so the shelf would never be
    // re-evaluated for an ordinary app.
    //
    // MAXIMIZED <-> FULLSCREEN moves neither mFillsScreen bit (both fill), but
    // it is exactly the edge the shelf policy runs on — only fullscreen owns
    // the screen — so re-evaluate on it too: entering fullscreen asserts the
    // auto-hide, and dropping back to maximized revokes it.
    const bool fullscreenChanged =
            (prevState == WaylandWindow::WindowState::FULLSCREEN) !=
            (state == WaylandWindow::WindowState::FULLSCREEN);
    if (mFillsScreen != wasFillsScreen || fullscreenChanged) {
        onFocusChanged(mFocused);
    }

    // Record timestamp when restoring from minimized (for delayed focus)
    if (prevState == WaylandWindow::WindowState::MINIMIZED &&
        state != WaylandWindow::WindowState::MINIMIZED) {
        struct timespec ts;
        clock_gettime(CLOCK_MONOTONIC, &ts);
        mRestoredFromMinimizedNs = ts.tv_sec * 1000000000LL + ts.tv_nsec;
    }

    // Remember (trusted-)pinned kiosk windows so a trailing FULLSCREEN keeps the caption hidden.
    //
    // Mirrored into the globals as well, because this container is not the
    // last one this task will have: the pin is announced exactly once, and a
    // suspend/resume rebuilds the container (see setTaskKioskPinned). The
    // replay at window creation reads it back from there.
    if (state == WaylandWindow::WindowState::PINNED ||
        state == WaylandWindow::WindowState::TRUSTED_PINNED) {
        mKioskPinned = true;
        if (mTaskId > 0 && mClient && mClient->globals())
            mClient->globals()->setTaskKioskPinned(mTaskId, true);
    } else if (state == WaylandWindow::WindowState::NORMAL) {
        mKioskPinned = false;
        if (mTaskId > 0 && mClient && mClient->globals())
            mClient->globals()->setTaskKioskPinned(mTaskId, false);
    }

    // Leaving float. Ash restores the window's pre-float state, so a state
    // change to a screen-filling or minimized one is the signal that the float
    // is over — there is no "unfloated" state_type to watch for, just as there
    // is no floated one (bounds reason 8 is the whole float protocol here).
    if (mFloated && (state == WaylandWindow::WindowState::MAXIMIZED ||
                     state == WaylandWindow::WindowState::FULLSCREEN ||
                     state == WaylandWindow::WindowState::MINIMIZED ||
                     state == WaylandWindow::WindowState::PINNED ||
                     state == WaylandWindow::WindowState::TRUSTED_PINNED)) {
        ALOGI("container taskId=%d: left float (state=%u)",
              mTaskId, static_cast<uint32_t>(state));
        mFloated = false;
        applyFrameButtons();
        // Hand the task back its tablet steady state.
        if (mAppliedLayoutMode == ChromeLayoutMode::kTablet) {
            switchWindowingModeAsync(tabletWindowingMode());
        }
    }

    // When Chrome leaves the snapped state, call unsetSnap to deactivate its
    // SplitViewController. Mirrors the snap acknowledgement in onBoundsChanged
    // and so is likewise not tablet-only: leaving a clamshell snap by
    // maximizing must tear the split down, or Chrome keeps the window in a
    // split group it has visually left.
    //
    // A clamshell snap arrives as bounds_changed alone, and the window stays
    // NORMAL while snapped: Chrome sends state_type_changed only for a state
    // it originates, never for the snap our own set_snap_* echo commits. So
    // a NORMAL that arrives while mSnapped is ash leaving
    // kPrimary/SecondarySnapped (Alt+[ / Alt+] again, or the caption's
    // restore), and it releases the snap too; the NORMAL branch below then
    // restores the pre-snap rectangle saved in onBoundsChanged. Leaving a
    // snap by dragging or floating is released from onBoundsChanged.
    const bool clamshellUnsnap =
            state == WaylandWindow::WindowState::NORMAL &&
            mAppliedLayoutMode != ChromeLayoutMode::kTablet;
    if (mTaskId > 0 && mSnapped &&
        (state == WaylandWindow::WindowState::MAXIMIZED ||
         state == WaylandWindow::WindowState::FULLSCREEN || clamshellUnsnap)) {
        if (clamshellUnsnap) {
            ALOGI("container taskId=%d: unsnapped (NORMAL while snapped)",
                  mTaskId);
            // Dragged out of the split: give back the pre-snap size where the
            // window was dropped, keeping the cursor at the same fraction of
            // the caption, as ash does for its own windows. The pre-snap
            // origin would yank the window out from under the cursor.
            if (mSnapDragEnded && mSavedBounds.valid && mServerBoundsW > 0) {
                const float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
                const float cursorX = mSnapDragEndX / dsf;
                const float frac = (cursorX - mServerBoundsX) / mServerBoundsW;
                mSavedBounds.x = static_cast<int32_t>(
                        std::lround(cursorX - frac * mSavedBounds.w));
                mSavedBounds.y = mServerBoundsY;
                ALOGI("container taskId=%d: dragged out of the snap — restore "
                      "at (%d,%d)", mTaskId, mSavedBounds.x, mSavedBounds.y);
            }
        }
        mSnapped = false;
        if (mWindowCategory == WaylandWindowCategory::APP && !mIsDialog && mWindow) {
            static_cast<WaylandAppWindow*>(mWindow.get())->unsetSnap();
        }
        // Return the task to its tablet steady state. Clamshell tasks were
        // never moved off freeform, so there is nothing to restore.
        if (mAppliedLayoutMode == ChromeLayoutMode::kTablet) {
            switchWindowingModeAsync(tabletWindowingMode());
        }
    }
    mSnapDragEnded = false;

    // Autohide the title bar when Chrome puts us in MAXIMIZED / FULLSCREEN
    // (clamshell mode only — tablet mode keeps FRAME_NONE from
    // applyLayoutMode). Chrome's "fullscreen" / "maximize" doesn't change
    // Android's windowing mode, so we can't gate this on
    // switchWindowingModeAsync — it fires only here.
    if (mAppliedLayoutMode != ChromeLayoutMode::kTablet && !mIsDialog &&
        mWindowCategory == WaylandWindowCategory::APP) {
        auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
        if (appWindow) {
            if (mKioskPinned) {
                // Kiosk: no caption at all, not even revealable on hover.
                appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE);
            } else if (mUnresizable) {
                // Unresizable: always the fullscreen caption, whatever state
                // Chrome reports (see applyUnresizableFullscreen).
                appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_AUTOHIDE);
            } else if (state == WaylandWindow::WindowState::FULLSCREEN) {
                // Fullscreen is the mode that hides the caption. The shelf is
                // already auto-hidden (stateFillsScreen covers both states) and
                // stays that way, and the pre-fullscreen bounds were saved on
                // the way in — see the MAXIMIZED/FULLSCREEN branch below.
                appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_AUTOHIDE);
            } else if (state == WaylandWindow::WindowState::MAXIMIZED ||
                       state == WaylandWindow::WindowState::NORMAL) {
                // Maximized keeps its title bar — that is what distinguishes it
                // from fullscreen. It had been sharing the AUTOHIDE caption,
                // which left a maximized window looking fullscreen and gave the
                // user no way back short of guessing where the caption was.
                appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NORMAL);
            }
        }
    }

    if (mWindow) {
        if (state == WaylandWindow::WindowState::MINIMIZED) {
            mWindow->minimize();
        } else if (state == WaylandWindow::WindowState::MAXIMIZED ||
            state == WaylandWindow::WindowState::FULLSCREEN) {
            // In tablet mode, skip save — bounds are managed by Chrome's
            // geometry_changed and snap/unsnap, not by save/restore.
            if (mAppliedLayoutMode != ChromeLayoutMode::kTablet &&
                !mSavedBounds.valid) {
                mSavedBounds = {mServerBoundsX, mServerBoundsY,
                                mServerBoundsW, mServerBoundsH, true};
                ALOGI("container taskId=%d: saved bounds (%d,%d %dx%d)",
                      mTaskId, mSavedBounds.x, mSavedBounds.y,
                      mSavedBounds.w, mSavedBounds.h);
            }
            // Maximized gets the work area, fullscreen the whole output —
            // allowedWindowRect() reads mCurrentState, which was assigned from
            // `state` above. Asking for the output while ash has us merely
            // maximized just invites ash's own clamp back at us as a bounds
            // change; see allowedWindowRect().
            const OutputRect r = allowedWindowRect();
            mWindow->setBoundsInOutput(mOutput, r.x, r.y, r.w, r.h);
            if (state == WaylandWindow::WindowState::MAXIMIZED)
                mWindow->maximize();
            else
                mWindow->fullscreen();
        } else if (state == WaylandWindow::WindowState::PINNED ||
                   state == WaylandWindow::WindowState::TRUSTED_PINNED) {
            // Kiosk: go real-fullscreen off the pin itself rather than waiting
            // for a state we don't control. Chrome's M150 kiosk launcher
            // happens to pin and then immediately fullscreen, so the
            // MAXIMIZED/FULLSCREEN branch above would size the window — but
            // that ordering is Chrome's, not a contract. Asserting it here
            // means kiosk geometry no longer depends on it.
            applyKioskFullscreen(mOutputWidth, mOutputHeight);
        } else if (state == WaylandWindow::WindowState::NORMAL && mUnresizable) {
            // An unresizable app has nowhere to be restored to. Something still
            // managed to put it back to NORMAL — an unsnap, a double-click on
            // the caption, a window-state push that raced window creation — so
            // take it straight back to fullscreen rather than leaving it in a
            // freeform rectangle it cannot be resized out of.
            ALOGI("container taskId=%d: NORMAL on unresizable app, re-asserting "
                  "fullscreen", mTaskId);
            applyUnresizableFullscreen(mOutputWidth, mOutputHeight);
        } else if (state == WaylandWindow::WindowState::NORMAL) {
            // In tablet mode, skip restore — stale snapped bounds would
            // cause half-screen rendering when returning from home.
            if (mAppliedLayoutMode != ChromeLayoutMode::kTablet &&
                mSavedBounds.valid) {
                // Saved bounds are DIP (from geometry_changed), convert to output coords
                float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
                int32_t ox = static_cast<int32_t>(mSavedBounds.x * dsf);
                int32_t oy = static_cast<int32_t>(mSavedBounds.y * dsf);
                int32_t ow = static_cast<int32_t>(mSavedBounds.w * dsf);
                int32_t oh = static_cast<int32_t>(mSavedBounds.h * dsf);
                mSkipMaskForRestore =
                        prevState == WaylandWindow::WindowState::MAXIMIZED ||
                        prevState == WaylandWindow::WindowState::FULLSCREEN;
                mWindow->setBoundsInOutput(mOutput, ox, oy, ow, oh);
                ALOGI("container taskId=%d: restoring bounds DIP(%d,%d %dx%d) output(%d,%d %dx%d)",
                      mTaskId, mSavedBounds.x, mSavedBounds.y,
                      mSavedBounds.w, mSavedBounds.h, ox, oy, ow, oh);

                // Shrink the Android task back to the pre-maximize window size.
                // Chrome does not echo geometry_changed after restore() (it
                // restores the widget to its remembered bounds and only reports
                // the NORMAL state, assuming the client already knows the size),
                // so onGeometryChanged never fires to drive resizeTask — without
                // this the task stays pinned at the maximized bounds while the
                // surface is the small restored size, and the app keeps its
                // fullscreen layout squeezed into the small window. mSavedBounds
                // is the live window size captured at maximize (kept current by
                // both bounds_changed and geometry_changed), so unlike
                // mLastResizedBounds it is correct even after a drag-resize
                // (which resizes via the AR-step path, not onGeometryChanged).
                // Mirror onGeometryChanged's scaling: task in androidDSF px,
                // render target in outputDSF px. Guard w/h > 0 (a window that
                // launched maximized has no windowed size to go back to).
                if (!mIsDialog && !mKioskPinned && mTaskId > 0 &&
                    mWindowCategory == WaylandWindowCategory::APP &&
                    mCurrentWindowingMode !=
                            WaylandTaskResizer::WINDOWING_MODE_FULLSCREEN &&
                    mSavedBounds.w > 0 && mSavedBounds.h > 0) {
                    float taskScale =
                            (mAndroidDsf > 0.0f) ? mAndroidDsf : dsf;
                    int32_t rw = static_cast<int32_t>(mSavedBounds.w * taskScale);
                    int32_t rh = static_cast<int32_t>(mSavedBounds.h * taskScale);
                    int32_t tw = static_cast<int32_t>(mSavedBounds.w * dsf);
                    int32_t th = static_cast<int32_t>(mSavedBounds.h * dsf);
                    ALOGI("container taskId=%d: restore -> resizeTask %dx%d, "
                          "render target %dx%d", mTaskId, rw, rh, tw, th);
                    mTaskResizer.resizeTask(mTaskId, 0, 0, rw, rh);
                    mLastResizedBounds = {0, 0, rw, rh};
                    mContainerFrame = {0, 0, rw, rh};
                    if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
                        hwc->setTaskRenderTargetSize(mTaskId, tw, th);
                        hwc->scheduleRefresh();
                    }
                }
                // Chrome sends no geometry_changed after restore(), so
                // mServerBounds* would keep the maximized geometry. The next
                // maximize snapshots it as the "pre-maximize" size and restore
                // then hands back a maximized window. Sync it ourselves.
                mServerBoundsX = mSavedBounds.x;
                mServerBoundsY = mSavedBounds.y;
                mServerBoundsW = mSavedBounds.w;
                mServerBoundsH = mSavedBounds.h;
                mSavedBounds.valid = false;
            }
            mWindow->restore();
        }
    }
}

void WaylandLayerContainer::applyFrameColors(uint32_t active, uint32_t inactive) {
    mHeaderFrameActive = active;
    mHeaderFrameInactive = inactive;
    pushFrameColors();
}

void WaylandLayerContainer::applyTaskFrameColor(uint32_t active, uint32_t inactive) {
    mTaskFrameActive = active;
    mTaskFrameInactive = inactive;
    ALOGI("container taskId=%d: app caption colour #%08X/#%08X%s", mTaskId,
          active, inactive, active ? "" : " (cleared, back to ChromeOS header)");
    pushFrameColors();
}

void WaylandLayerContainer::applyAccessibilityId(int32_t windowId) {
    if (!mWindow) return;
    mWindow->setAccessibilityId(windowId);
}

void WaylandLayerContainer::setStandaloneAccessibilityId(int32_t windowId) {
    if (windowId < 0) windowId = -1;
    if (mStandaloneA11yId == windowId) return;
    mStandaloneA11yId = windowId;
    // Before the window exists this only records the id; ensureWindow's
    // catch-up publishes it when the surface is created.
    applyAccessibilityId(windowId);
}

void WaylandLayerContainer::applyResizeLimits(WaylandAppWindow* window) {
    if (!window) return;
    // Resize limits are in DIP — divide physical by DSF.
    // An unresizable activity is advertised as fixed-size so Chrome never
    // offers to snap it into split view; it cannot take a half-screen bound,
    // and the snap would only relaunch it at an aspect-ratio-stepped size.
    float sf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
    bool resizable = true;
    int32_t minW = -1, minH = -1;
    if (mTaskId > 0) {
        resizable = mTaskResizer.isTaskResizable(mTaskId);
        mUnresizable = !resizable;
        // Android reports the declared minimum in px; the limits Chrome wants
        // are DIP.
        int32_t pxW = -1, pxH = -1;
        if (mTaskResizer.getTaskMinSize(mTaskId, &pxW, &pxH)) {
            if (pxW > 0) minW = static_cast<int32_t>(pxW / sf);
            if (pxH > 0) minH = static_cast<int32_t>(pxH / sf);
        }
        ALOGI("container taskId=%d: resizable=%d declared min=%dx%d dip",
              mTaskId, resizable, minW, minH);
    }
    window->setResizeLimits(static_cast<int32_t>(mOutputWidth / sf),
                            static_cast<int32_t>(mOutputHeight / sf),
                            resizable, minW, minH);
}

bool WaylandLayerContainer::isPipDragTarget() const {
    // Both halves matter. mPip.isPip() is Android's view, which is what window
    // policy must key off; constrained() says ash will actually build a
    // WindowResizer for it, without which start_move is dropped in silence.
    return mPip.isPip() && mPip.constrained();
}

void WaylandLayerContainer::pipStartMove(int32_t surfaceLocalX, int32_t surfaceLocalY) {
    if (!mWindow || mWindowCategory != WaylandWindowCategory::APP) return;
    auto* appWin = static_cast<WaylandAppWindow*>(mWindow.get());
    // exo wants the grab point relative to the DISPLAY, not the surface, and
    // in OUTPUT PIXELS — the same space `surfaceLocalX/Y` arrive in and the
    // same space set_bounds_in_output uses. THREE different spaces are in
    // reach here and two of them are wrong:
    //
    //   mWindowOffsetX/Y    ANDROID task pixels (mAndroidDsf-scaled) — measured
    //                       start_move (3610,960) for a press at (322,280) on a
    //                       window at x=1616, i.e. ~2x too big.
    //   boundsX()/boundsY() DIP. It is last written by window_geometry_changed,
    //                       which reports DIP, so it is HALF the origin that
    //                       bounds_changed_in_output reports for the same
    //                       window — measured 822 against 1644. Sending it made
    //                       ash anchor the grab 822px left of the pointer, so
    //                       the window jumped +814px the instant the drag
    //                       started, then tracked correctly from there. That
    //                       jump reads as "the drag has an offset".
    //   boundsX() * DSF     the origin in output pixels. This one.
    //
    // Note this is a scale mismatch, not a rotation one: this panel is
    // physically 1600x2560 with Android at ROTATION_90, but both the pointer
    // and ash's bounds are already in the logical 2560x1600 landscape frame, so
    // no rotation mapping belongs here.
    const float sf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
    appWin->startMove(static_cast<int32_t>(appWin->boundsX() * sf) + surfaceLocalX,
                      static_cast<int32_t>(appWin->boundsY() * sf) + surfaceLocalY);
}

void WaylandLayerContainer::applyPipState(bool inPip) {
    // Not an early return on an unchanged state: the size arrives in a second
    // message once WM has resolved the pinned rectangle (see
    // ArcWindowManagerService's pip bounds listener), so a repeat carrying the
    // same state is the normal way the real size shows up.
    const bool stateChanged = mPip.setPip(inPip);
    pushPipState(stateChanged);
}

void WaylandLayerContainer::pushPipState(bool stateChanged) {
    // Only a real app window can be a PIP window. A dialog/modal is drawn by SF
    // at a position of its own choosing and has no remote surface state to hand
    // to ash.
    if (!mWindow || mWindowCategory != WaylandWindowCategory::APP || mIsDialog) return;
    auto* appWin = static_cast<WaylandAppWindow*>(mWindow.get());

    if (stateChanged && mPip.isPip()) {
        // PIP supersedes every other window state. Drop the ones we track so
        // the handlers that reassert them (the tablet maximize reassert in
        // onGeometryChanged, the drag-out test in onBoundsChanged case 5)
        // cannot fight ash over the PIP rectangle.
        if (mSnapped) {
            mSnapped = false;
            appWin->unsetSnap();
        }
        mFloated = false;
    }

    // The pip=0 message carries the rectangle to restore to, in the same
    // per-task slot the PIP size uses.
    int32_t restoreDipW = 0, restoreDipH = 0;
    if (!mPip.isPip() && mClient && mClient->globals()) {
        int32_t rw = 0, rh = 0;
        if (mClient->globals()->taskPipBounds(mTaskId, &rw, &rh) && rw > 0 && rh > 0) {
            // px — set_bounds_in_output is a pixel-space call.
            restoreDipW = rw;
            restoreDipH = rh;
        }
    }
    if (stateChanged) {
        // PIP swaps the caption's button set (see applyFrameButtons).
        applyFrameButtons();
    }
    if (mPip.push(appWin, mOutput, stateChanged, mScaleFactor,
                  mWindowOffsetX, mWindowOffsetY,
                  mLastGeometryW, mLastGeometryH,
                  mOutputWidth, mOutputHeight,
                  // Size to come back to on exit, in DIP. Supplied by
                  // ArcWindowManagerService on the pip=0 message and carried in
                  // the same slot as the PIP size — WM is the only side that
                  // knows it, and every attempt to infer it here was wrong
                  // (the window is mid-launch when an app PIPs from onCreate).
                  restoreDipW, restoreDipH)) {
        wl_surface_commit(appWin->surface());
    }

    // Leaving PIP: the controller has already put can_maximize back, but the
    // PIP min/max it sent are still what ash holds. Re-assert the window's real
    // limits, or this window keeps a maximum of half the screen for the rest of
    // its life — which, per exo's rule, would also keep it non-maximizable.
    if (stateChanged && !mPip.isPip()) {
        applyResizeLimits(appWin);
        wl_surface_commit(appWin->surface());
    }
}

void WaylandLayerContainer::pushFrameColors() {
    if (!mWindow || mWindowCategory != WaylandWindowCategory::APP) return;
    // The app's own colour wins: a caption in the app's background reads as
    // part of the window, where the system header reads as chrome bolted on
    // top of it. The header is what a task that never declares one gets.
    const uint32_t active = mTaskFrameActive ? mTaskFrameActive : mHeaderFrameActive;
    const uint32_t inactive = mTaskFrameActive ? mTaskFrameInactive : mHeaderFrameInactive;
    if (!active) return;  // nothing known yet — leave ash on its default
    static_cast<WaylandAppWindow*>(mWindow.get())->setFrameColors(active, inactive);
}

void WaylandLayerContainer::applyLayoutMode(ChromeLayoutMode mode, int32_t outputW,
                                             int32_t outputH) {
    // Tablet mode is a system status — we apply it to every app window.
    // Skip non-app surfaces (IME/toast/overlay/notification/dialogs) since
    // their geometry is not laid out by Chrome's layout mode.
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP) return;
    if (outputW <= 0 || outputH <= 0) return;

    // A PIP window is never laid out by the layout mode. Its geometry is ash's,
    // and "fill the work area" — what tablet mode means — is the one shape it
    // must never take.
    //
    // The same-mode branch below is the one that bites: a ROTATION keeps the
    // mode but changes the output dimensions, so it re-asserts full-screen
    // bounds on every window. Applied to a PIP window that resized it to the
    // rotated screen and WM took the task straight back out of PINNED —
    // measured on device as the PIP window vanishing on every rotation
    // ("android pip=0 bounds=1600x2480", ~100ms BEFORE the RotationLayer even
    // appeared, so it reads as "the PIP force-closed at rotation").
    //
    // Guarded here rather than at the call sites so every path is covered:
    // window creation, layout-mode change, and the rotation refresh.
    if (mPip.isPip()) {
        ALOGI("container taskId=%d: skipping layout mode for a PIP window", mTaskId);
        return;
    }

    // A kiosk window is full-screen in either layout mode — the app owns the
    // screen, so clamshell must not hand it back a caption and saved bounds.
    // An unresizable app is full-screen in either mode for its own reason: it
    // has no other size it can legally take.
    const bool wantFullscreen =
            (mode == ChromeLayoutMode::kTablet) || mKioskPinned || mUnresizable;

    // Same-mode call: rotation changes output dimensions while the mode stays
    // put. Refresh bounds so the task fills the rotated output.
    if (mode == mAppliedLayoutMode) {
        if (wantFullscreen) {
            auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
            if (appWindow && appWindow->remoteSurface()) {
                const OutputRect r = allowedWindowRect();
                appWindow->setBoundsInOutput(mOutput, r.x, r.y, r.w, r.h);
            }
        }
        return;
    }

    auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
    if (!appWindow || !appWindow->remoteSurface()) {
        // Window not yet created — record the desired mode so it gets
        // applied when ensureWindow() runs.
        mAppliedLayoutMode = mode;
        return;
    }

    ChromeLayoutMode prev = mAppliedLayoutMode;
    mAppliedLayoutMode = mode;

    ALOGI("container taskId=%d: applyLayoutMode %s -> %s (output %dx%d)",
          mTaskId, toString(prev), toString(mode), outputW, outputH);

    if (mKioskPinned) {
        // Kiosk wins over whatever layout mode Chrome reports.
        applyKioskFullscreen(outputW, outputH);
    } else if (mUnresizable) {
        // Same for an unresizable app, in either direction: entering clamshell
        // must not hand it a caption and a freeform rectangle, and entering
        // tablet needs no special casing beyond the fullscreen it already has.
        applyUnresizableFullscreen(outputW, outputH);
    } else if (mode == ChromeLayoutMode::kTablet && mSnapped) {
        // TABLET, but the task is in a split. Everything below this branch
        // exists to make a window own the screen, and a snapped one must not:
        // asking Chrome to maximize half of a split is how the snap group gets
        // broken, and Chrome answers by popping the *partner* out too.
        //
        // This is reachable because the mode is applied per container and a
        // container is rebuilt whenever its app is relaunched — which a
        // rotation does to every app that does not handle the configuration
        // change. mSnapped is carried across that rebuild (see
        // WaylandLayerManager::registerContainer); without this branch the
        // carry would be pointless, since applyLayoutMode runs first and
        // maximizes.
        //
        // The frame type and the windowing mode still apply: a snapped tablet
        // window has no caption either, and it needs the tablet windowing mode
        // like any other. Bounds and state are Chrome's — it is about to send
        // the snap half as a reason 3/4 bounds change.
        ALOGI("container taskId=%d: tablet layout on a snapped task — "
              "not maximizing", mTaskId);
        appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE);
        mFillsScreen = stateFillsScreen(mCurrentState);
        mGameFullScreen = mIsGame && mFillsScreen;
        onFocusChanged(mFocused);
        switchWindowingModeAsync(tabletWindowingMode());
    } else if (mode == ChromeLayoutMode::kTablet) {
        // TABLET: no caption bar, work-area bounds, maximized.
        // Arm the geometry-echo reassert (see onGeometryChanged) — the
        // requests below can race window creation and get dropped by exo.
        armReassert();
        if (!mSavedBounds.valid) {
            mSavedBounds = {mServerBoundsX, mServerBoundsY,
                            mServerBoundsW, mServerBoundsH, true};
            ALOGI("container taskId=%d: tablet mode saved bounds (%d,%d %dx%d)",
                  mTaskId, mSavedBounds.x, mSavedBounds.y,
                  mSavedBounds.w, mSavedBounds.h);
        }
        appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE);
        // The work area, not the whole output: maximize() puts this window in
        // ash's kMaximized state, and ash clamps a maximized window into the
        // work area whatever we ask for. Asking for the output anyway meant
        // ash's clamp came back as a WM-driven bounds change we then refused,
        // once per tablet entry and once per rotation, for a rectangle we
        // were never going to be allowed to keep. See allowedWindowRect().
        {
            const OutputRect r = allowedWindowRect();
            appWindow->setBoundsInOutput(mOutput, r.x, r.y, r.w, r.h);
        }
        appWindow->maximize();

        // Record the state we just asked for. Chrome sends no
        // state_type_changed in reply to a state we requested ourselves, so
        // without this mCurrentState keeps whatever clamshell left behind —
        // and a window that had been FULLSCREEN there stays FULLSCREEN for the
        // whole tablet session, long after ash has maximized it. Everything
        // that asks "does this window own the screen" reads mCurrentState
        // (allowedWindowRect, onFocusChanged), so a stale value there is the
        // difference between the work area and the whole output.
        mCurrentState = WaylandWindow::WindowState::MAXIMIZED;

        // This call is what actually puts the window fullscreen in tablet mode,
        // and Chrome sends no state_type_changed in reply — so onStateChanged
        // never runs and nothing else would ever notice the app is filling the
        // screen. Re-evaluate here, or an app launched while already in tablet
        // mode keeps the shelf over it.
        mFillsScreen = stateFillsScreen(mCurrentState);
        mGameFullScreen = mIsGame && mFillsScreen;
        onFocusChanged(mFocused);

        switchWindowingModeAsync(tabletWindowingMode());
    } else {
        // CLAMSHELL: restore caption bar and saved bounds — but not while the
        // window is maximized/fullscreen. The first maximize of a session can
        // land just before the lazy UNKNOWN -> CLAMSHELL push, and restoring
        // here would silently undo it (the click then only "works" on the
        // second try, once the mode is already CLAMSHELL and this no longer
        // runs).
        //
        // A SNAPPED window counts as filled too. ash carries a tablet split
        // into clamshell as a snap group, and restoring saved bounds here tore
        // that down the moment a keyboard was attached: the window jumped back
        // to its pre-tablet rectangle and the partner was left alone in the
        // split. mSnapped is checked alongside the snapped state types because
        // a clamshell snap arrives as bounds_changed alone, with no state
        // change to record (see onBoundsChanged reasons 3/4).
        const bool stateIsMaximized =
                mCurrentState == WaylandWindow::WindowState::MAXIMIZED ||
                mCurrentState == WaylandWindow::WindowState::FULLSCREEN;
        const bool stateIsSnapped =
                mSnapped ||
                mCurrentState == WaylandWindow::WindowState::LEFT_SNAPPED ||
                mCurrentState == WaylandWindow::WindowState::RIGHT_SNAPPED;
        const bool stateIsFilled = stateIsMaximized || stateIsSnapped;
        if (stateIsFilled) {
            ALOGI("container taskId=%d: clamshell keeping %s bounds (state=%u)",
                  mTaskId, stateIsSnapped && !stateIsMaximized ? "snapped"
                          : (mCurrentState == WaylandWindow::WindowState::FULLSCREEN
                                     ? "fullscreen" : "maximized"),
                  static_cast<uint32_t>(mCurrentState));
            // Give the window its caption back. Tablet mode left it at
            // FRAME_NONE and there is no state change on the way out to make
            // onStateChanged do it — ash keeps a maximized window maximized
            // across the fold, and pushes no frame type at all for a snapped
            // state. Without this a window arriving from tablet mode has no
            // title bar, and now no shelf either (the assert is re-evaluated
            // at the end of this branch), so nothing to close or restore it
            // with. Deliberately the same mapping onStateChanged uses for
            // clamshell, so the UNKNOWN -> CLAMSHELL race the comment below
            // warns about lands on the value that path would have set anyway.
            appWindow->setFrameType(
                    mCurrentState == WaylandWindow::WindowState::FULLSCREEN
                            ? ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_AUTOHIDE
                            : ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NORMAL);
        } else {
            // The caption is subject to the same race as the bounds: a
            // fullscreen/maximize sets AUTOHIDE, then this push lands a few ms
            // later and puts the title bar back. Only restore NORMAL when the
            // window is genuinely not filled — otherwise leave the AUTOHIDE
            // that onStateChanged just applied.
            appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NORMAL);
        }
        if (mSavedBounds.valid && !stateIsFilled) {
            float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
            int32_t ox = static_cast<int32_t>(mSavedBounds.x * dsf);
            int32_t oy = static_cast<int32_t>(mSavedBounds.y * dsf);
            int32_t ow = static_cast<int32_t>(mSavedBounds.w * dsf);
            int32_t oh = static_cast<int32_t>(mSavedBounds.h * dsf);
            appWindow->setBoundsInOutput(mOutput, ox, oy, ow, oh);
            ALOGI("container taskId=%d: clamshell restoring bounds DIP(%d,%d %dx%d) output(%d,%d %dx%d)",
                  mTaskId, mSavedBounds.x, mSavedBounds.y,
                  mSavedBounds.w, mSavedBounds.h, ox, oy, ow, oh);
            mSavedBounds.valid = false;
        }
        if (!stateIsFilled) {
            appWindow->restore();
        }
        // Leaving tablet mode: the task was in the tablet windowing mode
        // (MULTI_WINDOW) — restore FREEFORM so it is resizable again and
        // follows clamshell window bounds, including a clamshell snap, which
        // is freeform with half-screen bounds. Only on a real
        // tablet→clamshell transition; the boot-time 0→1 push must not churn
        // windowing modes.
        if (prev == ChromeLayoutMode::kTablet) {
            switchWindowingModeAsync(WaylandTaskResizer::WINDOWING_MODE_FREEFORM);
        }

        // stateFillsScreen() is unconditionally true in tablet mode, so leaving
        // it can drop this window out of "fills screen" with no state change to
        // announce it. Recompute, or the flag stays latched true and we would
        // re-assert auto-hide for a restored freeform window on its next focus
        // change.
        mFillsScreen = stateFillsScreen(mCurrentState);
        mGameFullScreen = mIsGame && mFillsScreen;

        // ...and then re-evaluate the shelf, which the recompute above only
        // ever made stale. Both directions matter: a GAME folding out of
        // tablet still maximized is entitled to its assert again, and a
        // window that asserted under an earlier owns-the-screen state needs
        // the revoke push — declining alone leaves ash holding the flag and
        // the shelf hidden over a merely maximized window.
        //
        // Gated on stateIsFilled to stay out of the leaving-maximized race the
        // comment in onFocusChanged warns about: a window that is genuinely
        // not filled took the restore() path above, and pushing anything at
        // ash while it is coming out of maximize makes the restore look like
        // it did nothing. mFillsScreen is the real gate inside onFocusChanged
        // — a snapped window is filled for the purposes of this branch but not
        // screen-filling, so it correctly asserts nothing.
        if (stateIsFilled) {
            onFocusChanged(mFocused);
        }
    }
}

void WaylandLayerContainer::applyKioskFullscreen(int32_t outputW, int32_t outputH) {
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP) return;
    if (outputW <= 0 || outputH <= 0) return;
    auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
    if (!appWindow || !appWindow->remoteSurface()) return;

    ALOGI("container taskId=%d: kiosk fullscreen (output %dx%d)",
          mTaskId, outputW, outputH);

    // Arm the budget the two repair paths spend (the bounds refusal in
    // onBoundsChanged, the origin repair in onGeometryChanged). Both already
    // name mKioskPinned, but neither could ever fire: armReassert() was only
    // reached from the TABLET branch of applyLayoutMode and from an output
    // shape change, so a kiosk window carried a budget of 0 for its whole
    // life. Measured on a kiosk board: ash placed the window with its
    // own open-animation rectangle (59x736 at the centred, caption-inset
    // origin 653,32, reason=5), the refusal was skipped for want of budget,
    // and echoing that rectangle back committed the origin — the window grew
    // to the full 1366x768 but stayed at (653,32), i.e. most of the app off
    // the right and bottom of the screen.
    // This is the only place a kiosk window is asserted from, and both repairs
    // re-apply their rectangle inline rather than calling back into here, so
    // arming here cannot re-arm itself into a ping-pong with ash.
    armReassert();

    // FRAME_TYPE_NONE, not AUTOHIDE: a kiosk caption must not even be
    // revealable by moving the pointer to the top of the screen.
    appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE);
    appWindow->setBoundsInOutput(mOutput, 0, 0, outputW, outputH);

    // Android-side FULLSCREEN: no caption decor and task bounds come from the
    // display config, which is what makes this *real* fullscreen rather than a
    // freeform window that merely happens to cover the output.
    switchWindowingModeAsync(WaylandTaskResizer::WINDOWING_MODE_FULLSCREEN);

    // Deliberately no maximize()/fullscreen() request. Chrome's kiosk launcher
    // already drives the window state, and echoing a state back at it invites a
    // state<->configure ping-pong. FRAME_NONE plus full-output bounds gives the
    // caption-less, screen-filling result on its own; the window state is
    // Chrome's to own.
}

void WaylandLayerContainer::applyFrameButtons() {
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP) return;
    auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
    if (!appWindow || !appWindow->remoteSurface()) return;

    constexpr uint32_t kMinimize =
            ZCR_REMOTE_SURFACE_V2_FRAME_BUTTON_TYPE_MINIMIZE;
    constexpr uint32_t kMaximizeRestore =
            ZCR_REMOTE_SURFACE_V2_FRAME_BUTTON_TYPE_MAXIMIZE_RESTORE;
    constexpr uint32_t kClose = ZCR_REMOTE_SURFACE_V2_FRAME_BUTTON_TYPE_CLOSE;

    // A PIP window has no frame at all — its controls are the WM Shell's own
    // PipMenuView, which ArcSystemUI composites into the task itself — so there
    // is no button set to state. Leave the mask alone rather than asserting one
    // for a caption that is not drawn.
    if (mPip.isPip()) return;

    uint32_t buttons;
    if (mUnresizable) {
        // Nowhere to maximize or restore to; see applyUnresizableFullscreen.
        buttons = kMinimize | kClose;
    } else {
        // Chrome's usual set for an ARC window, including the size button that
        // carries the window-layout menu. Spelled out rather than left alone
        // because the unresizable case above has to be undone if the task
        // turns out to be resizable after all.
        buttons = kMinimize | kMaximizeRestore | kClose;
    }
    appWindow->setFrameButtons(buttons, buttons);
}

void WaylandLayerContainer::applyUnresizableFullscreen(int32_t outputW,
                                                        int32_t outputH) {
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP) return;
    if (outputW <= 0 || outputH <= 0) return;
    auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
    if (!appWindow || !appWindow->remoteSurface()) return;

    ALOGI("container taskId=%d: unresizable fullscreen (output %dx%d)",
          mTaskId, outputW, outputH);

    // Arm the budget the bounds refusal (onBoundsChanged) and the off-origin
    // repair (onGeometryChanged) spend — the kiosk fix in applyKioskFullscreen,
    // for the same reason: without it the budget stayed 0, ash's caption-inset
    // placement right after this request was echoed back, and the window stayed
    // under a 32-DIP strip (Crossy Road on device). Neither repair calls back into
    // here, so this cannot re-arm itself into a ping-pong.
    armReassert();

    // Fullscreen, not maximized: the two differ by the caption, and this app
    // gets the fullscreen one — AUTOHIDE, revealable at the top of the screen
    // so close and minimise stay reachable, rather than the permanent title bar
    // a maximized window keeps. NONE is wrong here too: that is the kiosk
    // treatment, where the caption must not be revealable at all.
    //
    // The restore/maximize button is already gone — setResizeLimits drops
    // can_maximize for an unresizable activity — and min == max == the output
    // leaves Chrome no room to offer a snap.
    appWindow->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_AUTOHIDE);

    // Drop the restore button from that caption. unset_can_maximize (see
    // setResizeLimits) governs whether the window may be *maximized*, but the
    // frame of an already-fullscreen window still offers restore, which is the
    // one thing this app must not be given: it has no other size to go to.
    // Minimize and close stay, so the window is still dismissable.
    applyFrameButtons();

    appWindow->setBoundsInOutput(mOutput, 0, 0, outputW, outputH);
    appWindow->fullscreen();

    // Android-side FULLSCREEN: no caption decor and task bounds come from the
    // display config. This is also what keeps the app out of split view —
    // FULLSCREEN rejects resizeTask, so it cannot follow a half-screen bound.
    switchWindowingModeAsync(WaylandTaskResizer::WINDOWING_MODE_FULLSCREEN);

    // Chrome sends no state_type_changed in reply to the request above — same
    // as the tablet maximize — so onStateChanged never runs and nothing else
    // would notice the app now fills the screen. Both the shelf assert and the
    // game-fullscreen handling key off mFillsScreen, so set it from what we
    // just did rather than from a state that is never going to arrive.
    mFillsScreen = true;
    mGameFullScreen = mIsGame && mFillsScreen;
    onFocusChanged(mFocused);

    // Nothing to restore to. A saved rectangle here would only give a later
    // clamshell push something to hand the window back to.
    mSavedBounds.valid = false;
}

void WaylandLayerContainer::onOutputChanged(WaylandWindow* /*window*/,
                                             wl_output* output) {
    if (!output || output == mOutput) return;

    int32_t displayIndex = mClient->displayIndexForOutput(output);
    auto* newDisplay = (displayIndex >= 0 && mClient->globals())
            ? mClient->globals()->display(displayIndex)
            : nullptr;

    ALOGI("container taskId=%d: output changed to %p (was %p), displayIndex=%d",
          mTaskId, output, mOutput, displayIndex);

    mOutput = output;

    // Arm a one-shot immediate recompose for the next geometry update (which
    // resizes the render target to this display's DSF), so the new-size buffer
    // is presented promptly instead of a frame later — see mPendingCrossoverRefresh.
    mPendingCrossoverRefresh = true;

    // The Android task always stays on the primary display (display 0) in
    // ARC's display manager — there are no per-task Android displays. What
    // changes when the window crosses physical displays is the buffer→screen
    // scale: each wl_output has its own DSF (display 0=2.0, HDMI=1.10), and
    // we must update set_scale_factor on the remote surface so Chrome maps
    // our 2x buffer to the correct number of output pixels on the new
    // display. Without this, the window renders too big or too small on the
    // new display and pointer coords don't line up with visible content.
    if (newDisplay && mWindow && !mGameEfficiency &&
        mWindowCategory == WaylandWindowCategory::APP && !mIsDialog) {
        float newScale = newDisplay->scaleFactor();
        if (newScale > 0.0f && newScale != mScaleFactor) {
            mScaleFactor = newScale;
            auto* appWin = static_cast<WaylandAppWindow*>(mWindow.get());
            if (appWin->remoteSurface()) {
                uint32_t asUint;
                memcpy(&asUint, &newScale, sizeof(float));
                zcr_remote_surface_v2_set_scale_factor(
                        appWin->remoteSurface(), asUint);
                ALOGI("container taskId=%d: set_scale_factor=%.2f (new display)",
                      mTaskId, newScale);
            }

            // Resize the SF render target to the new display's DSF now, from
            // the last known geometry. When the window crosses ONTO a display
            // Chrome streams geometry_changed (the drag), and onGeometryChanged
            // resizes the target — but when it crosses BACK to its origin
            // display Chrome often sends no further geometry_changed, so the
            // target would stay pinned at the previous display's scale (e.g. the
            // HDMI 1.10 buffer shown on the 1.00 internal panel → content ~10%
            // too big, out of sync with the window). Recompute here as the
            // system-scale-change path does; the source stays latched on
            // mAndroidDsf and SF scales source→target on the way out.
            if (mTaskId > 0 && mServerBoundsW > 0 && mServerBoundsH > 0 &&
                !mGameEfficiency) {
                if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
                    int32_t targetW =
                            static_cast<int32_t>(mServerBoundsW * newScale);
                    int32_t targetH =
                            static_cast<int32_t>(mServerBoundsH * newScale);
                    ALOGI("container taskId=%d: crossover -> render target "
                          "%dx%d (geom=%dx%d × %.2f)", mTaskId, targetW, targetH,
                          mServerBoundsW, mServerBoundsH, newScale);
                    hwc->setTaskRenderTargetSize(mTaskId, targetW, targetH);
                    hwc->scheduleRefresh();
                }
            }
        }
    }
}

void WaylandLayerContainer::onWindowClosed(WaylandWindow* /*window*/) {
    ALOGI("container taskId=%d: window closed by Chrome", mTaskId);
    mClosed = true;

    // Remove the Android task so the app actually closes
    if (mTaskId > 0) {
        mTaskResizer.removeTask(mTaskId);
    }
}

// persist.fydroid.resize_mask_timeout_ms: longest the mask waits for the app
// to redraw at the new size before live frames go out regardless.
static int64_t resizeMaskTimeoutMs() {
    static const int64_t sTimeoutMs =
            property_get_int64("persist.fydroid.resize_mask_timeout_ms", 2000);
    return sTimeoutMs;
}

// Nothing composes by itself once the timeout passes if the app never
// redraws; wake SF then so presentComposedBuffer sees the deadline.
static void armResizeMaskTimeout() {
    std::thread([]() {
        usleep(static_cast<useconds_t>(resizeMaskTimeoutMs() * 1000 + 20000));
        if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
            hwc->scheduleRefresh();
        }
    }).detach();
}

void WaylandLayerContainer::onDragStarted(WaylandWindow* /*window*/,
        uint32_t direction) {
    // direction 0 = move only (no resize). 1+ = drag-resize from an
    // edge. Only flag resize so we don't accidentally skip move-driven
    // bounds updates (those don't change w/h anyway, but be precise).
    if (direction != 0) {
        mDragResizing = true;
        ALOGI("container taskId=%d: drag-resize started (dir=%u)",
              mTaskId, direction);

        if (!resizeMaskEligible(/*transition=*/false)) return;
        bool request = false;
        {
            std::lock_guard<std::mutex> lock(mMaskMutex);
            if (mMask == ResizeMask::kRequested && mMaskTransition) {
                // Grabbed mid-transition: the pending mask serves the drag.
                mMaskTransition = false;
                mMaskContentReady = false;
            } else if (mMask == ResizeMask::kOff) {
                mMask = ResizeMask::kRequested;
                mMaskStartNs = systemTime(SYSTEM_TIME_MONOTONIC);
                mMaskDroppedFrames = 0;
                request = true;
            } else if (mMask == ResizeMask::kSettling) {
                // Grabbed again before the last resize settled: the mask is
                // still on screen, so keep dragging it.
                mMask = ResizeMask::kShown;
                mMaskTransition = false;
                mMaskContentReady = false;
            }
        }
        auto* hwc = Hwc2::WaylandHwc2Composer::getInstance();
        if (!hwc) return;
        if (request) {
            hwc->requestTaskResizeMask(mTaskId);
        } else {
            hwc->awaitTaskResizeContent(mTaskId, 0, 0);
        }
    }
}

void WaylandLayerContainer::beginResizeTransition() {
    if (!resizeMaskEligible(/*transition=*/true)) return;
    // mContainerFrame is the task size this geometry resized to (after any
    // AR-step adjustment for a snapped window).
    const int32_t awaitW = mContainerFrame.right;
    const int32_t awaitH = mContainerFrame.bottom;
    if (awaitW <= 0 || awaitH <= 0) return;
    bool request = false;
    {
        std::lock_guard<std::mutex> lock(mMaskMutex);
        const nsecs_t now = systemTime(SYSTEM_TIME_MONOTONIC);
        if (mMask == ResizeMask::kOff) {
            mMask = ResizeMask::kRequested;
            mMaskTransition = true;
            mMaskStartNs = now;
            mMaskDroppedFrames = 0;
            request = true;
        } else if (mMask == ResizeMask::kShown ||
                   (mMask == ResizeMask::kRequested && !mMaskTransition)) {
            return;  // a drag owns the mask
        }
        // New, or another size change while one is settling (the split
        // divider, a maximize during a restore): wait for the latest size.
        mMaskContentReady = false;
        mMaskReleaseNs = now;
        mMaskDeadlineNs = now + ms2ns(resizeMaskTimeoutMs());
    }
    armResizeMaskTimeout();
    ALOGI("container taskId=%d: resize transition — %s, awaiting task %dx%d",
          mTaskId, request ? "mask requested" : "mask kept", awaitW, awaitH);
    if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
        if (request) hwc->requestTaskResizeMask(mTaskId);
        hwc->awaitTaskResizeContent(mTaskId, awaitW, awaitH);
    }
}

bool WaylandLayerContainer::resizeMaskEligible(bool transition) const {
    // persist.fydroid.resize_mask=0 restores the old live-resize behaviour.
    static const bool sEnabled =
            property_get_bool("persist.fydroid.resize_mask", true);
    if (!sEnabled) return false;
    if (mTaskId <= 0 || mIsDialog || !mWindow || !mWindow->isVisible()) {
        return false;
    }
    if (mWindowCategory != WaylandWindowCategory::APP) return false;
    // Efficiency-scaled games size their target in a different space, and a
    // PIP window is resized by ash's PIP resizer, not by the edge drag.
    if (mGameEfficiency || mPip.isPip()) return false;
    if (transition) {
        // Only where the task follows the window: a FULLSCREEN task (kiosk,
        // unresizable) takes its size from the display, so its content never
        // arrives at the new size and the mask would sit out the timeout.
        if (mKioskPinned || mUnresizable ||
            mCurrentWindowingMode == WaylandTaskResizer::WINDOWING_MODE_FULLSCREEN) {
            return false;
        }
    } else if (mAppliedLayoutMode == ChromeLayoutMode::kTablet) {
        return false;  // no edge drag in tablet mode
    }
    auto* globals = mClient ? mClient->globals() : nullptr;
    return globals && globals->viewporter();
}

bool WaylandLayerContainer::presentResizeMask(
        WaylandLayerDrmBufferManager* bufMgr, const sp<GraphicBuffer>& mask,
        int32_t maskW, int32_t maskH, int acquireFence) {
    if (acquireFence >= 0) {
        sync_wait(acquireFence, 1000);
        close(acquireFence);
    }
    std::lock_guard<std::mutex> lock(mMaskMutex);
    if (mMask != ResizeMask::kRequested) return false;  // drag already over
    if (mMaskTransition && mMaskContentReady) {
        // The resized content beat the mask; the next live frame is right.
        mMask = ResizeMask::kOff;
        mMaskTransition = false;
        mMaskContentReady = false;
        return false;
    }
    auto* globals = mClient ? mClient->globals() : nullptr;
    wp_viewporter* viewporter = globals ? globals->viewporter() : nullptr;
    wl_buffer* wlBuf = (bufMgr && mask) ? bufMgr->getOrCreateBuffer(mask) : nullptr;
    const int32_t destW = mGeometryTargetW;
    const int32_t destH = mGeometryTargetH;
    if (!mWindow || !viewporter || !wlBuf || maskW <= 0 || maskH <= 0 ||
        destW <= 0 || destH <= 0) {
        ALOGW("container taskId=%d: resize mask not shown (window=%d "
              "viewporter=%d buf=%d mask=%dx%d dest=%dx%d)", mTaskId,
              mWindow != nullptr, viewporter != nullptr, wlBuf != nullptr,
              maskW, maskH, destW, destH);
        mMask = ResizeMask::kOff;
        mMaskTransition = false;
        return false;
    }

    wl_surface* surface = mWindow->surface();
    if (!mMaskViewport) {
        mMaskViewport = wp_viewporter_get_viewport(viewporter, surface);
    }
    wp_viewport_set_source(mMaskViewport, wl_fixed_from_int(0),
                           wl_fixed_from_int(0), wl_fixed_from_int(maskW),
                           wl_fixed_from_int(maskH));
    wp_viewport_set_destination(mMaskViewport, destW, destH);
    mMaskDestW = destW;
    mMaskDestH = destH;
    wl_surface_attach(surface, wlBuf, 0, 0);
    if (wl_proxy_get_version(reinterpret_cast<wl_proxy*>(surface)) >=
            WL_SURFACE_DAMAGE_BUFFER_SINCE_VERSION) {
        wl_surface_damage_buffer(surface, 0, 0, maskW, maskH);
    } else {
        wl_surface_damage(surface, 0, 0, destW, destH);
    }
    // No flush: the physical display's presentDisplay flushes every frame.
    wl_surface_commit(surface);
    mMaskBuffer = mask;
    mMask = mMaskTransition ? ResizeMask::kSettling : ResizeMask::kShown;
    ALOGI("container taskId=%d: resize mask shown %dx%d -> %dx%d (%.1f ms "
          "after %s)", mTaskId, maskW, maskH, destW, destH,
          ns2us(systemTime(SYSTEM_TIME_MONOTONIC) - mMaskStartNs) / 1000.0f,
          mMaskTransition ? "size change" : "drag start");
    return true;
}

void WaylandLayerContainer::onResizeContentReady() {
    std::lock_guard<std::mutex> lock(mMaskMutex);
    const bool pendingTransition =
            mMask == ResizeMask::kRequested && mMaskTransition;
    if ((mMask != ResizeMask::kSettling && !pendingTransition) ||
        mMaskContentReady) {
        return;
    }
    mMaskContentReady = true;
    ALOGI("container taskId=%d: resized content ready %.1f ms after release",
          mTaskId,
          ns2us(systemTime(SYSTEM_TIME_MONOTONIC) - mMaskReleaseNs) / 1000.0f);
}

void WaylandLayerContainer::setResizeMaskDestinationLocked(int32_t w, int32_t h) {
    if (!mMaskViewport || !mWindow || w <= 0 || h <= 0) return;
    if (w == mMaskDestW && h == mMaskDestH) return;
    mMaskDestW = w;
    mMaskDestH = h;
    wp_viewport_set_destination(mMaskViewport, w, h);
    // Pending bounds from the echo that got us here ride on the same commit.
    wl_surface_commit(mWindow->surface());
}

void WaylandLayerContainer::dropResizeMaskLocked(const char* why) {
    if (mMaskViewport) {
        wp_viewport_destroy(mMaskViewport);
        mMaskViewport = nullptr;
    }
    const nsecs_t now = systemTime(SYSTEM_TIME_MONOTONIC);
    ALOGI("container taskId=%d: resize mask removed (%s) — %.1f ms after "
          "release, %.1f ms total, %u frames held back", mTaskId, why,
          mMaskReleaseNs ? ns2us(now - mMaskReleaseNs) / 1000.0f : 0.0f,
          ns2us(now - mMaskStartNs) / 1000.0f, mMaskDroppedFrames);
    const bool awaited = (mMask == ResizeMask::kSettling && !mMaskContentReady);
    mMask = ResizeMask::kOff;
    mMaskTransition = false;
    mMaskContentReady = false;
    mMaskReleaseNs = 0;
    mMaskDestW = mMaskDestH = 0;
    // SF is still watching for the resized content; it no longer matters.
    if (awaited) {
        if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
            hwc->awaitTaskResizeContent(mTaskId, 0, 0);
        }
    }
}

void WaylandLayerContainer::onDragFinished(WaylandWindow* window,
        int32_t x, int32_t y, bool cancelled) {
    // A move (not resize) drag of a snapped window: ash follows it with NORMAL
    // if the window left the split; see onStateChanged.
    mSnapDragEnded = mSnapped && !mDragResizing && !cancelled;
    mSnapDragEndX = x;
    bool maskUp = false;
    {
        std::lock_guard<std::mutex> lock(mMaskMutex);
        if (mMask == ResizeMask::kRequested) {
            mMask = ResizeMask::kOff;  // SF's late answer is refused
        } else if (mMask == ResizeMask::kShown) {
            maskUp = true;
            mMask = ResizeMask::kSettling;
            mMaskContentReady = false;
            mMaskReleaseNs = systemTime(SYSTEM_TIME_MONOTONIC);
            mMaskDeadlineNs = mMaskReleaseNs + ms2ns(resizeMaskTimeoutMs());
            armResizeMaskTimeout();
        }
    }
    auto* hwc = Hwc2::WaylandHwc2Composer::getInstance();
    if (maskUp && hwc && mGeometryTargetW > 0 && mGeometryTargetH > 0) {
        // Unfreeze the render target at the window's final size so the first
        // live frame is composed for the window it will be shown in.
        hwc->setTaskRenderTargetSize(mTaskId, mGeometryTargetW, mGeometryTargetH);
    }

    const int32_t awaitW = finishDragResize(window, x, y, cancelled);
    if (!maskUp || !hwc) return;
    if (awaitW > 0) {
        // mContainerFrame now holds the task size the drag settled on.
        hwc->awaitTaskResizeContent(mTaskId, mContainerFrame.right,
                                    mContainerFrame.bottom);
    } else {
        // The task was not resized (cancelled, or nothing to wait for): what
        // SF composes next is already right.
        onResizeContentReady();
        hwc->scheduleRefresh();
    }
}

int32_t WaylandLayerContainer::finishDragResize(WaylandWindow* /*window*/,
        int32_t /*x*/, int32_t /*y*/, bool cancelled) {
    const bool wasDragResizing = mDragResizing;
    mDragResizing = false;
    if (cancelled || !wasDragResizing) return 0;
    if (mTaskId <= 0) return 0;
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP) return 0;
    // Tablet mode is bounds-controlled by Chrome itself — never AR-step.
    if (mAppliedLayoutMode == ChromeLayoutMode::kTablet) return 0;
    // No final geometry to snap from — Chrome hasn't told us where the
    // user released yet.
    if (mServerBoundsW <= 0 || mServerBoundsH <= 0) return 0;

    // Dragged bounds are in DIP. Convert to output (= "Android task")
    // pixels so the Java side talks in the same coordinate space ATMS
    // expects.
    float dsf = (mScaleFactor > 0.0f) ? mScaleFactor : 1.0f;
    int32_t px = static_cast<int32_t>(mServerBoundsX * dsf);
    int32_t py = static_cast<int32_t>(mServerBoundsY * dsf);
    int32_t pw = static_cast<int32_t>(mServerBoundsW * dsf);
    int32_t ph = static_cast<int32_t>(mServerBoundsH * dsf);

    // Use the actual display the container is currently on. -1 from
    // displayIndexForOutput would mean an unknown output (e.g., the
    // container is mid-migration); clamp to primary in that case.
    int32_t displayId = mClient ? mClient->displayIndexForOutput(mOutput) : -1;
    if (displayId < 0) displayId = 0;

    ALOGI("container taskId=%d: drag finished, asking Java AR-step "
          "for bounds=(%d,%d %dx%d) displayId=%d",
          mTaskId, px, py, pw, ph, displayId);

    int32_t aX = px, aY = py, aW = pw, aH = ph;
    bool ok = WaylandShellHandlerProxy::instance()->resizeTaskWithArStep(
            mTaskId, px, py, pw, ph, displayId, &aX, &aY, &aW, &aH);

    // Java decides whether the activity is resizable. When it isn't, the
    // applied rect is snapped to the closest supported aspect ratio and
    // differs from what the user dragged to. In that case Chrome's window
    // is still at the dragged size — push the snapped rect back through
    // set_bounds_in_output so Chrome resizes its frame to match the new
    // Android task bounds. Without this the input region (driven by the
    // Android task) and Chrome's visible window stay out of sync.
    if (ok && (aX != px || aY != py || aW != pw || aH != ph) && mWindow
            && mWindowCategory == WaylandWindowCategory::APP && mOutput) {
        ALOGI("container taskId=%d: pushing snapped bounds to Chrome "
              "(%d,%d %dx%d)", mTaskId, aX, aY, aW, aH);
        auto* appWindow = static_cast<WaylandAppWindow*>(mWindow.get());
        appWindow->setBoundsInOutput(mOutput, aX, aY, aW, aH);
        wl_surface_commit(appWindow->surface());
    }

    // Resync everything to the size the drag actually settled on.
    //
    // resizeTask() is deferred for the whole drag (see onGeometryChanged), so
    // when it ends the Android task jumps to the AR-stepped size in one go
    // while mLastResizedBounds / mContainerFrame still hold the last dragged
    // size and the render target holds whatever the final drag frame set. Those
    // agree only when Java applied the request verbatim; an unresizable
    // activity gets a snapped rect instead and every one of them is then stale
    // — the window and its content stay different sizes until something else
    // happens to resize the task.
    if (!ok || mTaskId <= 0) return 0;
    const float taskScale = (mAndroidDsf > 0.0f) ? mAndroidDsf : dsf;
    // Java applied aW x aH in output pixels; derive the primary-DSF task size
    // by ratio, rounded — a floor on each side loses up to 3 px at DSF 2.4
    // and the content latch tolerates 2.
    const float ratio = (dsf > 0.0f) ? (taskScale / dsf) : 1.0f;
    const int32_t physW = static_cast<int32_t>(std::lround(aW * ratio));
    const int32_t physH = static_cast<int32_t>(std::lround(aH * ratio));
    if (physW <= 0 || physH <= 0) return 0;

    mLastResizedBounds = {0, 0, physW, physH};
    mContainerFrame = {0, 0, physW, physH};
    if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
        hwc->setTaskRenderTargetSize(mTaskId, aW, aH);
        hwc->scheduleRefresh();
    }
    ALOGI("container taskId=%d: drag resync — task %dx%d, render target %dx%d",
          mTaskId, physW, physH, aW, aH);
    return physW;
}

bool WaylandLayerContainer::applyBoundsWithArStep(int32_t physW, int32_t physH) {
    if (mTaskId <= 0 || physW <= 0 || physH <= 0) return false;
    if (mIsDialog || mWindowCategory != WaylandWindowCategory::APP) return false;

    int32_t displayId = mClient ? mClient->displayIndexForOutput(mOutput) : -1;
    if (displayId < 0) displayId = 0;

    int32_t aX = 0, aY = 0, aW = physW, aH = physH;
    bool ok = WaylandShellHandlerProxy::instance()->resizeTaskWithArStep(
            mTaskId, 0, 0, physW, physH, displayId, &aX, &aY, &aW, &aH);
    if (!ok) {
        // Java unavailable or the request was rejected — fall back to the
        // plain resize so the task at least follows the new bounds.
        ALOGI("container taskId=%d: AR-step unavailable, plain resizeTask %dx%d",
              mTaskId, physW, physH);
        mTaskResizer.resizeTask(mTaskId, 0, 0, physW, physH);
        return false;
    }

    // An unresizable activity comes back at an aspect-ratio-stepped size
    // rather than the rect we asked for. Adopt what Java actually applied so
    // the task frame, the input region and the render target agree with the
    // relaunched activity instead of the size we requested.
    if (aW > 0 && aH > 0 && (aW != physW || aH != physH)) {
        ALOGI("container taskId=%d: AR-step applied %dx%d (requested %dx%d)",
              mTaskId, aW, aH, physW, physH);
        mLastResizedBounds = {0, 0, aW, aH};
        mContainerFrame = {0, 0, aW, aH};
        if (auto* hwc = Hwc2::WaylandHwc2Composer::getInstance()) {
            hwc->setTaskRenderTargetSize(mTaskId, aW, aH);
            hwc->scheduleRefresh();
        }
    }
    return true;
}

} // namespace android

#pragma clang diagnostic pop

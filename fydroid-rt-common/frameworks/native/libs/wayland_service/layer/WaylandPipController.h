/*
 * Copyright (C) 2026 The FydeOS Authors
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

struct wl_output;

namespace android {

class WaylandAppWindow;

// Picture-in-Picture policy for one task's window.
//
// Deliberately NOT a WaylandWindow subclass. The other window classes
// (WaylandToastWindow, WaylandModalWindow, ...) each exist because they wrap a
// genuinely different exo surface — zcr_remote_shell_v2_get_toast_surface,
// get_input_method_surface and so on. PIP is not one of those: there is no
// "get_pip_surface", and zcr_remote_surface_v2.pip is a request ON the app's
// existing remote surface, exactly like maximize, set_snapped_to_left or
// set_float. PIP is a window STATE, not a surface type.
//
// It is also a state a live window passes through — normal, PIP, expanded back
// — while the same task, task VD, buffers and container carry on. Rebuilding
// the surface for it would drop app_id, title, frame colours, session id,
// resize limits and the secure flag, make ash treat it as a brand-new widget
// (breaking the expand animation and the return-to-origin behaviour), churn the
// render target, and break set_pip_original_window, which exists precisely
// because the original window survives as its own surface.
//
// So this is a plain policy object the container owns: it holds the PIP state
// machine and the size bookkeeping, and drives the window through the same
// WaylandAppWindow requests everything else uses.
//
// Threading: every method must run on the Wayland event thread. Android's PIP
// state arrives on a binder thread and is handed over by
// WaylandGlobals::processPendingPipState.
class WaylandPipController {
public:
    // `window` is the container's app window; may be null when Android reports
    // PIP before the surface exists (an app can call
    // enterPictureInPictureMode() from onCreate). The state is latched either
    // way and replayed by the container from ensureWindow().
    explicit WaylandPipController(int32_t taskId) : mTaskId(taskId) {}

    // Android put this task into, or took it out of, WINDOWING_MODE_PINNED.
    // Returns true if the state actually changed.
    bool setPip(bool inPip);

    // Android's view: is this task in PIP? Distinct from what ash thinks (the
    // container's mCurrentState == PIP), which lags by the round trip — window
    // policy must be asserted off this one, or a window ends up half in PIP.
    bool isPip() const { return mPipRequested; }

    // WM's pinned rectangle, in display pixels. This is where the PIP window's
    // SIZE comes from: ash's PipPositioner only chooses WHERE a PIP window
    // sits and keeps whatever size the client last reported.
    void setBounds(int32_t wPx, int32_t hPx) { mPipW = wPx; mPipH = hPx; }

    // Send the current state to ash.
    //
    // `stateChanged` distinguishes entering/leaving PIP (send the pip request,
    // flip input suppression) from a size-only update, which is the normal way
    // the real rectangle arrives — it lands in a second message once WM has
    // resolved the pinned configuration.
    //
    // `dsf` converts WM's pixels to the DIP that set_bounds_in_output wants.
    // `curDipW/H` are the window's current geometry; a size equal to them is
    // dropped, because that is the stale pre-PIP rectangle the bounds listener
    // reports when it first registers.
    //
    // `preDipW/H` is the size to come back to on exit, taken from the Android
    // task frame rather than the window geometry: entering PIP from onCreate
    // catches the window mid-launch, and restoring to that leaves it too small
    // (measured 371x420 where the task was 371x659).
    //
    // Returns true if a size was pushed, so the caller can commit.
    // `outPxW/H` are the output size in pixels: a rectangle that large is not
    // a PIP rectangle and is rejected — see the comment at the test.
    bool push(WaylandAppWindow* window, wl_output* output, bool stateChanged,
              float dsf, int32_t originX, int32_t originY,
              int32_t curDipW, int32_t curDipH,
              int32_t outPxW, int32_t outPxH,
              int32_t preDipW, int32_t preDipH);

    // The window's on-screen origin while it is NOT in PIP — the position to
    // restore to. Fed from every non-PIP bounds event rather than sampled when
    // PIP starts: ash can move the window before the state push reaches us
    // (measured 116ms between the pre-PIP rect and ash's PIP rect, with the
    // push landing after both), so sampling raced and restored the window to
    // the PIP position, clamped on-screen.
    void setRestoreOrigin(int32_t x, int32_t y) {
        if (mPipRequested) return;  // never learn a PIP-era position
        mPreOriginX = x;
        mPreOriginY = y;
        mHavePreOrigin = true;
        mSawPipBounds = false;
    }

    // The first bounds event of a PIP session still carries the window's
    // PRE-PIP rectangle — ash has not moved it yet; the move arrives ~116ms
    // later as a second event. So this one, and only this one, is the true
    // "where it was" and beats anything recorded earlier (window placement is
    // only the centred creation position, which is wrong the moment the user
    // or ash has moved the window since).
    void notePipBounds(int32_t x, int32_t y) {
        if (mSawPipBounds) return;
        mSawPipBounds = true;
        mPreOriginX = x;
        mPreOriginY = y;
        mHavePreOrigin = true;
    }

    // Forget the last size handed to ash. Called when leaving PIP so a later
    // re-entry at the same size is not deduped away.
    void reset() { mSentDipW = mSentDipH = 0; }

    // Re-advertise ash's PIP size limits for a new output size.
    //
    // The limits are derived from the output, so they are only right for the
    // orientation they were computed in, and nothing re-sends them: rotating a
    // PIP window portrait->landscape left ash holding the PORTRAIT maximum
    // (half of 1600x2560 = 800x1280) while the Shell re-derived a landscape
    // 900x506 rectangle. Ash clamped the width to its stale 800, that clamp
    // came back as a bounds change, WM resized the task to 800x506, and the
    // window sat at the wrong aspect with the menu faithfully matching the
    // task instead of the video. The compositor's own half-output cap did NOT
    // misfire here — mOutputWidth/mOutputHeight are current by this point;
    // only ash's copy was stale.
    //
    // A no-op unless the window is in PIP, already constrained, and the output
    // really changed shape. Returns true if a commit is needed.
    bool refreshSizeConstraints(WaylandAppWindow* window,
                                int32_t outPxW, int32_t outPxH);

    // Whether ash currently has PIP size constraints for this window — i.e.
    // whether it will build a WindowResizer and therefore honour start_move.
    // The pointer handler checks this before offering the window a drag: with
    // it false, start_move is dropped silently and the press is better left to
    // Android.
    bool constrained() const { return mConstrained; }

private:
    const int32_t mTaskId;
    bool mPipRequested = false;
    // WM's rectangle in display pixels, 0 until it arrives.
    int32_t mPipW = 0, mPipH = 0;
    // Last size handed to ash, in PIXELS (set_bounds_in_output's space).
    int32_t mSentDipW = 0, mSentDipH = 0;
    // The window as it was before PIP, restored on the way out. Handing ash a
    // PIP size makes that size the window's bounds, so restore() alone brings
    // the window back as a normal window still the size of the PIP one — and
    // the caption has to come back too, since PIP runs frameless.
    int32_t mPreDipW = 0, mPreDipH = 0;  // pixels
    // Where the window sat before PIP, in the same pixel space
    // (WaylandLayerContainer::mWindowOffsetX/Y). Restoring the size alone put
    // the window back at the PIP origin — hard against a screen edge, because
    // that is where ash parks a PIP window.
    int32_t mPreOriginX = 0, mPreOriginY = 0;
    bool mHavePreOrigin = false;
    bool mSawPipBounds = false;
    // Captured independently of the size: the size may arrive later (or
    // only on the way out), but the frame must be sampled BEFORE PIP
    // makes the window frameless or there is nothing to put back.
    uint32_t mPreFrameType = 0;
    bool mHavePreFrame = false;
    bool mHavePre = false;
    // True while ash holds PIP min/max for this window and can_maximize is
    // unset. Tracked so leaving PIP puts can_maximize back exactly once.
    bool mConstrained = false;
    // Output size the limits ash holds were computed from, so a rotation can
    // be told apart from the work-area and DSF changes that come through the
    // same path and need no new limits.
    int32_t mConstraintOutW = 0, mConstraintOutH = 0;
};

} // namespace android

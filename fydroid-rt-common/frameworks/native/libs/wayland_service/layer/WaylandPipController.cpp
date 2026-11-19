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

#define LOG_TAG "WaylandPipController"

#include "WaylandPipController.h"

#include <algorithm>

#include <log/log.h>
#include <remote-shell-unstable-v2-client-protocol.h>
#include <wayland-client.h>

#include "../window/WaylandAppWindow.h"

namespace android {

bool WaylandPipController::setPip(bool inPip) {
    if (mPipRequested == inPip) return false;
    mPipRequested = inPip;
    ALOGI("container taskId=%d: android pip %s", mTaskId, inPip ? "ENTER" : "EXIT");
    return true;
}

bool WaylandPipController::push(WaylandAppWindow* window, wl_output* output,
                                bool stateChanged, float dsf,
                                int32_t originX, int32_t originY,
                                int32_t curDipW, int32_t curDipH,
                                int32_t outPxW, int32_t outPxH,
                                int32_t preDipW, int32_t preDipH) {
    if (!window) return false;

    if (!mPipRequested) {
        if (stateChanged) {
            // No "unpip" request exists — restore is how a window leaves PIP,
            // the same as leaving maximized.
            reset();
            window->setInputSuppressed(false);
            // Give back can_maximize before anything else: while it is unset
            // the window is not maximizable and its caption loses the
            // maximize/restore button. The container re-asserts the window's
            // real min/max straight after this push returns.
            if (mConstrained) {
                window->clearPipSizeConstraints();
                mConstrained = false;
                mConstraintOutW = mConstraintOutH = 0;
            }
            // Back into the normal z-order — an expanded window that stayed
            // always-on-top would sit over everything else forever.
            window->setAlwaysOnTop(false);
            if (preDipW > 0 && preDipH > 0) {
                mPreDipW = preDipW;
                mPreDipH = preDipH;
                mHavePre = true;
            }
            if (mHavePre) {
                // Put the caption back before restoring, and re-assert the
                // pre-PIP size. restore() on its own is not enough: the PIP
                // size we handed ash IS the window's bounds now, so the window
                // came back as a normal window still the size of the PIP one.
                window->setFrameType(mHavePreFrame
                                             ? mPreFrameType
                                             : ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NORMAL);
                mHavePreFrame = false;
                window->restore();
                if (mPreDipW > 0 && mPreDipH > 0) {
                    // originX/Y is where the PIP window sat — typically hard
                    // against a screen edge, because that is where ash parks a
                    // PIP window. Restoring a full-size window at that origin
                    // pushes most of it off the display, so clamp it back on.
                    // TODO: clamp to the work area rather than the output, so a
                    // restored window does not end up flush under the shelf.
                    // Back to where it was, not to where the PIP window sat.
                    int32_t ox = mHavePreOrigin ? mPreOriginX : originX;
                    int32_t oy = mHavePreOrigin ? mPreOriginY : originY;
                    if (outPxW > 0 && ox + mPreDipW > outPxW) {
                        ox = outPxW - mPreDipW;
                    }
                    if (outPxH > 0 && oy + mPreDipH > outPxH) {
                        oy = outPxH - mPreDipH;
                    }
                    if (ox < 0) ox = 0;
                    if (oy < 0) oy = 0;
                    ALOGI("container taskId=%d: pip exit, restoring %dx%d px at (%d,%d)",
                          mTaskId, mPreDipW, mPreDipH, ox, oy);
                    window->setBoundsInOutput(output, ox, oy, mPreDipW, mPreDipH);
                }
                mHavePre = false;
                mHavePreOrigin = false;
            } else {
                window->restore();
            }
            return true;
        }
        return false;
    }

    if (stateChanged) {
        // Remember what to come back to. Captured here rather than on the way
        // out, because by then the window is already the PIP size.
        if (!mHavePre && preDipW > 0 && preDipH > 0) {
            mPreDipW = preDipW;
            mPreDipH = preDipH;
            mHavePre = true;
        }
        // NOTE: the origin is NOT sampled here. It used to be, on the theory
        // that ash had not moved the window yet — but it has: two reason-7
        // bounds events arrive ~116ms apart (the pre-PIP rect, then ash's PIP
        // rect) and this push lands after the second, so the sample took the
        // PIP position and the window restored there, clamped on-screen.
        // setRestoreOrigin() feeds it from every non-PIP bounds event instead,
        // which has no ordering dependency: the last position the window held
        // as a non-PIP window is by definition where it goes back to.
        // Sample the frame unconditionally, and before setFrameType below.
        // Gating it on the size meant it was never captured (the size arrives
        // on the way out), so the restore put back frame type 0 — NONE — and
        // the window came back with no caption at all.
        if (!mHavePreFrame) {
            mPreFrameType = window->frameType();
            mHavePreFrame = true;
        }
        // Android keeps this window's input. The rule used to be the reverse —
        // suppress it, so ash could draw and hit-test the PIP affordances — but
        // ash draws no controls for a FRAME_TYPE_NONE window. The controls are
        // Android's: the WM Shell's own PipMenuView, composited into this task
        // by ArcSystemUI's ArcPipShellMenu, revealed by tapping the window —
        // which cannot happen if the surface declines every pointer event.
        window->setInputSuppressed(false);
        // NONE: a ChromeOS PIP window is frameless. What draws the controls
        // instead is the WM Shell's stock PipMenuView: ArcSystemUI's
        // ArcPipShellMenu stamps that layer with this task's TaskId so
        // SurfaceFlinger composites it into the task's virtual display, so the
        // menu ends up inside the PIP window's own buffer, over the content,
        // and follows the window wherever ash puts it. That is why the interim
        // caption (OVERLAP, with maximize_restore and close) is gone.
        window->setFrameType(ZCR_REMOTE_SURFACE_V2_FRAME_TYPE_NONE);
        window->setPip();
        // The pip request alone does not lift the window: ash leaves it in the
        // normal z-order and the next window focused covers it, which defeats
        // the point of a PIP. ARC sends always-on-top as its own signal
        // (ArcAlwaysOnTopMetadata) on entering PIP; so do we.
        window->setAlwaysOnTop(true);
        // Without this ash will not drag, tuck or fling this window — see
        // WaylandAppWindow::setPipSizeConstraints. Sent on the way IN because
        // the maximum must never be advertised on an ordinary window (it would
        // make it non-maximizable), and cleared again on the way out.
        //
        // Half the output for the maximum and a sixth for the minimum mirrors
        // ArcPipBoundsAlgorithm, which ARCVM uses for the same purpose
        // (max = half the inset display bounds per axis; min ≈ a fifth of the
        // display width). Both bracket the rectangle WM actually chooses, so
        // ash has room to resize without snapping the window to a limit the
        // moment a drag starts.
    // Minimum: the SHORTER output edge / 6, on BOTH axes.
    //
    // Not outPxW/6 x outPxH/6. That minimum is orientation-dependent, and ash
    // enforces it: measured on device, a 900x378 PIP came back from ash as
    // 900x426 in landscape and 900x378 in portrait, flipping on every rotation
    // — and 426 is exactly 2560/6, the landscape minimum WIDTH, applied to the
    // window's height. The window kept being pushed to a size nobody asked for,
    // the task and menu stayed at 378, and the 48px difference showed as the
    // menu not covering the bottom of the window.
    //
    // A square minimum off the shorter edge is the same number in both
    // orientations, so a rotation cannot change what ash enforces. It still
    // satisfies the only thing the minimum is FOR — PipController::CanResizePip
    // wants a non-empty maximum strictly greater than the minimum, or ash
    // builds no WindowResizer and start_move is silently dropped.
        if (outPxW > 0 && outPxH > 0) {
            const int32_t minEdge = std::min(outPxW, outPxH) / 6;
            mConstrained = window->setPipSizeConstraints(minEdge, minEdge,
                                                         outPxW / 2, outPxH / 2);
            if (mConstrained) {
                mConstraintOutW = outPxW;
                mConstraintOutH = outPxH;
            }
        }
    }

    if (mPipW <= 0 || mPipH <= 0) return false;

    // A PIP window is by definition smaller than the screen, so a rectangle as
    // large as the output is not a PIP rectangle — it is the task on its way
    // OUT of PIP.
    //
    // Leaving PIP, WM restores the task's bounds while the task is still
    // WINDOWING_MODE_PINNED, so the bounds listener reports the restored
    // rectangle before the unpin does. Measured on device: "pip size ->
    // 1280x800 dip (2560x1600 px)" 411ms ahead of the EXIT, which blew the
    // window up to fullscreen while the task settled back to 742x1318 — a
    // full-screen window with a narrow strip of content and blank space beside
    // it. Ash would never make a PIP window fullscreen either, so this is a
    // real invariant rather than a heuristic.
    if (outPxW > 0 && outPxH > 0 && (mPipW >= outPxW || mPipH >= outPxH)) {
        ALOGI("container taskId=%d: ignoring %dx%d px as a pip size "
              "(output is %dx%d) — task is leaving pip",
              mTaskId, mPipW, mPipH, outPxW, outPxH);
        return false;
    }

    // set_bounds_in_output takes PIXELS, not DIP — "Bounds are set in pixels,
    // and should not be scaled" (zcr_remote_shell_impl.cc; see OutputRect in
    // WaylandLayerContainer.h). WM's rectangle is already in display pixels, so
    // it goes straight out. Dividing by the DSF first made every PIP window
    // half-size, which then dragged the task and the render target down with
    // it — the "scale is wrong" symptom.
    const float scale = (dsf > 0.0f) ? dsf : 1.0f;
    int32_t pxW = mPipW;
    int32_t pxH = mPipH;
    if (pxW <= 0 || pxH <= 0) return false;

    // Cap at half the output on each axis, preserving the aspect ratio.
    //
    // A PIP window is small by definition — ArcPipBoundsAlgorithm, which ARCVM
    // uses, takes "half the inset display bounds" as its MAXIMUM. A rectangle
    // bigger than that is not a PIP rectangle at all; it is the launch size of a
    // task that never had one computed.
    //
    // That happens for real, on the multi-activity path: an app can launch a new
    // activity into a new task that is ALREADY pinned (VLC does — "android
    // pip=1" arrives before onTaskCreated), and because the task is born pinned
    // WM never runs its PIP bounds computation over it and simply reports the
    // launch bounds. Measured on device: VLC's PIP window came through as
    // 1558x946 — its own pre-PIP window size — so the "PIP" was near
    // full-window-sized and looked exactly like the app had never left its
    // window. The single-activity path is unaffected: WM computes a real
    // rectangle there (900x506 for 16:9).
    //
    // The >= output test below does not catch this: 1558x946 is comfortably
    // smaller than a 2560x1600 output. Scale rather than crop, so an aspect
    // ratio the app asked for survives.
    //
    // ENTRY ONLY. This answers "the task never had a PIP rectangle computed",
    // which is a question about the FIRST size of a session; every size after
    // it comes from WM and the Shell, which have both already applied the ARC
    // maximum (ArcPipShellMenu.clampToPipMaximum). Left running on every push
    // it became a second opinion on a settled window: on rotating to portrait
    // the half-output maximum drops to 800, so a perfectly good 900x506 window
    // was "capped" to 800x449 and the PIP shrank on every rotation with no
    // owner having asked for it. A PIP window keeps its size across a rotation.
    if (outPxW > 0 && outPxH > 0 && mSentDipW == 0 && mSentDipH == 0) {
        const int32_t maxW = outPxW / 2;
        const int32_t maxH = outPxH / 2;
        if (pxW > maxW || pxH > maxH) {
            const float fit = std::min(static_cast<float>(maxW) / pxW,
                                       static_cast<float>(maxH) / pxH);
            const int32_t fitW = static_cast<int32_t>(pxW * fit);
            const int32_t fitH = static_cast<int32_t>(pxH * fit);
            ALOGI("container taskId=%d: %dx%d is not a pip size (max %dx%d) — "
                  "scaling to %dx%d", mTaskId, pxW, pxH, maxW, maxH, fitW, fitH);
            if (fitW <= 0 || fitH <= 0) return false;
            pxW = fitW;
            pxH = fitH;
        }
    }
    // The stale-check values arrive as DIP (window geometry), so compare in px.
    const int32_t curPxW = static_cast<int32_t>(curDipW * scale);
    const int32_t curPxH = static_cast<int32_t>(curDipH * scale);

    // Registering the bounds listener dispatches the CURRENT configuration
    // straight away, and at that moment WM has not recomputed the pinned
    // rectangle — so the first size to arrive is the pre-PIP one, i.e. exactly
    // the size this window already is. Applying it is a no-op that still costs
    // a round trip and a frame at the wrong size. Dropped here rather than on
    // the Java side, where "stale" and "real" are indistinguishable.
    if (pxW == curPxW && pxH == curPxH) return false;

    // "We already sent this size" is only a reason to stay quiet if ash
    // actually HAS it.
    //
    // Ash keeps its own window size and re-derives it per orientation, so it
    // can drift away from the size Android holds — measured on device after a few
    // rotations: ash's window was 900x426 in landscape (and reported that on
    // every drag event) while the task, the menu view and the menu buffer were
    // all 900x378. WM pushes the correct 900x378 on the configuration change,
    // but this dedupe dropped it because 900x378 had been sent earlier, in
    // portrait. The two then sat 48px apart for the rest of the session: the
    // menu covers the TASK, so the bottom band of ash's window had no menu over
    // it, which is what "the menu size doesn't match the window" looked like.
    //
    // So dedupe on agreement, not on history: skip the send only when the size
    // is one we sent AND the window is that size now. curPx comes from ash's
    // last reported geometry, so this cannot loop — once ash applies the size
    // it reports it back and the test above drops the repeats.
    const bool ashHasIt = (curPxW == mSentDipW && curPxH == mSentDipH);
    if (pxW == mSentDipW && pxH == mSentDipH && ashHasIt) return false;
    if (pxW == mSentDipW && pxH == mSentDipH) {
        ALOGI("container taskId=%d: ash has %dx%d but the task is %dx%d — "
              "re-asserting", mTaskId, curPxW, curPxH, pxW, pxH);
    }

    ALOGI("container taskId=%d: pip size -> %dx%d px", mTaskId, pxW, pxH);
    mSentDipW = pxW;
    mSentDipH = pxH;
    // Origin is ash's to choose — PipPositioner re-places the window by its
    // snap fraction and collision rules the moment it is PIP — so send the
    // current one and let this be a pure resize.
    window->setBoundsInOutput(output, originX, originY, pxW, pxH);
    return true;
}

bool WaylandPipController::refreshSizeConstraints(WaylandAppWindow* window,
                                                  int32_t outPxW, int32_t outPxH) {
    if (!window || !mPipRequested || !mConstrained) return false;
    if (outPxW <= 0 || outPxH <= 0) return false;
    if (outPxW == mConstraintOutW && outPxH == mConstraintOutH) return false;

    // Never advertise a maximum smaller than the window already is: ash
    // enforces max_size immediately, so a rotation whose half-output maximum
    // falls below the current size would make ASH shrink the window — the same
    // "PIP gets smaller every rotation" outcome as the entry cap, arriving by a
    // different route. Measured on device: a 900x506 landscape window rotated
    // into portrait, where half the output is 800, and ash clipped it to 800
    // wide (aspect 1.58 instead of 1.78) with the task and menu faithfully
    // following. The limits exist so ash will BUILD a resizer, not to
    // re-litigate a size two other owners have already agreed on.
    const int32_t maxW = std::max(outPxW / 2, mPipW);
    const int32_t maxH = std::max(outPxH / 2, mPipH);
    ALOGI("container taskId=%d: output %dx%d -> %dx%d, re-arming pip limits "
          "(max %dx%d -> %dx%d, window %dx%d)",
          mTaskId, mConstraintOutW, mConstraintOutH, outPxW, outPxH,
          mConstraintOutW / 2, mConstraintOutH / 2, maxW, maxH, mPipW, mPipH);
    // Square minimum off the shorter edge — see the note in push().
    const int32_t minEdge = std::min(outPxW, outPxH) / 6;
    mConstrained = window->setPipSizeConstraints(minEdge, minEdge, maxW, maxH);
    if (mConstrained) {
        mConstraintOutW = outPxW;
        mConstraintOutH = outPxH;
    }
    return mConstrained;
}

} // namespace android

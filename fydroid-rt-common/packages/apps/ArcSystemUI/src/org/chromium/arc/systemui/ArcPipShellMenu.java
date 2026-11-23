/*
 * Copyright (C) 2026 Fyde Innovations
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

package org.chromium.arc.systemui;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.PictureInPictureParams;
import android.app.TaskStackListener;
import android.content.Context;
import android.content.ComponentName;
import android.content.res.Configuration;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import android.view.Display;
import android.view.SurfaceControl;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;

import com.android.server.wm.IArcWindowManager;
import com.android.systemui.CoreStartable;
import com.android.systemui.dagger.SysUISingleton;
import com.android.wm.shell.pip.PipBoundsAlgorithm;
import com.android.wm.shell.pip.PipBoundsState;
import com.android.wm.shell.pip.phone.PhonePipMenuController;
import com.android.wm.shell.pip.phone.PipTouchGesture;
import com.android.wm.shell.pip.phone.PipTouchHandler;
import com.android.wm.shell.pip.phone.PipTouchState;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Inject;

/**
 * Makes the WM Shell's own PIP menu usable on fydroid.
 *
 * The Shell is running — {@code ShellTaskOrganizer} is registered, {@code
 * PipTaskOrganizer} owns the pinned task and its leash, and {@code
 * PhonePipMenuController} has already attached a {@code PipMenuView} sized to
 * the PIP window. Two things stop it working, and this class fixes the first
 * of them.
 *
 * <p><b>The menu is drawn in the wrong place.</b> {@code PipMenuView} is a
 * shell-root window that {@code PhonePipMenuController} positions by
 * {@code setMatrix} onto {@code PipBoundsState.getBounds()} — and on fydroid
 * that rectangle is WM's original pinned rect (measured
 * {@code Rect(1632,1066-2532,1572)}) while the window is wherever <i>ash</i>
 * has since dragged it. Rather than chase ash's rectangle into the Shell, stamp
 * the menu's surface with the pinned task's {@code METADATA_TASK_ID}: fydroid's
 * compositor composites every layer sharing a TaskId into that task's virtual
 * display, so the menu lands inside the PIP window's own buffer and follows it
 * for free, with no bounds plumbing at all. This is the same trick the
 * now-deleted hand-built {@code ArcPipMenu} used for its pills — see
 * {@code docs/picture-in-picture-plan.md} §9.1 — applied to the stock menu
 * instead, which is why that 784-line reimplementation could go.
 *
 * <p>It fits because {@code PipMenuView} is already laid out at
 * {@code 0,0-900,506}: the task footprint's own size at its own origin, which
 * is exactly what a task-VD layer wants.
 *
 * <p><b>The Shell is a second writer for touch.</b> Its default
 * {@code PipTouchGesture.onMove} calls {@code PipMotionHelper.movePip()},
 * writing the leash directly and fighting ash. Measured on device, that is
 * avoided only because the compositor's ACTION_CANCEL wins a slop race against
 * it.
 *
 * <p>The fix is {@code setTouchGesture}, NOT {@code setTouchEnabled(false)}.
 * That was tried and broke every button on the menu: {@code PipMenuView}'s
 * window is {@code FLAG_NOT_TOUCHABLE}, so its ONLY input path is
 * {@code PipTouchHandler.onPipTouchEvent} → {@code
 * PhonePipMenuController.handlePointerEvent} ("Handles a pointer event sent
 * from pip input consumer"), and {@code setTouchEnabled(false)} closes exactly
 * that path. Replacing the gesture keeps the menu fed while removing the second
 * writer — which is also what Google's {@code ArcPipController} does.
 *
 * <p><b>Close has to be done here too.</b> Stock {@code
 * PipTaskOrganizer.removePip()} fades the leash with a ValueAnimator and only
 * removes the task when that animation reports finished — and the leash is not
 * ours to animate, so the callback never lands and the task stays. Dismiss is
 * therefore handled directly, by removing the task.
 */
@SysUISingleton
public class ArcPipShellMenu implements CoreStartable {
    private static final String TAG = "ArcPipShellMenu";

    /**
     * How long to wait after the pin before asking WM to resize the task.
     * Long enough that the pin transition has released its locks; short enough
     * that the window is not visibly the wrong size first.
     */
    private static final long BOUNDS_SETTLE_MS = 120;

    /**
     * How long after the last configuration change a rotation is considered
     * finished.
     *
     * Measured on device: rotating a PIP window produced display, work-area and
     * scale changes spread over ~230ms (09:08:48.783 output change ->
     * 09:08:49.046 the last bounds event), so anything shorter fires mid-flight
     * and sizes the window for a display shape that no longer exists. Debounced
     * rather than fixed, so this is a quiet period and not a race: each change
     * pushes the deadline out, and a slower rotation simply waits longer.
     */
    private static final long ROTATION_SETTLE_MS = 400;

    /**
     * How long after the entry size is applied before playback resumes.
     *
     * Long enough for the app to declare its aspect ratio and for that re-shape
     * to be adopted while the picture is still — measured 1.28s for VLC — and
     * short enough that the pause reads as part of the transition. Everything
     * ugly about entering PIP happens inside this window.
     */
    private static final long ENTRY_SETTLE_MS = 500;

    /**
     * How long after the pin a re-shape still counts as part of entering PIP.
     *
     * Backstop only — the window normally closes as soon as the app declares
     * its shape. Generous, because the alternative is a PIP that letterboxes
     * for the rest of the session.
     */
    private static final long ADOPT_WINDOW_MS = 4000;

    /**
     * How long after a display change to re-assert the menu's geometry.
     *
     * Run ON DEMAND, never on a timer. A periodic version of this was tried and
     * had to be removed: the Shell re-derives its own PIP rectangle (in display
     * coordinates, e.g. `Rect(28,71-954,591)`) whenever anything about the
     * display changes, and polling turned a one-off correction into a permanent
     * tug-of-war — measured as `menu resync` firing every second or two,
     * alternating between the Shell's rect and ours. Each correction re-lays-out
     * the menu view, so the menu ended up mis-hitting taps and behaving badly.
     *
     * Correcting once per real event is enough; the Shell does not move the menu
     * on its own otherwise.
     */
    private static final long MENU_RESYNC_MS = 500;

    /**
     * How long to let the Shell's dismiss animation run before closing the task.
     * The animation is on the task's leash, so removing the task first cancels
     * it — that is what made the slide-out vanish.
     */
    private static final long DISMISS_ANIMATION_MS = 300;

    private final Context mContext;
    private final Handler mHandler;
    private final PhonePipMenuController mMenuController;
    private final PipTouchHandler mTouchHandler;
    private final PipBoundsState mBoundsState;
    private final PipBoundsAlgorithm mBoundsAlgorithm;

    private int mPipTaskId = ActivityTaskManager.INVALID_TASK_ID;

    /**
     * The size this PIP window is, and stays, until the USER resizes it.
     *
     * A PIP window's size is decided once, on entry, and then belongs to the
     * user. Nothing the app does afterwards may change it — see
     * {@link #keepOrAdopt}. Zero until the entry size has been applied, and
     * cleared on unpin.
     */
    private int mLatchedW = 0, mLatchedH = 0;

    /**
     * The media session we paused on the way into PIP, or null.
     *
     * Held so the SAME session is resumed — looking it up again after the
     * window has settled would race the app tearing its session down and
     * building a new one for the PIP activity.
     */
    private volatile MediaController mPausedForEntry;

    /**
     * True from the pin until playback resumes — the window is being built and
     * the user is looking at a still frame.
     *
     * Independent of {@link #mPausedForEntry}, which is null when the video was
     * not playing: the transition window exists either way.
     */
    private volatile boolean mInEntryTransition;


    /**
     * For the calls that go back into WindowManager.
     *
     * These must NOT run on SystemUI's main thread. onActivityPinned is
     * delivered while WM is still completing the pin, so a synchronous binder
     * call back into it blocks until the transition finishes — measured on device
     * as "ANR in com.android.systemui ... Completed ANR in 2604ms", which takes
     * the PIP menu with it (a stalled SystemUI draws nothing).
     */
    private Handler mWmHandler;

    @Inject
    public ArcPipShellMenu(Context context, Handler handler,
            PhonePipMenuController menuController, PipTouchHandler touchHandler,
            PipBoundsState boundsState, PipBoundsAlgorithm boundsAlgorithm) {
        mContext = context;
        mHandler = handler;
        mMenuController = menuController;
        mTouchHandler = touchHandler;
        mBoundsState = boundsState;
        mBoundsAlgorithm = boundsAlgorithm;
    }

    @Override
    public void start() {
        final HandlerThread wmThread = new HandlerThread("ArcPipShellMenuWm");
        wmThread.start();
        mWmHandler = new Handler(wmThread.getLooper());
        try {
            ActivityManager.getService().registerTaskStackListener(
                    new TaskStackListener() {
                        @Override
                        public void onActivityPinned(String packageName, int userId,
                                int taskId, int rootTaskId) {
                            mHandler.post(() -> onPinned(taskId, packageName));
                        }

                        @Override
                        public void onActivityUnpinned() {
                            mHandler.post(() -> onUnpinned());
                        }
                    });
            mMenuController.addListener(mMenuListener);
            // Put the menu back whenever the Shell moves it.
            //
            // The Shell re-lays its menu out on every PIP bounds change, into
            // DISPLAY coordinates — measured Rect(28,71-928,448) — which mean
            // nothing inside a task-local virtual display, so the menu ends up
            // 28x71 inside the window with a sliver of it uncovered. Rotation
            // was only one cause; the one with no trigger at all was the app
            // changing its aspect ratio (VLC does it on stop/restart, and the
            // PIP legitimately re-shapes to the video), which is why the menu
            // drifted after pressing play with no configuration change in
            // sight.
            //
            // setBounds() is the event, so listen for it rather than reaching
            // for a timer: a periodic version of this was tried and removed
            // (see MENU_RESYNC_MS) because polling a value the Shell also owns
            // is a standing argument, not an observation. This fires ONLY when
            // the Shell actually moved something.
            mBoundsState.addPipExclusionBoundsChangeCallback(bounds -> onShellMovedMenu());
            // Once, for the life of the process — see mGesture for why this is
            // not installed and removed around each PIP.
            mTouchHandler.setTouchGesture(mGesture);
            Slog.i(TAG, "watching for PIP tasks");
        } catch (RemoteException e) {
            Slog.e(TAG, "failed to register task stack listener", e);
        }
    }

    /**
     * Everything that has to happen when a task becomes the PIP.
     *
     * THE ORDER AND THE THREADS ARE LOAD-BEARING. Each step below cost a
     * regression to learn; keep them in this shape:
     *
     * <ol>
     *   <li><b>Sizing off the main thread, deferred.</b> This callback is
     *       delivered while WM is still completing the pin, so calling back into
     *       WM here blocks on its lock — it ANR'd SystemUI for 2.6s, and a
     *       stalled SystemUI shows no menu.</li>
     *   <li><b>Adopt the menu on a retry ladder.</b> Its surface does not exist
     *       yet; the Shell attaches it during the PIP transition.</li>
     * </ol>
     *
     * The touch gesture is NOT installed here — it is installed once in
     * {@link #start()}; see {@link #mGesture}.
     */
    private void onPinned(int taskId, String packageName) {
        mPipTaskId = taskId;
        // (1) Stop playback while the window is being built.
        //
        // Entering PIP is a resize of a surface the app is actively rendering
        // into, and everything ugly about the transition happens during it: the
        // task arrives at its launch size, the entry rectangle is applied, the
        // Shell lays its menu out, and the app re-reports its aspect ratio —
        // all while frames keep arriving for the old geometry. Pausing means
        // the window is built and settled against a still image, then playback
        // resumes into a window that is already the right size.
        // OFF the main thread, like every other call out of this class.
        // getActiveSessions() and the transport controls are synchronous binder
        // calls into MediaSessionService, and onActivityPinned arrives while WM
        // is still completing the pin — running them inline ANR'd SystemUI
        // outright ("ANR in com.android.systemui", pip_input_consumer not
        // responding, the PIP window stuck). See onWm().
        mInEntryTransition = true;
        // The adoption window deliberately OUTLIVES the pause. A video player
        // cannot report the shape of a video it is not rendering: measured on
        // device, VLC declared its aspect 0.5s AFTER playback resumed, so a
        // window that closed on resume shut exactly when the event it was
        // waiting for became possible. It closes on the first adopted re-shape
        // instead, with this as the backstop for apps that never declare one.
        onWm(() -> mInEntryTransition = false, ADOPT_WINDOW_MS);
        onWm(() -> pausePlaybackForEntry(packageName), 0);
        // (2) NEVER inline this onto the calling thread — see onWm().
        onWmDebounced(mApplySize, BOUNDS_SETTLE_MS);
        // (3) Adopt the menu. Its surface does not exist until
        // PhonePipMenuController has attached the view, which happens on the
        // Shell's PIP transition and races this callback either way round — so
        // retry on a short ladder rather than assume an order. Stamping twice is
        // harmless (setMetadata is idempotent), and adoptMenuSurface() always
        // does stamp-then-reposition, which must not be split: a stamped menu
        // still sitting at the Shell's stale rectangle inflates the task
        // footprint instead of riding inside the window.
        adoptMenuSurface("pinned");
        mHandler.postDelayed(() -> adoptMenuSurface("+120ms"), 120);
        mHandler.postDelayed(() -> adoptMenuSurface("+400ms"), 400);
        mHandler.postDelayed(() -> adoptMenuSurface("+900ms"), 900);
        // ...and once more after the entry size has settled. The ladder only covers the first second; the
        // Shell moves the menu again on any display change. Measured on device:
        // after a rotation the Shell recomputed its PIP rect as
        // Rect(28,71-928,577) and repositioned the menu layer to (28,71) — but
        // the menu is composited into the TASK's virtual display, whose
        // coordinates are task-local, so it sat 28x71 inside the PIP window and
        // left a sliver of it uncovered.
        onWm(mResync, 1200);
    }

    /**
     * Close the task the menu's dismiss button was pressed on.
     *
     * Unconditional. The Shell's own dismiss path takes the task OUT of pinned
     * mode but does not close it — `onActivityUnpinned` fires and the app simply
     * comes back as an ordinary window, which is not what the close button
     * means. (An earlier version guarded on "is it still pinned?" and therefore
     * never fired, because unpin happens on EVERY PIP exit, expand included.)
     *
     * Safe to call when the task is already gone: removeTask just returns false.
     * Dismiss is the only thing routed here — expand arrives as onPipExpand and
     * is left to the Shell.
     */
    /**
     * Run something that talks to WindowManager, off the main thread.
     *
     * The single funnel for it: every call into WM from this class goes through
     * here, so no future edit can accidentally put one back on SystemUI's main
     * thread. WM's own callbacks (onActivityPinned, onPipDismiss) are delivered
     * mid-transition while WM holds its global lock, and a synchronous binder
     * call back into it from the main thread stalls SystemUI outright.
     */
    private void onWm(Runnable work, long delayMs) {
        if (mWmHandler == null) return;
        mWmHandler.postDelayed(work, delayMs);
    }

    /**
     * Like {@link #onWm}, but only the LAST post in a burst runs.
     *
     * A rotation is not one event, it is a stream of them — the display change,
     * the work-area change, the DSF change — arriving over a few hundred
     * milliseconds, and the display is a different shape at each. Sizing the
     * window off any but the last one picks an intermediate rectangle and then
     * has to be corrected, which is how the window came out one size and the
     * menu another. Re-posting cancels the pending work, so the size is chosen
     * once, from the display as it finally is.
     *
     * Requires a STABLE Runnable — {@code removeCallbacks} matches by identity,
     * so a fresh lambda per call would cancel nothing. That is why the rotation
     * work lives in {@link #mApplySize} and {@link #mResync} rather than being
     * written inline at the call sites.
     */
    private void onWmDebounced(Runnable work, long delayMs) {
        if (mWmHandler == null) return;
        mWmHandler.removeCallbacks(work);
        mWmHandler.postDelayed(work, delayMs);
    }

    /**
     * Re-derive and apply the PIP window's size for the display as it is now.
     *
     * A field rather than a lambda at the call site so {@link #onWmDebounced}
     * can cancel it.
     */
    private final Runnable mApplySize = new Runnable() {
        @Override
        public void run() {
            final int taskId = mPipTaskId;
            if (taskId == ActivityTaskManager.INVALID_TASK_ID) return;
            sizeBornPinnedTask(taskId);
        }
    };

    /**
     * A task rectangle fydroid will accept: the SIZE of {@code r}, at the origin.
     *
     * Every ARC container task must sit at {@code (0,0,W,H)} — a non-zero origin
     * desynchronises the WMS frame from the task-local coordinates SurfaceFlinger
     * emits, and taps stop landing where they are aimed (picture-in-picture-plan
     * §3.4 item 2; base patch 0042 forces this on the launch path). Rectangles
     * that come from the Shell carry a screen position, so they must be passed
     * through here before being applied. Where the window SITS is ash's.
     */
    private static Rect sizeAtOrigin(Rect r) {
        return new Rect(0, 0, r.width(), r.height());
    }

    /**
     * Decide what the PIP window's size should be, given what it just became.
     *
     * <p>The window keeps its size until the USER resizes it. What kept
     * changing it instead was the app: {@code
     * PipBoundsAlgorithm.getAspectRatioOrDefault()} falls back to {@code
     * config_pictureInPictureDefaultAspectRatio} (1.7778) whenever the app's
     * {@code PictureInPictureParams} carry no aspect ratio, and VLC only sets
     * one WHILE PLAYING. So stopping and restarting playback swung the aspect
     * 2.39 → 1.78 → 2.39 and the Shell re-shaped the pinned task each time —
     * measured as the window snapping between 900x378 and 900x506 with the user
     * never having touched it.
     *
     * <p>The two cases are told apart by the ASPECT RATIO, which is exactly
     * what distinguishes them physically:
     *
     * <ul>
     *   <li><b>Different aspect</b> — an app re-shape. Refused: the latched
     *       size is put back.
     *   <li><b>Same aspect, different size</b> — a uniform scale, which is what
     *       a user resize is (ash resizes a PIP without distorting it). Adopted,
     *       and the latch moves with it, so the new size is the one defended
     *       from then on.
     * </ul>
     *
     * <p>Same tolerance style as the compositor's projection test: an integer
     * cross-multiply with an absolute tolerance, because both sides are
     * independent roundings and a relative tolerance is far too loose at these
     * sizes.
     *
     * @return the size to enforce, at the origin
     */
    private Rect keepOrAdopt(Rect taskSize) {
        final int w = taskSize.width(), h = taskSize.height();
        if (mLatchedW <= 0 || mLatchedH <= 0) return taskSize;
        if (w == mLatchedW && h == mLatchedH) return taskSize;
        if (w <= 0 || h <= 0) return taskSize;

        // Not a re-shape at all — this is the task on its way OUT.
        //
        // Leaving PIP, WM restores the task's normal bounds while it is STILL
        // windowing-mode-pinned, so the restore arrives here looking like a
        // re-shape. Measured on device with the first version of this latch: the
        // exit to 1280x1440 was refused over and over and the window was held
        // at 900x506 while the user was trying to expand it.
        //
        // Judged by AREA, not by the PIP maximum. The maximum was tried first
        // and is wrong, because "keep the size across a rotation" means the
        // window legitimately exceeds it: a 900-wide window kept from landscape
        // is over the portrait maximum of 800, so every rotation into portrait
        // released the latch and the size drifted free — measured 900x378
        // becoming 900x426 across one rotation each way. A restore multiplies
        // the area several-fold (1280x1440 is 5.4x a 900x378 PIP) while no
        // re-shape or resize comes close, so the ratio separates them cleanly
        // and says nothing about the display.
        final boolean isRestore = w >= mLatchedW && h >= mLatchedH
                && (long) w * h >= EXIT_AREA_RATIO * (long) mLatchedW * mLatchedH;
        if (isRestore) {
            Slog.i(TAG, "task size " + w + "x" + h + " is not a pip size — leaving pip, "
                    + "releasing the size latch");
            mLatchedW = mLatchedH = 0;
            return taskSize;
        }

        final long a = (long) mLatchedW * h;
        final long b = (long) w * mLatchedH;
        final long diff = Math.abs(a - b);
        final long tolerance = mLatchedW + mLatchedH + w + h;
        if (diff <= tolerance) {
            Slog.i(TAG, "pip resized " + mLatchedW + "x" + mLatchedH + " -> " + w + "x" + h
                    + " at the same aspect — adopting it as the new size");
            mLatchedW = w;
            mLatchedH = h;
            return taskSize;
        }
        if (mInEntryTransition) {
            // Still inside the entry pause, so nothing is moving on screen and
            // a re-shape here costs the user nothing. Take it.
            //
            // This is what the pause buys. Holding a 16:9 window for a 2.39:1
            // film is not free: VLC best-fits into it, so the picture
            // letterboxes and the menu — which correctly covers the WINDOW —
            // ends up visibly taller than the video, with black bars above and
            // below. Adopting the app's shape while playback is stopped gives
            // window == video == menu with no snap for the user to see, which
            // is strictly better than either half of the earlier trade.
            //
            // Measured on device: with the pause in place VLC declared its aspect
            // 1.28s after the entry size was applied, so ENTRY_SETTLE_MS has to
            // outlast that or the window resumes before the shape arrives.
            Slog.i(TAG, "adopting " + w + "x" + h + " during the entry transition (was "
                    + mLatchedW + "x" + mLatchedH + ")");
            mLatchedW = w;
            mLatchedH = h;
            // One shape per session: the app has now said what it is, so the
            // window is settled and everything after this is refused.
            mInEntryTransition = false;
            return taskSize;
        }
        // NOT adopted, once playback has resumed.
        //
        // An earlier version made an exception for it, on the reasoning that
        // the entry size came from the default aspect ratio (the app having
        // said nothing yet) and so was not a real choice. That is true, but the
        // exception is not worth its cost: VLC declares nothing at entry and
        // only reports its aspect ~5 SECONDS in, so the exception showed as the
        // window visibly snapping 900x506 -> 900x378 while the user watched,
        // once per PIP session — and VLC starts a new session on every restart.
        //
        // It buys nothing, because a video player scales the video to BEST FIT
        // the window it is given: a 2.39:1 film in a 16:9 PIP is letterboxed,
        // not cropped or distorted. So the window's exact aspect does not
        // decide whether the video looks right. Stability does, and the size
        // the window opens at is the one the user sees. Hold it.
        Slog.i(TAG, "refusing app re-shape " + mLatchedW + "x" + mLatchedH + " -> " + w + "x" + h
                + " (aspect " + String.format("%.2f", (float) mLatchedW / mLatchedH) + " -> "
                + String.format("%.2f", (float) w / h) + "); keeping the window's size");
        return new Rect(0, 0, mLatchedW, mLatchedH);
    }


    /**
     * Pause {@code packageName}'s playback, if it is playing, for the duration
     * of the PIP entry.
     *
     * Only a session that is ACTUALLY PLAYING is touched, and the controller is
     * remembered so {@link #resumePlaybackAfterEntry} can put back exactly what
     * was changed — a user who entered PIP on a paused video must not find it
     * playing afterwards.
     */
    private void pausePlaybackForEntry(String packageName) {
        mPausedForEntry = null;
        if (packageName == null) return;
        try {
            final MediaSessionManager msm = mContext.getSystemService(MediaSessionManager.class);
            if (msm == null) return;
            for (MediaController c : msm.getActiveSessions(null /* notificationListener */)) {
                if (c == null || !packageName.equals(c.getPackageName())) continue;
                final PlaybackState state = c.getPlaybackState();
                if (state == null || state.getState() != PlaybackState.STATE_PLAYING) continue;
                c.getTransportControls().pause();
                mPausedForEntry = c;
                Slog.i(TAG, "paused " + packageName + " for the pip transition");
                return;
            }
        } catch (Exception e) {
            // SecurityException if the permission is missing, or the session
            // list changed under us. Playback simply carries on — the entry is
            // uglier, not broken.
            Slog.w(TAG, "could not pause " + packageName + " for the pip transition", e);
        }
    }

    /** Resume whatever {@link #pausePlaybackForEntry} paused. Idempotent. */
    private void resumePlaybackAfterEntry() {
        final MediaController c = mPausedForEntry;
        if (c == null) return;
        mPausedForEntry = null;
        try {
            c.getTransportControls().play();
            Slog.i(TAG, "resumed " + c.getPackageName() + " in the settled pip window");
        } catch (Exception e) {
            Slog.w(TAG, "could not resume " + c.getPackageName(), e);
        }
    }

    /**
     * One line describing every number that can displace or scale the menu.
     *
     * Kept because the rectangles all agreed while the menu still looked wrong:
     * the view's laid-out size and its matrix are what the logs were missing.
     */
    private void logMenuGeometry(Rect bounds) {
        try {
            if (mMenuViewField == null) return;
            final Object o = mMenuViewField.get(mMenuController);
            if (!(o instanceof View)) return;
            final View v = (View) o;
            Slog.i(TAG, "menu geometry: want=" + bounds
                    + " view=" + v.getWidth() + "x" + v.getHeight()
                    + " at (" + v.getLeft() + "," + v.getTop() + ")"
                    + " measured=" + v.getMeasuredWidth() + "x" + v.getMeasuredHeight()
                    + " scale=" + v.getScaleX() + "," + v.getScaleY()
                    + " translation=" + v.getTranslationX() + "," + v.getTranslationY()
                    + " pivot=" + v.getPivotX() + "," + v.getPivotY()
                    + " shell=" + mBoundsState.getBounds());
        } catch (Throwable t) {
            // Diagnostic only.
        }
    }

    /** Cached {@code PhonePipMenuController.mPipMenuView}. See relayoutMenuView(). */
    private Field mMenuViewField;

    /**
     * Force the menu's VIEW to re-measure and re-layout at {@code bounds}.
     *
     * <p>Everything above this moves the menu's WINDOW — {@code
     * updateMenuBounds} rewrites its layout params, {@code movePipMenu}
     * repositions its surface — and none of it lays out the view's children.
     * On a phone that costs nothing, because the Shell resizes the menu while
     * it is VISIBLE and animating, so a traversal follows immediately. Here the
     * menu is {@code GONE} until the user taps the window, so no traversal
     * happens and the children keep the layout they had at the size the menu
     * was created with.
     *
     * <p>Measured on device from a screenshot with the menu open: the window and
     * the menu surface were both 900x380 — agreeing, as every rectangle in the
     * logs said — but the transport controls sat at about (425,262) instead of
     * the (450,190) that centres them, i.e. laid out for the ~900x506 entry
     * rectangle the menu was born with. The window was never the wrong size;
     * its contents were.
     *
     * <p>Reflection because {@code mPipMenuView} is private and {@code
     * PhonePipMenuController} exposes no view accessor. Failure is non-fatal —
     * the menu is merely laid out as before — and the field is cached so this
     * costs one lookup per process.
     */
    private void relayoutMenuView(Rect bounds) {
        try {
            if (mMenuViewField == null) {
                mMenuViewField = mMenuController.getClass()
                        .getDeclaredField("mPipMenuView");
                mMenuViewField.setAccessible(true);
            }
            final Object o = mMenuViewField.get(mMenuController);
            if (!(o instanceof View)) return;
            final View v = (View) o;
            final int w = bounds.width(), h = bounds.height();
            v.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                      MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            v.layout(0, 0, w, h);
            v.requestLayout();
            v.invalidate();
        } catch (Throwable t) {
            Slog.w(TAG, "could not re-layout the pip menu view", t);
            mMenuViewField = null;
        }
    }

    /**
     * How much bigger than the PIP the task must get before it counts as
     * leaving PIP rather than being re-shaped. See {@link #keepOrAdopt}.
     */
    private static final long EXIT_AREA_RATIO = 3L;

    /** How many running tasks to scan when looking for the pinned one. */
    private static final int MAX_TASKS_TO_SCAN = 32;

    /**
     * The size the APP asked for, or null if it did not ask.
     *
     * The PIP window's size should come from the app, not from the screen.
     * {@code PipBoundsAlgorithm.getEntryDestinationBounds()} takes the app's
     * ASPECT RATIO but computes the SIZE as a percentage of the display, so the
     * same video got a different rectangle on every display shape — 900x506 in
     * landscape, 926x520 in portrait — and that recomputation is what made the
     * window change size when nothing about the video had.
     *
     * {@code PictureInPictureParams.getSourceRectHint()} is the app's own
     * statement of what is going into the PIP: the on-screen rectangle of the
     * view being handed over. Taking the size from there means the PIP is
     * derived from the content, is stable across rotations because nothing
     * about the display enters into it, and honours an app that has thought
     * about its own PIP.
     *
     * Still passed through {@link #clampToPipMaximum} — a source hint is
     * frequently the full-screen video surface, and a full-screen "PIP" is not
     * one. Clamping scales it down uniformly, so the app's shape survives.
     *
     * Returns a size at the origin, or null when the app supplied no hint (many
     * do not), in which case the caller falls back to the algorithm.
     */
    private Rect appDeclaredSize(int taskId) {
        try {
            final List<ActivityManager.RunningTaskInfo> tasks =
                    ActivityTaskManager.getService().getTasks(
                            MAX_TASKS_TO_SCAN, false /* filterOnlyVisibleRecents */,
                            false /* keepIntentExtra */, Display.INVALID_DISPLAY);
            if (tasks == null) return null;
            for (ActivityManager.RunningTaskInfo info : tasks) {
                if (info == null || info.taskId != taskId) continue;
                final PictureInPictureParams params = info.pictureInPictureParams;
                if (params == null) return null;
                Slog.i(TAG, "task " + taskId + " pip params: sourceHint="
                        + (params.hasSourceBoundsHint() ? params.getSourceRectHint() : "none")
                        + " aspect="
                        + (params.hasSetAspectRatio() ? params.getAspectRatioFloat() : "none"));
                if (params.hasSourceBoundsHint()) {
                    final Rect hint = params.getSourceRectHint();
                    if (hint != null && !hint.isEmpty()) {
                        return new Rect(0, 0, hint.width(), hint.height());
                    }
                }
                // No size, but the app may still have declared a SHAPE. Ask it
                // here rather than waiting for the Shell to be told: measured on
                // device, VLC's aspect ratio reached PipBoundsState 5.2 SECONDS
                // after the PIP opened, so the window sat at the framework
                // default (16:9, 900x506) and then visibly snapped to the
                // movie's 2.39:1 (900x378) while the user watched. Reading the
                // params directly gets the same answer at entry, if the app has
                // set it by then.
                if (params.hasSetAspectRatio()) {
                    final float aspect = params.getAspectRatioFloat();
                    if (aspect > 0.01f) {
                        final Rect base = mBoundsAlgorithm.getEntryDestinationBounds();
                        if (base != null && !base.isEmpty()) {
                            // Keep the algorithm's WIDTH — it is the sensible
                            // fraction of the display — and take the height from
                            // the app's shape.
                            final int w = base.width();
                            final int h = Math.max(1, Math.round(w / aspect));
                            return new Rect(0, 0, w, h);
                        }
                    }
                }
                return null;
            }
        } catch (Exception e) {
            Slog.w(TAG, "failed to read pip params for task " + taskId, e);
        }
        return null;
    }

    /**
     * Apply ARC's maximum PIP size — half the display on each axis.
     *
     * ARCVM's {@code ArcPipBoundsAlgorithm} applies exactly this, and so does
     * our compositor. That duplication is the bug: the Shell's stock {@link
     * PipBoundsAlgorithm} knows nothing about the ARC maximum and derived a
     * 926x520 rectangle on a 1600x2560 portrait display, which the compositor
     * then quietly scaled to 800x449 on the way to ash and told nobody. The
     * task stayed 926x520, this menu is sized from the task, and the window on
     * screen was 800x449 — "the menu and the window are different sizes".
     *
     * Applying the maximum HERE, before the size is ever set on the task,
     * leaves ONE owner of that number: WM, the task, ash and the menu all see
     * the same rectangle, and the compositor's cap goes back to being the
     * backstop its comment claims it is rather than a second opinion. Do not
     * "fix" this by teaching the compositor to report its clamp back — that
     * builds a second owner and a round trip; see the one-owner rule in
     * docs/picture-in-picture-plan.md.
     *
     * Both axes scale by the tighter ratio, so the aspect the algorithm chose
     * (16:9 for video) survives the clamp instead of the window going square.
     */
    private Rect clampToPipMaximum(Rect size) {
        final Rect display = mBoundsState.getDisplayBounds();
        if (display == null || display.isEmpty()) return size;
        final int maxW = display.width() / 2;
        final int maxH = display.height() / 2;
        if (maxW <= 0 || maxH <= 0) return size;
        if (size.width() <= maxW && size.height() <= maxH) return size;
        final float fit = Math.min((float) maxW / size.width(),
                                   (float) maxH / size.height());
        return new Rect(0, 0,
                        Math.max(1, Math.round(size.width() * fit)),
                        Math.max(1, Math.round(size.height() * fit)));
    }

    /**
     * Tell WM the pinned task is closing rather than expanding, so it suppresses
     * the PIP-exit push to the compositor. Best-effort: without it the close
     * still works, it just flashes a restored window on the way out.
     */
    private void notifyPipClosing(int taskId) {
        try {
            final IArcWindowManager awm = IArcWindowManager.Stub.asInterface(
                    ServiceManager.getService("arc_window_manager"));
            if (awm == null) {
                Slog.w(TAG, "no arc_window_manager; pip close will flash a restore");
                return;
            }
            awm.notifyPipClosing(taskId);
        } catch (RemoteException e) {
            Slog.w(TAG, "notifyPipClosing failed for task " + taskId, e);
        }
    }

    private void closeDismissedTask(int taskId) {
        try {
            final boolean removed = ActivityTaskManager.getService().removeTask(taskId);
            Slog.i(TAG, "dismiss: removeTask(" + taskId + ") -> " + removed);
        } catch (RemoteException e) {
            Slog.e(TAG, "failed to remove pip task " + taskId, e);
        }
    }

    /**
     * Give a task that was launched ALREADY pinned a real PIP rectangle.
     *
     * On the multi-activity path an app can start a new activity in a new task
     * that is pinned from birth — VLC does, and `android pip=1` arrives before
     * `onTaskCreated`. Because the task never existed unpinned, WM never runs
     * its PIP bounds computation over it and simply reports the launch bounds:
     * measured on device as a 1558x946 "PIP" window, VLC's own pre-PIP size, which
     * looks exactly like the app never left its window. (The single-activity
     * path is fine — WM produces 900x506 for 16:9 there.)
     *
     * `getEntryDestinationBounds()` is what upstream applies for this, from
     * `ArcPipTransition.maySetWctForPipInHandleRequest`. Only applied when the
     * task is actually too big, so a correctly-sized PIP is left alone rather
     * than nudged into a second resize.
     */
    private void sizeBornPinnedTask(int taskId) {
        // Catch everything: this runs on its own thread, and getTaskBounds and
        // friends throw unchecked (IllegalArgumentException for a task that has
        // already gone) as readily as they throw RemoteException. An escape here
        // would take the thread down silently.
        try {
            if (mPipTaskId != taskId) return;  // already left PIP
            // The APP's own rectangle first; the display-derived one only as a
            // fallback. See appDeclaredSize().
            Rect entry = appDeclaredSize(taskId);
            String source = "app";
            if (entry == null) {
                entry = mBoundsAlgorithm.getEntryDestinationBounds();
                source = "algorithm";
            }
            if (entry == null || entry.isEmpty()) return;
            // Size only — getEntryDestinationBounds() also carries the
            // rectangle's screen position (measured Rect(1632,1066-2532,1572)),
            // and applying that verbatim broke every tap. See sizeAtOrigin().
            final Rect atOrigin = clampToPipMaximum(sizeAtOrigin(entry));
            final IArcWindowManager awm = IArcWindowManager.Stub.asInterface(
                    ServiceManager.getService("arc_window_manager"));
            if (awm == null) return;
            Slog.i(TAG, "task " + taskId + " pinned — applying entry size "
                    + atOrigin + " (from the " + source + ", " + entry + ")");
            awm.setTaskWindowBounds(taskId, -1 /* keep display */, atOrigin);
            mLatchedW = atOrigin.width();
            mLatchedH = atOrigin.height();
            // The window is now the size it will keep — start playing again.
            onWm(ArcPipShellMenu.this::resumePlaybackAfterEntry, ENTRY_SETTLE_MS);
            // Give the MENU the same rectangle, now, from the same number —
            // do not wait to observe it back off the task.
            //
            // The menu and the window used to be sized in two steps 500ms
            // apart: this call sized the task, and mResync later *observed* the
            // task and laid the menu out to match. In between, the menu kept
            // the Shell's own unclamped rectangle, and the task VD (which is
            // the union of the task and the layers stamped into it) took the
            // taller of the two. Measured on device across a rotation to
            // landscape:
            //
            //     VD 800x450 -> 900x520   width from the task, height from the menu
            //     menu resync -> 900x506
            //     VD 900x520 -> 900x506   only now do they agree
            //
            // — half a second of a 506-tall window with a 520-tall menu in it,
            // seen as "the window is shorter than the pip menu". One number,
            // handed to both owners in one step, cannot disagree; mResync stays
            // as the late backstop and is now a no-op in the normal case.
            //
            // Posted to the main thread: applyMenuBounds lays out views, and
            // this runs on the WM thread. See onWm().
            mHandler.post(() -> applyMenuBounds(new Rect(atOrigin), "entry size"));
        } catch (Exception e) {
            Slog.w(TAG, "failed to size pinned task " + taskId, e);
        }
    }

    private void onUnpinned() {
        Slog.i(TAG, "pip task " + mPipTaskId + " unpinned");
        mPipTaskId = ActivityTaskManager.INVALID_TASK_ID;
        mLatchedW = mLatchedH = 0;
        mInEntryTransition = false;
        // Never leave a session paused because the PIP went away mid-entry.
        onWm(this::resumePlaybackAfterEntry, 0);
        if (mWmHandler != null) {
            mWmHandler.removeCallbacks(mResync);
            // A debounced size can still be queued from a rotation that
            // overlapped the exit; it would resize a task that is no longer
            // pinned.
            mWmHandler.removeCallbacks(mApplySize);
        }
    }

    /**
     * THE single writer of the PIP window's geometry, and of the menu's.
     *
     * <p>Everything funnels here — the stamp ladder, the Shell's own
     * {@code setBounds} callback, and a configuration change — and it always
     * does the same three things, in the same order, from ONE number:
     *
     * <ol>
     *   <li>read the task's rectangle, which is the truth;
     *   <li>decide the size ({@link #keepOrAdopt}) and put the task back at
     *       {@code (0,0,W,H)} if it moved or was re-shaped;
     *   <li>lay the menu out on exactly that rectangle.
     * </ol>
     *
     * <p>It is written as one function on purpose. Every menu bug in this class
     * came from a second writer: a 500ms poll fighting the Shell, then a
     * separate {@code moveMenuToTaskOrigin} on the stamp ladder that sized the
     * menu from the SHELL's rectangle while this sized it from the TASK. Both
     * are gone. If the menu is ever the wrong size again, the question is not
     * "what value is wrong" but "who else is writing it" — and the answer
     * should be nobody.
     *
     * <p>Cheap in the steady state: {@link #applyMenuBounds} returns
     * immediately when the rectangle is already right, so a no-op resync is one
     * binder read and two comparisons, with no logging.
     */
    private final Runnable mResync = new Runnable() {
        @Override
        public void run() {
            final int taskId = mPipTaskId;
            if (taskId == ActivityTaskManager.INVALID_TASK_ID) return;
            // The TASK's rectangle is the truth. The Shell's PipBoundsState is
            // not: our compositor resizes the pinned task (ash owns the window,
            // and rotation re-derives its size — measured 900x506 -> 800x450),
            // and nothing tells the Shell, so a menu laid out from its bounds
            // ends up the wrong size for the window it is sitting in.
            //
            // getTaskBounds is a binder call, so it runs on the WM thread; the
            // menu update touches views, so it goes back to the main thread.
            // Keeping that split is what stops this ANRing SystemUI.
            Rect task = null;
            try {
                task = ActivityTaskManager.getService().getTaskBounds(taskId);
            } catch (Exception e) {
                // Task gone, or WM busy — try again next tick.
            }
            if (task != null && !task.isEmpty()) {
                final Rect want = keepOrAdopt(sizeAtOrigin(task));
                // Pin the TASK back to the origin, keeping its size.
                //
                // The Shell applies its destination bounds POSITION as well as
                // its size, so a re-layout leaves the pinned task at e.g.
                // Rect(28,71-928,577). Every ARC container task must sit at
                // (0,0,W,H) — a non-zero origin desynchronises the WMS frame
                // from the task-local coordinates SurfaceFlinger emits and taps
                // stop landing where they are aimed (see sizeAtOrigin()). This
                // used to be fixed as a side effect of re-deriving the size on
                // every configuration change; now that a rotation deliberately
                // does not resize anything, the origin has to be put back on
                // its own. Same width and height, so it is a move, not a
                // resize — which is the whole point.
                if (task.left != 0 || task.top != 0
                        || task.width() != want.width() || task.height() != want.height()) {
                    try {
                        final IArcWindowManager awm = IArcWindowManager.Stub.asInterface(
                                ServiceManager.getService("arc_window_manager"));
                        if (awm != null) {
                            Slog.i(TAG, "task " + taskId + " was " + task
                                    + " — asserting " + want);
                            awm.setTaskWindowBounds(taskId, -1 /* keep display */, want);
                        }
                    } catch (Exception e) {
                        Slog.w(TAG, "failed to re-origin pinned task " + taskId, e);
                    }
                }
                if (!want.equals(mBoundsState.getBounds())) {
                    mHandler.post(() -> applyMenuBounds(want, "resync"));
                }
            }
            // Deliberately does NOT re-arm itself. Run on demand only — see the
            // comment on MENU_RESYNC_MS.
        }
    };

    /**
     * A rotation must not change the PIP window.
     *
     * <p>A PIP window keeps its size, its aspect and its content scale when the
     * screen rotates — the user put it there, the video is playing in it, and
     * nothing about turning the device over is a request to resize it. So this
     * deliberately does NOT re-derive a size. Everything that used to do so has
     * been removed:
     *
     * <ul>
     *   <li>this method used to call {@code getEntryDestinationBounds()} again
     *       and re-apply it, which recomputes from scratch for the new display
     *       (measured 900x506 landscape -> 926x520 portrait, a DIFFERENT aspect
     *       ratio for the same video);
     *   <li>the compositor's half-output cap used to run on every push, so
     *       rotating into portrait — where half of 1600 is 800 — "capped" a
     *       good 900x506 window to 800x449; it is now entry-only;
     *   <li>ash's re-armed max_size used to be half the new output, which made
     *       ASH clip the width to 800; it is now never smaller than the window.
     * </ul>
     *
     * <p>What still has to happen is the MENU. The Shell re-lays its own menu
     * out on a configuration change, into display coordinates
     * ({@code Rect(28,71-928,577)}) that mean nothing inside a task-local
     * virtual display — so the menu, and only the menu, is put back onto the
     * task. {@link #mResync} reads the task's CURRENT rectangle, which the
     * rotation has not changed, so this restores rather than resizes.
     *
     * <p>Debounced: a rotation is a burst of configuration changes and the
     * Shell moves the menu on each, so the restore runs once, after the last.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mPipTaskId == ActivityTaskManager.INVALID_TASK_ID) return;
        onWmDebounced(mResync, ROTATION_SETTLE_MS);
    }

    /**
     * The Shell laid its menu out somewhere of its own choosing — put it back.
     *
     * Debounced hard, for two reasons. A single re-layout is a burst of
     * {@code setBounds} calls, and {@link #applyMenuBounds} calls
     * {@code setBounds} itself, so an immediate response would re-enter this
     * through the same callback. {@code applyMenuBounds} also returns early
     * when the rectangle is already right, so the steady state is one
     * comparison and no work.
     */
    private void onShellMovedMenu() {
        if (mPipTaskId == ActivityTaskManager.INVALID_TASK_ID) return;
        onWmDebounced(mResync, MENU_RESYNC_MS);
    }

    /**
     * Put the Shell's menu at {@code bounds} — size and origin.
     *
     * Main thread only: {@code updateMenuBounds} lays out the menu view.
     */
    private void applyMenuBounds(Rect bounds, String why) {
        if (mPipTaskId == ActivityTaskManager.INVALID_TASK_ID) return;
        if (bounds.equals(mBoundsState.getBounds())) return;
        Slog.i(TAG, "menu " + why + ": " + mBoundsState.getBounds() + " -> " + bounds);
        mBoundsState.setBounds(bounds);
        mMenuController.updateMenuBounds(bounds);
        // BEFORE movePipMenu, not after.
        //
        // movePipMenu() builds a MATRIX for the menu's surface from the VIEW's
        // current size against the destination bounds — so with a stale view it
        // scales and translates the menu instead of just placing it. A 900x506
        // view (the entry size) moved into 900x378 bounds gives a scale of
        // 378/506 = 0.747 plus an offset, which is exactly "the menu is smaller
        // than the window" and "the menu is offset" seen together, with every
        // rectangle in the logs still agreeing — the matrix is not a rectangle.
        // Laying the view out first means the matrix is computed from a view
        // that is already the right size, so it comes out as identity.
        relayoutMenuView(bounds);
        mMenuController.movePipMenu(null /* pipLeash */, null /* t */, bounds);
        logMenuGeometry(bounds);
    }

    /**
     * The PIP gesture, replacing the Shell's default.
     *
     * {@code onMove} consumes the event and does nothing: ash owns the drag,
     * asked for by the compositor's {@code start_move}, so the default's
     * {@code PipMotionHelper.movePip()} must never run. {@code onUp} keeps the
     * one behaviour of the default we do want — a tap raises the menu.
     */
    /**
     * The PIP gesture, replacing the Shell's default — installed ONCE.
     *
     * Not installed on pin and restored on unpin, for two reasons. The Shell's
     * default is a private inner class ({@code DefaultPipTouchGesture}), so
     * there is nothing to restore it *to*; and an install/uninstall cycle around
     * every PIP is a state machine with an obvious failure mode — miss one
     * unpin and the Shell silently starts moving windows behind ash's back
     * again. Installing once removes the asymmetry: for ARC this gesture is
     * always the correct one, because ash always owns the drag.
     *
     * Self-guarding: every callback no-ops unless a task is actually pinned.
     * {@code PipTouchHandler}'s input consumer only exists while there is a PIP,
     * so this is belt-and-braces rather than load-bearing — but it means the
     * object is inert at every moment it has no business acting.
     */
    private final PipTouchGesture mGesture = new PipTouchGesture() {
        @Override
        public boolean onMove(PipTouchState touchState) {
            if (mPipTaskId == ActivityTaskManager.INVALID_TASK_ID) return false;
            return true;  // consumed; the window is ash's to move
        }

        @Override
        public boolean onUp(PipTouchState touchState) {
            if (mPipTaskId == ActivityTaskManager.INVALID_TASK_ID) return false;
            if (!touchState.isUserInteracting() || touchState.startedDragging()) {
                return false;
            }
            mMenuController.showMenu(PhonePipMenuController.MENU_STATE_FULL,
                    mBoundsState.getBounds(), true /* allowMenuTimeout */,
                    false /* willResizeMenu */, false /* showResizeHandle */);
            return true;
        }
    };

    /**
     * Menu actions that the Shell cannot complete on fydroid.
     *
     * Only dismiss needs taking over — see the class comment for why
     * {@code PipTaskOrganizer.removePip()} stalls. Expand is left to the Shell,
     * which reparents the activity back through WM core.
     */
    private final PhonePipMenuController.Listener mMenuListener =
            new PhonePipMenuController.Listener() {
                @Override
                public void onPipMenuStateChangeStart(int menuState, boolean resize,
                        Runnable callback) {}

                @Override
                public void onPipMenuStateChangeFinish(int menuState) {}

                @Override
                public void onPipExpand() {}

                @Override
                public void onPipDismiss() {
                    final int taskId = mPipTaskId;
                    if (taskId == ActivityTaskManager.INVALID_TASK_ID) return;
                    // Tell WM this is a CLOSE before anything else. WM tears a
                    // pinned task down THROUGH the windowing-mode change, so a
                    // dismiss and an expand both surface as onActivityUnpinned —
                    // and pushing that to the compositor as a PIP exit makes it
                    // restore the caption and the pre-PIP rectangle. Measured on
                    // device: "android pip=0" 13ms after the dismiss, then "pip
                    // exit, restoring 962x1162 px at (800,326)", i.e. a full-size
                    // ordinary window flashing up as the PIP goes away. No
                    // ordering trick avoids it — the exit is caused BY the
                    // teardown — so WM has to be told which kind of exit it is.
                    onWm(() -> notifyPipClosing(taskId), 0);
                    // Then let the Shell's dismiss animation play before the task
                    // actually goes. Removing it immediately kills the leash the
                    // animation is running on, which is why the slide-out
                    // disappeared when this was made synchronous.
                    onWm(() -> closeDismissedTask(taskId), DISMISS_ANIMATION_MS);
                }

                @Override
                public void onPipShowMenu() {}

                @Override
                public void onEnterSplit() {}
            };

    /**
     * Stamp the Shell's PIP menu surface with the pinned task's id, so
     * SurfaceFlinger composites it into that task's virtual display.
     */
    private void adoptMenuSurface(String why) {
        if (mPipTaskId == ActivityTaskManager.INVALID_TASK_ID) return;
        final SurfaceControl sc = mMenuController.getSurfaceControl();
        if (sc == null || !sc.isValid()) {
            Slog.i(TAG, "stamp(" + why + "): no menu surface yet");
            return;
        }
        final SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        try {
            t.setMetadata(sc, SurfaceControl.METADATA_TASK_ID, mPipTaskId);
            // ...and declare what it IS, or joining it to the task lets it
            // define the task's size.
            //
            // The compositor already refuses to let an overlay enlarge the task
            // footprint (CompositionEngineArc's arcIsTaskOverlayLayer, whose
            // comment names this menu as the case it exists for) — but it
            // classifies by the layer's window type, and this layer has none.
            // `PipMenuController.getPipMenuLayoutParams` does say
            // TYPE_APPLICATION_OVERLAY, but the menu is added through the
            // Shell's WINDOWLESS SystemWindows, so WMS never creates the
            // SurfaceControl and never stamps METADATA_WINDOW_TYPE. Measured on
            // device with persist.fydroid.log_task_bounds: `taskBounds:
            // taskId=170 layer=PipMenuView#100 ... sub=0` — unclassified, in
            // the main union, free to set the task's size. Every time the Shell
            // moved the menu to its own rectangle the task VD followed it
            // (900x506 -> 928x577 on rotation, -> 2532x1572 on entry), and the
            // window's content was then projected into a window of a different
            // size.
            //
            // METADATA_WINDOW_TYPE is settable by transaction and is in
            // SurfaceFlinger's generic-metadata key map, so this reaches
            // arcWindowType() the same way a WMS-created window's does.
            t.setMetadata(sc, SurfaceControl.METADATA_WINDOW_TYPE,
                          WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            t.apply();
            Slog.i(TAG, "stamp(" + why + "): pip menu surface " + sc
                    + " -> taskId " + mPipTaskId + ", shell bounds="
                    + mBoundsState.getBounds());
        } catch (Exception e) {
            Slog.w(TAG, "stamp(" + why + "): failed", e);
        } finally {
            t.close();
        }
        // Placement is NOT done here. mResync is the single writer of the
        // menu's geometry — see its comment. This used to size the menu from
        // the SHELL's rectangle while mResync sized it from the TASK, and two
        // writers disagreeing by a pixel or a rotation is exactly the menu-size
        // drift that outlived every other fix.
        onWmDebounced(mResync, 0);
    }

}

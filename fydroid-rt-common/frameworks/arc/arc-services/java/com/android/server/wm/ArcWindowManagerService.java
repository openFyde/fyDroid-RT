package com.android.server.wm;

import android.app.ActivityManager;
import android.app.IApplicationThread;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.SurfaceControl;
import com.android.internal.util.ToBooleanFunction;
import com.android.server.DisplayThread;
import com.android.server.LocalServices;
import com.android.server.arc.input_method_manager.ArcInputMethodManagerService;
import com.android.server.arc.system_ui.ArcSystemUiService;
import com.android.server.SystemService;
import com.android.server.inputmethod.InputMethodManagerInternal;
import io.fydeos.arc.wm.IArcTaskController;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * ARC window manager service providing task management, resize lock,
 * PIP support, activation tracking, and display density management.
 * Publishes "arc_window_manager" binder service.
 */
class ArcWindowManagerService extends IArcWindowManager.Stub {
    private static final String TAG = "ArcWindowManagerService";
    private static final String PLAY_STORE_PACKAGE = "com.android.vending";
    private static final String NETFLIX_PACKAGE = "com.netflix.mediaclient";
    private static final String SUPPRESS_CAPTION_BACK_BUTTON =
            "WindowManagerPreference:SuppressWindowControlNavigationButton";
    private static final int DEFAULT_WAIT_TIMEOUT_MS = 500;
    // Delay after a task is removed before checking whether the app is left with
    // only a Presentation window (the activity finishes destroying async).
    private static final int FORCE_STOP_ORPHAN_PRESENTATION_DELAY_MS = 700;
    private static final int WAIT_FOR_MAIN_WINDOW_DELAY_MS = 200;
    private static final int WAIT_FOR_MAIN_WINDOW_MAX_ATTEMPTS = 10;

    private final WindowManagerService mWindowManagerService;
    private final WindowManagerInternal mWindowManagerInternal;
    private final Handler mHandler;
    private final RemoteCallbackList<ITaskOrientationListener> mTaskOrientationListeners =
            new RemoteCallbackList<>();
    private final RemoteCallbackList<ILockTaskListener> mLockTaskListeners =
            new RemoteCallbackList<>();
    private final SparseIntArray mWaitForMainAppWindowDrawnCounter = new SparseIntArray();
    // Per-task caption colour the app asked for, 0 = none (fall back to the
    // ChromeOS header). Kept so a colour scheme change can re-derive the
    // unfocused half, which depends on the scheme; pruned on task removal so
    // it cannot outgrow the task list.
    private final SparseIntArray mTaskCaptionColors = new SparseIntArray();
    private int mUserId = -10000;

    // The task currently in WINDOWING_MODE_PINNED (PIP), or INVALID_TASK_ID.
    // onActivityUnpinned() carries no task id, and WM allows exactly one pinned
    // root task at a time, so remembering the last pinned task is what lets the
    // unpin be attributed. Written only from the task-stack listener, which
    // TaskStackListener delivers on a single handler thread.
    private static final int INVALID_TASK_ID = -1;
    private int mPinnedTaskId = INVALID_TASK_ID;
    // The pinned task ArcSystemUI has told us is closing (menu dismiss), so
    // its unpin is suppressed rather than pushed to the compositor as an exit.
    private int mClosingPipTaskId = INVALID_TASK_ID;

    // FydeOS task-control binder, implemented with the real ActivityTaskManager /
    // WindowManager APIs so the native wayland composer (SurfaceFlinger) no longer
    // hardcodes IActivityTaskManager transaction codes (which shifted between AOSP
    // versions and called the wrong methods). Published as "arc_task_controller"; the
    // composer calls it via the generated BpArcTaskController (auto-correct codes).
    final IArcTaskController.Stub mTaskController = new IArcTaskController.Stub() {
        @Override
        public boolean resizeTask(int taskId, int left, int top, int right, int bottom,
                int resizeMode) {
            try {
                return mWindowManagerService.mAtmService.resizeTask(
                        taskId, new Rect(left, top, right, bottom), resizeMode);
            } catch (Exception e) {
                Slog.e(TAG, "ArcTaskController.resizeTask failed", e);
                return false;
            }
        }

        @Override
        public boolean setTaskBoundsUnchecked(int taskId, int left, int top, int right,
                int bottom) {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null) return false;
                try {
                    task.setBounds(new Rect(left, top, right, bottom));
                    // Re-place the task's own surface. Task.setBounds() writes
                    // the bounds but nothing on this path refreshes
                    // mLastSurfacePosition, so the Task layer keeps whatever
                    // position WM gave it when it first resolved the pinned
                    // rectangle — measured (1632,1066) against bounds that had
                    // since become (0,0,900,506).
                    //
                    // A uniform offset is invisible while a task is alone in
                    // its virtual display (CompositionEngineArc anchors the
                    // composition at the footprint origin, so everything shifts
                    // together), which is why it went unnoticed. It stops being
                    // invisible the moment a layer laid out in WM's coordinates
                    // joins the task — ArcSystemUI's PIP menu did, and the
                    // footprint union blew up from 900x506 to 2161x1159.
                    // It is also exactly the origin desync base patch 0042
                    // exists to prevent for input.
                    //
                    // Written straight onto the task's SurfaceControl rather
                    // than through updateSurfacePosition(): that helper is
                    // gated on the container not being organized and having no
                    // animation or freezer leash, and on this path it returned
                    // without doing anything (measured: mLastSurfacePosition
                    // stayed at the sentinel it had been poisoned with). The
                    // position we want is not a guess either — it is exactly
                    // what getRelativePosition() would compute, since the task's
                    // parent display area is at the display origin.
                    pinTaskSurfaceTo(taskId, task, left, top);
                    return true;
                } catch (Exception e) {
                    Slog.e(TAG, "ArcTaskController.setTaskBoundsUnchecked failed", e);
                    return false;
                }
            }
        }

        @Override
        public void setArcActivated(boolean activated) {
            // Only the losing edge does anything. Regaining activation always
            // comes with a setFocusedTask for the window Chrome activated, and
            // that moves focus back through the ordinary path.
            if (activated) return;
            try {
                // WindowManager has no way to represent "none of my windows are
                // focused": DisplayContent#findFocusedWindow always settles on
                // the topmost key-receiving window, and there is no unfocus
                // request to make of it. So instead of trying to clear focus,
                // give it something harmless to hold — the ARC IME placeholder,
                // which is 1x1, translucent, excluded from recents, and asks for
                // no keyboard of its own.
                //
                // Focusing it is what makes the app the user clicked away from
                // let go: it gets onWindowFocusChanged(false) through the normal
                // path, stops drawing its caret, and its IME hides because the
                // newly focused window has no text editor. Nothing is paused or
                // hidden — the app's window is still on screen beside Chrome's,
                // and background playback is untouched.
                ArcInputMethodManagerService.focusPlaceholder(
                        mWindowManagerService.mContext);
                Slog.i(TAG, "ARC deactivated; focus parked on the IME placeholder");
            } catch (Exception e) {
                Slog.e(TAG, "ArcTaskController.setArcActivated failed", e);
            }
        }

        @Override
        public boolean removeTask(int taskId) {
            // Capture the app owning this task before it is removed, so we can
            // clean up any Presentation windows it leaves behind on a secondary
            // display. Some apps (e.g. some POS apps) never dismiss their external-
            // display Presentation when their activity is destroyed; the
            // orphaned window keeps the process alive and lingers on the HDMI
            // panel after the user closes the app via the window's X button.
            String pkg = null;
            int userId = 0;
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task != null && task.realActivity != null) {
                    pkg = task.realActivity.getPackageName();
                    userId = task.mUserId;
                }
            }
            boolean removed;
            try {
                removed = mWindowManagerService.mAtmService.removeTask(taskId);
            } catch (Exception e) {
                Slog.e(TAG, "ArcTaskController.removeTask failed", e);
                return false;
            }
            if (removed && pkg != null) {
                // Defer: the activity is finished asynchronously, so its own
                // window is still up right after removeTask returns. Wait for it
                // to tear down, then force-stop only if the app is left with a
                // Presentation and no remaining activity window.
                final String fPkg = pkg;
                final int fUserId = userId;
                mHandler.postDelayed(
                        () -> forceStopIfOnlyPresentationLeft(fPkg, fUserId),
                        FORCE_STOP_ORPHAN_PRESENTATION_DELAY_MS);
            }
            return removed;
        }

        @Override
        public void moveTaskToDisplay(int taskId, int displayId) {
            try {
                mWindowManagerService.mAtmService.moveRootTaskToDisplay(taskId, displayId);
            } catch (Exception e) {
                Slog.e(TAG, "ArcTaskController.moveTaskToDisplay failed", e);
            }
        }

        @Override
        public void setFocusedTask(int taskId) {
            try {
                mWindowManagerService.mAtmService.setFocusedTask(taskId);
                // Push the resulting z-order to the InputDispatcher before
                // returning. Container tasks all sit at origin (0,0,W,H) with an
                // effectively unbounded touchable region — the on-screen
                // position belongs to Chrome, not WM — so the dispatcher cannot
                // separate two side-by-side snapped tasks by coordinates and
                // delivers every touch to whichever is topmost. The composer
                // calls this the moment a task is touched and then injects the
                // event, so the new order has to be in place by the time this
                // returns; otherwise the first tap on the other half of a split
                // still lands in the previously focused task and only the second
                // one works. syncInputTransactions() applies the input windows
                // synchronously (waitForAnimations=false: we only need the
                // window list, not the animation to finish).
                mWindowManagerService.syncInputTransactions(false /*waitForAnimations*/);
            } catch (Exception e) {
                Slog.e(TAG, "ArcTaskController.setFocusedTask failed", e);
            }
        }

        @Override
        public boolean setTaskWindowingMode(int taskId, int windowingMode, boolean toTop) {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null) return false;
                task.setWindowingMode(windowingMode);
                // A FULLSCREEN task must inherit the display configuration so it
                // re-layouts on rotation. Clear any explicit bounds override left
                // over from a prior snap/resize — otherwise the stale bounds pin
                // the task size and it keeps the old orientation's geometry after
                // the display rotates (content fills only part of the buffer).
                // Task.setBounds(null) resets the task to fullscreen.
                if (windowingMode
                        == android.app.WindowConfiguration.WINDOWING_MODE_FULLSCREEN) {
                    task.setBounds(null);
                }
                return true;
            }
        }

        @Override
        public boolean isTaskResizable(int taskId) {
            return ArcWindowManagerService.this.isTaskResizable(taskId);
        }

        @Override
        public int[] getTaskMinSize(int taskId) {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null) return new int[] {-1, -1};
                return new int[] {task.mMinWidth, task.mMinHeight};
            }
        }
    };

    // --- LifeCycle (loaded by SystemServiceManager) ---

    public static class LifeCycle extends SystemService {
        private ArcWindowManagerService mService;

        public LifeCycle(Context context) {
            super(context);
        }

        @Override
        public void onStart() {
            mService = new ArcWindowManagerService(
                    (WindowManagerService) ServiceManager.getService("window"));
            ServiceManager.addService("arc_window_manager", mService);
            ServiceManager.addService("arc_task_controller", mService.mTaskController);
        }

        @Override
        public void onUserStarting(TargetUser user) {
            mService.mUserId = user.getUserIdentifier();
        }

        @Override
        public void onUserSwitching(TargetUser from, TargetUser to) {
            mService.mUserId = to.getUserIdentifier();
        }
    }

    // --- Constructor ---

    private ArcWindowManagerService(WindowManagerService wms) {
        mWindowManagerService = wms;
        mWindowManagerInternal = LocalServices.getService(WindowManagerInternal.class);
        mHandler = new Handler(DisplayThread.getHandler().getLooper());

        try {
            ActivityManager.getService().registerTaskStackListener(
                    new TaskStackListener() {
                        @Override
                        public void onTaskRequestedOrientationChanged(int taskId,
                                int orientation) {
                            broadcastTaskOrientationChanged(taskId, orientation);
                        }

                        @Override
                        public void onTaskCreated(int taskId,
                                ComponentName componentName) {
                            notifyTaskSessionId(taskId);
                            maybePinKioskTask(taskId);
                        }

                        @Override
                        public void onTaskDescriptionChanged(
                                ActivityManager.RunningTaskInfo taskInfo) {
                            notifyTaskCaptionColor(taskInfo);
                        }

                        @Override
                        public void onActivityPinned(String packageName, int userId,
                                int taskId, int rootTaskId) {
                            // The activity entered WINDOWING_MODE_PINNED — by its
                            // own enterPictureInPictureMode(), by auto-PIP, or via
                            // requestPictureInPictureMode below. Only the
                            // compositor can turn that into a ChromeOS PIP window,
                            // and it has no other way to learn about it: the
                            // windowing mode lives entirely on this side.
                            mPinnedTaskId = taskId;
                            pushTaskPipState(taskId, true);
                            // Put the pinned rectangle back at the display
                            // origin, where every ARC task lives (base patch
                            // 0042). WM resolves a pinned task to a rectangle
                            // carrying its on-screen offset — measured
                            // Rect(1632,1066-2532,1572) — and the compositor
                            // only zeroes it as a SIDE EFFECT of resizing the
                            // task. Re-entering PIP at a size the window
                            // already has produces no geometry change, so no
                            // resize, so the offset stayed: ArcSystemUI then
                            // laid its menu out 1632px away from the task's own
                            // layers, which put the reveal target outside the
                            // window and blew the task footprint up again.
                            // Cheap and idempotent, so it runs on every pin.
                            zeroTaskOrigin(taskId);
                        }

                        @Override
                        public void onActivityUnpinned() {
                            // No taskId in this callback — WM allows exactly one
                            // pinned root task at a time (Task#setWindowingMode
                            // dismisses any existing PIP before creating the next),
                            // so the one we recorded is the one that just left.
                            final int taskId = mPinnedTaskId;
                            mPinnedTaskId = INVALID_TASK_ID;
                            if (taskId == INVALID_TASK_ID) return;
                            // A dismiss (close) and an expand both arrive here;
                            // only the dismiss must NOT be pushed as an exit.
                            // ArcSystemUI tells us which via notifyPipClosing()
                            // before the Shell's dismiss animation runs, so the
                            // slide-out can still play while the compositor is
                            // spared restoring a window that is about to die.
                            if (mClosingPipTaskId == taskId) {
                                mClosingPipTaskId = INVALID_TASK_ID;
                                Slog.i(TAG, "task " + taskId + " unpinned by a CLOSE"
                                        + " — not pushing a pip exit");
                                return;
                            }
                            // A task that is going away must NOT be pushed as a
                            // PIP exit. WM tears a pinned task down THROUGH the
                            // windowing-mode change, so closing a PIP fires this
                            // callback after the task is already gone — and the
                            // compositor's exit path then puts the caption back
                            // and resizes the window to its pre-PIP rectangle,
                            // which flashes a full-size ordinary window on screen
                            // for the frame or two before the surface dies.
                            // Measured on device: removeTask -> true, then 13ms
                            // later "android pip=0", then "pip exit, restoring
                            // 962x1162 px at (800,326)". No ordering trick fixes
                            // it — the exit is caused BY the removal — so the
                            // discriminator has to be whether the task still
                            // exists. onTaskRemoved does the rest of the cleanup.
                            synchronized (mWindowManagerService.mGlobalLock) {
                                if (getTaskById(taskId) == null) {
                                    Slog.i(TAG, "task " + taskId
                                            + " unpinned but already gone — not pushing"
                                            + " a pip exit (it is closing, not expanding)");
                                    return;
                                }
                            }
                            pushTaskPipState(taskId, false);
                        }

                        @Override
                        public void onTaskRemoved(int taskId) {
                            synchronized (mTaskCaptionColors) {
                                mTaskCaptionColors.delete(taskId);
                            }
                            // A PIP task killed outright never sends
                            // onActivityUnpinned, so clear the latch here too or
                            // the next unpin would push a stale task id.
                            if (mPinnedTaskId == taskId) {
                                mPinnedTaskId = INVALID_TASK_ID;
                                pushTaskPipState(taskId, false);
                            }
                            // Tell the wayland service too — it keeps the
                            // colour as state so a task's window can read it
                            // at creation, and that map is only bounded if
                            // dead tasks are taken out of it.
                            pushTaskCaptionColor(taskId, 0);
                        }
                    });
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to register task stack listener", e);
        }

        ArcSystemUiService.addPaletteListener(this::repushTaskCaptionColors);

        // Disable TaskPositioner — ARC handles its own window positioning
        TaskPositioner.setFactory(new TaskPositioner.Factory() {
            @Override
            public TaskPositioner create(WindowManagerService wms2) {
                return null;
            }
        });
    }

    // --- Custom onTransact for applyTaskTransaction (tx 28) ---
    // AIDL can't represent SurfaceControl.Transaction (inner class), so we
    // intercept and handle manually.

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
            throws RemoteException {
        if (code == TRANSACTION_applyTaskTransaction) {
            data.enforceInterface("com.android.server.wm.IArcWindowManager");
            int taskId = data.readInt();
            SurfaceControl.Transaction transaction = null;
            if (data.readInt() != 0) {
                transaction = SurfaceControl.Transaction.CREATOR.createFromParcel(data);
            }
            applyTaskTransactionImpl(taskId, transaction);
            reply.writeNoException();
            return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    // --- Private helpers ---

    @Override
    public void notifyPipClosing(int taskId) {
        mClosingPipTaskId = taskId;
        Slog.i(TAG, "pip task " + taskId + " is closing");
    }

    private Task getTaskById(int taskId) {
        return mWindowManagerService.mRoot.anyTaskForId(taskId, 0);
    }

    /**
     * Put a task's own SurfaceControl where its bounds say it is.
     *
     * <p>Every ARC task is laid out at the display origin (base patch 0042), and
     * everything that shares its virtual display — the activity's windows, and
     * now ArcSystemUI's PIP pill — is positioned by WindowManager in that same
     * space. A pinned task broke the rule: WM's first pinned rectangle
     * (measured Rect(1632,1066-2532,1572)) stayed on the task's surface after
     * the compositor had rewritten the bounds to (0,0,900,506), and
     * {@code updateSurfacePosition} never reconciled the two.
     *
     * <p>While the task was alone in its virtual display the offset was
     * invisible: CompositionEngineArc anchors the composition at the footprint
     * origin, so a uniformly displaced task still composes correctly. The PIP
     * menu is what exposed it — a layer laid out at the real origin, unioned
     * with a task 1632px away, produced a 2161x1159 footprint of which the PIP
     * window showed only the top-left corner. The pill rendered; the app did
     * not.
     *
     * <p>Written directly rather than through {@code updateSurfacePosition}:
     * that helper refuses to run for an organized container or one holding an
     * animation or freezer leash, and on this path it returned without doing
     * anything at all.
     */
    /**
     * Move a task's rectangle to the display origin, keeping its size, and take
     * the surface with it. WM's own pinned-task resolution is the only thing in
     * the tree that produces an offset ARC task, and TaskLaunchParamsModifier
     * (base 0042) never sees that path.
     */
    private void zeroTaskOrigin(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return;
            final Rect b = task.getBounds();
            if (b.isEmpty()) return;
            if (b.left != 0 || b.top != 0) {
                Slog.i(TAG, "task " + taskId + " pinned at " + b.toShortString()
                        + " — moving to the origin");
                task.setBounds(new Rect(0, 0, b.width(), b.height()));
            }
            forceTaskSurfaceOrigin(task, 0, 0);
        }
    }

    /**
     * Put a task's surface where the bounds we have just written say it is, and
     * keep it there while the change animation runs.
     *
     * <p>Writing the bounds is only half of a move. {@code Task.setBounds()}
     * records the rectangle, but the surface follows only through
     * {@code updateSurfacePosition()}, which refuses to run for an organized
     * container or one holding an animation or freezer leash — both true of a
     * pinned task — and leaves {@code mLastSurfacePosition} claiming the surface
     * is already right, so nothing ever reconciles the two afterwards.
     *
     * <p>The delayed repeats are not belt-and-braces: the change animation ends
     * by re-running {@code updateSurfacePosition} itself, which stamps WM's own
     * idea of the position back about one frame later (measured 14ms), and
     * nothing calls it again after that.
     *
     * <p>Callers must hold the WM lock for the immediate write; the repeats take
     * it themselves.
     */
    private void pinTaskSurfaceTo(int taskId, Task task, int left, int top) {
        forceTaskSurfaceOrigin(task, left, top);
        for (int delayMs : new int[] {250, 800}) {
            mWindowManagerService.mH.postDelayed(() -> {
                synchronized (mWindowManagerService.mGlobalLock) {
                    Task t = getTaskById(taskId);
                    if (t != null) forceTaskSurfaceOrigin(t, left, top);
                }
            }, delayMs);
        }
    }

    private void forceTaskSurfaceOrigin(Task task, int left, int top) {
        final SurfaceControl sc = task.getSurfaceControl();
        if (sc == null || !sc.isValid()) return;
        // Written unconditionally rather than gated on mLastSurfacePosition:
        // that record and the surface have already been observed to disagree,
        // and a skipped write here is the bug this method exists to fix.
        final Point last = task.getLastSurfacePosition();
        final boolean changed = last.x != left || last.y != top;
        task.getSyncTransaction().setPosition(sc, left, top);
        last.set(left, top);
        mWindowManagerService.scheduleAnimationLocked();
        if (changed) {
            Slog.i(TAG, "task " + task.mTaskId + " surface origin -> (" + left + ","
                    + top + ") bounds=" + task.getBounds().toShortString());
        }
    }

    // If, after a task is removed, the owning app is left with a Presentation
    // window (external-display customer display) and no remaining activity
    // window, force-stop it — the app never dismissed its Presentation, so the
    // process and its window would otherwise linger on the secondary display.
    // Guarded on "no activity window" so closing one window of a multi-window
    // app does not kill its other windows.
    private void forceStopIfOnlyPresentationLeft(String pkg, int userId) {
        final AtomicBoolean hasPresentation = new AtomicBoolean(false);
        final AtomicBoolean hasActivityWindow = new AtomicBoolean(false);
        synchronized (mWindowManagerService.mGlobalLock) {
            mWindowManagerService.mRoot.forAllWindows(
                    (ToBooleanFunction<WindowState>) ws -> {
                        if (!pkg.equals(ws.getOwningPackage())) {
                            return false;
                        }
                        final int type = ws.mAttrs.type;
                        if (type == android.view.WindowManager.LayoutParams
                                .TYPE_PRESENTATION) {
                            hasPresentation.set(true);
                        } else if (type >= android.view.WindowManager.LayoutParams
                                        .FIRST_APPLICATION_WINDOW
                                && type <= android.view.WindowManager.LayoutParams
                                        .LAST_APPLICATION_WINDOW) {
                            hasActivityWindow.set(true);
                        }
                        return false; // scan all windows
                    }, true /* traverseTopToBottom */);
        }
        if (!hasPresentation.get() || hasActivityWindow.get()) {
            return;
        }
        long ident = Binder.clearCallingIdentity();
        try {
            ActivityManager.getService().forceStopPackage(pkg, userId);
            Slog.i(TAG, "removeTask: force-stopped " + pkg
                    + " (orphan Presentation left on secondary display)");
        } catch (RemoteException e) {
            Slog.e(TAG, "removeTask: forceStopPackage failed for " + pkg, e);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private boolean isInputMethodBehindDimmer() {
        final AtomicBoolean found = new AtomicBoolean(false);
        synchronized (mWindowManagerService.mGlobalLock) {
            mWindowManagerService.mRoot.forAllWindows((ToBooleanFunction<WindowState>) ws -> {
                if (ws.mIsImWindow) {
                    return true; // Stop: IME found first, not behind dimmer
                }
                if ((ws.mAttrs.flags & 2) != 0 && ws.isVisible()) {
                    found.set(true);
                    return true; // Stop: dim window found before IME
                }
                return false;
            }, true);
        }
        return found.get();
    }

    private void broadcastTaskOrientationChanged(int taskId, int orientation) {
        int count = mTaskOrientationListeners.beginBroadcast();
        for (int i = 0; i < count; i++) {
            try {
                mTaskOrientationListeners.getBroadcastItem(i)
                        .onTaskOrientationChanged(taskId, orientation);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to notify task orientation change " + taskId, e);
            }
        }
        mTaskOrientationListeners.finishBroadcast();
    }

    /**
     * Extract session ID from task intent and push to native WaylandBinderService.
     * Called on task creation so SurfaceFlinger can resolve session IDs without dumpsys.
     */
    // --- Per-task caption colour -------------------------------------------
    //
    // The ChromeOS header (pushed globally by ArcSystemUiService) is the
    // fallback. When an app declares its own colour the caption should take
    // that instead, so the title bar reads as part of the app rather than as a
    // strip of system chrome bolted above it.

    /**
     * The colour an app wants its caption painted, or 0 if it has not said.
     *
     * primaryColor first: it is what ChromeOS itself uses for an ARC caption,
     * and it is already the app's background in practice — Settings reports
     * #F2F0F4, which is exactly what its window is painted. backgroundColor is
     * the documented window background and covers an app that set one without
     * a primary.
     */
    private static int appCaptionColor(ActivityManager.TaskDescription td) {
        if (td == null) return 0;
        int color = opaqueOrNone(td.getPrimaryColor());
        if (color == 0) color = opaqueOrNone(td.getBackgroundColor());
        return color;
    }

    /**
     * 0 unless the colour is fully opaque. A TaskDescription colour of 0 means
     * "unset", and ash forces the frame opaque anyway — so a translucent value
     * would be painted at full strength, which is not what the app asked for.
     * Treat anything short of opaque as unset and keep the ChromeOS header.
     */
    private static int opaqueOrNone(int color) {
        return ((color >>> 24) == 0xFF) ? color : 0;
    }

    private void notifyTaskCaptionColor(ActivityManager.RunningTaskInfo taskInfo) {
        if (taskInfo == null || taskInfo.taskId <= 0) return;
        final int color = appCaptionColor(taskInfo.taskDescription);
        synchronized (mTaskCaptionColors) {
            if (color == 0) {
                mTaskCaptionColors.delete(taskInfo.taskId);
            } else {
                mTaskCaptionColors.put(taskInfo.taskId, color);
            }
        }
        pushTaskCaptionColor(taskInfo.taskId, color);
    }

    /**
     * Hand a task's caption colours to the wayland service. A colour of 0
     * clears the override, putting the task back on the ChromeOS header.
     *
     * The unfocused half is derived here rather than natively because the
     * blend target is a palette tone, and the palette lives on this side.
     */
    private void pushTaskCaptionColor(int taskId, int color) {
        final int active = color;
        final int inactive = (color == 0) ? 0
                : ArcSystemUiService.unfocusedFrom(color, ArcSystemUiService.isDarkTheme());
        try {
            IBinder wayland = ServiceManager.getService("Wayland");
            if (wayland == null) return;
            Parcel data = Parcel.obtain();
            try {
                data.writeInterfaceToken("org.chromium.arc.wayland.IWaylandService");
                data.writeInt(taskId);
                data.writeInt(active);
                data.writeInt(inactive);
                wayland.transact(52, data, null, IBinder.FLAG_ONEWAY);
                Slog.i(TAG, String.format("task %d caption colour -> #%08X/#%08X",
                        taskId, active, inactive));
            } finally {
                data.recycle();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to push caption colour for task " + taskId, e);
        }
    }

    /**
     * Tell the wayland service a task entered or left WINDOWING_MODE_PINNED, so
     * it can ask ash for a real ChromeOS PIP window (and take it back again).
     *
     * One-way and best-effort, like the caption colour above: if the wayland
     * service is not up yet there is no PIP window to manage either, and the
     * next transition re-states it.
     */
    private void pushTaskPipState(int taskId, boolean inPip) {
        // The PIP window's SIZE originates here, not in ash. ash's PipPositioner
        // only chooses where a PIP window sits (the snap fraction) and preserves
        // whatever size the client reports; upstream it is the WM Shell's
        // PipBoundsAlgorithm that decides the size and pushes it. We have no
        // shell, but WM has still computed a pinned-task rectangle honouring the
        // activity's PictureInPictureParams aspect ratio — so send that, and the
        // compositor gives it to ash as the window bounds.
        //
        // Without it the window keeps its pre-PIP size and only moves: measured
        // on device as a 742x1318 window holding a task Android had already
        // resized to 900x506.
        // Deliberately NOT read here when entering PIP: onActivityPinned is
        // delivered from the end of moveActivityToPinnedRootTask, and WM has
        // not applied the pinned rectangle yet — the task still reports its
        // pre-PIP size (measured on device: 742x1318 here, 900x506 once WM has
        // resolved the pinned configuration). Send the state now so the
        // compositor can ask ash for a PIP window and suppress input; the size
        // follows from the config listener below, which fires when WM has
        // actually applied the rectangle rather than at a guessed delay.
        Rect bounds = null;
        if (inPip) {
            // Capture the rectangle to come back to, NOW. ash keeps no restore
            // bounds for a client-controlled (ARC) window — ClientControlledState
            // never calls SaveCurrentBoundsForRestore and states plainly that
            // "Client is responsible for adjusting bounds" — so restore() changes
            // only the STATE and we must supply the rectangle ourselves.
            //
            // This instant is the one moment it is trustworthy: WM has not yet
            // applied the pinned rectangle, and the compositor has not yet
            // written any PIP-sized bounds back to the task. Reading it later
            // (mLastNonFullscreenBounds, or the task at unpin) returns a value
            // our own PIP sizing has already polluted — and because fydroid
            // persists task bounds, that corruption survives across launches.
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task != null && !task.getBounds().isEmpty()) {
                    mPipRestoreBounds.set(task.getBounds());
                }
            }
            watchPipTaskBounds(taskId);
        } else {
            unwatchPipTaskBounds();
            // Leaving PIP: send the rectangle to come back to. The compositor
            // cannot work this out for itself — the PIP size it handed ash IS
            // the window's bounds by now, so restore() alone leaves the window
            // PIP-sized, and remembering a "before" size in the compositor is
            // unreliable (an app that PIPs from onCreate is still mid-launch).
            // The rectangle captured on the way IN. Nothing read at this
            // moment is usable: the task has not settled (it reads as the full
            // display, 2560x1600) and mLastNonFullscreenBounds has been
            // overwritten by our own PIP-era task sizing.
            if (!mPipRestoreBounds.isEmpty()) {
                bounds = new Rect(mPipRestoreBounds);
                mPipRestoreBounds.setEmpty();
            }
        }
        try {
            IBinder wayland = ServiceManager.getService("Wayland");
            if (wayland == null) return;
            Parcel data = Parcel.obtain();
            try {
                data.writeInterfaceToken("org.chromium.arc.wayland.IWaylandService");
                data.writeInt(taskId);
                data.writeInt(inPip ? 1 : 0);
                // Always four ints so the parcel is fixed-size; all zero when
                // leaving PIP or when the task has gone.
                data.writeInt(bounds != null ? bounds.left : 0);
                data.writeInt(bounds != null ? bounds.top : 0);
                data.writeInt(bounds != null ? bounds.right : 0);
                data.writeInt(bounds != null ? bounds.bottom : 0);
                wayland.transact(53, data, null, IBinder.FLAG_ONEWAY);
                Slog.i(TAG, "task " + taskId + " pip=" + inPip + " bounds=" + bounds
                        + " -> wayland");
            } finally {
                data.recycle();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to push pip state for task " + taskId, e);
        }
    }

    /**
     * Watch the pinned task's configuration so we learn its real rectangle.
     *
     * WM owns the PIP rectangle — it computes one honouring the activity's
     * PictureInPictureParams aspect ratio (measured 900x506 for 16:9 on a
     * 2560x1600 panel) — but applies it *after* onActivityPinned. There is no
     * bounds-changed callback on TaskStackListener, and a fixed settle delay is
     * a guess that was still reading the pre-PIP size at 200ms.
     *
     * ConfigurationContainerListener#onMergedOverrideConfigurationChanged is the
     * real signal, and it needs no framework patch: this class lives in
     * com.android.server.wm, so WindowContainer#registerWindowContainerListener
     * is in scope. Registering also dispatches the current configuration
     * immediately, so a rectangle that has already landed is not missed.
     */
    private WindowContainerListener mPipBoundsListener;
    private int mPipBoundsWatchedTaskId = INVALID_TASK_ID;
    private final Rect mLastPushedPipBounds = new Rect();
    // The task's rectangle immediately before it entered PIP — the size to
    // restore the window to on the way out. See pushTaskPipState.
    private final Rect mPipRestoreBounds = new Rect();

    private void watchPipTaskBounds(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            if (mPipBoundsWatchedTaskId == taskId) return;
            unwatchPipTaskBoundsLocked();
            Task task = getTaskById(taskId);
            if (task == null) return;
            mPipBoundsWatchedTaskId = taskId;
            mLastPushedPipBounds.setEmpty();
            mPipBoundsListener = new WindowContainerListener() {
                @Override
                public void onMergedOverrideConfigurationChanged(Configuration config) {
                    // Called with the WM lock held, from deep inside a
                    // configuration cascade — do not re-enter WM or block here.
                    // The binder push is one-way, but hop to our handler anyway
                    // so nothing runs under the lock.
                    final Rect b = new Rect(config.windowConfiguration.getBounds());
                    mHandler.post(() -> pushTaskPipBounds(taskId, b));
                }

                @Override
                public void onRemoved() {
                    mHandler.post(() -> unwatchPipTaskBounds());
                }
            };
            task.registerWindowContainerListener(mPipBoundsListener);
            Slog.i(TAG, "watching pip bounds for task " + taskId);
        }
    }

    private void unwatchPipTaskBounds() {
        synchronized (mWindowManagerService.mGlobalLock) {
            unwatchPipTaskBoundsLocked();
        }
    }

    private void unwatchPipTaskBoundsLocked() {
        if (mPipBoundsListener == null) return;
        Task task = getTaskById(mPipBoundsWatchedTaskId);
        if (task != null) {
            task.unregisterWindowContainerListener(mPipBoundsListener);
        }
        Slog.i(TAG, "stopped watching pip bounds for task " + mPipBoundsWatchedTaskId);
        mPipBoundsListener = null;
        mPipBoundsWatchedTaskId = INVALID_TASK_ID;
        mLastPushedPipBounds.setEmpty();
    }

    /**
     * Send the pinned task's rectangle — the PIP window's size.
     *
     * Re-sends the state flag alongside it so the parcel stays one fixed shape;
     * the compositor treats a repeat as a size update and only asks ash for PIP
     * once.
     */
    private void pushTaskPipBounds(int taskId, Rect bounds) {
        if (bounds == null || bounds.isEmpty()) return;
        // The cascade reports the same configuration repeatedly.
        if (bounds.equals(mLastPushedPipBounds)) return;
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            // Gone, or already out of PIP again — nothing to say.
            if (task == null || !task.inPinnedWindowingMode()) return;
        }
        mLastPushedPipBounds.set(bounds);
        try {
            IBinder wayland = ServiceManager.getService("Wayland");
            if (wayland == null) return;
            Parcel data = Parcel.obtain();
            try {
                data.writeInterfaceToken("org.chromium.arc.wayland.IWaylandService");
                data.writeInt(taskId);
                data.writeInt(1);
                data.writeInt(bounds.left);
                data.writeInt(bounds.top);
                data.writeInt(bounds.right);
                data.writeInt(bounds.bottom);
                wayland.transact(53, data, null, IBinder.FLAG_ONEWAY);
                Slog.i(TAG, "task " + taskId + " pip bounds=" + bounds + " -> wayland");
            } finally {
                data.recycle();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to push pip bounds for task " + taskId, e);
        }
    }

    /**
     * Re-send every task's colours after the ChromeOS scheme changed. Only the
     * unfocused half actually moves — the app's colour is its own — but that
     * half is blended against a scheme-dependent neutral, so leaving it alone
     * would strand an unfocused caption in the previous scheme's wash.
     */
    private void repushTaskCaptionColors() {
        final SparseIntArray snapshot;
        synchronized (mTaskCaptionColors) {
            snapshot = mTaskCaptionColors.clone();
        }
        for (int i = 0; i < snapshot.size(); i++) {
            pushTaskCaptionColor(snapshot.keyAt(i), snapshot.valueAt(i));
        }
    }

    private void notifyTaskSessionId(int taskId) {
        int sessionId = 0;
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task != null && task.intent != null) {
                String id = task.intent.getIdentifier();
                if (id != null) {
                    String prefix = "window_session_id=";
                    int idx = id.indexOf(prefix);
                    if (idx >= 0) {
                        int start = idx + prefix.length();
                        int end = start;
                        while (end < id.length() && Character.isDigit(id.charAt(end))) end++;
                        if (end > start) {
                            try {
                                sessionId = Integer.parseInt(id.substring(start, end));
                            } catch (NumberFormatException e) { /* ignore */ }
                        }
                    }
                }
            }
        }

        try {
            IBinder wayland = ServiceManager.getService("Wayland");
            if (wayland != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken("org.chromium.arc.wayland.IWaylandService");
                    data.writeInt(taskId);
                    data.writeInt(sessionId);
                    wayland.transact(41, data, reply, 0);
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to notify task session ID", e);
        }
    }

    private Point getTaskMinSizeInternal(Task task, int densityDpi) {
        if (task.realActivity == null) return null;
        try {
            ActivityInfo info = mWindowManagerService.mContext.getPackageManager()
                    .getActivityInfo(task.realActivity, 0);
            float density = densityDpi / 160.0f;
            int minW = -1;
            int minH = -1;
            if (info != null && info.windowLayout != null) {
                ActivityInfo.WindowLayout wl = info.windowLayout;
                // minWidth/minHeight are in dp; convert to pixels at display density
                if (wl.minWidth >= 0) {
                    minW = (int) ((wl.minWidth * density) + 0.5f);
                    task.mMinWidth = minW;
                }
                if (wl.minHeight >= 0) {
                    minH = (int) ((wl.minHeight * density) + 0.5f);
                    task.mMinHeight = minH;
                }
            }
            int defaultMin = (int) ((mWindowManagerService.mRoot
                    .mDefaultMinSizeOfResizeableTaskDp * density) + 0.5f);
            if (minW < 0) minW = defaultMin;
            if (minH < 0) minH = defaultMin;
            return new Point(minW, minH);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e(TAG, "Cannot find package to get WindowLayout", e);
            return null;
        }
    }

    private void appendWindowLayoutString(StringBuilder sb,
            ActivityInfo.WindowLayout wl) {
        if (wl.hasSpecifiedSize()) {
            if (wl.width > 0 || wl.height > 0) {
                sb.append(" layoutSize(").append(wl.width).append("x")
                        .append(wl.height).append(")");
            }
            if (wl.widthFraction > 0.0f || wl.heightFraction > 0.0f) {
                sb.append(" layoutSizeF(").append(wl.widthFraction).append("x")
                        .append(wl.heightFraction).append(")");
            }
        }
        int hGravity = wl.gravity & 7;
        int vGravity = wl.gravity & 112;
        if (hGravity != 0) {
            sb.append(hGravity == 3 ? " gLeft" : hGravity == 5 ? " gRight" : " gHCenter");
        }
        if (vGravity != 0) {
            sb.append(vGravity == 48 ? " gTop" : vGravity == 80 ? " gBottom" : " gVCenter");
        }
    }

    // --- IArcWindowManager implementation ---

    // 1
    @Override
    public void registerTaskOrientationListener(ITaskOrientationListener listener) {
        mTaskOrientationListeners.register(listener);
    }

    // 2
    @Override
    public boolean moveTaskToBack(int taskId) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.MANAGE_ACTIVITY_STACKS", "moveTaskToBack()")) {
            throw new SecurityException("Requires MANAGE_ACTIVITY_STACKS permission");
        }
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null) {
                    Slog.w(TAG, "moveTaskToBack: taskId=" + taskId + " not found");
                    return false;
                }
                task.getRootTask().mTaskSupervisor.mUserLeaving = true;
                ActivityRecord top = task.getRootTask().getTopNonFinishingActivity();
                if (top != null) {
                    top.supportsEnterPipOnTaskSwitch = true;
                }
                return task.getRootTask().moveTaskToBack(task);
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 3
    @Override
    public SurfaceControl getTaskSurface(int taskId) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.REAL_GET_TASKS", "getTaskSurface()")) {
            throw new SecurityException("Requires REAL_GET_TASKS permission");
        }
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task != null && task.getSurfaceControl() != null) {
                SurfaceControl sc = new SurfaceControl();
                sc.copyFrom(task.getSurfaceControl(),
                        "ArcWindowManagerService.getTaskSurface");
                return sc;
            }
            return null;
        }
    }

    // 4
    @Override
    public int getTaskRequestedWindowingMode(int taskId) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.REAL_GET_TASKS",
                "getTaskRequestedWindowingMode()")) {
            throw new SecurityException("Requires REAL_GET_TASKS permission");
        }
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return 0;
            return task.getRootTask().getRequestedOverrideConfiguration()
                    .windowConfiguration.getWindowingMode();
        }
    }

    // 5
    @Override
    public boolean isUidPresentInTask(int taskId, int uid) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.REAL_GET_TASKS", "getTaskUid()")) {
            throw new SecurityException("Requires REAL_GET_TASKS permission");
        }
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return false;
            return task.isUidPresent(uid);
        }
    }

    // 6
    @Override
    public void requestActivate(int taskId, int flags, boolean suppressCallback,
            boolean force) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.MANAGE_ACTIVITY_STACKS", "requestActivate()")) {
            throw new SecurityException("Requires MANAGE_ACTIVITY_STACKS permission");
        }
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                mWindowManagerService.mAtmService.moveTaskToFront(
                        (IApplicationThread) null, (String) null,
                        taskId, flags, (Bundle) null);
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 7
    @Override
    public boolean setTaskResizing(int taskId, boolean resizing) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.MANAGE_ACTIVITY_STACKS", "setTaskResizing()")) {
            throw new SecurityException("Requires MANAGE_ACTIVITY_STACKS permission");
        }
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null) return false;
                int mode = task.getWindowingMode() == 5 ? 0 : 1;
                task.setDragResizing(resizing, mode);
                if (mode == 1 && resizing) {
                    mWindowManagerService.mRoot.performSurfacePlacement();
                }
                return true;
            }
        } catch (IllegalArgumentException e) {
            Slog.wtf(TAG, "setTaskResizing: Failed calling setDragResizing", e);
            return false;
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 8
    @Override
    public boolean isTaskResizing(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return false;
            return task.isDragResizing();
        }
    }

    // 9
    @Override
    public boolean isTaskResizable(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return false;
            return task.isResizeable() && task.mResizeMode != 0;
        }
    }

    // 10
    @Override
    public String getCaptionText(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = mWindowManagerService.mRoot.anyTaskForId(taskId, 0);
            if (task == null || task.getRootActivity() == null) {
                return " no task found in Core";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("id(").append(taskId).append(")");
            sb.append(isTaskResizable(taskId) ? " resizable" : " non-resizable");

            ActivityRecord top = task.getTopVisibleActivity();
            if (top != null) {
                if (top.shouldCreateCompatDisplayInsets()) {
                    sb.append(top.inSizeCompatMode()
                            ? " size-compat:i" : " size-compat:s");
                }
                // fydroid-rt (AOSP-13): ActivityRecord.pendingOptions became the
                // private field mPendingOptions with no accessor in 12+; the launch-
                // bounds-from-options diagnostic is dropped (debug string only).
                if (top.info != null) {
                    if (top.info.windowLayout != null) {
                        appendWindowLayoutString(sb, top.info.windowLayout);
                    }
                    if (top.info.supportsSizeChanges) {
                        sb.append(" support-size-changes");
                    }
                    String oriStr;
                    if (top.info.screenOrientation
                            != top.getRequestedOrientation()) {
                        oriStr = "r:" + top.getRequestedOrientation();
                    } else {
                        oriStr = "m:" + top.info.screenOrientation;
                    }
                    sb.append(" ori(").append(oriStr).append(")");
                    if (top.info.hasFixedAspectRatio()) {
                        sb.append(" ar(").append(top.info.getMaxAspectRatio())
                                .append(":").append(top.info.getMinAspectRatio())
                                .append(")");
                    }
                    sb.append(" ").append(
                            top.mActivityComponent.flattenToShortString());
                    if (top.info.applicationInfo != null) {
                        sb.append(" SDK(")
                                .append(top.info.applicationInfo.targetSdkVersion)
                                .append(")");
                    }
                }
            }

            ActivityManager.TaskDescription td = task.getTaskDescription();
            if (td != null) {
                sb.append(" primaryColor(")
                        .append(Integer.toHexString(td.getPrimaryColor())).append(")");
                sb.append(" statusBarColor(")
                        .append(Integer.toHexString(td.getStatusBarColor())).append(")");
                sb.append(" navBarColor(")
                        .append(Integer.toHexString(td.getNavigationBarColor()))
                        .append(")");
            }
            return sb.toString();
        }
    }

    // 11
    @Override
    public boolean isBackButtonSuppressed(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null || task.getTopNonFinishingActivity() == null) {
                return false;
            }
            ActivityInfo info = task.getTopNonFinishingActivity().info;
            if (info == null) return false;
            Bundle meta = info.metaData;
            if (meta != null && meta.containsKey(SUPPRESS_CAPTION_BACK_BUTTON)) {
                return ((Boolean) meta.get(SUPPRESS_CAPTION_BACK_BUTTON)).booleanValue();
            }
            Bundle appMeta = info.applicationInfo.metaData;
            if (appMeta != null && appMeta.containsKey(SUPPRESS_CAPTION_BACK_BUTTON)) {
                return ((Boolean) appMeta.get(SUPPRESS_CAPTION_BACK_BUTTON))
                        .booleanValue();
            }
            return false;
        }
    }

    // 12
    @Override
    public boolean isTaskMinimized(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return false;
            final Task stack = task.getRootTask();
            if (stack == null) return false;
            TaskDisplayArea displayArea = task.getDisplayArea();
            if (displayArea == null) return false;
            // fydroid-rt (AOSP-13): ActivityStack was merged into Task and
            // TaskDisplayArea no longer exposes getStackCount()/getStackAt(i).
            // Re-express the top-down walk with forAllRootTasks(..., topToBottom):
            // if a HOME root task is encountered above our own root task, the
            // task is behind home (minimized).
            final boolean[] behindHome = {false};
            final boolean[] reached = {false};
            final boolean[] result = {false};
            displayArea.forAllRootTasks(s -> {
                if (reached[0]) return;
                if (s == stack) {
                    reached[0] = true;
                    result[0] = behindHome[0];
                    return;
                }
                if (s.getActivityType() == 2) {
                    behindHome[0] = true;
                }
            }, true /* traverseTopToBottom */);
            return result[0];
        }
    }

    // 13
    @Override
    public boolean isTaskRequestAppControlled(int taskId) {
        // ArcLaunchParamsModifier not available — return false
        return false;
    }

    // 14
    @Override
    public void restartSizeCompatActivityOnDisplayChangedIfNeeded(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = mWindowManagerService.mRoot.getTask(
                    t -> t.mTaskId == taskId);
            if (task == null) return;
            ActivityRecord top = task.getTopActivity(false, false);
            if (top != null && top.shouldCreateCompatDisplayInsets()
                    && task.getWindowingMode() == 1) {
                // fydroid-rt (AOSP-13): restartActivityProcessIfVisible moved to
                // ActivityClientController and is a local void (no RemoteException).
                mWindowManagerService.mAtmService.mActivityClientController
                        .restartActivityProcessIfVisible(top.token);
            }
        }
    }

    // 15
    @Override
    public void restartSizeCompatActivitiesIfNeeded(int displayId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            DisplayContent dc = mWindowManagerService.mRoot
                    .getDisplayContent(displayId);
            if (dc == null) return;
            dc.forAllTasks((Consumer<Task>) task -> {
                ActivityRecord top = task.getTopActivity(false, false);
                if (top != null && top.inSizeCompatMode()
                        && ActivityInfo.isFixedOrientationPortrait(
                        top.getRequestedOrientation())) {
                    mWindowManagerService.mAtmService.mActivityClientController
                            .restartActivityProcessIfVisible(top.token);
                }
            });
        }
    }

    // 16
    @Override
    public void restartResizeLockedTaskIfNeeded(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = mWindowManagerService.mRoot.getTask(
                    t -> t.mTaskId == taskId);
            if (task == null) return;
            ActivityRecord top = task.getTopActivity(true, false);
            if (top == null) return;
            DisplayContent dc = task.getDisplayContent();
            if (dc == null || dc.getWindowingMode() == 1) return;
            if (ActivityInfo.isFixedOrientationPortrait(
                    top.getRequestedOrientation())) {
                if (top.info.screenOrientation == top.getRequestedOrientation()) {
                    return;
                }
                mWindowManagerService.mAtmService.mActivityClientController
                        .restartActivityProcessIfVisible(top.token);
            }
        }
    }

    // 17
    @Override
    public boolean isInstallationEligibleForResizeLock(String packageName) {
        synchronized (mWindowManagerService.mGlobalLock) {
            try {
                InstallSourceInfo isi = mWindowManagerService.mContext
                        .getPackageManager().getInstallSourceInfo(packageName);
                if (!PLAY_STORE_PACKAGE.equals(isi.getInitiatingPackageName())
                        && !PLAY_STORE_PACKAGE.equals(
                        isi.getInstallingPackageName())) {
                    return false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Slog.e(TAG, "Cannot find package", e);
            }
            return true;
        }
    }

    // 18
    @Override
    public int getResizeLockState(String packageName) {
        // ArcWindowPersister not implemented — return 0
        return 0;
    }

    // 19
    @Override
    public void saveResizeLockState(String packageName, int state) {
        // ArcWindowPersister not implemented — no-op
    }

    // 20
    @Override
    public void waitForAllWindowsDrawn(IArcWindowDrawnCallback callback,
            int timeoutMs) {
        mWindowManagerInternal.waitForAllWindowsDrawn(() -> {
            try {
                callback.notifyAllWindowDrawn();
            } catch (RemoteException e) {
                Slog.e(TAG, "Window drawn callback execution failed.", e);
            }
        }, timeoutMs, -1);
    }

    // 21
    @Override
    public void waitForMainAppWindowDrawn(int taskId,
            IArcWindowDrawnCallback callback) {
        mHandler.post(() -> waitForMainAppWindowDrawnInternal(taskId, callback));
    }

    private void waitForMainAppWindowDrawnInternal(int taskId,
            IArcWindowDrawnCallback callback) {
        if (mWaitForMainAppWindowDrawnCounter.indexOfKey(taskId) != -1) {
            Slog.d(TAG, "Already waiting for main window of " + taskId
                    + " to be drawn");
            return;
        }
        mWaitForMainAppWindowDrawnCounter.put(taskId, 0);
        waitForMainAppWindowDrawnStep(taskId, callback);
    }

    private void waitForMainAppWindowDrawnStep(int taskId,
            IArcWindowDrawnCallback callback) {
        mWindowManagerInternal.waitForAllWindowsDrawn(() -> {
            mHandler.post(() -> {
                checkMainAppWindowDrawn(taskId, callback);
            });
        }, DEFAULT_WAIT_TIMEOUT_MS, -1);
    }

    private void checkMainAppWindowDrawn(int taskId,
            IArcWindowDrawnCallback callback) {
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                int attempt = mWaitForMainAppWindowDrawnCounter.get(taskId);
                Task task = getTaskById(taskId);
                if (attempt > WAIT_FOR_MAIN_WINDOW_MAX_ATTEMPTS
                        || (task != null
                        && task.getTopVisibleAppMainWindow() != null
                        && task.getTopVisibleAppMainWindow().isDrawn()
                        && task.getTopVisibleAppMainWindow().getAttrs().type != 3)) {
                    callback.notifyAllWindowDrawn();
                    mWaitForMainAppWindowDrawnCounter.delete(taskId);
                } else {
                    mWaitForMainAppWindowDrawnCounter.put(taskId, attempt + 1);
                    mHandler.postDelayed(
                            () -> waitForMainAppWindowDrawnStep(taskId, callback),
                            WAIT_FOR_MAIN_WINDOW_DELAY_MS);
                }
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Window drawn callback execution failed.", e);
        }
    }

    // 22
    @Override
    public void requestPictureInPictureMode(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) {
                Slog.d(TAG, "Could not find task " + taskId
                        + " to request PIP mode for");
                return;
            }
            if (task.inPinnedWindowingMode()) {
                Slog.d(TAG, "Task already in PIP mode");
                return;
            }
            // Refuse while ANY task is pinned. WM allows exactly one PIP, so a
            // second request does not queue — Task.setWindowingMode dismisses
            // the incumbent to make room. From an auto-PIP trigger that is a
            // loop generator, and it was measured as one on device in tablet mode:
            // occlusion oscillates as the PIP window appears and disappears
            // (0.996 fires → 0.789 as the PIP is torn down → 1.0 fires again),
            // so the app was re-PIP'd about once a second, minting a fresh task
            // every cycle (148 → 149 → 150 → 151 → …). The rising-edge test in
            // the occlusion policy cannot damp that, because the fraction really
            // does cross back down in between.
            //
            // Nothing is lost by refusing: the user already has a PIP on screen,
            // and replacing it silently with a different app is not a behaviour
            // anyone asked for. An explicit in-app PIP request still replaces it,
            // which is the documented "one PIP at a time" path.
            final Task existingPip = task.getDisplayContent() != null
                    ? task.getDisplayContent().getDefaultTaskDisplayArea().getRootPinnedTask()
                    : null;
            if (existingPip != null) {
                Slog.d(TAG, "Refusing PIP for task " + taskId + ": task "
                        + existingPip.mTaskId + " is already pinned");
                return;
            }
            // fydroid-rt (AOSP-13): there used to be a
            // WINDOWING_MODE_MULTI_WINDOW test here, inherited from the ARC
            // design where MULTI_WINDOW meant "snapped into split view" and PIP
            // was refused for such a task.
            //
            // It is gone because that equivalence no longer holds. The legacy
            // split-screen windowing mode was removed in 12, and MULTI_WINDOW is
            // now the steady state of EVERY tablet-mode task — the compositor
            // needs a mode that accepts resizeTask so the Android task can follow
            // the work-area-sized window (WaylandLayerContainer::
            // tabletWindowingMode). Keeping the test would refuse PIP for every
            // app on the device whenever it is in tablet mode, which is far worse
            // than occasionally PIP-ing one half of a split.
            //
            // Nothing correctness-critical was lost: the real gates below
            // (checkEnterPictureInPictureState, the pinned-mode and virtual-display
            // checks above) all still apply, and ash re-lays-out the split by
            // itself when a window leaves it — exo's PIP-expand handler already
            // ends tablet split view for the reverse direction.
            //
            // TODO(PIP phase C): if PIP-ing a snapped window turns out to be
            // worth refusing, the snap state has to come from the compositor —
            // it is the only side that knows — not be inferred from the
            // windowing mode here.
            ActivityRecord top = task.getTopNonFinishingActivity();
            if (top == null) {
                Slog.d(TAG, "Task's top activity is null");
                return;
            }
            Display display = mWindowManagerService.mDisplayManager.getDisplay(
                    task.getDisplayId());
            if (display != null && display.getType() == 5) {
                Slog.d(TAG, "Virtual display cannot enter PIP");
                return;
            }
            top.supportsEnterPipOnTaskSwitch = true;
            if (!top.checkEnterPictureInPictureState(
                    "setTaskOcclusionFraction", true)) {
                Slog.d(TAG, "Activity cannot enter PIP mode");
                return;
            }
            Slog.i(TAG, "Requesting PIP mode for taskId:" + taskId);
            if (NETFLIX_PACKAGE.equals(
                    task.getTaskInfo().realActivity.getPackageName())) {
                moveTaskToBack(taskId);
            } else {
                // fydroid-rt (AOSP-13): requestPictureInPictureMode moved to
                // ActivityClientController and now takes the ActivityRecord
                // directly (local boolean, no RemoteException).
                mWindowManagerService.mAtmService.mActivityClientController
                        .requestPictureInPictureMode(top);
            }
        }
    }

    // 23
    @Override
    public void moveTaskToDisplay(int taskId, int displayId, Rect bounds) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return;
            boolean moved = false;
            if (displayId != -1
                    && task.getDisplayContent().getDisplayId() != displayId) {
                mWindowManagerService.mAtmService.moveRootTaskToDisplay(
                        task.getRootTaskId(), displayId);
                moved = true;
            }
            boolean resized = false;
            if (bounds != null) {
                try {
                    resized = mWindowManagerService.mAtmService
                            .resizeTask(taskId, bounds, 3);
                } catch (IllegalArgumentException e) {
                    Slog.e(TAG, "Cannot resize task", e);
                }
            }
            if (moved && !resized) {
                ActivityRecord top = task.getTopMostActivity();
                if (top != null) {
                    top.ensureActivityConfiguration(0, true);
                }
            }
        }
    }

    // 24
    @Override
    public void prepareAppTransition(int transit, int displayId) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.MANAGE_APP_TOKENS",
                "prepareAppTransition()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (mWindowManagerService.mGlobalLock) {
            DisplayContent dc = mWindowManagerService.mRoot
                    .getDisplayContent(displayId);
            if (dc == null) return;
            dc.prepareAppTransition(transit);
        }
    }

    // 25
    @Override
    public void executeAppTransition(int displayId) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.MANAGE_APP_TOKENS",
                "executeAppTransition()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (mWindowManagerService.mGlobalLock) {
            DisplayContent dc = mWindowManagerService.mRoot
                    .getDisplayContent(displayId);
            if (dc == null) return;
            dc.executeAppTransition();
        }
    }

    // fydroid-rt: in an ARC kiosk session (ChromeAccountType.ROBOT_ACCOUNT == 2,
    // mirrored to persist.sys.arc.main_acct_type by ArcAuthService) pin the first
    // standard app task in lock-task LOCKED mode, so the kiosk app cannot be left.
    // isSystemCaller=true gives trusted/LOCKED mode (no allowlist needed) and also
    // trips the compositor's kiosk caption-hiding (WaylandLayerContainer). Paired
    // with the kiosk-fullscreen forcing in TaskLaunchParamsModifier so the app is
    // both maximized and pinned. Only fires for STANDARD tasks (skips Home) and
    // only when nothing is locked yet (pins once, the kiosk app).
    private void maybePinKioskTask(int taskId) {
        if (SystemProperties.getInt("persist.sys.arc.main_acct_type", 0) != 2) {
            return;  // not a kiosk (ROBOT_ACCOUNT) session
        }
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null
                        || task.getActivityType()
                                != android.app.WindowConfiguration.ACTIVITY_TYPE_STANDARD) {
                    return;
                }
                LockTaskController ltc = mWindowManagerService.mAtmService
                        .getLockTaskController();
                if (ltc.getLockTaskModeState()
                        != ActivityManager.LOCK_TASK_MODE_NONE) {
                    return;  // already pinned
                }
                ltc.startLockTaskMode(task, true /* isSystemCaller */,
                        android.os.Process.SYSTEM_UID);
                Slog.i(TAG, "kiosk: pinned task " + taskId);
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 26
    @Override
    public void setTaskLockMode(int taskId, int lockMode) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.MANAGE_ACTIVITY_STACKS", "setTaskLockMode()")) {
            throw new SecurityException("Requires MANAGE_ACTIVITY_STACKS permission");
        }
        int callingUid = Binder.getCallingUid();
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                LockTaskController ltc = mWindowManagerService.mAtmService
                        .getLockTaskController();
                Task task = getTaskById(taskId);
                if (task == null) return;
                if (lockMode != 0) {
                    ltc.startLockTaskMode(task, true, callingUid);
                } else {
                    ltc.stopLockTaskMode(task, true, callingUid);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 27
    @Override
    public void registerLockTaskListener(ILockTaskListener listener) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.MANAGE_ACTIVITY_STACKS", "setTaskLockMode()")) {
            throw new SecurityException("Requires MANAGE_ACTIVITY_STACKS permission");
        }
        mLockTaskListeners.register(listener);
    }

    // 28 - AIDL placeholder (real handling in onTransact override)
    @Override
    public void applyTaskTransaction(int taskId) {
        // Not called directly — intercepted by onTransact for proper
        // SurfaceControl.Transaction deserialization.
        throw new UnsupportedOperationException(
                "applyTaskTransaction must be called via binder");
    }

    private void applyTaskTransactionImpl(int taskId,
            SurfaceControl.Transaction transaction) {
        if (!mWindowManagerService.checkCallingPermission(
                "android.permission.REAL_GET_TASKS", "getTaskSurface()")) {
            throw new SecurityException("Requires REAL_GET_TASKS permission");
        }
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return;
            // fydroid-rt (AOSP-13): the getMainWindowSizeChangeTransaction() getter
            // was removed in 12. The model is now push-only: setMainWindowSizeChangeTransaction
            // schedules the transaction to be merged with the next BASE_APPLICATION
            // window draw (deferred to the resize frame), or applies it immediately
            // if there is no visible main window — which is exactly the old
            // merge-with-pending / apply-now behavior.
            task.setMainWindowSizeChangeTransaction(transaction);
        }
    }

    // 29
    @Override
    public int onPreEnterPip(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return -1;
            ArraySet<Task> tasks = new ArraySet<>();
            tasks.add(task);
            long ident = Binder.clearCallingIdentity();
            try {
                mWindowManagerService.mTaskSnapshotController
                        .snapshotTasks(tasks);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
            return task.mTaskId;
        }
    }

    // 30
    @Override
    public Point getTaskMinSize(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return null;
            return getTaskMinSizeInternal(task,
                    mWindowManagerService.getBaseDisplayDensity(
                            task.getDisplayId()));
        }
    }

    // 31
    @Override
    public void hideInputMethodIfDimmed() {
        if (isInputMethodBehindDimmer()) {
            LocalServices.getService(InputMethodManagerInternal.class)
                    .hideCurrentInputMethod(19);
        }
    }

    // 32
    @Override
    public void processDisplayDensityChange(int displayId, int densityDpi,
            float scale) {
        if (mWindowManagerService.mContext.checkCallingOrSelfPermission(
                "android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException(
                    "Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        synchronized (mWindowManagerService.mGlobalLock) {
            mWindowManagerService.mAtmService.deferWindowLayout();
            try {
                mWindowManagerService.setForcedDisplayDensityForUser(
                        displayId, densityDpi, -2);
                DisplayContent dc = mWindowManagerService.mRoot
                        .getDisplayContent(displayId);
                if (dc == null) {
                    Slog.e(TAG, "processDisplayDensityChange: "
                            + "cannot find displayId=" + displayId);
                    return;
                }
                dc.forAllLeafTasks(task -> {
                    if (task.getWindowingMode() == 5) {
                        Rect taskBounds = mWindowManagerService.mAtmService
                                .getTaskBounds(task.mTaskId);
                        if (taskBounds != null && !taskBounds.isEmpty()) {
                            taskBounds.scale(scale);
                            mWindowManagerService.mAtmService.resizeTask(
                                    task.mTaskId, taskBounds, 3);
                        }
                    }
                    return false;
                });
            } finally {
                mWindowManagerService.mAtmService.continueWindowLayout();
            }
        }
    }

    // 33
    @Override
    public Rect getTaskMinWidthAndHeight(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null || task.getSurfaceControl() == null) return null;
            int defaultMin = (int) ((mWindowManagerService.mRoot
                    .mDefaultMinSizeOfResizeableTaskDp
                    * (task.getDisplayContent().mBaseDisplayDensity / 160.0f))
                    + 0.5f);
            int w = task.mMinWidth < 0 ? defaultMin : task.mMinWidth;
            int h = task.mMinHeight < 0 ? defaultMin : task.mMinHeight;
            return new Rect(0, 0, w, h);
        }
    }

    // 34
    @Override
    public void startHomeOnDisplay(int displayId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            if (mUserId == -10000) {
                Slog.e(TAG, "Invoked startHomeOnDisplay before any user starts");
                return;
            }
            LocalServices.getService(ActivityTaskManagerInternal.class)
                    .startHomeOnDisplay(mUserId, TAG, displayId, false, false);
        }
    }

    // 35
    @Override
    public void forceClosePackage(String packageName) {
        int callingUid = Binder.getCallingUid();
        long ident = Binder.clearCallingIdentity();
        try {
            ActivityManager.getService().forceStopPackage(
                    packageName, callingUid);
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to force-stop package ("
                    + packageName + ")", e);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 36
    @Override
    public Intent getTaskIntentWithExtras(int taskId) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = mWindowManagerService.mRoot.getTask(
                    t -> t.mTaskId == taskId);
            if (task == null) {
                Slog.e(TAG, "getTaskIntentWithExtras: taskId="
                        + taskId + " not found.");
            }
            return task != null ? task.getBaseIntent() : null;
        }
    }

    // 37
    @Override
    public void setLastNonFullscreenBounds(int taskId, Rect bounds) {
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null) return;
                task.setLastNonFullscreenBounds(bounds);
                mWindowManagerService.mAtmService.mTaskSupervisor
                        .mLaunchParamsPersister
                        .saveTask(task, task.getDisplayContent());
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 38
    @Override
    public Rect getLastNonFullscreenBounds(int taskId) {
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                Task task = getTaskById(taskId);
                if (task == null) return null;
                return task.mLastNonFullscreenBounds;
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    // 39
    @Override
    public void setTaskWindowBounds(int taskId, int displayId, Rect bounds) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return;
            boolean moved = false;
            if (displayId != -1
                    && task.getDisplayContent().getDisplayId() != displayId) {
                mWindowManagerService.mAtmService.moveRootTaskToDisplay(
                        task.getRootTaskId(), displayId);
                moved = true;
            }
            boolean resized = false;
            if (bounds != null) {
                resized = task.setBounds(bounds) == 2;
                // Take the surface with them.
                //
                // Bounds alone are not a move (see pinTaskSurfaceTo), and this
                // path is where that bites hardest: it is how ArcSystemUI puts
                // a pinned task back at the origin after WM has moved it, which
                // WM does on EVERY display rotation — measured on device as
                // "task 200 was Rect(28,71-828,521) — asserting
                // Rect(0,0-800,450)" once per rotation, after which the task's
                // bounds read (0,0) while its Task layer stayed at (28,71).
                //
                // The app cannot show that (CompositionEngineArc anchors the
                // composition at the footprint origin, so a uniformly displaced
                // task still composes correctly) but the PIP menu can: it is
                // stamped into the task and laid out at the real origin, so it
                // ended up drawn (28,71) up and to the left of the window it is
                // supposed to cover, with a strip of undimmed video along the
                // bottom and right. That is the whole of the "the PIP menu is
                // offset after a rotation" bug.
                pinTaskSurfaceTo(taskId, task, bounds.left, bounds.top);
            }
            if (moved && !resized) {
                ActivityRecord top = task.getTopMostActivity();
                if (top != null) {
                    top.ensureActivityConfiguration(0, true);
                }
            }
        }
    }

    // 40
    @Override
    public int getAllowlistedDefaultLaunchBoundsType(String packageName) {
        // ArcLaunchParamsModifier not available — return 0 (no allowlist)
        return 0;
    }

    // 41
    @Override
    public void setAlwaysOnTop(int taskId, boolean alwaysOnTop) {
        synchronized (mWindowManagerService.mGlobalLock) {
            Task task = getTaskById(taskId);
            if (task == null) return;
            task.setAlwaysOnTop(alwaysOnTop);
        }
    }

    // 42
    @Override
    public void setRoundedWindowRadiusPx(int displayId, int radiusPx) {
        synchronized (mWindowManagerService.mGlobalLock) {
            SystemProperties.set(
                    "persist.sys.ui.roundedWindowRadiusPx_" + displayId,
                    String.valueOf(radiusPx));
        }
    }

    // 43
    @Override
    public void reconfigureDisplay(int displayId) {
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (mWindowManagerService.mGlobalLock) {
                DisplayContent dc = mWindowManagerService.mRoot
                        .getDisplayContent(displayId);
                if (dc == null) return;
                dc.reconfigureDisplayLocked();
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }
}

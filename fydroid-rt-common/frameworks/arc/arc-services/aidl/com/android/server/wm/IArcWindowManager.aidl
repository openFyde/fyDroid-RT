package com.android.server.wm;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceControl;
import com.android.server.wm.IArcWindowDrawnCallback;
import com.android.server.wm.ILockTaskListener;
import com.android.server.wm.ITaskOrientationListener;

/**
 * ARC window manager binder interface.
 * Methods are ordered to match the proprietary transaction IDs exactly.
 */
interface IArcWindowManager {
    // 1
    void registerTaskOrientationListener(ITaskOrientationListener listener);
    // 2
    boolean moveTaskToBack(int taskId);
    // 3
    SurfaceControl getTaskSurface(int taskId);
    // 4
    int getTaskRequestedWindowingMode(int taskId);
    // 5
    boolean isUidPresentInTask(int taskId, int uid);
    // 6
    void requestActivate(int taskId, int flags, boolean suppressCallback, boolean force);
    // 7
    boolean setTaskResizing(int taskId, boolean resizing);
    // 8
    boolean isTaskResizing(int taskId);
    // 9
    boolean isTaskResizable(int taskId);
    // 10
    String getCaptionText(int taskId);
    // 11
    boolean isBackButtonSuppressed(int taskId);
    // 12
    boolean isTaskMinimized(int taskId);
    // 13
    boolean isTaskRequestAppControlled(int taskId);
    // 14
    void restartSizeCompatActivityOnDisplayChangedIfNeeded(int taskId);
    // 15
    void restartSizeCompatActivitiesIfNeeded(int displayId);
    // 16
    void restartResizeLockedTaskIfNeeded(int taskId);
    // 17
    boolean isInstallationEligibleForResizeLock(String packageName);
    // 18
    int getResizeLockState(String packageName);
    // 19
    void saveResizeLockState(String packageName, int state);
    // 20
    void waitForAllWindowsDrawn(IArcWindowDrawnCallback callback, int timeoutMs);
    // 21
    void waitForMainAppWindowDrawn(int taskId, IArcWindowDrawnCallback callback);
    // 22
    void requestPictureInPictureMode(int taskId);
    // 23
    void moveTaskToDisplay(int taskId, int displayId, in Rect bounds);
    // 24
    void prepareAppTransition(int transit, int displayId);
    // 25
    void executeAppTransition(int displayId);
    // 26
    void setTaskLockMode(int taskId, int lockMode);
    // 27
    void registerLockTaskListener(ILockTaskListener listener);
    // 28 - Real signature uses SurfaceControl.Transaction; handled via onTransact override
    void applyTaskTransaction(int taskId);
    // 29
    int onPreEnterPip(int taskId);
    // 30
    Point getTaskMinSize(int taskId);
    // 31
    void hideInputMethodIfDimmed();
    // 32
    void processDisplayDensityChange(int displayId, int densityDpi, float scale);
    // 33
    Rect getTaskMinWidthAndHeight(int taskId);
    // 34
    void startHomeOnDisplay(int displayId);
    // 35
    void forceClosePackage(String packageName);
    // 36
    Intent getTaskIntentWithExtras(int taskId);
    // 37
    void setLastNonFullscreenBounds(int taskId, in Rect bounds);
    // 38
    Rect getLastNonFullscreenBounds(int taskId);
    // 39
    void setTaskWindowBounds(int taskId, int displayId, in Rect bounds);
    // 40
    int getAllowlistedDefaultLaunchBoundsType(String packageName);
    // 41
    void setAlwaysOnTop(int taskId, boolean alwaysOnTop);
    // 42
    void setRoundedWindowRadiusPx(int displayId, int radiusPx);
    // 43
    void reconfigureDisplay(int displayId);
    // 44
    // Tell WM that this pinned task is CLOSING, not expanding, before its
    // windowing-mode exit is delivered. WM tears a pinned task down through the
    // mode change, so a dismiss and an expand both arrive as onActivityUnpinned
    // — and pushing that to the compositor as a PIP exit makes it restore the
    // caption and the pre-PIP rectangle, flashing a full-size ordinary window on
    // screen for the moment before the surface dies. With this the exit push is
    // suppressed for the closing task and the window simply goes away.
    // APPEND ONLY — see docs/picture-in-picture-plan.md trap 0.
    void notifyPipClosing(int taskId);
}

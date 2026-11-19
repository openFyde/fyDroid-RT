/* Open source replacement for com.android.server.wm.IArcWindowManager
 * from the proprietary com.google.android.chromeos jar. */
package com.android.server.wm;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceControl;
import com.android.server.wm.IArcWindowDrawnCallback;
import com.android.server.wm.ILockTaskListener;
import com.android.server.wm.ITaskOrientationListener;

/**
 * Core binder interface exposed by the ARC window manager extensions in
 * system_server. Consumers include ArcSystemUI, arc-services and the
 * wayland_service bridge.
 *
 * Proprietary binder descriptor:
 *   "com.android.server.wm.IArcWindowManager"
 *
 * This interface is NOT oneway: the proprietary Proxy transacts with
 * flags=0 and reads a reply for every method (including void ones, which
 * read _reply.readException() after writeNoException). Methods are
 * declared below in transaction-code order (1..43) so the AIDL compiler
 * generates matching sequential TRANSACTION_* constants, preserving the
 * wire-level ABI of the proprietary jar.
 *
 * Complex parameter types (matching the proprietary signatures):
 *   - android.graphics.Rect  for setLastNonFullscreenBounds,
 *     getLastNonFullscreenBounds, setTaskWindowBounds, moveTaskToDisplay,
 *     getTaskMinWidthAndHeight.
 *   - android.graphics.Point for getTaskMinSize.
 *   - android.content.Intent for getTaskIntentWithExtras.
 *   - android.view.SurfaceControl for getTaskSurface.
 *   - android.view.SurfaceControl.Transaction for applyTaskTransaction.
 */
interface IArcWindowManager {
    const int OCCLUSION_CHANGE_REASON_USER_ACTION    = 1;
    const int OCCLUSION_CHANGE_REASON_PROGRAM_ACTION = 2;

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
    void requestActivate(int taskId, int flags, boolean suppressWaylandNotification, boolean forced);
    // 7
    boolean setTaskResizing(int taskId, boolean dragResizing);
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
    void saveResizeLockState(String packageName, int resizeLockState);
    // 20
    void waitForAllWindowsDrawn(IArcWindowDrawnCallback callback, int timeout);
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
    void setTaskLockMode(int taskId, int lockTaskMode);
    // 27
    void registerLockTaskListener(ILockTaskListener lockTaskListener);
    // 28
    void applyTaskTransaction(int taskId, in SurfaceControl.Transaction transaction);
    // 29
    int onPreEnterPip(int taskId);
    // 30
    Point getTaskMinSize(int taskId);
    // 31
    void hideInputMethodIfDimmed();
    // 32
    void processDisplayDensityChange(int displayId, int newDensityDpi, float resizeScale);
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
    void setRoundedWindowRadiusPx(int displayId, int px);
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

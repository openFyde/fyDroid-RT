/* Open source replacement for com.google.android.chromeos.IArcCompanionHandler from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.chromeos.IArcCompanionCallback;

/**
 * Main binder handle that application code uses to talk to the Chrome OS
 * systemui ArcCompanionService. Implemented on the systemui side by
 * ArcCompanionService; consumed on the app side through CompanionLibUtil.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads writeNoException() from the reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..17:
 *   1  getTaskWindowBounds
 *   2  setTaskWindowBounds
 *   3  setTaskWindowState
 *   4  getTaskWindowState
 *   5  getDeviceMode
 *   6  getWorkspaceInsets
 *   7  getDisplayTopology
 *   8  getCaptionHeightForDefaultDisplayPx
 *   9  getPrimaryDisplayId
 *   10 activateTask
 *   11 startResizingTask
 *   12 setHiddenCaptionButtons
 *   13 getHiddenCaptionButtons
 *   14 hideShadow
 *   15 registerCallback
 *   16 unregisterCallback
 *   17 checkTaskMatchesCallingUid
 */
interface IArcCompanionHandler {
    // 1
    Rect getTaskWindowBounds(int taskId);
    // 2
    void setTaskWindowBounds(int taskId, int displayId, in Rect bounds);
    // 3
    void setTaskWindowState(int taskId, int state);
    // 4
    int getTaskWindowState(int taskId);
    // 5
    int getDeviceMode();
    // 6
    Rect getWorkspaceInsets(int displayId);
    // 7
    Point getDisplayTopology(int displayId);
    // 8
    int getCaptionHeightForDefaultDisplayPx();
    // 9
    int getPrimaryDisplayId();
    // 10
    void activateTask(int taskId);
    // 11
    boolean startResizingTask(int taskId, int ctrlType, int startX, int startY);
    // 12
    boolean setHiddenCaptionButtons(int taskId, int buttonsToHide);
    // 13
    int getHiddenCaptionButtons(int taskId);
    // 14
    void hideShadow(int taskId, boolean hide);
    // 15
    void registerCallback(IArcCompanionCallback callback);
    // 16
    void unregisterCallback(IArcCompanionCallback callback);
    // 17
    void checkTaskMatchesCallingUid(int taskId);
}

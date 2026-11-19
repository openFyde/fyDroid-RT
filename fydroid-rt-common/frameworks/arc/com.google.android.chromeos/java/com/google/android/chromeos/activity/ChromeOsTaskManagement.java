/* Open source replacement for com.google.android.chromeos.activity.ChromeOsTaskManagement from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.chromeos.ChromeOsSharedLib;
import com.google.android.chromeos.CompanionLibUtil;
import com.google.android.chromeos.IArcCompanionHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Public entry point that an Activity uses to talk to Chrome OS about its
 * own task window: read/write bounds, window state, caption buttons, and
 * install close-request handlers. Implements the CompanionLib "activity"
 * surface 1:1 with the proprietary jar.
 *
 * Public surface preserved:
 *   - Constants: ALL_CAPTION_BUTTONS is private in the proprietary jar,
 *     but FLAG_CAPTION_BUTTON_*, CLOSE_REQUEST_CODE_*, WINDOW_STATE_* are
 *     all public and reproduced verbatim.
 *   - Nested annotation interfaces CaptionButton / CloseRequestCode /
 *     WindowState.
 *   - Constructor ChromeOsTaskManagement(int, Activity).
 *   - All public methods: setTaskWindowState, getTaskWindowState,
 *     activateTask, hideCaptionButtons, getHiddenCaptionButtons,
 *     getCaptionHeight, setTaskWindowBounds, getTaskWindowBounds,
 *     hideShadow, isShadowHidden, setOnCloseRequestHandler,
 *     removeOnCloseRequestHandler, onCloseRequestResponse.
 */
public final class ChromeOsTaskManagement {
    public static final int CLOSE_REQUEST_CODE_WAIT = 0;
    public static final int CLOSE_REQUEST_CODE_ABORT = 1;
    public static final int CLOSE_REQUEST_CODE_CLOSE = 2;

    public static final int FLAG_CAPTION_BUTTON_GO_BACK = 1;
    public static final int FLAG_CAPTION_BUTTON_MINIMIZE = 2;
    public static final int FLAG_CAPTION_BUTTONS_MAXIMIZE_AND_RESTORE = 12;
    public static final int FLAG_CAPTION_BUTTON_LEGACY_MENU = 16;
    public static final int FLAG_CAPTION_BUTTON_CLOSE_TASK = 32;

    public static final int WINDOW_STATE_NORMAL = 0;
    public static final int WINDOW_STATE_MAXIMIZED = 1;
    public static final int WINDOW_STATE_MINIMIZED = 2;
    public static final int WINDOW_STATE_ALWAYS_ON_TOP = 4;
    public static final int WINDOW_STATE_APP_MANAGED = 256;

    private static final int ALL_CAPTION_BUTTONS = 63;
    private static final int WINDOW_STATE_MASK = -257;
    private static final int WINDOW_STATE_MASKED_MAX = 5;

    private static final String TAG = "ChromeOsTaskManagement";

    private final int mClientLibVersion;
    private final Activity mActivity;
    private final IArcCompanionHandler mArcCompanionHandler;
    private final Map<Integer, TaskCloserServiceConnection> mTaskCloserHandlerMap =
            new HashMap<>();

    @Retention(RetentionPolicy.SOURCE)
    public @interface CaptionButton {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CloseRequestCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface WindowState {
    }

    public ChromeOsTaskManagement(int clientLibVersion, Activity activity) {
        this.mClientLibVersion = clientLibVersion;
        this.mActivity = activity;
        IArcCompanionHandler handler = CompanionLibUtil.getCompanionHandler(activity);
        this.mArcCompanionHandler = handler;
        if (handler == null) {
            Log.e(TAG, "Error on connect to Companion Service");
        }
    }

    public int setTaskWindowState(int windowState) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_SET_TASK_WINDOW_STATE);
        int masked = windowState & WINDOW_STATE_MASK;
        if (masked >= WINDOW_STATE_MASKED_MAX) {
            Log.w(TAG, "Unknown window state: " + windowState);
            return ChromeOsSharedLib.ERROR_INVALID_OPTION;
        }
        if (ChromeOsSharedLib.isFlagSet(windowState, WINDOW_STATE_ALWAYS_ON_TOP)
                && (masked == WINDOW_STATE_MAXIMIZED || masked == WINDOW_STATE_MINIMIZED)) {
            Log.w(TAG, "Cannot set task window state to ALWAYS_ON_TOP and MAXIMIZED/MINIMIZED");
            return ChromeOsSharedLib.ERROR_INVALID_OPTION;
        }
        if (mArcCompanionHandler == null) {
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        try {
            mArcCompanionHandler.setTaskWindowState(mActivity.getTaskId(), windowState);
            return ChromeOsSharedLib.SUCCESS;
        } catch (RemoteException e) {
            Log.e(TAG, "Error on set task window state", e);
            return ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        }
    }

    public TaskWindowState getTaskWindowState() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_TASK_WINDOW_STATE);
        if (mArcCompanionHandler == null) {
            return new TaskWindowState(ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE);
        }
        try {
            return new TaskWindowState(
                    mArcCompanionHandler.getTaskWindowState(mActivity.getTaskId()));
        } catch (RemoteException e) {
            Log.e(TAG, "Error on get task window state", e);
            return new TaskWindowState(ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION);
        }
    }

    public int activateTask() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_ACTIVATE_TASK);
        if (mArcCompanionHandler == null) {
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        try {
            mArcCompanionHandler.activateTask(mActivity.getTaskId());
            return ChromeOsSharedLib.SUCCESS;
        } catch (RemoteException e) {
            return ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        }
    }

    public int hideCaptionButtons(int buttonsToHide) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_HIDE_CAPTION_BUTTON);
        if (mArcCompanionHandler == null) {
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        if ((buttonsToHide & FLAG_CAPTION_BUTTON_LEGACY_MENU) != 0) {
            Log.w(TAG, "FLAG_CAPTION_BUTTON_LEGACY_MENU has been deprecated.");
        }
        try {
            return mArcCompanionHandler.setHiddenCaptionButtons(
                    mActivity.getTaskId(), buttonsToHide & ALL_CAPTION_BUTTONS)
                    ? ChromeOsSharedLib.SUCCESS
                    : ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        } catch (RemoteException e) {
            Log.w(TAG, "Error on hideCaptionButtons", e);
            return ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        }
    }

    public int getHiddenCaptionButtons() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_HIDDEN_CAPTION_BUTTON);
        if (mArcCompanionHandler == null) {
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        try {
            return mArcCompanionHandler.getHiddenCaptionButtons(mActivity.getTaskId());
        } catch (RemoteException e) {
            Log.w(TAG, "Error getHiddenCaptionButtons", e);
            return 0;
        }
    }

    public int getCaptionHeight() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_CAPTION_HEIGHT);
        if (mArcCompanionHandler == null) {
            Log.e(TAG, "Error on connect to Companion Service");
            return 0;
        }
        try {
            return mArcCompanionHandler.getCaptionHeightForDefaultDisplayPx();
        } catch (RemoteException e) {
            Log.e(TAG, "Error on getCaptionHeight", e);
            return 0;
        }
    }

    public int setTaskWindowBounds(int displayId, Rect bounds) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_SET_TASK_WINDOW_BOUNDS);
        if (bounds == null) {
            Log.e(TAG, "Cannot set activity bounds with null bounds.");
            return ChromeOsSharedLib.ERROR_INVALID_OPTION;
        }
        if (DisplayManagerGlobal.getInstance().getDisplayInfo(displayId) == null) {
            Log.e(TAG, "Cannot place activity on non-existent display with ID " + displayId);
            return ChromeOsSharedLib.ERROR_INVALID_OPTION;
        }
        if (mArcCompanionHandler == null) {
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        try {
            mArcCompanionHandler.setTaskWindowBounds(
                    mActivity.getTaskId(), displayId, bounds);
            return ChromeOsSharedLib.SUCCESS;
        } catch (RemoteException e) {
            Log.e(TAG, "Error on setTaskWindowBounds", e);
            return ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        }
    }

    public Rect getTaskWindowBounds() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_TASK_WINDOW_BOUNDS);
        if (mArcCompanionHandler == null) {
            return null;
        }
        try {
            return mArcCompanionHandler.getTaskWindowBounds(mActivity.getTaskId());
        } catch (RemoteException e) {
            Log.e(TAG, "Error on getTaskWindowBounds", e);
            return null;
        }
    }

    public int hideShadow(boolean hide) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_HIDE_SHADOW);
        if (mArcCompanionHandler == null) {
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        try {
            mArcCompanionHandler.hideShadow(mActivity.getTaskId(), hide);
            return ChromeOsSharedLib.SUCCESS;
        } catch (RemoteException e) {
            Log.e(TAG, "Error on hide task window shadow", e);
            return ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        }
    }

    public boolean isShadowHidden() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_IS_SHADOW_HIDDEN);
        return false;
    }

    public int setOnCloseRequestHandler(Runnable runnable) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_SET_ON_CLOSE_REQUEST_HANDLER);
        setTaskCloseHandler(mActivity.getTaskId(), runnable);
        return ChromeOsSharedLib.SUCCESS;
    }

    public int removeOnCloseRequestHandler() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_REMOVE_ON_CLOSE_REQUEST_HANDLER);
        resetTaskCloseHandler(mActivity.getTaskId());
        return ChromeOsSharedLib.SUCCESS;
    }

    public void onCloseRequestResponse(int closeRequestCode) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_ON_CLOSE_REQUEST_RESPONSE);
        if (closeRequestCode == CLOSE_REQUEST_CODE_WAIT) {
            notifyTaskCloseInProgress(mActivity.getTaskId());
            return;
        }
        if (closeRequestCode == CLOSE_REQUEST_CODE_ABORT) {
            cancelTaskClose(mActivity.getTaskId());
            return;
        }
        if (closeRequestCode == CLOSE_REQUEST_CODE_CLOSE) {
            finishTaskClose(mActivity.getTaskId());
            return;
        }
        Log.w(TAG, "Unrecognized close request code " + closeRequestCode);
    }

    private void setTaskCloseHandler(int taskId, Runnable callback) {
        if (!isTaskOwnedByCaller(taskId)) {
            Log.e(TAG, "Cannot set close handler for the task " + taskId
                    + " because task is not owned by caller");
            return;
        }
        synchronized (mTaskCloserHandlerMap) {
            if (mTaskCloserHandlerMap.containsKey(taskId)) {
                mTaskCloserHandlerMap.get(taskId).close();
            }
            mTaskCloserHandlerMap.put(taskId,
                    new TaskCloserServiceConnection(
                            mActivity.getBaseContext(), taskId, callback));
        }
    }

    private void resetTaskCloseHandler(int taskId) {
        synchronized (mTaskCloserHandlerMap) {
            if (mTaskCloserHandlerMap.containsKey(taskId)) {
                mTaskCloserHandlerMap.remove(taskId).close();
            }
        }
    }

    private void notifyTaskCloseInProgress(int taskId) {
        if (!isTaskOwnedByCaller(taskId)) {
            Log.e(TAG, "Cannot handle close in progress event for the task " + taskId
                    + " because task is not owned by caller");
            return;
        }
        synchronized (mTaskCloserHandlerMap) {
            if (mTaskCloserHandlerMap.containsKey(taskId)) {
                mTaskCloserHandlerMap.get(taskId).notifyCloseInProgress();
            }
        }
    }

    private void cancelTaskClose(int taskId) {
        if (!isTaskOwnedByCaller(taskId)) {
            Log.e(TAG, "Cannot handle cancel close event for the task " + taskId
                    + " because task is not owned by caller");
            return;
        }
        synchronized (mTaskCloserHandlerMap) {
            if (mTaskCloserHandlerMap.containsKey(taskId)) {
                mTaskCloserHandlerMap.get(taskId).cancelTaskClose();
            }
        }
    }

    private void finishTaskClose(int taskId) {
        if (!isTaskOwnedByCaller(taskId)) {
            Log.e(TAG, "Cannot handle finish closing the task event for the task " + taskId
                    + " because task is not owned by the caller");
            return;
        }
        synchronized (mTaskCloserHandlerMap) {
            if (mTaskCloserHandlerMap.containsKey(taskId)) {
                mTaskCloserHandlerMap.get(taskId).finishTaskClose();
            }
        }
    }

    private boolean isTaskOwnedByCaller(int taskId) {
        if (mArcCompanionHandler == null) {
            return false;
        }
        try {
            mArcCompanionHandler.checkTaskMatchesCallingUid(taskId);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "The task " + taskId + " is not owned by caller", e);
            return false;
        }
    }
}

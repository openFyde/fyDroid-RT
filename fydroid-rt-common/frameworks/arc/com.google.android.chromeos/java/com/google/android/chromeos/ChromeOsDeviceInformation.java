/* Open source replacement for com.google.android.chromeos.ChromeOsDeviceInformation from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos;

import android.app.ActivityThread;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

/**
 * Entry point used by Chrome OS device apps to query device mode (tablet
 * vs clamshell), workspace insets, display topology, and register
 * callbacks for changes. Singleton keyed on clientLibVersion.
 *
 * Public surface is preserved 1:1 with the proprietary jar:
 *   - getInstance(int clientLibVersion)
 *   - getDeviceMode / getWorkspaceInsets / getDisplayTopology / getPrimaryDisplayId
 *   - register/unregisterArcDeviceInformationCallback
 *   - nested ArcDeviceInformationCallback interface
 *   - DEVICE_MODE_CLAMSHELL / DEVICE_MODE_TABLET constants
 */
public class ChromeOsDeviceInformation {
    public static final int DEVICE_MODE_CLAMSHELL = 0;
    public static final int DEVICE_MODE_TABLET = 1;

    private static final String TAG = "ChromeOsDeviceInfo";

    private final IArcCompanionHandler mArcCompanionHandler;
    private final int mClientLibVersion;
    private final Set<ArcDeviceInformationCallback> mDeviceInformationCallbacks;
    private final Handler mHandler;
    private final IArcCompanionCallback.Stub mRemoteArcCompanionCallback;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DeviceMode {
    }

    private static final class ChromeOsDeviceInformationHolderV1 {
        static final ChromeOsDeviceInformation INSTANCE = new ChromeOsDeviceInformation(1);

        private ChromeOsDeviceInformationHolderV1() {
        }
    }

    public static ChromeOsDeviceInformation getInstance(int clientLibVersion) {
        if (clientLibVersion != 1) {
            throw new IllegalArgumentException(
                    "Unsupported clientLibVersion (" + clientLibVersion + ")");
        }
        return ChromeOsDeviceInformationHolderV1.INSTANCE;
    }

    private ChromeOsDeviceInformation(int clientLibVersion) {
        this.mDeviceInformationCallbacks = new HashSet<>();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mRemoteArcCompanionCallback = new IArcCompanionCallback.Stub() {
            @Override
            public void onDeviceModeChanged(final int deviceMode) {
                mHandler.post(() -> {
                    synchronized (mDeviceInformationCallbacks) {
                        for (ArcDeviceInformationCallback callback : mDeviceInformationCallbacks) {
                            try {
                                callback.onDeviceModeChanged(deviceMode);
                            } catch (AbstractMethodError e) {
                                Log.d(TAG, "Error calling onDeviceModeChanged", e);
                            }
                        }
                    }
                });
            }

            @Override
            public void onWorkspaceInsetsChanged(final int displayId, final Rect insets) {
                mHandler.post(() -> {
                    synchronized (mDeviceInformationCallbacks) {
                        for (ArcDeviceInformationCallback callback : mDeviceInformationCallbacks) {
                            try {
                                callback.onWorkspaceInsetsChanged(displayId, insets);
                            } catch (AbstractMethodError e) {
                                Log.d(TAG, "Error calling onWorkspaceInsetsChanged", e);
                            }
                        }
                    }
                });
            }

            @Override
            public void onTaskBoundsChanged(final Rect bounds) {
                mHandler.post(() -> {
                    synchronized (mDeviceInformationCallbacks) {
                        for (ArcDeviceInformationCallback callback : mDeviceInformationCallbacks) {
                            try {
                                callback.onTaskBoundsChanged(bounds);
                            } catch (AbstractMethodError e) {
                                Log.d(TAG, "Error calling onTaskBoundsChanged", e);
                            }
                        }
                    }
                });
            }
        };
        this.mClientLibVersion = clientLibVersion;
        IArcCompanionHandler companionHandler =
                CompanionLibUtil.getCompanionHandler(ActivityThread.currentApplication());
        this.mArcCompanionHandler = companionHandler;
        if (companionHandler == null) {
            Slog.e(TAG, "Error on connect to Companion Service");
        }
    }

    public int getDeviceMode() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_DEVICE_MODE);
        if (mArcCompanionHandler == null) {
            Slog.e(TAG, "getDeviceMode - Cannot connect to Companion Service");
            return DEVICE_MODE_CLAMSHELL;
        }
        try {
            return mArcCompanionHandler.getDeviceMode();
        } catch (RemoteException e) {
            Slog.e(TAG, "getDeviceMode - Exception while getting device mode", e);
            return DEVICE_MODE_CLAMSHELL;
        }
    }

    public int getWorkspaceInsets(int displayId, Rect insets) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_WORKSPACE_INSETS);
        if (mArcCompanionHandler == null) {
            Log.e(TAG, "getWorkspaceInsets - Cannot connect to Companion Service");
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        try {
            Rect workspaceInsets = mArcCompanionHandler.getWorkspaceInsets(displayId);
            if (workspaceInsets == null) {
                return ChromeOsSharedLib.ERROR_UNSUPPORTED_DISPLAY_ID;
            }
            insets.left = workspaceInsets.left;
            insets.top = workspaceInsets.top;
            insets.right = workspaceInsets.right;
            insets.bottom = workspaceInsets.bottom;
            return ChromeOsSharedLib.SUCCESS;
        } catch (RemoteException e) {
            Log.e(TAG, "getWorkspaceInsets - RemoteException while getting the workspace rectangle.", e);
            return ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        }
    }

    public void registerArcDeviceInformationCallback(ArcDeviceInformationCallback callback) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_REGISTER_CALLBACK);
        synchronized (mDeviceInformationCallbacks) {
            if (mDeviceInformationCallbacks.isEmpty()) {
                registerRemoteArcDeviceInformationCallbackLocked();
            }
            mDeviceInformationCallbacks.add(callback);
        }
    }

    public void unregisterArcDeviceInformationCallback(ArcDeviceInformationCallback callback) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_UNREGISTER_CALLBACK);
        synchronized (mDeviceInformationCallbacks) {
            mDeviceInformationCallbacks.remove(callback);
            if (mDeviceInformationCallbacks.isEmpty()) {
                unregisterRemoteArcDeviceInformationCallbackLocked();
            }
        }
    }

    public int getDisplayTopology(int displayId, Point offset) {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_DISPLAY_TOPOLOGY);
        if (mArcCompanionHandler == null) {
            return ChromeOsSharedLib.ERROR_FUNCTION_NOT_AVAILABLE;
        }
        try {
            Point result = mArcCompanionHandler.getDisplayTopology(displayId);
            if (result == null) {
                Slog.e(TAG, "Unsupported display id");
                return ChromeOsSharedLib.ERROR_UNSUPPORTED_DISPLAY_ID;
            }
            offset.set(result.x, result.y);
            return ChromeOsSharedLib.SUCCESS;
        } catch (RemoteException e) {
            Slog.e(TAG, "Error on getDisplayTopology", e);
            return ChromeOsSharedLib.ERROR_REMOTE_EXCEPTION;
        }
    }

    public int getPrimaryDisplayId() {
        CompanionLibUtil.reportApiUsage(CompanionLibUtil.API_ID_GET_PRIMARY_DISPLAY_ID);
        if (mArcCompanionHandler == null) {
            Log.e(TAG, "getPrimaryDisplayId - Cannot connect to Companion Service");
            return 0;
        }
        try {
            return mArcCompanionHandler.getPrimaryDisplayId();
        } catch (RemoteException e) {
            Log.e(TAG, "getPrimaryDisplay - RemoteException while getting the primary display ID.", e);
            return 0;
        }
    }

    private void registerRemoteArcDeviceInformationCallbackLocked() {
        if (mArcCompanionHandler == null) {
            Log.e(TAG, "Unable to register callback in Companion Service");
            return;
        }
        try {
            mArcCompanionHandler.registerCallback(mRemoteArcCompanionCallback);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to register IArcCompanionCallback");
        }
    }

    private void unregisterRemoteArcDeviceInformationCallbackLocked() {
        if (mArcCompanionHandler == null) {
            Log.e(TAG, "Unable to unregister callback in Companion Service");
            return;
        }
        try {
            mArcCompanionHandler.unregisterCallback(mRemoteArcCompanionCallback);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to unregister IArcCompanionCallback");
        }
    }

    public interface ArcDeviceInformationCallback {
        default void onDeviceModeChanged(int deviceMode) {
        }

        default void onWorkspaceInsetsChanged(int displayId, Rect insets) {
        }

        default void onTaskBoundsChanged(Rect bounds) {
        }
    }
}

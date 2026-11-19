/* Open source replacement for com.google.android.chromeos.activity.TaskCloserServiceConnection from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;

import org.chromium.arc.applauncher.ITaskCloserCallback;
import org.chromium.arc.applauncher.ITaskCloserService;

/**
 * Binds to the Chrome OS applauncher's TaskCloserService and installs an
 * {@link ITaskCloserCallback} for a given taskId. The proprietary class
 * extends ITaskCloserCallback.Stub and implements ServiceConnection; we
 * reproduce the same structure.
 */
class TaskCloserServiceConnection extends ITaskCloserCallback.Stub
        implements ServiceConnection {
    private static final String TAG = "TaskCloserServiceConnection";

    private final Context mContext;
    private final int mTaskId;
    private final Runnable mCallback;
    private ITaskCloserService mService;

    public TaskCloserServiceConnection(Context context, int taskId, Runnable callback) {
        this.mContext = context;
        this.mTaskId = taskId;
        this.mCallback = callback;
        Intent intent = new Intent();
        intent.setClassName("org.chromium.arc.applauncher",
                "org.chromium.arc.applauncher.app.service.TaskCloserService");
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public void close() {
        mContext.unbindService(this);
    }

    public void notifyCloseInProgress() {
        synchronized (this) {
            if (mService == null) {
                Slog.e(TAG, "Failed to notify task close in progress. Service is not available");
                return;
            }
            try {
                mService.notifyCloseInProgress(mTaskId);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to notify task close in progress.", e);
            }
        }
    }

    public void cancelTaskClose() {
        synchronized (this) {
            if (mService == null) {
                Slog.e(TAG, "Failed to cancel task close. Service is not available");
                return;
            }
            try {
                mService.cancelClose(mTaskId);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to cancel task close.", e);
            }
        }
    }

    public void finishTaskClose() {
        synchronized (this) {
            if (mService == null) {
                Slog.e(TAG, "Failed to finish task close. Service is not available");
                return;
            }
            try {
                mService.finishClose(mTaskId);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to finish task close.", e);
            }
        }
    }

    @Override
    public void onCloseRequest() {
        if (mCallback != null) {
            mCallback.run();
        }
    }

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        synchronized (this) {
            mService = ITaskCloserService.Stub.asInterface(service);
            Slog.i(TAG, "Connected to TaskCloserService.");
            try {
                mService.setHandler(mTaskId, this);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to set TaskCloserCallback");
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
        synchronized (this) {
            Slog.i(TAG, "TaskCloserService has been disconnected.");
            mService = null;
        }
    }
}

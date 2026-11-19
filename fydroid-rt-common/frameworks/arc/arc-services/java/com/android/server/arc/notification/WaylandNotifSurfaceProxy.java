package com.android.server.arc.notification;

import android.os.IBinder;
import android.os.Parcel;
import android.os.ServiceManager;
import android.util.Slog;

/**
 * Binder client for the Wayland service — tells SurfaceFlinger's wayland_service
 * that the next VirtualDisplay is the shared notification display.
 *
 * Transaction codes match WaylandBinderService.h on the native side.
 */
class WaylandNotifSurfaceProxy {
    private static final String TAG = "WaylandNotifProxy";
    private static final String DESCRIPTOR = "org.chromium.arc.wayland.IWaylandService";
    private static final int TRANSACTION_setNotificationDisplayId = 50;

    private IBinder mService;

    boolean connect() {
        mService = ServiceManager.getService("Wayland");
        if (mService == null) {
            Slog.e(TAG, "Wayland service not found");
            return false;
        }
        Slog.i(TAG, "Connected to Wayland service");
        return true;
    }

    boolean setNotificationDisplayId(int displayId) {
        return setNotificationDisplayId(displayId, 0, 0);
    }

    /**
     * Arms/sets the notification display. When arming (displayId &lt;= 0), width
     * and height are the notification VD's pixel size, which the composer uses
     * to consume the flag for the matching VD only (avoids the task-VD race).
     */
    boolean setNotificationDisplayId(int displayId, int width, int height) {
        if (mService == null) {
            Slog.e(TAG, "setNotificationDisplayId: not connected");
            return false;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(displayId);
            data.writeInt(width);
            data.writeInt(height);
            mService.transact(TRANSACTION_setNotificationDisplayId, data, reply, 0);
            reply.readException();
            Slog.d(TAG, "setNotificationDisplayId: " + displayId
                    + " (" + width + "x" + height + ")");
            return true;
        } catch (Exception e) {
            Slog.e(TAG, "setNotificationDisplayId failed for " + displayId, e);
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}

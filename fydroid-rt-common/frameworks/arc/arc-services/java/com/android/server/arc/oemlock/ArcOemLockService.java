package com.android.server.arc.oemlock;

import android.content.Context;
import android.os.Binder;
import android.util.Slog;
import com.android.server.SystemService;

/**
 * OEM lock service for ARC++. Always reports device as OEM-locked
 * (OEM unlock is not applicable in a container environment).
 * Publishes "oem_lock" binder service.
 */
public final class ArcOemLockService extends Binder {
    private static final String TAG = "ArcOemLockService";

    public static class LifeCycle extends SystemService {
        private final ArcOemLockService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcOemLockService();
        }

        @Override
        public void onStart() {
            publishBinderService("oem_lock", mService);
            Slog.d(TAG, "Arc oem lock service started");
        }
    }

    private ArcOemLockService() {
    }
}

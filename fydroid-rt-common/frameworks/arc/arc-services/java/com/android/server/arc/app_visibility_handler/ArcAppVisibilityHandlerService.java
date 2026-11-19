package com.android.server.arc.app_visibility_handler;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.android.server.SystemService;

/**
 * Handles app visibility state during initial device provisioning.
 * In the proprietary version, this calls into the app launcher to hide/show
 * apps based on provisioning state. Our version logs the state for now.
 */
class ArcAppVisibilityHandlerService {
    private static final String TAG = "ArcAppVisibility";
    private final Context mContext;

    public static class LifeCycle extends SystemService {
        private final ArcAppVisibilityHandlerService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcAppVisibilityHandlerService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase != SystemService.PHASE_THIRD_PARTY_APPS_CAN_START) {
                return;
            }
            mService.onSystemServicesReady();
        }

        @Override
        public void onStart() {
        }
    }

    ArcAppVisibilityHandlerService(Context context) {
        mContext = context;
    }

    private void onSystemServicesReady() {
        boolean provisioned = Settings.Global.getInt(
                mContext.getContentResolver(), "device_provisioned", 0) != 0;
        Log.i(TAG, "Device provisioned: " + provisioned);
        // Proprietary version calls into app launcher to set visibility.
        // In fydroid-rt, apps are always visible since we don't have the
        // Chrome OS provisioning flow.
    }
}

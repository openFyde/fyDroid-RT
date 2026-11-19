package com.android.server.arc.power;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.android.server.SystemService;

/**
 * ANR (Application Not Responding) helper service.
 * Receives pre-ANR and ANR recovery failed messages via Messenger
 * and forwards to the arcvm_power_anr service.
 * Publishes "arc_anr_messenger_service" binder.
 */
public final class ArcPowerHelperService {
    private static final int MSG_ON_PRE_ANR = 1;
    private static final int MSG_ON_ANR_RECOVERY_FAILED = 2;
    private static final String TAG = "ArcPowerHelper";

    private final Messenger mMessenger = new Messenger(new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ON_PRE_ANR:
                    Log.i(TAG, "Pre-ANR for pid " + msg.arg1);
                    break;
                case MSG_ON_ANR_RECOVERY_FAILED:
                    Log.w(TAG, "ANR recovery failed for pid " + msg.arg1);
                    break;
                default:
                    Log.wtf(TAG, "Invalid message id: " + msg.what);
                    break;
            }
        }
    });

    public static class LifeCycle extends SystemService {
        private final ArcPowerHelperService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcPowerHelperService();
        }

        @Override
        public void onStart() {
            publishBinderService("arc_anr_messenger_service",
                    mService.getMessengerBinder());
            Log.i(TAG, "ArcPowerHelperService started");
        }
    }

    IBinder getMessengerBinder() {
        return mMessenger.getBinder();
    }
}

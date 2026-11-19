package io.fydeos.arc.policymanager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/** Base service that owns the install-progress notifications and the install watch callback. */
abstract class ServiceWithNotification extends Service {
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void unWatch(int sessionId, String errMsg) {
        Log.d("ServiceWithNotification", "session:" + sessionId + " " + (errMsg != null ? errMsg : "success"));
    }

    void toast(CharSequence message) {
        mMainHandler.post(() -> Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show());
    }
}

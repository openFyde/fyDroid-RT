package io.fydeos.arc.policymanager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Runtime-registered receiver for package add/remove events, so a newly installed app immediately
 * has its pending policy applied. Forwards the event to {@link PolicyService}.
 */
public class PackageInstallStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "PolicyManagerReceiver";

    private static PackageInstallStatusReceiver sInstance;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.toString());
        intent.setComponent(new ComponentName(context, PolicyService.class));
        context.startService(intent);
    }

    private PackageInstallStatusReceiver register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(this, filter);
        return this;
    }

    static synchronized void registerPackageInstallStatusReceiver(Context context) {
        if (sInstance == null) {
            sInstance = new PackageInstallStatusReceiver().register(context);
        }
    }

    static synchronized void unregisterPackageInstallStatusReceiver(Context context) {
        if (sInstance != null) {
            context.unregisterReceiver(sInstance);
            sInstance = null;
        }
    }
}

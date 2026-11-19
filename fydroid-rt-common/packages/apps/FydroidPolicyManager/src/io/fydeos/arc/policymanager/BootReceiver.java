package io.fydeos.arc.policymanager;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** Starts {@link PolicyService} on boot so persisted policies are re-applied, and pulls policy
 *  from arc-services so we don't depend on the initial APPLY_POLICY broadcast (which can be
 *  dropped while the user is still locked, since PolicyReceiver is not directBootAware) — but
 *  only when this app is the device owner. */
public class BootReceiver extends BroadcastReceiver {
    private static final String ACTION_REQUEST_POLICY_REFRESH =
            "io.fydeos.arc.policy.action.REQUEST_REFRESH";

    @Override
    public void onReceive(Context context, Intent intent) {
        DevicePolicyManager dpm = context.getSystemService(DevicePolicyManager.class);
        if (dpm == null || !dpm.isDeviceOwnerApp(context.getPackageName())) {
            // Device isn't managed by us, so there's nothing to enforce. Starting a foreground
            // service here would briefly post the ongoing notification (to satisfy the
            // startForegroundService contract) before stopping, and Chrome OS surfaces that
            // transient notification as a stuck, uncancelable one. Just do nothing.
            Log.i("PolicyManagerBoot", "Not device owner; skipping policy service start.");
            return;
        }
        intent.setComponent(new ComponentName(context, PolicyService.class));
        context.startService(intent);

        context.sendBroadcast(new Intent(ACTION_REQUEST_POLICY_REFRESH));
    }
}

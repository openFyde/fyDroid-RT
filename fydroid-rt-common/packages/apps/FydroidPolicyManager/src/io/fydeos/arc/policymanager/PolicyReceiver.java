package io.fydeos.arc.policymanager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Entry point for system-sent policy broadcasts (APPLY_POLICY / STOP_MANAGER / SET_COMPONENT_STATE).
 * These are protected broadcasts, so only the system (arc-services / the Chrome bridge) can send
 * them. Forwards them to the non-exported {@link PolicyService}.
 */
public class PolicyReceiver extends BroadcastReceiver {
    private static final String TAG = "PolicyManagerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received " + intent.getAction());
        intent.setComponent(new ComponentName(context, PolicyService.class));
        context.startService(intent);
    }
}

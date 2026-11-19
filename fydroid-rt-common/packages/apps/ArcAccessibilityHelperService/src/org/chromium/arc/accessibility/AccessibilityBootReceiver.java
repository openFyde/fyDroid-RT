package org.chromium.arc.accessibility;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

/**
 * Enables the accessibility helper so Chrome can reach it.
 *
 * <p>The helper is an {@code AccessibilityService}, and Android only binds one
 * that appears in {@link Settings.Secure#ENABLED_ACCESSIBILITY_SERVICES}. On a
 * normal Android device the user enables it from Settings; in fydroid the
 * decision belongs to Chrome OS, which turns ChromeVox on and off and drives
 * the helper through {@code SetFilter}. So the helper enables itself at boot
 * and stays bound but idle — with the filter OFF it asks the framework for no
 * event types at all, so an unused bridge costs nothing.
 *
 * <p>{@code ACCESSIBILITY_ENABLED} is deliberately not written here: Android's
 * AccessibilityManagerService owns that setting and derives it from whether any
 * service is actually handling events.
 */
public class AccessibilityBootReceiver extends BroadcastReceiver {
    private static final String TAG = ArcAccessibilityHelperBridge.TAG;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }
        try {
            ensureEnabled(context);
        } catch (RuntimeException e) {
            Log.e(TAG, "failed to enable the accessibility helper", e);
        }
    }

    private static void ensureEnabled(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String component = new ComponentName(context, ArcAccessibilityHelperBridge.class)
                .flattenToString();

        String enabled = Settings.Secure.getString(
                resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (containsComponent(enabled, component)) {
            return;
        }

        String updated = TextUtils.isEmpty(enabled) ? component : enabled + ":" + component;
        Settings.Secure.putString(
                resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, updated);
        Log.i(TAG, "enabled " + component);
    }

    private static boolean containsComponent(String setting, String component) {
        if (TextUtils.isEmpty(setting)) {
            return false;
        }
        for (String entry : setting.split(":")) {
            if (component.equals(entry)) {
                return true;
            }
        }
        return false;
    }
}

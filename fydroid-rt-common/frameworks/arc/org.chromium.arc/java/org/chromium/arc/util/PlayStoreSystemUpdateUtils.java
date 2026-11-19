package org.chromium.arc.util;

import android.content.Context;

/**
 * Stub replacement for proprietary PlayStoreSystemUpdateUtils.
 * No-op since fydroid-rt doesn't use Play Store auto-update.
 */
public class PlayStoreSystemUpdateUtils {

    public static void setEnabledOverride(Context context) {
        // no-op
    }

    public static void setSystemUpdate(Context context, boolean enabled) {
        // no-op
    }

    public static void setSystemUpdateToDefaultState(Context context) {
        // no-op
    }
}

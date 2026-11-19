/* Open source replacement for org.chromium.arc.applauncher.AppVisibilityHandler
 * from the proprietary com.google.android.chromeos jar.
 *
 * Used on boot to hide / unhide specific first-party packages depending on
 * the user scope (unmanaged user -> Play Store; managed user -> CloudDPC).
 * Logic mirrors the proprietary class byte-for-byte so existing callers
 * can keep calling AppVisibilityHandler.setVisible unchanged.
 */
package org.chromium.arc.applauncher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;

public final class AppVisibilityHandler {
    public static final int PACKAGES_SCOPE_DEFAULT = 0;
    public static final int PACKAGES_SCOPE_MANAGED_USER = 1;

    private static final String TAG = "ArcAppVisibilityHandler";
    private static final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";
    private static final String CLOUD_DPC_PACKAGE_NAME =
            "com.google.android.apps.work.clouddpc.arc";

    private static final String[] PACKAGES_TO_HIDE_ON_BOOT_DEFAULT = {
            PLAY_STORE_PACKAGE_NAME
    };
    private static final String[] PACKAGES_TO_HIDE_ON_BOOT_MANAGED_USER = {
            CLOUD_DPC_PACKAGE_NAME
    };

    public static void setVisible(Context context, int scope, boolean visible) {
        String[] packages;
        PackageManager packageManager = context.getPackageManager();
        if (scope == PACKAGES_SCOPE_DEFAULT) {
            packages = PACKAGES_TO_HIDE_ON_BOOT_DEFAULT;
        } else if (scope == PACKAGES_SCOPE_MANAGED_USER) {
            packages = PACKAGES_TO_HIDE_ON_BOOT_MANAGED_USER;
        } else {
            Log.wtf(TAG, "Unrecognized scope of packages: " + scope + ".");
            return;
        }
        for (String packageName : packages) {
            boolean currentlyHidden = packageManager
                    .getApplicationHiddenSettingAsUser(packageName, UserHandle.SYSTEM);
            if (currentlyHidden != (!visible)) {
                packageManager.setApplicationHiddenSettingAsUser(
                        packageName, !visible, UserHandle.SYSTEM);
                if (visible) {
                    Log.i(TAG, "Visibility is restored for the package "
                            + packageName + ".");
                } else {
                    Log.i(TAG, "Package " + packageName + " was hidden.");
                }
            }
        }
    }
}

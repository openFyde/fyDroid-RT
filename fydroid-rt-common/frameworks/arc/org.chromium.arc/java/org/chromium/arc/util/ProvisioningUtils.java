package org.chromium.arc.util;

import android.accounts.Account;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Stub replacement for proprietary ProvisioningUtils.
 * Returns safe defaults: no Google accounts, device provisioned, no transitions.
 */
public final class ProvisioningUtils {
    public static final String GMAIL_DOMAIN = "gmail.com";
    public static final String GOOGLEMAIL_DOMAIN = "googlemail.com";
    public static final int MANAGEMENT_TRANSITION_CHILD_TO_REGULAR = 1;
    public static final String MANAGEMENT_TRANSITION_COMPLETED_ACTION =
            "org.chromium.arc.MANAGEMENT_TRANSITION_COMPLETED";
    public static final int MANAGEMENT_TRANSITION_NONE = 0;
    public static final int MANAGEMENT_TRANSITION_REGULAR_TO_CHILD = 2;
    public static final int MANAGEMENT_TRANSITION_UNMANAGED_TO_MANAGED = 3;
    public static final String CLOUDDPC_PACKAGE =
            "com.google.android.apps.work.clouddpc.arc";
    public static final String CLOUDDPC_ADMIN_RECEIVER =
            "com.google.android.apps.work.clouddpc.arc.receivers.ArcDeviceAdminReceiver";
    public static final ComponentName CLOUDDPC_COMPONENT_NAME =
            new ComponentName(CLOUDDPC_PACKAGE, CLOUDDPC_ADMIN_RECEIVER);

    public static Account getMainAccount(Context context) {
        return null;
    }

    public static String getMainAccountName(Context context) {
        return null;
    }

    public static int getMainAccountResolutionStatus(Context context) {
        return 0; // no accounts
    }

    public static String getAccountTypesInUse(Context context) {
        return "";
    }

    public static List<String> getGoogleAccountNames(Context context) {
        return Collections.emptyList();
    }

    public static Optional<Account> getGoogleAccountByName(Context context, String name) {
        return Optional.empty();
    }

    public static Stream<Account> findAccountsWithDeprecatedHashCode(Context context) {
        return Stream.empty();
    }

    public static String hashAccountName(String str) {
        return "";
    }

    public static boolean isCheckinExpected() {
        return false;
    }

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Secure.getInt(
                context.getContentResolver(), "user_setup_complete", 0) == 1;
    }

    public static boolean isMainAccount(Context context, String str) {
        return false;
    }

    public static boolean isMainAccountExpected(Context context) {
        return false;
    }

    public static boolean isMainAccountUnknown(Context context) {
        return false;
    }

    public static boolean isTransitioning(Context context) {
        return false;
    }

    public static boolean isTransitioningToManaged(Context context) {
        return false;
    }

    public static boolean isTransitioningToRegular(Context context) {
        return false;
    }

    public static boolean isDeprecatedHashMissing() {
        return true;
    }

    public static void setMainAccount(String str) {
        // no-op
    }

    public static boolean setWorkAccountAsMain(Context context) {
        return false;
    }

    public static void resetMainAccountForTest() {
        // no-op
    }
}

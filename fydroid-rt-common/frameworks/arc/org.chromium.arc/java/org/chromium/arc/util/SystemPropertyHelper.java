package org.chromium.arc.util;

import android.os.SystemProperties;
import android.util.Log;

public class SystemPropertyHelper {

    static final String DEPRECATED_PROPERTY_NAME_MAIN_ACCOUNT_HASH =
            "persist.sys.arc.main_acc_hash2";

    private static final String TAG = "ArcSystemPropertyHelper";

    private SystemPropertyHelper() {
    }

    public static String getDeprecatedMainAccountHash() {
        return SystemProperties.get(DEPRECATED_PROPERTY_NAME_MAIN_ACCOUNT_HASH);
    }

    public static boolean getForceReauthOnNextBoot() {
        return SystemProperties.getBoolean("persist.sys.arc.force_reauth", false);
    }

    public static String getMainAccountHashCode() {
        return SystemProperties.get("persist.sys.arc.main_acc_hash3");
    }

    public static int getMainAccountType() {
        return SystemProperties.getInt("persist.sys.arc.main_acct_type", 0);
    }

    public static int getManagementTransition(int defaultValue) {
        return SystemProperties.getInt("ro.boot.supervision.transition", defaultValue);
    }

    public static boolean isDeveloperModeOn() {
        return SystemProperties.getInt("ro.boot.dev_mode", 0) == 1;
    }

    public static boolean isLoginEnforced() {
        return SystemProperties.getBoolean("persist.sys.arc.enforce_login", true);
    }

    public static boolean isMainAccountManaged() {
        int type = getMainAccountType();
        if (type == 5 || type == 2 || type == 4) {
            return true;
        }
        return SystemProperties.getBoolean("persist.sys.arc.main_acc_managed", false);
    }

    public static boolean isMainAccountOfflineDemo() {
        return getMainAccountType() == 5;
    }

    // fydroid-rt: true when the session is an ARC kiosk. ChromeAccountType
    // ROBOT_ACCOUNT (2) is the kiosk/service account; ArcAuthService mirrors the
    // bridge AccountInfo.account_type into persist.sys.arc.main_acct_type. Used
    // to force the kiosk app fullscreen + pinned.
    public static boolean isKiosk() {
        return getMainAccountType() == 2;
    }

    public static boolean isManagementTransitionCompleted() {
        return SystemProperties.getBoolean("dev.management.transition.done", false);
    }

    public static boolean isProvisioningPaused() {
        return isDeveloperModeOn()
                && SystemProperties.getInt("ro.boot.pause_provisioning", 0) == 1;
    }

    public static void setAccountCheckResult(String result) {
        setProperty("dev.arc.accountcheck.result", result);
    }

    private static void setBooleanProperty(String name, boolean value) {
        setProperty(name, String.valueOf(value));
    }

    public static void setBootCompleted() {
        if (SystemProperties.get("ro.vendor.arc.boot_completed").isEmpty()) {
            SystemProperties.set("ro.vendor.arc.boot_completed", "1");
        }
    }

    public static void setCheckinResult(String result) {
        setProperty("dev.arc.accountcheckin.result", result);
    }

    public static void setForceReauthOnNextBoot(boolean force) {
        setBooleanProperty("persist.sys.arc.force_reauth", force);
    }

    public static void setLoginEnforced(boolean enforced) {
        setBooleanProperty("persist.sys.arc.enforce_login", enforced);
    }

    public static void setMainAccountHashCode(String hash) {
        setProperty("persist.sys.arc.main_acc_hash3", hash);
    }

    public static void setMainAccountManaged(boolean managed) {
        setBooleanProperty("persist.sys.arc.main_acc_managed", managed);
    }

    public static void setMainAccountType(int type) {
        setProperty("persist.sys.arc.main_acct_type", String.valueOf(type));
    }

    public static void setManagementTransitionCompleted() {
        setBooleanProperty("dev.management.transition.done", true);
    }

    public static void setNetworkWaitResult(String result) {
        setProperty("dev.arc.networkwait.result", result);
    }

    private static void setProperty(String name, String value) {
        if (value != null && value.length() > 91) {
            Log.wtf(TAG, "Failed to save result in '" + name
                    + "' system property: " + value);
            return;
        }
        SystemProperties.set(name, value);
        Log.d(TAG, "Set system property '" + name + "' to '" + value + "'");
    }

    public static void setSignInResult(String result) {
        setProperty("dev.arc.accountsignin.result", result);
    }

    public static void setUReadAheadExit() {
        setProperty("dev.arc.ureadahead.exit", "1");
    }

    public static void unsetDeprecatedMainAccountHash() {
        setProperty(DEPRECATED_PROPERTY_NAME_MAIN_ACCOUNT_HASH, "");
    }
}

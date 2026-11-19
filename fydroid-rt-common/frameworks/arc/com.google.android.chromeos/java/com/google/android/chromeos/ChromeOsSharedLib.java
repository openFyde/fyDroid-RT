/* Open source replacement for com.google.android.chromeos.ChromeOsSharedLib from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Shared library constants and helpers used across the ChromeOS companion
 * library. Simple holder for error codes, version number, and bit-test
 * helper — public surface is preserved 1:1 with the proprietary class.
 */
public final class ChromeOsSharedLib {
    public static final int ERROR_FUNCTION_NOT_AVAILABLE = -2;
    public static final int ERROR_FUNCTION_NOT_IMPLEMENTED = -4;
    public static final int ERROR_INCORRECT_PLATFORM = -1;
    public static final int ERROR_INVALID_OPTION = -6;
    public static final int ERROR_NO_WINDOW = -7;
    public static final int ERROR_REMOTE_EXCEPTION = -3;
    public static final int ERROR_UNSUPPORTED_DISPLAY_ID = -5;
    public static final int SUCCESS = 0;

    private static final int VERSION = 3;
    public static final int VERSION_ANDROID_T_VPN_SERVICE_BUILDER = 3;
    public static final int VERSION_LOW_LATENCY_SUPPORT = 2;

    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusCode {
    }

    private ChromeOsSharedLib() {
    }

    public static int version() {
        return VERSION;
    }

    public static boolean isFlagSet(int value, int flag) {
        return (value & flag) == flag;
    }
}

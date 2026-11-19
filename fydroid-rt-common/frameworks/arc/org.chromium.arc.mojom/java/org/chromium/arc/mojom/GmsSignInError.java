package org.chromium.arc.mojom;

public final class GmsSignInError {
    public static final int GMS_SIGN_IN_BAD_AUTHENTICATION = 3;
    public static final int GMS_SIGN_IN_FAILED = 4;
    public static final int GMS_SIGN_IN_INTERNAL_ERROR = 6;
    public static final int GMS_SIGN_IN_NETWORK_ERROR = 1;
    public static final int GMS_SIGN_IN_SERVICE_UNAVAILABLE = 2;
    public static final int GMS_SIGN_IN_TIMEOUT = 5;

    private GmsSignInError() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

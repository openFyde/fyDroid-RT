package org.chromium.arc.mojom;

public final class InstallErrorReason {
    public static final int NOT_APPROVED = 5;
    public static final int NOT_AVAILABLE_IN_COUNTRY = 7;
    public static final int NOT_COMPATIBLE_WITH_DEVICE = 4;
    public static final int NOT_ENROLLED = 9;
    public static final int NOT_FOUND = 3;
    public static final int NO_LICENSES_REMAINING = 8;
    public static final int PERMISSIONS_NOT_ACCEPTED = 6;
    public static final int REASON_UNSPECIFIED = 0;
    public static final int TIMEOUT = 1;
    public static final int TRANSIENT_ERROR = 2;
    public static final int USER_INVALID = 10;

    private InstallErrorReason() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

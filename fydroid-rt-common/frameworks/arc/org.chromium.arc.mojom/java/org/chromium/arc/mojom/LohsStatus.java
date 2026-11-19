package org.chromium.arc.mojom;

public final class LohsStatus {
    public static final int ERROR_CONFIGURING_PLATFORM = 6;
    public static final int ERROR_GENERIC = 2;
    public static final int ERROR_INCOMPATIBLE_MODE = 3;
    public static final int ERROR_INVALID_ARGUMENT = 5;
    public static final int ERROR_TETHERING_DISALLOWED = 4;
    public static final int SUCCESS = 1;
    public static final int UNKNOWN = 0;

    private LohsStatus() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

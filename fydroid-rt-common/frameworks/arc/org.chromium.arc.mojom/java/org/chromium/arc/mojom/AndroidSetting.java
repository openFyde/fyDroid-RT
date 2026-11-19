package org.chromium.arc.mojom;

public final class AndroidSetting {
    public static final int GEO_LOCATION = 1;
    public static final int GEO_LOCATION_ACCURACY_USER_TRIGGERED = 4;
    public static final int GEO_LOCATION_AT_BOOT = 2;
    public static final int GEO_LOCATION_USER_TRIGGERED = 3;
    public static final int UNKNOWN = 0;

    private AndroidSetting() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

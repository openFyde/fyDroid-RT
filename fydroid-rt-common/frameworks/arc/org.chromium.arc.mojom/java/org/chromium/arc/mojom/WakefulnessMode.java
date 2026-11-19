package org.chromium.arc.mojom;

public final class WakefulnessMode {
    public static final int ASLEEP = 0;
    public static final int AWAKE = 1;
    public static final int DOZING = 3;
    public static final int DREAMING = 2;
    public static final int UNKNOWN = -1;

    private WakefulnessMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == -1 || i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class RoundedWindowCompatStrategy {
    public static final int BOTTOM_ONLY_GESTURE = 1;
    public static final int DISABLED = 0;
    public static final int LEFT_RIGHT_BOTTOM_GESTURE = 2;

    private RoundedWindowCompatStrategy() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

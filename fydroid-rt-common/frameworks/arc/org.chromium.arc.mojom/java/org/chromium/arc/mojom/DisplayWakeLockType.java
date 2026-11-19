package org.chromium.arc.mojom;

public final class DisplayWakeLockType {
    public static final int BRIGHT = 0;
    public static final int DIM = 1;

    private DisplayWakeLockType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

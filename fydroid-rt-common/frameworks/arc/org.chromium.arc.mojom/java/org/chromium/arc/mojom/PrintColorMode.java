package org.chromium.arc.mojom;

public final class PrintColorMode {
    public static final int COLOR = 2;
    public static final int MONOCHROME = 1;

    private PrintColorMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

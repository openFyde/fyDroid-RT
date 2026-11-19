package org.chromium.arc.mojom;

public final class PrintDuplexMode {
    public static final int LONG_EDGE = 2;
    public static final int NONE = 1;
    public static final int SHORT_EDGE = 4;

    private PrintDuplexMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2 || i == 4;
    }

    public static void validate(int i) {
    }
}

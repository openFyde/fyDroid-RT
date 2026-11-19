package org.chromium.arc.mojom;

public final class WindowSizeType {
    public static final int MAXIMIZE = 3;
    public static final int PHONE_SIZE = 1;
    public static final int TABLET_SIZE = 2;
    public static final int UNKNOWN = 0;

    private WindowSizeType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

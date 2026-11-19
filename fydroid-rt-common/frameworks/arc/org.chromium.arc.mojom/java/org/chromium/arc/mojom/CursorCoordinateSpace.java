package org.chromium.arc.mojom;

public final class CursorCoordinateSpace {
    public static final int DISPLAY = 1;
    public static final int NOTIFICATION = 2;
    public static final int SCREEN = 0;

    private CursorCoordinateSpace() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

package org.chromium.ax.android.mojom;

public final class AccessibilitySelectionMode {
    public static final int INVALID_ENUM_VALUE = 3;
    public static final int MULTIPLE = 2;
    public static final int NONE = 0;
    public static final int SINGLE = 1;

    private AccessibilitySelectionMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

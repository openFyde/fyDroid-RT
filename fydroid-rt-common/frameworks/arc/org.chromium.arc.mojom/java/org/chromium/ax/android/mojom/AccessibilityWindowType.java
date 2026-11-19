package org.chromium.ax.android.mojom;

public final class AccessibilityWindowType {
    public static final int INVALID_ENUM_VALUE = 5;
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 0;
    public static final int TYPE_APPLICATION = 1;
    public static final int TYPE_INPUT_METHOD = 2;
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 3;
    public static final int TYPE_SYSTEM = 4;

    private AccessibilityWindowType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
    }
}

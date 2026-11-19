package org.chromium.ax.android.mojom;

public final class AccessibilityStringListProperty {
    public static final int CUSTOM_ACTION_DESCRIPTIONS = 0;
    public static final int INVALID_ENUM_VALUE = 1;

    private AccessibilityStringListProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

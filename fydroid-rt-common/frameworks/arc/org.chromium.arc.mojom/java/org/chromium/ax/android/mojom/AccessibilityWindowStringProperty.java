package org.chromium.ax.android.mojom;

public final class AccessibilityWindowStringProperty {
    public static final int INVALID_ENUM_VALUE = 1;
    public static final int TITLE = 0;

    private AccessibilityWindowStringProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

package org.chromium.ax.android.mojom;

public final class SpanType {
    public static final int CLICKABLE = 1;
    public static final int INVALID_ENUM_VALUE = 2;
    public static final int URL = 0;

    private SpanType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

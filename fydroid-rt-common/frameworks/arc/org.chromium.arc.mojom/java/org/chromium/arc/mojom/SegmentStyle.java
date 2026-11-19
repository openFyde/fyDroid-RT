package org.chromium.arc.mojom;

public final class SegmentStyle {
    public static final int DEFAULT = 0;
    public static final int EMPHASIZED = 1;
    public static final int NONE = 2;

    private SegmentStyle() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

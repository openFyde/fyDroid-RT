package org.chromium.arc.mojom;

public final class ArcNotificationExpandState {
    public static final int COLLAPSED = 1;
    public static final int EXPANDED = 2;
    public static final int FIXED_SIZE = 0;

    private ArcNotificationExpandState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

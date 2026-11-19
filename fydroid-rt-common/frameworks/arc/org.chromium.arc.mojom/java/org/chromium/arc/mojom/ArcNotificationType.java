package org.chromium.arc.mojom;

public final class ArcNotificationType {
    public static final int BUNDLED = 4;
    public static final int DEPRECATED_IMAGE = 1;
    public static final int DEPRECATED_LIST = 3;
    public static final int DEPRECATED_PROGRESS = 2;
    public static final int SIMPLE = 0;

    private ArcNotificationType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class ArcNotificationPriority {
    public static final int DEFAULT = 0;
    public static final int HIGH = 1;
    public static final int LOW = -1;
    public static final int MAX = 2;
    public static final int MIN = -2;
    public static final int NONE = -3;

    private ArcNotificationPriority() {
    }

    public static boolean isKnownValue(int i) {
        return i == -3 || i == -2 || i == -1 || i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

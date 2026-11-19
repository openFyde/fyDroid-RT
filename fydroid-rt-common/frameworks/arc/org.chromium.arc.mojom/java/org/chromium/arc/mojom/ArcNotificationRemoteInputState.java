package org.chromium.arc.mojom;

public final class ArcNotificationRemoteInputState {
    public static final int CLOSED = 0;
    public static final int OPENED = 1;

    private ArcNotificationRemoteInputState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

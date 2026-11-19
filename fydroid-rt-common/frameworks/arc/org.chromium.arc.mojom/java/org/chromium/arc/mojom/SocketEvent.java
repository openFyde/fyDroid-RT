package org.chromium.arc.mojom;

public final class SocketEvent {
    public static final int CLOSE = 2;
    public static final int OPEN = 1;
    public static final int UNKNOWN = 0;

    private SocketEvent() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

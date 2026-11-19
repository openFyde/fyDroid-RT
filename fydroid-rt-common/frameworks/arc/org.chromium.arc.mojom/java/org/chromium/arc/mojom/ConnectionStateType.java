package org.chromium.arc.mojom;

public final class ConnectionStateType {
    public static final int CONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int NOT_CONNECTED = 2;
    public static final int ONLINE = 4;
    public static final int PORTAL = 3;

    private ConnectionStateType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

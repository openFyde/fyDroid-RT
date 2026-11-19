package org.chromium.arc.mojom;

public final class BluetoothAclState {
    public static final int CONNECTED = 0;
    public static final int DISCONNECTED = 1;

    private BluetoothAclState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

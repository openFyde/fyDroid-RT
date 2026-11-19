package org.chromium.arc.mojom;

public final class BluetoothDiscoveryState {
    public static final int STARTED = 1;
    public static final int STOPPED = 0;

    private BluetoothDiscoveryState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

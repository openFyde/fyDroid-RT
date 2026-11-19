package org.chromium.arc.mojom;

public final class BluetoothAdapterState {
    public static final int OFF = 0;
    public static final int ON = 1;

    private BluetoothAdapterState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

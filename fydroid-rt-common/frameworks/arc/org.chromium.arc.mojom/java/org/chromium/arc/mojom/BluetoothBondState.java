package org.chromium.arc.mojom;

public final class BluetoothBondState {
    public static final int BONDED = 2;
    public static final int BONDING = 1;
    public static final int NONE = 0;

    private BluetoothBondState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

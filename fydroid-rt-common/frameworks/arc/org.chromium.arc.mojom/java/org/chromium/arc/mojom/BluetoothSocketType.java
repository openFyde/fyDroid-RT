package org.chromium.arc.mojom;

public final class BluetoothSocketType {
    public static final int TYPE_L2CAP_LE = 4;
    public static final int TYPE_RFCOMM = 1;

    private BluetoothSocketType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 4;
    }

    public static void validate(int i) {
    }
}

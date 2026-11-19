package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class BluetoothAdvertisementType {
    public static final int ADV_TYPE_CONNECTABLE = 0;
    public static final int ADV_TYPE_NON_CONNECTABLE = 3;
    public static final int ADV_TYPE_SCANNABLE = 2;

    private BluetoothAdvertisementType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

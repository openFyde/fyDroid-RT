package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbControlTransferType {
    public static final int CLASS = 1;
    public static final int RESERVED = 3;
    public static final int STANDARD = 0;
    public static final int VENDOR = 2;

    private UsbControlTransferType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

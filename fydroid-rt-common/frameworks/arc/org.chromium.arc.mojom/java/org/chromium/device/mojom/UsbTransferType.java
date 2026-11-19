package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbTransferType {
    public static final int BULK = 2;
    public static final int CONTROL = 0;
    public static final int INTERRUPT = 3;
    public static final int ISOCHRONOUS = 1;

    private UsbTransferType() {
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

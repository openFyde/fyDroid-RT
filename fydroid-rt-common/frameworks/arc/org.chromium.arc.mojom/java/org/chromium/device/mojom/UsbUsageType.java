package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbUsageType {
    public static final int DATA = 0;
    public static final int EXPLICIT_FEEDBACK = 2;
    public static final int FEEDBACK = 1;
    public static final int NOTIFICATION = 4;
    public static final int PERIODIC = 3;
    public static final int RESERVED = 5;

    private UsbUsageType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

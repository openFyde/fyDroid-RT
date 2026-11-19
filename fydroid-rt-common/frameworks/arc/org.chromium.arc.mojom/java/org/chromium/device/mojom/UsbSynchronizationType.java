package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbSynchronizationType {
    public static final int ADAPTIVE = 2;
    public static final int ASYNCHRONOUS = 1;
    public static final int NONE = 0;
    public static final int SYNCHRONOUS = 3;

    private UsbSynchronizationType() {
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

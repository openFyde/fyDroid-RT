package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbTransferStatus {
    public static final int BABBLE = 6;
    public static final int CANCELLED = 3;
    public static final int COMPLETED = 0;
    public static final int DISCONNECT = 5;
    public static final int PERMISSION_DENIED = 8;
    public static final int SHORT_PACKET = 7;
    public static final int STALLED = 4;
    public static final int TIMEOUT = 2;
    public static final int TRANSFER_ERROR = 1;

    private UsbTransferStatus() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

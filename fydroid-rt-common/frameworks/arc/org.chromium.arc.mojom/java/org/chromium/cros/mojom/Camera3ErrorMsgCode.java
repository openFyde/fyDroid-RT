package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3ErrorMsgCode {
    public static final int CAMERA3_MSG_ERROR_BUFFER = 4;
    public static final int CAMERA3_MSG_ERROR_DEVICE = 1;
    public static final int CAMERA3_MSG_ERROR_REQUEST = 2;
    public static final int CAMERA3_MSG_ERROR_RESULT = 3;

    private Camera3ErrorMsgCode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

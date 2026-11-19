package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3BufferStatus {
    public static final int CAMERA3_BUFFER_STATUS_ERROR = 1;
    public static final int CAMERA3_BUFFER_STATUS_OK = 0;

    private Camera3BufferStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

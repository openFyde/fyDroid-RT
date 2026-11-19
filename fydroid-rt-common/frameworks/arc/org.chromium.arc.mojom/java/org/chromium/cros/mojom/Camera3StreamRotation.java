package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3StreamRotation {
    public static final int CAMERA3_STREAM_ROTATION_0 = 0;
    public static final int CAMERA3_STREAM_ROTATION_180 = 2;
    public static final int CAMERA3_STREAM_ROTATION_270 = 3;
    public static final int CAMERA3_STREAM_ROTATION_90 = 1;

    private Camera3StreamRotation() {
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

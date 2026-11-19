package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class CameraFacing {
    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_EXTERNAL = 2;
    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_FACING_VIRTUAL_BACK = 3;
    public static final int CAMERA_FACING_VIRTUAL_EXTERNAL = 5;
    public static final int CAMERA_FACING_VIRTUAL_FRONT = 4;

    private CameraFacing() {
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

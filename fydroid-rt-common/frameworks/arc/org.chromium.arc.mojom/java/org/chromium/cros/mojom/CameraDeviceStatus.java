package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class CameraDeviceStatus {
    public static final int CAMERA_DEVICE_STATUS_ENUMERATING = 2;
    public static final int CAMERA_DEVICE_STATUS_NOT_PRESENT = 0;
    public static final int CAMERA_DEVICE_STATUS_PRESENT = 1;

    private CameraDeviceStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

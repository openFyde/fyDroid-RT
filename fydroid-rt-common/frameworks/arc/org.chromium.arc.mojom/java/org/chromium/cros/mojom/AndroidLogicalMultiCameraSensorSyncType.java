package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidLogicalMultiCameraSensorSyncType {
    public static final int ANDROID_LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE_APPROXIMATE = 0;
    public static final int ANDROID_LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE_CALIBRATED = 1;

    private AndroidLogicalMultiCameraSensorSyncType() {
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

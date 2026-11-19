package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidSensorInfoLensShadingApplied {
    public static final int ANDROID_SENSOR_INFO_LENS_SHADING_APPLIED_FALSE = 0;
    public static final int ANDROID_SENSOR_INFO_LENS_SHADING_APPLIED_TRUE = 1;

    private AndroidSensorInfoLensShadingApplied() {
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

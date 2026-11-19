package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidSensorInfoColorFilterArrangement {
    public static final int ANDROID_SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR = 3;
    public static final int ANDROID_SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG = 2;
    public static final int ANDROID_SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG = 1;
    public static final int ANDROID_SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGB = 4;
    public static final int ANDROID_SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB = 0;

    private AndroidSensorInfoColorFilterArrangement() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidRequestAvailableCapabilities {
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE = 0;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE = 6;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO = 9;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT = 8;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA = 11;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING = 2;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR = 1;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME = 12;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING = 10;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING = 4;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_RAW = 3;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS = 5;
    public static final int ANDROID_REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING = 7;

    private AndroidRequestAvailableCapabilities() {
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
            case 9:
            case 10:
            case 11:
            case 12:
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

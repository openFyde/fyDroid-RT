package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class CameraMetadataSection {
    public static final int ANDROID_BLACK_LEVEL = 22;
    public static final int ANDROID_COLOR_CORRECTION = 0;
    public static final int ANDROID_CONTROL = 1;
    public static final int ANDROID_DEMOSAIC = 2;
    public static final int ANDROID_DEPTH = 25;
    public static final int ANDROID_DISTORTION_CORRECTION = 27;
    public static final int ANDROID_EDGE = 3;
    public static final int ANDROID_FLASH = 4;
    public static final int ANDROID_FLASH_INFO = 5;
    public static final int ANDROID_HOT_PIXEL = 6;
    public static final int ANDROID_INFO = 21;
    public static final int ANDROID_JPEG = 7;
    public static final int ANDROID_LED = 20;
    public static final int ANDROID_LENS = 8;
    public static final int ANDROID_LENS_INFO = 9;
    public static final int ANDROID_LOGICAL_MULTI_CAMERA = 26;
    public static final int ANDROID_NOISE_REDUCTION = 10;
    public static final int ANDROID_QUIRKS = 11;
    public static final int ANDROID_REPROCESS = 24;
    public static final int ANDROID_REQUEST = 12;
    public static final int ANDROID_SCALER = 13;
    public static final int ANDROID_SECTION_COUNT = 28;
    public static final int ANDROID_SENSOR = 14;
    public static final int ANDROID_SENSOR_INFO = 15;
    public static final int ANDROID_SHADING = 16;
    public static final int ANDROID_STATISTICS = 17;
    public static final int ANDROID_STATISTICS_INFO = 18;
    public static final int ANDROID_SYNC = 23;
    public static final int ANDROID_TONEMAP = 19;
    public static final int VENDOR_SECTION = 32768;

    private CameraMetadataSection() {
    }

    public static boolean isKnownValue(int i) {
        if (i == 32768) {
            return true;
        }
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
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
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

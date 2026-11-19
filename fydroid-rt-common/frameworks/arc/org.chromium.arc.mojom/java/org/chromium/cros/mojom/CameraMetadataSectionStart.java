package org.chromium.cros.mojom;

public final class CameraMetadataSectionStart {
    public static final int ANDROID_BLACK_LEVEL_START = 1441792;
    public static final int ANDROID_COLOR_CORRECTION_START = 0;
    public static final int ANDROID_CONTROL_START = 65536;
    public static final int ANDROID_DEMOSAIC_START = 131072;
    public static final int ANDROID_DEPTH_START = 1638400;
    public static final int ANDROID_DISTORTION_CORRECTION_START = 1769472;
    public static final int ANDROID_EDGE_START = 196608;
    public static final int ANDROID_FLASH_INFO_START = 327680;
    public static final int ANDROID_FLASH_START = 262144;
    public static final int ANDROID_HOT_PIXEL_START = 393216;
    public static final int ANDROID_INFO_START = 1376256;
    public static final int ANDROID_JPEG_START = 458752;
    public static final int ANDROID_LED_START = 1310720;
    public static final int ANDROID_LENS_INFO_START = 589824;
    public static final int ANDROID_LENS_START = 524288;
    public static final int ANDROID_LOGICAL_MULTI_CAMERA_START = 1703936;
    public static final int ANDROID_NOISE_REDUCTION_START = 655360;
    public static final int ANDROID_QUIRKS_START = 720896;
    public static final int ANDROID_REPROCESS_START = 1572864;
    public static final int ANDROID_REQUEST_START = 786432;
    public static final int ANDROID_SCALER_START = 851968;
    public static final int ANDROID_SENSOR_INFO_START = 983040;
    public static final int ANDROID_SENSOR_START = 917504;
    public static final int ANDROID_SHADING_START = 1048576;
    public static final int ANDROID_STATISTICS_INFO_START = 1179648;
    public static final int ANDROID_STATISTICS_START = 1114112;
    public static final int ANDROID_SYNC_START = 1507328;
    public static final int ANDROID_TONEMAP_START = 1245184;

    private CameraMetadataSectionStart() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 65536:
            case 131072:
            case 196608:
            case 262144:
            case 327680:
            case 393216:
            case 458752:
            case 524288:
            case 589824:
            case 655360:
            case 720896:
            case 786432:
            case 851968:
            case 917504:
            case 983040:
            case 1048576:
            case 1114112:
            case 1179648:
            case 1245184:
            case 1310720:
            case 1376256:
            case 1441792:
            case 1507328:
            case 1572864:
            case 1638400:
            case 1703936:
            case 1769472:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

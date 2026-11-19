package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlCaptureIntent {
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_CUSTOM = 0;
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_MANUAL = 6;
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_MOTION_TRACKING = 7;
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_PREVIEW = 1;
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_STILL_CAPTURE = 2;
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_VIDEO_RECORD = 3;
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_VIDEO_SNAPSHOT = 4;
    public static final int ANDROID_CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG = 5;

    private AndroidControlCaptureIntent() {
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

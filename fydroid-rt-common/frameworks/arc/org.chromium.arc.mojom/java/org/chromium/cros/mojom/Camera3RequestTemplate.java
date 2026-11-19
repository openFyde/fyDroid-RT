package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3RequestTemplate {
    public static final int CAMERA3_TEMPLATE_COUNT = 7;
    public static final int CAMERA3_TEMPLATE_MANUAL = 6;
    public static final int CAMERA3_TEMPLATE_PREVIEW = 1;
    public static final int CAMERA3_TEMPLATE_STILL_CAPTURE = 2;
    public static final int CAMERA3_TEMPLATE_VIDEO_RECORD = 3;
    public static final int CAMERA3_TEMPLATE_VIDEO_SNAPSHOT = 4;
    public static final int CAMERA3_TEMPLATE_ZERO_SHUTTER_LAG = 5;

    private Camera3RequestTemplate() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
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

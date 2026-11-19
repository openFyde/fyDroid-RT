package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlSceneMode {
    public static final int ANDROID_CONTROL_SCENE_MODE_ACTION = 2;
    public static final int ANDROID_CONTROL_SCENE_MODE_BARCODE = 16;
    public static final int ANDROID_CONTROL_SCENE_MODE_BEACH = 8;
    public static final int ANDROID_CONTROL_SCENE_MODE_CANDLELIGHT = 15;
    public static final int ANDROID_CONTROL_SCENE_MODE_DEVICE_CUSTOM_END = 127;
    public static final int ANDROID_CONTROL_SCENE_MODE_DEVICE_CUSTOM_START = 100;
    public static final int ANDROID_CONTROL_SCENE_MODE_DISABLED = 0;
    public static final int ANDROID_CONTROL_SCENE_MODE_FACE_PRIORITY = 1;
    public static final int ANDROID_CONTROL_SCENE_MODE_FACE_PRIORITY_LOW_LIGHT = 19;
    public static final int ANDROID_CONTROL_SCENE_MODE_FIREWORKS = 12;
    public static final int ANDROID_CONTROL_SCENE_MODE_HDR = 18;
    public static final int ANDROID_CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO = 17;
    public static final int ANDROID_CONTROL_SCENE_MODE_LANDSCAPE = 4;
    public static final int ANDROID_CONTROL_SCENE_MODE_NIGHT = 5;
    public static final int ANDROID_CONTROL_SCENE_MODE_NIGHT_PORTRAIT = 6;
    public static final int ANDROID_CONTROL_SCENE_MODE_PARTY = 14;
    public static final int ANDROID_CONTROL_SCENE_MODE_PORTRAIT = 3;
    public static final int ANDROID_CONTROL_SCENE_MODE_SNOW = 9;
    public static final int ANDROID_CONTROL_SCENE_MODE_SPORTS = 13;
    public static final int ANDROID_CONTROL_SCENE_MODE_STEADYPHOTO = 11;
    public static final int ANDROID_CONTROL_SCENE_MODE_SUNSET = 10;
    public static final int ANDROID_CONTROL_SCENE_MODE_THEATRE = 7;

    private AndroidControlSceneMode() {
    }

    public static boolean isKnownValue(int i) {
        if (i == 100 || i == 127) {
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

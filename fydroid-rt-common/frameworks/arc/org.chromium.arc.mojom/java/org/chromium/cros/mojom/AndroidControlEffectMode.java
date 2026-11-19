package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlEffectMode {
    public static final int ANDROID_CONTROL_EFFECT_MODE_AQUA = 8;
    public static final int ANDROID_CONTROL_EFFECT_MODE_BLACKBOARD = 7;
    public static final int ANDROID_CONTROL_EFFECT_MODE_MONO = 1;
    public static final int ANDROID_CONTROL_EFFECT_MODE_NEGATIVE = 2;
    public static final int ANDROID_CONTROL_EFFECT_MODE_OFF = 0;
    public static final int ANDROID_CONTROL_EFFECT_MODE_POSTERIZE = 5;
    public static final int ANDROID_CONTROL_EFFECT_MODE_SEPIA = 4;
    public static final int ANDROID_CONTROL_EFFECT_MODE_SOLARIZE = 3;
    public static final int ANDROID_CONTROL_EFFECT_MODE_WHITEBOARD = 6;

    private AndroidControlEffectMode() {
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

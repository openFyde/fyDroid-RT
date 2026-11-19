package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlMode {
    public static final int ANDROID_CONTROL_MODE_AUTO = 1;
    public static final int ANDROID_CONTROL_MODE_OFF = 0;
    public static final int ANDROID_CONTROL_MODE_OFF_KEEP_STATE = 3;
    public static final int ANDROID_CONTROL_MODE_USE_SCENE_MODE = 2;

    private AndroidControlMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

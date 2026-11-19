package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAfSceneChange {
    public static final int ANDROID_CONTROL_AF_SCENE_CHANGE_DETECTED = 1;
    public static final int ANDROID_CONTROL_AF_SCENE_CHANGE_NOT_DETECTED = 0;

    private AndroidControlAfSceneChange() {
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

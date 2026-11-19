package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidTonemapMode {
    public static final int ANDROID_TONEMAP_MODE_CONTRAST_CURVE = 0;
    public static final int ANDROID_TONEMAP_MODE_FAST = 1;
    public static final int ANDROID_TONEMAP_MODE_GAMMA_VALUE = 3;
    public static final int ANDROID_TONEMAP_MODE_HIGH_QUALITY = 2;
    public static final int ANDROID_TONEMAP_MODE_PRESET_CURVE = 4;

    private AndroidTonemapMode() {
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

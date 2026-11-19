package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidColorCorrectionAberrationMode {
    public static final int ANDROID_COLOR_CORRECTION_ABERRATION_MODE_FAST = 1;
    public static final int ANDROID_COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY = 2;
    public static final int ANDROID_COLOR_CORRECTION_ABERRATION_MODE_OFF = 0;

    private AndroidColorCorrectionAberrationMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

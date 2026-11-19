package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidTonemapPresetCurve {
    public static final int ANDROID_TONEMAP_PRESET_CURVE_REC709 = 1;
    public static final int ANDROID_TONEMAP_PRESET_CURVE_SRGB = 0;

    private AndroidTonemapPresetCurve() {
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

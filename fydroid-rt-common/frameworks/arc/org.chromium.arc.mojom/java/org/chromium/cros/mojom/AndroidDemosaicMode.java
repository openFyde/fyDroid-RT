package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidDemosaicMode {
    public static final int ANDROID_DEMOSAIC_MODE_FAST = 0;
    public static final int ANDROID_DEMOSAIC_MODE_HIGH_QUALITY = 1;

    private AndroidDemosaicMode() {
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

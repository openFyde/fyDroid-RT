package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidDepthDepthIsExclusive {
    public static final int ANDROID_DEPTH_DEPTH_IS_EXCLUSIVE_FALSE = 0;
    public static final int ANDROID_DEPTH_DEPTH_IS_EXCLUSIVE_TRUE = 1;

    private AndroidDepthDepthIsExclusive() {
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

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlEnableZsl {
    public static final int ANDROID_CONTROL_ENABLE_ZSL_FALSE = 0;
    public static final int ANDROID_CONTROL_ENABLE_ZSL_TRUE = 1;

    private AndroidControlEnableZsl() {
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

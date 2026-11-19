package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAfMode {
    public static final int ANDROID_CONTROL_AF_MODE_AUTO = 1;
    public static final int ANDROID_CONTROL_AF_MODE_CONTINUOUS_PICTURE = 4;
    public static final int ANDROID_CONTROL_AF_MODE_CONTINUOUS_VIDEO = 3;
    public static final int ANDROID_CONTROL_AF_MODE_EDOF = 5;
    public static final int ANDROID_CONTROL_AF_MODE_MACRO = 2;
    public static final int ANDROID_CONTROL_AF_MODE_OFF = 0;

    private AndroidControlAfMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

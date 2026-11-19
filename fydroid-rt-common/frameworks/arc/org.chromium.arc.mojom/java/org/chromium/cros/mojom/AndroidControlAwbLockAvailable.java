package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAwbLockAvailable {
    public static final int ANDROID_CONTROL_AWB_LOCK_AVAILABLE_FALSE = 0;
    public static final int ANDROID_CONTROL_AWB_LOCK_AVAILABLE_TRUE = 1;

    private AndroidControlAwbLockAvailable() {
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

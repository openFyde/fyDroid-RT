package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidFlashInfoAvailable {
    public static final int ANDROID_FLASH_INFO_AVAILABLE_FALSE = 0;
    public static final int ANDROID_FLASH_INFO_AVAILABLE_TRUE = 1;

    private AndroidFlashInfoAvailable() {
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

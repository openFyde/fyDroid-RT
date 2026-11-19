package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class CameraIntentMode {
    public static final int PHOTO = 0;
    public static final int VIDEO = 1;

    private CameraIntentMode() {
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

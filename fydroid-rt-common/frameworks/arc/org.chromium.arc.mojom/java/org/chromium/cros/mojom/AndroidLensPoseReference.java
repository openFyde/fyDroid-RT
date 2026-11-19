package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidLensPoseReference {
    public static final int ANDROID_LENS_POSE_REFERENCE_GYROSCOPE = 1;
    public static final int ANDROID_LENS_POSE_REFERENCE_PRIMARY_CAMERA = 0;

    private AndroidLensPoseReference() {
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

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidScalerAvailableStreamConfigurations {
    public static final int ANDROID_SCALER_AVAILABLE_STREAM_CONFIGURATIONS_INPUT = 1;
    public static final int ANDROID_SCALER_AVAILABLE_STREAM_CONFIGURATIONS_OUTPUT = 0;

    private AndroidScalerAvailableStreamConfigurations() {
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

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidDepthAvailableDepthStreamConfigurations {
    public static final int ANDROID_DEPTH_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS_INPUT = 1;
    public static final int ANDROID_DEPTH_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS_OUTPUT = 0;

    private AndroidDepthAvailableDepthStreamConfigurations() {
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

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidSyncMaxLatency {
    public static final int ANDROID_SYNC_MAX_LATENCY_PER_FRAME_CONTROL = 0;
    public static final int ANDROID_SYNC_MAX_LATENCY_UNKNOWN = -1;

    private AndroidSyncMaxLatency() {
    }

    public static boolean isKnownValue(int i) {
        return i == -1 || i == 0;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

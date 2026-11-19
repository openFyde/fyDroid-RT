package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAwbState {
    public static final int ANDROID_CONTROL_AWB_STATE_CONVERGED = 2;
    public static final int ANDROID_CONTROL_AWB_STATE_INACTIVE = 0;
    public static final int ANDROID_CONTROL_AWB_STATE_LOCKED = 3;
    public static final int ANDROID_CONTROL_AWB_STATE_SEARCHING = 1;

    private AndroidControlAwbState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

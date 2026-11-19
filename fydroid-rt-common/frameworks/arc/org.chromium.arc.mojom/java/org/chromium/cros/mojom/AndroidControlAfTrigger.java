package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAfTrigger {
    public static final int ANDROID_CONTROL_AF_TRIGGER_CANCEL = 2;
    public static final int ANDROID_CONTROL_AF_TRIGGER_IDLE = 0;
    public static final int ANDROID_CONTROL_AF_TRIGGER_START = 1;

    private AndroidControlAfTrigger() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

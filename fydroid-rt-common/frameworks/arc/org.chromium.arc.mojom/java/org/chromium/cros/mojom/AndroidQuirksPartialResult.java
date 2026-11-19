package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidQuirksPartialResult {
    public static final int ANDROID_QUIRKS_PARTIAL_RESULT_FINAL = 0;
    public static final int ANDROID_QUIRKS_PARTIAL_RESULT_PARTIAL = 1;

    private AndroidQuirksPartialResult() {
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

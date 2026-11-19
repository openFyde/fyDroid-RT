package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidStatisticsFaceDetectMode {
    public static final int ANDROID_STATISTICS_FACE_DETECT_MODE_FULL = 2;
    public static final int ANDROID_STATISTICS_FACE_DETECT_MODE_OFF = 0;
    public static final int ANDROID_STATISTICS_FACE_DETECT_MODE_SIMPLE = 1;

    private AndroidStatisticsFaceDetectMode() {
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

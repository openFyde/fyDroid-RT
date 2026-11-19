package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidStatisticsHistogramMode {
    public static final int ANDROID_STATISTICS_HISTOGRAM_MODE_OFF = 0;
    public static final int ANDROID_STATISTICS_HISTOGRAM_MODE_ON = 1;

    private AndroidStatisticsHistogramMode() {
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

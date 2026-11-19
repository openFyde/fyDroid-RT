package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidStatisticsSceneFlicker {
    public static final int ANDROID_STATISTICS_SCENE_FLICKER_50HZ = 1;
    public static final int ANDROID_STATISTICS_SCENE_FLICKER_60HZ = 2;
    public static final int ANDROID_STATISTICS_SCENE_FLICKER_NONE = 0;

    private AndroidStatisticsSceneFlicker() {
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

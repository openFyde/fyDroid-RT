package org.chromium.memory_instrumentation.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class LevelOfDetail {
    public static final int BACKGROUND = 0;
    public static final int DETAILED = 2;
    public static final int LIGHT = 1;

    private LevelOfDetail() {
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

package org.chromium.memory_instrumentation.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class ProcessType {
    public static final int ARC = 6;
    public static final int BROWSER = 1;
    public static final int GPU = 3;
    public static final int OTHER = 0;
    public static final int PLUGIN = 5;
    public static final int RENDERER = 2;
    public static final int UTILITY = 4;

    private ProcessType() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

package org.chromium.memory_instrumentation.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class DumpType {
    public static final int EXPLICITLY_TRIGGERED = 1;
    public static final int PERIODIC_INTERVAL = 0;
    public static final int SUMMARY_ONLY = 2;

    private DumpType() {
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

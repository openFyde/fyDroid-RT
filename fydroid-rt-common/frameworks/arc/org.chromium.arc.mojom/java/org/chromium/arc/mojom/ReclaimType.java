package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class ReclaimType {
    public static final int ALL = 1;
    public static final int ANON = 0;

    private ReclaimType() {
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

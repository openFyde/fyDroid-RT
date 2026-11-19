package org.chromium.ax.android.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class ActionComplexArgumentType {
    public static final int ACCESSIBLE_CLICKABLE_SPAN = 0;

    private ActionComplexArgumentType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

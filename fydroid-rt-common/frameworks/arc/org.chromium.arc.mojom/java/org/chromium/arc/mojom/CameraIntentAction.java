package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class CameraIntentAction {
    public static final int APPEND_DATA = 2;
    public static final int CANCEL = 1;
    public static final int CLEAR_DATA = 3;
    public static final int FINISH = 0;

    private CameraIntentAction() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

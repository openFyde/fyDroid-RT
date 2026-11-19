package org.chromium.ax.android.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class ActionIntArgumentType {
    public static final int COLUMN_INT = 4;
    public static final int MOVEMENT_GRANULARITY_INT = 0;
    public static final int MOVE_WINDOW_X = 5;
    public static final int MOVE_WINDOW_Y = 6;
    public static final int PRESS_AND_HOLD_DURATION_MILLIS_INT = 7;
    public static final int ROW_INT = 3;
    public static final int SELECTION_END_INT = 2;
    public static final int SELECTION_START_INT = 1;

    private ActionIntArgumentType() {
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
            case 7:
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

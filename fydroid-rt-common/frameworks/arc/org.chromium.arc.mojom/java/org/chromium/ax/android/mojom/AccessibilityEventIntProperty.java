package org.chromium.ax.android.mojom;

public final class AccessibilityEventIntProperty {
    public static final int ACTION = 0;
    public static final int CURRENT_ITEM_INDEX = 4;
    public static final int FROM_INDEX = 1;
    public static final int INVALID_ENUM_VALUE = 11;
    public static final int ITEM_COUNT = 3;
    public static final int MAX_SCROLL_X = 7;
    public static final int MAX_SCROLL_Y = 8;
    public static final int SCROLL_DELTA_X = 9;
    public static final int SCROLL_DELTA_Y = 10;
    public static final int SCROLL_X = 5;
    public static final int SCROLL_Y = 6;
    public static final int TO_INDEX = 2;

    private AccessibilityEventIntProperty() {
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
            case 8:
            case 9:
            case 10:
            case 11:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

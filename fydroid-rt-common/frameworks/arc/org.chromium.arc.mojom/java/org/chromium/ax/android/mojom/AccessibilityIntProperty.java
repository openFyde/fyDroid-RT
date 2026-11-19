package org.chromium.ax.android.mojom;

public final class AccessibilityIntProperty {
    public static final int INPUT_TYPE = 7;
    public static final int INVALID_ENUM_VALUE = 9;
    public static final int LABELED_BY = 1;
    public static final int LABEL_FOR = 0;
    public static final int LIVE_REGION = 8;
    public static final int MAX_TEXT_LENGTH = 4;
    public static final int TEXT_SELECTION_END = 6;
    public static final int TEXT_SELECTION_START = 5;
    public static final int TRAVERSAL_AFTER = 3;
    public static final int TRAVERSAL_BEFORE = 2;

    private AccessibilityIntProperty() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

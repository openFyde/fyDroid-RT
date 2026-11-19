package org.chromium.ax.android.mojom;

public final class AccessibilityStringProperty {
    public static final int CHROME_ROLE = 5;
    public static final int CLASS_NAME = 1;
    public static final int CONTENT_DESCRIPTION = 3;
    public static final int HINT_TEXT = 9;
    public static final int INVALID_ENUM_VALUE = 11;
    public static final int PACKAGE_NAME = 0;
    public static final int PANE_TITLE = 8;
    public static final int ROLE_DESCRIPTION = 6;
    public static final int STATE_DESCRIPTION = 10;
    public static final int TEXT = 2;
    public static final int TOOLTIP = 7;
    public static final int VIEW_ID_RESOURCE_NAME = 4;

    private AccessibilityStringProperty() {
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

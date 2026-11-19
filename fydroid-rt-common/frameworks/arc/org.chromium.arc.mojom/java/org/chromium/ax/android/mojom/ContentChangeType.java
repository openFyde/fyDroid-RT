package org.chromium.ax.android.mojom;

public final class ContentChangeType {
    public static final int CONTENT_DESCRIPTION = 0;
    public static final int INVALID_ENUM_VALUE = 8;
    public static final int PANE_APPEARED = 6;
    public static final int PANE_DISAPPEARED = 7;
    public static final int PANE_TITLE = 4;
    public static final int STATE_DESCRIPTION = 1;
    public static final int SUBTREE = 2;
    public static final int TEXT = 3;
    public static final int UNDEFINED = 5;

    private ContentChangeType() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class ArcNotificationStyle {
    public static final int BIG_PICTURE_STYLE = 1;
    public static final int BIG_TEXT_STYLE = 2;
    public static final int CALL_STYLE = 8;
    public static final int DECORATED_CUSTOM_VIEW_STYLE = 6;
    public static final int DECORATED_MEDIA_CUSTOM_VIEW_STYLE = 7;
    public static final int INBOX_STYLE = 4;
    public static final int MEDIA_STYLE = 5;
    public static final int MESSAGING_STYLE = 3;
    public static final int NO_STYLE = 0;

    private ArcNotificationStyle() {
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

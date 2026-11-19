package org.chromium.arc.mojom;

public final class ArcNotificationEvent {
    public static final int ACTIVATED = 8;
    public static final int BODY_CLICKED = 0;
    public static final int CLOSED = 1;
    public static final int DEACTIVATED = 9;
    public static final int DEPRECATED_BUTTON_1_CLICKED = 2;
    public static final int DEPRECATED_BUTTON_2_CLICKED = 3;
    public static final int DEPRECATED_BUTTON_3_CLICKED = 4;
    public static final int DEPRECATED_BUTTON_4_CLICKED = 5;
    public static final int DEPRECATED_BUTTON_5_CLICKED = 6;
    public static final int TOGGLE_EXPANSION = 7;

    private ArcNotificationEvent() {
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

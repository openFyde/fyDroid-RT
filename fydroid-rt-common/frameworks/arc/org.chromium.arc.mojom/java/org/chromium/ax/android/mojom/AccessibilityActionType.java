package org.chromium.ax.android.mojom;

public final class AccessibilityActionType {
    public static final int ACCESSIBILITY_FOCUS = 6;
    public static final int CLEAR_ACCESSIBILITY_FOCUS = 7;
    public static final int CLEAR_FOCUS = 1;
    public static final int CLEAR_SELECTION = 3;
    public static final int CLICK = 4;
    public static final int COLLAPSE = 19;
    public static final int CONTEXT_CLICK = 22;
    public static final int COPY = 14;
    public static final int CUSTOM_ACTION = 30;
    public static final int CUT = 16;
    public static final int DISMISS = 20;
    public static final int EXPAND = 18;
    public static final int FOCUS = 0;
    public static final int GET_TEXT_LOCATION = 31;
    public static final int HIDE_TOOLTIP = 33;
    public static final int INVALID_ENUM_VALUE = 34;
    public static final int LONG_CLICK = 5;
    public static final int NEXT_AT_MOVEMENT_GRANULARITY = 8;
    public static final int NEXT_HTML_ELEMENT = 10;
    public static final int PASTE = 15;
    public static final int PREVIOUS_AT_MOVEMENT_GRANULARITY = 9;
    public static final int PREVIOUS_HTML_ELEMENT = 11;
    public static final int SCROLL_BACKWARD = 13;
    public static final int SCROLL_DOWN = 23;
    public static final int SCROLL_FORWARD = 12;
    public static final int SCROLL_LEFT = 24;
    public static final int SCROLL_RIGHT = 25;
    public static final int SCROLL_TO_POSITION = 26;
    public static final int SCROLL_UP = 27;
    public static final int SELECT = 2;
    public static final int SET_PROGRESS = 28;
    public static final int SET_SELECTION = 17;
    public static final int SET_TEXT = 21;
    public static final int SHOW_ON_SCREEN = 29;
    public static final int SHOW_TOOLTIP = 32;

    private AccessibilityActionType() {
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
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

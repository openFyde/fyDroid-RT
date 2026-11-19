package org.chromium.arc.mojom;

public final class CompanionLibApiId {
    public static final int ACTIVATE_TASK = 7;
    public static final int GET_CAPTION_HEIGHT = 8;
    public static final int GET_DEVICE_MODE = 1;
    public static final int GET_DISPLAY_TOPOLOGY = 2;
    public static final int GET_HIDDEN_CAPTION_BUTTON = 9;
    public static final int GET_PRIMARY_DISPLAY_ID = 3;
    public static final int GET_TASK_WINDOW_BOUNDS = 11;
    public static final int GET_TASK_WINDOW_STATE = 12;
    public static final int GET_WINDOW_STATE = 20;
    public static final int GET_WORKSPACE_INSETS = 4;
    public static final int HIDE_CAPTION_BUTTON = 10;
    public static final int HIDE_SHADOW = 14;
    public static final int IS_CLIPPING_TO_TASK_DISABLED = 23;
    public static final int IS_SHADOW_HIDDEN = 13;
    public static final int ON_CLOSE_REQUEST_RESPONSE = 15;
    public static final int REGISTER_CALLBACK = 5;
    public static final int REMOVE_ON_CLOSE_REQUEST_HANDLER = 17;
    public static final int SET_CLIPPING_TO_TASK_DISABLE = 22;
    public static final int SET_ON_CLOSE_REQUEST_HANDLER = 16;
    public static final int SET_TASK_WINDOW_BOUNDS = 18;
    public static final int SET_TASK_WINDOW_STATE = 19;
    public static final int UNREGISTER_CALLBACK = 6;
    public static final int UNSUPPORTED = 0;
    public static final int WINDOW_INTERACTION_CONTROLLER = 21;

    private CompanionLibApiId() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

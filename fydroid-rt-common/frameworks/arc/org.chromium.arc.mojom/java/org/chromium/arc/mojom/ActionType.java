package org.chromium.arc.mojom;

public final class ActionType {
    public static final int CREATE_NOTE = 3;
    public static final int EDIT = 4;
    public static final int SEND = 1;
    public static final int SEND_MULTIPLE = 2;
    public static final int VIEW = 0;

    private ActionType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

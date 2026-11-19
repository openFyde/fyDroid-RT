package org.chromium.arc.mojom;

public final class AppCategory {
    public static final int ACCESSIBILITY = 8;
    public static final int AUDIO = 1;
    public static final int GAME = 0;
    public static final int IMAGE = 3;
    public static final int MAPS = 6;
    public static final int NEWS = 5;
    public static final int PRODUCTIVITY = 7;
    public static final int SOCIAL = 4;
    public static final int UNDEFINED = -1;
    public static final int VIDEO = 2;

    private AppCategory() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case -1:
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

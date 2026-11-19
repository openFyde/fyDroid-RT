package org.chromium.arc.mojom;

public final class TextInputType {
    public static final int ANDROID_NULL = 11;
    public static final int DATE = 8;
    public static final int DATETIME = 10;
    public static final int EMAIL = 4;
    public static final int NONE = 0;
    public static final int NUMBER = 5;
    public static final int PASSWORD = 2;
    public static final int SEARCH = 3;
    public static final int TELEPHONE = 6;
    public static final int TEXT = 1;
    public static final int TIME = 9;
    public static final int URL = 7;

    private TextInputType() {
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

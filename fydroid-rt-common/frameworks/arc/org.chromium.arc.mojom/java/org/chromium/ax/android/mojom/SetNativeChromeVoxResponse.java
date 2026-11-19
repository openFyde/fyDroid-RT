package org.chromium.ax.android.mojom;

public final class SetNativeChromeVoxResponse {
    public static final int FAILURE = 3;
    public static final int INVALID_ENUM_VALUE = 5;
    public static final int NEED_DEPRECATION_CONFIRMATION = 4;
    public static final int SUCCESS = 0;
    public static final int TALKBACK_NOT_INSTALLED = 2;
    public static final int WINDOW_NOT_FOUND = 1;

    private SetNativeChromeVoxResponse() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
    }
}

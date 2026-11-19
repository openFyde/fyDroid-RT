package org.chromium.arc.mojom;

public final class ChromeApp {
    public static final int CAMERA = 0;
    public static final int LAST = 0;

    private ChromeApp() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0;
    }

    public static void validate(int i) {
    }
}

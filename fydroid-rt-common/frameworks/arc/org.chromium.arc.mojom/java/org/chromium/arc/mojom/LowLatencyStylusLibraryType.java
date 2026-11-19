package org.chromium.arc.mojom;

public final class LowLatencyStylusLibraryType {
    public static final int CPU = 1;
    public static final int GPU = 2;
    public static final int UNSUPPORTED = 0;

    private LowLatencyStylusLibraryType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class AndroidIdSource {
    public static final int CACHED = 1;
    public static final int GMS_PROXY = 3;
    public static final int GSERVICE_FLAG = 2;
    public static final int MISSING = 0;

    private AndroidIdSource() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

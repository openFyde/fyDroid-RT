package org.chromium.arc.mojom;

public final class NativeBridgeType {
    public static final int HOUDINI = 1;
    public static final int NDK_TRANSLATION = 2;
    public static final int NONE = 0;

    private NativeBridgeType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

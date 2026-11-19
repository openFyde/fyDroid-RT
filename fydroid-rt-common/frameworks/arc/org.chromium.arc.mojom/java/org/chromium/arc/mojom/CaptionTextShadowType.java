package org.chromium.arc.mojom;

public final class CaptionTextShadowType {
    public static final int DEPRESSED = 4;
    public static final int DROP_SHADOW = 2;
    public static final int INVALID_ENUM_VALUE = -1;
    public static final int NONE = 0;
    public static final int RAISED = 3;
    public static final int UNIFORM = 1;

    private CaptionTextShadowType() {
    }

    public static boolean isKnownValue(int i) {
        return i == -1 || i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class DecoderStatus {
    public static final int ABORTED = 1;
    public static final int CREATION_FAILED = 4;
    public static final int FAILED = 2;
    public static final int INVALID_ARGUMENT = 3;
    public static final int OK = 0;

    private DecoderStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

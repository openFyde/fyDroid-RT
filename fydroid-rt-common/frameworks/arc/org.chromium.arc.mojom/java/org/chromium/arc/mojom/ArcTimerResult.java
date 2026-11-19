package org.chromium.arc.mojom;

public final class ArcTimerResult {
    public static final int FAILURE = 1;
    public static final int SUCCESS = 0;

    private ArcTimerResult() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

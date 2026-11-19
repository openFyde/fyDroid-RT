package org.chromium.arc.mojom;

public final class ArcPipEvent {
    public static final int ENTER = 0;

    private ArcPipEvent() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0;
    }

    public static void validate(int i) {
    }
}

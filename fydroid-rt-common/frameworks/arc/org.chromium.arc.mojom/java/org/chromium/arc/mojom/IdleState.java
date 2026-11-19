package org.chromium.arc.mojom;

public final class IdleState {
    public static final int ACTIVE = 0;
    public static final int FORCE_INACTIVE = 2;
    public static final int INACTIVE = 1;

    private IdleState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class ChangeType {
    public static final int CHANGED = 0;
    public static final int DELETED = 1;

    private ChangeType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

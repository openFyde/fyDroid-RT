package org.chromium.arc.mojom;

public final class DataRestoreStatus {
    public static final int FAILED = 2;
    public static final int NOT_NEEDED = 0;
    public static final int SUCCESS = 1;

    private DataRestoreStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

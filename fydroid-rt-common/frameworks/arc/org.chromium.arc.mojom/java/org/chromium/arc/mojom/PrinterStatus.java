package org.chromium.arc.mojom;

public final class PrinterStatus {
    public static final int BUSY = 2;
    public static final int IDLE = 1;
    public static final int UNAVAILABLE = 3;

    private PrinterStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

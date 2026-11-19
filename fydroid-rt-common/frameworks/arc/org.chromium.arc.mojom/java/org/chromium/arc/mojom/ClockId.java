package org.chromium.arc.mojom;

public final class ClockId {
    public static final int BOOTTIME_ALARM = 1;
    public static final int REALTIME_ALARM = 0;

    private ClockId() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

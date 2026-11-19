package org.chromium.arc.mojom;

public final class CpuRestrictionState {
    public static final int CPU_RESTRICTION_BACKGROUND = 1;
    public static final int CPU_RESTRICTION_FOREGROUND = 0;

    private CpuRestrictionState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class WifiBand {
    public static final int K2_GHZ = 1;
    public static final int K5_GHZ = 2;
    public static final int K60_GHZ = 4;
    public static final int K6_GHZ = 3;
    public static final int UNKNOWN = 0;

    private WifiBand() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

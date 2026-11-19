package org.chromium.arc.mojom;

public final class Flag {
    public static final int DEPRECATED_ENABLE_ARC_HOST_VPN = 1;
    public static final int UNKNOWN = 0;

    private Flag() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

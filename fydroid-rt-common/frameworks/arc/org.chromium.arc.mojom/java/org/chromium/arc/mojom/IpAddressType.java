package org.chromium.arc.mojom;

public final class IpAddressType {
    public static final int IPV4 = 0;
    public static final int IPV6 = 1;

    private IpAddressType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

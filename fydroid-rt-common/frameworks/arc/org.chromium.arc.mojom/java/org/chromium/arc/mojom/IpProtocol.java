package org.chromium.arc.mojom;

public final class IpProtocol {
    public static final int TCP = 1;
    public static final int UDP = 2;
    public static final int UNKNOWN = 0;

    private IpProtocol() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

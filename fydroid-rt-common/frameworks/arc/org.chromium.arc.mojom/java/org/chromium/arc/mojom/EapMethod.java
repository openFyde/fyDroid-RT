package org.chromium.arc.mojom;

public final class EapMethod {
    public static final int LEAP = 1;
    public static final int NONE = 0;
    public static final int PEAP = 2;
    public static final int TLS = 3;
    public static final int TTLS = 4;

    private EapMethod() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

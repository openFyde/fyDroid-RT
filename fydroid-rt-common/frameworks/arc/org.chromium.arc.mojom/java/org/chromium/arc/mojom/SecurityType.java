package org.chromium.arc.mojom;

public final class SecurityType {
    public static final int NONE = 0;
    public static final int WEP_8021X = 2;
    public static final int WEP_PSK = 1;
    public static final int WPA_EAP = 4;
    public static final int WPA_PSK = 3;

    private SecurityType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class KeyManagement {
    public static final int FT_EAP = 2;
    public static final int FT_PSK = 3;
    public static final int FT_SAE = 4;
    public static final int IEEE8021_X = 1;
    public static final int NONE = 0;
    public static final int SAE = 8;
    public static final int WPA_EAP = 5;
    public static final int WPA_EAP_SHA256 = 6;
    public static final int WPA_PSK = 7;

    private KeyManagement() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

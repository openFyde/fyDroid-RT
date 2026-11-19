package org.chromium.arc.mojom;

public final class OemCryptoAlgorithm {
    public static final int AES_CBC_128_NO_PADDING = 0;
    public static final int HMAC_SHA265 = 1;

    private OemCryptoAlgorithm() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

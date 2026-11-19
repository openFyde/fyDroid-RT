package org.chromium.arc.mojom;

public final class OemCryptoRsaPaddingScheme {
    public static final int SIGN_PKCS1_BLOCK1 = 2;
    public static final int SIGN_RSASSA_PSS = 1;

    private OemCryptoRsaPaddingScheme() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

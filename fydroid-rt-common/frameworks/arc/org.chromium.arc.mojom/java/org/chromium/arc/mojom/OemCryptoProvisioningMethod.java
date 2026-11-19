package org.chromium.arc.mojom;

public final class OemCryptoProvisioningMethod {
    public static final int DRM_CERTIFICATE = 1;
    public static final int KEYBOX = 2;
    public static final int OEM_CERTIFICATE = 3;
    public static final int PROVISIONING_ERROR = 0;

    private OemCryptoProvisioningMethod() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

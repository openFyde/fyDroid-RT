package org.chromium.arc.mojom;

public final class OemCryptoHdcpCapability {
    public static final int HDCP_NONE = 0;
    public static final int HDCP_NO_DIGITAL_OUTPUT = 255;
    public static final int HDCP_V1 = 1;
    public static final int HDCP_V2 = 2;
    public static final int HDCP_V2_1 = 3;
    public static final int HDCP_V2_2 = 4;
    public static final int HDCP_V2_3 = 5;

    private OemCryptoHdcpCapability() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 255;
    }

    public static void validate(int i) {
    }
}

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidScalerAvailableFormats {
    public static final int ANDROID_SCALER_AVAILABLE_FORMATS_BLOB = 33;
    public static final int ANDROID_SCALER_AVAILABLE_FORMATS_IMPLEMENTATION_DEFINED = 34;
    public static final int ANDROID_SCALER_AVAILABLE_FORMATS_RAW16 = 32;
    public static final int ANDROID_SCALER_AVAILABLE_FORMATS_RAW_OPAQUE = 36;
    public static final int ANDROID_SCALER_AVAILABLE_FORMATS_YV12 = 842094169;
    public static final int ANDROID_SCALER_AVAILABLE_FORMATS_Y_CB_CR_420_888 = 35;
    public static final int ANDROID_SCALER_AVAILABLE_FORMATS_Y_CR_CB_420_SP = 17;

    private AndroidScalerAvailableFormats() {
    }

    public static boolean isKnownValue(int i) {
        if (i == 17 || i == 842094169) {
            return true;
        }
        switch (i) {
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

package org.chromium.cros.mojom;

public final class HalPixelFormat {
    public static final int HAL_PIXEL_FORMAT_BGRA_8888 = 5;
    public static final int HAL_PIXEL_FORMAT_BLOB = 33;
    public static final int HAL_PIXEL_FORMAT_IMPLEMENTATION_DEFINED = 34;
    public static final int HAL_PIXEL_FORMAT_RGBA_8888 = 1;
    public static final int HAL_PIXEL_FORMAT_RGBX_8888 = 2;
    public static final int HAL_PIXEL_FORMAT_YV12 = 842094169;
    public static final int HAL_PIXEL_FORMAT_Y_CB_CR_420_888 = 35;
    public static final int HAL_PIXEL_FORMAT_Y_CB_CR_422_I = 20;
    public static final int HAL_PIXEL_FORMAT_Y_CR_CB_420_SP = 17;

    private HalPixelFormat() {
    }

    public static boolean isKnownValue(int i) {
        if (i != 1 && i != 2 && i != 5 && i != 17 && i != 20 && i != 842094169) {
            switch (i) {
                case 33:
                case 34:
                case 35:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static void validate(int i) {
    }
}

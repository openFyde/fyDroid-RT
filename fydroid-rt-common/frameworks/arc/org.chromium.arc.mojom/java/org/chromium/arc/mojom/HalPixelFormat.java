package org.chromium.arc.mojom;

public final class HalPixelFormat {
    public static final int HAL_PIXEL_FORMAT_BGRA_8888 = 5;
    public static final int HAL_PIXEL_FORMAT_NV12 = 842094158;
    public static final int HAL_PIXEL_FORMAT_YV12 = 842094169;
    public static final int HAL_PIXEL_FORMAT_Y_CB_CR_420_888 = 35;

    private HalPixelFormat() {
    }

    public static boolean isKnownValue(int i) {
        return i == 5 || i == 35 || i == 842094158 || i == 842094169;
    }

    public static void validate(int i) {
    }
}

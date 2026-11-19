package org.chromium.gfx.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class BufferFormat {
    public static final int BGRA_1010102 = 9;
    public static final int BGRA_8888 = 11;
    public static final int BGRX_8888 = 8;
    public static final int BGR_565 = 4;
    public static final int P010 = 15;
    public static final int RGBA_1010102 = 10;
    public static final int RGBA_4444 = 5;
    public static final int RGBA_8888 = 7;
    public static final int RGBA_F16 = 12;
    public static final int RGBX_8888 = 6;
    public static final int RG_1616 = 3;
    public static final int RG_88 = 2;
    public static final int R_16 = 1;
    public static final int R_8 = 0;
    public static final int YUV_420_BIPLANAR = 14;
    public static final int YVU_420 = 13;

    private BufferFormat() {
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
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
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

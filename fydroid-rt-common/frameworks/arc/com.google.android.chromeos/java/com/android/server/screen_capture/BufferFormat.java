/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Open source replacement for com.android.server.screen_capture.BufferFormat
 * from the proprietary com.google.android.chromeos jar. Integer constants
 * describing the pixel format of a graphics buffer shared with
 * IArcScreenCaptureService#setOutputBuffer. Values are bit-for-bit identical
 * to the proprietary class so any in-process consumer keeps working unchanged.
 */
package com.android.server.screen_capture;

public @interface BufferFormat {
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
}

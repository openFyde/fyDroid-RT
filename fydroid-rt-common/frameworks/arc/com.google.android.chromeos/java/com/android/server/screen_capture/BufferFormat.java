/*
 * Copyright (C) 2024 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

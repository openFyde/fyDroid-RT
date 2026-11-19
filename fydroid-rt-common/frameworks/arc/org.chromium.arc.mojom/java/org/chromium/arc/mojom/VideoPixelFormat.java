// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class VideoPixelFormat {
    public static final int PIXEL_FORMAT_ABGR = 27;
    public static final int PIXEL_FORMAT_ARGB = 10;
    public static final int PIXEL_FORMAT_I420 = 1;
    public static final int PIXEL_FORMAT_NV12 = 6;
    public static final int PIXEL_FORMAT_NV21 = 7;
    public static final int PIXEL_FORMAT_UNKNOWN = 0;
    public static final int PIXEL_FORMAT_XBGR = 28;
    public static final int PIXEL_FORMAT_YV12 = 2;

    private VideoPixelFormat() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 6 || i == 7 || i == 10 || i == 27 || i == 28;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

public final class Camera3Constants {
    public static final int GRALLOC_USAGE_FORCE_I420 = 268435456;
    public static final int GRALLOC_USAGE_HW_CAMERA_READ = 262144;
    public static final int GRALLOC_USAGE_HW_CAMERA_WRITE = 131072;
    public static final int GRALLOC_USAGE_HW_COMPOSER = 2048;
    public static final int GRALLOC_USAGE_HW_VIDEO_ENCODER = 65536;
    public static final int GRALLOC_USAGE_STILL_CAPTURE = 536870912;
    public static final int GRALLOC_USAGE_SW_READ_NEVER = 0;
    public static final int GRALLOC_USAGE_SW_READ_OFTEN = 3;
    public static final int GRALLOC_USAGE_SW_WRITE_NEVER = 0;
    public static final int GRALLOC_USAGE_SW_WRITE_OFTEN = 48;
    public static final long NO_BUFFER_BUFFER_ID = -1;

    private Camera3Constants() {
    }
}

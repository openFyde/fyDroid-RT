// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAwbMode {
    public static final int ANDROID_CONTROL_AWB_MODE_AUTO = 1;
    public static final int ANDROID_CONTROL_AWB_MODE_CLOUDY_DAYLIGHT = 6;
    public static final int ANDROID_CONTROL_AWB_MODE_DAYLIGHT = 5;
    public static final int ANDROID_CONTROL_AWB_MODE_FLUORESCENT = 3;
    public static final int ANDROID_CONTROL_AWB_MODE_INCANDESCENT = 2;
    public static final int ANDROID_CONTROL_AWB_MODE_OFF = 0;
    public static final int ANDROID_CONTROL_AWB_MODE_SHADE = 8;
    public static final int ANDROID_CONTROL_AWB_MODE_TWILIGHT = 7;
    public static final int ANDROID_CONTROL_AWB_MODE_WARM_FLUORESCENT = 4;

    private AndroidControlAwbMode() {
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

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidSensorTestPatternMode {
    public static final int ANDROID_SENSOR_TEST_PATTERN_MODE_COLOR_BARS = 2;
    public static final int ANDROID_SENSOR_TEST_PATTERN_MODE_COLOR_BARS_FADE_TO_GRAY = 3;
    public static final int ANDROID_SENSOR_TEST_PATTERN_MODE_CUSTOM1 = 256;
    public static final int ANDROID_SENSOR_TEST_PATTERN_MODE_OFF = 0;
    public static final int ANDROID_SENSOR_TEST_PATTERN_MODE_PN9 = 4;
    public static final int ANDROID_SENSOR_TEST_PATTERN_MODE_SOLID_COLOR = 1;

    private AndroidSensorTestPatternMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 256;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

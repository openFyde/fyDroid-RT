// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidNoiseReductionMode {
    public static final int ANDROID_NOISE_REDUCTION_MODE_FAST = 1;
    public static final int ANDROID_NOISE_REDUCTION_MODE_HIGH_QUALITY = 2;
    public static final int ANDROID_NOISE_REDUCTION_MODE_MINIMAL = 3;
    public static final int ANDROID_NOISE_REDUCTION_MODE_OFF = 0;
    public static final int ANDROID_NOISE_REDUCTION_MODE_ZERO_SHUTTER_LAG = 4;

    private AndroidNoiseReductionMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidColorCorrectionMode {
    public static final int ANDROID_COLOR_CORRECTION_MODE_FAST = 1;
    public static final int ANDROID_COLOR_CORRECTION_MODE_HIGH_QUALITY = 2;
    public static final int ANDROID_COLOR_CORRECTION_MODE_TRANSFORM_MATRIX = 0;

    private AndroidColorCorrectionMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

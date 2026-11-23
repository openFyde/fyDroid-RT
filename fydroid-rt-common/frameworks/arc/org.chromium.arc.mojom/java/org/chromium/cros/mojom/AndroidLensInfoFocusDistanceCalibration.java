// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidLensInfoFocusDistanceCalibration {
    public static final int ANDROID_LENS_INFO_FOCUS_DISTANCE_CALIBRATION_APPROXIMATE = 1;
    public static final int ANDROID_LENS_INFO_FOCUS_DISTANCE_CALIBRATION_CALIBRATED = 2;
    public static final int ANDROID_LENS_INFO_FOCUS_DISTANCE_CALIBRATION_UNCALIBRATED = 0;

    private AndroidLensInfoFocusDistanceCalibration() {
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

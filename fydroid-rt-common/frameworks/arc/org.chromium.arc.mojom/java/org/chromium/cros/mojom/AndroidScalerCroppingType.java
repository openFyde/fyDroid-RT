// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidScalerCroppingType {
    public static final int ANDROID_SCALER_CROPPING_TYPE_CENTER_ONLY = 0;
    public static final int ANDROID_SCALER_CROPPING_TYPE_FREEFORM = 1;

    private AndroidScalerCroppingType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

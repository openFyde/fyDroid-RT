// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAeAntibandingMode {
    public static final int ANDROID_CONTROL_AE_ANTIBANDING_MODE_50HZ = 1;
    public static final int ANDROID_CONTROL_AE_ANTIBANDING_MODE_60HZ = 2;
    public static final int ANDROID_CONTROL_AE_ANTIBANDING_MODE_AUTO = 3;
    public static final int ANDROID_CONTROL_AE_ANTIBANDING_MODE_OFF = 0;

    private AndroidControlAeAntibandingMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

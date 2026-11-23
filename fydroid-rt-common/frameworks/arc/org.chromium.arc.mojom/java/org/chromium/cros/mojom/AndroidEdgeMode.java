// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidEdgeMode {
    public static final int ANDROID_EDGE_MODE_FAST = 1;
    public static final int ANDROID_EDGE_MODE_HIGH_QUALITY = 2;
    public static final int ANDROID_EDGE_MODE_OFF = 0;
    public static final int ANDROID_EDGE_MODE_ZERO_SHUTTER_LAG = 3;

    private AndroidEdgeMode() {
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

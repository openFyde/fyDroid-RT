// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3StreamConfigurationMode {
    public static final int CAMERA3_STREAM_CONFIGURATION_CONSTRAINED_HIGH_SPEED_MODE = 1;
    public static final int CAMERA3_STREAM_CONFIGURATION_NORMAL_MODE = 0;

    private Camera3StreamConfigurationMode() {
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

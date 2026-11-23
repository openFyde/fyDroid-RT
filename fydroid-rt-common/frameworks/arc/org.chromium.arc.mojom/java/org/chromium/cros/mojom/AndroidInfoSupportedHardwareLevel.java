// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidInfoSupportedHardwareLevel {
    public static final int ANDROID_INFO_SUPPORTED_HARDWARE_LEVEL_3 = 3;
    public static final int ANDROID_INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL = 4;
    public static final int ANDROID_INFO_SUPPORTED_HARDWARE_LEVEL_FULL = 1;
    public static final int ANDROID_INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY = 2;
    public static final int ANDROID_INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED = 0;

    private AndroidInfoSupportedHardwareLevel() {
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

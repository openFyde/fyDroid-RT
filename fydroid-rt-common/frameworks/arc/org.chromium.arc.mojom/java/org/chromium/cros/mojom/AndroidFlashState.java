// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidFlashState {
    public static final int ANDROID_FLASH_STATE_CHARGING = 1;
    public static final int ANDROID_FLASH_STATE_FIRED = 3;
    public static final int ANDROID_FLASH_STATE_PARTIAL = 4;
    public static final int ANDROID_FLASH_STATE_READY = 2;
    public static final int ANDROID_FLASH_STATE_UNAVAILABLE = 0;

    private AndroidFlashState() {
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

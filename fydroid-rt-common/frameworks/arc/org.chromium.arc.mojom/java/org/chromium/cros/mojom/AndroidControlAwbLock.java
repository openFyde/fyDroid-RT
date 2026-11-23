// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAwbLock {
    public static final int ANDROID_CONTROL_AWB_LOCK_OFF = 0;
    public static final int ANDROID_CONTROL_AWB_LOCK_ON = 1;

    private AndroidControlAwbLock() {
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

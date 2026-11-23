// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidSyncFrameNumber {
    public static final int ANDROID_SYNC_FRAME_NUMBER_CONVERGING = -1;
    public static final int ANDROID_SYNC_FRAME_NUMBER_UNKNOWN = -2;

    private AndroidSyncFrameNumber() {
    }

    public static boolean isKnownValue(int i) {
        return i == -2 || i == -1;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

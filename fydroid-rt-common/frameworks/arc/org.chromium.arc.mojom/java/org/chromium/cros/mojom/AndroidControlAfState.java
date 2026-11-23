// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidControlAfState {
    public static final int ANDROID_CONTROL_AF_STATE_ACTIVE_SCAN = 3;
    public static final int ANDROID_CONTROL_AF_STATE_FOCUSED_LOCKED = 4;
    public static final int ANDROID_CONTROL_AF_STATE_INACTIVE = 0;
    public static final int ANDROID_CONTROL_AF_STATE_NOT_FOCUSED_LOCKED = 5;
    public static final int ANDROID_CONTROL_AF_STATE_PASSIVE_FOCUSED = 2;
    public static final int ANDROID_CONTROL_AF_STATE_PASSIVE_SCAN = 1;
    public static final int ANDROID_CONTROL_AF_STATE_PASSIVE_UNFOCUSED = 6;

    private AndroidControlAfState() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

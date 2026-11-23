// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3MsgType {
    public static final int CAMERA3_MSG_ERROR = 1;
    public static final int CAMERA3_MSG_SHUTTER = 2;

    private Camera3MsgType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

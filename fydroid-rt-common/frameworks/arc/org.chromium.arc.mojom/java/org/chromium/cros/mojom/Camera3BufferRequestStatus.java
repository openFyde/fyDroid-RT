// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3BufferRequestStatus {
    public static final int CAMERA3_BUF_REQ_FAILED_CONFIGURING = 2;
    public static final int CAMERA3_BUF_REQ_FAILED_ILLEGAL_ARGUMENTS = 3;
    public static final int CAMERA3_BUF_REQ_FAILED_PARTIAL = 1;
    public static final int CAMERA3_BUF_REQ_FAILED_UNKNOWN = 4;
    public static final int CAMERA3_BUF_REQ_OK = 0;

    private Camera3BufferRequestStatus() {
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

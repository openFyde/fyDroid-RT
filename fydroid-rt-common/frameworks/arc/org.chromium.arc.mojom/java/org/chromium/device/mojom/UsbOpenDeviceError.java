// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbOpenDeviceError {
    public static final int ACCESS_DENIED = 1;
    public static final int ALREADY_OPEN = 2;
    public static final int OK = 0;

    private UsbOpenDeviceError() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

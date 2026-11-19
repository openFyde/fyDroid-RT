// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbTransferDirection {
    public static final int INBOUND = 0;
    public static final int OUTBOUND = 1;

    private UsbTransferDirection() {
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

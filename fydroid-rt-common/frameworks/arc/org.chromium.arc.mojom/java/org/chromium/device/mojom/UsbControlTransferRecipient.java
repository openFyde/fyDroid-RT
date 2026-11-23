// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.device.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class UsbControlTransferRecipient {
    public static final int DEVICE = 0;
    public static final int ENDPOINT = 2;
    public static final int INTERFACE = 1;
    public static final int OTHER = 3;

    private UsbControlTransferRecipient() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidLedAvailableLeds {
    public static final int ANDROID_LED_AVAILABLE_LEDS_TRANSMIT = 0;

    private AndroidLedAvailableLeds() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

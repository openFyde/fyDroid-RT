// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class BluetoothDeviceType {
    public static final int BLE = 2;
    public static final int BREDR = 1;
    public static final int DUAL = 3;

    private BluetoothDeviceType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class BluetoothScanMode {
    public static final int CONNECTABLE = 1;
    public static final int CONNECTABLE_DISCOVERABLE = 2;
    public static final int NONE = 0;

    private BluetoothScanMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

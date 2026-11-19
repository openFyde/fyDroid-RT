// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class BluetoothGattDbAttributeType {
    public static final int BTGATT_DB_CHARACTERISTIC = 3;
    public static final int BTGATT_DB_DESCRIPTOR = 4;
    public static final int BTGATT_DB_INCLUDED_SERVICE = 2;
    public static final int BTGATT_DB_PRIMARY_SERVICE = 0;
    public static final int BTGATT_DB_SECONDARY_SERVICE = 1;

    private BluetoothGattDbAttributeType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

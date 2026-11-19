// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class BluetoothAdvertisingDataType {
    public static final int DATA_TYPE_FLAGS = 1;
    public static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 9;
    public static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 255;
    public static final int DATA_TYPE_SERVICE_DATA = 22;
    public static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 7;
    public static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 3;
    public static final int DATA_TYPE_TX_POWER_LEVEL = 10;

    private BluetoothAdvertisingDataType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 3 || i == 7 || i == 22 || i == 255 || i == 9 || i == 10;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class BluetoothSdpAttributeType {
    public static final int BOOL = 5;
    public static final int INT = 2;
    public static final int NULLTYPE = 0;
    public static final int SEQUENCE = 6;
    public static final int STRING = 4;
    public static final int UINT = 1;
    public static final int URL = 7;
    public static final int UUID = 3;

    private BluetoothSdpAttributeType() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

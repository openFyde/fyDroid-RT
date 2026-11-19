// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class BluetoothStatus {
    public static final int AUTH_FAILURE = 9;
    public static final int AUTH_REJECTED = 11;
    public static final int BUSY = 4;
    public static final int DONE = 5;
    public static final int FAIL = 1;
    public static final int NOMEM = 3;
    public static final int NOT_READY = 2;
    public static final int PARM_INVALID = 7;
    public static final int RMT_DEV_DOWN = 10;
    public static final int SUCCESS = 0;
    public static final int UNHANDLED = 8;
    public static final int UNSUPPORTED = 6;

    private BluetoothStatus() {
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
            case 8:
            case 9:
            case 10:
            case 11:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

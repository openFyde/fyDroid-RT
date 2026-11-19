// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ManagementChangeStatus {
    public static final int CLOUD_DPC_ALREADY_DISABLED = 1;
    public static final int CLOUD_DPC_ALREADY_ENABLED = 3;
    public static final int CLOUD_DPC_DISABLED = 0;
    public static final int CLOUD_DPC_DISABLING_FAILED = 5;
    public static final int CLOUD_DPC_ENABLED = 2;
    public static final int CLOUD_DPC_ENABLING_FAILED = 6;
    public static final int INVALID_MANAGEMENT_STATE = 4;

    private ManagementChangeStatus() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

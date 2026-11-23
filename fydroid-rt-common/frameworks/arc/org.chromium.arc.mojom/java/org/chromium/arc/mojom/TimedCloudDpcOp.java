// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class TimedCloudDpcOp {
    public static final int DEVICE_SETUP = 1;
    public static final int SETUP_ADD_ACCOUNT = 9;
    public static final int SETUP_CHECK_FOR_ANDROID_ID = 3;
    public static final int SETUP_CHECK_FOR_FIRST_ACCOUNT_READY = 4;
    public static final int SETUP_CHECK_REGISTRATION_TOKEN = 13;
    public static final int SETUP_INSTALL_APPS = 10;
    public static final int SETUP_INSTALL_APPS_RETRY = 11;
    public static final int SETUP_PULL_AND_APPLY_POLICIES = 6;
    public static final int SETUP_QUARANTINED = 8;
    public static final int SETUP_REGISTER = 5;
    public static final int SETUP_REPORT_POLICY_COMPLIANCE = 7;
    public static final int SETUP_THIRD_PARTY_SIGNIN = 14;
    public static final int SETUP_TOTAL = 2;
    public static final int SETUP_UPDATE_PLAY_SERVICES = 12;
    public static final int UNKNOWN_OP = 0;

    private TimedCloudDpcOp() {
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
            case 12:
            case 13:
            case 14:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

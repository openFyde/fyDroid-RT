// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class CloudProvisionFlowError {
    public static final int ERROR_ACCOUNT_NOT_ALLOWLISTED = 16;
    public static final int ERROR_ACCOUNT_NOT_READY = 14;
    public static final int ERROR_ACCOUNT_OTHER = 23;
    public static final int ERROR_ADD_ACCOUNT_FAILED = 4;
    public static final int ERROR_CHECKIN_FAILED = 15;
    public static final int ERROR_DEVICE_QUOTA_EXCEEDED = 2;
    public static final int ERROR_DPC_SUPPORT = 13;
    public static final int ERROR_ENROLLMENT_TOKEN_INVALID = 1;
    public static final int ERROR_ENTERPRISE_INVALID = 9;
    public static final int ERROR_INVALID_POLICY_STATE = 12;
    public static final int ERROR_INVALID_SETUP_ACTION = 19;
    public static final int ERROR_JSON = 17;
    public static final int ERROR_MANAGED_PROVISIONING_FAILED = 18;
    public static final int ERROR_NETWORK_UNAVAILABLE = 8;
    public static final int ERROR_NO_ACCOUNT_IN_WORK_PROFILE = 11;
    public static final int ERROR_OAUTH_TOKEN = 22;
    public static final int ERROR_OAUTH_TOKEN_AUTHENTICATOR_EXCEPTION = 24;
    public static final int ERROR_OAUTH_TOKEN_IO_EXCEPTION = 25;
    public static final int ERROR_OAUTH_TOKEN_OPERATION_CANCELED_EXCEPTION = 26;
    public static final int ERROR_OTHER = 0;
    public static final int ERROR_QUARANTINE = 6;
    public static final int ERROR_REMOVE_ACCOUNT_FAILED = 21;
    public static final int ERROR_REQUEST_ANDROID_ID_FAILED = 5;
    public static final int ERROR_SERVER = 20;
    public static final int ERROR_SERVER_TRANSIENT_ERROR = 3;
    public static final int ERROR_TIMEOUT = 7;
    public static final int ERROR_USER_CANCEL = 10;

    private CloudProvisionFlowError() {
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
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

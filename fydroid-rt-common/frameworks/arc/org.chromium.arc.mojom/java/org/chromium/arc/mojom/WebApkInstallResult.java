// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class WebApkInstallResult {
    public static final int ERROR_API_DISABLED = 6;
    public static final int ERROR_CALLER_VERIFICATION_FAILED = 4;
    public static final int ERROR_DOWNLOAD_CANCELLED = 11;
    public static final int ERROR_DOWNLOAD_ERROR = 12;
    public static final int ERROR_INSTALL_ERROR = 13;
    public static final int ERROR_NOT_GOOGLE_SIGNED = 10;
    public static final int ERROR_POLICY_VIOLATION = 5;
    public static final int ERROR_RESOLVE_ERROR = 9;
    public static final int ERROR_RESOLVE_NETWORK_ERROR = 8;
    public static final int ERROR_SERVICE_COMMUNICATION = 2;
    public static final int ERROR_SERVICE_TIMEOUT = 3;
    public static final int ERROR_UNKNOWN = 1;
    public static final int ERROR_UNKNOWN_ACCOUNT = 7;
    public static final int SUCCESS = 0;

    private WebApkInstallResult() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

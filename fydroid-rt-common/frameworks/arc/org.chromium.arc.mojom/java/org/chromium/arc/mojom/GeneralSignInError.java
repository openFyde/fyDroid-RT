// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class GeneralSignInError {
    public static final int ARC_DISABLED = 5;
    public static final int CHROME_ACCOUNT_NOT_FOUND = 7;
    public static final int CHROME_SERVER_COMMUNICATION_ERROR = 4;
    public static final int GENERIC_PROVISIONING_TIMEOUT = 2;
    public static final int MOJO_VERSION_MISMATCH = 1;
    public static final int NO_NETWORK_CONNECTION = 3;
    public static final int UNKNOWN_ERROR = 0;
    public static final int UNSUPPORTED_ACCOUNT_TYPE = 6;

    private GeneralSignInError() {
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

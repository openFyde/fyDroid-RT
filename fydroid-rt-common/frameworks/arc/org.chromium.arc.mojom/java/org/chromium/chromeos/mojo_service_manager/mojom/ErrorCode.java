// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.mojo_service_manager.mojom;

public final class ErrorCode {
    public static final int PERMISSION_DENIED = 3;
    public static final int SERVICE_ALREADY_REGISTERED = 4;
    public static final int SERVICE_NOT_FOUND = 5;
    public static final int TIMEOUT = 2;
    public static final int UNEXPECTED_OS_ERROR = 6;
    public static final int UNKNOWN = 1;

    private ErrorCode() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
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

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ArcAuthCodeStatus {
    public static final int ARC_DISABLED = 17;
    public static final int CHROME_ACCOUNT_NOT_FOUND = 21;
    public static final int CHROME_SERVER_COMMUNICATION_ERROR = 16;
    public static final int SUCCESS = 18;

    private ArcAuthCodeStatus() {
    }

    public static boolean isKnownValue(int i) {
        if (i == 21) {
            return true;
        }
        switch (i) {
            case 16:
            case 17:
            case 18:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

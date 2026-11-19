// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.payments.mojom;

public final class BillingResponseCode {
    public static final int CLIENT_APP_ERROR = 6;
    public static final int CLIENT_APP_UNAVAILABLE = 5;
    public static final int ERROR = 1;
    public static final int ITEM_ALREADY_OWNED = 2;
    public static final int ITEM_NOT_OWNED = 3;
    public static final int ITEM_UNAVAILABLE = 4;
    public static final int OK = 0;

    private BillingResponseCode() {
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

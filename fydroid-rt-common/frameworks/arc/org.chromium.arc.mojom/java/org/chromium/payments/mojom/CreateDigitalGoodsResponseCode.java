// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.payments.mojom;

public final class CreateDigitalGoodsResponseCode {
    public static final int ERROR = 1;
    public static final int OK = 0;
    public static final int UNSUPPORTED_CONTEXT = 3;
    public static final int UNSUPPORTED_PAYMENT_METHOD = 2;

    private CreateDigitalGoodsResponseCode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

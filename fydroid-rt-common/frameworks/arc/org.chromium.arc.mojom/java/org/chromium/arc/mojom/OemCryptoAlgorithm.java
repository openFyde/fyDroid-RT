// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class OemCryptoAlgorithm {
    public static final int AES_CBC_128_NO_PADDING = 0;
    public static final int HMAC_SHA265 = 1;

    private OemCryptoAlgorithm() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

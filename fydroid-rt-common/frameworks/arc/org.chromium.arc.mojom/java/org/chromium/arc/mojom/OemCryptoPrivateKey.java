// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class OemCryptoPrivateKey {
    public static final int ECC_PRIVATE_KEY = 1;
    public static final int RSA_PRIVATE_KEY = 0;

    private OemCryptoPrivateKey() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class OemCryptoCipherMode {
    public static final int CIPHER_MODE_CBC = 1;
    public static final int CIPHER_MODE_CTR = 0;

    private OemCryptoCipherMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

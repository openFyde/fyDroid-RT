// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class KeyPurpose {
    public static final int DECRYPT = 1;
    public static final int DERIVE_KEY = 4;
    public static final int ENCRYPT = 0;
    public static final int SIGN = 2;
    public static final int VERIFY = 3;
    public static final int WRAP_KEY = 5;

    private KeyPurpose() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
    }
}

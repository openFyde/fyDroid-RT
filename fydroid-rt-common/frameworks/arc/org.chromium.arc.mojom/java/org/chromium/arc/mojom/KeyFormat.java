// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class KeyFormat {
    public static final int PKCS8 = 1;
    public static final int RAW = 3;
    public static final int X509 = 0;

    private KeyFormat() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 3;
    }

    public static void validate(int i) {
    }
}

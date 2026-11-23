// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class OemCryptoUsageEntryStatus {
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 2;
    public static final int INACTIVE_UNUSED = 4;
    public static final int INACTIVE_USED = 3;
    public static final int UNUSED = 0;

    private OemCryptoUsageEntryStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class BootType {
    public static final int FIRST_BOOT = 1;
    public static final int FIRST_BOOT_AFTER_UPDATE = 2;
    public static final int REGULAR_BOOT = 3;
    public static final int UNKNOWN = 0;

    private BootType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityRangeType {
    public static final int FLOAT = 1;
    public static final int INT = 0;
    public static final int INVALID_ENUM_VALUE = 3;
    public static final int PERCENT = 2;

    private AccessibilityRangeType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

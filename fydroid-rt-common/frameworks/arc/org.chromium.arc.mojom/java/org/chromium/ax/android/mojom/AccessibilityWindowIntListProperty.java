// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityWindowIntListProperty {
    public static final int CHILD_WINDOW_IDS = 0;
    public static final int INVALID_ENUM_VALUE = 1;

    private AccessibilityWindowIntListProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

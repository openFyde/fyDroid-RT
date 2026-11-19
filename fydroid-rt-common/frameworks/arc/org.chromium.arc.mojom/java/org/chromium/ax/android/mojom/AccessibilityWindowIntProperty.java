// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityWindowIntProperty {
    public static final int ANCHOR_NODE_ID = 0;
    public static final int INVALID_ENUM_VALUE = 3;
    public static final int LAYER_ORDER = 1;
    public static final int PARENT_WINDOW_ID = 2;

    private AccessibilityWindowIntProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

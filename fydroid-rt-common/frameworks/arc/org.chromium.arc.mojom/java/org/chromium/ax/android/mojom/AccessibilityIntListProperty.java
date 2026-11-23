// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityIntListProperty {
    public static final int CHILD_NODE_IDS = 0;
    public static final int CUSTOM_ACTION_IDS_DEPRECATED = 1;
    public static final int INVALID_ENUM_VALUE = 3;
    public static final int STANDARD_ACTION_IDS_DEPRECATED = 2;

    private AccessibilityIntListProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

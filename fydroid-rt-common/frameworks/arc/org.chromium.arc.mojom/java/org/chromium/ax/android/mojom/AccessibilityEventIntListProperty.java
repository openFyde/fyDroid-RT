// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityEventIntListProperty {
    public static final int CONTENT_CHANGE_TYPES = 0;
    public static final int INVALID_ENUM_VALUE = 1;

    private AccessibilityEventIntListProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

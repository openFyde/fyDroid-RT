// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityWindowBooleanProperty {
    public static final int ACCESSIBILITY_FOCUSED = 0;
    public static final int FOCUSED = 1;
    public static final int INVALID_ENUM_VALUE = 4;
    public static final int IN_PICTURE_IN_PICTURE_MODE = 2;
    public static final int WINDOW_ACTIVE = 3;

    private AccessibilityWindowBooleanProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

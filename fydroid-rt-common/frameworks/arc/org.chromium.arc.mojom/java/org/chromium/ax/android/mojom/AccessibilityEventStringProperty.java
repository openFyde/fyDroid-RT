// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityEventStringProperty {
    public static final int CLASS_NAME = 0;
    public static final int CONTENT_DESCRIPTION = 2;
    public static final int INVALID_ENUM_VALUE = 3;
    public static final int PACKAGE_NAME = 1;

    private AccessibilityEventStringProperty() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

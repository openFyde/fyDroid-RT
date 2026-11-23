// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityNotificationStateType {
    public static final int INVALID_ENUM_VALUE = 2;
    public static final int SURFACE_CREATED = 0;
    public static final int SURFACE_REMOVED = 1;

    private AccessibilityNotificationStateType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

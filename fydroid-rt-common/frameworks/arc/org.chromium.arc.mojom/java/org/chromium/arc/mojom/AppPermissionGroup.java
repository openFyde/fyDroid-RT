// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class AppPermissionGroup {
    public static final int CAMERA = 0;
    public static final int LOCATION = 2;
    public static final int MICROPHONE = 1;

    private AppPermissionGroup() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

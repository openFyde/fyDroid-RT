// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class AppPermission {
    public static final int CAMERA = 0;
    public static final int CONTACTS = 4;
    public static final int LOCATION = 1;
    public static final int MICROPHONE = 2;
    public static final int NOTIFICATIONS = 3;
    public static final int STORAGE = 5;

    private AppPermission() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ArcResizeLockState {
    public static final int FULLY_LOCKED = 4;
    public static final int OFF = 3;
    public static final int ON = 2;
    public static final int READY = 1;
    public static final int UNDEFINED = 0;

    private ArcResizeLockState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

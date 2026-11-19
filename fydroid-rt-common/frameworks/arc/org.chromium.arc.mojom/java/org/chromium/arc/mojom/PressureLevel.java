// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class PressureLevel {
    public static final int CACHED = 2;
    public static final int FOREGROUND = 0;
    public static final int PERCEPTIBLE = 1;

    private PressureLevel() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

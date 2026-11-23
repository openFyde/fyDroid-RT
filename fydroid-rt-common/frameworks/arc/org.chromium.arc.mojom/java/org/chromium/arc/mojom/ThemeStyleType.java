// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ThemeStyleType {
    public static final int EXPRESSIVE = 2;
    public static final int FRUIT_SALAD = 5;
    public static final int RAINBOW = 4;
    public static final int SPRITZ = 3;
    public static final int TONAL_SPOT = 0;
    public static final int VIBRANT = 1;

    private ThemeStyleType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
    }
}

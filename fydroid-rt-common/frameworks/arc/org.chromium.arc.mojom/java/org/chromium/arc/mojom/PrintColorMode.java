// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class PrintColorMode {
    public static final int COLOR = 2;
    public static final int MONOCHROME = 1;

    private PrintColorMode() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

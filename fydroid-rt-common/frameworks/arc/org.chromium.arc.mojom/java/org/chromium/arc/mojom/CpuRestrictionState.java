// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class CpuRestrictionState {
    public static final int CPU_RESTRICTION_BACKGROUND = 1;
    public static final int CPU_RESTRICTION_FOREGROUND = 0;

    private CpuRestrictionState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

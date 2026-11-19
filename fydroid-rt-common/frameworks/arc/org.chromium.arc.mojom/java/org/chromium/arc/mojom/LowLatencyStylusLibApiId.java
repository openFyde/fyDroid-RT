// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class LowLatencyStylusLibApiId {
    public static final int GL_INK_OVERLAY_LOW_LATENCY_LIB_IN_USE = 2;
    public static final int GL_INK_OVERLAY_SET_PROJECTION_MATRIX = 3;
    public static final int GL_INK_OVERLAY_SET_VIEW_MATRIX = 4;
    public static final int INK_OVERLAY_LOW_LATENCY_LIB_IN_USE = 1;
    public static final int UNSUPPORTED = 0;

    private LowLatencyStylusLibApiId() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

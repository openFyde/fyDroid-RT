// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class NativeBridgeType {
    public static final int HOUDINI = 1;
    public static final int NDK_TRANSLATION = 2;
    public static final int NONE = 0;

    private NativeBridgeType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

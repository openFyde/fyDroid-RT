// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class MeteredOverride {
    public static final int METERED = 1;
    public static final int NONE = 0;
    public static final int NOTMETERED = 2;

    private MeteredOverride() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

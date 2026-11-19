// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class TetheringClientState {
    public static final int CONFIRMED = 0;
    public static final int NOT_DETECTED = 1;
    public static final int SUSPECTED = 2;

    private TetheringClientState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

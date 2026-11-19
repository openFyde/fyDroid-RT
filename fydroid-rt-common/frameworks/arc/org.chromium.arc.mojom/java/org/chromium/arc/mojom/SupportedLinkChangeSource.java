// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class SupportedLinkChangeSource {
    public static final int ARC_SYSTEM = 1;
    public static final int UNKNOWN = 0;
    public static final int USER_PREFERENCE = 2;

    private SupportedLinkChangeSource() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

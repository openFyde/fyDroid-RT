// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

public final class AudioFocusType {
    public static final int AMBIENT = 3;
    public static final int GAIN = 0;
    public static final int GAIN_TRANSIENT = 2;
    public static final int GAIN_TRANSIENT_MAY_DUCK = 1;

    private AudioFocusType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

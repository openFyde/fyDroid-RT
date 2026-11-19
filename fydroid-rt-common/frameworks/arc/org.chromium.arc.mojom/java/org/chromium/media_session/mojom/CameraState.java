// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

public final class CameraState {
    public static final int TURNED_OFF = 2;
    public static final int TURNED_ON = 1;
    public static final int UNKNOWN = 0;

    private CameraState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

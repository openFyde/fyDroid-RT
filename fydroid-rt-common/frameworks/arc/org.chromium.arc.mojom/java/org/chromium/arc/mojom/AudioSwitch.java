// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class AudioSwitch {
    public static final int SW_HEADPHONE_INSERT = 2;
    public static final int SW_MICROPHONE_INSERT = 4;

    private AudioSwitch() {
    }

    public static boolean isKnownValue(int i) {
        return i == 2 || i == 4;
    }

    public static void validate(int i) {
    }
}

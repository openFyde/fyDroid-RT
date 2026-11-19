// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class TtsEventType {
    public static final int END = 1;
    public static final int ERROR = 3;
    public static final int INTERRUPTED = 2;
    public static final int START = 0;
    public static final int WORD = 4;

    private TtsEventType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.mojo_base.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class ThreadPriority {
    public static final int BACKGROUND = 0;
    public static final int DISPLAY = 2;
    public static final int NORMAL = 1;
    public static final int REALTIME_AUDIO = 3;

    private ThreadPriority() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

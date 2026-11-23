// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.mojo_base.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class TextDirection {
    public static final int LEFT_TO_RIGHT = 2;
    public static final int RIGHT_TO_LEFT = 1;
    public static final int UNKNOWN_DIRECTION = 0;

    private TextDirection() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

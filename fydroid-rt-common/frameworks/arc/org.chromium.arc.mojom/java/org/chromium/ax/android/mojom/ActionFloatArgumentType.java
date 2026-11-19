// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class ActionFloatArgumentType {
    public static final int PROGRESS_VALUE = 0;

    private ActionFloatArgumentType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

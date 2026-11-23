// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidLensFacing {
    public static final int ANDROID_LENS_FACING_BACK = 1;
    public static final int ANDROID_LENS_FACING_EXTERNAL = 2;
    public static final int ANDROID_LENS_FACING_FRONT = 0;

    private AndroidLensFacing() {
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

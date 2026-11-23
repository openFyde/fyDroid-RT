// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AppPrimaryAbi {
    public static final int ARM = 1;
    public static final int ARM64 = 2;
    public static final int NONE = 0;
    public static final int X86 = 3;
    public static final int X86_64 = 4;

    private AppPrimaryAbi() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class GmsCheckInError {
    public static final int GMS_CHECK_IN_FAILED = 1;
    public static final int GMS_CHECK_IN_INTERNAL_ERROR = 3;
    public static final int GMS_CHECK_IN_TIMEOUT = 2;

    private GmsCheckInError() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

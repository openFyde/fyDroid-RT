// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class AnrType {
    public static final int APP_REQUESTED = 5;
    public static final int BROADCAST = 3;
    public static final int CONTENT_PROVIDER = 4;
    public static final int INPUT = 1;
    public static final int PROCESS = 6;
    public static final int SERVICE = 2;
    public static final int UNKNOWN = 0;

    private AnrType() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

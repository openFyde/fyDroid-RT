// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ArcKeyMintError {
    public static final int INVALID_ARGUMENT = 4;
    public static final int INVALID_KEY_BLOB = 3;
    public static final int OK = 1;
    public static final int OTHER = 0;
    public static final int UNIMPLEMENTED = 6;
    public static final int UNKNOWN_ERROR = 2;
    public static final int UNSUPPORTED_ALGORITHM = 5;

    private ArcKeyMintError() {
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

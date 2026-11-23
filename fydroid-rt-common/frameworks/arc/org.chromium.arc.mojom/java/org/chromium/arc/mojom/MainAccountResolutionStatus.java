// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class MainAccountResolutionStatus {
    public static final int HASH_CODE_COLLIDE_FOR_MULTIPLE_ACCOUNTS = 8;
    public static final int HASH_CODE_DO_NOT_MATCH_MULTIPLE_ACCOUNTS = 5;
    public static final int HASH_CODE_DO_NOT_MATCH_SINGLE_ACCOUNT = 4;
    public static final int HASH_CODE_MATCH_MULTIPLE_ACCOUNTS = 7;
    public static final int HASH_CODE_MATCH_SINGLE_ACCOUNT = 6;
    public static final int HASH_CODE_NO_ACCOUNT = 3;
    public static final int NO_HASH_CODE_MULTIPLE_ACCOUNTS = 2;
    public static final int NO_HASH_CODE_NO_ACCOUNT = 0;
    public static final int NO_HASH_CODE_SINGLE_ACCOUNT = 1;

    private MainAccountResolutionStatus() {
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
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

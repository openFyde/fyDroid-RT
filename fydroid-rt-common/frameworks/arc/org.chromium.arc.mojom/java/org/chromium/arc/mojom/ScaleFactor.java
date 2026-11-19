// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ScaleFactor {
    public static final int NUM_SCALE_FACTORS = 10;
    public static final int SCALE_FACTOR_100P = 1;
    public static final int SCALE_FACTOR_125P = 2;
    public static final int SCALE_FACTOR_133P = 3;
    public static final int SCALE_FACTOR_140P = 4;
    public static final int SCALE_FACTOR_150P = 5;
    public static final int SCALE_FACTOR_180P = 6;
    public static final int SCALE_FACTOR_200P = 7;
    public static final int SCALE_FACTOR_250P = 8;
    public static final int SCALE_FACTOR_300P = 9;
    public static final int SCALE_FACTOR_NONE = 0;

    private ScaleFactor() {
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
            case 9:
            case 10:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class AnrSource {
    public static final int ARC_APP_LAUNCHER = 7;
    public static final int ARC_OTHER = 6;
    public static final int FIRST_PARTY = 5;
    public static final int GMS_CORE = 3;
    public static final int OTHER = 0;
    public static final int PLAY_STORE = 4;
    public static final int SYSTEM_APP = 2;
    public static final int SYSTEM_SERVER = 1;

    private AnrSource() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

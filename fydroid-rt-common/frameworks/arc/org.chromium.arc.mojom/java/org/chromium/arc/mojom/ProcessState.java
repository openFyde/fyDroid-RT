// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ProcessState {
    public static final int BACKUP = 8;
    public static final int BOUND_FOREGROUND_SERVICE = 4;
    public static final int CACHED_ACTIVITY = 15;
    public static final int CACHED_ACTIVITY_CLIENT = 16;
    public static final int CACHED_EMPTY = 18;
    public static final int CACHED_RECENT = 17;
    public static final int FOREGROUND_SERVICE = 3;
    public static final int HEAVY_WEIGHT = 12;
    public static final int HOME = 13;
    public static final int IMPORTANT_BACKGROUND = 6;
    public static final int IMPORTANT_FOREGROUND = 5;
    public static final int LAST_ACTIVITY = 14;
    public static final int NONEXISTENT = 19;
    public static final int PERSISTENT = 0;
    public static final int PERSISTENT_UI = 1;
    public static final int RECEIVER = 10;
    public static final int R_BACKUP = 9;
    public static final int R_BOUND_FOREGROUND_SERVICE = 5;
    public static final int R_BOUND_TOP = 3;
    public static final int R_CACHED_ACTIVITY = 16;
    public static final int R_CACHED_ACTIVITY_CLIENT = 17;
    public static final int R_CACHED_EMPTY = 19;
    public static final int R_CACHED_RECENT = 18;
    public static final int R_FOREGROUND_SERVICE = 4;
    public static final int R_HEAVY_WEIGHT = 13;
    public static final int R_HOME = 14;
    public static final int R_IMPORTANT_BACKGROUND = 7;
    public static final int R_IMPORTANT_FOREGROUND = 6;
    public static final int R_LAST_ACTIVITY = 15;
    public static final int R_NONEXISTENT = 20;
    public static final int R_PERSISTENT = 0;
    public static final int R_PERSISTENT_UI = 1;
    public static final int R_RECEIVER = 11;
    public static final int R_SERVICE = 10;
    public static final int R_TOP = 2;
    public static final int R_TOP_SLEEPING = 12;
    public static final int R_TRANSIENT_BACKGROUND = 8;
    public static final int R_UNKNOWN = -1;
    public static final int SERVICE = 9;
    public static final int TOP = 2;
    public static final int TOP_SLEEPING = 11;
    public static final int TRANSIENT_BACKGROUND = 7;
    public static final int UNKNOWN = -1;

    private ProcessState() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case -1:
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
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

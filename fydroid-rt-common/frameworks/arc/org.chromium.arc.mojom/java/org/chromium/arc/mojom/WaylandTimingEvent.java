// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class WaylandTimingEvent {
    public static final int BINDER_RELEASE_CLIPBOARD_DATA = 1;
    public static final int OTHER = 0;
    public static final int WL_BUFFER_RELEASE = 2;
    public static final int WL_KEYBOARD_LEAVE = 3;
    public static final int WL_POINTER_LEAVE = 5;
    public static final int WL_POINTER_MOTION = 4;
    public static final int ZAURA_SHELL_ACTIVATED = 6;
    public static final int ZAURA_SURFACE_OCCLUSION_CHANGED = 7;
    public static final int ZCR_REMOTE_SURFACE_BOUNDS_CHANGED_IN_OUTPUT = 9;
    public static final int ZCR_REMOTE_SURFACE_WINDOW_GEOMETRY_CHANGED = 8;
    public static final int ZCR_VSYNC_TIMING_UPDATE = 10;

    private WaylandTimingEvent() {
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

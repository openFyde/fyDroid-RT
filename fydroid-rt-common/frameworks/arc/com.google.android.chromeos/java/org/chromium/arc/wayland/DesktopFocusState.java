/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Open source replacement for org.chromium.arc.wayland.DesktopFocusState
 * from the proprietary com.google.android.chromeos jar. Integer constants
 * describing the desktop-level focus state reported by wayland_service.
 *
 * Values are bit-for-bit identical to the proprietary class so any
 * in-process consumer keeps working unchanged.
 */
package org.chromium.arc.wayland;

public @interface DesktopFocusState {
    public static final int CLIENT_FOCUSED = 2;
    public static final int NO_FOCUS = 1;
    public static final int OTHER_CLIENT_FOCUSED = 3;
}

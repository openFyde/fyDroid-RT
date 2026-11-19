/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Open source replacement for org.chromium.arc.wayland.CtrlType from the
 * proprietary com.google.android.chromeos jar. Integer constants describing
 * the drag / resize handle used by IWaylandShellHandler#taskDragStarted.
 *
 * Values are bit-for-bit identical to the proprietary class. Note that the
 * numbering intentionally skips 3 and 7 (there is no CTRL_TYPE value for
 * those ints) — this matches the proprietary layout and is preserved here
 * verbatim so any in-process consumer keeps working unchanged.
 */
package org.chromium.arc.wayland;

public @interface CtrlType {
    public static final int CTRL_TYPE_BOTTOM = 8;
    public static final int CTRL_TYPE_BOTTOM_LEFT = 9;
    public static final int CTRL_TYPE_BOTTOM_RIGHT = 10;
    public static final int CTRL_TYPE_LEFT = 1;
    public static final int CTRL_TYPE_NONE = 0;
    public static final int CTRL_TYPE_RIGHT = 2;
    public static final int CTRL_TYPE_TOP = 4;
    public static final int CTRL_TYPE_TOP_LEFT = 5;
    public static final int CTRL_TYPE_TOP_RIGHT = 6;
}

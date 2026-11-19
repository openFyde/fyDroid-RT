package org.chromium.arc.wayland;

/**
 * Notified by wayland_service when Chrome reports a display orientation
 * change. Single one-way method (matches the proprietary interface).
 */
oneway interface IWaylandOrientationChangeListener {
    // 1
    void onDisplayOrientationChange(int newOrientation);
}

/* Open source replacement for com.google.android.chromeos.IArcCompanionCallback from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos;

import android.graphics.Rect;

/**
 * Callback registered by application code with the Chrome OS systemui
 * ArcCompanionService. wayland_service / systemui notifies client apps of
 * device-mode / workspace / task-bounds changes through this binder.
 *
 * One-way: every method in the proprietary Proxy calls
 * transact(..., null, FLAG_ONEWAY).
 *
 * Method declaration order matches the proprietary transaction codes 1..3:
 *   1  onDeviceModeChanged
 *   2  onWorkspaceInsetsChanged
 *   3  onTaskBoundsChanged
 */
oneway interface IArcCompanionCallback {
    // 1
    void onDeviceModeChanged(int deviceMode);
    // 2
    void onWorkspaceInsetsChanged(int displayId, in Rect insets);
    // 3
    void onTaskBoundsChanged(in Rect bounds);
}

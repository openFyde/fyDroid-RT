/* Open source replacement for com.android.server.wm.ITaskOrientationListener
 * from the proprietary com.google.android.chromeos jar. */
package com.android.server.wm;

/**
 * Listener registered via IArcWindowManager.registerTaskOrientationListener.
 *
 * Proprietary binder descriptor:
 *   "com.android.server.wm.ITaskOrientationListener"
 *
 * Proxy transacts with FLAG_ONEWAY, so this interface is declared oneway.
 * Single method with transaction code 1.
 */
oneway interface ITaskOrientationListener {
    // 1
    void onTaskOrientationChanged(int taskId, int requestedOrientation);
}

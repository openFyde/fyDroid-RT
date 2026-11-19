/* Open source replacement for com.android.server.wm.ILockTaskListener
 * from the proprietary com.google.android.chromeos jar. */
package com.android.server.wm;

/**
 * Listener registered via IArcWindowManager.registerLockTaskListener.
 *
 * Proprietary binder descriptor:
 *   "com.android.server.wm.ILockTaskListener"
 *
 * Proxy transacts with FLAG_ONEWAY, so this interface is declared oneway.
 * Single method with transaction code 1.
 */
oneway interface ILockTaskListener {
    // 1
    void onLockTaskModeChanged(int taskId, int lockTaskMode);
}

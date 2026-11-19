/* Open source replacement for com.android.server.wm.IArcWindowDrawnCallback
 * from the proprietary com.google.android.chromeos jar. */
package com.android.server.wm;

/**
 * Callback invoked by IArcWindowManager.waitForAllWindowsDrawn /
 * waitForMainAppWindowDrawn when the target windows have been drawn.
 *
 * Proprietary binder descriptor:
 *   "com.android.server.wm.IArcWindowDrawnCallback"
 *
 * Proxy transacts with FLAG_ONEWAY, so this interface is declared oneway.
 * Single method with transaction code 1.
 */
oneway interface IArcWindowDrawnCallback {
    // 1
    void notifyAllWindowDrawn();
}

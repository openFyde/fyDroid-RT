/* Open source replacement for org.chromium.arc.applauncher.ITaskCloserCallback
 * from the proprietary com.google.android.chromeos jar. */
package org.chromium.arc.applauncher;

/**
 * Callback registered with ITaskCloserService.setHandler. Chrome invokes
 * onCloseRequest when the user asks to close the window and the Android
 * side is expected to gracefully tear down the task.
 *
 * One-way in the proprietary jar (Proxy.transact(..., null, 1) with
 * FLAG_ONEWAY and a null reply parcel).
 */
oneway interface ITaskCloserCallback {
    // 1
    void onCloseRequest();
}

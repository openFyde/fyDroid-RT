/* Open source replacement for org.chromium.arc.applauncher.ITaskCloserService
 * from the proprietary com.google.android.chromeos jar. */
package org.chromium.arc.applauncher;

import org.chromium.arc.applauncher.ITaskCloserCallback;

/**
 * Binder the launcher side uses to manage the "Chrome asked to close this
 * task" flow. Clients register an ITaskCloserCallback per taskId, then
 * notify the service about close-in-progress / cancel / finish states
 * while they animate the close.
 *
 * Method order matches the proprietary transaction codes 1..4 one-for-one:
 *   1 setHandler
 *   2 notifyCloseInProgress
 *   3 cancelClose
 *   4 finishClose
 *
 * All methods synchronous in the proprietary jar (transact flag 0).
 */
interface ITaskCloserService {
    // 1
    void setHandler(int taskId, ITaskCloserCallback callback);
    // 2
    void notifyCloseInProgress(int taskId);
    // 3
    void cancelClose(int taskId);
    // 4
    void finishClose(int taskId);
}

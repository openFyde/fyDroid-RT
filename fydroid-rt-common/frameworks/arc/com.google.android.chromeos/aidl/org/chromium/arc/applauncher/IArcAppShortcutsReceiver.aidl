/* Open source replacement for org.chromium.arc.applauncher.IArcAppShortcutsReceiver
 * from the proprietary com.google.android.chromeos jar. */
package org.chromium.arc.applauncher;

import org.chromium.arc.applauncher.AppShortcutItemInfo;

/**
 * Callback supplied by the ArcAppShortcuts launcher client. The service
 * (IArcAppShortcutsService) invokes onReceiveResults asynchronously with
 * the list of shortcut items that match the most recent request.
 *
 * Single synchronous transaction (#1) in the proprietary interface, kept
 * synchronous here so the tx code layout stays bit-for-bit identical.
 */
interface IArcAppShortcutsReceiver {
    // 1
    void onReceiveResults(in List<AppShortcutItemInfo> results);
}

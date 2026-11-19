/* Open source replacement for org.chromium.arc.applauncher.IArcAppShortcutsService
 * from the proprietary com.google.android.chromeos jar. */
package org.chromium.arc.applauncher;

import org.chromium.arc.applauncher.IArcAppShortcutsReceiver;

/**
 * Binder exposed by arc-services (ArcHome) for app shortcut queries and
 * launches. Launcher-style clients call getAppShortcutGlobalQueryItems /
 * getAppShortcutItems with an IArcAppShortcutsReceiver, then invoke
 * launchAppShortcutItem once the user picks a result.
 *
 * Method order matches the proprietary transaction codes 1..3 one-for-one:
 *   1 getAppShortcutGlobalQueryItems
 *   2 getAppShortcutItems
 *   3 launchAppShortcutItem
 *
 * All methods are synchronous in the proprietary jar (transact flag 0).
 */
interface IArcAppShortcutsService {
    // 1
    void getAppShortcutGlobalQueryItems(String query, int maxResults,
            IArcAppShortcutsReceiver receiver);
    // 2
    void getAppShortcutItems(String packageName, IArcAppShortcutsReceiver receiver);
    // 3
    void launchAppShortcutItem(String packageName, String shortcutId, int displayId);
}

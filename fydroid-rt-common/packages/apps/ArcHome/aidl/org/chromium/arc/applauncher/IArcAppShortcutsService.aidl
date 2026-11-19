package org.chromium.arc.applauncher;

import org.chromium.arc.applauncher.IArcAppShortcutsReceiver;

interface IArcAppShortcutsService {
    void getAppShortcutGlobalQueryItems(String query, int maxResults,
            IArcAppShortcutsReceiver receiver);
    void getAppShortcutItems(String packageName,
            IArcAppShortcutsReceiver receiver);
    void launchAppShortcutItem(String packageName, String shortcutId, int displayId);
}

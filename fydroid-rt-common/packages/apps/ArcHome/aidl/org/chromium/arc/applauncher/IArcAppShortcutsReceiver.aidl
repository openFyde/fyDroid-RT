package org.chromium.arc.applauncher;

import org.chromium.arc.applauncher.AppShortcutItemInfo;

oneway interface IArcAppShortcutsReceiver {
    void onReceiveResults(in List<AppShortcutItemInfo> results);
}

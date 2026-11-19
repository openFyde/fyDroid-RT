package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AppHost extends Interface {
    public static final Interface.Manager<AppHost, Proxy> MANAGER = AppHost_Internal.MANAGER;

    public interface Proxy extends AppHost, Interface.Proxy {
    }

    void onAppAddedDeprecated(AppInfo appInfo);

    void onAppListRefreshed(AppInfo[] appInfoArr);

    void onInstallShortcut(ShortcutInfo shortcutInfo);

    void onInstallationActiveChanged(String str, boolean z);

    void onInstallationFinished(InstallationResult installationResult);

    void onInstallationProgressChanged(String str, float f);

    void onInstallationStarted(String str);

    void onNotificationsEnabledChanged(String str, boolean z);

    void onPackageAdded(ArcPackageInfo arcPackageInfo);

    void onPackageAppListRefreshed(String str, AppInfo[] appInfoArr);

    void onPackageListRefreshed(ArcPackageInfo[] arcPackageInfoArr);

    void onPackageModified(ArcPackageInfo arcPackageInfo);

    void onPackageRemoved(String str);

    void onTaskCreated(int i, String str, String str2, String str3, String str4, int i2);

    void onTaskDescriptionChanged(int i, String str, RawIconPngData rawIconPngData, int i2, int i3);

    void onTaskDescriptionUpdated(int i, String str, byte[] bArr);

    void onTaskDestroyed(int i);

    void onTaskSetActive(int i);

    void onUninstallShortcut(String str, String str2);
}

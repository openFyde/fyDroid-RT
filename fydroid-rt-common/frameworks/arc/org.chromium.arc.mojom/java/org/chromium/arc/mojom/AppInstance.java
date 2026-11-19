// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AppInstance extends Interface {
    public static final Interface.Manager<AppInstance, Proxy> MANAGER = AppInstance_Internal.MANAGER;

    public interface GetAndroidIdResponse extends Callbacks.Callback1<Long> {
    }

    public interface GetAppCategoryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetAppIconResponse extends Callbacks.Callback1<RawIconPngData> {
    }

    public interface GetAppShortcutGlobalQueryItemsResponse extends Callbacks.Callback1<AppShortcutItem[]> {
    }

    public interface GetAppShortcutIconResponse extends Callbacks.Callback1<RawIconPngData> {
    }

    public interface GetAppShortcutItemsResponse extends Callbacks.Callback1<AppShortcutItem[]> {
    }

    public interface GetPackageIconResponse extends Callbacks.Callback1<RawIconPngData> {
    }

    public interface GetRecentAndSuggestedAppsFromPlayStoreResponse extends Callbacks.Callback2<Integer, AppDiscoveryResult[]> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface IsInstallableResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends AppInstance, Interface.Proxy {
    }

    public interface RequestAppIconResponse extends Callbacks.Callback1<byte[]> {
    }

    public interface RequestPackageIconResponse extends Callbacks.Callback1<byte[]> {
    }

    public interface RequestShortcutIconResponse extends Callbacks.Callback1<byte[]> {
    }

    public interface StartPaiFlowResponse extends Callbacks.Callback1<Integer> {
    }

    void closeTask(int i);

    void getAndroidId(GetAndroidIdResponse getAndroidIdResponse);

    void getAppCategory(String str, GetAppCategoryResponse getAppCategoryResponse);

    void getAppIcon(String str, String str2, int i, GetAppIconResponse getAppIconResponse);

    void getAppShortcutGlobalQueryItems(String str, int i, GetAppShortcutGlobalQueryItemsResponse getAppShortcutGlobalQueryItemsResponse);

    void getAppShortcutIcon(String str, int i, GetAppShortcutIconResponse getAppShortcutIconResponse);

    void getAppShortcutItems(String str, GetAppShortcutItemsResponse getAppShortcutItemsResponse);

    void getPackageIcon(String str, int i, boolean z, GetPackageIconResponse getPackageIconResponse);

    void getRecentAndSuggestedAppsFromPlayStore(String str, int i, GetRecentAndSuggestedAppsFromPlayStoreResponse getRecentAndSuggestedAppsFromPlayStoreResponse);

    void init(AppHost appHost, InitResponse initResponse);

    void installPackage(ArcPackageInfo arcPackageInfo);

    void isInstallable(String str, IsInstallableResponse isInstallableResponse);

    void launchAppShortcutItem(String str, String str2, long j);

    void launchAppWithWindowInfo(String str, String str2, WindowInfo windowInfo);

    void launchIntentWithWindowInfo(String str, WindowInfo windowInfo);

    void removeCachedIcon(String str);

    void requestAppIcon(String str, String str2, int i, RequestAppIconResponse requestAppIconResponse);

    void requestPackageIcon(String str, int i, boolean z, RequestPackageIconResponse requestPackageIconResponse);

    void requestShortcutIcon(String str, int i, RequestShortcutIconResponse requestShortcutIconResponse);

    void setAppLocale(String str, String str2);

    void setNotificationsEnabled(String str, boolean z);

    void setTaskActive(int i);

    void showPackageInfoDeprecated(String str, Rect rect);

    void showPackageInfoOnPage(String str, int i, long j);

    void showPackageInfoOnPageDeprecated(String str, int i, Rect rect);

    void startFastAppReinstallFlow(String[] strArr);

    void startPaiFlow(StartPaiFlowResponse startPaiFlowResponse);

    void uninstallPackage(String str);

    void updateAppDetails(String str);

    void updateWindowInfo(WindowInfo windowInfo);
}

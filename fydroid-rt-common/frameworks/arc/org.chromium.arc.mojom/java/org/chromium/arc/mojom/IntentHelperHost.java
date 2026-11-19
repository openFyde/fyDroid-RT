package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.url.mojom.Url;

public interface IntentHelperHost extends Interface {
    public static final Interface.Manager<IntentHelperHost, Proxy> MANAGER = IntentHelperHost_Internal.MANAGER;

    public interface IsChromeAppEnabledResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface OnOpenCustomTabResponse extends Callbacks.Callback1<CustomTabSession> {
    }

    public interface Proxy extends IntentHelperHost, Interface.Proxy {
    }

    void closeCameraApp();

    void factoryResetArc();

    void isChromeAppEnabled(int i, IsChromeAppEnabledResponse isChromeAppEnabledResponse);

    void launchCameraApp(int i, int i2, boolean z, boolean z2, boolean z3, int i3);

    void onAndroidSettingChange(int i, boolean z);

    void onCloseSystemDialogs();

    void onDownloadAddedDeprecated(String str, String str2);

    void onIconInvalidated(String str);

    void onIntentFiltersUpdated(IntentFilter[] intentFilterArr);

    void onIntentFiltersUpdatedForPackage(String str, IntentFilter[] intentFilterArr);

    void onOpenAppWithIntent(Url url, LaunchIntent launchIntent);

    void onOpenChromePage(int i);

    void onOpenCustomTab(String str, int i, OnOpenCustomTabResponse onOpenCustomTabResponse);

    void onOpenDownloads();

    void onOpenGlobalActions();

    void onOpenUrl(String str);

    void onOpenWebApp(String str);

    void onSupportedLinksChanged(SupportedLinksPackage[] supportedLinksPackageArr, SupportedLinksPackage[] supportedLinksPackageArr2, int i);

    void openVolumeControl();

    void openWallpaperPicker();

    void recordShareFilesMetricsDeprecated(int i);
}

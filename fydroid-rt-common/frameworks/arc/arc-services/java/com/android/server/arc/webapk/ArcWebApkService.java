package com.android.server.arc.webapk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.android.server.SystemService;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.WebApkInfo;
import org.chromium.arc.mojom.WebApkInstance;
import org.chromium.arc.mojom.WebApkInstallResult;
import org.chromium.mojo.system.MojoException;

/**
 * Handles WebAPK queries from Chrome OS.
 *
 * WebAPKs are Chrome-managed APKs for Progressive Web Apps. Since we don't have
 * Play Store / GMS, installation always returns ERROR_API_DISABLED. Queries for
 * existing WebAPK metadata are forwarded to PackageManager.
 */
class ArcWebApkService implements WebApkInstance, ArcService {
    private static final String TAG = "ArcWebApk";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    // WebAPK metadata keys (set by Chrome when installing WebAPKs)
    private static final String META_MANIFEST_URL = "org.chromium.webapk.shell_apk.manifestUrl";
    private static final String META_START_URL = "org.chromium.webapk.shell_apk.startUrl";
    private static final String META_SCOPE = "org.chromium.webapk.shell_apk.scope";
    private static final String META_NAME = "org.chromium.webapk.shell_apk.name";
    private static final String META_SHELL_APK_VERSION = "org.chromium.webapk.shell_apk.shellApkVersion";
    private static final String META_ICON_HASH = "org.chromium.webapk.shell_apk.iconHash";

    private final Context mContext;

    public static class LifeCycle extends SystemService {
        private final ArcWebApkService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcWebApkService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
        }
    }

    ArcWebApkService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- WebApkInstance ---

    @Override
    public void getWebApkInfo(String packageName,
            GetWebApkInfoResponse response) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(packageName, 0);
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            Bundle meta = appInfo.metaData;

            if (meta == null || !meta.containsKey(META_MANIFEST_URL)) {
                // Not a WebAPK
                response.call(null);
                return;
            }

            WebApkInfo info = new WebApkInfo();
            info.packageName = packageName;
            info.apkVersion = pkgInfo.versionName != null ? pkgInfo.versionName : "";
            info.shellApkVersion = meta.getString(META_SHELL_APK_VERSION, "");
            info.manifestUrl = meta.getString(META_MANIFEST_URL, "");
            info.name = meta.getString(META_NAME, "");
            info.startUrl = meta.getString(META_START_URL, "");
            info.scope = meta.getString(META_SCOPE, "");
            info.iconHash = meta.getString(META_ICON_HASH, "");
            info.shareInfo = null;

            response.call(info);
        } catch (PackageManager.NameNotFoundException e) {
            response.call(null);
        }
    }

    @Override
    public void installWebApk(String packageName, int version, String appName,
            String token, InstallWebApkResponse response) {
        // No GMS/Play Store available — installation not supported
        Log.i(TAG, "installWebApk: API disabled (no GMS), pkg=" + packageName);
        response.call(WebApkInstallResult.ERROR_API_DISABLED);
    }

    @Override
    public void close() {
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering WebApkInstance");
        host.onWebApkInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version);
    }
}

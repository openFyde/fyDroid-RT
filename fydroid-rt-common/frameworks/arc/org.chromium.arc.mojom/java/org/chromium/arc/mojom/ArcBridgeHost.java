// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.keymint.KeyMintInstance;
import org.chromium.ax.android.mojom.AccessibilityHelperInstance;
import org.chromium.chromeos.payments.mojom.PaymentAppInstance;
import org.chromium.mojo.bindings.Interface;

public interface ArcBridgeHost extends Interface {
    public static final Interface.Manager<ArcBridgeHost, Proxy> MANAGER = ArcBridgeHost_Internal.MANAGER;

    public interface Proxy extends ArcBridgeHost, Interface.Proxy {
    }

    void onAccessibilityHelperInstanceReady(AccessibilityHelperInstance accessibilityHelperInstance);

    void onAdbdMonitorInstanceReady(AdbdMonitorInstance adbdMonitorInstance);

    void onAppInstanceReady(AppInstance appInstance);

    void onAppPermissionsInstanceReady(AppPermissionsInstance appPermissionsInstance);

    void onAppfuseInstanceReady(AppfuseInstance appfuseInstance);

    void onArcWifiInstanceReady(ArcWifiInstance arcWifiInstance);

    void onAudioInstanceReady(AudioInstance audioInstance);

    void onAuthInstanceReady(AuthInstance authInstance);

    void onBackupSettingsInstanceReady(BackupSettingsInstance backupSettingsInstance);

    void onBluetoothInstanceReady(BluetoothInstance bluetoothInstance);

    void onBootPhaseMonitorInstanceReady(BootPhaseMonitorInstance bootPhaseMonitorInstance);

    void onCameraInstanceReady(CameraInstance cameraInstance);

    void onChromeFeatureFlagsInstanceReady(ChromeFeatureFlagsInstance chromeFeatureFlagsInstance);

    void onCompatibilityModeInstanceReady(CompatibilityModeInstance compatibilityModeInstance);

    void onCrashCollectorInstanceReady(CrashCollectorInstance crashCollectorInstance);

    void onDigitalGoodsInstanceReady(DigitalGoodsInstance digitalGoodsInstance);

    void onDiskSpaceInstanceReady(DiskSpaceInstance diskSpaceInstance);

    void onEnterpriseReportingInstanceReady(EnterpriseReportingInstance enterpriseReportingInstance);

    void onErrorNotificationInstanceReady(ErrorNotificationInstance errorNotificationInstance);

    void onFileSystemInstanceReady(FileSystemInstance fileSystemInstance);

    void onIioSensorInstanceReady(IioSensorInstance iioSensorInstance);

    void onImeInstanceReady(ImeInstance imeInstance);

    void onInputMethodManagerInstanceReady(InputMethodManagerInstance inputMethodManagerInstance);

    void onIntentHelperInstanceReady(IntentHelperInstance intentHelperInstance);

    void onKeyMintInstanceReady(KeyMintInstance keyMintInstance);

    void onKeymasterInstanceReady(KeymasterInstance keymasterInstance);

    void onMediaSessionInstanceReady(MediaSessionInstance mediaSessionInstance);

    void onMemoryInstanceReady(MemoryInstance memoryInstance);

    void onMetricsInstanceReady(MetricsInstance metricsInstance);

    void onMidisInstanceReady(MidisInstance midisInstance);

    void onNearbyShareInstanceReady(NearbyShareInstance nearbyShareInstance);

    void onNetInstanceReady(NetInstance netInstance);

    void onNotificationsInstanceReady(NotificationsInstance notificationsInstance);

    void onObbMounterInstanceReady(ObbMounterInstance obbMounterInstance);

    void onOemCryptoInstanceReady(OemCryptoInstance oemCryptoInstance);

    void onPaymentAppInstanceReady(PaymentAppInstance paymentAppInstance);

    void onPipInstanceReady(PipInstance pipInstance);

    void onPolicyInstanceReady(PolicyInstance policyInstance);

    void onPowerInstanceReady(PowerInstance powerInstance);

    void onPrintSpoolerInstanceReady(PrintSpoolerInstance printSpoolerInstance);

    void onPrivacyItemsInstanceReady(PrivacyItemsInstance privacyItemsInstance);

    void onProcessInstanceReady(ProcessInstance processInstance);

    void onScreenCaptureInstanceReady(ScreenCaptureInstance screenCaptureInstance);

    void onSharesheetInstanceReady(SharesheetInstance sharesheetInstance);

    void onSystemStateInstanceReady(SystemStateInstance systemStateInstance);

    void onSystemUiInstanceReady(SystemUiInstance systemUiInstance);

    void onTimerInstanceReady(TimerInstance timerInstance);

    void onTracingInstanceReady(TracingInstance tracingInstance);

    void onTtsInstanceReady(TtsInstance ttsInstance);

    void onUsbHostInstanceReady(UsbHostInstance usbHostInstance);

    void onVideoInstanceReady(VideoInstance videoInstance);

    void onVolumeMounterInstanceReady(VolumeMounterInstance volumeMounterInstance);

    void onWakeLockInstanceReady(WakeLockInstance wakeLockInstance);

    void onWallpaperInstanceReady(WallpaperInstance wallpaperInstance);

    void onWebApkInstanceReady(WebApkInstance webApkInstance);
}

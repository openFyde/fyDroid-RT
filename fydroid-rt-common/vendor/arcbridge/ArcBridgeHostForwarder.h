// Per-client ArcBridgeHost proxy.
//
// For each client that calls getMojoBootstrapChannel(), we create an
// ArcBridgeHostForwarder bound to the client's message pipe. When the
// client sends OnXxxInstanceReady messages, the forwarder receives them
// (via StubDispatch in libarcbridge.so) and forwards to Chrome OS via
// the root ArcBridgeHostProxy (also in libarcbridge.so).
//
// All methods use non-virtual calls (proxy->ArcBridgeHostProxy::OnXxx)
// to bypass vtable dispatch on the root proxy, matching the arcwlservice
// pattern. This ensures correct dispatch even if our vtable layout has
// minor differences from libarcbridge.so's internal layout.
#pragma once

#include <map>
#include <memory>
#include <set>
#include <string>
#include <base/logging.h>
#include <base/single_thread_task_runner.h>
#include <mojo/public/cpp/bindings/binding.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>

#include <arc/arc_bridge.mojom.h>

namespace arc {

class ArcBridgeHostForwarder : public arc::mojom::ArcBridgeHost {
public:
    // |root_host| must outlive this object. It points to the InterfacePtr
    // for the root Mojo connection to Chrome OS. |host_version| points at the
    // version Chrome reported for that connection (0 until QueryVersion
    // replies); methods newer than Chrome are dropped rather than forwarded —
    // see OnCdmInstanceReady.
    ArcBridgeHostForwarder(
        mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* root_host,
        const uint32_t* host_version,
        int client_pid);
    ~ArcBridgeHostForwarder() override;

    // Bind this forwarder to a message pipe handle (the local end of the
    // pipe sent to the client via getMojoBootstrapChannel).
    void Bind(mojo::ScopedMessagePipeHandle pipe,
              scoped_refptr<base::SingleThreadTaskRunner> task_runner);

    int client_pid() const { return client_pid_; }

    // --- ArcBridgeHost overrides (all 58 methods) ---
    // Each forwards to root_host_ via non-virtual call on the proxy.

    void OnAccessibilityHelperInstanceReady(
        mojo::InterfacePtr<ax::android::mojom::AccessibilityHelperInstance> i) override;
    void OnAdbdMonitorInstanceReady(
        mojo::InterfacePtr<arc::mojom::AdbdMonitorInstance> i) override;
    void OnAppInstanceReady(
        mojo::InterfacePtr<arc::mojom::AppInstance> i) override;
    void OnAppPermissionsInstanceReady(
        mojo::InterfacePtr<arc::mojom::AppPermissionsInstance> i) override;
    void OnAppfuseInstanceReady(
        mojo::InterfacePtr<arc::mojom::AppfuseInstance> i) override;
    void OnArcShellExecutionInstanceReady(
        mojo::InterfacePtr<arc::mojom::ArcShellExecutionInstance> i) override;
    void OnArcWifiInstanceReady(
        mojo::InterfacePtr<arc::mojom::ArcWifiInstance> i) override;
    void OnAudioInstanceReady(
        mojo::InterfacePtr<arc::mojom::AudioInstance> i) override;
    void OnAuthInstanceReady(
        mojo::InterfacePtr<arc::mojom::AuthInstance> i) override;
    void OnBackupSettingsInstanceReady(
        mojo::InterfacePtr<arc::mojom::BackupSettingsInstance> i) override;
    void OnBluetoothInstanceReady(
        mojo::InterfacePtr<arc::mojom::BluetoothInstance> i) override;
    void OnBootPhaseMonitorInstanceReady(
        mojo::InterfacePtr<arc::mojom::BootPhaseMonitorInstance> i) override;
    void OnCameraInstanceReady(
        mojo::InterfacePtr<arc::mojom::CameraInstance> i) override;
    void OnCdmInstanceReady(
        mojo::InterfacePtr<arc::mojom::CdmInstance> i) override;
    void OnChromeFeatureFlagsInstanceReady(
        mojo::InterfacePtr<arc::mojom::ChromeFeatureFlagsInstance> i) override;
    void OnCompatibilityModeInstanceReady(
        mojo::InterfacePtr<arc::mojom::CompatibilityModeInstance> i) override;
    void OnCrashCollectorInstanceReady(
        mojo::InterfacePtr<arc::mojom::CrashCollectorInstance> i) override;
    void OnDigitalGoodsInstanceReady(
        mojo::InterfacePtr<arc::mojom::DigitalGoodsInstance> i) override;
    void OnDiskSpaceInstanceReady(
        mojo::InterfacePtr<arc::mojom::DiskSpaceInstance> i) override;
    void OnEnterpriseReportingInstanceReady(
        mojo::InterfacePtr<arc::mojom::EnterpriseReportingInstance> i) override;
    void OnErrorNotificationInstanceReady(
        mojo::InterfacePtr<arc::mojom::ErrorNotificationInstance> i) override;
    void OnFileSystemInstanceReady(
        mojo::InterfacePtr<arc::mojom::FileSystemInstance> i) override;
    void OnIioSensorInstanceReady(
        mojo::InterfacePtr<arc::mojom::IioSensorInstance> i) override;
    void OnImeInstanceReady(
        mojo::InterfacePtr<arc::mojom::ImeInstance> i) override;
    void OnInputMethodManagerInstanceReady(
        mojo::InterfacePtr<arc::mojom::InputMethodManagerInstance> i) override;
    void OnIntentHelperInstanceReady(
        mojo::InterfacePtr<arc::mojom::IntentHelperInstance> i) override;
    void OnKeymasterInstanceReady(
        mojo::InterfacePtr<arc::mojom::KeymasterInstance> i) override;
    void OnKeyMintInstanceReady(
        mojo::InterfacePtr<arc::mojom::keymint::KeyMintInstance> i) override;
    void OnKioskInstanceReady(
        mojo::InterfacePtr<arc::mojom::KioskInstance> i) override;
    void OnMediaSessionInstanceReady(
        mojo::InterfacePtr<arc::mojom::MediaSessionInstance> i) override;
    void OnMemoryInstanceReady(
        mojo::InterfacePtr<arc::mojom::MemoryInstance> i) override;
    void OnMetricsInstanceReady(
        mojo::InterfacePtr<arc::mojom::MetricsInstance> i) override;
    void OnMidisInstanceReady(
        mojo::InterfacePtr<arc::mojom::MidisInstance> i) override;
    void OnNearbyShareInstanceReady(
        mojo::InterfacePtr<arc::mojom::NearbyShareInstance> i) override;
    void OnNetInstanceReady(
        mojo::InterfacePtr<arc::mojom::NetInstance> i) override;
    void OnNotificationsInstanceReady(
        mojo::InterfacePtr<arc::mojom::NotificationsInstance> i) override;
    void OnObbMounterInstanceReady(
        mojo::InterfacePtr<arc::mojom::ObbMounterInstance> i) override;
    void OnOemCryptoInstanceReady(
        mojo::InterfacePtr<arc::mojom::OemCryptoInstance> i) override;
    void OnOnDeviceSafetyInstanceReady(
        mojo::InterfacePtr<arc::mojom::OnDeviceSafetyInstance> i) override;
    void OnPaymentAppInstanceReady(
        mojo::InterfacePtr<chromeos::payments::mojom::PaymentAppInstance> i) override;
    void OnPipInstanceReady(
        mojo::InterfacePtr<arc::mojom::PipInstance> i) override;
    void OnPolicyInstanceReady(
        mojo::InterfacePtr<arc::mojom::PolicyInstance> i) override;
    void OnPowerInstanceReady(
        mojo::InterfacePtr<arc::mojom::PowerInstance> i) override;
    void OnPrintSpoolerInstanceReady(
        mojo::InterfacePtr<arc::mojom::PrintSpoolerInstance> i) override;
    void OnPrivacyItemsInstanceReady(
        mojo::InterfacePtr<arc::mojom::PrivacyItemsInstance> i) override;
    void OnProcessInstanceReady(
        mojo::InterfacePtr<arc::mojom::ProcessInstance> i) override;
    void OnScreenCaptureInstanceReady(
        mojo::InterfacePtr<arc::mojom::ScreenCaptureInstance> i) override;
    void OnSharesheetInstanceReady(
        mojo::InterfacePtr<arc::mojom::SharesheetInstance> i) override;
    void OnSystemStateInstanceReady(
        mojo::InterfacePtr<arc::mojom::SystemStateInstance> i) override;
    void OnSystemUiInstanceReady(
        mojo::InterfacePtr<arc::mojom::SystemUiInstance> i) override;
    void OnTimerInstanceReady(
        mojo::InterfacePtr<arc::mojom::TimerInstance> i) override;
    void OnTracingInstanceReady(
        mojo::InterfacePtr<arc::mojom::TracingInstance> i) override;
    void OnTtsInstanceReady(
        mojo::InterfacePtr<arc::mojom::TtsInstance> i) override;
    void OnUsbHostInstanceReady(
        mojo::InterfacePtr<arc::mojom::UsbHostInstance> i) override;
    void OnVideoInstanceReady(
        mojo::InterfacePtr<arc::mojom::VideoInstance> i) override;
    void OnVolumeMounterInstanceReady(
        mojo::InterfacePtr<arc::mojom::VolumeMounterInstance> i) override;
    void OnWakeLockInstanceReady(
        mojo::InterfacePtr<arc::mojom::WakeLockInstance> i) override;
    void OnWallpaperInstanceReady(
        mojo::InterfacePtr<arc::mojom::WallpaperInstance> i) override;
    void OnWebApkInstanceReady(
        mojo::InterfacePtr<arc::mojom::WebApkInstance> i) override;

private:
    // Returns the root proxy for non-virtual calls.
    arc::mojom::ArcBridgeHostProxy* proxy();

    mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* root_host_;
    const uint32_t* host_version_;
    int client_pid_;
    mojo::Binding<arc::mojom::ArcBridgeHost> binding_{this};

    // Per-forwarder dedup: track which instance types this forwarder has
    // already forwarded. Prevents the same client from registering the
    // same instance type twice (which causes Chrome's ConnectionHolder
    // to tear down the existing connection as "stale").
    //
    // Types declared with FORWARD_METHOD_REREGISTRABLE are exempt: they are
    // still recorded here, but a repeat is forwarded rather than dropped, so a
    // client can deliberately re-register to recover a dead connection.
    std::set<std::string> forwarded_types_;

    // Cross-forwarder tracking: global map of instance_type → PID.
    // Used to log when different processes register the same instance type.
    static std::map<std::string, int> s_global_instance_registry_;
};

} // namespace arc

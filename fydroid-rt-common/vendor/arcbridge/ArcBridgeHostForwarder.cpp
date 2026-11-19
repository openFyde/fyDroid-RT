/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "ArcBridgeHostForwarder.h"

#include <base/bind.h>
#include <base/single_thread_task_runner.h>

namespace arc {

// Static member initialization
std::map<std::string, int> ArcBridgeHostForwarder::s_global_instance_registry_;

ArcBridgeHostForwarder::ArcBridgeHostForwarder(
    mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* root_host,
    const uint32_t* host_version,
    int client_pid)
    : root_host_(root_host), host_version_(host_version),
      client_pid_(client_pid) {
}

ArcBridgeHostForwarder::~ArcBridgeHostForwarder() {
    if (binding_.is_bound()) {
        binding_.Close();
    }
    LOG(INFO) << "ArcBridgeHostForwarder: destroyed for PID " << client_pid_;
}

void ArcBridgeHostForwarder::Bind(
    mojo::ScopedMessagePipeHandle pipe,
    scoped_refptr<base::SingleThreadTaskRunner> task_runner) {
    binding_.Bind(
        mojo::InterfaceRequest<arc::mojom::ArcBridgeHost>(std::move(pipe)),
        std::move(task_runner));
    binding_.set_connection_error_handler(base::BindOnce(
        [](ArcBridgeHostForwarder* self) {
            LOG(WARNING) << "ArcBridgeHostForwarder: connection lost for PID "
                         << self->client_pid_;
        }, this));
}

arc::mojom::ArcBridgeHostProxy* ArcBridgeHostForwarder::proxy() {
    return static_cast<arc::mojom::ArcBridgeHostProxy*>(root_host_->get());
}

// ---------------------------------------------------------------------------
// All 58 forwarding methods.
// Each calls the corresponding method non-virtually on the root proxy,
// bypassing virtual dispatch (matching the arcwlservice pattern).
// ---------------------------------------------------------------------------

#define FORWARD_METHOD(MethodName, ParamType) \
    void ArcBridgeHostForwarder::MethodName(mojo::InterfacePtr<ParamType> i) { \
        const char* kName = #MethodName; \
        if (!forwarded_types_.insert(kName).second) { \
            LOG(WARNING) << "ArcBridgeHostForwarder: dropping duplicate " \
                         << kName << " from PID " << client_pid_ \
                         << " (already forwarded by this forwarder)"; \
            return; \
        } \
        auto it = s_global_instance_registry_.find(kName); \
        if (it != s_global_instance_registry_.end() && it->second != client_pid_) { \
            LOG(WARNING) << "ArcBridgeHostForwarder: " << kName \
                         << " already registered by PID " << it->second \
                         << ", re-registering from PID " << client_pid_; \
        } \
        s_global_instance_registry_[kName] = client_pid_; \
        LOG(INFO) << "ArcBridgeHostForwarder: forwarding " \
                  << kName << " from PID " << client_pid_; \
        proxy()->arc::mojom::ArcBridgeHostProxy::MethodName(std::move(i)); \
    }

// Same as FORWARD_METHOD, but lets the same client register again.
//
// FORWARD_METHOD's dedup exists to stop an ACCIDENTAL second registration from
// making Chrome's ConnectionHolder tear down a healthy connection as stale. A
// client that re-registers DELIBERATELY, to recover a connection that is
// already dead, needs the opposite. There is nothing healthy left to protect,
// and dropping the repeat destroys the InterfacePtr the caller just bound —
// which reaches the client as an instant disconnect, indistinguishable from
// Chrome hanging up, so it can never recover. Chrome handles the replacement:
// ConnectionHolder drops the stale instance, binds a fresh host receiver and
// calls Init() on the new instance, which is exactly what recovery needs.
//
// Only for services that drive their own re-registration and back off between
// attempts — see ArcImeMojoService::scheduleReconnect().
#define FORWARD_METHOD_REREGISTRABLE(MethodName, ParamType) \
    void ArcBridgeHostForwarder::MethodName(mojo::InterfacePtr<ParamType> i) { \
        const char* kName = #MethodName; \
        /* Recorded, but a repeat is forwarded rather than dropped. */ \
        forwarded_types_.insert(kName); \
        auto it = s_global_instance_registry_.find(kName); \
        if (it != s_global_instance_registry_.end() && it->second != client_pid_) { \
            LOG(WARNING) << "ArcBridgeHostForwarder: " << kName \
                         << " already registered by PID " << it->second \
                         << ", re-registering from PID " << client_pid_; \
        } \
        s_global_instance_registry_[kName] = client_pid_; \
        LOG(INFO) << "ArcBridgeHostForwarder: forwarding " \
                  << kName << " from PID " << client_pid_; \
        proxy()->arc::mojom::ArcBridgeHostProxy::MethodName(std::move(i)); \
    }

// Same as FORWARD_METHOD, but drops the message when Chrome's ArcBridgeHost is
// older than the method. Writing an ordinal Chrome does not know onto the root
// pipe is a mojo validation error at the receiving end, and Chrome closes the
// whole ArcBridgeHost channel over it — taking every other ARC service down
// with it, not just the caller's. Needed for any method that exists on our side
// but may not yet exist on Chrome's; ArcBridgeSupport's own QueryVersion gate
// does not catch this, because the version it sees is ours, not Chrome's.
#define FORWARD_METHOD_MIN_VERSION(MethodName, ParamType, MinVersion) \
    void ArcBridgeHostForwarder::MethodName(mojo::InterfacePtr<ParamType> i) { \
        const char* kName = #MethodName; \
        if (!host_version_ || *host_version_ < (MinVersion)) { \
            LOG(WARNING) << "ArcBridgeHostForwarder: dropping " << kName \
                         << " from PID " << client_pid_ << " — Chrome's " \
                         << "ArcBridgeHost is v" \
                         << (host_version_ ? *host_version_ : 0) \
                         << ", needs v" << (MinVersion); \
            return; \
        } \
        if (!forwarded_types_.insert(kName).second) { \
            LOG(WARNING) << "ArcBridgeHostForwarder: dropping duplicate " \
                         << kName << " from PID " << client_pid_; \
            return; \
        } \
        s_global_instance_registry_[kName] = client_pid_; \
        LOG(INFO) << "ArcBridgeHostForwarder: forwarding " \
                  << kName << " from PID " << client_pid_; \
        proxy()->arc::mojom::ArcBridgeHostProxy::MethodName(std::move(i)); \
    }

FORWARD_METHOD(OnAccessibilityHelperInstanceReady, ax::android::mojom::AccessibilityHelperInstance)
FORWARD_METHOD(OnAdbdMonitorInstanceReady, arc::mojom::AdbdMonitorInstance)
FORWARD_METHOD(OnAppInstanceReady, arc::mojom::AppInstance)
FORWARD_METHOD(OnAppPermissionsInstanceReady, arc::mojom::AppPermissionsInstance)
FORWARD_METHOD(OnAppfuseInstanceReady, arc::mojom::AppfuseInstance)
FORWARD_METHOD(OnArcShellExecutionInstanceReady, arc::mojom::ArcShellExecutionInstance)
FORWARD_METHOD(OnArcWifiInstanceReady, arc::mojom::ArcWifiInstance)
FORWARD_METHOD(OnAudioInstanceReady, arc::mojom::AudioInstance)
FORWARD_METHOD(OnAuthInstanceReady, arc::mojom::AuthInstance)
FORWARD_METHOD(OnBackupSettingsInstanceReady, arc::mojom::BackupSettingsInstance)
FORWARD_METHOD(OnBluetoothInstanceReady, arc::mojom::BluetoothInstance)
FORWARD_METHOD(OnBootPhaseMonitorInstanceReady, arc::mojom::BootPhaseMonitorInstance)
FORWARD_METHOD(OnCameraInstanceReady, arc::mojom::CameraInstance)
// fydroid-rt only: the Widevine DRM HAL's CdmInstance. Chrome only grew
// OnCdmInstanceReady at ArcBridgeHost v71 — see
// device/fydeos/docs/widevine-cdm-bridge-plan.md.
FORWARD_METHOD_MIN_VERSION(
    OnCdmInstanceReady, arc::mojom::CdmInstance,
    arc::mojom::ArcBridgeHost::kOnCdmInstanceReadyMinVersion)
FORWARD_METHOD(OnChromeFeatureFlagsInstanceReady, arc::mojom::ChromeFeatureFlagsInstance)
FORWARD_METHOD(OnCompatibilityModeInstanceReady, arc::mojom::CompatibilityModeInstance)
FORWARD_METHOD(OnCrashCollectorInstanceReady, arc::mojom::CrashCollectorInstance)
FORWARD_METHOD(OnDigitalGoodsInstanceReady, arc::mojom::DigitalGoodsInstance)
FORWARD_METHOD(OnDiskSpaceInstanceReady, arc::mojom::DiskSpaceInstance)
FORWARD_METHOD(OnEnterpriseReportingInstanceReady, arc::mojom::EnterpriseReportingInstance)
FORWARD_METHOD(OnErrorNotificationInstanceReady, arc::mojom::ErrorNotificationInstance)
FORWARD_METHOD(OnFileSystemInstanceReady, arc::mojom::FileSystemInstance)
FORWARD_METHOD(OnIioSensorInstanceReady, arc::mojom::IioSensorInstance)
// Re-registrable: arc_ime_bridge re-offers its ImeInstance to get a fresh
// ImeHost after that pipe drops, which mojo does on any message it fails to
// deserialise. Without this the retry is silently dropped and the IME has no
// Chrome connection for the rest of the session.
FORWARD_METHOD_REREGISTRABLE(OnImeInstanceReady, arc::mojom::ImeInstance)
FORWARD_METHOD(OnInputMethodManagerInstanceReady, arc::mojom::InputMethodManagerInstance)
FORWARD_METHOD(OnIntentHelperInstanceReady, arc::mojom::IntentHelperInstance)
FORWARD_METHOD(OnKeymasterInstanceReady, arc::mojom::KeymasterInstance)
FORWARD_METHOD(OnKeyMintInstanceReady, arc::mojom::keymint::KeyMintInstance)
FORWARD_METHOD(OnKioskInstanceReady, arc::mojom::KioskInstance)
FORWARD_METHOD(OnMediaSessionInstanceReady, arc::mojom::MediaSessionInstance)
FORWARD_METHOD(OnMemoryInstanceReady, arc::mojom::MemoryInstance)
FORWARD_METHOD(OnMetricsInstanceReady, arc::mojom::MetricsInstance)
FORWARD_METHOD(OnMidisInstanceReady, arc::mojom::MidisInstance)
FORWARD_METHOD(OnNearbyShareInstanceReady, arc::mojom::NearbyShareInstance)
FORWARD_METHOD(OnNetInstanceReady, arc::mojom::NetInstance)
FORWARD_METHOD(OnNotificationsInstanceReady, arc::mojom::NotificationsInstance)
FORWARD_METHOD(OnObbMounterInstanceReady, arc::mojom::ObbMounterInstance)
FORWARD_METHOD(OnOemCryptoInstanceReady, arc::mojom::OemCryptoInstance)
FORWARD_METHOD(OnOnDeviceSafetyInstanceReady, arc::mojom::OnDeviceSafetyInstance)
FORWARD_METHOD(OnPaymentAppInstanceReady, chromeos::payments::mojom::PaymentAppInstance)
FORWARD_METHOD(OnPipInstanceReady, arc::mojom::PipInstance)
FORWARD_METHOD(OnPolicyInstanceReady, arc::mojom::PolicyInstance)
FORWARD_METHOD(OnPowerInstanceReady, arc::mojom::PowerInstance)
FORWARD_METHOD(OnPrintSpoolerInstanceReady, arc::mojom::PrintSpoolerInstance)
FORWARD_METHOD(OnPrivacyItemsInstanceReady, arc::mojom::PrivacyItemsInstance)
FORWARD_METHOD(OnProcessInstanceReady, arc::mojom::ProcessInstance)
FORWARD_METHOD(OnScreenCaptureInstanceReady, arc::mojom::ScreenCaptureInstance)
FORWARD_METHOD(OnSharesheetInstanceReady, arc::mojom::SharesheetInstance)
FORWARD_METHOD(OnSystemStateInstanceReady, arc::mojom::SystemStateInstance)
FORWARD_METHOD(OnSystemUiInstanceReady, arc::mojom::SystemUiInstance)
FORWARD_METHOD(OnTimerInstanceReady, arc::mojom::TimerInstance)
FORWARD_METHOD(OnTracingInstanceReady, arc::mojom::TracingInstance)
FORWARD_METHOD(OnTtsInstanceReady, arc::mojom::TtsInstance)
FORWARD_METHOD(OnUsbHostInstanceReady, arc::mojom::UsbHostInstance)
FORWARD_METHOD(OnVideoInstanceReady, arc::mojom::VideoInstance)
FORWARD_METHOD(OnVolumeMounterInstanceReady, arc::mojom::VolumeMounterInstance)
FORWARD_METHOD(OnWakeLockInstanceReady, arc::mojom::WakeLockInstance)
FORWARD_METHOD(OnWallpaperInstanceReady, arc::mojom::WallpaperInstance)
FORWARD_METHOD(OnWebApkInstanceReady, arc::mojom::WebApkInstance)

#undef FORWARD_METHOD

} // namespace arc

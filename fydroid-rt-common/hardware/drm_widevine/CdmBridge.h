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

// CdmBridge — the Android half of the fydroid Widevine bridge.
//
// Owns the ARC mojo connection to Chrome's library CDM and exposes it to the
// DRM HAL as a set of *blocking* calls, because every AIDL entry point
// (getKeyRequest, provideKeyResponse, decrypt, ...) is synchronous while mojo
// is not. Binder threads call in here and block on a future with a timeout;
// the mojo traffic itself all happens on the MojoProcessSupport thread.
//
// See device/fydeos/docs/widevine-cdm-bridge-plan.md. Contract:
// frameworks/native/libs/arcbridge/mojom/arc/cdm.mojom.
#pragma once

// The NDK binder headers drag in bionic's <syslog.h>, which defines LOG_INFO /
// LOG_WARNING as macros; libchrome's base/logging.h declares constants with
// those names. Whoever includes this header may have pulled in either first, so
// clear the macros before the libchrome headers below.
#undef LOG_INFO
#undef LOG_WARNING
#undef LOG_DEBUG

#include <chrono>
#include <condition_variable>
#include <deque>
#include <map>
#include <memory>
#include <mutex>
#include <string>
#include <vector>

#include <utils/StrongPointer.h>
#include <mojo/public/cpp/bindings/associated_binding.h>
#include <mojo/public/cpp/bindings/associated_interface_ptr.h>
#include <mojo/public/cpp/bindings/binding.h>

#include <arc/ArcService.h>
#include <arc/MojoProcessSupport.h>
#include <arc/arc_bridge.mojom.h>
#include <arc/cdm.mojom.h>

#include <aidl/android/hardware/drm/IDrmPluginListener.h>
#include <aidl/android/hardware/drm/Status.h>

namespace fydroid {
namespace widevine {

using ::aidl::android::hardware::drm::Status;

// A license message pushed up from the CDM, waiting to be picked up by the
// getKeyRequest() that asked for it (or by the one the framework makes after we
// fire onEvent(KEY_NEEDED) for a CDM-initiated renewal).
struct SessionMessage {
    ::arc::mojom::CdmMessageType type;
    std::vector<uint8_t> data;
};

class CdmBridge : public ::arc::ArcService,
                  public ::arc::mojom::CdmInstance,
                  public ::arc::mojom::CdmSessionClient {
public:
    // Leaked singleton — the mojo connection outlives every plugin.
    static CdmBridge* get();

    // Starts the mojo thread and the async ARC bridge bootstrap. Returns
    // immediately; the connection may not exist for some time (or ever, if
    // Chrome is not up yet). Safe to call once from main().
    void start();

    // True once Chrome has pushed us a CdmHost. Key operations that arrive
    // before this return ERROR_DRM_RESOURCE_BUSY rather than blocking forever.
    bool hostReady() const;

    // True once Chrome has answered CreateCdm with kNotSupported — no CDM
    // component, or an unknown key system. Lets DrmFactory refuse to build a
    // plugin so apps get UnsupportedSchemeException out of `new MediaDrm()`,
    // which is the thing they actually branch on, instead of discovering it at
    // the first getKeyRequest with playback already set up. Reset when a new
    // host arrives, so a later component install recovers without a reboot.
    bool cdmUnavailable() const;

    // --- Session id bookkeeping -------------------------------------------
    // MediaDrm session ids are opaque bytes we mint; the CDM's are strings it
    // mints. openSession() has to return one before any CDM session exists, so
    // the two are bound only when CreateSessionAndGenerateRequest returns.
    std::vector<uint8_t> openAndroidSession();
    bool hasAndroidSession(const std::vector<uint8_t>& sid) const;
    // Empty string if the session has no CDM session bound yet.
    std::string cdmSessionFor(const std::vector<uint8_t>& sid) const;
    void bindCdmSession(const std::vector<uint8_t>& sid,
                        const std::string& cdm_session_id);
    void closeAndroidSession(const std::vector<uint8_t>& sid);
    size_t numOpenSessions() const;

    // --- Blocking CDM operations (called on binder threads) ---------------
    Status createSessionAndGenerateRequest(::arc::mojom::CdmSessionType type,
                                           const std::vector<uint8_t>& init_data,
                                           std::string* out_cdm_session_id);
    Status loadSession(::arc::mojom::CdmSessionType type,
                       const std::string& cdm_session_id);
    Status updateSession(const std::string& cdm_session_id,
                         const std::vector<uint8_t>& response);
    Status closeSession(const std::string& cdm_session_id);
    Status removeSession(const std::string& cdm_session_id);
    Status setServerCertificate(const std::vector<uint8_t>& cert);

    // MediaDrm getPropertyByteArray("deviceUniqueId"). Unlike everything else
    // here this goes to |host_|, not |cdm_|, so it needs no CDM instance —
    // Chrome derives it from the per-profile CDM origin ID (see cdm.mojom).
    // BAD_VALUE if the Chrome side predates the call; ERROR_DRM_RESOURCE_BUSY
    // while the bridge or its version is still unknown.
    Status deviceUniqueId(std::vector<uint8_t>* out_id);

    Status decrypt(const std::string& cdm_session_id,
                   const std::vector<uint8_t>& encrypted,
                   ::arc::mojom::CdmDecryptConfigPtr config,
                   std::vector<uint8_t>* out_decrypted);

    // Waits for a license message on |cdm_session_id|. Messages that arrived
    // before the caller got here are buffered, so this is safe to call after
    // CreateSessionAndGenerateRequest has already returned — the CDM usually
    // emits OnSessionMessage before it replies.
    bool waitForSessionMessage(const std::string& cdm_session_id,
                               std::chrono::milliseconds timeout,
                               SessionMessage* out);
    // Non-blocking variant, for the renewal case where the framework calls
    // getKeyRequest() again after our onEvent(KEY_NEEDED).
    bool takePendingSessionMessage(const std::string& cdm_session_id,
                                   SessionMessage* out);

    // IDrmPluginListener, shared by every plugin instance (there is one CDM).
    void setListener(
            const std::shared_ptr<
                    ::aidl::android::hardware::drm::IDrmPluginListener>& l);

    // --- arc::ArcService --------------------------------------------------
    void ready(mojo::InterfacePtr<::arc::mojom::ArcBridgeHost>* host) override;
    void versionMismatch(unsigned int version) override;

    // --- arc::mojom::CdmInstance ------------------------------------------
    void Init(::arc::mojom::CdmHostPtr host_remote,
              const InitCallback& callback) override;

    // --- arc::mojom::CdmSessionClient -------------------------------------
    void OnSessionMessage(const std::string& session_id,
                          ::arc::mojom::CdmMessageType message_type,
                          const std::vector<uint8_t>& message) override;
    void OnSessionKeysChange(
            const std::string& session_id, bool has_additional_usable_key,
            std::vector<::arc::mojom::CdmKeyInfoPtr> keys_info) override;
    void OnSessionExpirationUpdate(const std::string& session_id,
                                   double new_expiry_time_sec) override;
    void OnSessionClosed(const std::string& session_id) override;

private:
    CdmBridge();
    ~CdmBridge() override;

    // Creates the Cdm on first use. Must be called from a binder thread; posts
    // to the mojo thread and blocks. Returns Status::OK when |cdm_| is usable.
    Status ensureCdm();
    void createCdmOnMojoThread();

    void postToMojo(base::OnceClosure task);
    // QueryVersion reply for |host_|. See deviceUniqueId().
    void onHostVersion(uint32_t version);
    // Reverse of bindCdmSession, for OnSessionClosed / closeSession.
    std::vector<uint8_t> androidSessionFor(const std::string& cdm_session_id) const;

    ::arc::MojoProcessSupport* mojo_support_ = nullptr;
    ::arc::ArcBridgeSupport* bridge_support_ = nullptr;

    // Mojo state. Touched only on the mojo thread except where noted.
    mojo::Binding<::arc::mojom::CdmInstance> instance_binding_{this};
    mojo::AssociatedBinding<::arc::mojom::CdmSessionClient> client_binding_{this};
    ::arc::mojom::CdmHostPtr host_;
    ::arc::mojom::CdmAssociatedPtr cdm_;

    // Guards host_ready_ / cdm_ready_ / cdm_pending_, which cross threads.
    mutable std::mutex state_mutex_;
    std::condition_variable state_cv_;
    bool host_ready_ = false;
    bool cdm_ready_ = false;
    bool cdm_pending_ = false;
    // |host_|'s interface version, once QueryVersion has answered. Calling a
    // [MinVersion] method an older Chrome does not implement is a validation
    // error that closes the pipe and takes the whole CdmHost connection with
    // it, so every such call is gated on this.
    bool host_version_known_ = false;
    uint32_t host_version_ = 0;
    bool cdm_unavailable_ = false;
    Status cdm_create_status_ = Status::ERROR_DRM_RESOURCE_BUSY;

    // Session bookkeeping + buffered messages.
    mutable std::mutex session_mutex_;
    std::condition_variable session_cv_;
    std::map<std::vector<uint8_t>, std::string> android_to_cdm_;
    std::map<std::string, std::vector<uint8_t>> cdm_to_android_;
    std::map<std::string, std::deque<SessionMessage>> pending_messages_;

    mutable std::mutex listener_mutex_;
    std::shared_ptr<::aidl::android::hardware::drm::IDrmPluginListener> listener_;
};

}  // namespace widevine
}  // namespace fydroid

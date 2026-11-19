#define LOG_TAG "widevine-CdmBridge"

#include "CdmBridge.h"

#include <base/bind.h>
#include <base/logging.h>
#include <base/rand_util.h>
#include <base/single_thread_task_runner.h>

#include <arc/ArcBridgeSupport.h>
#include <utils/Log.h>

#include <aidl/android/hardware/drm/EventType.h>
#include <aidl/android/hardware/drm/KeyStatus.h>
#include <aidl/android/hardware/drm/KeyStatusType.h>

namespace fydroid {
namespace widevine {

namespace {

using ::aidl::android::hardware::drm::EventType;
using ::aidl::android::hardware::drm::KeyStatus;
using ::aidl::android::hardware::drm::KeyStatusType;

// How long a binder thread will sit on a mojo round trip before giving up.
// Everything here is local IPC plus software crypto; the license server round
// trip happens in the app, not in this process. A hung Chrome must not hold a
// binder thread forever.
constexpr auto kControlTimeout = std::chrono::seconds(5);
constexpr auto kDecryptTimeout = std::chrono::seconds(2);
constexpr auto kCreateCdmTimeout = std::chrono::seconds(10);

// One-shot rendezvous between a binder thread and a mojo reply.
template <typename T>
class Waiter {
public:
    void set(T value) {
        {
            std::lock_guard<std::mutex> lock(mutex_);
            if (done_) return;
            value_ = std::move(value);
            done_ = true;
        }
        cv_.notify_all();
    }

    bool wait(std::chrono::milliseconds timeout, T* out) {
        std::unique_lock<std::mutex> lock(mutex_);
        if (!cv_.wait_for(lock, timeout, [this] { return done_; })) return false;
        *out = std::move(value_);
        return true;
    }

private:
    std::mutex mutex_;
    std::condition_variable cv_;
    bool done_ = false;
    T value_{};
};

struct SessionReply {
    ::arc::mojom::CdmStatus status = ::arc::mojom::CdmStatus::kFailed;
    std::string session_id;
};

struct DecryptReply {
    ::arc::mojom::CdmStatus status = ::arc::mojom::CdmStatus::kFailed;
    std::vector<uint8_t> data;
};

using StatusWaiter = Waiter<::arc::mojom::CdmStatus>;
using SessionWaiter = Waiter<SessionReply>;
using DecryptWaiter = Waiter<DecryptReply>;

void OnStatusReply(std::shared_ptr<StatusWaiter> waiter,
                   ::arc::mojom::CdmStatus status) {
    waiter->set(status);
}

// closeSession does not wait, but mojo still needs somewhere to deliver the
// reply for a method that declares one.
void OnCloseSessionIgnored(::arc::mojom::CdmStatus status) {
    if (status != ::arc::mojom::CdmStatus::kSuccess) {
        ALOGW("CdmBridge: CloseSession returned %d", static_cast<int>(status));
    }
}

void OnSessionReply(std::shared_ptr<SessionWaiter> waiter,
                    ::arc::mojom::CdmStatus status,
                    const std::string& session_id) {
    waiter->set(SessionReply{status, session_id});
}

void OnDecryptReply(std::shared_ptr<DecryptWaiter> waiter,
                    ::arc::mojom::CdmStatus status,
                    const std::vector<uint8_t>& data) {
    waiter->set(DecryptReply{status, data});
}

Status toDrmStatus(::arc::mojom::CdmStatus status) {
    switch (status) {
        case ::arc::mojom::CdmStatus::kSuccess:
            return Status::OK;
        case ::arc::mojom::CdmStatus::kNotSupported:
            return Status::ERROR_DRM_CANNOT_HANDLE;
        case ::arc::mojom::CdmStatus::kInvalidState:
            return Status::ERROR_DRM_INVALID_STATE;
        case ::arc::mojom::CdmStatus::kInvalidArgument:
            return Status::BAD_VALUE;
        case ::arc::mojom::CdmStatus::kQuotaExceeded:
            return Status::ERROR_DRM_RESOURCE_BUSY;
        case ::arc::mojom::CdmStatus::kNeedsProvisioning:
            // Chrome's CDM provisions itself against its own storage, so this
            // should never surface. If it does, the Chrome side's CdmStorage
            // is not wired up.
            return Status::ERROR_DRM_NOT_PROVISIONED;
        case ::arc::mojom::CdmStatus::kSessionNotFound:
            return Status::ERROR_DRM_SESSION_NOT_OPENED;
        case ::arc::mojom::CdmStatus::kDecryptError:
            return Status::ERROR_DRM_DECRYPT;
        case ::arc::mojom::CdmStatus::kFailed:
        default:
            return Status::ERROR_DRM_UNKNOWN;
    }
}

KeyStatusType toKeyStatusType(::arc::mojom::CdmKeyStatus status) {
    switch (status) {
        case ::arc::mojom::CdmKeyStatus::kUsable:
            return KeyStatusType::USABLE;
        case ::arc::mojom::CdmKeyStatus::kExpired:
            return KeyStatusType::EXPIRED;
        case ::arc::mojom::CdmKeyStatus::kOutputRestricted:
        case ::arc::mojom::CdmKeyStatus::kOutputDownscaled:
            return KeyStatusType::OUTPUT_NOT_ALLOWED;
        case ::arc::mojom::CdmKeyStatus::kStatusPending:
            return KeyStatusType::STATUS_PENDING;
        case ::arc::mojom::CdmKeyStatus::kReleased:
            // No AOSP equivalent; a released key is not usable.
            return KeyStatusType::EXPIRED;
        case ::arc::mojom::CdmKeyStatus::kInternalError:
        default:
            return KeyStatusType::INTERNAL_ERROR;
    }
}

}  // namespace

// ---------------------------------------------------------------------------
// Lifetime
// ---------------------------------------------------------------------------

CdmBridge* CdmBridge::get() {
    // Leaked on purpose: the mojo connection must outlive every plugin, and
    // ArcBridgeSupport holds a wp<ArcService> back to us.
    static android::sp<CdmBridge> instance = new CdmBridge();
    return instance.get();
}

CdmBridge::CdmBridge() = default;
CdmBridge::~CdmBridge() = default;

void CdmBridge::start() {
    mojo_support_ = ::arc::MojoProcessSupport::GetInstance();
    bridge_support_ = new ::arc::ArcBridgeSupport(mojo_support_);
    bridge_support_->requestArcBridgeProxyAsync(
            android::wp<::arc::ArcService>(this),
            ::arc::mojom::ArcBridgeHost::kOnCdmInstanceReadyMinVersion);
    ALOGI("CdmBridge: ARC bridge bootstrap requested");
}

void CdmBridge::postToMojo(base::OnceClosure task) {
    if (!mojo_support_ || !mojo_support_->getThread()) {
        ALOGE("CdmBridge: mojo thread not available");
        return;
    }
    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
                                                          std::move(task));
}

bool CdmBridge::hostReady() const {
    std::lock_guard<std::mutex> lock(state_mutex_);
    return host_ready_;
}

bool CdmBridge::cdmUnavailable() const {
    std::lock_guard<std::mutex> lock(state_mutex_);
    return cdm_unavailable_;
}

// ---------------------------------------------------------------------------
// arc::ArcService
// ---------------------------------------------------------------------------

void CdmBridge::ready(mojo::InterfacePtr<::arc::mojom::ArcBridgeHost>* host) {
    ALOGI("CdmBridge: ARC bridge ready, registering CdmInstance");

    mojo::InterfacePtr<::arc::mojom::CdmInstance> instance_ptr;
    instance_binding_.Bind(mojo::MakeRequest(&instance_ptr));
    instance_binding_.set_connection_error_handler(
            base::BindOnce([](CdmBridge* self) {
                ALOGW("CdmBridge: CdmInstance binding lost");
                self->host_.reset();
                self->cdm_.reset();
                {
                    std::lock_guard<std::mutex> lock(self->state_mutex_);
                    self->host_ready_ = false;
                    self->cdm_ready_ = false;
                    self->cdm_pending_ = false;
                }
                self->state_cv_.notify_all();
            }, this));

    (*host)->OnCdmInstanceReady(std::move(instance_ptr));
}

void CdmBridge::versionMismatch(unsigned int version) {
    ALOGE("CdmBridge: Chrome's ArcBridgeHost is v%u, OnCdmInstanceReady needs "
          "v%d — no Widevine.",
          version,
          ::arc::mojom::ArcBridgeHost::kOnCdmInstanceReadyMinVersion);
}

// ---------------------------------------------------------------------------
// arc::mojom::CdmInstance
// ---------------------------------------------------------------------------

void CdmBridge::Init(::arc::mojom::CdmHostPtr host_remote,
                     const InitCallback& callback) {
    ALOGI("CdmBridge: CdmHost connected");
    host_ = std::move(host_remote);
    host_.set_connection_error_handler(base::BindOnce([](CdmBridge* self) {
        ALOGW("CdmBridge: CdmHost connection lost");
        self->cdm_.reset();
        {
            std::lock_guard<std::mutex> lock(self->state_mutex_);
            self->host_ready_ = false;
            self->cdm_ready_ = false;
            self->cdm_pending_ = false;
        }
        self->state_cv_.notify_all();
    }, this));

    {
        std::lock_guard<std::mutex> lock(state_mutex_);
        host_ready_ = true;
        // A fresh host is a fresh verdict: a CDM that was missing may have been
        // installed by the component updater since, and we must not keep
        // refusing MediaDrm for the life of the boot because of an old answer.
        cdm_unavailable_ = false;
        host_version_known_ = false;
        host_version_ = 0;
    }
    // Ask once, here, rather than on the first deviceUniqueId(): the reply has
    // to be in before an app queries, and an app queries early (Netflix reads
    // deviceUniqueId during startup).
    host_.QueryVersion(
            base::BindRepeating(&CdmBridge::onHostVersion, base::Unretained(this)));
    state_cv_.notify_all();
    callback.Run();
}

void CdmBridge::onHostVersion(uint32_t version) {
    ALOGI("CdmBridge: CdmHost interface version %u", version);
    {
        std::lock_guard<std::mutex> lock(state_mutex_);
        host_version_ = version;
        host_version_known_ = true;
    }
    state_cv_.notify_all();
}

// ---------------------------------------------------------------------------
// CDM creation (lazy — do not load Chrome's CDM until an app wants Widevine)
// ---------------------------------------------------------------------------

void CdmBridge::createCdmOnMojoThread() {
    if (!host_) {
        std::lock_guard<std::mutex> lock(state_mutex_);
        cdm_pending_ = false;
        cdm_create_status_ = Status::ERROR_DRM_RESOURCE_BUSY;
        state_cv_.notify_all();
        return;
    }

    ::arc::mojom::CdmSessionClientAssociatedPtrInfo client_info;
    auto client_request = mojo::MakeRequest(&client_info);
    if (client_binding_.is_bound()) client_binding_.Close();
    client_binding_.Bind(std::move(client_request));

    auto cdm_request = mojo::MakeRequest(&cdm_);

    host_->CreateCdm(
            "com.widevine.alpha", std::move(client_info), std::move(cdm_request),
            base::BindRepeating([](CdmBridge* self,
                                   ::arc::mojom::CdmStatus status) {
                ALOGI("CdmBridge: CreateCdm returned %d",
                      static_cast<int>(status));
                {
                    std::lock_guard<std::mutex> lock(self->state_mutex_);
                    self->cdm_pending_ = false;
                    self->cdm_create_status_ = toDrmStatus(status);
                    self->cdm_ready_ =
                            (status == ::arc::mojom::CdmStatus::kSuccess);
                    // kNotSupported is Chrome's definitive "there is no CDM
                    // here" (component missing, or unknown key system) rather
                    // than a transient failure. Remember it so MediaDrm
                    // construction can fail fast instead of every app finding
                    // out at its first getKeyRequest. Cleared on reconnect.
                    self->cdm_unavailable_ =
                            (status == ::arc::mojom::CdmStatus::kNotSupported);
                }
                if (status != ::arc::mojom::CdmStatus::kSuccess) {
                    self->cdm_.reset();
                    if (self->client_binding_.is_bound())
                        self->client_binding_.Close();
                }
                self->state_cv_.notify_all();
            }, this));
}

Status CdmBridge::ensureCdm() {
    std::unique_lock<std::mutex> lock(state_mutex_);
    if (cdm_ready_) return Status::OK;
    if (!host_ready_) return Status::ERROR_DRM_RESOURCE_BUSY;

    if (!cdm_pending_) {
        cdm_pending_ = true;
        cdm_create_status_ = Status::ERROR_DRM_RESOURCE_BUSY;
        lock.unlock();
        postToMojo(base::BindOnce(&CdmBridge::createCdmOnMojoThread,
                                  base::Unretained(this)));
        lock.lock();
    }

    if (!state_cv_.wait_for(lock, kCreateCdmTimeout,
                            [this] { return !cdm_pending_; })) {
        // Clear the flag ourselves. Only the reply callback does it otherwise,
        // so leaving it set after a Chrome that never answers would make every
        // later key operation skip the post and block for the full timeout
        // again — turning one hiccup into a permanently dead CDM.
        ALOGE("CdmBridge: CreateCdm timed out; will retry on the next request");
        cdm_pending_ = false;
        return Status::ERROR_DRM_RESOURCE_BUSY;
    }
    return cdm_ready_ ? Status::OK : cdm_create_status_;
}

// ---------------------------------------------------------------------------
// Session id bookkeeping
// ---------------------------------------------------------------------------

std::vector<uint8_t> CdmBridge::openAndroidSession() {
    std::vector<uint8_t> sid(16);
    base::RandBytes(sid.data(), sid.size());

    std::lock_guard<std::mutex> lock(session_mutex_);
    // Vanishingly unlikely, but a duplicate id would silently alias sessions.
    while (android_to_cdm_.count(sid)) {
        base::RandBytes(sid.data(), sid.size());
    }
    android_to_cdm_[sid] = std::string();
    return sid;
}

bool CdmBridge::hasAndroidSession(const std::vector<uint8_t>& sid) const {
    std::lock_guard<std::mutex> lock(session_mutex_);
    return android_to_cdm_.count(sid) > 0;
}

std::string CdmBridge::cdmSessionFor(const std::vector<uint8_t>& sid) const {
    std::lock_guard<std::mutex> lock(session_mutex_);
    auto it = android_to_cdm_.find(sid);
    return it == android_to_cdm_.end() ? std::string() : it->second;
}

std::vector<uint8_t> CdmBridge::androidSessionFor(
        const std::string& cdm_session_id) const {
    std::lock_guard<std::mutex> lock(session_mutex_);
    auto it = cdm_to_android_.find(cdm_session_id);
    return it == cdm_to_android_.end() ? std::vector<uint8_t>() : it->second;
}

void CdmBridge::bindCdmSession(const std::vector<uint8_t>& sid,
                               const std::string& cdm_session_id) {
    std::lock_guard<std::mutex> lock(session_mutex_);
    auto it = android_to_cdm_.find(sid);
    if (it != android_to_cdm_.end() && !it->second.empty()) {
        cdm_to_android_.erase(it->second);
    }
    android_to_cdm_[sid] = cdm_session_id;
    cdm_to_android_[cdm_session_id] = sid;
}

void CdmBridge::closeAndroidSession(const std::vector<uint8_t>& sid) {
    std::lock_guard<std::mutex> lock(session_mutex_);
    auto it = android_to_cdm_.find(sid);
    if (it == android_to_cdm_.end()) return;
    if (!it->second.empty()) {
        cdm_to_android_.erase(it->second);
        pending_messages_.erase(it->second);
    }
    android_to_cdm_.erase(it);
}

size_t CdmBridge::numOpenSessions() const {
    std::lock_guard<std::mutex> lock(session_mutex_);
    return android_to_cdm_.size();
}

// ---------------------------------------------------------------------------
// Blocking CDM operations
// ---------------------------------------------------------------------------

Status CdmBridge::createSessionAndGenerateRequest(
        ::arc::mojom::CdmSessionType type,
        const std::vector<uint8_t>& init_data,
        std::string* out_cdm_session_id) {
    Status ready = ensureCdm();
    if (ready != Status::OK) return ready;

    auto waiter = std::make_shared<SessionWaiter>();
    postToMojo(base::BindOnce(
            [](CdmBridge* self, ::arc::mojom::CdmSessionType type,
               std::vector<uint8_t> init_data,
               std::shared_ptr<SessionWaiter> waiter) {
                if (!self->cdm_) {
                    waiter->set(SessionReply{
                            ::arc::mojom::CdmStatus::kInvalidState, ""});
                    return;
                }
                self->cdm_->CreateSessionAndGenerateRequest(
                        type, init_data,
                        base::BindRepeating(&OnSessionReply, waiter));
            },
            base::Unretained(this), type, init_data, waiter));

    SessionReply reply;
    if (!waiter->wait(kControlTimeout, &reply)) {
        ALOGE("CdmBridge: CreateSessionAndGenerateRequest timed out");
        return Status::ERROR_DRM_UNKNOWN;
    }
    if (reply.status != ::arc::mojom::CdmStatus::kSuccess) {
        return toDrmStatus(reply.status);
    }
    if (reply.session_id.empty()) {
        ALOGE("CdmBridge: CDM returned an empty session id");
        return Status::ERROR_DRM_UNKNOWN;
    }
    *out_cdm_session_id = reply.session_id;
    return Status::OK;
}

Status CdmBridge::loadSession(::arc::mojom::CdmSessionType type,
                              const std::string& cdm_session_id) {
    Status ready = ensureCdm();
    if (ready != Status::OK) return ready;

    auto waiter = std::make_shared<StatusWaiter>();
    postToMojo(base::BindOnce(
            [](CdmBridge* self, ::arc::mojom::CdmSessionType type,
               std::string id, std::shared_ptr<StatusWaiter> waiter) {
                if (!self->cdm_) {
                    waiter->set(::arc::mojom::CdmStatus::kInvalidState);
                    return;
                }
                self->cdm_->LoadSession(
                        type, id, base::BindRepeating(&OnStatusReply, waiter));
            },
            base::Unretained(this), type, cdm_session_id, waiter));

    ::arc::mojom::CdmStatus status;
    if (!waiter->wait(kControlTimeout, &status)) {
        ALOGE("CdmBridge: LoadSession timed out");
        return Status::ERROR_DRM_UNKNOWN;
    }
    return toDrmStatus(status);
}

Status CdmBridge::updateSession(const std::string& cdm_session_id,
                                const std::vector<uint8_t>& response) {
    Status ready = ensureCdm();
    if (ready != Status::OK) return ready;

    auto waiter = std::make_shared<StatusWaiter>();
    postToMojo(base::BindOnce(
            [](CdmBridge* self, std::string id, std::vector<uint8_t> response,
               std::shared_ptr<StatusWaiter> waiter) {
                if (!self->cdm_) {
                    waiter->set(::arc::mojom::CdmStatus::kInvalidState);
                    return;
                }
                self->cdm_->UpdateSession(
                        id, response,
                        base::BindRepeating(&OnStatusReply, waiter));
            },
            base::Unretained(this), cdm_session_id, response, waiter));

    ::arc::mojom::CdmStatus status;
    if (!waiter->wait(kControlTimeout, &status)) {
        ALOGE("CdmBridge: UpdateSession timed out");
        return Status::ERROR_DRM_UNKNOWN;
    }
    return toDrmStatus(status);
}

Status CdmBridge::closeSession(const std::string& cdm_session_id) {
    // Fire and forget. Nothing downstream needs the result — closeSession()
    // returns void to the app either way, and a close that fails leaves the CDM
    // to reap the session itself. Waiting only offers a way to lose: it cost a
    // binder thread the full 5s timeout on every close while Chrome's
    // Cdm::CloseSession never invoked its reply callback.
    {
        std::lock_guard<std::mutex> lock(state_mutex_);
        if (!cdm_ready_) return Status::OK;
    }

    postToMojo(base::BindOnce(
            [](CdmBridge* self, std::string id) {
                if (!self->cdm_) return;
                self->cdm_->CloseSession(
                        id, base::BindRepeating(&OnCloseSessionIgnored));
            },
            base::Unretained(this), cdm_session_id));
    return Status::OK;
}

Status CdmBridge::removeSession(const std::string& cdm_session_id) {
    Status ready = ensureCdm();
    if (ready != Status::OK) return ready;

    auto waiter = std::make_shared<StatusWaiter>();
    postToMojo(base::BindOnce(
            [](CdmBridge* self, std::string id,
               std::shared_ptr<StatusWaiter> waiter) {
                if (!self->cdm_) {
                    waiter->set(::arc::mojom::CdmStatus::kInvalidState);
                    return;
                }
                self->cdm_->RemoveSession(
                        id, base::BindRepeating(&OnStatusReply, waiter));
            },
            base::Unretained(this), cdm_session_id, waiter));

    ::arc::mojom::CdmStatus status;
    if (!waiter->wait(kControlTimeout, &status)) {
        ALOGE("CdmBridge: RemoveSession timed out");
        return Status::ERROR_DRM_UNKNOWN;
    }
    return toDrmStatus(status);
}

Status CdmBridge::setServerCertificate(const std::vector<uint8_t>& cert) {
    Status ready = ensureCdm();
    if (ready != Status::OK) return ready;

    auto waiter = std::make_shared<StatusWaiter>();
    postToMojo(base::BindOnce(
            [](CdmBridge* self, std::vector<uint8_t> cert,
               std::shared_ptr<StatusWaiter> waiter) {
                if (!self->cdm_) {
                    waiter->set(::arc::mojom::CdmStatus::kInvalidState);
                    return;
                }
                self->cdm_->SetServerCertificate(
                        cert, base::BindRepeating(&OnStatusReply, waiter));
            },
            base::Unretained(this), cert, waiter));

    ::arc::mojom::CdmStatus status;
    if (!waiter->wait(kControlTimeout, &status)) {
        ALOGE("CdmBridge: SetServerCertificate timed out");
        return Status::ERROR_DRM_UNKNOWN;
    }
    return toDrmStatus(status);
}

Status CdmBridge::deviceUniqueId(std::vector<uint8_t>* out_id) {
    {
        std::lock_guard<std::mutex> lock(state_mutex_);
        if (!host_ready_ || !host_version_known_) {
            ALOGW("deviceUniqueId: ARC bridge not ready yet");
            return Status::ERROR_DRM_RESOURCE_BUSY;
        }
        if (host_version_ <
            ::arc::mojom::CdmHost::kGetDeviceUniqueIdMinVersion) {
            ALOGW("deviceUniqueId: Chrome side is version %u, needs %u",
                  host_version_,
                  ::arc::mojom::CdmHost::kGetDeviceUniqueIdMinVersion);
            return Status::BAD_VALUE;
        }
    }

    // GetDeviceUniqueId replies (status, bytes) — the same shape as Decrypt, so
    // it reuses that waiter rather than adding a second identical one.
    auto waiter = std::make_shared<DecryptWaiter>();
    postToMojo(base::BindOnce(
            [](CdmBridge* self, std::shared_ptr<DecryptWaiter> waiter) {
                if (!self->host_) {
                    waiter->set(DecryptReply{
                            ::arc::mojom::CdmStatus::kInvalidState, {}});
                    return;
                }
                self->host_->GetDeviceUniqueId(
                        base::BindRepeating(&OnDecryptReply, waiter));
            },
            base::Unretained(this), waiter));

    DecryptReply reply;
    if (!waiter->wait(kControlTimeout, &reply)) {
        ALOGE("CdmBridge: GetDeviceUniqueId timed out");
        return Status::ERROR_DRM_UNKNOWN;
    }
    if (reply.status != ::arc::mojom::CdmStatus::kSuccess) {
        return toDrmStatus(reply.status);
    }
    if (reply.data.empty()) {
        ALOGE("CdmBridge: GetDeviceUniqueId returned no bytes");
        return Status::BAD_VALUE;
    }
    *out_id = std::move(reply.data);
    return Status::OK;
}

Status CdmBridge::decrypt(const std::string& cdm_session_id,
                          const std::vector<uint8_t>& encrypted,
                          ::arc::mojom::CdmDecryptConfigPtr config,
                          std::vector<uint8_t>* out_decrypted) {
    {
        // Decrypt is on the sample hot path — never pay for CDM creation here.
        std::lock_guard<std::mutex> lock(state_mutex_);
        if (!cdm_ready_) return Status::ERROR_DRM_NO_LICENSE;
    }

    auto waiter = std::make_shared<DecryptWaiter>();
    postToMojo(base::BindOnce(
            [](CdmBridge* self, std::string id, std::vector<uint8_t> data,
               ::arc::mojom::CdmDecryptConfigPtr config,
               std::shared_ptr<DecryptWaiter> waiter) {
                if (!self->cdm_) {
                    waiter->set(DecryptReply{
                            ::arc::mojom::CdmStatus::kInvalidState, {}});
                    return;
                }
                self->cdm_->Decrypt(
                        id, data, std::move(config),
                        base::BindRepeating(&OnDecryptReply, waiter));
            },
            base::Unretained(this), cdm_session_id, encrypted,
            base::Passed(std::move(config)), waiter));

    DecryptReply reply;
    if (!waiter->wait(kDecryptTimeout, &reply)) {
        ALOGE("CdmBridge: Decrypt timed out");
        return Status::ERROR_DRM_DECRYPT;
    }
    if (reply.status != ::arc::mojom::CdmStatus::kSuccess) {
        return toDrmStatus(reply.status);
    }
    *out_decrypted = std::move(reply.data);
    return Status::OK;
}

// ---------------------------------------------------------------------------
// Session messages
// ---------------------------------------------------------------------------

bool CdmBridge::waitForSessionMessage(const std::string& cdm_session_id,
                                      std::chrono::milliseconds timeout,
                                      SessionMessage* out) {
    std::unique_lock<std::mutex> lock(session_mutex_);
    auto has_message = [this, &cdm_session_id] {
        auto it = pending_messages_.find(cdm_session_id);
        return it != pending_messages_.end() && !it->second.empty();
    };
    if (!session_cv_.wait_for(lock, timeout, has_message)) return false;
    auto& queue = pending_messages_[cdm_session_id];
    *out = std::move(queue.front());
    queue.pop_front();
    return true;
}

bool CdmBridge::takePendingSessionMessage(const std::string& cdm_session_id,
                                          SessionMessage* out) {
    return waitForSessionMessage(cdm_session_id, std::chrono::milliseconds(0),
                                 out);
}

void CdmBridge::setListener(
        const std::shared_ptr<
                ::aidl::android::hardware::drm::IDrmPluginListener>& l) {
    std::lock_guard<std::mutex> lock(listener_mutex_);
    listener_ = l;
}

// ---------------------------------------------------------------------------
// arc::mojom::CdmSessionClient — all on the mojo thread
// ---------------------------------------------------------------------------

void CdmBridge::OnSessionMessage(const std::string& session_id,
                                 ::arc::mojom::CdmMessageType message_type,
                                 const std::vector<uint8_t>& message) {
    {
        std::lock_guard<std::mutex> lock(session_mutex_);
        pending_messages_[session_id].push_back(
                SessionMessage{message_type, message});
    }
    session_cv_.notify_all();

    // A renewal or release the CDM raised on its own has no getKeyRequest()
    // waiting for it. Poke the framework so it comes and asks; the buffered
    // message is what it will get.
    if (message_type == ::arc::mojom::CdmMessageType::kLicenseRequest) return;

    std::vector<uint8_t> android_sid = androidSessionFor(session_id);
    if (android_sid.empty()) return;

    std::shared_ptr<::aidl::android::hardware::drm::IDrmPluginListener> listener;
    {
        std::lock_guard<std::mutex> lock(listener_mutex_);
        listener = listener_;
    }
    if (listener) {
        listener->onEvent(EventType::KEY_NEEDED, android_sid,
                          std::vector<uint8_t>());
    }
}

void CdmBridge::OnSessionKeysChange(
        const std::string& session_id, bool has_additional_usable_key,
        std::vector<::arc::mojom::CdmKeyInfoPtr> keys_info) {
    std::vector<uint8_t> android_sid = androidSessionFor(session_id);
    if (android_sid.empty()) return;

    std::shared_ptr<::aidl::android::hardware::drm::IDrmPluginListener> listener;
    {
        std::lock_guard<std::mutex> lock(listener_mutex_);
        listener = listener_;
    }
    if (!listener) return;

    std::vector<KeyStatus> statuses;
    statuses.reserve(keys_info.size());
    for (const auto& info : keys_info) {
        KeyStatus ks;
        ks.keyId = info->key_id;
        ks.type = toKeyStatusType(info->status);
        statuses.push_back(std::move(ks));
    }
    listener->onKeysChange(android_sid, statuses, has_additional_usable_key);
}

void CdmBridge::OnSessionExpirationUpdate(const std::string& session_id,
                                          double new_expiry_time_sec) {
    std::vector<uint8_t> android_sid = androidSessionFor(session_id);
    if (android_sid.empty()) return;

    std::shared_ptr<::aidl::android::hardware::drm::IDrmPluginListener> listener;
    {
        std::lock_guard<std::mutex> lock(listener_mutex_);
        listener = listener_;
    }
    if (!listener) return;

    // The contract is seconds since the epoch with 0 meaning "never"; the
    // framework wants milliseconds, and passes 0 through as "never" too.
    int64_t expiry_ms =
            new_expiry_time_sec <= 0
                    ? 0
                    : static_cast<int64_t>(new_expiry_time_sec * 1000.0);
    listener->onExpirationUpdate(android_sid, expiry_ms);
}

void CdmBridge::OnSessionClosed(const std::string& session_id) {
    std::vector<uint8_t> android_sid = androidSessionFor(session_id);
    {
        std::lock_guard<std::mutex> lock(session_mutex_);
        pending_messages_.erase(session_id);
    }
    session_cv_.notify_all();
    if (android_sid.empty()) return;

    std::shared_ptr<::aidl::android::hardware::drm::IDrmPluginListener> listener;
    {
        std::lock_guard<std::mutex> lock(listener_mutex_);
        listener = listener_;
    }
    if (listener) listener->onSessionLostState(android_sid);
}

}  // namespace widevine
}  // namespace fydroid

#define LOG_TAG "widevine-DrmPlugin"

#include "DrmPlugin.h"

#include <android-base/properties.h>
#include <utils/Log.h>

#include <chrono>

#include "WidevineUtils.h"

namespace {

// persist.fydroid.drm_log: trace every MediaDrm property query and its answer.
// Off by default — an app interrogates a dozen properties at startup, and the
// answers carry device identity (systemId, the deviceUniqueId length). Turn it
// on when an MSL app such as Netflix gives up before opening a session, since
// "which property did we refuse" is the first question worth asking:
//
//   setprop persist.fydroid.drm_log 1   (then restart the HAL or the app)
bool drmLogEnabled() {
    static const bool enabled =
            ::android::base::GetBoolProperty("persist.fydroid.drm_log", false);
    return enabled;
}

}  // namespace

namespace fydroid {
namespace widevine {

namespace {

// The CDM generates a license request locally; this is only a guard against a
// wedged Chrome, not a network timeout.
constexpr auto kMessageTimeout = std::chrono::seconds(5);

::arc::mojom::CdmSessionType toCdmSessionType(drm::KeyType type) {
    // OFFLINE keys must survive a reboot, which is exactly EME's
    // persistent-license session. Everything else is temporary.
    return type == drm::KeyType::OFFLINE
                   ? ::arc::mojom::CdmSessionType::kPersistentLicense
                   : ::arc::mojom::CdmSessionType::kTemporary;
}

drm::KeyRequestType toKeyRequestType(::arc::mojom::CdmMessageType type) {
    switch (type) {
        case ::arc::mojom::CdmMessageType::kLicenseRenewal:
            return drm::KeyRequestType::RENEWAL;
        case ::arc::mojom::CdmMessageType::kLicenseRelease:
            return drm::KeyRequestType::RELEASE;
        case ::arc::mojom::CdmMessageType::kLicenseRequest:
        case ::arc::mojom::CdmMessageType::kIndividualizationRequest:
        default:
            return drm::KeyRequestType::INITIAL;
    }
}

}  // namespace

// ---------------------------------------------------------------------------
// Sessions
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::openSession(drm::SecurityLevel in_securityLevel,
                                            std::vector<uint8_t>* _aidl_return) {
    if (in_securityLevel == drm::SecurityLevel::HW_SECURE_CRYPTO ||
        in_securityLevel == drm::SecurityLevel::HW_SECURE_DECODE ||
        in_securityLevel == drm::SecurityLevel::HW_SECURE_ALL) {
        // Sample data crosses the mojo bridge in the clear; there is no secure
        // buffer path. Refusing here is the honest answer.
        ALOGW("openSession: refusing hardware security level %d",
              static_cast<int>(in_securityLevel));
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "fydroid Widevine is L3 (software) only");
    }

    // No CDM session yet — the CDM mints its own id on the first
    // getKeyRequest(), and we bind the two then.
    *_aidl_return = bridge_->openAndroidSession();
    ALOGI("openSession: %zu open", bridge_->numOpenSessions());
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::closeSession(
        const std::vector<uint8_t>& in_sessionId) {
    if (!bridge_->hasAndroidSession(in_sessionId)) {
        return toNdkStatus(drm::Status::ERROR_DRM_SESSION_NOT_OPENED);
    }
    const std::string cdm_session = bridge_->cdmSessionFor(in_sessionId);
    if (!cdm_session.empty()) bridge_->closeSession(cdm_session);
    bridge_->closeAndroidSession(in_sessionId);
    return ::ndk::ScopedAStatus::ok();
}

// ---------------------------------------------------------------------------
// Key exchange
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::getKeyRequest(
        const std::vector<uint8_t>& in_scope,
        const std::vector<uint8_t>& in_initData, const std::string& in_mimeType,
        drm::KeyType in_keyType,
        const std::vector<drm::KeyValue>& in_optionalParameters,
        drm::KeyRequest* _aidl_return) {
    (void)in_mimeType;
    (void)in_optionalParameters;

    if (!bridge_->hostReady()) {
        // The HAL starts before the ARC bridge; do not block startup on it.
        ALOGW("getKeyRequest: ARC bridge not connected yet");
        return toNdkStatus(drm::Status::ERROR_DRM_RESOURCE_BUSY,
                           "ARC CDM bridge not connected");
    }

    std::string cdm_session;
    drm::KeyRequestType fallback_type = drm::KeyRequestType::INITIAL;

    if (in_keyType == drm::KeyType::RELEASE) {
        // For RELEASE, |scope| is a keySetId, which for us is the CDM session
        // id. Removing the session makes the CDM emit the release message.
        cdm_session = toString(in_scope);
        if (cdm_session.empty()) {
            return toNdkStatus(drm::Status::BAD_VALUE, "empty keySetId");
        }
        drm::Status status = bridge_->removeSession(cdm_session);
        if (status != drm::Status::OK) return toNdkStatus(status);
        fallback_type = drm::KeyRequestType::RELEASE;
    } else {
        if (!bridge_->hasAndroidSession(in_scope)) {
            return toNdkStatus(drm::Status::ERROR_DRM_SESSION_NOT_OPENED);
        }
        cdm_session = bridge_->cdmSessionFor(in_scope);

        // A CDM-initiated renewal is already sitting in the queue: this call is
        // the framework responding to our onEvent(KEY_NEEDED), so hand it over
        // instead of starting a second session.
        if (!cdm_session.empty()) {
            SessionMessage pending;
            if (bridge_->takePendingSessionMessage(cdm_session, &pending)) {
                _aidl_return->request = std::move(pending.data);
                _aidl_return->requestType = toKeyRequestType(pending.type);
                _aidl_return->defaultUrl = "";
                return ::ndk::ScopedAStatus::ok();
            }
            ALOGW("getKeyRequest: session already bound to CDM session '%s' "
                  "with no pending message; creating a new CDM session",
                  cdm_session.c_str());
        }

        std::string new_session;
        drm::Status status = bridge_->createSessionAndGenerateRequest(
                toCdmSessionType(in_keyType), in_initData, &new_session);
        if (status != drm::Status::OK) return toNdkStatus(status);
        bridge_->bindCdmSession(in_scope, new_session);
        cdm_session = new_session;
    }

    // The request itself arrives asynchronously on OnSessionMessage. It is
    // usually already buffered by the time the reply above lands.
    SessionMessage message;
    if (!bridge_->waitForSessionMessage(cdm_session, kMessageTimeout, &message)) {
        ALOGE("getKeyRequest: no license message for CDM session '%s'",
              cdm_session.c_str());
        return toNdkStatus(drm::Status::ERROR_DRM_UNKNOWN,
                           "timed out waiting for the CDM license request");
    }

    _aidl_return->request = std::move(message.data);
    _aidl_return->requestType = in_keyType == drm::KeyType::RELEASE
                                        ? fallback_type
                                        : toKeyRequestType(message.type);
    _aidl_return->defaultUrl = "";
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::provideKeyResponse(
        const std::vector<uint8_t>& in_scope,
        const std::vector<uint8_t>& in_response, drm::KeySetId* _aidl_return) {
    if (in_response.empty()) {
        return toNdkStatus(drm::Status::BAD_VALUE, "empty key response");
    }

    // |scope| is a session id for a normal response, or a keySetId when the app
    // is completing a release. A keySetId is the CDM session id verbatim, so
    // fall back to that when the bytes are not a session we handed out.
    std::string cdm_session;
    if (bridge_->hasAndroidSession(in_scope)) {
        cdm_session = bridge_->cdmSessionFor(in_scope);
        if (cdm_session.empty()) {
            return toNdkStatus(drm::Status::ERROR_DRM_INVALID_STATE,
                               "no CDM session; getKeyRequest first");
        }
    } else {
        cdm_session = toString(in_scope);
    }

    drm::Status status = bridge_->updateSession(cdm_session, in_response);
    if (status != drm::Status::OK) return toNdkStatus(status);

    // The keySetId an app stores for restoreKeys() later.
    _aidl_return->keySetId = toBytes(cdm_session);
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::removeKeys(
        const std::vector<uint8_t>& in_sessionId) {
    const std::string cdm_session = bridge_->cdmSessionFor(in_sessionId);
    if (cdm_session.empty()) {
        return toNdkStatus(drm::Status::ERROR_DRM_SESSION_NOT_OPENED);
    }
    return toNdkStatus(bridge_->removeSession(cdm_session));
}

::ndk::ScopedAStatus DrmPlugin::restoreKeys(
        const std::vector<uint8_t>& in_sessionId,
        const drm::KeySetId& in_keySetId) {
    if (!bridge_->hasAndroidSession(in_sessionId)) {
        return toNdkStatus(drm::Status::ERROR_DRM_SESSION_NOT_OPENED);
    }
    const std::string cdm_session = toString(in_keySetId.keySetId);
    if (cdm_session.empty()) {
        return toNdkStatus(drm::Status::BAD_VALUE, "empty keySetId");
    }

    drm::Status status = bridge_->loadSession(
            ::arc::mojom::CdmSessionType::kPersistentLicense, cdm_session);
    if (status != drm::Status::OK) return toNdkStatus(status);

    bridge_->bindCdmSession(in_sessionId, cdm_session);
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::queryKeyStatus(
        const std::vector<uint8_t>& in_sessionId,
        std::vector<drm::KeyValue>* _aidl_return) {
    if (!bridge_->hasAndroidSession(in_sessionId)) {
        return toNdkStatus(drm::Status::ERROR_DRM_SESSION_NOT_OPENED);
    }
    // The contract pushes key state through OnSessionKeysChange rather than
    // offering a query, so there is nothing authoritative to report here.
    _aidl_return->clear();
    return ::ndk::ScopedAStatus::ok();
}

// ---------------------------------------------------------------------------
// Properties
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::getPropertyString(
        const std::string& in_propertyName, std::string* _aidl_return) {
    // Apps read these before they will attempt playback; answering "" or
    // failing makes several of them give up.
    if (in_propertyName == "vendor") {
        *_aidl_return = "Google";
    } else if (in_propertyName == "version") {
        *_aidl_return = "1.0";
    } else if (in_propertyName == "description") {
        *_aidl_return = "Widevine CDM";
    } else if (in_propertyName == "algorithms") {
        // No generic crypto/sign surface — see decrypt()/sign() below.
        *_aidl_return = "";
    } else if (in_propertyName == "securityLevel") {
        *_aidl_return = "L3";
    } else if (in_propertyName == "systemId") {
        *_aidl_return = "4464";
    } else if (in_propertyName == "hdcpLevel" ||
               in_propertyName == "maxHdcpLevel") {
        *_aidl_return = "HDCP_NONE";
    } else if (in_propertyName == "usageReportingSupport") {
        // Secure stops are L1 territory.
        *_aidl_return = "false";
    } else if (in_propertyName == "oemCryptoApiVersion") {
        // There is no OEMCrypto under this path — the library CDM does its own
        // crypto in software — so 0 is the truthful answer. Reporting "16"
        // instead was tried to see whether Netflix gates on a
        // non-zero level: it changed nothing (it still never opened a session),
        // so the invented number was dropped. Do not re-add it without
        // evidence that some client actually needs it.
        *_aidl_return = "0";
    } else if (in_propertyName == "maxNumberOfSessions") {
        *_aidl_return = "16";
    } else if (in_propertyName == "numberOfOpenSessions") {
        *_aidl_return = std::to_string(bridge_->numOpenSessions());
    } else {
        // At ALOGW on purpose: a property we refuse is the first thing to look
        // at when an MSL app (Netflix) gives up before opening a session.
        if (drmLogEnabled()) {
            ALOGW("getPropertyString: unsupported '%s'", in_propertyName.c_str());
        }
        return toNdkStatus(drm::Status::BAD_VALUE, "unsupported property");
    }
    if (drmLogEnabled()) {
        ALOGI("getPropertyString: %s = \"%s\"", in_propertyName.c_str(),
              _aidl_return->c_str());
    }
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::getPropertyByteArray(
        const std::string& in_propertyName, std::vector<uint8_t>* _aidl_return) {
    // MSL-based apps (Netflix) derive their ESN from deviceUniqueId and will not
    // play without it: failing this call is what puts Netflix into
    // WIDEVINE_INITIAL_PROVISIONING_CHECK_FAILED / MSL_LEGACY_CRYPTO, which it
    // shows as "could not reach the Netflix service". Chrome answers from the
    // per-profile CDM origin ID — see docs/widevine-cdm-bridge-plan.md.
    if (in_propertyName == "deviceUniqueId") {
        std::vector<uint8_t> id;
        const drm::Status status = bridge_->deviceUniqueId(&id);
        if (status != drm::Status::OK) {
            ALOGE("getPropertyByteArray: deviceUniqueId unavailable (%d)",
                  static_cast<int>(status));
            return toNdkStatus(status, "deviceUniqueId unavailable");
        }
        if (drmLogEnabled()) {
            ALOGI("getPropertyByteArray: deviceUniqueId -> %zu bytes", id.size());
        }
        *_aidl_return = std::move(id);
        return ::ndk::ScopedAStatus::ok();
    }
    if (in_propertyName == "metrics") {
        // MediaDrm::getMetrics() wants a serialized drm_metrics proto. We keep
        // no counters, and an empty proto is a valid one with no fields set, so
        // answer empty rather than BAD_VALUE: the exception is what an app sees
        // otherwise, and Netflix asks for this twice during startup.
        _aidl_return->clear();
        if (drmLogEnabled()) {
            ALOGI("getPropertyByteArray: metrics -> empty (no counters kept)");
        }
        return ::ndk::ScopedAStatus::ok();
    }
    if (drmLogEnabled()) {
        ALOGW("getPropertyByteArray: unsupported '%s'", in_propertyName.c_str());
    }
    return toNdkStatus(drm::Status::BAD_VALUE, "unsupported property");
}

::ndk::ScopedAStatus DrmPlugin::setPropertyString(
        const std::string& in_propertyName, const std::string& in_value) {
    (void)in_value;
    // "privacyMode" / "sessionSharing" are advisory; accept and ignore rather
    // than failing playback over a preference.
    if (in_propertyName == "privacyMode" ||
        in_propertyName == "sessionSharing" ||
        in_propertyName == "appId") {
        return ::ndk::ScopedAStatus::ok();
    }
    ALOGV("setPropertyString: unsupported '%s'", in_propertyName.c_str());
    return toNdkStatus(drm::Status::BAD_VALUE, "unsupported property");
}

::ndk::ScopedAStatus DrmPlugin::setPropertyByteArray(
        const std::string& in_propertyName, const std::vector<uint8_t>& in_value) {
    if (in_propertyName == "serviceCertificate") {
        return toNdkStatus(bridge_->setServerCertificate(in_value));
    }
    ALOGV("setPropertyByteArray: unsupported '%s'", in_propertyName.c_str());
    return toNdkStatus(drm::Status::BAD_VALUE, "unsupported property");
}

// ---------------------------------------------------------------------------
// Capabilities
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::getSecurityLevel(
        const std::vector<uint8_t>& in_sessionId,
        drm::SecurityLevel* _aidl_return) {
    if (!bridge_->hasAndroidSession(in_sessionId)) {
        return toNdkStatus(drm::Status::ERROR_DRM_SESSION_NOT_OPENED);
    }
    *_aidl_return = drm::SecurityLevel::SW_SECURE_CRYPTO;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::getHdcpLevels(drm::HdcpLevels* _aidl_return) {
    // Never claim HDCP: the license server would then hand out content this
    // path cannot protect.
    _aidl_return->connectedLevel = drm::HdcpLevel::HDCP_NONE;
    _aidl_return->maxLevel = drm::HdcpLevel::HDCP_NONE;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::getNumberOfSessions(
        drm::NumberOfSessions* _aidl_return) {
    _aidl_return->currentSessions = static_cast<int32_t>(bridge_->numOpenSessions());
    _aidl_return->maxSessions = 16;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::requiresSecureDecoder(
        const std::string& in_mime, drm::SecurityLevel in_level,
        bool* _aidl_return) {
    (void)in_mime;
    (void)in_level;
    *_aidl_return = false;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::setListener(
        const std::shared_ptr<drm::IDrmPluginListener>& in_listener) {
    bridge_->setListener(in_listener);
    return ::ndk::ScopedAStatus::ok();
}

// ---------------------------------------------------------------------------
// Provisioning
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::getProvisionRequest(
        const std::string& in_certificateType,
        const std::string& in_certificateAuthority,
        drm::ProvisionRequest* _aidl_return) {
    (void)in_certificateType;
    (void)in_certificateAuthority;
    (void)_aidl_return;
    // Chrome's CDM provisions against its own storage. Reaching here means the
    // Chrome side is misconfigured, not that Android should provision.
    ALOGE("getProvisionRequest: unexpected — Chrome's CDM owns provisioning");
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                       "provisioning is handled by Chrome");
}

::ndk::ScopedAStatus DrmPlugin::provideProvisionResponse(
        const std::vector<uint8_t>& in_response,
        drm::ProvideProvisionResponseResult* _aidl_return) {
    (void)in_response;
    (void)_aidl_return;
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                       "provisioning is handled by Chrome");
}

// ---------------------------------------------------------------------------
// Secure stops (unsupported; answer empty)
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::getSecureStop(
        const drm::SecureStopId& in_secureStopId, drm::SecureStop* _aidl_return) {
    (void)in_secureStopId;
    (void)_aidl_return;
    return toNdkStatus(drm::Status::BAD_VALUE, "secure stops are not supported");
}

::ndk::ScopedAStatus DrmPlugin::getSecureStopIds(
        std::vector<drm::SecureStopId>* _aidl_return) {
    _aidl_return->clear();
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::getSecureStops(
        std::vector<drm::SecureStop>* _aidl_return) {
    _aidl_return->clear();
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::releaseAllSecureStops() {
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::releaseSecureStop(
        const drm::SecureStopId& in_secureStopId) {
    (void)in_secureStopId;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::releaseSecureStops(
        const drm::OpaqueData& in_ssRelease) {
    (void)in_ssRelease;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::removeAllSecureStops() {
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::removeSecureStop(
        const drm::SecureStopId& in_secureStopId) {
    (void)in_secureStopId;
    return ::ndk::ScopedAStatus::ok();
}

// ---------------------------------------------------------------------------
// Offline licenses
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::getOfflineLicenseKeySetIds(
        std::vector<drm::KeySetId>* _aidl_return) {
    // The CDM's persistent store is not enumerable through the contract.
    _aidl_return->clear();
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::getOfflineLicenseState(
        const drm::KeySetId& in_keySetId, drm::OfflineLicenseState* _aidl_return) {
    (void)in_keySetId;
    *_aidl_return = drm::OfflineLicenseState::UNKNOWN;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::removeOfflineLicense(
        const drm::KeySetId& in_keySetId) {
    const std::string cdm_session = toString(in_keySetId.keySetId);
    if (cdm_session.empty()) {
        return toNdkStatus(drm::Status::BAD_VALUE, "empty keySetId");
    }
    return toNdkStatus(bridge_->removeSession(cdm_session));
}

// ---------------------------------------------------------------------------
// Generic crypto / signing — outside the CDM contract
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::decrypt(const std::vector<uint8_t>& in_sessionId,
                                        const std::vector<uint8_t>& in_keyId,
                                        const std::vector<uint8_t>& in_input,
                                        const std::vector<uint8_t>& in_iv,
                                        std::vector<uint8_t>* _aidl_return) {
    (void)in_sessionId; (void)in_keyId; (void)in_input; (void)in_iv;
    (void)_aidl_return;
    // This is MediaDrm's generic-crypto API, not media decryption — that goes
    // through ICryptoPlugin::decrypt. "algorithms" is "" for the same reason.
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE);
}

::ndk::ScopedAStatus DrmPlugin::encrypt(const std::vector<uint8_t>& in_sessionId,
                                        const std::vector<uint8_t>& in_keyId,
                                        const std::vector<uint8_t>& in_input,
                                        const std::vector<uint8_t>& in_iv,
                                        std::vector<uint8_t>* _aidl_return) {
    (void)in_sessionId; (void)in_keyId; (void)in_input; (void)in_iv;
    (void)_aidl_return;
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE);
}

::ndk::ScopedAStatus DrmPlugin::sign(const std::vector<uint8_t>& in_sessionId,
                                     const std::vector<uint8_t>& in_keyId,
                                     const std::vector<uint8_t>& in_message,
                                     std::vector<uint8_t>* _aidl_return) {
    (void)in_sessionId; (void)in_keyId; (void)in_message; (void)_aidl_return;
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE);
}

::ndk::ScopedAStatus DrmPlugin::signRSA(const std::vector<uint8_t>& in_sessionId,
                                        const std::string& in_algorithm,
                                        const std::vector<uint8_t>& in_message,
                                        const std::vector<uint8_t>& in_wrappedkey,
                                        std::vector<uint8_t>* _aidl_return) {
    (void)in_sessionId; (void)in_algorithm; (void)in_message;
    (void)in_wrappedkey; (void)_aidl_return;
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE);
}

::ndk::ScopedAStatus DrmPlugin::verify(const std::vector<uint8_t>& in_sessionId,
                                       const std::vector<uint8_t>& in_keyId,
                                       const std::vector<uint8_t>& in_message,
                                       const std::vector<uint8_t>& in_signature,
                                       bool* _aidl_return) {
    (void)in_sessionId; (void)in_keyId; (void)in_message; (void)in_signature;
    (void)_aidl_return;
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE);
}

::ndk::ScopedAStatus DrmPlugin::setCipherAlgorithm(
        const std::vector<uint8_t>& in_sessionId, const std::string& in_algorithm) {
    (void)in_sessionId; (void)in_algorithm;
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE);
}

::ndk::ScopedAStatus DrmPlugin::setMacAlgorithm(
        const std::vector<uint8_t>& in_sessionId, const std::string& in_algorithm) {
    (void)in_sessionId; (void)in_algorithm;
    return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE);
}

// ---------------------------------------------------------------------------
// Diagnostics
// ---------------------------------------------------------------------------

::ndk::ScopedAStatus DrmPlugin::setPlaybackId(
        const std::vector<uint8_t>& in_sessionId, const std::string& in_playbackId) {
    (void)in_sessionId; (void)in_playbackId;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::getMetrics(
        std::vector<drm::DrmMetricGroup>* _aidl_return) {
    _aidl_return->clear();
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmPlugin::getLogMessages(
        std::vector<drm::LogMessage>* _aidl_return) {
    _aidl_return->clear();
    return ::ndk::ScopedAStatus::ok();
}

}  // namespace widevine
}  // namespace fydroid

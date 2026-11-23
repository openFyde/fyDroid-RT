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

// DrmPlugin — MediaDrm surface for the fydroid Widevine bridge.
//
// Every session/key operation is forwarded to Chrome's library CDM over the ARC
// mojo bridge (CdmBridge). Nothing crypto happens in this process; this is a
// translation layer between MediaDrm's synchronous, byte-array-session-id world
// and EME's asynchronous, string-session-id one.
//
// L3 only, by construction — see docs/widevine-cdm-bridge-plan.md.
#pragma once

#include <memory>

#include <aidl/android/hardware/drm/BnDrmPlugin.h>

#include "CdmBridge.h"

namespace fydroid {
namespace widevine {

namespace drm = ::aidl::android::hardware::drm;

class DrmPlugin : public drm::BnDrmPlugin {
public:
    DrmPlugin() : bridge_(CdmBridge::get()) {}
    virtual ~DrmPlugin() = default;

    ::ndk::ScopedAStatus openSession(drm::SecurityLevel in_securityLevel,
                                     std::vector<uint8_t>* _aidl_return) override;
    ::ndk::ScopedAStatus closeSession(
            const std::vector<uint8_t>& in_sessionId) override;

    ::ndk::ScopedAStatus getKeyRequest(
            const std::vector<uint8_t>& in_scope,
            const std::vector<uint8_t>& in_initData, const std::string& in_mimeType,
            drm::KeyType in_keyType,
            const std::vector<drm::KeyValue>& in_optionalParameters,
            drm::KeyRequest* _aidl_return) override;
    ::ndk::ScopedAStatus provideKeyResponse(
            const std::vector<uint8_t>& in_scope,
            const std::vector<uint8_t>& in_response,
            drm::KeySetId* _aidl_return) override;
    ::ndk::ScopedAStatus removeKeys(
            const std::vector<uint8_t>& in_sessionId) override;
    ::ndk::ScopedAStatus restoreKeys(const std::vector<uint8_t>& in_sessionId,
                                     const drm::KeySetId& in_keySetId) override;
    ::ndk::ScopedAStatus queryKeyStatus(
            const std::vector<uint8_t>& in_sessionId,
            std::vector<drm::KeyValue>* _aidl_return) override;

    ::ndk::ScopedAStatus getPropertyString(const std::string& in_propertyName,
                                           std::string* _aidl_return) override;
    ::ndk::ScopedAStatus getPropertyByteArray(
            const std::string& in_propertyName,
            std::vector<uint8_t>* _aidl_return) override;
    ::ndk::ScopedAStatus setPropertyString(const std::string& in_propertyName,
                                           const std::string& in_value) override;
    ::ndk::ScopedAStatus setPropertyByteArray(
            const std::string& in_propertyName,
            const std::vector<uint8_t>& in_value) override;

    ::ndk::ScopedAStatus getSecurityLevel(
            const std::vector<uint8_t>& in_sessionId,
            drm::SecurityLevel* _aidl_return) override;
    ::ndk::ScopedAStatus getHdcpLevels(drm::HdcpLevels* _aidl_return) override;
    ::ndk::ScopedAStatus getNumberOfSessions(
            drm::NumberOfSessions* _aidl_return) override;
    ::ndk::ScopedAStatus requiresSecureDecoder(
            const std::string& in_mime, drm::SecurityLevel in_level,
            bool* _aidl_return) override;

    ::ndk::ScopedAStatus setListener(
            const std::shared_ptr<drm::IDrmPluginListener>& in_listener) override;

    // --- Provisioning: Chrome's CDM provisions itself, so these never fire in
    // a healthy setup. See CdmBridge's kNeedsProvisioning mapping.
    ::ndk::ScopedAStatus getProvisionRequest(
            const std::string& in_certificateType,
            const std::string& in_certificateAuthority,
            drm::ProvisionRequest* _aidl_return) override;
    ::ndk::ScopedAStatus provideProvisionResponse(
            const std::vector<uint8_t>& in_response,
            drm::ProvideProvisionResponseResult* _aidl_return) override;

    // --- Secure stops: L1 territory (they exist to prove playback stopped on
    // trusted hardware). Answer empty rather than failing, so apps that poll
    // them do not treat it as a fatal error.
    ::ndk::ScopedAStatus getSecureStop(const drm::SecureStopId& in_secureStopId,
                                        drm::SecureStop* _aidl_return) override;
    ::ndk::ScopedAStatus getSecureStopIds(
            std::vector<drm::SecureStopId>* _aidl_return) override;
    ::ndk::ScopedAStatus getSecureStops(
            std::vector<drm::SecureStop>* _aidl_return) override;
    ::ndk::ScopedAStatus releaseAllSecureStops() override;
    ::ndk::ScopedAStatus releaseSecureStop(
            const drm::SecureStopId& in_secureStopId) override;
    ::ndk::ScopedAStatus releaseSecureStops(
            const drm::OpaqueData& in_ssRelease) override;
    ::ndk::ScopedAStatus removeAllSecureStops() override;
    ::ndk::ScopedAStatus removeSecureStop(
            const drm::SecureStopId& in_secureStopId) override;

    // --- Offline licenses: the CDM owns its own persistent store; we cannot
    // enumerate it through the contract.
    ::ndk::ScopedAStatus getOfflineLicenseKeySetIds(
            std::vector<drm::KeySetId>* _aidl_return) override;
    ::ndk::ScopedAStatus getOfflineLicenseState(
            const drm::KeySetId& in_keySetId,
            drm::OfflineLicenseState* _aidl_return) override;
    ::ndk::ScopedAStatus removeOfflineLicense(
            const drm::KeySetId& in_keySetId) override;

    // --- Generic crypto / signing: not exposed by the CDM contract.
    ::ndk::ScopedAStatus decrypt(const std::vector<uint8_t>& in_sessionId,
                                  const std::vector<uint8_t>& in_keyId,
                                  const std::vector<uint8_t>& in_input,
                                  const std::vector<uint8_t>& in_iv,
                                  std::vector<uint8_t>* _aidl_return) override;
    ::ndk::ScopedAStatus encrypt(const std::vector<uint8_t>& in_sessionId,
                                  const std::vector<uint8_t>& in_keyId,
                                  const std::vector<uint8_t>& in_input,
                                  const std::vector<uint8_t>& in_iv,
                                  std::vector<uint8_t>* _aidl_return) override;
    ::ndk::ScopedAStatus sign(const std::vector<uint8_t>& in_sessionId,
                               const std::vector<uint8_t>& in_keyId,
                               const std::vector<uint8_t>& in_message,
                               std::vector<uint8_t>* _aidl_return) override;
    ::ndk::ScopedAStatus signRSA(const std::vector<uint8_t>& in_sessionId,
                                  const std::string& in_algorithm,
                                  const std::vector<uint8_t>& in_message,
                                  const std::vector<uint8_t>& in_wrappedkey,
                                  std::vector<uint8_t>* _aidl_return) override;
    ::ndk::ScopedAStatus verify(const std::vector<uint8_t>& in_sessionId,
                                 const std::vector<uint8_t>& in_keyId,
                                 const std::vector<uint8_t>& in_message,
                                 const std::vector<uint8_t>& in_signature,
                                 bool* _aidl_return) override;
    ::ndk::ScopedAStatus setCipherAlgorithm(
            const std::vector<uint8_t>& in_sessionId,
            const std::string& in_algorithm) override;
    ::ndk::ScopedAStatus setMacAlgorithm(
            const std::vector<uint8_t>& in_sessionId,
            const std::string& in_algorithm) override;

    ::ndk::ScopedAStatus setPlaybackId(const std::vector<uint8_t>& in_sessionId,
                                        const std::string& in_playbackId) override;
    ::ndk::ScopedAStatus getMetrics(
            std::vector<drm::DrmMetricGroup>* _aidl_return) override;
    ::ndk::ScopedAStatus getLogMessages(
            std::vector<drm::LogMessage>* _aidl_return) override;

private:
    CdmBridge* bridge_;
};

}  // namespace widevine
}  // namespace fydroid

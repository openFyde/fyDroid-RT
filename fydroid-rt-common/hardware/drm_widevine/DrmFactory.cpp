#define LOG_TAG "widevine-DrmFactory"

#include "DrmFactory.h"

#include <utils/Log.h>

#include "CdmBridge.h"
#include "CryptoPlugin.h"
#include "DrmPlugin.h"
#include "WidevineUtils.h"

namespace fydroid {
namespace widevine {

namespace {

// The MIME types Chrome's library CDM handles. Audio is the case that matters
// (Spotify); video is listed because the CDM supports it, but every protected
// sample makes a mojo round trip, so high-bitrate video may not keep up.
constexpr const char* kSupportedMimeTypes[] = {
        "video/mp4", "audio/mp4", "video/webm", "audio/webm", "cenc", "webm",
};

}  // namespace

::ndk::ScopedAStatus DrmFactory::createDrmPlugin(
        const drm::Uuid& in_uuid, const std::string& in_appPackageName,
        std::shared_ptr<drm::IDrmPlugin>* _aidl_return) {
    (void)in_appPackageName;
    std::vector<uint8_t> uuid(in_uuid.uuid.begin(), in_uuid.uuid.end());
    if (!isWidevineUuid(uuid)) {
        ALOGE("createDrmPlugin: not the Widevine UUID");
        *_aidl_return = nullptr;
        return toNdkStatus(drm::Status::BAD_VALUE);
    }
    // Chrome has already told us it has no CDM. Refusing here turns into
    // UnsupportedSchemeException from `new MediaDrm()`, which apps handle by
    // falling back or reporting the device unsupported — far better than
    // letting them set playback up and fail at the first getKeyRequest. The
    // scheme itself stays advertised (see getSupportedCryptoSchemes), and the
    // verdict is dropped the moment a new host connects.
    if (CdmBridge::get()->cdmUnavailable()) {
        ALOGE("createDrmPlugin: Chrome reports no Widevine CDM available");
        *_aidl_return = nullptr;
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "no Widevine CDM on the Chrome side");
    }
    *_aidl_return = ::ndk::SharedRefBase::make<DrmPlugin>();
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmFactory::createCryptoPlugin(
        const drm::Uuid& in_uuid, const std::vector<uint8_t>& in_initData,
        std::shared_ptr<drm::ICryptoPlugin>* _aidl_return) {
    std::vector<uint8_t> uuid(in_uuid.uuid.begin(), in_uuid.uuid.end());
    if (!isWidevineUuid(uuid)) {
        ALOGE("createCryptoPlugin: not the Widevine UUID");
        *_aidl_return = nullptr;
        return toNdkStatus(drm::Status::BAD_VALUE);
    }
    if (CdmBridge::get()->cdmUnavailable()) {
        ALOGE("createCryptoPlugin: Chrome reports no Widevine CDM available");
        *_aidl_return = nullptr;
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "no Widevine CDM on the Chrome side");
    }
    // |initData| is the MediaDrm session id for MediaCrypto.
    *_aidl_return = ::ndk::SharedRefBase::make<CryptoPlugin>(in_initData);
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus DrmFactory::getSupportedCryptoSchemes(
        drm::CryptoSchemes* _aidl_return) {
    // Advertised unconditionally, including before the ARC bridge is up: the
    // framework queries this once at startup and caches it, and Chrome is
    // expected to be there. If it never connects, key operations fail with
    // ERROR_DRM_RESOURCE_BUSY rather than the scheme quietly vanishing.
    drm::CryptoSchemes schemes{};
    drm::Uuid uuid;
    std::copy(kWidevineUuid.begin(), kWidevineUuid.end(), uuid.uuid.begin());
    schemes.uuids.push_back(uuid);
    for (const char* mime : kSupportedMimeTypes) {
        schemes.mimeTypes.push_back({mime, drm::SecurityLevel::SW_SECURE_CRYPTO,
                                     drm::SecurityLevel::SW_SECURE_CRYPTO});
    }
    *_aidl_return = schemes;
    return ::ndk::ScopedAStatus::ok();
}

binder_status_t DrmFactory::dump(int fd, const char** args, uint32_t numArgs) {
    (void)args;
    (void)numArgs;
    if (fd < 0) return STATUS_BAD_VALUE;
    CdmBridge* bridge = CdmBridge::get();
    dprintf(fd, "fydroid Widevine (L3, via Chrome's library CDM)\n");
    dprintf(fd, "  ARC CdmHost connected: %s\n",
            bridge->hostReady() ? "yes" : "no");
    dprintf(fd, "  open sessions: %zu\n", bridge->numOpenSessions());
    return STATUS_OK;
}

}  // namespace widevine
}  // namespace fydroid

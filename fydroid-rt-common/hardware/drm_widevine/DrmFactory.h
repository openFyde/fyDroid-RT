// DrmFactory — advertises the Widevine crypto scheme to the media framework.
#pragma once

#include <aidl/android/hardware/drm/BnDrmFactory.h>

namespace fydroid {
namespace widevine {

namespace drm = ::aidl::android::hardware::drm;

class DrmFactory : public drm::BnDrmFactory {
public:
    DrmFactory() = default;
    virtual ~DrmFactory() = default;

    ::ndk::ScopedAStatus createDrmPlugin(
            const drm::Uuid& in_uuid, const std::string& in_appPackageName,
            std::shared_ptr<drm::IDrmPlugin>* _aidl_return) override;
    ::ndk::ScopedAStatus createCryptoPlugin(
            const drm::Uuid& in_uuid, const std::vector<uint8_t>& in_initData,
            std::shared_ptr<drm::ICryptoPlugin>* _aidl_return) override;
    ::ndk::ScopedAStatus getSupportedCryptoSchemes(
            drm::CryptoSchemes* _aidl_return) override;

    binder_status_t dump(int fd, const char** args, uint32_t numArgs) override;
};

}  // namespace widevine
}  // namespace fydroid

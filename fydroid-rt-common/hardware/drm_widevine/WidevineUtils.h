// Shared helpers for the fydroid Widevine DRM HAL.
#pragma once

#include <array>
#include <cstdint>
#include <string>
#include <vector>

#include <aidl/android/hardware/drm/Status.h>
#include <android/binder_auto_utils.h>

namespace fydroid {
namespace widevine {

// edef8ba9-79d6-4ace-a3c8-27dcd51d21ed
constexpr std::array<uint8_t, 16> kWidevineUuid = {
        0xed, 0xef, 0x8b, 0xa9, 0x79, 0xd6, 0x4a, 0xce,
        0xa3, 0xc8, 0x27, 0xdc, 0xd5, 0x1d, 0x21, 0xed};

inline bool isWidevineUuid(const std::vector<uint8_t>& uuid) {
    return uuid.size() == kWidevineUuid.size() &&
           std::equal(uuid.begin(), uuid.end(), kWidevineUuid.begin());
}

inline ::ndk::ScopedAStatus toNdkStatus(
        ::aidl::android::hardware::drm::Status status,
        const char* message = "") {
    if (status == ::aidl::android::hardware::drm::Status::OK) {
        return ::ndk::ScopedAStatus::ok();
    }
    return ::ndk::ScopedAStatus::fromServiceSpecificErrorWithMessage(
            static_cast<int32_t>(status), message);
}

// CDM session ids are opaque strings; MediaDrm keySetIds are opaque bytes. We
// use the CDM's session id as the keySetId so restoreKeys() can hand it
// straight back to LoadSession().
inline std::vector<uint8_t> toBytes(const std::string& s) {
    return std::vector<uint8_t>(s.begin(), s.end());
}

inline std::string toString(const std::vector<uint8_t>& b) {
    return std::string(b.begin(), b.end());
}

}  // namespace widevine
}  // namespace fydroid

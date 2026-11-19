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

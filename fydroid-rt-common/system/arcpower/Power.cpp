// Copyright (C) 2024 Fyde Innovations
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#define LOG_TAG "fydroid_arcpower"

#include "Power.h"

#include <log/log.h>

namespace fydroid {
namespace power {

using ::aidl::android::hardware::power::Boost;
using ::aidl::android::hardware::power::IPowerHintSession;
using ::aidl::android::hardware::power::Mode;

::ndk::ScopedAStatus Power::setMode(Mode type, bool enabled) {
    ALOGV("setMode(%d, %d)", static_cast<int>(type), enabled);
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus Power::isModeSupported(Mode type, bool* _aidl_return) {
    ALOGV("isModeSupported(%d)", static_cast<int>(type));
    *_aidl_return = false;
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus Power::setBoost(Boost type, int32_t durationMs) {
    ALOGV("setBoost(%d, %d)", static_cast<int>(type), durationMs);
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus Power::isBoostSupported(Boost type, bool* _aidl_return) {
    ALOGV("isBoostSupported(%d)", static_cast<int>(type));
    *_aidl_return = false;
    return ::ndk::ScopedAStatus::ok();
}

// No hint sessions: report unsupported the same way AOSP's example service
// does, so PowerManagerService stops asking instead of retrying.
::ndk::ScopedAStatus Power::createHintSession(int32_t, int32_t, const std::vector<int32_t>&,
                                              int64_t,
                                              std::shared_ptr<IPowerHintSession>* _aidl_return) {
    *_aidl_return = nullptr;
    return ::ndk::ScopedAStatus::fromExceptionCode(EX_UNSUPPORTED_OPERATION);
}

::ndk::ScopedAStatus Power::getHintSessionPreferredRate(int64_t* outNanoseconds) {
    *outNanoseconds = -1;
    return ::ndk::ScopedAStatus::ok();
}

}  // namespace power
}  // namespace fydroid

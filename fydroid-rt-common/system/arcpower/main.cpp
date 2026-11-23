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

#include <android/binder_manager.h>
#include <android/binder_process.h>
#include <log/log.h>

#include <string>

#include "Power.h"
#include "PowerStats.h"

int main() {
    ABinderProcess_setThreadPoolMaxThreadCount(1);

    auto power = ::ndk::SharedRefBase::make<fydroid::power::Power>();
    const std::string powerName =
        std::string(::aidl::android::hardware::power::IPower::descriptor) + "/default";
    binder_status_t status = AServiceManager_addService(power->asBinder().get(), powerName.c_str());
    LOG_ALWAYS_FATAL_IF(status != STATUS_OK, "Failed to register IPower service: %d", status);

    auto stats = ::ndk::SharedRefBase::make<fydroid::power::PowerStats>();
    const std::string statsName =
        std::string(::aidl::android::hardware::power::stats::IPowerStats::descriptor) + "/default";
    status = AServiceManager_addService(stats->asBinder().get(), statsName.c_str());
    LOG_ALWAYS_FATAL_IF(status != STATUS_OK, "Failed to register IPowerStats service: %d", status);

    ALOGI("fydroid_arcpower registered IPower and IPowerStats (AIDL)");
    ABinderProcess_joinThreadPool();
    return 1;  // ABinderProcess_joinThreadPool should never return
}

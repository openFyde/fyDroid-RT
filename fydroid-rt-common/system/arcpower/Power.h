// Copyright (C) 2024 The FydeOS Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0

#pragma once

#include <aidl/android/hardware/power/BnPower.h>

namespace fydroid {
namespace power {

// Stub implementation of android.hardware.power.IPower (AIDL).
// All methods are no-ops; goal is just to satisfy framework lookups so
// servicemanager doesn't enter the lazy-HAL retry loop.
//
// Was HIDL @1.0 originally. FCM 6 lists only the AIDL power HAL, so
// serving @1.0 failed checkvintf ("deprecated in compatibility matrix at FCM
// Version 6; it should not be served"). Interface version is pinned to 2
// because compatibility_matrix.6.xml accepts android.hardware.power 1-2.
//
// TODO(fydroid): wire setMode/setBoost to ARC PowerHost mojo when an
// arcbridge connection is available.
class Power : public ::aidl::android::hardware::power::BnPower {
   public:
    Power() = default;

    ::ndk::ScopedAStatus setMode(::aidl::android::hardware::power::Mode type,
                                 bool enabled) override;
    ::ndk::ScopedAStatus isModeSupported(::aidl::android::hardware::power::Mode type,
                                         bool* _aidl_return) override;
    ::ndk::ScopedAStatus setBoost(::aidl::android::hardware::power::Boost type,
                                  int32_t durationMs) override;
    ::ndk::ScopedAStatus isBoostSupported(::aidl::android::hardware::power::Boost type,
                                          bool* _aidl_return) override;
    ::ndk::ScopedAStatus createHintSession(
        int32_t tgid, int32_t uid, const std::vector<int32_t>& threadIds,
        int64_t durationNanos,
        std::shared_ptr<::aidl::android::hardware::power::IPowerHintSession>* _aidl_return)
        override;
    ::ndk::ScopedAStatus getHintSessionPreferredRate(int64_t* outNanoseconds) override;
};

}  // namespace power
}  // namespace fydroid

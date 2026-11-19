// Copyright (C) 2024 The FydeOS Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0

#pragma once

#include <aidl/android/hardware/power/stats/BnPowerStats.h>

namespace fydroid {
namespace power {

// Stub implementation of android.hardware.power.stats.IPowerStats (AIDL).
// Every query returns an empty set; goal is just to satisfy framework lookups
// so servicemanager doesn't enter the lazy-HAL retry loop (BatteryStats used
// to block Settings launch by ~4s waiting on this).
//
// Was HIDL @1.0 originally, migrated with IPower for the same reason:
// compatibility_matrix.6.xml lists power.stats only as AIDL, so the HIDL
// interface counted as deprecated-and-served.
class PowerStats : public ::aidl::android::hardware::power::stats::BnPowerStats {
   public:
    PowerStats() = default;

    ::ndk::ScopedAStatus getPowerEntityInfo(
        std::vector<::aidl::android::hardware::power::stats::PowerEntity>* _aidl_return) override;
    ::ndk::ScopedAStatus getStateResidency(
        const std::vector<int32_t>& powerEntityIds,
        std::vector<::aidl::android::hardware::power::stats::StateResidencyResult>* _aidl_return)
        override;
    ::ndk::ScopedAStatus getEnergyConsumerInfo(
        std::vector<::aidl::android::hardware::power::stats::EnergyConsumer>* _aidl_return)
        override;
    ::ndk::ScopedAStatus getEnergyConsumed(
        const std::vector<int32_t>& energyConsumerIds,
        std::vector<::aidl::android::hardware::power::stats::EnergyConsumerResult>* _aidl_return)
        override;
    ::ndk::ScopedAStatus getEnergyMeterInfo(
        std::vector<::aidl::android::hardware::power::stats::Channel>* _aidl_return) override;
    ::ndk::ScopedAStatus readEnergyMeter(
        const std::vector<int32_t>& channelIds,
        std::vector<::aidl::android::hardware::power::stats::EnergyMeasurement>* _aidl_return)
        override;
};

}  // namespace power
}  // namespace fydroid

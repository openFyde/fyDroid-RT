/*
 * Copyright (C) 2024 The FydeOS Authors
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

#pragma once

#include <V2_1/SubHal.h>

#include <android/hardware/sensors/1.0/types.h>
#include <android/hardware/sensors/2.0/ISensors.h>
#include <android/hardware/sensors/2.1/types.h>

#include <atomic>
#include <condition_variable>
#include <mutex>
#include <thread>

#include <binder/IBinder.h>
#include <binder/IServiceManager.h>

namespace fydroid {
namespace sensors {

namespace V1_0 = ::android::hardware::sensors::V1_0;
namespace V2_0 = ::android::hardware::sensors::V2_0;
namespace V2_1 = ::android::hardware::sensors::V2_1;

using ISensorsSubHal = V2_1::implementation::ISensorsSubHal;
using IHalProxyCallback = V2_1::implementation::IHalProxyCallback;
using ScopedWakelock = V2_0::implementation::ScopedWakelock;

using ::android::hardware::Return;
using ::android::hardware::Void;
using ::android::hardware::hidl_handle;
using ::android::hardware::hidl_string;
using ::android::hardware::hidl_vec;

class OrientationSensorSubHal : public ISensorsSubHal {
public:
    OrientationSensorSubHal();
    ~OrientationSensorSubHal() override;

    // ISensorsSubHal
    Return<V1_0::Result> initialize(
            const android::sp<IHalProxyCallback>& halProxyCallback) override;
    Return<void> getSensorsList_2_1(getSensorsList_2_1_cb _hidl_cb) override;
    Return<V1_0::Result> activate(int32_t sensorHandle, bool enabled) override;
    Return<V1_0::Result> batch(int32_t sensorHandle, int64_t samplingPeriodNs,
                               int64_t maxReportLatencyNs) override;
    Return<V1_0::Result> flush(int32_t sensorHandle) override;
    Return<V1_0::Result> setOperationMode(V1_0::OperationMode mode) override;
    Return<void> debug(const hidl_handle& fd, const hidl_vec<hidl_string>& args) override;
    const std::string getName() override;

    // Stubs
    Return<void> registerDirectChannel(
            const V1_0::SharedMemInfo& mem,
            V2_0::ISensors::registerDirectChannel_cb cb) override;
    Return<V1_0::Result> unregisterDirectChannel(int32_t channelHandle) override;
    Return<void> configDirectReport(
            int32_t sensorHandle, int32_t channelHandle, V1_0::RateLevel rate,
            V2_0::ISensors::configDirectReport_cb cb) override;
    Return<V1_0::Result> injectSensorData_2_1(const V2_1::Event& event) override;

private:
    static constexpr int32_t kSensorHandle = 0;

    V2_1::SensorInfo buildSensorInfo();

    void pollThread();
    int32_t queryDisplayTransform();

    android::sp<IHalProxyCallback> mCallback;
    V1_0::OperationMode mOperationMode = V1_0::OperationMode::NORMAL;

    std::atomic<bool> mActive{false};
    std::atomic<bool> mStopThread{false};
    std::thread mPollThread;
    std::mutex mPollMutex;
    std::condition_variable mPollCv;

    int32_t mLastTransform = -1;

    android::sp<android::IBinder> mWaylandBinder;
};

}  // namespace sensors
}  // namespace fydroid

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

#include "IioSensorSubHal.h"

#include <arc/MojoProcessSupport.h>

#include <log/log.h>
#include <utils/SystemClock.h>

#define LOG_TAG "IioSensorSubHal"

namespace fydroid {
namespace sensors {

IioSensorSubHal::IioSensorSubHal() {
    ALOGI("IioSensorSubHal: starting Mojo bridge in constructor");
    arc::MojoProcessSupport* mojoSupport = arc::MojoProcessSupport::GetInstance();
    mBridge = new IioSensorBridge(mojoSupport, this);
    mBridge->start();
}

IioSensorSubHal::~IioSensorSubHal() = default;

Return<V1_0::Result> IioSensorSubHal::initialize(
        const android::sp<IHalProxyCallback>& halProxyCallback) {
    ALOGI("IioSensorSubHal::initialize");
    mCallback = halProxyCallback;
    mOperationMode = V1_0::OperationMode::NORMAL;
    return V1_0::Result::OK;
}

Return<void> IioSensorSubHal::getSensorsList_2_1(getSensorsList_2_1_cb _hidl_cb) {
    ALOGI("IioSensorSubHal::getSensorsList_2_1");

    if (mSensorList.empty() && mBridge != nullptr) {
        auto sensors = mBridge->getSensors(10000);
        for (auto& [handle, info] : sensors) {
            mSensorList.push_back(info);
            mSensors[handle] = info;
        }
        ALOGI("IioSensorSubHal: discovered %zu sensors", mSensorList.size());
    }

    _hidl_cb(mSensorList);
    return Void();
}

Return<V1_0::Result> IioSensorSubHal::activate(int32_t sensorHandle, bool enabled) {
    if (mSensors.find(sensorHandle) == mSensors.end()) return V1_0::Result::BAD_VALUE;
    if (mBridge) mBridge->activate(sensorHandle, enabled);
    return V1_0::Result::OK;
}

Return<V1_0::Result> IioSensorSubHal::batch(
        int32_t sensorHandle, int64_t samplingPeriodNs, int64_t) {
    if (mSensors.find(sensorHandle) == mSensors.end()) return V1_0::Result::BAD_VALUE;
    if (mBridge) mBridge->batch(sensorHandle, samplingPeriodNs);
    return V1_0::Result::OK;
}

Return<V1_0::Result> IioSensorSubHal::flush(int32_t sensorHandle) {
    if (mSensors.find(sensorHandle) == mSensors.end()) return V1_0::Result::BAD_VALUE;

    V2_1::Event ev;
    ev.sensorHandle = sensorHandle;
    ev.sensorType = V2_1::SensorType::META_DATA;
    ev.u.meta.what = V1_0::MetaDataEventType::META_DATA_FLUSH_COMPLETE;
    ev.timestamp = android::elapsedRealtimeNano();

    std::vector<V2_1::Event> events = {ev};
    mCallback->postEvents(events, mCallback->createScopedWakelock(false));
    return V1_0::Result::OK;
}

Return<V1_0::Result> IioSensorSubHal::setOperationMode(V1_0::OperationMode mode) {
    mOperationMode = mode;
    return V1_0::Result::OK;
}

Return<void> IioSensorSubHal::debug(const hidl_handle& fd, const hidl_vec<hidl_string>&) {
    if (fd.getNativeHandle() == nullptr || fd->numFds < 1) return Void();
    FILE* out = fdopen(dup(fd->data[0]), "w");
    fprintf(out, "IioSensorSubHal:\n");
    fprintf(out, "  sensors: %zu\n", mSensors.size());
    for (auto& [handle, info] : mSensors) {
        fprintf(out, "  [%d] %s (type %d)\n", handle,
                info.name.c_str(), static_cast<int>(info.type));
    }
    fclose(out);
    return Void();
}

const std::string IioSensorSubHal::getName() {
    return "Fydroid-IIOService-Subhal";
}

Return<void> IioSensorSubHal::registerDirectChannel(
        const V1_0::SharedMemInfo&,
        V2_0::ISensors::registerDirectChannel_cb cb) {
    cb(V1_0::Result::INVALID_OPERATION, -1);
    return Void();
}

Return<V1_0::Result> IioSensorSubHal::unregisterDirectChannel(int32_t) {
    return V1_0::Result::INVALID_OPERATION;
}

Return<void> IioSensorSubHal::configDirectReport(
        int32_t, int32_t, V1_0::RateLevel,
        V2_0::ISensors::configDirectReport_cb cb) {
    cb(V1_0::Result::INVALID_OPERATION, 0);
    return Void();
}

Return<V1_0::Result> IioSensorSubHal::injectSensorData_2_1(const V2_1::Event&) {
    return V1_0::Result::INVALID_OPERATION;
}

void IioSensorSubHal::postSensorEvents(const std::vector<V2_1::Event>& events, bool wakeup) {
    if (mCallback) {
        mCallback->postEvents(events, mCallback->createScopedWakelock(wakeup));
    }
}

}  // namespace sensors
}  // namespace fydroid

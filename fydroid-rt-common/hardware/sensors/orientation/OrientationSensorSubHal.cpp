/*
 * Copyright (C) 2024 Fyde Innovations
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

#include "OrientationSensorSubHal.h"

#include <binder/Parcel.h>
#include <log/log.h>
#include <utils/SystemClock.h>

#define LOG_TAG "OrientationSensorSubHal"

namespace fydroid {
namespace sensors {

static constexpr uint32_t TRANSACTION_getDisplayLayout = 6;
static const android::String16 kWaylandInterfaceDescriptor(
        "org.chromium.arc.wayland.IWaylandService");

OrientationSensorSubHal::OrientationSensorSubHal() = default;

OrientationSensorSubHal::~OrientationSensorSubHal() {
    mStopThread = true;
    mPollCv.notify_all();
    if (mPollThread.joinable()) {
        mPollThread.join();
    }
}

V2_1::SensorInfo OrientationSensorSubHal::buildSensorInfo() {
    V2_1::SensorInfo info;
    info.sensorHandle = kSensorHandle;
    info.name = "Window Orientation";
    info.vendor = "FydeOS";
    info.version = 1;
    info.type = V2_1::SensorType::DEVICE_ORIENTATION;
    info.typeAsString = "android.sensor.device_orientation";
    info.maxRange = 3.0f;
    info.resolution = 1.0f;
    info.power = 0.001f;
    info.minDelay = 0;
    info.maxDelay = 0;
    info.fifoReservedEventCount = 0;
    info.fifoMaxEventCount = 0;
    info.requiredPermission = "";
    info.flags = static_cast<uint32_t>(V1_0::SensorFlagBits::ON_CHANGE_MODE) |
                 static_cast<uint32_t>(V1_0::SensorFlagBits::WAKE_UP);
    return info;
}

Return<V1_0::Result> OrientationSensorSubHal::initialize(
        const android::sp<IHalProxyCallback>& halProxyCallback) {
    mCallback = halProxyCallback;
    mOperationMode = V1_0::OperationMode::NORMAL;
    mActive = false;
    mLastTransform = -1;

    mStopThread = false;
    if (mPollThread.joinable()) {
        mStopThread = true;
        mPollCv.notify_all();
        mPollThread.join();
        mStopThread = false;
    }
    mPollThread = std::thread(&OrientationSensorSubHal::pollThread, this);
    return V1_0::Result::OK;
}

Return<void> OrientationSensorSubHal::getSensorsList_2_1(getSensorsList_2_1_cb _hidl_cb) {
    std::vector<V2_1::SensorInfo> sensors = {buildSensorInfo()};
    _hidl_cb(sensors);
    return Void();
}

Return<V1_0::Result> OrientationSensorSubHal::activate(int32_t sensorHandle, bool enabled) {
    if (sensorHandle != kSensorHandle) return V1_0::Result::BAD_VALUE;
    mActive = enabled;
    if (enabled) {
        mLastTransform = -1;
        mPollCv.notify_all();
    }
    return V1_0::Result::OK;
}

Return<V1_0::Result> OrientationSensorSubHal::batch(
        int32_t sensorHandle, int64_t, int64_t) {
    if (sensorHandle != kSensorHandle) return V1_0::Result::BAD_VALUE;
    return V1_0::Result::OK;
}

Return<V1_0::Result> OrientationSensorSubHal::flush(int32_t sensorHandle) {
    if (sensorHandle != kSensorHandle) return V1_0::Result::BAD_VALUE;
    if (!mActive) return V1_0::Result::BAD_VALUE;

    V2_1::Event ev;
    ev.sensorHandle = kSensorHandle;
    ev.sensorType = V2_1::SensorType::META_DATA;
    ev.u.meta.what = V1_0::MetaDataEventType::META_DATA_FLUSH_COMPLETE;
    ev.timestamp = android::elapsedRealtimeNano();

    std::vector<V2_1::Event> events = {ev};
    mCallback->postEvents(events, mCallback->createScopedWakelock(false));
    return V1_0::Result::OK;
}

Return<V1_0::Result> OrientationSensorSubHal::setOperationMode(V1_0::OperationMode mode) {
    mOperationMode = mode;
    return V1_0::Result::OK;
}

Return<void> OrientationSensorSubHal::debug(const hidl_handle& fd,
                                             const hidl_vec<hidl_string>&) {
    if (fd.getNativeHandle() == nullptr || fd->numFds < 1) return Void();
    FILE* out = fdopen(dup(fd->data[0]), "w");
    fprintf(out, "OrientationSensorSubHal:\n");
    fprintf(out, "  active: %s\n", mActive.load() ? "true" : "false");
    fprintf(out, "  lastTransform: %d\n", mLastTransform);
    fclose(out);
    return Void();
}

const std::string OrientationSensorSubHal::getName() {
    return "Fydroid-Orientation-Sensor-Subhal";
}

Return<void> OrientationSensorSubHal::registerDirectChannel(
        const V1_0::SharedMemInfo&,
        V2_0::ISensors::registerDirectChannel_cb cb) {
    cb(V1_0::Result::INVALID_OPERATION, -1);
    return Void();
}

Return<V1_0::Result> OrientationSensorSubHal::unregisterDirectChannel(int32_t) {
    return V1_0::Result::INVALID_OPERATION;
}

Return<void> OrientationSensorSubHal::configDirectReport(
        int32_t, int32_t, V1_0::RateLevel,
        V2_0::ISensors::configDirectReport_cb cb) {
    cb(V1_0::Result::INVALID_OPERATION, 0);
    return Void();
}

Return<V1_0::Result> OrientationSensorSubHal::injectSensorData_2_1(const V2_1::Event&) {
    return V1_0::Result::INVALID_OPERATION;
}

int32_t OrientationSensorSubHal::queryDisplayTransform() {
    if (mWaylandBinder == nullptr || !mWaylandBinder->isBinderAlive()) {
        mWaylandBinder = android::defaultServiceManager()->checkService(
                android::String16("Wayland"));
        if (mWaylandBinder == nullptr) return -1;
    }

    android::Parcel data, reply;
    data.writeInterfaceToken(kWaylandInterfaceDescriptor);

    android::status_t status = mWaylandBinder->transact(
            TRANSACTION_getDisplayLayout, data, &reply);
    if (status != android::OK) {
        ALOGW("getDisplayLayout transact failed: %d", status);
        mWaylandBinder = nullptr;
        return -1;
    }

    int32_t exception = reply.readExceptionCode();
    if (exception != 0) return -1;

    int32_t marker = reply.readInt32();
    if (marker == 0) return -1;

    // ArcDisplayLayout: size-prefixed
    int32_t layoutSize = reply.readInt32();
    if (layoutSize <= 0) return -1;

    reply.readInt32();  // layoutMode
    reply.readInt64();  // primaryPhysId

    int32_t numDisplays = reply.readInt32();
    if (numDisplays <= 0) return -1;

    int32_t displayMarker = reply.readInt32();
    if (displayMarker == 0) return -1;

    // ArcDisplay: size-prefixed
    int32_t displaySize = reply.readInt32();
    if (displaySize <= 0) return -1;

    reply.readInt64();  // physId
    reply.readInt32();  // globalPosX
    reply.readInt32();  // globalPosY
    reply.readInt32();  // width
    reply.readInt32();  // height
    int32_t transform = reply.readInt32();

    return transform;
}

void OrientationSensorSubHal::pollThread() {
    while (!mStopThread) {
        {
            std::unique_lock<std::mutex> lock(mPollMutex);
            mPollCv.wait_for(lock, std::chrono::milliseconds(500),
                             [this] { return mStopThread.load(); });
        }

        if (mStopThread) break;
        if (!mActive) continue;

        int32_t transform = queryDisplayTransform();
        if (transform < 0) continue;

        if (transform != mLastTransform) {
            mLastTransform = transform;

            V2_1::Event ev;
            ev.sensorHandle = kSensorHandle;
            ev.sensorType = V2_1::SensorType::DEVICE_ORIENTATION;
            ev.timestamp = android::elapsedRealtimeNano();
            ev.u.scalar = static_cast<float>(transform);

            std::vector<V2_1::Event> events = {ev};
            mCallback->postEvents(events, mCallback->createScopedWakelock(true));

            ALOGI("OrientationSensor: reported transform=%d", transform);
        }
    }
}

}  // namespace sensors
}  // namespace fydroid

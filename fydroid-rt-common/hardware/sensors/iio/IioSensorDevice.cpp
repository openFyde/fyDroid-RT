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

#include "IioSensorDevice.h"

#include <base/bind.h>
#include <base/logging.h>
#include <base/strings/string_number_conversions.h>
#include <utils/SystemClock.h>

#define LOG_TAG "IioSensorDevice"

namespace fydroid {
namespace sensors {

using ::android::hardware::sensors::V1_0::SensorFlagBits;
using SensorType = ::android::hardware::sensors::V2_1::SensorType;
using chromeos::sensors::mojom::DeviceType;

// Map Android sensor type to IIO channel prefix
static const char* channelPrefixForType(SensorType type) {
    switch (type) {
        case SensorType::ACCELEROMETER: return "accel";
        case SensorType::GYROSCOPE: return "anglvel";
        case SensorType::MAGNETIC_FIELD: return "magn";
        case SensorType::GRAVITY: return "gravity";
        default: return nullptr;
    }
}

static const char* sensorTypeName(SensorType type) {
    switch (type) {
        case SensorType::ACCELEROMETER: return "Accelerometer";
        case SensorType::GYROSCOPE: return "Gyroscope";
        case SensorType::MAGNETIC_FIELD: return "Magnetometer";
        case SensorType::LIGHT: return "Light";
        case SensorType::GRAVITY: return "Gravity";
        default: return "Unknown";
    }
}

IioSensorDevice::IioSensorDevice(
        int32_t iioDeviceId,
        int32_t sensorHandle,
        SensorType androidType,
        const std::vector<int32_t>& deviceTypes,
        IioSensorEventCallback* callback)
    : mIioDeviceId(iioDeviceId),
      mSensorHandle(sensorHandle),
      mAndroidType(androidType),
      mDeviceTypes(deviceTypes),
      mCallback(callback) {
}

IioSensorDevice::~IioSensorDevice() {
    if (mObserverBinding.is_bound()) {
        mObserverBinding.Close();
    }
}

void IioSensorDevice::initialize(
        mojo::InterfacePtr<chromeos::sensors::mojom::SensorService>& service) {
    service->GetDevice(mIioDeviceId, mojo::MakeRequest(&mDevice));

    mDevice.set_connection_error_handler(base::BindOnce(
            [](IioSensorDevice* self) {
                LOG(WARNING) << "IioSensorDevice " << self->mIioDeviceId
                             << ": SensorDevice connection lost";
            },
            base::Unretained(this)));

    // Set a generous timeout
    mDevice->SetTimeout(5000);

    // Query channel IDs first
    mDevice->GetAllChannelIds(base::BindRepeating(
            &IioSensorDevice::onChannelIds, base::Unretained(this)));
}

void IioSensorDevice::onChannelIds(const std::vector<std::string>& channelIds) {
    mChannelIds = channelIds;
    for (size_t i = 0; i < channelIds.size(); i++) {
        mChannelIndex[channelIds[i]] = static_cast<int32_t>(i);
    }

    // Find relevant channel indices based on sensor type
    const char* prefix = channelPrefixForType(mAndroidType);
    if (prefix) {
        std::string xChan = std::string(prefix) + "_x";
        std::string yChan = std::string(prefix) + "_y";
        std::string zChan = std::string(prefix) + "_z";

        auto itX = mChannelIndex.find(xChan);
        auto itY = mChannelIndex.find(yChan);
        auto itZ = mChannelIndex.find(zChan);

        if (itX != mChannelIndex.end()) mXIndex = itX->second;
        if (itY != mChannelIndex.end()) mYIndex = itY->second;
        if (itZ != mChannelIndex.end()) mZIndex = itZ->second;
    } else if (mAndroidType == SensorType::LIGHT) {
        auto it = mChannelIndex.find("illuminance");
        if (it != mChannelIndex.end()) mLightIndex = it->second;
    }

    auto itTs = mChannelIndex.find("timestamp");
    if (itTs != mChannelIndex.end()) mTimestampIndex = itTs->second;

    // Query device attributes: name, scale, location
    std::vector<std::string> attrNames = {"name", "scale", "location"};
    mDevice->GetAttributes(attrNames, base::BindRepeating(
            &IioSensorDevice::onAttributes, base::Unretained(this)));
}

void IioSensorDevice::onAttributes(
        const std::vector<base::Optional<std::string>>& values) {
    if (values.size() >= 1 && values[0].has_value()) {
        mDeviceName = values[0].value();
    }
    if (values.size() >= 2 && values[1].has_value()) {
        if (!base::StringToDouble(values[1].value(), &mScale)) {
            mScale = 1.0;
        }
    }
    if (values.size() >= 3 && values[2].has_value()) {
        mLocation = values[2].value();
    }

    LOG(INFO) << "IioSensorDevice " << mIioDeviceId << " (" << sensorTypeName(mAndroidType)
              << "): name=" << mDeviceName << " scale=" << mScale
              << " location=" << mLocation
              << " channels=" << mChannelIds.size();

    mReady = true;
}

SensorInfo IioSensorDevice::getSensorInfo() const {
    SensorInfo info;
    info.sensorHandle = mSensorHandle;

    // Include location in name if available (e.g. "Accelerometer (lid)")
    if (!mLocation.empty()) {
        info.name = std::string(sensorTypeName(mAndroidType)) + " (" + mLocation + ")";
    } else {
        info.name = sensorTypeName(mAndroidType);
    }

    info.vendor = "FydeOS";
    info.version = 1;
    info.type = mAndroidType;
    info.maxRange = 0;
    info.resolution = 0;
    info.power = 0.001f;
    info.flags = 0;

    switch (mAndroidType) {
        case SensorType::ACCELEROMETER:
            info.typeAsString = "android.sensor.accelerometer";
            info.maxRange = 78.4532f;  // ~8g
            info.resolution = 0.0023928226f;
            info.minDelay = 10000;  // 100 Hz
            info.flags = static_cast<uint32_t>(SensorFlagBits::CONTINUOUS_MODE);
            break;
        case SensorType::GYROSCOPE:
            info.typeAsString = "android.sensor.gyroscope";
            info.maxRange = 34.906586f;  // ~2000 deg/s
            info.resolution = 0.001065264f;
            info.minDelay = 10000;
            info.flags = static_cast<uint32_t>(SensorFlagBits::CONTINUOUS_MODE);
            break;
        case SensorType::MAGNETIC_FIELD:
            info.typeAsString = "android.sensor.magnetic_field";
            info.maxRange = 4911.994f;
            info.resolution = 0.15f;
            info.minDelay = 20000;  // 50 Hz
            info.flags = static_cast<uint32_t>(SensorFlagBits::CONTINUOUS_MODE);
            break;
        case SensorType::LIGHT:
            info.typeAsString = "android.sensor.light";
            info.maxRange = 100000.0f;
            info.resolution = 1.0f;
            info.minDelay = 0;
            info.flags = static_cast<uint32_t>(SensorFlagBits::ON_CHANGE_MODE);
            break;
        case SensorType::GRAVITY:
            info.typeAsString = "android.sensor.gravity";
            info.maxRange = 9.80665f;
            info.resolution = 0.001f;
            info.minDelay = 10000;
            info.flags = static_cast<uint32_t>(SensorFlagBits::CONTINUOUS_MODE);
            break;
        default:
            info.typeAsString = "";
            break;
    }

    info.maxDelay = 200000;  // 5 Hz minimum
    info.fifoReservedEventCount = 0;
    info.fifoMaxEventCount = 0;
    info.requiredPermission = "";

    return info;
}

void IioSensorDevice::activate(bool enabled) {
    mActive = enabled;
    if (enabled) {
        startSampling();
    } else {
        stopSampling();
    }
}

void IioSensorDevice::batch(int64_t samplingPeriodNs) {
    mFrequencyHz = 1.0e9 / static_cast<double>(samplingPeriodNs);
    if (mActive) {
        mDevice->SetFrequency(mFrequencyHz, base::BindRepeating(
                [](double resultFreq) {
                    LOG(INFO) << "IioSensorDevice: SetFrequency result=" << resultFreq;
                }));
    }
}

void IioSensorDevice::startSampling() {
    if (!mDevice) return;

    // Enable relevant channels
    std::vector<int32_t> channelsToEnable;
    if (mXIndex >= 0) channelsToEnable.push_back(mXIndex);
    if (mYIndex >= 0) channelsToEnable.push_back(mYIndex);
    if (mZIndex >= 0) channelsToEnable.push_back(mZIndex);
    if (mLightIndex >= 0) channelsToEnable.push_back(mLightIndex);
    if (mTimestampIndex >= 0) channelsToEnable.push_back(mTimestampIndex);

    if (channelsToEnable.empty()) {
        LOG(WARNING) << "IioSensorDevice " << mIioDeviceId << ": no channels to enable";
        return;
    }

    mDevice->SetChannelsEnabled(channelsToEnable, true, base::BindRepeating(
            [](int32_t deviceId, const std::vector<int32_t>& failed) {
                if (!failed.empty()) {
                    LOG(WARNING) << "IioSensorDevice " << deviceId
                                 << ": failed to enable " << failed.size() << " channels";
                }
            },
            mIioDeviceId));

    // Set frequency
    double freq = mFrequencyHz > 0 ? mFrequencyHz : 50.0;
    mDevice->SetFrequency(freq, base::BindRepeating(
            [](double resultFreq) {
                LOG(INFO) << "IioSensorDevice: frequency set to " << resultFreq << " Hz";
            }));

    // Start reading samples
    if (mObserverBinding.is_bound()) {
        mObserverBinding.Close();
    }
    mojo::InterfacePtr<chromeos::sensors::mojom::SensorDeviceSamplesObserver> observer;
    mObserverBinding.Bind(mojo::MakeRequest(&observer));
    mDevice->StartReadingSamples(std::move(observer));
}

void IioSensorDevice::stopSampling() {
    if (!mDevice) return;
    mDevice->StopReadingSamples();
    if (mObserverBinding.is_bound()) {
        mObserverBinding.Close();
    }
}

void IioSensorDevice::OnSampleUpdated(
        const base::flat_map<int32_t, int64_t>& sample) {
    if (!mActive || !mCallback) return;

    Event ev;
    ev.sensorHandle = mSensorHandle;
    ev.sensorType = mAndroidType;

    // Get timestamp from IIO or use system time
    if (mTimestampIndex >= 0) {
        auto tsIt = sample.find(mTimestampIndex);
        if (tsIt != sample.end()) {
            ev.timestamp = tsIt->second;
        } else {
            ev.timestamp = android::elapsedRealtimeNano();
        }
    } else {
        ev.timestamp = android::elapsedRealtimeNano();
    }

    switch (mAndroidType) {
        case SensorType::ACCELEROMETER:
        case SensorType::GYROSCOPE:
        case SensorType::MAGNETIC_FIELD:
        case SensorType::GRAVITY: {
            float x = 0, y = 0, z = 0;
            if (mXIndex >= 0) {
                auto it = sample.find(mXIndex);
                if (it != sample.end()) x = static_cast<float>(it->second) * mScale;
            }
            if (mYIndex >= 0) {
                auto it = sample.find(mYIndex);
                if (it != sample.end()) y = static_cast<float>(it->second) * mScale;
            }
            if (mZIndex >= 0) {
                auto it = sample.find(mZIndex);
                if (it != sample.end()) z = static_cast<float>(it->second) * mScale;
            }
            ev.u.vec3.x = x;
            ev.u.vec3.y = y;
            ev.u.vec3.z = z;
            ev.u.vec3.status =
                    android::hardware::sensors::V1_0::SensorStatus::ACCURACY_HIGH;
            break;
        }
        case SensorType::LIGHT: {
            float lux = 0;
            if (mLightIndex >= 0) {
                auto it = sample.find(mLightIndex);
                if (it != sample.end()) lux = static_cast<float>(it->second) * mScale;
            }
            ev.u.scalar = lux;
            break;
        }
        default:
            return;
    }

    std::vector<Event> events = {ev};
    mCallback->postSensorEvents(events, false);
}

void IioSensorDevice::OnErrorOccurred(
        chromeos::sensors::mojom::ObserverErrorType type) {
    LOG(WARNING) << "IioSensorDevice " << mIioDeviceId
                 << ": observer error " << static_cast<int>(type);

    // On read failures, try to restart sampling after a short delay
    if (mActive && type == chromeos::sensors::mojom::ObserverErrorType::READ_FAILED) {
        if (mObserverBinding.is_bound()) {
            mObserverBinding.Close();
        }
        // Re-start sampling
        startSampling();
    }
}

}  // namespace sensors
}  // namespace fydroid

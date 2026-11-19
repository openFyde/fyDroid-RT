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

#include <android/hardware/sensors/2.1/types.h>

#include <map>
#include <mutex>
#include <string>
#include <vector>

#include <mojo/public/cpp/bindings/binding.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <chromeos/components/sensors/mojom/sensor.mojom.h>

namespace fydroid {
namespace sensors {

using ::android::hardware::sensors::V2_1::Event;
using ::android::hardware::sensors::V2_1::SensorInfo;
using SensorType = ::android::hardware::sensors::V2_1::SensorType;

// Callback interface for posting sensor events to the framework
class IioSensorEventCallback {
public:
    virtual ~IioSensorEventCallback() = default;
    virtual void postSensorEvents(const std::vector<Event>& events, bool wakeup) = 0;
};

// Represents a single IIO sensor device.
// Handles channel enumeration, attribute reading, frequency control,
// and sample observation via Mojo SensorDevice interface.
class IioSensorDevice : public chromeos::sensors::mojom::SensorDeviceSamplesObserver {
public:
    IioSensorDevice(int32_t iioDeviceId,
                    int32_t sensorHandle,
                    SensorType androidType,
                    const std::vector<int32_t>& deviceTypes,
                    IioSensorEventCallback* callback);
    ~IioSensorDevice() override;

    // Initialize: bind device interface, query channels and attributes
    void initialize(mojo::InterfacePtr<chromeos::sensors::mojom::SensorService>& service);

    // Get Android SensorInfo (valid after initialize completes)
    SensorInfo getSensorInfo() const;

    // Whether initialization completed successfully
    bool isReady() const { return mReady; }

    // Activate / deactivate sampling
    void activate(bool enabled);

    // Set sampling period
    void batch(int64_t samplingPeriodNs);

    // SensorDeviceSamplesObserver
    void OnSampleUpdated(const base::flat_map<int32_t, int64_t>& sample) override;
    void OnErrorOccurred(chromeos::sensors::mojom::ObserverErrorType type) override;

private:
    void onChannelIds(const std::vector<std::string>& channelIds);
    void onAttributes(const std::vector<base::Optional<std::string>>& values);
    void startSampling();
    void stopSampling();

    int32_t mIioDeviceId;
    int32_t mSensorHandle;
    SensorType mAndroidType;
    std::vector<int32_t> mDeviceTypes;
    IioSensorEventCallback* mCallback;

    mojo::InterfacePtr<chromeos::sensors::mojom::SensorDevice> mDevice;
    mojo::Binding<chromeos::sensors::mojom::SensorDeviceSamplesObserver> mObserverBinding{this};

    // Channel info
    std::vector<std::string> mChannelIds;
    std::map<std::string, int32_t> mChannelIndex;  // channel name → index

    // Device attributes
    std::string mDeviceName;
    double mScale = 1.0;
    std::string mLocation;

    // State
    bool mReady = false;
    bool mActive = false;
    double mFrequencyHz = 0.0;

    // Index mapping for xyz channels
    int32_t mXIndex = -1;
    int32_t mYIndex = -1;
    int32_t mZIndex = -1;
    int32_t mLightIndex = -1;
    int32_t mTimestampIndex = -1;
};

}  // namespace sensors
}  // namespace fydroid

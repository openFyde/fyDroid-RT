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

#include "IioSensorBridge.h"

#include <arc/ArcBridgeSupport.h>
#include <base/bind.h>
#include <base/logging.h>

#define LOG_TAG "IioSensorBridge"

namespace fydroid {
namespace sensors {

using SensorType = ::android::hardware::sensors::V2_1::SensorType;
using chromeos::sensors::mojom::DeviceType;

// Map CrOS DeviceType to Android SensorType. Returns META_DATA for unsupported.
static SensorType mapDeviceType(DeviceType dt) {
    switch (dt) {
        case DeviceType::ACCEL: return SensorType::ACCELEROMETER;
        case DeviceType::ANGLVEL: return SensorType::GYROSCOPE;
        case DeviceType::MAGN: return SensorType::MAGNETIC_FIELD;
        case DeviceType::LIGHT: return SensorType::LIGHT;
        case DeviceType::GRAVITY: return SensorType::GRAVITY;
        default: return SensorType::META_DATA;
    }
}

// Check if a DeviceType is one we support
static bool isSupportedType(DeviceType dt) {
    return mapDeviceType(dt) != SensorType::META_DATA;
}

IioSensorBridge::IioSensorBridge(
        arc::MojoProcessSupport* mojoSupport,
        IioSensorEventCallback* callback)
    : mMojoSupport(mojoSupport),
      mCallback(callback) {
}

IioSensorBridge::~IioSensorBridge() {
    if (mInstanceBinding.is_bound()) mInstanceBinding.Close();
    if (mClientBinding.is_bound()) mClientBinding.Close();
}

void IioSensorBridge::start() {
    mBridgeSupport = new arc::ArcBridgeSupport(mMojoSupport);
    mBridgeSupport->requestArcBridgeProxyAsync(
            android::wp<arc::ArcService>(this),
            arc::mojom::ArcBridgeHost::kOnIioSensorInstanceReadyMinVersion);
    LOG(INFO) << "IioSensorBridge: connecting to ARC bridge (async)";
}

std::vector<std::pair<int32_t, SensorInfo>> IioSensorBridge::getSensors(int timeoutMs) {
    std::unique_lock<std::mutex> lock(mEnumMutex);
    mEnumCv.wait_for(lock, std::chrono::milliseconds(timeoutMs),
                     [this] { return mEnumDone; });

    std::vector<std::pair<int32_t, SensorInfo>> result;
    std::lock_guard<std::mutex> devLock(mDevicesMutex);
    for (auto& [handle, device] : mDevices) {
        if (device->isReady()) {
            result.emplace_back(handle, device->getSensorInfo());
        }
    }
    return result;
}

void IioSensorBridge::activate(int32_t sensorHandle, bool enabled) {
    std::lock_guard<std::mutex> lock(mDevicesMutex);
    auto it = mDevices.find(sensorHandle);
    if (it != mDevices.end()) {
        // Post to Mojo thread since device uses Mojo calls
        auto task = mMojoSupport->getThread()->getTaskRunner();
        task->PostTask(FROM_HERE, base::BindOnce(
                [](IioSensorDevice* dev, bool en) { dev->activate(en); },
                base::Unretained(it->second.get()), enabled));
    }
}

void IioSensorBridge::batch(int32_t sensorHandle, int64_t samplingPeriodNs) {
    std::lock_guard<std::mutex> lock(mDevicesMutex);
    auto it = mDevices.find(sensorHandle);
    if (it != mDevices.end()) {
        auto task = mMojoSupport->getThread()->getTaskRunner();
        task->PostTask(FROM_HERE, base::BindOnce(
                [](IioSensorDevice* dev, int64_t ns) { dev->batch(ns); },
                base::Unretained(it->second.get()), samplingPeriodNs));
    }
}

// ---------------------------------------------------------------------------
// ArcService overrides
// ---------------------------------------------------------------------------

void IioSensorBridge::ready(
        mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) {
    LOG(INFO) << "IioSensorBridge: ARC bridge ready";

    mojo::InterfacePtr<arc::mojom::IioSensorInstance> instancePtr;
    mInstanceBinding.Bind(mojo::MakeRequest(&instancePtr));

    mInstanceBinding.set_connection_error_handler(base::BindOnce(
            [](IioSensorBridge* self) {
                LOG(WARNING) << "IioSensorBridge: IioSensorInstance binding lost";
            },
            base::Unretained(this)));

    (*host)->OnIioSensorInstanceReady(std::move(instancePtr));
    LOG(INFO) << "IioSensorBridge: registered IioSensorInstance with Chrome OS";
}

void IioSensorBridge::versionMismatch(unsigned int version) {
    LOG(ERROR) << "IioSensorBridge: version mismatch - Chrome supports v"
               << version << ", need v"
               << arc::mojom::ArcBridgeHost::kOnIioSensorInstanceReadyMinVersion;

    // Signal enumeration done with no sensors
    {
        std::lock_guard<std::mutex> lock(mEnumMutex);
        mEnumDone = true;
    }
    mEnumCv.notify_all();
}

// ---------------------------------------------------------------------------
// IioSensorInstance overrides
// ---------------------------------------------------------------------------

void IioSensorBridge::Init(
        mojo::InterfacePtr<arc::mojom::IioSensorHost> host,
        const InitCallback& callback) {
    LOG(INFO) << "IioSensorBridge::Init - received IioSensorHost from Chrome";
    mSensorHost = std::move(host);

    // Register ourselves as SensorHalClient
    mojo::InterfacePtr<chromeos::sensors::mojom::SensorHalClient> clientPtr;
    mClientBinding.Bind(mojo::MakeRequest(&clientPtr));

    mClientBinding.set_connection_error_handler(base::BindOnce(
            [](IioSensorBridge* self) {
                LOG(WARNING) << "IioSensorBridge: SensorHalClient binding lost";
            },
            base::Unretained(this)));

    mSensorHost->RegisterSensorHalClient(std::move(clientPtr));

    callback.Run();
}

void IioSensorBridge::OnTabletModeChanged(bool isTabletModeOn) {
    LOG(INFO) << "IioSensorBridge: tablet mode "
              << (isTabletModeOn ? "ON" : "OFF");
}

// ---------------------------------------------------------------------------
// SensorHalClient overrides
// ---------------------------------------------------------------------------

void IioSensorBridge::SetUpChannel(
        mojo::InterfacePtr<chromeos::sensors::mojom::SensorService> service) {
    LOG(INFO) << "IioSensorBridge::SetUpChannel - received SensorService from Chrome";
    mSensorService = std::move(service);

    mSensorService.set_connection_error_with_reason_handler(base::BindOnce(
            [](IioSensorBridge* self, uint32_t reason, const std::string& desc) {
                LOG(WARNING) << "IioSensorBridge: SensorService disconnected, reason="
                             << reason << " desc=" << desc;
            },
            base::Unretained(this)));

    // Enumerate all IIO devices
    mSensorService->GetAllDeviceIds(base::BindRepeating(
            &IioSensorBridge::onAllDeviceIds, base::Unretained(this)));
}

// ---------------------------------------------------------------------------
// Device enumeration
// ---------------------------------------------------------------------------

void IioSensorBridge::onAllDeviceIds(
        const base::flat_map<int32_t,
            std::vector<int32_t>>& deviceMap) {
    LOG(INFO) << "IioSensorBridge: got " << deviceMap.size() << " IIO devices";

    int pendingCount = 0;

    for (auto& [deviceId, types] : deviceMap) {
        // Find the first supported type for this device
        SensorType androidType = SensorType::META_DATA;
        for (auto rawType : types) {
            DeviceType dt = static_cast<DeviceType>(rawType);
            if (isSupportedType(dt)) {
                androidType = mapDeviceType(dt);
                break;
            }
        }

        if (androidType == SensorType::META_DATA) {
            continue;  // Skip unsupported device types
        }

        int32_t handle;
        {
            std::lock_guard<std::mutex> lock(mDevicesMutex);
            handle = mNextHandle++;
            auto device = std::make_unique<IioSensorDevice>(
                    deviceId, handle, androidType, types, mCallback);
            device->initialize(mSensorService);
            mDevices[handle] = std::move(device);
        }
        pendingCount++;

        LOG(INFO) << "IioSensorBridge: device " << deviceId
                  << " -> handle " << handle
                  << " type " << static_cast<int>(androidType);
    }

    // Wait a bit for all devices to finish their async attribute queries,
    // then signal enumeration complete.
    // We use a delayed task since each device does async GetAllChannelIds + GetAttributes
    {
        std::lock_guard<std::mutex> lock(mEnumMutex);
        mPendingDevices = pendingCount;
    }

    if (pendingCount == 0) {
        LOG(INFO) << "IioSensorBridge: no supported IIO devices found";
        std::lock_guard<std::mutex> lock(mEnumMutex);
        mEnumDone = true;
        mEnumCv.notify_all();
        return;
    }

    // Post a delayed check — give devices 2 seconds to complete initialization
    auto taskRunner = mMojoSupport->getThread()->getTaskRunner();
    taskRunner->PostDelayedTask(
            FROM_HERE,
            base::BindOnce(&IioSensorBridge::checkEnumerationComplete,
                           base::Unretained(this)),
            base::TimeDelta::FromSeconds(2));
}

void IioSensorBridge::checkEnumerationComplete() {
    int readyCount = 0;
    {
        std::lock_guard<std::mutex> lock(mDevicesMutex);
        for (auto& [handle, device] : mDevices) {
            if (device->isReady()) readyCount++;
        }
    }

    LOG(INFO) << "IioSensorBridge: " << readyCount << " devices ready";

    {
        std::lock_guard<std::mutex> lock(mEnumMutex);
        mEnumDone = true;
    }
    mEnumCv.notify_all();
}

}  // namespace sensors
}  // namespace fydroid

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

#include <map>
#include <memory>
#include <mutex>
#include <vector>

#include <mojo/public/cpp/bindings/binding.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <utils/RefBase.h>

#include <arc/ArcService.h>
#include <arc/MojoProcessSupport.h>
#include <arc/arc_bridge.mojom.h>
#include <arc/iio_sensor.mojom.h>
#include <chromeos/components/sensors/mojom/cros_sensor_service.mojom.h>
#include <chromeos/components/sensors/mojom/sensor.mojom.h>

#include "IioSensorDevice.h"

namespace fydroid {
namespace sensors {

// Mojo bridge connecting Android sensor HAL to Chrome OS iioservice.
//
// Implements three Mojo interfaces:
//   ArcService — receives ArcBridgeHost proxy, registers IioSensorInstance
//   IioSensorInstance — Chrome calls Init() to give us IioSensorHost
//   SensorHalClient — Chrome calls SetUpChannel() to give us SensorService
//
// On receiving SensorService, enumerates all IIO devices and creates
// IioSensorDevice objects for supported types.
class IioSensorBridge : public arc::ArcService,
                        public arc::mojom::IioSensorInstance,
                        public chromeos::sensors::mojom::SensorHalClient {
public:
    explicit IioSensorBridge(arc::MojoProcessSupport* mojoSupport,
                             IioSensorEventCallback* callback);
    ~IioSensorBridge() override;

    // Start the Mojo bootstrap (async)
    void start();

    // Get discovered sensors (blocks until enumeration completes or timeout)
    std::vector<std::pair<int32_t, SensorInfo>> getSensors(int timeoutMs = 10000);

    // Forward activate/batch to the correct device
    void activate(int32_t sensorHandle, bool enabled);
    void batch(int32_t sensorHandle, int64_t samplingPeriodNs);

    // ArcService overrides
    void ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) override;
    void versionMismatch(unsigned int version) override;

    // IioSensorInstance overrides
    void Init(mojo::InterfacePtr<arc::mojom::IioSensorHost> host,
              const InitCallback& callback) override;
    void OnTabletModeChanged(bool isTabletModeOn) override;

    // SensorHalClient overrides
    void SetUpChannel(
            mojo::InterfacePtr<chromeos::sensors::mojom::SensorService> service) override;

private:
    void onAllDeviceIds(
            const base::flat_map<int32_t,
                std::vector<int32_t>>& deviceMap);
    void checkEnumerationComplete();

    arc::MojoProcessSupport* mMojoSupport;
    IioSensorEventCallback* mCallback;

    // Mojo bindings
    mojo::Binding<arc::mojom::IioSensorInstance> mInstanceBinding{this};
    mojo::Binding<chromeos::sensors::mojom::SensorHalClient> mClientBinding{this};

    // Chrome proxies
    mojo::InterfacePtr<arc::mojom::IioSensorHost> mSensorHost;
    mojo::InterfacePtr<chromeos::sensors::mojom::SensorService> mSensorService;

    // Discovered devices
    std::mutex mDevicesMutex;
    std::map<int32_t, std::unique_ptr<IioSensorDevice>> mDevices;
    int32_t mNextHandle = 1;  // Start from 1 (0 is reserved for orientation)

    // Enumeration synchronization
    std::mutex mEnumMutex;
    std::condition_variable mEnumCv;
    bool mEnumDone = false;
    int mPendingDevices = 0;

    // ArcBridgeSupport (prevent premature deletion)
    arc::ArcBridgeSupport* mBridgeSupport = nullptr;
};

}  // namespace sensors
}  // namespace fydroid

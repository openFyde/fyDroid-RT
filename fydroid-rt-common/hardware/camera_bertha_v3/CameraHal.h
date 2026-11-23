/*
 * Copyright (C) 2026 Fyde Innovations
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

#include <hardware/camera_common.h>
#include <memory>
#include <map>
#include <utils/Mutex.h>
#include <utils/StrongPointer.h>

#include "PipeHealth.h"

namespace base { class Thread; }

namespace fydroid { namespace camera {

class CameraInstanceImpl;
class CameraModuleDelegate;
class CameraModuleCallbacksImpl;
class VendorTagOpsDelegate;
class Camera3DeviceOpsDelegate;

// Top-level camera HAL implementation.
//
// Singleton — all camera_module_t callbacks dispatch through Instance().
// Init() bootstraps the Mojo channel; subsequent calls forward to
// CameraModuleDelegate which wraps the cros::mojom::CameraModuleProxy.
class CameraHal {
 public:
  static CameraHal& Instance();

  int Init();
  int GetNumberOfCameras();
  int GetCameraInfo(int camera_id, camera_info* info);
  int SetCallbacks(const camera_module_callbacks_t* cb);
  int SetTorchMode(const char* camera_id, bool enabled);
  int Open(const hw_module_t* module, const char* name, hw_device_t** device);
  void GetVendorTagOps(vendor_tag_ops_t* ops);

 private:
  CameraHal() = default;
  CameraHal(const CameraHal&) = delete;
  CameraHal& operator=(const CameraHal&) = delete;

  // Forwards a Chrome OS CameraModuleCallbacks event (delivered on the Mojo
  // thread) up to the Android framework's camera_module_callbacks_t, on
  // callback_thread_. The framework answers a PRESENT by calling back into the
  // HAL (getCameraInfo, via CameraModule::getDeviceVersion), and those calls
  // post to the Mojo thread and wait. Run on the Mojo thread itself, they
  // could only time out: the provider then cached a garbage device version,
  // named the camera "" ("parseDeviceName: Invalid device name ''"), and every
  // camera that appeared after startup -- a hot-plugged webcam, or the Fake
  // Camera HAL coming back when the real camera leaves -- was lost until ARC
  // restarted.
  void OnDeviceStatusChange(int camera_id, int new_status);
  void OnTorchModeStatusChange(int camera_id, int new_status);
  void DeliverDeviceStatusChange(int camera_id, int new_status);
  void DeliverTorchModeStatusChange(int camera_id, int new_status);

  android::Mutex lock_;
  // Must be android::sp (not unique_ptr) because arc::ArcService extends
  // android::RefBase. ArcBridgeSupport::promote() would otherwise be the only
  // strong reference and would free this object while WaitForModule blocks.
  android::sp<CameraInstanceImpl> instance_;
  std::unique_ptr<CameraModuleDelegate> module_delegate_;
  std::unique_ptr<CameraModuleCallbacksImpl> callbacks_impl_;
  // Runs framework callbacks in arrival order; see OnDeviceStatusChange.
  std::unique_ptr<base::Thread> callback_thread_;
  std::unique_ptr<VendorTagOpsDelegate> vendor_tag_delegate_;
  // Guards the one-shot GetVendorTagOps handshake, which happens before the
  // VendorTagOpsDelegate (and its own PipeHealth) exists.
  std::shared_ptr<PipeHealth> vendor_tag_pipe_ = std::make_shared<PipeHealth>();
  const camera_module_callbacks_t* module_callbacks_ = nullptr;
  bool callbacks_registered_ = false;
  bool initialized_ = false;
};

}} // namespace fydroid::camera

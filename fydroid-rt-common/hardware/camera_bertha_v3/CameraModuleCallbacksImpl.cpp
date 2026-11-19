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

#include "CameraModuleCallbacksImpl.h"

#include <android/log.h>

#include <mojo/public/cpp/bindings/associated_interface_ptr.h>

namespace fydroid { namespace camera {

CameraModuleCallbacksImpl::CameraModuleCallbacksImpl(
        StatusCallback device_status_cb, StatusCallback torch_status_cb)
        : device_status_cb_(std::move(device_status_cb)),
          torch_status_cb_(std::move(torch_status_cb)) {}

CameraModuleCallbacksImpl::~CameraModuleCallbacksImpl() {
    if (binding_.is_bound()) binding_.Close();
}

cros::mojom::CameraModuleCallbacksAssociatedPtrInfo
CameraModuleCallbacksImpl::BindOnMojoThread() {
    cros::mojom::CameraModuleCallbacksAssociatedPtrInfo ptr_info;
    // The receiver end is safe to bind before the remote (ptr_info) is sent
    // over the CameraModule pipe; that send is what establishes the
    // association. Mirrors CdmBridge::createCdmOnMojoThread.
    auto request = mojo::MakeRequest(&ptr_info);
    if (binding_.is_bound()) binding_.Close();
    binding_.Bind(std::move(request));
    return ptr_info;
}

void CameraModuleCallbacksImpl::CameraDeviceStatusChange(
        int32_t camera_id, cros::mojom::CameraDeviceStatus new_status) {
    __android_log_print(ANDROID_LOG_INFO, "CameraBerthaV3",
            "CameraDeviceStatusChange: camera %d -> status %d", camera_id,
            static_cast<int>(new_status));
    device_status_cb_.Run(camera_id, static_cast<int>(new_status));
}

void CameraModuleCallbacksImpl::TorchModeStatusChange(
        int32_t camera_id, cros::mojom::TorchModeStatus new_status) {
    torch_status_cb_.Run(camera_id, static_cast<int>(new_status));
}

}} // namespace fydroid::camera

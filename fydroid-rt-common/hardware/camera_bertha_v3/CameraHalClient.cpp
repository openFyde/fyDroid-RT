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

#include "CameraHalClient.h"

#include <base/logging.h>

namespace fydroid { namespace camera {

CameraHalClient::~CameraHalClient() {
    if (binding_.is_bound()) {
        binding_.Close();
    }
}

mojo::InterfacePtr<cros::mojom::CameraHalClient>
CameraHalClient::BindOnMojoThread() {
    mojo::InterfacePtr<cros::mojom::CameraHalClient> ptr;
    binding_.Bind(mojo::MakeRequest(&ptr));
    return ptr;
}

void CameraHalClient::SetUpChannel(
        mojo::InterfacePtr<cros::mojom::CameraModule> camera_module) {
    LOG(INFO) << "CameraHalClient::SetUpChannel - CameraModule received";
    RechannelHandler handler;
    {
        std::lock_guard<std::mutex> lock(mutex_);
        if (module_ready_ && rechannel_handler_) {
            handler = rechannel_handler_;
        } else {
            module_ = std::move(camera_module);
            module_ready_ = true;
        }
    }
    if (handler) {
        handler(std::move(camera_module));
        return;
    }
    cv_.notify_all();
}

void CameraHalClient::SetRechannelHandler(RechannelHandler handler) {
    std::lock_guard<std::mutex> lock(mutex_);
    rechannel_handler_ = std::move(handler);
}

bool CameraHalClient::WaitForModule(int timeout_seconds) {
    std::unique_lock<std::mutex> lock(mutex_);
    return cv_.wait_for(lock,
                        std::chrono::seconds(timeout_seconds),
                        [this] { return module_ready_; });
}

}} // namespace fydroid::camera

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

#include "CameraInstanceImpl.h"

#include <base/bind.h>
#include <base/logging.h>
#include <base/task_runner.h>

namespace fydroid { namespace camera {

CameraInstanceImpl::CameraInstanceImpl(arc::MojoProcessSupport* mojo_support)
        : mojo_support_(mojo_support),
          hal_client_(std::make_unique<CameraHalClient>()) {
}

CameraInstanceImpl::~CameraInstanceImpl() {
    if (instance_binding_.is_bound()) {
        instance_binding_.Close();
    }
}

void CameraInstanceImpl::Bootstrap() {
    bridge_support_ = std::make_unique<arc::ArcBridgeSupport>(mojo_support_);
    bridge_support_->requestArcBridgeProxyAsync(
            android::wp<arc::ArcService>(this),
            arc::mojom::ArcBridgeHost::kOnCameraInstanceReadyMinVersion);
    LOG(INFO) << "CameraInstanceImpl: ARC bridge bootstrap started";
}

bool CameraInstanceImpl::WaitForModule() {
    return hal_client_->WaitForModule(10);
}

// ---------------------------------------------------------------------------
// arc::ArcService overrides
// ---------------------------------------------------------------------------

void CameraInstanceImpl::ready(
        mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) {
    LOG(INFO) << "CameraInstanceImpl: ARC bridge ready - registering CameraInstance";

    mojo::InterfacePtr<arc::mojom::CameraInstance> instance_ptr;
    instance_binding_.Bind(mojo::MakeRequest(&instance_ptr));

    instance_binding_.set_connection_error_handler(
            base::BindOnce([](CameraInstanceImpl* self) {
                LOG(WARNING) << "CameraInstanceImpl: CameraInstance binding lost";
            }, this));

    (*host)->OnCameraInstanceReady(std::move(instance_ptr));
    LOG(INFO) << "CameraInstanceImpl: OnCameraInstanceReady sent";
}

void CameraInstanceImpl::versionMismatch(unsigned int version) {
    LOG(ERROR) << "CameraInstanceImpl: ARC bridge version mismatch - Chrome supports v"
               << version;
}

// ---------------------------------------------------------------------------
// arc::mojom::CameraInstance overrides
// ---------------------------------------------------------------------------

void CameraInstanceImpl::Init(
        mojo::InterfacePtr<arc::mojom::CameraHost> host_remote,
        const InitCallback& callback) {
    LOG(INFO) << "CameraInstanceImpl::Init - CameraHost received";

    // Store the CameraHost pipe so it stays alive after Init() returns.
    // If host_remote_ went out of scope Chrome would see a disconnection
    // and abort the SetUpChannel handshake.
    host_remote_ = std::move(host_remote);

    // Bind CameraHalClient on this Mojo thread, get an InterfacePtr.
    mojo::InterfacePtr<cros::mojom::CameraHalClient> client_ptr =
            hal_client_->BindOnMojoThread();

    // Register with Chrome. The result callback fires when Chrome acknowledges
    // registration. SetUpChannel() fires separately when Chrome is ready.
    host_remote_->RegisterCameraHalClient(
            std::move(client_ptr),
            base::BindRepeating([](int32_t result) {
                if (result == 0) {
                    LOG(INFO) << "CameraInstanceImpl: RegisterCameraHalClient succeeded";
                } else {
                    LOG(ERROR) << "CameraInstanceImpl: RegisterCameraHalClient failed: "
                               << result;
                }
            }));

    LOG(INFO) << "CameraInstanceImpl::Init done";
    callback.Run();
}

}} // namespace fydroid::camera

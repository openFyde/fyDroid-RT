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

#include <memory>
#include <mutex>
#include <condition_variable>

#include <utils/StrongPointer.h>
#include <mojo/public/cpp/bindings/binding.h>

#include <arc/ArcBridgeSupport.h>
#include <arc/ArcService.h>
#include <arc/MojoProcessSupport.h>
#include <arc/arc_bridge.mojom.h>
#include <arc/camera.mojom.h>

#include "CameraHalClient.h"

namespace fydroid { namespace camera {

// Implements arc::ArcService + arc::mojom::CameraInstance.
//
// Lifecycle:
//  1. Bootstrap() starts ArcBridgeSupport.
//  2. ready() is called when the ARC bridge connects; we register our
//     CameraInstance endpoint with Chrome via OnCameraInstanceReady.
//  3. Chrome calls Init() with a CameraHost remote; we then call
//     CameraHost::RegisterCameraHalClient to hand Chrome our CameraHalClient.
//  4. Chrome calls CameraHalClient::SetUpChannel with a CameraModule proxy.
//  5. WaitForModule() returns once the module is ready.
//
// All steps 2-4 run on the Mojo thread.  WaitForModule() is called from the
// camera framework's binder thread (never the Mojo thread).
class CameraInstanceImpl : public arc::ArcService,
                           public arc::mojom::CameraInstance {
 public:
  explicit CameraInstanceImpl(arc::MojoProcessSupport* mojo_support);
  ~CameraInstanceImpl() override;

  // Start the async bootstrap. Returns immediately.
  // Calls ready() asynchronously via ArcBridgeSupport when the bridge is up.
  void Bootstrap();

  // Block until Chrome has called SetUpChannel and given us a CameraModule.
  // Returns false on timeout (10 s).
  bool WaitForModule();

  // Returns the CameraModule proxy. Valid after WaitForModule() == true.
  cros::mojom::CameraModule* module() { return hal_client_->module(); }
  mojo::InterfacePtr<cros::mojom::CameraModule>& module_ptr() {
      return hal_client_->module_ptr();
  }

  void SetRechannelHandler(CameraHalClient::RechannelHandler handler) {
      hal_client_->SetRechannelHandler(std::move(handler));
  }

  // arc::ArcService overrides.
  void ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) override;
  void versionMismatch(unsigned int version) override;

  // arc::mojom::CameraInstance overrides.
  void Init(mojo::InterfacePtr<arc::mojom::CameraHost> host_remote,
            const InitCallback& callback) override;

 private:
  arc::MojoProcessSupport* mojo_support_;

  // Keep ArcBridgeSupport alive for the HAL lifetime.
  std::unique_ptr<arc::ArcBridgeSupport> bridge_support_;

  // Mojo binding for CameraInstance (receives Init() from Chrome).
  mojo::Binding<arc::mojom::CameraInstance> instance_binding_{this};

  // CameraHalClient: receives SetUpChannel() from Chrome.
  // Created once; reused if Chrome reconnects and calls SetUpChannel again.
  std::unique_ptr<CameraHalClient> hal_client_;

  // Keep the CameraHost pipe alive. If this goes out of scope Chrome
  // sees a disconnection and aborts the SetUpChannel handshake.
  mojo::InterfacePtr<arc::mojom::CameraHost> host_remote_;
};

}} // namespace fydroid::camera

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

#include <atomic>
#include <memory>
#include <mutex>
#include <condition_variable>

#include <utils/StrongPointer.h>
#include <mojo/public/cpp/bindings/binding.h>

#include <arc/ArcService.h>
#include <arc/MojoProcessSupport.h>
#include <arc/wake_lock.mojom.h>
#include <arc/arc_bridge.mojom.h>

namespace arc {

// Mojo-facing half of the wake lock service.
//
// Inherits arc::ArcService so libarcbridgeservice can manage the ARC bridge
// connection and call ready() / versionMismatch() through the vtable.
//
// Inherits arc::mojom::WakeLockInstance so Chrome OS can call Init() to hand
// us the WakeLockHost endpoint.
//
// Object layout:
//   offset  0: primary vtable ptr (ArcService sub-object)
//   offset  8: WakeLockInstance vtable ptr (secondary sub-object)
//   offset 16: MojoProcessSupport*
//   offset 296: android::RefBase (via virtual inheritance in ArcService)
class ArcWakeLockMojoService
        : public arc::ArcService,
          public arc::mojom::WakeLockInstance {
public:
    explicit ArcWakeLockMojoService(arc::MojoProcessSupport* mojo_support);
    ~ArcWakeLockMojoService() override;

    // --- arc::ArcService overrides ---

    // Called by libarcbridgeservice when the ARC bridge is up and version is OK.
    // |host| points to the InterfacePtr<ArcBridgeHost> held by ArcBridgeSupport.
    void ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) override;

    // Called by libarcbridgeservice when Chrome OS version < our requested version.
    void versionMismatch(unsigned int version) override;

    // --- arc::mojom::WakeLockInstance overrides ---

    // Chrome OS calls this after OnWakeLockInstanceReady(), handing us the
    // WakeLockHost endpoint to use for acquire/release calls.
    void Init(mojo::InterfacePtr<arc::mojom::WakeLockHost> host,
              const InitCallback& callback) override;

    // --- Called by ArcWakeLockServiceImpl (Binder thread) ---

    // Blocks until Init() has been called and host_ is ready.
    void waitForHost();

    // Calls WakeLockHost::AcquirePartialWakeLock() synchronously.
    // Posts to the Mojo thread and waits for the result.
    bool acquirePartialWakeLock();

    // Calls WakeLockHost::ReleasePartialWakeLock() synchronously.
    bool releasePartialWakeLock();

private:
    // Synchronously post a call to |host_| on the Mojo thread and wait for
    // the boolean result.
    bool callHostSync(bool acquire);

    arc::MojoProcessSupport* mojo_support_;

    // The WakeLockHost remote received from Chrome OS via Init().
    mojo::InterfacePtr<arc::mojom::WakeLockHost> host_;

    // Mojo binding for receiving Init() calls from Chrome OS.
    mojo::Binding<arc::mojom::WakeLockInstance> binding_{this};

    // Guards host_ready_ and host_cv_.
    std::mutex host_mutex_;
    std::condition_variable host_cv_;
    bool host_ready_ = false;
};

}  // namespace arc

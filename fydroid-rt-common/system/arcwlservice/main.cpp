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

#include <base/logging.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/ProcessState.h>

#include <arc/ArcBridgeSupport.h>
#include <arc/MojoProcessSupport.h>
#include <arc/arc_bridge.mojom.h>
#include <arc/wake_lock.mojom.h>

#include "ArcWakeLockMojoService.h"
#include "ArcWakeLockServiceImpl.h"

int main(int argc, char* argv[]) {
    (void)argc; (void)argv;

    // Initialize Android logging.
    android::ProcessState::self()->setThreadPoolMaxThreadCount(4);

    LOG(INFO) << "Starting wake lock service";

    // Get the MojoProcessSupport singleton. This starts the Mojo thread and
    // connects to the Chrome OS ARC bridge HIDL service.
    arc::MojoProcessSupport* mojo_support = arc::MojoProcessSupport::GetInstance();

    // Create the Mojo-facing service (implements WakeLockInstance for Chrome OS).
    android::sp<arc::ArcWakeLockMojoService> mojo_service(
            new arc::ArcWakeLockMojoService(mojo_support));

    // Create the Binder-facing service (implements IArcWakeLockService for system_server).
    android::sp<arc::ArcWakeLockServiceImpl> binder_service(
            new arc::ArcWakeLockServiceImpl(mojo_service));

    // Register the Binder service with Android's service manager.
    // system_server will find it under "arc_wake_lock_service".
    android::status_t status = android::defaultServiceManager()->addService(
            android::String16(arc::ArcWakeLockServiceImpl::getServiceName()),
            binder_service);
    if (status != android::OK) {
        LOG(FATAL) << "Failed to register arc_wake_lock_service: " << status;
    }
    LOG(INFO) << "Registered arc_wake_lock_service with service manager";

    // Connect to Chrome OS ARC bridge. When the connection is established,
    // libfydroid_arcbridgeservice calls mojo_service->ready() which registers our
    // WakeLockInstance endpoint. Chrome OS then calls Init() with WakeLockHost.
    arc::ArcBridgeSupport* bridge_support = new arc::ArcBridgeSupport(mojo_support);
    bridge_support->requestArcBridgeProxyAsync(
            android::wp<arc::ArcService>(mojo_service.get()),
            arc::mojom::ArcBridgeHost::kOnWakeLockInstanceReadyMinVersion);

    LOG(INFO) << "Connecting to Chrome OS ARC bridge (async)";

    // Join the Binder thread pool. This serves IArcWakeLockService calls from
    // system_server indefinitely. The Mojo thread (inside MojoProcessSupport)
    // runs concurrently to handle Mojo IPC.
    android::ProcessState::self()->startThreadPool();
    android::IPCThreadState::self()->joinThreadPool();

    LOG(INFO) << "Finishing wake lock service";
    return 0;
}

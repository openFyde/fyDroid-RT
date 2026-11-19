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
#include <arc/ime.mojom.h>

#include "ArcImeMojoService.h"
#include "ArcImeBinderService.h"

int main(int argc, char* argv[]) {
    (void)argc; (void)argv;

    android::ProcessState::self()->setThreadPoolMaxThreadCount(4);

    LOG(INFO) << "Starting arc_ime_bridge";

    // Get the MojoProcessSupport singleton. This starts the Mojo thread and
    // connects to the Chrome OS ARC bridge HIDL service.
    arc::MojoProcessSupport* mojo_support = arc::MojoProcessSupport::GetInstance();

    // Create the Mojo-facing service (implements ImeInstance for Chrome OS).
    android::sp<arc::ArcImeMojoService> mojo_service(
            new arc::ArcImeMojoService(mojo_support));

    // Create the Binder-facing service (raw BBinder for FydroidIME).
    android::sp<arc::ArcImeBinderService> binder_service(
            new arc::ArcImeBinderService(mojo_service));

    // Register the Binder service with Android's service manager.
    android::status_t status = android::defaultServiceManager()->addService(
            android::String16(arc::ArcImeBinderService::getServiceName()),
            binder_service);
    if (status != android::OK) {
        LOG(FATAL) << "Failed to register arc_ime: " << status;
    }
    LOG(INFO) << "Registered arc_ime with service manager";

    // Connect to Chrome OS ARC bridge. When the connection is established,
    // libarcbridgeservice calls mojo_service->ready() which registers our
    // ImeInstance endpoint. Chrome OS then calls Init() with ImeHost.
    arc::ArcBridgeSupport* bridge_support = new arc::ArcBridgeSupport(mojo_support);
    bridge_support->requestArcBridgeProxyAsync(
            android::wp<arc::ArcService>(mojo_service.get()),
            arc::mojom::ArcBridgeHost::kOnImeInstanceReadyMinVersion);

    LOG(INFO) << "Connecting to Chrome OS ARC bridge (async)";

    // Join the Binder thread pool. This serves arc_ime calls from
    // FydroidIME indefinitely. The Mojo thread (inside MojoProcessSupport)
    // runs concurrently to handle Mojo IPC.
    android::ProcessState::self()->startThreadPool();
    android::IPCThreadState::self()->joinThreadPool();

    LOG(INFO) << "Finishing arc_ime_bridge";
    return 0;
}

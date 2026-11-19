#include <binder/IPCThreadState.h>
#include <hidl/HidlTransportSupport.h>

#include <base/logging.h>

#include <arc/ArcBridgeSupport.h>
#include <arc/MojoProcessSupport.h>
#include <arc/arc_bridge.mojom.h>

#include "VideoService.h"

int main(int argc, char* argv[]) {
    (void)argc; (void)argv;

    LOG(INFO) << "Starting arcvideobridge service";

    // Disable background scheduling for latency-sensitive video work.
    android::IPCThreadState::self()->disableBackgroundScheduling(true);

    // Start the Mojo IPC thread and connect to the Chrome OS ARC bridge.
    arc::MojoProcessSupport* mojo_support = arc::MojoProcessSupport::GetInstance();

    // Create the VideoService (implements VideoInstance, VideoHost, and HIDL).
    android::sp<arc::VideoService> video_service(
            new arc::VideoService(mojo_support));

    // Connect to the Chrome OS ARC bridge. When ready, VideoService::ready()
    // will register VideoInstance via OnVideoInstanceReady.
    arc::ArcBridgeSupport* bridge_support = new arc::ArcBridgeSupport(mojo_support);
    bridge_support->requestArcBridgeProxyAsync(
            android::wp<arc::ArcService>(video_service.get()),
            arc::mojom::ArcBridgeHost::kOnVideoInstanceReadyMinVersion);

    LOG(INFO) << "VideoService: connecting to Chrome OS ARC bridge (async)";

    // Register the HIDL service so Android codec HALs can find it.
    android::status_t status = video_service->registerAsService("default");
    if (status != android::OK) {
        LOG(FATAL) << "VideoService: failed to register HIDL service: " << status;
        return 1;
    }
    LOG(INFO) << "VideoService: registered HIDL service 'default'";

    // Join the hwbinder thread pool to serve HIDL calls from codec HALs.
    android::hardware::configureRpcThreadpool(2, /*callerWillJoin=*/true);
    android::hardware::joinRpcThreadpool();

    LOG(INFO) << "VideoService: exiting";
    return 0;
}

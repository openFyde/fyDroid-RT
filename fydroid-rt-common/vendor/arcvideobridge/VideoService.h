#pragma once

#include <mutex>
#include <string>

#include <cutils/native_handle.h>
#include <hidl/HidlSupport.h>
#include <mojo/public/cpp/bindings/binding.h>
#include <mojo/public/cpp/bindings/binding_set.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <utils/RefBase.h>

#include <arc/ArcService.h>
#include <arc/MojoProcessSupport.h>
#include <arc/arc_bridge.mojom.h>
#include <arc/video.mojom.h>

#include <vendor/google_arc/arcvideobridge/1.0/IArcVideoBridgeService.h>

namespace arc {

// VideoService is the open-source replacement for arcvideobridge.
//
// It implements three interfaces simultaneously:
//
//   arc::ArcService — lets libfydroid_arcbridgeservice manage the ARC bridge
//     connection. When the bridge is ready, ready() registers our VideoInstance
//     with Chrome OS via OnVideoInstanceReady.
//
//   arc::mojom::VideoInstance — Chrome OS calls Init() to hand us a VideoHost
//     proxy. We store it in chrome_host_ for use by bootstrapVideoHost().
//
//   arc::mojom::VideoHost — codec HALs (media.c2, media.omx) connect to us as
//     a VideoHost server after calling bootstrapVideoHost(). We forward their
//     OnBootstrapVideoAcceleratorFactory() calls to Chrome OS's chrome_host_.
//
//   vendor::google_arc::arcvideobridge::V1_0::IArcVideoBridgeService — HIDL
//     interface exposed to Android's codec HALs. bootstrapVideoHost() creates a
//     Mojo channel and returns (FD, token) so the HAL can connect to us.
class VideoService
        : public arc::ArcService,
          public arc::mojom::VideoInstance,
          public arc::mojom::VideoHost,
          public vendor::google_arc::arcvideobridge::V1_0::IArcVideoBridgeService {
public:
    explicit VideoService(arc::MojoProcessSupport* mojo_support);
    ~VideoService() override;

    // --- arc::ArcService overrides ---

    // Called when the ARC bridge is ready. Registers VideoInstance with
    // Chrome OS via OnVideoInstanceReady.
    void ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) override;

    // Called when Chrome OS version < our required version.
    void versionMismatch(unsigned int version) override;

    // --- arc::mojom::VideoInstance overrides ---

    // Chrome OS calls this after OnVideoInstanceReady to hand us a VideoHost.
    void Init(mojo::InterfacePtr<arc::mojom::VideoHost> host_remote,
              const InitCallback& callback) override;

    // --- arc::mojom::VideoHost overrides ---

    // Codec HALs call this to get a new VideoAcceleratorFactory channel.
    // We forward the call to Chrome OS's chrome_host_.
    void OnBootstrapVideoAcceleratorFactory(
            const OnBootstrapVideoAcceleratorFactoryCallback& callback) override;
    void CreateVideoAcceleratorFactory(
            const CreateVideoAcceleratorFactoryCallback& callback) override;

    // --- vendor::google_arc::arcvideobridge::V1_0::IArcVideoBridgeService overrides ---

    // Creates a Mojo channel (PlatformChannel + OutgoingInvitation), binds
    // ourselves as a VideoHost server on the local end, and returns the remote
    // end (FD + token) to the codec HAL so it can connect.
    android::hardware::Return<void> bootstrapVideoHost(
            bootstrapVideoHost_cb _hidl_cb) override;

    // Returns the Chrome OS VideoHost version (obtained via QueryVersion in Init).
    android::hardware::Return<uint32_t> hostVersion() override;

private:
    // Data exchanged between the HIDL thread and the Mojo thread during
    // bootstrapVideoHost(). Guarded by result_mutex.
    struct BootstrapResult {
        android::hardware::hidl_handle channel_handle;
        android::hardware::hidl_string pipe_name;
        bool done = false;
        std::mutex mutex;
        std::condition_variable cv;
    };

    // Runs on the Mojo thread. Creates PlatformChannel + OutgoingInvitation,
    // binds a VideoHost binding to the local pipe end, and fills |result|.
    void bootstrapVideoHostOnMojoThread(BootstrapResult* result);

    arc::MojoProcessSupport* mojo_support_;

    // Binding for VideoInstance — Chrome OS calls Init() on this.
    mojo::Binding<arc::mojom::VideoInstance> instance_binding_{this};

    // Binding set for VideoHost — one binding per codec HAL connection.
    mojo::BindingSet<arc::mojom::VideoHost> host_bindings_;

    // VideoHost proxy received from Chrome OS via Init().
    mojo::InterfacePtr<arc::mojom::VideoHost> chrome_host_;

    // Chrome OS VideoHost version, obtained via QueryVersion in Init().
    uint32_t host_version_ = 0;

    // Guards chrome_host_ readiness.
    std::mutex host_mutex_;
    std::condition_variable host_cv_;
    bool host_ready_ = false;
};

}  // namespace arc

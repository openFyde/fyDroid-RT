// ArcBridgeSupport — client-side bootstrap for connecting to the arcbridge
// HIDL service and obtaining an ArcBridgeHost Mojo channel.
//
// Used by arcwlservice and arcvideobridge to connect to Chrome OS via the
// ARC bridge.
#pragma once

#include <utils/StrongPointer.h>
#include <hidl/HidlSupport.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>

#include <arc/ArcService.h>
#include <arc/arc_bridge.mojom.h>

namespace arc {

class MojoProcessSupport;

class ArcBridgeSupport {
public:
    explicit ArcBridgeSupport(MojoProcessSupport* mojo_support);
    ~ArcBridgeSupport();

    // Posts bootstrap to the Mojo thread. When the connection is ready and
    // version is compatible, calls service->ready(&host_). On version mismatch,
    // calls service->versionMismatch(version).
    void requestArcBridgeProxyAsync(android::wp<ArcService> service,
                                    unsigned int version);

private:
    // Inner class to handle HIDL death notifications without exposing
    // ArcBridgeSupport itself to RefBase lifetime management.
    // ArcBridgeSupport is heap-allocated with raw new; passing it directly
    // to linkToDeath() creates a temporary sp<> that destroys the object
    // when the strong count drops to zero.
    class DeathRecipient : public android::hardware::hidl_death_recipient {
    public:
        explicit DeathRecipient(ArcBridgeSupport* parent) : parent_(parent) {}
        void serviceDied(uint64_t cookie,
                         const android::wp<android::hidl::base::V1_0::IBase>& who) override;
    private:
        ArcBridgeSupport* parent_;
    };

    void onServiceDied();
    void bootstrapOnMojoThread(android::wp<ArcService> service,
                               unsigned int version);

    MojoProcessSupport* mojo_support_;
    mojo::InterfacePtr<arc::mojom::ArcBridgeHost> host_;
    android::sp<DeathRecipient> death_recipient_;

    // Saved for re-bootstrap on HIDL death.
    android::wp<ArcService> service_;
    unsigned int version_ = 0;
};

}  // namespace arc

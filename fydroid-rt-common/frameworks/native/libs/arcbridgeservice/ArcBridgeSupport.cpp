#include <arc/ArcBridgeSupport.h>
#include <arc/MojoProcessSupport.h>

#include <base/bind.h>
#include <base/logging.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <mojo/public/cpp/system/invitation.h>
#include <mojo/public/cpp/platform/platform_channel_endpoint.h>

#include <vendor/google_arc/arcbridge/1.0/IArcBridgeService.h>

#include <arc/arc_bridge.mojom.h>

namespace arc {

ArcBridgeSupport::ArcBridgeSupport(MojoProcessSupport* mojo_support)
    : mojo_support_(mojo_support),
      death_recipient_(new DeathRecipient(this)) {
}

ArcBridgeSupport::~ArcBridgeSupport() = default;

void ArcBridgeSupport::requestArcBridgeProxyAsync(
        android::wp<ArcService> service, unsigned int version) {
    // Save the request. The actual bootstrap may happen later when
    // the Mojo system is ready — proprietary services call this during
    // early boot before MojoProcessSupport is initialized.
    service_ = service;
    version_ = version;

    if (!mojo_support_) {
        LOG(INFO) << "ArcBridgeSupport: mojo not initialized, deferring bridge request v"
                  << version;
        return;
    }

    auto* thread = mojo_support_->getThread();
    if (!thread) {
        LOG(INFO) << "ArcBridgeSupport: mojo thread not ready, deferring bridge request v"
                  << version;
        return;
    }

    thread->getTaskRunner()->PostTask(
        FROM_HERE,
        base::BindOnce(&ArcBridgeSupport::bootstrapOnMojoThread,
                       base::Unretained(this),
                       service, version));
}

void ArcBridgeSupport::DeathRecipient::serviceDied(
        uint64_t /* cookie */,
        const android::wp<android::hidl::base::V1_0::IBase>& /* who */) {
    parent_->onServiceDied();
}

void ArcBridgeSupport::onServiceDied() {
    LOG(WARNING) << "ArcBridgeSupport: arcbridge HIDL service died, re-bootstrapping";

    // Reset the existing Mojo connection.
    host_.reset();

    if (!mojo_support_ || !mojo_support_->getThread()) {
        LOG(ERROR) << "ArcBridgeSupport: cannot re-bootstrap, mojo not available";
        return;
    }

    // Re-bootstrap on the Mojo thread.
    mojo_support_->getThread()->getTaskRunner()->PostTask(
        FROM_HERE,
        base::BindOnce(&ArcBridgeSupport::bootstrapOnMojoThread,
                       base::Unretained(this),
                       service_, version_));
}

void ArcBridgeSupport::bootstrapOnMojoThread(
        android::wp<ArcService> service, unsigned int version) {
    LOG(INFO) << "ArcBridgeSupport: bootstrapping on Mojo thread, version=" << version;

    // Step 1: Get the HIDL service
    auto hidl_service =
        ::vendor::google_arc::arcbridge::V1_0::IArcBridgeService::getService("default");
    if (!hidl_service) {
        LOG(ERROR) << "ArcBridgeSupport: IArcBridgeService not available";
        return;
    }
    LOG(INFO) << "ArcBridgeSupport: got IArcBridgeService";

    // Register death recipient so we re-bootstrap if arcbridge crashes.
    hidl_service->linkToDeath(death_recipient_, 0 /* cookie */);

    // Step 2: Call getMojoBootstrapChannel
    using MojoBootstrapChannel = ::vendor::google_arc::arcbridge::V1_0::MojoBootstrapChannel;
    android::hardware::hidl_handle channel_handle;
    std::vector<uint64_t> pipe_names;
    bool got_channel = false;

    auto ret = hidl_service->getMojoBootstrapChannel(version,
        [&](const MojoBootstrapChannel& channel) {
            if (channel.channelHandle.getNativeHandle() &&
                channel.channelHandle.getNativeHandle()->numFds > 0) {
                channel_handle = channel.channelHandle;
                pipe_names.assign(channel.pipeNames.begin(), channel.pipeNames.end());
                got_channel = true;
            }
        });

    if (!ret.isOk() || !got_channel) {
        LOG(ERROR) << "ArcBridgeSupport: getMojoBootstrapChannel failed";
        return;
    }

    int fd = dup(channel_handle->data[0]);
    if (fd < 0) {
        PLOG(ERROR) << "ArcBridgeSupport: failed to dup channel fd";
        return;
    }
    LOG(INFO) << "ArcBridgeSupport: got channel fd=" << fd
              << " pipeNames count=" << pipe_names.size();

    // Step 3: Accept the Mojo invitation
    mojo::IncomingInvitation invitation = mojo::IncomingInvitation::Accept(
        mojo::PlatformChannelEndpoint(mojo::PlatformHandle(base::ScopedFD(fd))));

    // Step 4: Extract the ArcBridgeHost message pipe using the first pipe name
    if (pipe_names.empty()) {
        LOG(ERROR) << "ArcBridgeSupport: no pipe names in bootstrap channel";
        return;
    }
    mojo::ScopedMessagePipeHandle pipe =
        invitation.ExtractMessagePipe(pipe_names[0]);
    if (!pipe.is_valid()) {
        LOG(ERROR) << "ArcBridgeSupport: failed to extract message pipe";
        return;
    }

    // Step 5: Bind the InterfacePtr (on the current Mojo thread)
    host_.Bind(mojo::InterfacePtrInfo<arc::mojom::ArcBridgeHost>(
        std::move(pipe), version));

    // Step 6: Query version and notify the service
    host_.QueryVersion(base::BindRepeating(
        [](android::wp<ArcService> svc,
           mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host,
           unsigned int requested_version,
           uint32_t remote_version) {
            LOG(INFO) << "ArcBridgeSupport: remote version=" << remote_version
                      << " requested=" << requested_version;
            android::sp<ArcService> strong = svc.promote();
            if (!strong) {
                LOG(ERROR) << "ArcBridgeSupport: service destroyed";
                return;
            }
            if (remote_version >= requested_version) {
                LOG(INFO) << "ArcBridgeSupport: calling ready() on service";
                strong->ready(host);
                LOG(INFO) << "ArcBridgeSupport: ready() returned";
            } else {
                strong->versionMismatch(remote_version);
            }
        },
        service, &host_, version));
}

}  // namespace arc

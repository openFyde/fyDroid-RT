#include "VideoService.h"

#include <base/bind.h>
#include <base/logging.h>
#include <base/rand_util.h>
#include <base/strings/string_number_conversions.h>
#include <base/task_runner.h>
#include <mojo/public/cpp/platform/platform_channel.h>
#include <mojo/public/cpp/system/invitation.h>
#include <mojo/public/cpp/system/message_pipe.h>

namespace arc {
namespace {

constexpr size_t kMojoThreadOffsetInMojoProcessSupport = 0x120;

// MojoProcessSupport's layout is ABI-sensitive, and MojoThread is embedded at
// offset 0x120 for compatibility with existing ARC++ binaries. Use that stable
// offset directly so arcvideobridge does not depend on the newer
// MojoProcessSupport::getThread() symbol at link/load time.
MojoThread* GetEmbeddedMojoThread(MojoProcessSupport* mojo_support) {
    return reinterpret_cast<MojoThread*>(
            reinterpret_cast<char*>(mojo_support) +
            kMojoThreadOffsetInMojoProcessSupport);
}

}  // namespace

// ---------------------------------------------------------------------------
// Constructor / Destructor
// ---------------------------------------------------------------------------

VideoService::VideoService(arc::MojoProcessSupport* mojo_support)
        : mojo_support_(mojo_support) {
}

VideoService::~VideoService() {
    if (instance_binding_.is_bound()) {
        instance_binding_.Close();
    }
}

// ---------------------------------------------------------------------------
// arc::ArcService overrides
// ---------------------------------------------------------------------------

void VideoService::ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) {
    LOG(INFO) << "VideoService: ARC bridge ready";

    mojo::InterfacePtr<arc::mojom::VideoInstance> instance_ptr;
    instance_binding_.Bind(mojo::MakeRequest(&instance_ptr));

    instance_binding_.set_connection_error_handler(
            base::BindOnce([](VideoService* self) {
                LOG(WARNING) << "VideoService: Mojo VideoInstance binding lost";
                std::lock_guard<std::mutex> lock(self->host_mutex_);
                self->chrome_host_.reset();
                self->host_ready_ = false;
            }, this));

    (*host)->OnVideoInstanceReady(std::move(instance_ptr));
    LOG(INFO) << "VideoService: registered VideoInstance with Chrome OS";
}

void VideoService::versionMismatch(unsigned int version) {
    LOG(ERROR) << "VideoService: version mismatch - Chrome OS supports up to v"
               << version << ", we require v"
               << arc::mojom::VideoInstance::Version_;
}

// ---------------------------------------------------------------------------
// arc::mojom::VideoInstance overrides
// ---------------------------------------------------------------------------

void VideoService::Init(mojo::InterfacePtr<arc::mojom::VideoHost> host_remote,
                        const InitCallback& callback) {
    LOG(INFO) << "VideoService::Init - VideoHost connected";

    {
        std::lock_guard<std::mutex> lock(host_mutex_);
        chrome_host_ = std::move(host_remote);
    }

    // Query Chrome OS VideoHost version.
    chrome_host_.QueryVersion(base::BindRepeating(
            [](VideoService* self, uint32_t version) {
                LOG(INFO) << "VideoService: Chrome OS VideoHost version = " << version;
                {
                    std::lock_guard<std::mutex> lock(self->host_mutex_);
                    self->host_version_ = version;
                    self->host_ready_ = true;
                }
                self->host_cv_.notify_all();
            },
            this));

    callback.Run();
}

// ---------------------------------------------------------------------------
// arc::mojom::VideoHost overrides (for codec HALs)
// ---------------------------------------------------------------------------

void VideoService::OnBootstrapVideoAcceleratorFactory(
        const OnBootstrapVideoAcceleratorFactoryCallback& callback) {
    LOG(INFO) << "VideoService::OnBootstrapVideoAcceleratorFactory - forwarding to Chrome OS";

    std::lock_guard<std::mutex> lock(host_mutex_);
    if (!chrome_host_) {
        LOG(ERROR) << "VideoService: Chrome OS VideoHost not ready";
        callback.Run(mojo::ScopedHandle(), "");
        return;
    }

    chrome_host_->OnBootstrapVideoAcceleratorFactory(callback);
}

void VideoService::CreateVideoAcceleratorFactory(
        const CreateVideoAcceleratorFactoryCallback& callback) {
    LOG(INFO) << "VideoService::CreateVideoAcceleratorFactory - forwarding to Chrome OS";

    std::lock_guard<std::mutex> lock(host_mutex_);
    if (!chrome_host_) {
        LOG(ERROR) << "VideoService: Chrome OS VideoHost not ready";
        callback.Run(arc::mojom::VideoAcceleratorFactoryPtr());
        return;
    }

    chrome_host_->CreateVideoAcceleratorFactory(callback);
}

// ---------------------------------------------------------------------------
// IArcVideoBridgeService overrides (HIDL, called by codec HALs)
// ---------------------------------------------------------------------------

android::hardware::Return<void> VideoService::bootstrapVideoHost(
        bootstrapVideoHost_cb _hidl_cb) {
    LOG(INFO) << "VideoService::bootstrapVideoHost";

    // Wait for the Chrome OS VideoHost to be available.
    {
        std::unique_lock<std::mutex> lock(host_mutex_);
        host_cv_.wait(lock, [this] { return host_ready_; });
    }

    // Post to Mojo thread to create the channel, block until done.
    BootstrapResult result;

    auto task_runner = GetEmbeddedMojoThread(mojo_support_)->getTaskRunner();
    task_runner->PostTask(
            FROM_HERE,
            base::BindOnce(&VideoService::bootstrapVideoHostOnMojoThread,
                           // Safe: VideoService outlives this call (held by sp<>)
                           base::Unretained(this),
                           &result));

    {
        std::unique_lock<std::mutex> lock(result.mutex);
        result.cv.wait(lock, [&result] { return result.done; });
    }

    _hidl_cb({result.channel_handle, result.pipe_name});
    return android::hardware::Void();
}

android::hardware::Return<uint32_t> VideoService::hostVersion() {
    return host_version_;
}

// ---------------------------------------------------------------------------
// Private: runs on Mojo thread
// ---------------------------------------------------------------------------

void VideoService::bootstrapVideoHostOnMojoThread(BootstrapResult* result) {
    DCHECK(GetEmbeddedMojoThread(mojo_support_)->isCurrentThread());

    // Create a bidirectional platform channel.
    mojo::PlatformChannel channel;

    // Generate a random token for the message pipe.
    uint64_t rand_token = base::RandUint64();
    std::string token = base::NumberToString(rand_token);

    // Create the OutgoingInvitation and attach a message pipe with the token.
    mojo::OutgoingInvitation invitation;
    mojo::ScopedMessagePipeHandle local_pipe =
            invitation.AttachMessagePipe(token);

    // Send invitation to the codec HAL (it will accept via the remote endpoint).
    // pid=0: no specific process tracking needed.
    mojo::OutgoingInvitation::Send(
            std::move(invitation),
            /*target_process=*/0,
            channel.TakeLocalEndpoint(),
            base::BindRepeating([](const std::string& err) {
                LOG(ERROR) << "VideoService: OutgoingInvitation error: " << err;
            }));

    // Bind ourselves as VideoHost server on the local pipe end.
    host_bindings_.AddBinding(
            this,
            mojo::InterfaceRequest<arc::mojom::VideoHost>(std::move(local_pipe)));

    // Extract the remote endpoint FD to return to the codec HAL.
    mojo::PlatformChannelEndpoint remote_endpoint = channel.TakeRemoteEndpoint();
    mojo::PlatformHandle remote_handle = remote_endpoint.TakePlatformHandle();

    // Wrap the FD in a native_handle_t for HIDL transport.
    int fd = remote_handle.ReleaseFD();
    native_handle_t* native_handle = native_handle_create(1, 0);
    if (!native_handle) {
        LOG(ERROR) << "VideoService: native_handle_create failed";
        std::lock_guard<std::mutex> lock(result->mutex);
        result->done = true;
        result->cv.notify_one();
        return;
    }
    native_handle->data[0] = fd;

    {
        std::lock_guard<std::mutex> lock(result->mutex);
        result->channel_handle.setTo(native_handle, /*shouldOwn=*/true);
        result->pipe_name = token;
        result->done = true;
    }
    result->cv.notify_one();

    LOG(INFO) << "VideoService: bootstrapped VideoHost, token=" << token;
}

}  // namespace arc

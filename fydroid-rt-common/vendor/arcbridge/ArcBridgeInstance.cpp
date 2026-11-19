#include "ArcBridgeInstance.h"
#include "ArcBridgeHostForwarder.h"

#include <cerrno>
#include <chrono>
#include <cstring>

#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>

#include <base/bind.h>
#include <base/logging.h>
#include <base/rand_util.h>
#include <base/strings/string_piece.h>

#include <mojo/public/cpp/platform/platform_channel.h>
#include <mojo/public/cpp/platform/platform_channel_endpoint.h>
#include <mojo/public/cpp/platform/platform_handle.h>
#include <mojo/public/cpp/system/invitation.h>

#include <vendor/google_arc/arcbridge/1.0/types.h>

#include <arc/MojoProcessSupport.h>

namespace arc {

static constexpr char kArcBridgeSocketPath[] =
    "/var/run/chrome/arc_bridge.sock";

// MojoInitData protocol version 0 wire format:
//   offset 0: uint8_t protocol_version (= 0)
//   offset 1: uint8_t token_length (= 32)
//   offset 2: char[32] token (hex string)
// Total: 34 bytes
// Plus 1 file descriptor via SCM_RIGHTS (PlatformChannel remote endpoint).
static constexpr size_t kMojoInitDataSize = 34;
static constexpr uint8_t kProtocolVersion0 = 0;
static constexpr uint8_t kTokenLength = 32;

// ---------------------------------------------------------------------------
// Socket connection and MojoInitData receive
// ---------------------------------------------------------------------------

// Connect to the Chrome OS arc_bridge socket.
static int connectToSocket(const char* path) {
    int fd = socket(AF_UNIX, SOCK_STREAM | SOCK_CLOEXEC, 0);
    if (fd < 0) {
        PLOG(ERROR) << "socket() failed";
        return -1;
    }

    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    strncpy(addr.sun_path, path, sizeof(addr.sun_path) - 1);

    if (connect(fd, reinterpret_cast<struct sockaddr*>(&addr), sizeof(addr)) < 0) {
        PLOG(ERROR) << "connect() to " << path << " failed";
        close(fd);
        return -1;
    }

    return fd;
}

// Receive MojoInitData + PlatformChannel FD from Chrome OS via recvmsg.
// Returns the token string and platform FD on success.
static bool receiveMojoInitData(int socket_fd,
                                std::string* out_token,
                                int* out_platform_fd) {
    // Buffer for MojoInitData
    uint8_t buf[256];
    struct iovec iov;
    iov.iov_base = buf;
    iov.iov_len = sizeof(buf);

    // Control message buffer for SCM_RIGHTS (one FD)
    union {
        struct cmsghdr cm;
        char control[CMSG_SPACE(sizeof(int))];
    } cmsg_buf;

    struct msghdr msg;
    memset(&msg, 0, sizeof(msg));
    msg.msg_iov = &iov;
    msg.msg_iovlen = 1;
    msg.msg_control = cmsg_buf.control;
    msg.msg_controllen = sizeof(cmsg_buf.control);

    ssize_t bytes = recvmsg(socket_fd, &msg, 0);
    if (bytes < static_cast<ssize_t>(kMojoInitDataSize)) {
        LOG(ERROR) << "recvmsg: got " << bytes << " bytes, expected >= "
                   << kMojoInitDataSize;
        return false;
    }

    // Parse MojoInitData
    uint8_t protocol_version = buf[0];
    uint8_t token_length = buf[1];

    if (protocol_version != kProtocolVersion0) {
        LOG(WARNING) << "Unexpected protocol version " << (int)protocol_version
                     << ", proceeding anyway";
    }
    if (token_length != kTokenLength) {
        LOG(ERROR) << "Unexpected token length " << (int)token_length;
        return false;
    }

    *out_token = std::string(reinterpret_cast<char*>(buf + 2), token_length);

    // Extract FD from control message
    struct cmsghdr* cmsg = CMSG_FIRSTHDR(&msg);
    if (!cmsg || cmsg->cmsg_level != SOL_SOCKET || cmsg->cmsg_type != SCM_RIGHTS) {
        LOG(ERROR) << "No SCM_RIGHTS in control message";
        return false;
    }

    *out_platform_fd = *reinterpret_cast<int*>(CMSG_DATA(cmsg));
    if (*out_platform_fd < 0) {
        LOG(ERROR) << "Invalid platform FD received";
        return false;
    }

    LOG(INFO) << "Received MojoInitData: protocol_version="
              << (int)protocol_version
              << " token_length=" << (int)token_length
              << " platform_fd=" << *out_platform_fd;
    return true;
}

// ---------------------------------------------------------------------------
// ArcBridgeInstance
// ---------------------------------------------------------------------------

ArcBridgeInstance::ArcBridgeInstance(MojoThread* mojo_thread)
    : mojo_thread_(mojo_thread) {
    // MojoThread runs a single-threaded message loop, so the cast is safe.
    task_runner_ = static_cast<base::SingleThreadTaskRunner*>(
        mojo_thread_->getTaskRunner().get());
}

ArcBridgeInstance::~ArcBridgeInstance() = default;

bool ArcBridgeInstance::connect() {
    // Reset state so connect() is re-runnable after disconnect().
    {
        std::lock_guard<std::mutex> lock(connect_mutex_);
        connect_done_ = false;
        connect_success_ = false;
    }
    // Post connectOnMojoThread to the Mojo thread and wait.
    task_runner_->PostTask(
        FROM_HERE,
        base::BindOnce(&ArcBridgeInstance::connectOnMojoThread,
                       base::Unretained(this)));

    std::unique_lock<std::mutex> lock(connect_mutex_);
    connect_cv_.wait(lock, [this] { return connect_done_; });
    return connect_success_;
}

void ArcBridgeInstance::setOnDisconnect(std::function<void()> on_disconnect) {
    std::lock_guard<std::mutex> lock(disconnect_mutex_);
    on_disconnect_ = std::move(on_disconnect);
}

void ArcBridgeInstance::disconnect() {
    task_runner_->PostTask(
        FROM_HERE,
        base::BindOnce(&ArcBridgeInstance::disconnectOnMojoThread,
                       base::Unretained(this)));
}

void ArcBridgeInstance::disconnectOnMojoThread() {
    // Forwarders hold mojo::Receiver objects which can only be destroyed on
    // the Mojo thread; that's why this method only runs here.
    {
        std::lock_guard<std::mutex> lock(forwarders_mutex_);
        forwarders_.clear();
    }
    if (root_host_.is_bound()) {
        // Detach our error handler before resetting, otherwise reset() would
        // fire it again and we'd recurse into onRootHostDisconnected().
        root_host_.set_connection_error_handler(base::OnceClosure());
        root_host_.reset();
    }
    host_version_ = 0;
    LOG(INFO) << "Mojo state cleared, ready for reconnect";
}

void ArcBridgeInstance::onRootHostDisconnected() {
    LOG(ERROR) << "Root ArcBridgeHost connection lost";
    std::function<void()> cb;
    {
        std::lock_guard<std::mutex> lock(disconnect_mutex_);
        cb = on_disconnect_;
    }
    if (cb) {
        cb();
    }
}

void ArcBridgeInstance::connectOnMojoThread() {
    // Step 1: Connect to Chrome OS socket
    int socket_fd = connectToSocket(kArcBridgeSocketPath);
    if (socket_fd < 0) {
        LOG(ERROR) << "Failed to connect to Chrome OS";
        std::lock_guard<std::mutex> lock(connect_mutex_);
        connect_done_ = true;
        connect_success_ = false;
        connect_cv_.notify_all();
        return;
    }
    LOG(INFO) << "Connected to " << kArcBridgeSocketPath;

    // Step 2: Receive MojoInitData + PlatformChannel FD
    std::string token;
    int platform_fd = -1;
    if (!receiveMojoInitData(socket_fd, &token, &platform_fd)) {
        LOG(ERROR) << "Failed to receive MojoInitData";
        close(socket_fd);
        std::lock_guard<std::mutex> lock(connect_mutex_);
        connect_done_ = true;
        connect_success_ = false;
        connect_cv_.notify_all();
        return;
    }
    // Socket is no longer needed after receiving init data
    close(socket_fd);

    // Step 3: Accept Mojo invitation using the received PlatformChannel FD
    auto endpoint = mojo::PlatformChannelEndpoint(
        mojo::PlatformHandle(base::ScopedFD(platform_fd)));
    auto invitation = mojo::IncomingInvitation::Accept(std::move(endpoint));

    // Step 4: Extract ArcBridgeHost message pipe using the token
    auto pipe = invitation.ExtractMessagePipe(
        base::StringPiece(token.data(), token.size()));
    if (!pipe.is_valid()) {
        LOG(ERROR) << "Failed to extract ArcBridgeHost pipe with token";
        std::lock_guard<std::mutex> lock(connect_mutex_);
        connect_done_ = true;
        connect_success_ = false;
        connect_cv_.notify_all();
        return;
    }

    // Step 5: Bind as InterfacePtr<ArcBridgeHost> — our root connection to Chrome OS
    root_host_.Bind(
        mojo::InterfacePtrInfo<arc::mojom::ArcBridgeHost>(
            std::move(pipe), arc::mojom::ArcBridgeHost::Version_));

    root_host_.set_connection_error_handler(base::BindOnce(
        &ArcBridgeInstance::onRootHostDisconnected,
        base::Unretained(this)));

    // Step 6: Query Chrome OS version
    root_host_.QueryVersion(base::BindRepeating(
        [](ArcBridgeInstance* self, uint32_t version) {
            LOG(INFO) << "Chrome OS ArcBridgeHost version: " << version;
            self->host_version_ = version;
        }, base::Unretained(this)));

    LOG(INFO) << "Mojo invitation accepted, ArcBridgeHost pipe bound";

    // Signal success
    {
        std::lock_guard<std::mutex> lock(connect_mutex_);
        connect_done_ = true;
        connect_success_ = true;
    }
    connect_cv_.notify_all();
}

bool ArcBridgeInstance::createMojoBootstrapChannel(
    uint32_t version, int caller_pid,
    vendor::google_arc::arcbridge::V1_0::MojoBootstrapChannel* result) {

    LOG(INFO) << "createMojoBootstrapChannel: version=" << version
              << " pid=" << caller_pid;

    // Block the HIDL binder thread until root_host_ is bound. Without this,
    // a binder client that races a reconnect window (disconnect()+connect()
    // in progress) would walk away with a forwarder that references an
    // unbound root_host_ — its messages would silently never reach Chrome
    // until the client noticed the dead pipe and bootstrapped again. The
    // client-visible latency for that miss is one full per-instance Mojo
    // round trip plus the bridgeSupport re-bootstrap (~100s of ms).
    // Waiting here keeps the new forwarder bound to the post-reconnect
    // root_host_ on the first try.
    //
    // The predicate requires both connect_done_ AND connect_success_ so we
    // don't wake up during the "tried-and-failed" gap between main's
    // retry attempts (connect_done_ briefly toggles true with
    // connect_success_=false before main calls connect() again, which
    // resets connect_done_ to false). wait_for caps the total block at
    // 30 s so a wedged Chrome doesn't tie up a binder thread forever — at
    // that point we fail the HIDL call and let the client retry through
    // its own backoff.
    {
        std::unique_lock<std::mutex> lock(connect_mutex_);
        if (!connect_cv_.wait_for(lock, std::chrono::seconds(30),
                [this] { return connect_done_ && connect_success_; })) {
            LOG(WARNING) << "createMojoBootstrapChannel: gave up waiting for "
                         << "root_host_ to become ready";
            return false;
        }
    }

    // Step 1: Generate random uint64 pipe names
    // The client uses version to determine expected interface version.
    // We create one message pipe for the ArcBridgeHost channel.
    std::vector<uint64_t> pipe_names;
    pipe_names.push_back(base::RandUint64());

    // Step 2: Create OutgoingInvitation and attach message pipes
    mojo::OutgoingInvitation invitation;
    std::vector<mojo::ScopedMessagePipeHandle> local_handles;
    for (uint64_t name : pipe_names) {
        local_handles.push_back(invitation.AttachMessagePipe(name));
    }

    // Step 3: Create PlatformChannel pair
    mojo::PlatformChannel channel;

    // Step 4: Send invitation via local endpoint
    mojo::OutgoingInvitation::Send(
        std::move(invitation),
        base::kNullProcessHandle,
        channel.TakeLocalEndpoint());

    // Step 5: Set up the per-client forwarder on the Mojo thread
    auto forwarder = std::make_unique<ArcBridgeHostForwarder>(
        &root_host_, &host_version_, caller_pid);

    // We need to bind the forwarder to the first pipe (ArcBridgeHost channel)
    // on the Mojo thread since Mojo Binding is thread-affine.
    mojo::ScopedMessagePipeHandle forwarder_pipe = std::move(local_handles[0]);

    // Use a synchronization mechanism to wait for the Mojo thread to finish
    std::mutex setup_mutex;
    std::condition_variable setup_cv;
    bool setup_done = false;

    task_runner_->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](ArcBridgeInstance* self,
               std::unique_ptr<ArcBridgeHostForwarder> fwd,
               mojo::ScopedMessagePipeHandle pipe,
               std::mutex* mtx, std::condition_variable* cv, bool* done) {
                fwd->Bind(std::move(pipe), self->task_runner_);
                {
                    std::lock_guard<std::mutex> lock(self->forwarders_mutex_);
                    self->forwarders_.push_back(std::move(fwd));
                }
                {
                    std::lock_guard<std::mutex> lock(*mtx);
                    *done = true;
                }
                cv->notify_one();
            },
            base::Unretained(this),
            std::move(forwarder),
            std::move(forwarder_pipe),
            &setup_mutex, &setup_cv, &setup_done));

    // Wait for setup to complete
    {
        std::unique_lock<std::mutex> lock(setup_mutex);
        setup_cv.wait(lock, [&setup_done] { return setup_done; });
    }

    // Step 6: Populate HIDL result
    // The remote endpoint FD goes to the client
    auto remote_endpoint = channel.TakeRemoteEndpoint();
    auto remote_handle = remote_endpoint.TakePlatformHandle();
    int remote_fd = remote_handle.TakeFD().release();

    // Create native_handle_t to wrap the FD for HIDL
    native_handle_t* nh = native_handle_create(1, 0);
    nh->data[0] = remote_fd;

    result->channelHandle.setTo(nh, true /* owns */);
    result->pipeNames.resize(pipe_names.size());
    for (size_t i = 0; i < pipe_names.size(); i++) {
        result->pipeNames[i] = pipe_names[i];
    }

    LOG(INFO) << "Remote connection for process " << caller_pid
              << " established (fd=" << remote_fd << ")";
    return true;
}

} // namespace arc

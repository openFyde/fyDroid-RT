#include <arc/MojoProcessSupport.h>
#include <arc/ArcBridgeSupport.h>

#include <string.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>

#include <base/at_exit.h>
#include <base/command_line.h>
#include <base/logging.h>
#include <base/no_destructor.h>
#include <mojo/core/embedder/embedder.h>
#include <mojo/core/embedder/scoped_ipc_support.h>

namespace arc {

MojoProcessSupport::MojoProcessSupport() {
    // Zero ALL ABI padding so any offsets read by proprietary code
    // return NULL/0 rather than garbage.
    memset(_abi_pad_0, 0, sizeof(_abi_pad_0));
    bridge_support_ = nullptr;
    memset(_abi_pad_1, 0, sizeof(_abi_pad_1));
    // mojo_thread_ is default-constructed by C++ (offset 0x120 on 64-bit, 0x64 on 32-bit).

    // Some libchrome internals (e.g. logging, ICU) expect these to exist.
    // Leaked intentionally — the process runs until termination.
    new base::AtExitManager();
    if (!base::CommandLine::InitializedForCurrentProcess()) {
        base::CommandLine::Init(0, nullptr);
    }

    mojo::core::Init();
    mojo_thread_.start();

    // ScopedIPCSupport is needed for Mojo IPC. It's leaked intentionally —
    // the process runs until termination.
    new mojo::core::ScopedIPCSupport(
        mojo_thread_.getTaskRunner(),
        mojo::core::ScopedIPCSupport::ShutdownPolicy::FAST);

    // Proprietary binaries (libbluetooth, arctraceservice, arcvideobridge, etc.)
    // read bridge_support_ at offset 0x58 to obtain an ArcBridgeSupport*.
    // Create it here so the pointer is valid. The constructor only allocates a
    // DeathRecipient — it does NOT connect to arc_bridge.sock. The actual bridge
    // connection happens later when the binary calls requestArcBridgeProxyAsync().
    // Processes that don't need the bridge (e.g. c2@1.0-service-mojo) simply
    // never call requestArcBridgeProxyAsync, so no blocking occurs.
    bridge_support_ = new ArcBridgeSupport(this);
}

MojoProcessSupport::~MojoProcessSupport() = default;

MojoProcessSupport* MojoProcessSupport::GetInstance() {
    static base::NoDestructor<MojoProcessSupport> instance;
    return instance.get();
}

MojoThread* MojoProcessSupport::getThread() {
    return &mojo_thread_;
}

base::ScopedFD MojoProcessSupport::connectToUnixDomainSocket(
        const base::FilePath& path) {
    int fd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (fd < 0) {
        PLOG(ERROR) << "Failed to create socket";
        return base::ScopedFD();
    }

    struct sockaddr_un addr = {};
    addr.sun_family = AF_UNIX;
    strncpy(addr.sun_path, path.value().c_str(), sizeof(addr.sun_path) - 1);

    if (connect(fd, reinterpret_cast<struct sockaddr*>(&addr), sizeof(addr)) < 0) {
        PLOG(ERROR) << "Failed to connect to " << path.value();
        close(fd);
        return base::ScopedFD();
    }

    return base::ScopedFD(fd);
}

}  // namespace arc

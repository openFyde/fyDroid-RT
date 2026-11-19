// MojoThread — runs a TYPE_IO message loop for Mojo IPC.
//
// IMPORTANT: sizeof(MojoThread) is ABI-sensitive. The original MojoThread was
// ~96 bytes. Proprietary binaries (arctraceservice) construct MojoProcessSupport
// on the stack using the original sizeof. base::Thread is heap-allocated to
// keep MojoThread small.
#pragma once

#include <memory>
#include <base/threading/thread.h>
#include <base/single_thread_task_runner.h>

namespace arc {

class MojoThread {
public:
    MojoThread();
    ~MojoThread();

    void start();
    void stop();
    void join();
    bool isCurrentThread();
    void waitForMessageLoop();
    scoped_refptr<base::SingleThreadTaskRunner> getTaskRunner();

private:
    // Heap-allocated to keep sizeof(MojoThread) small (~96 bytes).
    // The original MojoThread used std::thread/pthread directly.
    std::unique_ptr<base::Thread> thread_;
    char _abi_pad[96 - sizeof(std::unique_ptr<base::Thread>)];
};

}  // namespace arc

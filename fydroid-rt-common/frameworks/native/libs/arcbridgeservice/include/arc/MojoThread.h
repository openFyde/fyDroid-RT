/*
 * Copyright (C) 2024 Fyde Innovations
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

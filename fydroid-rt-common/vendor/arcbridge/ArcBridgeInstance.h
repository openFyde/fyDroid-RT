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

// Core arcbridge logic: connects to Chrome OS, distributes Mojo channels.
//
// Lifecycle:
//   1. Construct with MojoThread pointer
//   2. (optional) setOnDisconnect() — fires when the Mojo root_host is lost
//   3. Call connect() — blocks until Chrome OS connection established
//   4. HIDL service calls createMojoBootstrapChannel() for each client
//   5. On Mojo disconnect: the disconnect callback fires on the Mojo thread.
//      The caller should call disconnect() and then connect() again to reset
//      state. connect()/disconnect() may be called repeatedly across the
//      lifetime of the process; per-client forwarders from previous epochs
//      are dropped during disconnect().
#pragma once

#include <functional>
#include <memory>
#include <mutex>
#include <condition_variable>
#include <vector>

#include <utils/RefBase.h>
#include <base/memory/scoped_refptr.h>
#include <base/single_thread_task_runner.h>
#include <base/task_runner.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <mojo/public/cpp/system/message_pipe.h>
#include <hidl/HidlSupport.h>

#include <arc/arc_bridge.mojom.h>

// Forward-declare HIDL types
namespace vendor { namespace google_arc { namespace arcbridge { namespace V1_0 {
    struct MojoBootstrapChannel;
}}}}

namespace arc {

class MojoThread;
class ArcBridgeHostForwarder;

class ArcBridgeInstance : public android::RefBase {
public:
    explicit ArcBridgeInstance(MojoThread* mojo_thread);
    ~ArcBridgeInstance() override;

    // Connect to Chrome OS. Blocks until connection is established or fails.
    // Returns true on success. Re-runnable after disconnect().
    bool connect();

    // Tear down the current Mojo root_host_ and all per-client forwarders.
    // Safe to call from any thread; the actual cleanup runs on the Mojo
    // thread. After this returns, connect() can be called again.
    void disconnect();

    // Register a callback that fires (once, on the Mojo thread) when the root
    // ArcBridgeHost connection to Chrome OS is lost. The callback should be
    // re-armable: after disconnect() + connect() it is invoked again on the
    // next disconnect.
    void setOnDisconnect(std::function<void()> on_disconnect);

    // Create a Mojo bootstrap channel for a client process.
    // Called from HIDL binder thread. |version| is the Mojo interface version
    // the client wants. |caller_pid| identifies the client.
    // Returns the channel via |result|.
    bool createMojoBootstrapChannel(
        uint32_t version, int caller_pid,
        vendor::google_arc::arcbridge::V1_0::MojoBootstrapChannel* result);

private:
    // Runs on the Mojo thread.
    void connectOnMojoThread();

    // Called on the Mojo thread after OutgoingInvitation::Send.
    // Binds the forwarder to the client's message pipe.
    void setupForwarderOnMojoThread(
        std::unique_ptr<ArcBridgeHostForwarder> forwarder,
        mojo::ScopedMessagePipeHandle pipe);

    // Runs on the Mojo thread; drops root_host_ and clears forwarders_.
    void disconnectOnMojoThread();

    // Mojo connection_error_handler entry point. Dispatches to the
    // disconnect callback while still on the Mojo thread.
    void onRootHostDisconnected();

    MojoThread* mojo_thread_;
    scoped_refptr<base::SingleThreadTaskRunner> task_runner_;

    // Root Mojo connection to Chrome OS
    mojo::InterfacePtr<arc::mojom::ArcBridgeHost> root_host_;
    uint32_t host_version_ = 0;

    // Connection synchronization (reset before each connect())
    std::mutex connect_mutex_;
    std::condition_variable connect_cv_;
    bool connect_done_ = false;
    bool connect_success_ = false;

    // Disconnect signaling
    std::mutex disconnect_mutex_;
    std::function<void()> on_disconnect_;

    // Per-client forwarders (owned, destroyed on Mojo thread)
    std::mutex forwarders_mutex_;
    std::vector<std::unique_ptr<ArcBridgeHostForwarder>> forwarders_;
};

} // namespace arc

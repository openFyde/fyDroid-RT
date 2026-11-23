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

#include "ArcWakeLockMojoService.h"

#include <mutex>
#include <condition_variable>

#include <base/bind.h>
#include <base/logging.h>
#include <base/task_runner.h>

namespace arc {

// ---------------------------------------------------------------------------
// Constructor / Destructor
// ---------------------------------------------------------------------------

ArcWakeLockMojoService::ArcWakeLockMojoService(arc::MojoProcessSupport* mojo_support)
        : mojo_support_(mojo_support) {
}

ArcWakeLockMojoService::~ArcWakeLockMojoService() {
    if (binding_.is_bound()) {
        binding_.Close();
    }
}

// ---------------------------------------------------------------------------
// arc::ArcService overrides
// ---------------------------------------------------------------------------

void ArcWakeLockMojoService::ready(
        mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) {
    LOG(INFO) << "ArcWakeLockMojoService: ARC bridge ready";

    // Register our WakeLockInstance endpoint with Chrome OS.
    // Chrome OS will respond by calling our Init() with the WakeLockHost remote.
    mojo::InterfacePtr<arc::mojom::WakeLockInstance> instance_ptr;
    binding_.Bind(mojo::MakeRequest(&instance_ptr));

    binding_.set_connection_error_handler(base::BindOnce([](ArcWakeLockMojoService* self) {
        LOG(WARNING) << "ArcWakeLockMojoService: Mojo binding lost";
        std::lock_guard<std::mutex> lock(self->host_mutex_);
        self->host_.reset();
        self->host_ready_ = false;
    }, this));

    // Call OnWakeLockInstanceReady via the generated proxy.
    // With libfydroid_arcbridge, the generated ArcBridgeHost has correct vtable
    // layout, so normal virtual dispatch works.
    (*host)->OnWakeLockInstanceReady(std::move(instance_ptr));
    LOG(INFO) << "ArcWakeLockMojoService: registered WakeLockInstance with Chrome OS";
}

void ArcWakeLockMojoService::versionMismatch(unsigned int version) {
    LOG(ERROR) << "ArcWakeLockMojoService: version mismatch - Chrome OS supports up to v"
               << version << ", we require v" << arc::mojom::WakeLockInstance::Version_;
}

// ---------------------------------------------------------------------------
// arc::mojom::WakeLockInstance overrides
// ---------------------------------------------------------------------------

void ArcWakeLockMojoService::Init(
        mojo::InterfacePtr<arc::mojom::WakeLockHost> host,
        const InitCallback& callback) {
    LOG(INFO) << "ArcWakeLockMojoService::Init - WakeLockHost connected";

    {
        std::lock_guard<std::mutex> lock(host_mutex_);
        host_ = std::move(host);
        host_.set_connection_error_handler(base::BindOnce([](ArcWakeLockMojoService* self) {
            LOG(WARNING) << "ArcWakeLockMojoService: WakeLockHost connection lost";
            std::lock_guard<std::mutex> lk(self->host_mutex_);
            self->host_.reset();
            self->host_ready_ = false;
        }, this));
        host_ready_ = true;
    }
    host_cv_.notify_all();
    callback.Run();
}

// ---------------------------------------------------------------------------
// Called by ArcWakeLockServiceImpl (Binder thread)
// ---------------------------------------------------------------------------

void ArcWakeLockMojoService::waitForHost() {
    std::unique_lock<std::mutex> lock(host_mutex_);
    host_cv_.wait(lock, [this] { return host_ready_; });
}

bool ArcWakeLockMojoService::acquirePartialWakeLock() {
    return callHostSync(true);
}

bool ArcWakeLockMojoService::releasePartialWakeLock() {
    return callHostSync(false);
}

// ---------------------------------------------------------------------------
// Synchronous Mojo call helper
// ---------------------------------------------------------------------------

bool ArcWakeLockMojoService::callHostSync(bool acquire) {
    // Shared state for the cross-thread result transfer.
    auto result_mutex = std::make_shared<std::mutex>();
    auto result_cv = std::make_shared<std::condition_variable>();
    auto result_ready = std::make_shared<bool>(false);
    auto result_value = std::make_shared<bool>(false);

    // Build the callback that will run on the Mojo thread when Chrome OS replies.
    auto on_result = base::BindRepeating(
        [](std::shared_ptr<std::mutex> mtx,
           std::shared_ptr<std::condition_variable> cv,
           std::shared_ptr<bool> ready,
           std::shared_ptr<bool> value,
           bool r) {
            {
                std::lock_guard<std::mutex> lock(*mtx);
                *value = r;
                *ready = true;
            }
            cv->notify_one();
        },
        result_mutex, result_cv, result_ready, result_value);

    // Get the Mojo thread's task runner via clean accessor.
    auto task_runner = mojo_support_->getThread()->getTaskRunner();

    // Capture the InterfacePtr by address (safe: host_ lives as long as this object).
    auto* host_ptr = &host_;
    bool is_acquire = acquire;

    task_runner->PostTask(
        FROM_HERE,
        base::BindOnce(
            [](mojo::InterfacePtr<arc::mojom::WakeLockHost>* host,
               bool do_acquire,
               base::RepeatingCallback<void(bool)> cb) {
                if (!*host) {
                    LOG(ERROR) << "callHostSync: WakeLockHost not connected";
                    cb.Run(false);
                    return;
                }
                if (do_acquire) {
                    (*host)->AcquirePartialWakeLock(cb);
                } else {
                    (*host)->ReleasePartialWakeLock(cb);
                }
            },
            host_ptr, is_acquire, std::move(on_result)));

    // Wait for the Mojo reply.
    std::unique_lock<std::mutex> lock(*result_mutex);
    result_cv->wait(lock, [&result_ready] { return *result_ready; });
    return *result_value;
}

}  // namespace arc

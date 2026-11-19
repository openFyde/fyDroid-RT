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

#include "ArcWakeLockServiceImpl.h"
#include <base/logging.h>

namespace arc {

ArcWakeLockServiceImpl::ArcWakeLockServiceImpl(
        android::sp<ArcWakeLockMojoService> mojo_service)
        : mojo_service_(std::move(mojo_service)) {}

ArcWakeLockServiceImpl::~ArcWakeLockServiceImpl() = default;

// static
const char* ArcWakeLockServiceImpl::getServiceName() {
    return "arc_wake_lock_service";
}

android::binder::Status ArcWakeLockServiceImpl::acquirePartialWakeLock(bool* result) {
    VLOG(1) << "ArcWakeLockServiceImpl::acquirePartialWakeLock";
    mojo_service_->waitForHost();
    *result = mojo_service_->acquirePartialWakeLock();
    if (!*result) {
        LOG(WARNING) << "AcquirePartialWakeLock returned false from Chrome OS";
    }
    return android::binder::Status::ok();
}

android::binder::Status ArcWakeLockServiceImpl::releasePartialWakeLock(bool* result) {
    VLOG(1) << "ArcWakeLockServiceImpl::releasePartialWakeLock";
    *result = mojo_service_->releasePartialWakeLock();
    if (!*result) {
        LOG(WARNING) << "ReleasePartialWakeLock returned false from Chrome OS";
    }
    return android::binder::Status::ok();
}

}  // namespace arc

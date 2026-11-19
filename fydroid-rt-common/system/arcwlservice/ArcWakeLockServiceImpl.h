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

#pragma once

#include <utils/StrongPointer.h>
#include <com/android/server/arc/power/BnArcWakeLockService.h>
#include "ArcWakeLockMojoService.h"

namespace arc {

// Binder-facing half of the wake lock service.
//
// Registers with Android's Binder service manager as "arc_wake_lock_service"
// (confirmed from arcwlservice binary symbol: ArcWakeLockServiceImpl::getServiceName()).
// Android's PowerManager in system_server calls acquirePartialWakeLock /
// releasePartialWakeLock over Binder when apps hold PARTIAL_WAKE_LOCK.
//
// Delegates to ArcWakeLockMojoService which calls Chrome OS via Mojo.
// The Binder thread blocks until the Mojo response arrives (SyncMojoCaller pattern).
class ArcWakeLockServiceImpl
        : public com::android::server::arc::power::BnArcWakeLockService {
public:
    explicit ArcWakeLockServiceImpl(android::sp<ArcWakeLockMojoService> mojo_service);
    ~ArcWakeLockServiceImpl() override;

    // Returns the Binder service name to register under.
    // Confirmed from arcwlservice binary: "arc_wake_lock_service"
    static const char* getServiceName();

    // BnArcWakeLockService overrides
    android::binder::Status acquirePartialWakeLock(bool* result) override;
    android::binder::Status releasePartialWakeLock(bool* result) override;

private:
    android::sp<ArcWakeLockMojoService> mojo_service_;
};

}  // namespace arc

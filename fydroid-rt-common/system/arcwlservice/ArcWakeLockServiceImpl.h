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

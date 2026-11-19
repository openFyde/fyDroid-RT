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

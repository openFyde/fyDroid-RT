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

#include <arc/Future.h>

namespace arc {

// --- CancellationRelay ---

CancellationRelay::CancellationRelay() = default;

CancellationRelay::~CancellationRelay() {
    cancel();
}

void CancellationRelay::addListener(internal::FutureLock* lock) {
    std::lock_guard<std::mutex> g(mutex_);
    if (cancelled_) {
        // Relay already cancelled — cancel the FutureLock immediately.
        lock->cancelLocked();
        return;
    }
    listeners_.insert(lock);
}

void CancellationRelay::removeListener(internal::FutureLock* lock) {
    std::lock_guard<std::mutex> g(mutex_);
    listeners_.erase(lock);
}

void CancellationRelay::cancel() {
    std::lock_guard<std::mutex> g(mutex_);
    cancelled_ = true;
    for (auto* lock : listeners_) {
        lock->cancelLocked();
    }
}

// --- FutureLock ---

namespace internal {

FutureLock::FutureLock(const android::sp<CancellationRelay>& relay)
    : shared_mutex_(std::make_shared<std::mutex>()),
      relay_(relay) {
    if (relay != nullptr) {
        relay->addListener(this);
    }
}

FutureLock::~FutureLock() {
    android::sp<CancellationRelay> strong = relay_.promote();
    if (strong != nullptr) {
        strong->removeListener(this);
    }
}

bool FutureLock::waitLocked() {
    // Caller MUST hold mutex_ before calling.
    // Adopt the already-held lock for condition_variable::wait.
    std::unique_lock<std::mutex> lk(mutex_, std::adopt_lock);
    cv_.wait(lk, [this] { return signalled_; });
    lk.release();  // Don't unlock — caller will unlock.
    return !cancelled_;
}

bool FutureLock::waitRelativeLocked(std::chrono::nanoseconds timeout) {
    std::unique_lock<std::mutex> lk(mutex_, std::adopt_lock);
    cv_.wait_for(lk, timeout, [this] { return signalled_; });
    lk.release();
    return !cancelled_;
}

void FutureLock::signalLocked() {
    // Does NOT require mutex_ to be held (caller may or may not hold it).
    signalled_ = true;
    // Lock+unlock shared_mutex_ as a memory barrier to ensure the
    // signalled_ write is visible before the notification.
    if (shared_mutex_) {
        shared_mutex_->lock();
        shared_mutex_->unlock();
    }
    cv_.notify_all();
}

void FutureLock::cancelLocked() {
    if (signalled_) return;  // Already signalled, nothing to do.
    cancelled_ = true;
    signalled_ = true;
    if (shared_mutex_) {
        shared_mutex_->lock();
        shared_mutex_->unlock();
    }
    cv_.notify_all();
}

}  // namespace internal

// Explicit instantiation so symbols are exported from the shared library.
// Existing binaries (libarctimer, arcwlservice, keymaster, etc.) link against these.
template class Future<void>;

}  // namespace arc

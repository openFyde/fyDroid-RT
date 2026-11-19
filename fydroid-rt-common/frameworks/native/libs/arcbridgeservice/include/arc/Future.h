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

// CancellationRelay / FutureLock / Future<void> — thread-safe future/promise
// with cancellation support.  Matches the ABI of the original proprietary
// libarcbridgeservice.so so that existing binaries (libarctimer, etc.) link.
#pragma once

#include <chrono>
#include <condition_variable>
#include <memory>
#include <mutex>
#include <unordered_set>

#include <utils/RefBase.h>

namespace arc {

namespace internal {
class FutureLock;
}

// Cancellation token shared among Futures.  Calling cancel() wakes all
// waiting FutureLocks.
//
// IMPORTANT: Member order is ABI-sensitive. Proprietary code constructs this
// class using inlined original constructors with compiled-in offsets:
//   0x10: mutex_, 0x38: cancelled_, 0x40: listeners_
// Do NOT reorder members.
class CancellationRelay : public android::RefBase {
public:
    CancellationRelay();
    virtual ~CancellationRelay();

    void addListener(internal::FutureLock* lock);
    void removeListener(internal::FutureLock* lock);
    void cancel();

    bool isCancelled() const { return cancelled_; }

private:
    mutable std::mutex mutex_;                          // offset 0x10
    bool cancelled_ = false;                            // offset 0x38
    std::unordered_set<internal::FutureLock*> listeners_; // offset 0x40
};

namespace internal {

// Low-level lock/condition wrapper used by Future.
//
// IMPORTANT: Member order is ABI-sensitive. Proprietary binaries access
// members at compiled-in offsets that depend on architecture:
//   32-bit (bionic: pthread_mutex_t=4, pthread_cond_t=4):
//     +0x00: cancelled_, +0x01: signalled_, +0x04: shared_mutex_,
//     +0x0C: cv_, +0x10: mutex_, +0x14: relay_  (total 0x1C = 28 bytes)
//   64-bit (bionic: pthread_mutex_t=40, pthread_cond_t=48):
//     +0x00: cancelled_, +0x01: signalled_, +0x08: shared_mutex_,
//     +0x18: cv_, +0x48: mutex_, +0x70: relay_  (total 0x80 = 128 bytes)
// Do NOT reorder members.
//
// The *Locked naming convention means the CALLER must hold mutex_ before
// calling waitLocked/waitRelativeLocked. signalLocked/cancelLocked do NOT
// require mutex_ to be held — they use shared_mutex_ for a memory barrier.
//
// waitLocked() uses pthread_cond_wait(cv_, mutex_) internally, which
// atomically releases mutex_ during the wait. This allows another thread
// (holding mutex_) to call signalLocked() and wake the waiter.
class FutureLock {
public:
    explicit FutureLock(const android::sp<CancellationRelay>& relay);
    ~FutureLock();

    // Caller MUST hold mutex_ before calling.
    // Returns true if signalled normally, false if cancelled.
    bool waitLocked();
    bool waitRelativeLocked(std::chrono::nanoseconds timeout);

    // signalLocked/cancelLocked do NOT require mutex_ to be held.
    // They use shared_mutex_ lock+unlock as a memory barrier, then notify cv_.
    void signalLocked();
    void cancelLocked();

    // Expose mutex for external locking (Future<T> and sub-HAL callers).
    std::mutex& mutex() { return mutex_; }

private:
    // ABI-sensitive layout — DO NOT reorder.
    // Natural alignment produces correct offsets on both architectures.
    bool cancelled_ = false;                        // +0x00
    bool signalled_ = false;                        // +0x01
    // (natural padding: 2 bytes on 32-bit, 6 bytes on 64-bit)
    std::shared_ptr<std::mutex> shared_mutex_;      // 32-bit: +0x04, 64-bit: +0x08
    std::condition_variable cv_;                    // 32-bit: +0x0C, 64-bit: +0x18
    std::mutex mutex_;                              // 32-bit: +0x10, 64-bit: +0x48
    android::wp<CancellationRelay> relay_;          // 32-bit: +0x14, 64-bit: +0x70
};

}  // namespace internal

// Simple void future with cancellation support.
template <typename T>
class Future {
public:
    explicit Future(const android::sp<CancellationRelay>& relay)
        : lock_(std::make_shared<internal::FutureLock>(relay)) {}

    static std::shared_ptr<Future<T>> make_shared(
            const android::sp<CancellationRelay>& relay) {
        return std::make_shared<Future<T>>(relay);
    }

    void get() {
        lock_->mutex().lock();
        lock_->waitLocked();
        lock_->mutex().unlock();
    }
    void wait() { get(); }
    void waitRelative(std::chrono::nanoseconds timeout) {
        lock_->mutex().lock();
        lock_->waitRelativeLocked(timeout);
        lock_->mutex().unlock();
    }
    void set() {
        lock_->mutex().lock();
        lock_->signalLocked();
        lock_->mutex().unlock();
    }

private:
    std::shared_ptr<internal::FutureLock> lock_;
};

}  // namespace arc

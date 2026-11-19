/*
 * Copyright (C) 2024 The FydeOS Authors
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

// WaylandTiming: VSync generator phase-locked to Chrome's display.
//
// Uses zcr_vsync_timing_v1 from the Wayland compositor when available,
// falling back to freewheel software timing otherwise.

#define LOG_TAG "WaylandTiming"

#include "WaylandTiming.h"

#include <log/log.h>
#include "../WaylandLog.h"
#include <time.h>
#include <utils/Timers.h>

namespace android {

WaylandTiming::WaylandTiming(int64_t periodNs) : mPeriodNs(periodNs) {
    ALOGI("WaylandTiming created with period %" PRId64 "ns (%.1f Hz)",
          periodNs, 1e9 / periodNs);
}

WaylandTiming::~WaylandTiming() {
    mRunning = false;
    mEnabled = false;
    mCondition.notify_all();
    if (mThread.joinable()) {
        mThread.join();
    }
    ALOGI("WaylandTiming destroyed");
}

void WaylandTiming::start(HWC2::ComposerCallback* callback,
                           Hwc2::Display displayId) {
    mCallback = callback;
    mDisplayId = displayId;
    mRunning = true;

    mThread = std::thread([this]() {
        threadLoop();
    });

    // Set thread name for debugging
    pthread_setname_np(mThread.native_handle(), "WaylandTiming");
    ALOGI("WaylandTiming thread started for display %" PRIu64, displayId);
}

void WaylandTiming::setEnabled(bool enabled) {
    bool wasEnabled = mEnabled.exchange(enabled);
    if (enabled && !wasEnabled) {
        mCondition.notify_all();
        ALOGVT("WaylandTiming enabled");
    } else if (!enabled && wasEnabled) {
        ALOGVT("WaylandTiming disabled");
    }
}

void WaylandTiming::updateTimingParameters(int64_t timebaseNs, int64_t intervalNs) {
    mTimebaseNs.store(timebaseNs, std::memory_order_relaxed);
    mIntervalNs.store(intervalNs, std::memory_order_relaxed);
    ALOGVT("vsync timing update: timebase=%" PRId64 "ns interval=%" PRId64 "ns (%.1f Hz)",
          timebaseNs, intervalNs, intervalNs > 0 ? 1e9 / intervalNs : 0.0);
}

void WaylandTiming::threadLoop() {
    struct timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    int64_t nextVsyncNs = now.tv_sec * INT64_C(1000000000) + now.tv_nsec;

    while (mRunning) {
        // Wait until enabled
        if (!mEnabled) {
            std::unique_lock lock(mMutex);
            mCondition.wait(lock, [this]() {
                return mEnabled.load() || !mRunning.load();
            });
            if (!mRunning) break;

            // Re-align after waking up
            clock_gettime(CLOCK_MONOTONIC, &now);
            nextVsyncNs = now.tv_sec * INT64_C(1000000000) + now.tv_nsec;
        }

        // Read timing parameters (may be updated by Wayland thread)
        int64_t interval = mIntervalNs.load(std::memory_order_relaxed);
        int64_t timebase = mTimebaseNs.load(std::memory_order_relaxed);
        if (interval <= 0) interval = mPeriodNs;  // fallback

        // Calculate next VSync time
        clock_gettime(CLOCK_MONOTONIC, &now);
        int64_t nowNs = now.tv_sec * INT64_C(1000000000) + now.tv_nsec;

        if (timebase > 0) {
            // Phase-locked: find next VSync after now
            int64_t elapsed = nowNs - timebase;
            int64_t periods = (elapsed >= 0) ? (elapsed / interval + 1) : 0;
            nextVsyncNs = timebase + periods * interval;
        } else {
            // Freewheel fallback
            nextVsyncNs += interval;
            if (nextVsyncNs <= nowNs) {
                nextVsyncNs = nowNs + interval;
            }
        }

        // Sleep until next VSync
        struct timespec target;
        // Cast for the 32-bit ABI where timespec fields are 32-bit (-Wshorten-64-to-32).
        target.tv_sec = static_cast<decltype(target.tv_sec)>(nextVsyncNs / INT64_C(1000000000));
        target.tv_nsec = static_cast<decltype(target.tv_nsec)>(nextVsyncNs % INT64_C(1000000000));
        clock_nanosleep(CLOCK_MONOTONIC, TIMER_ABSTIME, &target, nullptr);

        if (!mEnabled || !mRunning) continue;

        // Get the actual timestamp
        clock_gettime(CLOCK_MONOTONIC, &now);
        int64_t timestamp = now.tv_sec * INT64_C(1000000000) + now.tv_nsec;

        // Fire VSync callback (AOSP-13: onComposerHalVsync; nullopt vsync period)
        if (mCallback != nullptr) {
            mCallback->onComposerHalVsync(mDisplayId, timestamp, std::nullopt);
        }
    }
}

} // namespace android

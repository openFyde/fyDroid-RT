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

#pragma once

#include <atomic>
#include <condition_variable>
#include <cstdint>
#include <mutex>
#include <optional>
#include <thread>

#include <utils/StrongPointer.h>

#include "DisplayHardware/ComposerHal.h"
#include "DisplayHardware/HWC2.h"

namespace android {

// VSync generator using a timer thread phase-locked to Chrome's display.
//
// When zcr_vsync_timing_v1 is available, the thread locks to Chrome's
// exact timebase+interval (CLOCK_MONOTONIC). Otherwise falls back to a
// freewheel software timer at the configured period.
class WaylandTiming {
public:
    explicit WaylandTiming(int64_t periodNs);
    ~WaylandTiming();

    // Start the VSync thread. Must be called with a valid callback.
    // AOSP-13: HWC2::ComposerCallback (onComposerHalVsync) replaces the HIDL
    // Hwc2::IComposerCallback (onVsync). Stored as a raw pointer — SF owns the
    // callback for the composer's lifetime.
    void start(HWC2::ComposerCallback* callback, Hwc2::Display displayId);

    // Enable or disable VSync delivery. When disabled, the thread
    // stays alive but stops firing callbacks.
    void setEnabled(bool enabled);

    // Update timing parameters from zcr_vsync_timing_v1. Thread-safe.
    // Both values in nanoseconds (converted from protocol's µs).
    void updateTimingParameters(int64_t timebaseNs, int64_t intervalNs);

    int64_t periodNs() const { return mPeriodNs; }

private:
    void threadLoop();

    const int64_t mPeriodNs;
    Hwc2::Display mDisplayId = 0;
    HWC2::ComposerCallback* mCallback = nullptr;

    std::thread mThread;
    std::mutex mMutex;
    std::condition_variable mCondition;
    std::atomic<bool> mEnabled{false};
    std::atomic<bool> mRunning{false};

    // Phase-locked timing from zcr_vsync_timing_v1 (0 = not yet received)
    std::atomic<int64_t> mTimebaseNs{0};
    std::atomic<int64_t> mIntervalNs{0};
};

} // namespace android

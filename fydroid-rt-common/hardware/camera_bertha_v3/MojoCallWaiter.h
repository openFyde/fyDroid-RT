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

#include <algorithm>

#include <memory>

#include <android/log.h>

#include <base/bind.h>
#include <base/location.h>
#include <base/logging.h>
#include <base/synchronization/waitable_event.h>
#include <base/time/time.h>

#include "PipeHealth.h"

// Include this from .cpp files only — see the note in PipeHealth.h about
// libchrome headers and gfx::Size.

namespace fydroid { namespace camera {

// Budgets for a blocking Mojo round-trip to Chrome.
//
// CameraServiceWatchdog aborts cameraserver outright if a monitored call (open,
// close, disconnect) takes longer than ~10s — 100 cycles x 100ms. Every budget
// here has to stay comfortably under that, so the first timeout still leaves
// time for the close path to unwind and return an error instead of the whole
// process being SIGABRTed. See PipeHealth for why timeouts never stack.
inline constexpr int kMojoCallTimeoutMs = 2000;

// Opening a device and configuring streams legitimately do real work on the
// Chrome side (USB enumeration, buffer allocation), so they get more room.
inline constexpr int kMojoOpenTimeoutMs = 5000;

// The per-frame capture path gets a much larger budget than anything else.
//
// It is not on a watchdog-monitored call, so a slow reply here costs latency
// rather than the process, and a false positive is expensive: it kills a
// perfectly healthy streaming session. Observed on device with a 2s budget — a
// reply took over 2s after 1138 good frames and tore the session down. The only
// job of this timeout is to stop a genuinely wedged pipe from blocking forever.
inline constexpr int kMojoCaptureTimeoutMs = 8000;

// What a timeout should cost.
enum class TimeoutPolicy {
    // Fail this one call; the pipe stays usable and the next call tries again.
    //
    // Correct for one-shot module calls (enumerate, open, torch): they cannot
    // stack up, so there is no reason for a transient Chrome stall to cost more
    // than the call it hit. Marking the module pipe dead here is what turned a
    // momentary stall into a camera that never opens again.
    kFailCall,
    // Also mark the pipe dead, so the rest of this session fails immediately.
    //
    // Correct for per-frame device calls: the framework can have many capture
    // requests outstanding, and N x budget would blow through
    // CameraServiceWatchdog's ~10s limit during teardown. Recovery is automatic
    // — the next OpenDevice builds a fresh delegate with a fresh PipeHealth.
    kFailSession,
};

// Shared state for one outstanding Mojo call.
//
// This deliberately does not live on the caller's stack. The caller can give up
// and return while the reply is still in flight, and Chrome may deliver it
// afterwards — writing into a stack frame that no longer exists. Both the
// caller and the reply callback hold a shared_ptr, so the storage outlives
// whichever of the two finishes last.
template <typename T>
struct PendingReply {
    base::WaitableEvent event{base::WaitableEvent::ResetPolicy::AUTOMATIC,
                              base::WaitableEvent::InitialState::NOT_SIGNALED};
    T value{};
};

template <typename T>
std::shared_ptr<PendingReply<T>> MakePendingReply() {
    return std::make_shared<PendingReply<T>>();
}

// Blocks until the reply lands or the budget expires.
//
// Returns false on timeout, in which case pending->value is meaningless and
// must not be read.
template <typename T, typename Runner>
bool AwaitReply(const std::shared_ptr<PendingReply<T>>& pending,
                const std::shared_ptr<PipeHealth>& health,
                const Runner& mojo_runner,
                const char* what,
                int timeout_ms = kMojoCallTimeoutMs,
                TimeoutPolicy policy = TimeoutPolicy::kFailSession) {
    // Wait in slices so a pipe that dies mid-call (cros_camera_service
    // aborting) fails the call at once instead of after the full budget; the
    // connection-error handler marks `health` dead but cannot wake us.
    constexpr int kSliceMs = 50;
    bool pipeDied = false;
    for (int waited = 0; waited < timeout_ms; waited += kSliceMs) {
        const int slice = std::min(kSliceMs, timeout_ms - waited);
        if (pending->event.TimedWait(base::TimeDelta::FromMilliseconds(slice))) {
            return true;
        }
        if (!health->ok()) {
            pipeDied = true;
            break;
        }
    }
    if (pipeDied) {
        __android_log_print(ANDROID_LOG_ERROR, "CameraBerthaV3",
                "%s: pipe disconnected while waiting for Chrome — failing this call",
                what);
        mojo_runner->PostTask(FROM_HERE,
                base::BindOnce([](std::shared_ptr<PendingReply<T>>) {}, pending));
        return false;
    }
    // __android_log_print, not LOG(ERROR): libchrome's logging does not reach
    // logcat from the camera provider process, so a LOG() here is invisible
    // exactly when you most need it.
    if (policy == TimeoutPolicy::kFailSession && health->MarkDead()) {
        __android_log_print(ANDROID_LOG_ERROR, "CameraBerthaV3",
                "%s: no reply from Chrome after %d ms — failing the rest of "
                "this camera session fast; the next open starts a fresh pipe",
                what, timeout_ms);
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "CameraBerthaV3",
                "%s: no reply from Chrome after %d ms — failing this call; "
                "the pipe stays usable", what, timeout_ms);
    }
    // Hand our reference to the Mojo thread rather than dropping it here. The
    // reply may still land, and anything Mojo-owned inside `pending` (the
    // InterfacePtr that OpenDevice hands back, for one) is sequence-affine —
    // releasing the last reference on this binder thread would tear it down
    // off-sequence.
    mojo_runner->PostTask(FROM_HERE,
            base::BindOnce([](std::shared_ptr<PendingReply<T>>) {}, pending));
    return false;
}

}} // namespace fydroid::camera

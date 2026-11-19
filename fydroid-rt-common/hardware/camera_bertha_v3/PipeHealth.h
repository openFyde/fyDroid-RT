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

#include <atomic>
#include <memory>

// Deliberately dependency-light — <atomic> and <memory> only.
//
// The delegate headers hold a PipeHealth member, and CameraHal.h in particular
// must stay free of libchrome: pulling base/ or mojo/ headers in ahead of the
// generated mojom headers puts ui/gfx/geometry/size.h on a different include
// path and it redefines gfx::Size. The blocking-call machinery that actually
// needs libchrome lives in MojoCallWaiter.h, which only .cpp files include.

namespace fydroid { namespace camera {

// Tracks whether the Mojo pipe behind one delegate is still answering.
//
// Once a call times out — or the pipe reports a connection error — every later
// call on that pipe fails immediately instead of blocking for the full budget.
// Without this a wedged pipe still stalls teardown: the framework can have many
// capture requests outstanding, and N x kMojoCallTimeoutMs blows straight
// through CameraServiceWatchdog's ~10s limit and it SIGABRTs cameraserver, even
// though each individual call "only" timed out.
//
// Held by shared_ptr so a connection-error handler owned by the InterfacePtr
// (torn down on the Mojo thread, after the delegate itself is gone) can never
// observe a dangling pointer.
class PipeHealth {
 public:
    bool ok() const { return ok_.load(std::memory_order_acquire); }

    // Returns true only for the caller that flipped it, so callers can log once.
    bool MarkDead() { return ok_.exchange(false, std::memory_order_acq_rel); }

    // The pipe was replaced (CameraModuleDelegate::Rebind): calls may go again.
    void Revive() { ok_.store(true, std::memory_order_release); }

 private:
    std::atomic<bool> ok_{true};
};

}} // namespace fydroid::camera

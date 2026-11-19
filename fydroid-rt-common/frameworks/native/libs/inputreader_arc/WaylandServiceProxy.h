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

// Binder client for SurfaceFlinger's "Wayland" service.
// Sends the input bridge pipe write fd via transaction 29.

#pragma once

#include <android-base/unique_fd.h>
#include <binder/IBinder.h>
#include <utils/RefBase.h>

namespace android {

class WaylandServiceProxy {
public:
    // Look up the "Wayland" binder service. Returns true if found.
    bool connectToWayland();

    // Send pipe write fd to SurfaceFlinger via binder transaction 29.
    // Takes ownership of fd on success.
    bool notifyInputBridgePipeOpening(android::base::unique_fd fd);

    // Tell the composer that Android's focused window has taken or dropped
    // pointer capture, so it can take or drop the wayland pointer lock.
    // One-way: the caller is the input reader thread, which must not block on
    // SurfaceFlinger, and consecutive one-way calls on one binder keep order.
    bool requestPointerCapture(bool enable);

private:
    static constexpr uint32_t TRANSACTION_notifyInputBridgePipeOpening = 29;
    static constexpr uint32_t TRANSACTION_requestPointerCapture = 24;
    sp<IBinder> mService;
};

} // namespace android

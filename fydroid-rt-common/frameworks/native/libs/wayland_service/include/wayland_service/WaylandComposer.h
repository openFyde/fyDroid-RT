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

#include <memory>
#include <string>

namespace android {

class IWaylandService;

namespace compositionengine {
class CompositionEngine;
} // namespace compositionengine

// WaylandComposer replaces HWComposer in the fydroid-rt SurfaceFlinger.
//
// Instead of submitting frames to a hardware display via the HWC HAL,
// WaylandComposer submits them as Wayland surfaces to Chrome's Exo
// compositor via the wayland_service library.
//
// This is the main integration point between AOSP's SurfaceFlinger
// and the Chrome OS Wayland compositor.
class WaylandComposer {
public:
    WaylandComposer();
    ~WaylandComposer();

    // Initialize the Wayland connection and create the display.
    // Returns false if the connection cannot be established.
    bool initialize();

    // Get the underlying Wayland service.
    IWaylandService& getService();

    // Present a frame: submit all dirty surfaces to the compositor.
    void presentDisplay();

    // Handle VSync callback from zcr_vsync_timing_v1.
    void onVSync(int64_t timestamp, int64_t period);

    // Get display attributes from zcr_remote_output_v2.
    int32_t getWidth() const;
    int32_t getHeight() const;
    int32_t getVsyncPeriodNs() const;
    int32_t getDpiX() const;
    int32_t getDpiY() const;

private:
    static constexpr const char* kWaylandSocket = "/var/run/chrome/wayland-0";

    std::unique_ptr<IWaylandService> mService;

    int32_t mWidth = 0;
    int32_t mHeight = 0;
    int32_t mVsyncPeriodNs = 16666000; // 60fps default
    int32_t mDpiX = 160;
    int32_t mDpiY = 160;
};

} // namespace android

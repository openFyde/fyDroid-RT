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

#pragma once

#include "WaylandSurfaceData.h"
#include "WaylandWindow.h"

#include <cstdint>
#include <string>

struct wl_compositor;
struct wl_output;
struct wl_surface;
struct zcr_remote_shell_v2;
struct zaura_shell;
struct zaura_surface;
struct zcr_notification_surface_v2;

namespace android {

// Window for per-notification rendered content.
// Uses zcr_notification_surface_v2 — Chrome matches the surface to the
// notification popup by the notification_key string.
//
// Created when Chrome calls createNotificationWindow(key) via Mojo.
// A VirtualDisplay renders the notification View, SF composites it,
// and HWC2 presents the buffer through this Wayland surface.
class WaylandNotificationWindow : public WaylandWindow {
public:
    WaylandNotificationWindow(wl_compositor* compositor,
                              zcr_remote_shell_v2* remoteShell,
                              uint64_t layerId,
                              const std::string& notifKey,
                              float scaleFactor = 1.0f,
                              zaura_shell* auraShell = nullptr);
    ~WaylandNotificationWindow() override;

    WaylandNotificationWindow(const WaylandNotificationWindow&) = delete;
    WaylandNotificationWindow& operator=(const WaylandNotificationWindow&) = delete;

    // WaylandWindow interface
    wl_surface* surface() const override { return mSurface; }
    void setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                           int32_t w, int32_t h) override;
    void setEventListener(EventListener* /*listener*/) override {}

    void minimize() override {}
    void maximize() override {}
    void restore() override {}
    void fullscreen() override {}

    int32_t boundsX() const override { return mBoundsX; }
    int32_t boundsY() const override { return mBoundsY; }
    int32_t boundsWidth() const override { return mBoundsWidth; }
    int32_t boundsHeight() const override { return mBoundsHeight; }
    void setAccessibilityId(int32_t id) override;

    WindowState windowState() const override { return WindowState::NORMAL; }
    bool isVisible() const override { return true; }
    bool isClosed() const override { return false; }
    int32_t displayId() const override { return mDisplayId; }
    // The notification buffer is rendered at mBufferScale× DIP, but display 2
    // (the notification VD) is in pixels. Scale surface-local touch coords up
    // so the bottom half of the card (action buttons) is reachable, not just
    // the top half.
    float inputScale() const override { return static_cast<float>(mBufferScale); }

    // Set the Android display ID (notification VD) for input routing.
    void setDisplayId(int32_t id) { mDisplayId = id; }
    // Set the layer's display frame on the VD (for input coordinate mapping).
    void setDisplayFrame(int32_t x, int32_t y) { mBoundsX = x; mBoundsY = y; }

private:
    WaylandSurfaceData mSurfaceData;
    wl_surface* mSurface = nullptr;
    zcr_notification_surface_v2* mNotifSurface = nullptr;
    // Accessibility: see WaylandAccessibilityId.h.
    zaura_surface* mAuraSurface = nullptr;
    int32_t mAccessibilityId = -1;
    uint64_t mLayerId = 0;
    std::string mNotifKey;
    int32_t mBufferScale = 1;
    int32_t mBoundsX = 0, mBoundsY = 0, mBoundsWidth = 0, mBoundsHeight = 0;
    int32_t mDisplayId = 0;
};

} // namespace android

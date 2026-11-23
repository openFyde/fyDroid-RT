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

struct wl_compositor;
struct wl_output;
struct wl_surface;
struct zcr_remote_shell_v2;
struct zaura_shell;
struct zaura_surface;
struct zcr_toast_surface_v2;

namespace android {

// Overlay window for TYPE_APPLICATION_OVERLAY (2038).
// Uses zcr_toast_surface_v2 — lightweight surface with no Chrome window
// background, no decorations. Supports set_bounds_in_output for positioning
// and moveTo for drag-to-move.
class WaylandOverlayWindow : public WaylandWindow {
public:
    WaylandOverlayWindow(wl_compositor* compositor,
                         zcr_remote_shell_v2* remoteShell,
                         wl_output* output, uint64_t layerId,
                         float scaleFactor = 1.0f,
                         zaura_shell* auraShell = nullptr);
    ~WaylandOverlayWindow() override;

    WaylandOverlayWindow(const WaylandOverlayWindow&) = delete;
    WaylandOverlayWindow& operator=(const WaylandOverlayWindow&) = delete;

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
    int32_t boundsWidth() const override { return mBoundsW; }
    int32_t boundsHeight() const override { return mBoundsH; }
    // Android frame position — set by setBoundsInOutput, not by moveTo
    int32_t displayFrameX() const override { return mFrameX; }
    int32_t displayFrameY() const override { return mFrameY; }
    void moveTo(int32_t x, int32_t y) override;
    bool isOverlay() const override { return true; }
    void setAccessibilityId(int32_t id) override;

    WindowState windowState() const override { return WindowState::NORMAL; }
    bool isVisible() const override { return mSurface != nullptr; }
    bool isClosed() const override { return mSurface == nullptr; }

private:
    WaylandSurfaceData mSurfaceData;
    wl_surface* mSurface = nullptr;
    zcr_toast_surface_v2* mToastSurface = nullptr;
    // Accessibility: see WaylandAccessibilityId.h.
    zaura_surface* mAuraSurface = nullptr;
    int32_t mAccessibilityId = -1;
    wl_output* mOutput = nullptr;
    uint64_t mLayerId = 0;
    int32_t mBoundsX = 0, mBoundsY = 0, mBoundsW = 0, mBoundsH = 0;
    int32_t mFrameX = 0, mFrameY = 0;  // Android display frame position
    bool mDragged = false;  // true after moveTo — ignore setBoundsInOutput position
};

} // namespace android

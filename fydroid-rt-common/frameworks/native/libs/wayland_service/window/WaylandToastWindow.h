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

// Toast window backed by zcr_toast_surface_v2.
// No event callbacks from Chrome — just destroy + set_bounds_in_output requests.
class WaylandToastWindow : public WaylandWindow {
public:
    WaylandToastWindow(wl_compositor* compositor,
                       zcr_remote_shell_v2* remoteShell,
                       uint64_t layerId, float scaleFactor = 1.0f,
                       zaura_shell* auraShell = nullptr);
    ~WaylandToastWindow() override;

    WaylandToastWindow(const WaylandToastWindow&) = delete;
    WaylandToastWindow& operator=(const WaylandToastWindow&) = delete;

    // WaylandWindow interface
    wl_surface* surface() const override { return mSurface; }
    void setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                           int32_t w, int32_t h) override;
    void setEventListener(EventListener* /*listener*/) override {}

    // Toast placement policy: given the raw panel size (pre-rotation) and the
    // toast size, center the toast horizontally and put it 4/5 of the way down,
    // then emit set_bounds_in_output. Android's own toast frame sits too low and
    // is delivered a frame late (top-left flash), so the toast positions itself.
    void setCenteredBoundsInOutput(wl_output* output, int32_t screenW,
                                   int32_t screenH, int32_t toastW,
                                   int32_t toastH);

    void minimize() override {}
    void maximize() override {}
    void restore() override {}
    void fullscreen() override {}

    int32_t boundsX() const override { return mBoundsX; }
    int32_t boundsY() const override { return mBoundsY; }
    int32_t boundsWidth() const override { return mBoundsWidth; }
    int32_t boundsHeight() const override { return mBoundsHeight; }
    // A toast never takes focus and never eats a click — Android's own toast
    // window is NOT_FOCUSABLE | NOT_TOUCHABLE, and the Chrome-side surface has
    // an empty input region to match (see the constructor).
    bool acceptsInput() const override { return false; }
    void setAccessibilityId(int32_t id) override;

    WindowState windowState() const override { return WindowState::NORMAL; }
    bool isVisible() const override { return true; }
    bool isClosed() const override { return false; }

private:
    WaylandSurfaceData mSurfaceData;
    wl_surface* mSurface = nullptr;
    zcr_toast_surface_v2* mToastSurface = nullptr;
    // Accessibility: see WaylandAccessibilityId.h.
    zaura_surface* mAuraSurface = nullptr;
    int32_t mAccessibilityId = -1;
    uint64_t mLayerId = 0;
    int32_t mBoundsX = 0, mBoundsY = 0, mBoundsWidth = 0, mBoundsHeight = 0;
};

} // namespace android

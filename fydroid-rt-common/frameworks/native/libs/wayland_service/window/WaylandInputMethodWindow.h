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
#include <vector>

struct wl_compositor;
struct wl_output;
struct wl_surface;
struct wl_region;
struct zcr_remote_shell_v2;
struct zaura_shell;
struct zaura_surface;
struct zcr_input_method_surface_v2;

namespace android {

// IME window backed by zcr_input_method_surface_v2.
// No event callbacks from Chrome — just destroy + set_bounds_in_output requests.
//
// Scale: this surface deliberately does NOT set wl_surface buffer_scale, and
// there is no zcr_input_method_surface_v2 set_scale_factor to call either
// (unlike zcr_toast_surface_v2). Chrome's InputMethodSurface is a
// ClientControlledShellSurface, which submits in pixel coordinates, and with no
// scale factor ever requested exo's CalculateScaleFactor() falls back to the
// output's device scale factor. So the buffer, the set_bounds_in_output
// rectangle and the wl_pointer/wl_touch coordinates are all already in output
// physical pixels — the same space Android renders display 0 in. Setting
// buffer_scale here (as WaylandNotificationWindow must, because its buffer is
// rendered on a separate 1x DIP display) would apply the DSF twice and shrink
// the keyboard.
class WaylandInputMethodWindow : public WaylandWindow {
public:
    WaylandInputMethodWindow(wl_compositor* compositor,
                             zcr_remote_shell_v2* remoteShell,
                             uint64_t layerId,
                             zaura_shell* auraShell = nullptr);
    ~WaylandInputMethodWindow() override;

    WaylandInputMethodWindow(const WaylandInputMethodWindow&) = delete;
    WaylandInputMethodWindow& operator=(const WaylandInputMethodWindow&) = delete;

    // WaylandWindow interface
    wl_surface* surface() const override { return mSurface; }
    void setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                           int32_t w, int32_t h) override;
    void setEventListener(EventListener* /*listener*/) override {}

    // Android's on-display frame for this window, kept separate from the
    // on-screen bounds above. Android lays the IME out at the bottom of its
    // display, but Chrome OS draws the shelf over that strip
    // (kShellWindowId_ShelfContainer stacks above
    // kShellWindowId_ArcVirtualKeyboardContainer), so the surface is placed
    // bottom-aligned to the WORK AREA instead and ends up higher than Android
    // thinks it is. Input must not follow that shift: wl_touch coordinates are
    // surface-local, and the offset added to them has to be where the Android
    // window actually is, or every tap lands one shelf-height off.
    void setDisplayFrame(int32_t x, int32_t y) { mFrameX = x; mFrameY = y; }

    // Restrict input to the keys. An IME window is the full display with a
    // transparent region above the keyboard; with no input region exo's
    // hit_test_region is the whole surface, and because the input-method
    // surface sits above app windows it then swallows every tap on the
    // desktop. contentTop is METADATA_IME_CONTENT_TOP (0 = unknown, leave the
    // region unset).
    void setContentTop(int32_t contentTop);

    // Take no input at all: install an EMPTY input region, so every event over
    // this surface falls through to whatever ash has beneath it.
    //
    // This is what an IME that draws no keys of its own needs. FydroidIME's
    // window exists only to carry the app's IME inset — Chrome's virtual
    // keyboard is an ash window stacked above the ARC surface and takes the key
    // presses itself — and it says so with TOUCHABLE_INSETS_REGION + an empty
    // touchable region, which reaches us as
    // METADATA_IME_CONTENT_TOP == IME_CONTENT_TOP_NO_INPUT.
    //
    // Note the asymmetry in the protocol: an UNSET input region means the whole
    // surface, an empty one means nothing. Only the latter gets the events out
    // of our way.
    void setNoInputRegion();

    // Take input exactly where the IME said it does: rects are its
    // TOUCHABLE_INSETS_REGION, flattened left,top,right,bottom in window px
    // (= surface-local, see the scale note above). Needed when the touchable
    // part is not a bottom band — Gboard with a hardware keyboard shows only a
    // small toolbar at the side of a transparent full-display window, which a
    // content top cannot describe.
    void setInputRects(const std::vector<int32_t>& rects);
    int32_t displayFrameX() const override { return mFrameX; }
    int32_t displayFrameY() const override { return mFrameY; }

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

private:
    WaylandSurfaceData mSurfaceData;
    wl_surface* mSurface = nullptr;
    zcr_input_method_surface_v2* mImeSurface = nullptr;
    // Accessibility: see WaylandAccessibilityId.h.
    zaura_surface* mAuraSurface = nullptr;
    int32_t mAccessibilityId = -1;
    uint64_t mLayerId = 0;
    int32_t mBoundsX = 0, mBoundsY = 0, mBoundsWidth = 0, mBoundsHeight = 0;
    int32_t mFrameX = 0, mFrameY = 0;
    int32_t mContentTop = -1;   // last applied; -1 = never
    bool mNoInputRegion = false;  // last applied region is the empty one
    std::vector<int32_t> mInputRects;  // last applied by setInputRects()
    wl_compositor* mCompositor = nullptr;
};

} // namespace android

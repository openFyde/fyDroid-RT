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

#include <cstdint>
#include <memory>
#include <vector>

#include <utils/StrongPointer.h>

#include "DisplayHardware/ComposerHal.h"

struct zcr_vsync_timing_v1;

namespace android {

class WaylandClient;
class WaylandLayerManager;
class WaylandOutput;
class WaylandTiming;

// Accumulated output info from zaura_output_manager_v2 events.
// Events arrive as a sequence, terminated by `done`.
struct OutputManagerInfo {
    int32_t logicalWidth = 0, logicalHeight = 0;
    int32_t physicalWidth = 0, physicalHeight = 0;
    float scaleFactor = 0.0f;
    int32_t logicalTransform = -1;
    int32_t panelTransform = -1;
    int32_t workAreaTop = 0, workAreaLeft = 0;
    int32_t workAreaBottom = 0, workAreaRight = 0;
};

// Per-display coordinator. Owns WaylandOutput (display info),
// WaylandLayerManager (container registry), and WaylandTiming (VSync).
//
// Display properties come from two sources:
//   1. wl_output — physical pixel resolution and refresh rate
//   2. zaura_output_manager_v2 — logical size, physical size, device scale
//      factor, transforms, work area insets (authoritative when available)
//
// All layer presentation flows through HWC virtual displays. This class
// provides display properties, VSync, and a flush() for batch Wayland I/O.
class WaylandDisplay {
public:
    static constexpr int32_t kFallbackWidth = 960;
    static constexpr int32_t kFallbackHeight = 540;
    static constexpr int32_t kFallbackVsyncPeriodNs = 16666667; // ~60Hz
    static constexpr int32_t kDefaultDpiX = 160000; // units of 1/1000 dpi
    static constexpr int32_t kDefaultDpiY = 160000;
    // Density scale relative to Chrome. Android density is Chrome's
    // DSF-equivalent (DSF × 160) scaled by this factor. Pure layout
    // metric: the px↔dip task mapping stays 1:1 with Chrome
    // (mAndroidDsf / render targets are NOT scaled by this), so
    // presentation remains pixel-to-pixel. Tuned on-device: 1.1 made
    // Android UI larger than wanted; 0.9 reads best.
    static constexpr float kDensityTouchScale = 0.9f;

    // Construct with client (for Wayland globals), per-display output,
    // and the wl_registry global name for this output.
    WaylandDisplay(WaylandClient* client, std::unique_ptr<WaylandOutput> output,
                   uint32_t globalName = 0);
    ~WaylandDisplay();

    WaylandDisplay(const WaylandDisplay&) = delete;
    WaylandDisplay& operator=(const WaylandDisplay&) = delete;

    WaylandClient* client() const { return mClient; }
    WaylandOutput* output() const { return mOutput.get(); }

    // Logical display width (DIP). Uses output manager logical_size if
    // available, otherwise falls back to wl_output / kFallbackWidth.
    int32_t width() const;

    // Logical display height (DIP).
    int32_t height() const;

    // Returns vsync period in nanoseconds derived from output refresh rate,
    // or kFallbackVsyncPeriodNs if not available.
    int32_t vsyncPeriodNs() const;

    // DPI in units of 1/1000 dpi. Returns overrideDpi (averaged with DSF)
    // when scale factor is available, otherwise falls back to lcd_density.
    int32_t dpiX() const;
    int32_t dpiY() const;

    // Computed override DPI: (lcd_density + scaleFactor*160) / 2.
    // Returns 0 if not yet computable.
    int32_t overrideDpi() const;

    // Device scale factor (e.g. 2.0 for HiDPI). 0 means not yet received.
    float scaleFactor() const { return mScaleFactor; }

    // Effective display transform for SF coordinate space.
    int32_t displayTransform() const;

    // Integer buffer scale for wl_surface.set_buffer_scale.
    int32_t bufferScale() const;

    // Physical panel dimensions from zaura_output_manager_v2 (native, pre-rotation).
    int32_t physicalWidth() const { return mPhysicalWidth; }
    int32_t physicalHeight() const { return mPhysicalHeight; }

    // Current on-screen size in physical pixels, accounting for the live
    // rotation: Chrome's logical_size rotates with the display (tablet mode),
    // the physical panel size does not. Used to place rotation-aware standalone
    // windows (e.g. toasts) in the current orientation. Falls back to the
    // physical size before the first output-manager update.
    int32_t currentWidth() const {
        return (mLogicalWidth > 0 && mScaleFactor > 0.0f)
                ? static_cast<int32_t>(mLogicalWidth * mScaleFactor) : mPhysicalWidth;
    }
    int32_t currentHeight() const {
        return (mLogicalHeight > 0 && mScaleFactor > 0.0f)
                ? static_cast<int32_t>(mLogicalHeight * mScaleFactor) : mPhysicalHeight;
    }

    // Panel transform from zaura_output_manager_v2 (WL_OUTPUT_TRANSFORM_*).
    int32_t panelTransform() const { return mPanelTransform; }

    // Logical transform from zaura_output_manager_v2 — the user's display
    // rotation on its own. -1 until received.
    int32_t logicalTransform() const { return mLogicalTransform; }

    // The panel's fixed mount rotation in degrees clockwise.
    //
    // exo sends panel_transform = OutputTransform(display.rotation() + panel
    // mount) and logical_transform = OutputTransform(display.rotation()), so
    // dividing the second out of the first leaves just the mount — a constant
    // that does not move when the user rotates the screen.
    //
    // This is what the camera HAL needs. Chrome OS expresses a camera's
    // sensor_orientation relative to its own unrotated desktop, while fydroid
    // makes Android's *natural* orientation the physical panel (see width()),
    // so the two frames differ by exactly this angle. Using the live rotation
    // instead would double-compensate: apps already correct for it themselves
    // via Display.getRotation().
    int32_t panelBaseRotationDegrees() const;

    // Work area insets from zaura_output_manager_v2 (raw, in DIP).
    int32_t workAreaTop() const { return mWorkAreaTop; }
    int32_t workAreaLeft() const { return mWorkAreaLeft; }
    int32_t workAreaBottom() const { return mWorkAreaBottom; }
    int32_t workAreaRight() const { return mWorkAreaRight; }

    // The work area as a rectangle in this output's PIXEL coordinate space —
    // the same space setBoundsInOutput() and currentWidth()/currentHeight()
    // use. The insets above arrive in DIP (exo sends logical_insets, which are
    // in the same logical space as logical_size), so they have to be scaled by
    // the DSF before they can be mixed with anything else here.
    //
    // This is the rectangle ash will clamp a *maximized* window into. It moves
    // with the shelf: a window that hides the shelf (a game asserting
    // AUTOHIDE, a real fullscreen window) sees zero insets here, so callers do
    // not need to know which case they are in — the work area already reflects
    // it. Falls back to the whole output before the first output-manager
    // update, and if the insets are nonsense (bigger than the display).
    int32_t workAreaX() const;
    int32_t workAreaY() const;
    int32_t workAreaWidth() const;
    int32_t workAreaHeight() const;

    // Apply display properties from zaura_output_manager_v2.
    void setOutputManagerInfo(const OutputManagerInfo& info);

    // Propagate display info changes to LayerManager
    void updateLayerManagerOutputInfo();

    // Flush all queued Wayland requests to Chrome.
    // Called by WaylandHwc2Composer::presentDisplay() for the physical display
    // after all virtual displays have committed their surfaces.
    void flush();

    // --- VSync management ---

    // Start the VSync thread. Must be called with a valid callback.
    // AOSP-13: HWC2::ComposerCallback replaces HIDL Hwc2::IComposerCallback.
    void startVSync(HWC2::ComposerCallback* callback,
                    Hwc2::Display displayId);

    // Enable or disable VSync delivery.
    void setVsyncEnabled(bool enabled);

    // Layer manager access (container registry)
    WaylandLayerManager* layerManager() const { return mLayerManager.get(); }

    // wl_registry global name for this output (for output manager routing).
    uint32_t globalName() const { return mGlobalName; }

    // Chrome display ID from zaura_output_manager_v2 display_id event.
    int64_t chromeDisplayId() const { return mChromeDisplayId; }
    void setChromeDisplayId(int64_t id) { mChromeDisplayId = id; }

    // Logical position from zaura_output_manager_v2 (for spatial arrangement).
    int32_t logicalX() const { return mLogicalX; }
    int32_t logicalY() const { return mLogicalY; }
    void setLogicalPosition(int32_t x, int32_t y) { mLogicalX = x; mLogicalY = y; }

private:
    friend void vsyncTimingUpdate(void*, struct zcr_vsync_timing_v1*,
                                   uint32_t, uint32_t, uint32_t, uint32_t);

    WaylandClient* mClient;  // not owned — outlives this display
    std::unique_ptr<WaylandOutput> mOutput;
    std::unique_ptr<WaylandLayerManager> mLayerManager;
    std::unique_ptr<WaylandTiming> mTiming;
    zcr_vsync_timing_v1* mVsyncTiming = nullptr;  // owned, may be null

    // wl_registry global name
    uint32_t mGlobalName = 0;

    // Chrome display ID (from zaura_output_manager_v2 display_id)
    int64_t mChromeDisplayId = -1;

    // Logical position (from zaura_output_manager_v2 logical_position)
    int32_t mLogicalX = 0, mLogicalY = 0;

    // From ro.sf.lcd_density (Chrome's androidboot.lcd_density)
    int32_t mLcdDensity = 0;

    // From zaura_output_manager_v2 (authoritative when > 0)
    int32_t mLogicalWidth = 0, mLogicalHeight = 0;
    int32_t mPhysicalWidth = 0, mPhysicalHeight = 0;
    float mScaleFactor = 0.0f;
    int32_t mLogicalTransform = -1, mPanelTransform = -1;
    int32_t mWorkAreaTop = 0, mWorkAreaLeft = 0;
    int32_t mWorkAreaBottom = 0, mWorkAreaRight = 0;
};

} // namespace android

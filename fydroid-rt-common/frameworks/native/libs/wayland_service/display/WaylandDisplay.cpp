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

// WaylandDisplay: Per-display coordinator owning output info,
// layer manager (container registry), and VSync timing.
//
// All layer presentation flows through HWC virtual displays.
// This class provides display properties, VSync, and flush().

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandDisplay"

#include "WaylandDisplay.h"
#include "WaylandOutput.h"
#include "WaylandTiming.h"
#include "../WaylandClient.h"
#include "../layer/WaylandLayerManager.h"

#include <wayland-client.h>
#include <vsync-feedback-unstable-v1-client-protocol.h>

#include <cutils/properties.h>
#include <log/log.h>
#include <cmath>
#include <unistd.h>

namespace android {

// --- zcr_vsync_timing_v1 listener ---

void vsyncTimingUpdate(void* data, struct zcr_vsync_timing_v1*,
                               uint32_t tb_l, uint32_t tb_h,
                               uint32_t iv_l, uint32_t iv_h) {
    auto* self = static_cast<WaylandDisplay*>(data);
    int64_t timebaseNs = static_cast<int64_t>((static_cast<uint64_t>(tb_h) << 32) | tb_l) * 1000;
    int64_t intervalNs = static_cast<int64_t>((static_cast<uint64_t>(iv_h) << 32) | iv_l) * 1000;
    self->mTiming->updateTimingParameters(timebaseNs, intervalNs);
}

static const struct zcr_vsync_timing_v1_listener sVsyncTimingListener = {
    .update = vsyncTimingUpdate,
};

WaylandDisplay::WaylandDisplay(WaylandClient* client,
                               std::unique_ptr<WaylandOutput> output,
                               uint32_t globalName)
    : mClient(client), mOutput(std::move(output)), mGlobalName(globalName) {

    // Read Chrome's lcd_density for DPI reporting
    char value[PROPERTY_VALUE_MAX] = {};
    property_get("ro.sf.lcd_density", value, "0");
    mLcdDensity = atoi(value);
    ALOGI("WaylandDisplay: lcd_density=%d (from ro.sf.lcd_density)", mLcdDensity);

    ALOGI("WaylandDisplay created — %dx%d @ %d mHz (fallback: %dx%d)",
          mOutput ? mOutput->width() : 0,
          mOutput ? mOutput->height() : 0,
          mOutput ? mOutput->refreshMHz() : 0,
          kFallbackWidth, kFallbackHeight);

    // Create layer manager (container registry)
    wl_output* wlOutput = mOutput ? mOutput->wlOutput() : nullptr;
    mLayerManager = std::make_unique<WaylandLayerManager>(client, wlOutput);

    // Create VSync timing (uses output refresh rate or fallback)
    mTiming = std::make_unique<WaylandTiming>(vsyncPeriodNs());

    // Subscribe to compositor VSync timing if available
    auto* vsyncFeedback = mClient->getVsyncFeedback();
    if (vsyncFeedback && wlOutput) {
        mVsyncTiming = zcr_vsync_feedback_v1_get_vsync_timing(
                vsyncFeedback, wlOutput);
        zcr_vsync_timing_v1_add_listener(mVsyncTiming, &sVsyncTimingListener, this);
        ALOGI("subscribed to zcr_vsync_timing_v1 for phase-locked VSync");
    } else {
        ALOGI("zcr_vsync_feedback_v1 not available — using freewheel VSync");
    }
}

WaylandDisplay::~WaylandDisplay() {
    // Destroy vsync timing subscription before timing thread
    if (mVsyncTiming) {
        zcr_vsync_timing_v1_destroy(mVsyncTiming);
        mVsyncTiming = nullptr;
    }
    // Destroy timing (stop vsync thread)
    mTiming.reset();
    // Then layer manager (destroys containers and windows)
    mLayerManager.reset();
    // Then output (wl_output_destroy)
    mOutput.reset();
    ALOGI("WaylandDisplay destroyed");
}

int32_t WaylandDisplay::width() const {
    // Report physical panel dimensions (native, pre-rotation) — matching
    // ARC++ behavior.  WindowManager applies rotation via ArcDisplayLayout.
    if (mPhysicalWidth > 0) {
        return mPhysicalWidth;
    }
    // Fallback: logical × DSF (already rotated)
    if (mLogicalWidth > 0 && mScaleFactor > 0.0f) {
        return static_cast<int32_t>(mLogicalWidth * mScaleFactor);
    }
    if (mOutput && mOutput->width() > 0) {
        return mOutput->width();
    }
    return kFallbackWidth;
}

int32_t WaylandDisplay::height() const {
    if (mPhysicalHeight > 0) {
        return mPhysicalHeight;
    }
    if (mLogicalHeight > 0 && mScaleFactor > 0.0f) {
        return static_cast<int32_t>(mLogicalHeight * mScaleFactor);
    }
    if (mOutput && mOutput->height() > 0) {
        return mOutput->height();
    }
    return kFallbackHeight;
}

int32_t WaylandDisplay::vsyncPeriodNs() const {
    if (mOutput && mOutput->refreshMHz() > 0) {
        // mHz to ns: 1e9 / (mHz / 1000) = 1e12 / mHz
        return static_cast<int32_t>(1000000000000LL / mOutput->refreshMHz());
    }
    return kFallbackVsyncPeriodNs;
}

int32_t WaylandDisplay::overrideDpi() const {
    if (mScaleFactor > 0.0f) {
        // Android density = Chrome's DSF-equivalent density (DSF × 160)
        // with the touch-affordance bonus, e.g. 2.0 × 160 × 1.1 = 352.
        // Replaces the ARC++ (lcd_density + DSF·160)/2 average, which made
        // Android UI ~12% smaller than Chrome's proportions.
        return static_cast<int32_t>(mScaleFactor * 160.0f * kDensityTouchScale);
    }
    if (mLcdDensity > 0) {
        return mLcdDensity;
    }
    return 0;
}

int32_t WaylandDisplay::dpiX() const {
    int32_t dpi = overrideDpi();
    if (dpi > 0) return dpi * 1000;
    return kDefaultDpiX;
}

int32_t WaylandDisplay::dpiY() const {
    int32_t dpi = overrideDpi();
    if (dpi > 0) return dpi * 1000;
    return kDefaultDpiY;
}

int32_t WaylandDisplay::bufferScale() const {
    if (mLogicalWidth > 0) {
        return 1;
    }
    if (mScaleFactor > 0.0f) {
        int32_t scale = static_cast<int32_t>(std::round(mScaleFactor));
        return scale > 0 ? scale : 1;
    }
    return 2; // Default for HiDPI Chrome OS
}

// Work area in output pixels. See the header for why this exists.
//
// The insets are DIP in the LOGICAL (rotated) frame, which is the frame
// currentWidth()/currentHeight() are in — not the physical panel frame
// width()/height() report to SurfaceFlinger. Everything here stays logical.
int32_t WaylandDisplay::workAreaX() const {
    if (mScaleFactor <= 0.0f || workAreaWidth() >= currentWidth()) return 0;
    return static_cast<int32_t>(mWorkAreaLeft * mScaleFactor);
}

int32_t WaylandDisplay::workAreaY() const {
    if (mScaleFactor <= 0.0f || workAreaHeight() >= currentHeight()) return 0;
    return static_cast<int32_t>(mWorkAreaTop * mScaleFactor);
}

int32_t WaylandDisplay::workAreaWidth() const {
    const int32_t full = currentWidth();
    if (mLogicalWidth <= 0 || mScaleFactor <= 0.0f) return full;
    const int32_t dip = mLogicalWidth - mWorkAreaLeft - mWorkAreaRight;
    if (dip <= 0) return full;
    const int32_t px = static_cast<int32_t>(dip * mScaleFactor);
    return (px > 0 && px <= full) ? px : full;
}

int32_t WaylandDisplay::workAreaHeight() const {
    const int32_t full = currentHeight();
    if (mLogicalHeight <= 0 || mScaleFactor <= 0.0f) return full;
    const int32_t dip = mLogicalHeight - mWorkAreaTop - mWorkAreaBottom;
    if (dip <= 0) return full;
    const int32_t px = static_cast<int32_t>(dip * mScaleFactor);
    return (px > 0 && px <= full) ? px : full;
}

int32_t WaylandDisplay::displayTransform() const {
    if (mLogicalTransform >= 0) {
        return mLogicalTransform;
    }
    return mOutput ? mOutput->transform() : 0;
}

int32_t WaylandDisplay::panelBaseRotationDegrees() const {
    // WL_OUTPUT_TRANSFORM_* -> Android rotation quarter-turns. Same inverse
    // mapping WaylandLayoutParcel uses: WL 0->ROT_0, 1->ROT_270, 2->ROT_180,
    // 3->ROT_90.
    static const int32_t kPanelToAndroid[] = {0, 3, 2, 1};
    if (mPanelTransform < 0 || mPanelTransform > 3) {
        return 0;  // not received yet — assume a landscape-native panel
    }
    const int32_t panel = kPanelToAndroid[mPanelTransform];
    const int32_t logical = (mLogicalTransform >= 0 && mLogicalTransform <= 3)
            ? kPanelToAndroid[mLogicalTransform] : 0;
    return ((panel - logical + 4) % 4) * 90;
}

void WaylandDisplay::setOutputManagerInfo(const OutputManagerInfo& info) {
    mLogicalWidth = info.logicalWidth;
    mLogicalHeight = info.logicalHeight;
    mPhysicalWidth = info.physicalWidth;
    mPhysicalHeight = info.physicalHeight;
    mScaleFactor = info.scaleFactor;
    mLogicalTransform = info.logicalTransform;
    mPanelTransform = info.panelTransform;
    mWorkAreaTop = info.workAreaTop;
    mWorkAreaLeft = info.workAreaLeft;
    mWorkAreaBottom = info.workAreaBottom;
    mWorkAreaRight = info.workAreaRight;

    ALOGI("setOutputManagerInfo: logical=%dx%d physical=%dx%d scale=%.2f "
          "logicalTransform=%d panelTransform=%d workArea=(%d,%d,%d,%d)",
          mLogicalWidth, mLogicalHeight, mPhysicalWidth, mPhysicalHeight,
          mScaleFactor, mLogicalTransform, mPanelTransform,
          mWorkAreaTop, mWorkAreaLeft, mWorkAreaBottom, mWorkAreaRight);

    updateLayerManagerOutputInfo();
}

void WaylandDisplay::updateLayerManagerOutputInfo() {
    if (mLayerManager) {
        // Output info for window placement uses the logical display dimensions
        // (already rotated to match Chrome's output coordinate space), not the
        // physical panel dimensions that width()/height() return for SF.
        int32_t w, h;
        if (mLogicalWidth > 0 && mScaleFactor > 0.0f) {
            w = static_cast<int32_t>(mLogicalWidth * mScaleFactor);
            h = static_cast<int32_t>(mLogicalHeight * mScaleFactor);
        } else {
            w = width();
            h = height();
        }
        // The work area rides along with the output size: a window that ash
        // merely maximizes is clamped into it, so the containers need it to
        // ask for a rectangle ash will actually grant. It changes without the
        // output size changing (the shelf appearing over a tablet app), which
        // is why it cannot be derived from w/h here.
        const OutputRect workArea = {
                workAreaX(), workAreaY(), workAreaWidth(), workAreaHeight()};
        mLayerManager->updateOutputInfo(w, h, displayTransform(), scaleFactor(),
                                        workArea);
    }
}

// --- Wayland flush ---

void WaylandDisplay::flush() {
    wl_display* display = mClient->getDisplay();
    wl_display_flush(display);
}

// --- VSync management ---

void WaylandDisplay::startVSync(HWC2::ComposerCallback* callback,
                                 Hwc2::Display displayId) {
    if (mTiming) {
        mTiming->start(callback, displayId);
    }
}

void WaylandDisplay::setVsyncEnabled(bool enabled) {
    if (mTiming) {
        mTiming->setEnabled(enabled);
    }
}

} // namespace android

#pragma clang diagnostic pop

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

// WaylandOverlayWindow: Floating overlay using zcr_toast_surface_v2.
// For TYPE_APPLICATION_OVERLAY (2038) windows.
//
// Uses toast_surface — a lightweight surface type with no Chrome window
// background or decorations. Transparent by default.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandOverlayWindow"

#include "WaylandOverlayWindow.h"

#include <inttypes.h>
#include <cstring>
#include <remote-shell-unstable-v2-client-protocol.h>
#include <aura-shell-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include "WaylandAccessibilityId.h"
#include "../WaylandLog.h"

namespace android {

WaylandOverlayWindow::WaylandOverlayWindow(wl_compositor* compositor,
                                           zcr_remote_shell_v2* remoteShell,
                                           wl_output* output, uint64_t layerId,
                                           float scaleFactor,
                                           zaura_shell* auraShell)
    : mOutput(output), mLayerId(layerId) {

    mSurfaceData.layerId = layerId;
    mSurfaceData.window = this;

    ALOGI("WaylandOverlayWindow: creating for layer %" PRIu64, layerId);

    mSurface = wl_compositor_create_surface(compositor);
    if (!mSurface) {
        ALOGE("failed to create wl_surface for overlay layer %" PRIu64, layerId);
        return;
    }

    wl_surface_set_user_data(mSurface, &mSurfaceData);

    // The zaura_surface exists only to carry the accessibility window id, and
    // it is created before the role object below because that is the order exo
    // documents for aura state that must be in place when the role is assigned
    // (WaylandAppWindow does the same). It sends nothing on its own and takes
    // no listener: this window type has no zaura events to act on.
    if (auraShell) {
        mAuraSurface = zaura_shell_get_aura_surface(auraShell, mSurface);
        if (!mAuraSurface) {
            ALOGW("failed to create zaura_surface for layer %" PRIu64
                  "; this window will not be readable by a screen "
                  "reader", layerId);
        }
    }

    mToastSurface = zcr_remote_shell_v2_get_toast_surface(remoteShell, mSurface);
    if (!mToastSurface) {
        ALOGE("failed to create zcr_toast_surface_v2 for overlay layer %" PRIu64, layerId);
        wl_surface_destroy(mSurface);
        mSurface = nullptr;
        return;
    }

    // Tell Chrome the buffer is at DSF resolution so it doesn't upscale
    if (scaleFactor > 0.0f) {
        uint32_t asUint;
        memcpy(&asUint, &scaleFactor, sizeof(float));
        zcr_toast_surface_v2_set_scale_factor(mToastSurface, asUint);
        ALOGI("WaylandOverlayWindow: set_scale_factor=%.2f for layer %" PRIu64,
              scaleFactor, layerId);
    }

    // Commit initial state
    wl_surface_commit(mSurface);

    ALOGI("WaylandOverlayWindow: created layer %" PRIu64, layerId);
}

WaylandOverlayWindow::~WaylandOverlayWindow() {
    ALOGI("WaylandOverlayWindow: destroying layer %" PRIu64, mLayerId);
    // Unmap before destroying: attach a null buffer and commit so Chrome clears
    // this surface's content and recomposites the output it was on. On the
    // primary the display keeps compositing so a bare destroy is enough, but an
    // OVERLAY used as an external-HDMI Presentation is the only content on that
    // panel — destroying the surface without unmapping can leave its last frame
    // latched on the external display (a ghost window after the app closes).
    if (mSurface) {
        wl_surface_attach(mSurface, nullptr, 0, 0);
        wl_surface_commit(mSurface);
    }
    if (mAuraSurface) {
        zaura_surface_destroy(mAuraSurface);
    }
    if (mToastSurface) {
        zcr_toast_surface_v2_destroy(mToastSurface);
    }
    if (mSurface) {
        wl_surface_destroy(mSurface);
    }
}

void WaylandOverlayWindow::setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                                              int32_t w, int32_t h) {
    if (mToastSurface && output && w > 0 && h > 0) {
        // Save Android display frame position (for input coordinate mapping)
        mFrameX = x;
        mFrameY = y;
        mBoundsW = w;
        mBoundsH = h;
        if (!mDragged) {
            // Only update Wayland position if not dragged by user
            const bool moved = (x != mBoundsX || y != mBoundsY);
            zcr_toast_surface_v2_set_bounds_in_output(mToastSurface, output,
                                                        x, y, w, h);
            if (moved) {
                ALOGI("WaylandOverlayWindow: layer %" PRIu64 " bounds (%d,%d) %dx%d",
                      mLayerId, x, y, w, h);
            }
            mBoundsX = x;
            mBoundsY = y;
        }
    }
}

void WaylandOverlayWindow::moveTo(int32_t x, int32_t y) {
    if (mToastSurface && mOutput && mBoundsW > 0 && mBoundsH > 0) {
        zcr_toast_surface_v2_set_bounds_in_output(mToastSurface, mOutput,
                                                    x, y, mBoundsW, mBoundsH);
        mBoundsX = x;
        mBoundsY = y;
        mDragged = true;
        wl_surface_commit(mSurface);
        ALOGVT("moveTo: layer %" PRIu64 " -> (%d, %d)", mLayerId, x, y);
    }
}


void WaylandOverlayWindow::setAccessibilityId(int32_t id) {
    publishAccessibilityId(mAuraSurface, id, mAccessibilityId, "overlay",
                           mLayerId);
}

} // namespace android

#pragma clang diagnostic pop

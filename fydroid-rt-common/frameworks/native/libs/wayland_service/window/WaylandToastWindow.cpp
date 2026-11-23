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

// WaylandToastWindow: Toast surface via zcr_toast_surface_v2.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandToastWindow"

#include "WaylandToastWindow.h"

#include <inttypes.h>
#include <cstring>
#include <remote-shell-unstable-v2-client-protocol.h>
#include <aura-shell-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include "WaylandAccessibilityId.h"

namespace android {

WaylandToastWindow::WaylandToastWindow(wl_compositor* compositor,
                                       zcr_remote_shell_v2* remoteShell,
                                       uint64_t layerId,
                                       float scaleFactor,
                                       zaura_shell* auraShell)
    : mLayerId(layerId) {

    mSurfaceData.layerId = layerId;
    mSurfaceData.window = this;

    ALOGI("WaylandToastWindow: creating for layer %" PRIu64, layerId);

    mSurface = wl_compositor_create_surface(compositor);
    if (!mSurface) {
        ALOGE("failed to create wl_surface for toast layer %" PRIu64, layerId);
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
        ALOGE("failed to create zcr_toast_surface_v2 for layer %" PRIu64, layerId);
        wl_surface_destroy(mSurface);
        mSurface = nullptr;
        return;
    }

    // Tell Chrome the buffer is at DSF resolution so it doesn't upscale
    if (scaleFactor > 0.0f) {
        uint32_t asUint;
        memcpy(&asUint, &scaleFactor, sizeof(float));
        zcr_toast_surface_v2_set_scale_factor(mToastSurface, asUint);
        ALOGI("WaylandToastWindow: set_scale_factor=%.2f for layer %" PRIu64,
              scaleFactor, layerId);
    }

    // Input-transparent: an empty input region means Chrome never routes
    // pointer, touch or keyboard focus to this surface — events land on
    // whatever is beneath it instead. A toast is a passive notice (Android's
    // own toast window is NOT_FOCUSABLE | NOT_TOUCHABLE); without this the
    // surface is hit-testable, so it steals focus from the window under it and
    // swallows clicks for the seconds it is on screen. An empty region (a
    // wl_region with no rectangles added) is not the same as a null one: null
    // means "the whole surface", which is the default we are overriding.
    if (wl_region* empty = wl_compositor_create_region(compositor)) {
        wl_surface_set_input_region(mSurface, empty);
        wl_region_destroy(empty);
    } else {
        ALOGW("failed to create empty input region for toast layer %" PRIu64,
              layerId);
    }

    // Commit initial state
    wl_surface_commit(mSurface);

    ALOGI("WaylandToastWindow: created layer %" PRIu64, layerId);
}

WaylandToastWindow::~WaylandToastWindow() {
    ALOGI("WaylandToastWindow: destroying layer %" PRIu64, mLayerId);
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

void WaylandToastWindow::setBoundsInOutput(wl_output* output,
                                            int32_t x, int32_t y,
                                            int32_t w, int32_t h) {
    if (mToastSurface && output && w > 0 && h > 0) {
        zcr_toast_surface_v2_set_bounds_in_output(mToastSurface, output,
                                                   x, y, w, h);
        mBoundsX = x;
        mBoundsY = y;
        mBoundsWidth = w;
        mBoundsHeight = h;
    }
}

void WaylandToastWindow::setCenteredBoundsInOutput(wl_output* output,
                                                   int32_t screenW,
                                                   int32_t screenH,
                                                   int32_t toastW,
                                                   int32_t toastH) {
    if (toastW <= 0 || toastH <= 0 || screenW <= 0 || screenH <= 0) return;
    // screenW/screenH already follow the current rotation, so use them as-is:
    // center horizontally, place 4/5 of the way down.
    const int32_t x = (screenW - toastW) / 2;
    const int32_t y = (screenH * 4) / 5 - toastH / 2;
    setBoundsInOutput(output, x, y, toastW, toastH);
}


void WaylandToastWindow::setAccessibilityId(int32_t id) {
    publishAccessibilityId(mAuraSurface, id, mAccessibilityId, "toast",
                           mLayerId);
}

} // namespace android

#pragma clang diagnostic pop

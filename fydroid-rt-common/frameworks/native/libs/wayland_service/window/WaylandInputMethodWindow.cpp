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

// WaylandInputMethodWindow: IME surface via zcr_input_method_surface_v2.
// Renders Android IME as a Chrome OS IME overlay.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandInputMethodWindow"

#include "WaylandInputMethodWindow.h"

#include <inttypes.h>
#include <remote-shell-unstable-v2-client-protocol.h>
#include <aura-shell-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include "WaylandAccessibilityId.h"

namespace android {

WaylandInputMethodWindow::WaylandInputMethodWindow(wl_compositor* compositor,
                                                   zcr_remote_shell_v2* remoteShell,
                                                   uint64_t layerId,
                                                   zaura_shell* auraShell)
    : mLayerId(layerId), mCompositor(compositor) {

    mSurfaceData.layerId = layerId;
    mSurfaceData.window = this;

    ALOGI("WaylandInputMethodWindow: creating for layer %" PRIu64, layerId);

    mSurface = wl_compositor_create_surface(compositor);
    if (!mSurface) {
        ALOGE("failed to create wl_surface for IME layer %" PRIu64, layerId);
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

    mImeSurface = zcr_remote_shell_v2_get_input_method_surface(remoteShell, mSurface);
    if (!mImeSurface) {
        ALOGE("failed to create zcr_input_method_surface_v2 for layer %" PRIu64, layerId);
        wl_surface_destroy(mSurface);
        mSurface = nullptr;
        return;
    }

    // No listener — zcr_input_method_surface_v2 has no events (only requests).

    // Commit initial state
    wl_surface_commit(mSurface);

    ALOGI("WaylandInputMethodWindow: created layer %" PRIu64, layerId);
}

WaylandInputMethodWindow::~WaylandInputMethodWindow() {
    ALOGI("WaylandInputMethodWindow: destroying layer %" PRIu64, mLayerId);
    if (mAuraSurface) {
        zaura_surface_destroy(mAuraSurface);
    }
    if (mImeSurface) {
        zcr_input_method_surface_v2_destroy(mImeSurface);
    }
    if (mSurface) {
        wl_surface_destroy(mSurface);
    }
}

void WaylandInputMethodWindow::setNoInputRegion() {
    if (!mSurface || !mCompositor) return;
    if (mNoInputRegion) return;  // unchanged

    // An empty wl_region — NOT a null one, which exo reads as the whole
    // surface (Surface::hit_test_region_, surface.cc).
    wl_region* region = wl_compositor_create_region(mCompositor);
    if (!region) return;
    wl_surface_set_input_region(mSurface, region);
    wl_region_destroy(region);
    wl_surface_commit(mSurface);
    mNoInputRegion = true;
    mContentTop = -1;  // no content top is in effect any more
    mInputRects.clear();
    ALOGI("WaylandInputMethodWindow: input region = empty (IME takes no touch) "
          "on %dx%d", mBoundsWidth, mBoundsHeight);
}

void WaylandInputMethodWindow::setContentTop(int32_t contentTop) {
    if (!mSurface || !mCompositor) return;
    if (contentTop <= 0) return;              // unknown — leave input unrestricted
    if (contentTop == mContentTop && !mNoInputRegion) return;  // unchanged
    if (mBoundsHeight <= contentTop) return;  // nothing left to be a keyboard

    wl_region* region = wl_compositor_create_region(mCompositor);
    if (!region) return;
    // Surface-local: the buffer starts at the window origin, so the keys are
    // everything below contentTop.
    wl_region_add(region, 0, contentTop, mBoundsWidth, mBoundsHeight - contentTop);
    wl_surface_set_input_region(mSurface, region);
    wl_region_destroy(region);
    wl_surface_commit(mSurface);
    mContentTop = contentTop;
    mNoInputRegion = false;
    mInputRects.clear();
    ALOGI("WaylandInputMethodWindow: input region = (0,%d %dx%d) of %dx%d",
          contentTop, mBoundsWidth, mBoundsHeight - contentTop,
          mBoundsWidth, mBoundsHeight);
}

void WaylandInputMethodWindow::setInputRects(const std::vector<int32_t>& rects) {
    if (!mSurface || !mCompositor || rects.empty()) return;
    if (rects == mInputRects) return;  // unchanged

    wl_region* region = wl_compositor_create_region(mCompositor);
    if (!region) return;
    for (size_t i = 0; i + 3 < rects.size(); i += 4) {
        const int32_t w = rects[i + 2] - rects[i];
        const int32_t h = rects[i + 3] - rects[i + 1];
        if (w > 0 && h > 0) wl_region_add(region, rects[i], rects[i + 1], w, h);
    }
    wl_surface_set_input_region(mSurface, region);
    wl_region_destroy(region);
    wl_surface_commit(mSurface);
    mInputRects = rects;
    mContentTop = -1;
    mNoInputRegion = false;
    ALOGI("WaylandInputMethodWindow: input region = %zu IME rect(s), first "
          "(%d,%d %dx%d) of %dx%d", rects.size() / 4, rects[0], rects[1],
          rects[2] - rects[0], rects[3] - rects[1], mBoundsWidth, mBoundsHeight);
}

void WaylandInputMethodWindow::setBoundsInOutput(wl_output* output,
                                                  int32_t x, int32_t y,
                                                  int32_t w, int32_t h) {
    if (mImeSurface && output && w > 0 && h > 0) {
        zcr_input_method_surface_v2_set_bounds_in_output(mImeSurface, output,
                                                          x, y, w, h);
        mBoundsX = x;
        mBoundsY = y;
        mBoundsWidth = w;
        mBoundsHeight = h;
    }
}


void WaylandInputMethodWindow::setAccessibilityId(int32_t id) {
    publishAccessibilityId(mAuraSurface, id, mAccessibilityId, "ime",
                           mLayerId);
}

} // namespace android

#pragma clang diagnostic pop

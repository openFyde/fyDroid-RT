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

// WaylandOutput: Wraps wl_output, receives geometry/mode events from
// Chrome's Exo compositor.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandOutput"

#include "WaylandOutput.h"

#include <wayland-client.h>
#include <log/log.h>

namespace android {

// --- Static wl_output listener callbacks ---

static void outputGeometry(void* data, struct wl_output*, int32_t, int32_t,
                           int32_t, int32_t, int32_t, const char*, const char*,
                           int32_t transform) {
    static_cast<WaylandOutput*>(data)->onGeometryTransform(transform);
}

static void outputMode(void* data, struct wl_output*, uint32_t flags,
                       int32_t width, int32_t height, int32_t refresh) {
    static_cast<WaylandOutput*>(data)->onMode(flags, width, height, refresh);
}

static void outputDone(void*, struct wl_output*) {}
static void outputScale(void*, struct wl_output*, int32_t) {}

static const struct wl_output_listener sOutputListener = {
    .geometry = outputGeometry,
    .mode = outputMode,
    .done = outputDone,
    .scale = outputScale,
};

// --- WaylandOutput implementation ---

WaylandOutput::WaylandOutput(wl_output* output, ChangeCallback onChange)
    : mOutput(output), mOnChange(std::move(onChange)) {
    if (mOutput) {
        wl_output_add_listener(mOutput, &sOutputListener, this);
    }
}

WaylandOutput::~WaylandOutput() {
    if (mOutput) {
        wl_output_destroy(mOutput);
        ALOGI("wl_output destroyed");
    }
}

void WaylandOutput::onMode(uint32_t flags, int32_t width, int32_t height,
                           int32_t refresh) {
    // WL_OUTPUT_MODE_CURRENT (0x1) indicates the current active mode
    if (flags & 0x1) {
        mWidth = width;
        mHeight = height;
        mRefreshMHz = refresh;
        ALOGI("wl_output mode: %dx%d @ %d mHz (current)", width, height, refresh);
        if (mOnChange) {
            mOnChange();
        }
    } else {
        ALOGI("wl_output mode: %dx%d @ %d mHz (not current)", width, height, refresh);
    }
}

void WaylandOutput::onGeometryTransform(int32_t transform) {
    mTransform = transform;
    ALOGI("wl_output transform: %d", transform);
    if (mOnChange) {
        mOnChange();
    }
}

} // namespace android

#pragma clang diagnostic pop

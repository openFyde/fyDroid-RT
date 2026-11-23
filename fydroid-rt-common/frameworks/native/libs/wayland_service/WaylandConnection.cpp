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

// WaylandConnection: Owns the raw wl_display* socket connection.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandConnection"

#include "WaylandConnection.h"

#include <wayland-client.h>
#include <log/log.h>

namespace android {

WaylandConnection::WaylandConnection() = default;

WaylandConnection::~WaylandConnection() {
    disconnect();
}

bool WaylandConnection::connect(const char* socketPath) {
    if (mDisplay) {
        ALOGW("already connected — disconnect first");
        return false;
    }
    mDisplay = wl_display_connect(socketPath);
    if (!mDisplay) {
        ALOGE("failed to connect to %s", socketPath);
        return false;
    }
    ALOGI("connected to %s", socketPath);
    return true;
}

void WaylandConnection::disconnect() {
    if (mDisplay) {
        wl_display_disconnect(mDisplay);
        mDisplay = nullptr;
        ALOGI("disconnected from Wayland");
    }
}

int WaylandConnection::roundtrip() {
    return wl_display_roundtrip(mDisplay);
}

int WaylandConnection::flush() {
    return wl_display_flush(mDisplay);
}

int WaylandConnection::fd() const {
    return mDisplay ? wl_display_get_fd(mDisplay) : -1;
}

} // namespace android

#pragma clang diagnostic pop

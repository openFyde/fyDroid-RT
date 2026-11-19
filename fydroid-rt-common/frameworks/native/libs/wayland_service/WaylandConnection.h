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

struct wl_display;

namespace android {

// Owns the raw Wayland socket connection (wl_display*).
// Provides connect/disconnect and dispatch primitives.
class WaylandConnection {
public:
    WaylandConnection();
    ~WaylandConnection();

    WaylandConnection(const WaylandConnection&) = delete;
    WaylandConnection& operator=(const WaylandConnection&) = delete;

    // Connect to the Wayland compositor at the given socket path.
    bool connect(const char* socketPath);

    // Disconnect and release the wl_display.
    void disconnect();

    // Raw wl_display pointer (null if not connected).
    wl_display* display() const { return mDisplay; }

    // Blocking roundtrip — dispatches events until all pending requests
    // have been processed by the server.
    int roundtrip();

    // Flush outgoing requests to the compositor.
    int flush();

    // File descriptor for the Wayland socket (for poll/epoll).
    int fd() const;

private:
    wl_display* mDisplay = nullptr;
};

} // namespace android

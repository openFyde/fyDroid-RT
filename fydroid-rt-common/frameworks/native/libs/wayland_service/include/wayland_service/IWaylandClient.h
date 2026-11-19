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

struct wl_display;
struct wl_compositor;
struct wl_subcompositor;
struct wl_shm;
struct wl_seat;
struct wl_surface;
struct wl_region;
struct zcr_remote_shell_v2;
struct zwp_linux_dmabuf_v1;
struct zcr_vsync_timing_v1;
struct zaura_shell;

namespace android {

// Wayland client connection and global object access.
// Wraps wl_display and provides access to bound global interfaces.
class IWaylandClient {
public:
    virtual ~IWaylandClient() = default;

    // Core Wayland globals
    virtual wl_display* getDisplay() = 0;
    virtual wl_compositor* getCompositor() = 0;
    virtual wl_subcompositor* getSubcompositor() = 0;
    virtual wl_shm* getShm() = 0;
    virtual wl_seat* getSeat() = 0;

    // Chrome OS Exo extensions
    virtual zcr_remote_shell_v2* getRemoteShell() = 0;
    virtual zwp_linux_dmabuf_v1* getLinuxDmabuf() = 0;
    virtual zaura_shell* getAuraShell() = 0;

    // Surface creation helpers
    virtual wl_surface* createSurface() = 0;
    virtual wl_region* createRegion() = 0;

    // Connection management
    virtual int dispatch() = 0;
    virtual int flush() = 0;
    virtual int getFd() = 0;
};

} // namespace android

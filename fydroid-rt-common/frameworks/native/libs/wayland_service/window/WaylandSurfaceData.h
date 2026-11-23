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

#include <cstdint>

struct wl_surface;

namespace android {

class WaylandLayerContainer;
class WaylandWindow;

// Fixed-lifetime data stored on wl_surface via user data pointer.
// Enables O(1) lookup of task/container/window from any wl_surface*.
//
// For surfaces with a wl_surface_listener (WaylandAppWindow), this struct
// is passed as the listener data — wl_surface_add_listener and
// wl_surface_set_user_data share the same internal field.
struct WaylandSurfaceData {
    int32_t taskId = 0;                          // Android task ID (0 for standalone)
    uint64_t layerId = 0;                        // SF layer ID
    WaylandLayerContainer* container = nullptr;  // owning container (app windows only)
    WaylandWindow* window = nullptr;             // owning window (all types)

    // Set only on a surface that stands in for another one for input: the dim
    // scrim a modal draws around its dialog card (WaylandModalWindow). Every
    // lookup below resolves through it, so the scrim is the dialog as far as
    // focus and activation go, and events on it are delivered as if they hit
    // the target at (x - inputOriginX, y - inputOriginY) — i.e. OUTSIDE the
    // card, which is how Android learns about a tap outside a dialog.
    const WaylandSurfaceData* inputTarget = nullptr;
    int32_t inputOriginX = 0;  // target's origin, in this surface's pixels
    int32_t inputOriginY = 0;

    // Static helpers — retrieve fields from any wl_surface*.
    static int32_t getTaskId(wl_surface* surface);
    static WaylandLayerContainer* getSurfaceContainer(wl_surface* surface);
    static WaylandWindow* getSurfaceWindow(wl_surface* surface);
    // Where the input target sits in `surface`'s coordinates; (0,0) for every
    // ordinary surface. Subtract it from a surface-local position before use.
    static void getInputOrigin(wl_surface* surface, double* x, double* y);
};

} // namespace android

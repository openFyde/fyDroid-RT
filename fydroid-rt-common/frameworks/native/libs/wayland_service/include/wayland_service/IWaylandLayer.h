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
#include <ui/Rect.h>
#include <ui/Region.h>

struct wl_surface;

namespace android {

class GraphicBuffer;

// A single Wayland surface corresponding to an Android composition layer.
// Wraps a wl_surface and handles buffer attachment and damage tracking.
class IWaylandLayer {
public:
    virtual ~IWaylandLayer() = default;

    // Get the underlying wl_surface.
    virtual wl_surface* getSurface() = 0;

    // Attach a DMA-BUF backed buffer to this surface via zwp_linux_dmabuf_v1.
    virtual bool attachBuffer(const GraphicBuffer& buffer, int fenceFd) = 0;

    // Set the damaged region for the current frame.
    virtual void setDamage(const Region& damage) = 0;

    // Set the opaque region.
    virtual void setOpaqueRegion(const Region& opaque) = 0;

    // Set position relative to parent surface.
    virtual void setPosition(int32_t x, int32_t y) = 0;

    // Commit pending state.
    virtual void commit() = 0;

    // Get the layer ID.
    virtual int32_t getId() const = 0;
};

} // namespace android

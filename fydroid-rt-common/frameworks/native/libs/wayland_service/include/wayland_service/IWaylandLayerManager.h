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
#include <memory>

namespace android {

class IWaylandLayer;

// Manages the mapping between Android layers/tasks and Wayland surfaces.
// Owns the lifecycle of WaylandLayer objects and their associated
// zcr_remote_surface_v2 / wl_surface pairs.
class IWaylandLayerManager {
public:
    virtual ~IWaylandLayerManager() = default;

    // Create or retrieve a WaylandLayer for the given Android layer ID.
    virtual IWaylandLayer* getOrCreateLayer(int32_t layerId) = 0;

    // Remove a layer when an Android layer is destroyed.
    virtual void removeLayer(int32_t layerId) = 0;

    // Get an existing layer, or nullptr if not found.
    virtual IWaylandLayer* getLayer(int32_t layerId) = 0;

    // Called each frame before composition to update layer state.
    virtual void prepareFrame() = 0;

    // Called each frame after composition to submit buffers.
    virtual void postFrame() = 0;
};

} // namespace android

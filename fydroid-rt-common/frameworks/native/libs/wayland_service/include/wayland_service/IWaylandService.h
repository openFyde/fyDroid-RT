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

#include <memory>
#include <string>

namespace android {

class IWaylandClient;
class IWaylandLayerManager;
class IWaylandInputManager;

// Top-level interface for the Wayland service.
// Owns the Wayland connection, layer manager, and input manager.
// Created by WaylandComposer and driven by SurfaceFlinger's main loop.
class IWaylandService {
public:
    virtual ~IWaylandService() = default;

    // Initialize the Wayland connection to the compositor.
    // socket_path: path to the Wayland socket (e.g. "/var/run/chrome/wayland-0")
    virtual bool initialize(const std::string& socket_path) = 0;

    // Dispatch pending Wayland events. Called from SurfaceFlinger's main loop.
    virtual void dispatch() = 0;

    // Flush outgoing Wayland requests.
    virtual void flush() = 0;

    // Get the underlying client connection.
    virtual IWaylandClient& getClient() = 0;

    // Get the layer manager.
    virtual IWaylandLayerManager& getLayerManager() = 0;

    // Get the input manager.
    virtual IWaylandInputManager& getInputManager() = 0;

    // Check if connected to the compositor.
    virtual bool isConnected() const = 0;
};

// Factory function.
std::unique_ptr<IWaylandService> createWaylandService();

} // namespace android

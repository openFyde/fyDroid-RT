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

#include <atomic>
#include <memory>
#include <thread>

#include "include/wayland_service/IWaylandClient.h"

struct wl_output;
struct zcr_vsync_feedback_v1;
struct surface_augmenter;

namespace android {

class WaylandConnection;
class WaylandDataDevice;
class WaylandDisplay;
class WaylandGlobals;
class WaylandInputManager;

// IWaylandClient implementation. Owns WaylandConnection + WaylandGlobals.
// Orchestrates initialization (connect → registry → roundtrips → finalize).
class WaylandClient : public IWaylandClient {
public:
    WaylandClient();
    ~WaylandClient() override;

    WaylandClient(const WaylandClient&) = delete;
    WaylandClient& operator=(const WaylandClient&) = delete;

    // Connect to Exo and set up all globals and displays.
    bool init();

    bool isInitialized() const;

    // Primary display accessor (convenience)
    WaylandDisplay* primaryDisplay() const;

    // Access the globals object
    WaylandGlobals* globals() const { return mGlobals.get(); }

    // Return the 0-based display index for a given wl_output, or -1 if not found.
    int32_t displayIndexForOutput(wl_output* output) const;

    // IWaylandClient interface
    wl_display* getDisplay() override;
    wl_compositor* getCompositor() override;
    wl_subcompositor* getSubcompositor() override;
    wl_shm* getShm() override;
    wl_seat* getSeat() override;
    zcr_remote_shell_v2* getRemoteShell() override;
    zwp_linux_dmabuf_v1* getLinuxDmabuf() override;
    zaura_shell* getAuraShell() override;
    zcr_vsync_feedback_v1* getVsyncFeedback();
    surface_augmenter* getSurfaceAugmenter();
    wl_surface* createSurface() override;
    wl_region* createRegion() override;
    int dispatch() override;
    int flush() override;
    int getFd() override;

private:
    // One dispatcher per connection. libwayland runs listeners with the
    // display mutex released, so a second thread dispatching the same queue
    // interleaves the handlers of one protocol frame (e.g. wl_touch.up and
    // its wl_touch.frame) and the unsynchronised input handlers lose events.
    void startEventThread();

    std::unique_ptr<WaylandConnection> mConnection;
    std::unique_ptr<WaylandGlobals> mGlobals;
    std::unique_ptr<WaylandInputManager> mInputManager;
    std::unique_ptr<WaylandDataDevice> mDataDevice;

    std::thread mEventThread;
    std::atomic<bool> mEventRunning{false};
};

} // namespace android

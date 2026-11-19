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
#include <unordered_map>
#include <vector>

#include "WaylandLayerContainer.h"
#include "../window/WaylandWindowTypes.h"

struct wl_output;

namespace android {

class WaylandClient;
class WaylandLayerDrmBufferManager;

// Pure container registry. Maps task IDs and standalone layer IDs to
// WaylandLayerContainers. No per-layer HWC state tracking — all layer
// state is managed by WaylandHwc2Composer's VirtualDisplayData.
//
// Task containers: one per Android task, keyed by taskId.
// Standalone containers: one per standalone layer (IME, toast, overlay,
//   notification), keyed by layerId.
class WaylandLayerManager {
public:
    // client: Wayland globals (not owned — outlives this manager)
    // output: per-display wl_output (not owned)
    WaylandLayerManager(WaylandClient* client, wl_output* output);
    ~WaylandLayerManager();

    WaylandLayerManager(const WaylandLayerManager&) = delete;
    WaylandLayerManager& operator=(const WaylandLayerManager&) = delete;

    // --- Task container management ---

    // Register a container for a task. Creates if not exists, updates frame
    // if already registered.
    void registerContainer(int32_t taskId, const WaylandLayerContainer::Rect& frame);

    // Find a container by task ID. Returns nullptr if not found.
    WaylandLayerContainer* findContainerByTaskId(int32_t taskId) const;

    // Hand the dialog dim, and the rectangle of it that takes clicks, to every
    // modal on this display — task-keyed (dialog tasks) and standalone
    // (dialogs split out of their app) alike.
    void setModalScrim(float alpha, int32_t x, int32_t y, int32_t w, int32_t h);

    // Destroy a task container.
    void destroyContainer(int32_t taskId);

    // Set the session ID for a task container.
    void setContainerSessionId(int32_t taskId, int32_t sessionId);

    // --- Standalone container management ---

    // Register a standalone container for a non-task layer.
    // Creates the container and triggers window creation.
    WaylandLayerContainer* registerStandaloneContainer(uint64_t layerId,
                                                        WaylandWindowCategory category);
    // Overload with notification key for NOTIFICATION surfaces.
    WaylandLayerContainer* registerStandaloneContainer(uint64_t layerId,
                                                        WaylandWindowCategory category,
                                                        const std::string& notifKey);

    // Find a standalone container by layer ID. Returns nullptr if not found.
    WaylandLayerContainer* findStandaloneContainer(uint64_t layerId) const;

    // Find the NOTIFICATION container showing a given notification key.
    // Returns nullptr if that notification is not on this display.
    WaylandLayerContainer* findStandaloneContainerByNotifKey(
            const std::string& notifKey) const;

    // Find the standalone container of a category there can only be one of —
    // the IME. Returns the first match, or nullptr.
    WaylandLayerContainer* findStandaloneContainerByCategory(
            WaylandWindowCategory category) const;

    // Destroy a standalone container.
    void destroyStandaloneContainer(uint64_t layerId);

    // --- Common operations ---

    // Update output info and propagate to all containers. `workArea` is the
    // output's work area in output pixels — see
    // WaylandLayerContainer::allowedWindowRect().
    void updateOutputInfo(int32_t width, int32_t height, int32_t transform,
                          float scaleFactor, const OutputRect& workArea);

    // Apply the Chrome OS layout mode to all task containers. Tablet mode
    // is a system-wide state.
    void applyLayoutMode(ChromeLayoutMode mode);

    // Repaint every task caption in the ChromeOS header colours. Like the
    // layout mode above this is system-wide state, and for the same reason it
    // has to be pushed: the palette changes when the wallpaper or the colour
    // scheme does, and a window already on screen would otherwise keep the
    // caption it was created with. Standalone containers are skipped — an IME,
    // toast, overlay or notification surface has no caption to paint.
    void applyFrameColors(uint32_t active, uint32_t inactive);

    // Set the active (top) task when zaura_shell.activated fires.
    // Tells Android WM to bring the task to front.
    void setActiveTask(int32_t taskId);

    // A surface with no task (a standalone dialog / IME / overlay) took
    // activation. Tell the current active task it lost focus and remember it,
    // so that re-activating it afterwards is seen as a real change — without
    // this, setActiveTask() short-circuits on taskId == mActiveTaskId and the
    // task never gets onFocusChanged(true) back. Does not touch Android-side
    // focus: the dialog is what the user is typing into while it is up.
    void clearActiveTask();

    // The standalone surface went away — give focus back to the task it
    // displaced (see clearActiveTask). Chrome does not re-activate the task on
    // its own, so this is the only thing that restores it.
    void restoreDisplacedTask();

    // Get the active (focused) task's Chrome OS window offset in physical pixels.
    // Returns false if no active task.
    bool getActiveTaskWindowOffset(int32_t* outX, int32_t* outY) const;

    // Move all containers from this manager to another (for hotplug disconnect).
    // Updates each container's output to the target manager's output.
    void migrateAllContainersTo(WaylandLayerManager* target);

    // Returns the task ID of the active dialog, or -1 if none.
    int32_t activeDialogTaskId() const;

    // Access the DRM buffer manager (for importing buffers as wl_buffer).
    WaylandLayerDrmBufferManager* bufferManager() const { return mBufferManager.get(); }

    // Drain closed containers — returns closed task IDs.
    // Also drains orphaned containers whose task is gone.
    struct DrainResult {
        std::vector<int32_t> closedTaskIds;
        std::vector<uint64_t> closedStandaloneLayerIds;
    };
    DrainResult drainClosedContainers();

private:
    WaylandClient* mClient;   // not owned — Wayland globals
    wl_output* mOutput;        // not owned — per-display output
    std::unique_ptr<WaylandLayerDrmBufferManager> mBufferManager;

    // Task containers: one per Android task
    std::unordered_map<int32_t, std::unique_ptr<WaylandLayerContainer>> mTaskContainers;

    // Snap state of a destroyed task container, held for the recreation that
    // follows an activity relaunch (see destroyContainer). Time-bounded: a task
    // that is really going away must not hand its snap to whatever is
    // registered under that id next. The gap measured on device is ~100ms
    // (destroy 11:22:35.932 → create 11:22:36.032).
    struct PendingSnap {
        WaylandLayerContainer::SnapCarry carry;
        int64_t savedNs = 0;
    };
    std::unordered_map<int32_t, PendingSnap> mSnapCarryover;
    static constexpr int64_t kSnapCarryWindowNs = 2LL * 1000 * 1000 * 1000;

    // Standalone containers: one per standalone layer (IME, toast, overlay, notification)
    std::unordered_map<uint64_t, std::unique_ptr<WaylandLayerContainer>> mStandaloneContainers;

    // Active task — set by zaura_shell.activated → setFocusedTask
    WaylandTaskResizer mTaskResizer;
    int32_t mActiveTaskId = -1;
    // Task displaced by a task-less surface, restored when that surface is
    // destroyed. -1 = nothing displaced.
    int32_t mDisplacedTaskId = -1;

    // Output info cache
    int32_t mOutputWidth = 0;
    int32_t mOutputHeight = 0;
    int32_t mOutputTransform = 0;
    float mScaleFactor = 1.0f;
    OutputRect mWorkArea;
};

} // namespace android

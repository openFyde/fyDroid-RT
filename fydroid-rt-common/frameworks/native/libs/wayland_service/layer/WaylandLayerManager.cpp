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

// WaylandLayerManager: Pure container registry.
// Task containers map taskId → WaylandLayerContainer.
// Standalone containers map layerId → WaylandLayerContainer.
// No per-layer HWC state — all layer state lives in WaylandHwc2Composer's
// VirtualDisplayData.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandLayerManager"

#include "WaylandLayerManager.h"
#include "WaylandLayerContainer.h"
#include "WaylandLayerDrmBufferManager.h"
#include "../WaylandClient.h"
#include "../input/WaylandInputManager.h"

#include <wayland-client.h>

#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

WaylandLayerManager::WaylandLayerManager(WaylandClient* client, wl_output* output)
    : mClient(client),
      mOutput(output),
      mBufferManager(std::make_unique<WaylandLayerDrmBufferManager>(
              client->getLinuxDmabuf(), client->getDisplay())) {}

WaylandLayerManager::~WaylandLayerManager() {
    mStandaloneContainers.clear();
    mTaskContainers.clear();
    mBufferManager.reset();
}

// --- Task container management ---

static int64_t nowMonotonicNs() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec * 1000000000LL + ts.tv_nsec;
}

void WaylandLayerManager::registerContainer(int32_t taskId,
                                            const WaylandLayerContainer::Rect& frame) {
    auto it = mTaskContainers.find(taskId);
    if (it != mTaskContainers.end()) {
        it->second->setContainerFrame(frame);
        ALOGVT("container updated: taskId=%d", taskId);
        return;
    }

    // Create new container
    auto container = std::make_unique<WaylandLayerContainer>(
            mClient, mOutput,
            0 /*containerLayerId — set later via HWC*/, taskId, frame);
    container->updateOutputInfo(mOutputWidth, mOutputHeight,
                                mOutputTransform, mScaleFactor, mWorkArea);

    // If this task was snapped moments ago, this is the rebuild half of an
    // activity relaunch and ash still has it in the split. Restore the flag
    // before the window is created, because applyLayoutMode() runs off the back
    // of that and would otherwise treat a snapped task as a fresh tablet window
    // and request maximize — which Chrome answers by breaking the snap group.
    auto pending = mSnapCarryover.find(taskId);
    if (pending != mSnapCarryover.end()) {
        if (nowMonotonicNs() - pending->second.savedNs < kSnapCarryWindowNs) {
            container->restoreSnapCarry(pending->second.carry);
        }
        mSnapCarryover.erase(pending);
    }

    ALOGI("container registered: taskId=%d frame=(%d,%d,%d,%d)",
          taskId, frame.left, frame.top, frame.right, frame.bottom);

    mTaskContainers[taskId] = std::move(container);
}

WaylandLayerContainer* WaylandLayerManager::findContainerByTaskId(int32_t taskId) const {
    auto it = mTaskContainers.find(taskId);
    if (it != mTaskContainers.end() && !it->second->isClosed()) {
        return it->second.get();
    }
    return nullptr;
}

void WaylandLayerManager::setModalScrim(float alpha, int32_t x, int32_t y,
                                        int32_t w, int32_t h) {
    for (auto& [taskId, container] : mTaskContainers) {
        if (container) container->setModalScrim(alpha, x, y, w, h);
    }
    for (auto& [layerId, container] : mStandaloneContainers) {
        if (container) container->setModalScrim(alpha, x, y, w, h);
    }
}

void WaylandLayerManager::destroyContainer(int32_t taskId) {
    auto it = mTaskContainers.find(taskId);
    if (it != mTaskContainers.end()) {
        ALOGI("destroying container taskId=%d", taskId);
        // Keep the snap for the container that may replace this one in a few
        // milliseconds. Only a snapped container leaves anything behind, and
        // only for kSnapCarryWindowNs — see registerContainer.
        const auto carry = it->second->snapCarry();
        if (carry.snapped) {
            mSnapCarryover[taskId] = {carry, nowMonotonicNs()};
        } else {
            mSnapCarryover.erase(taskId);
        }
        mTaskContainers.erase(it);
    }

    // A modal opened by this task is a floating activity *inside* it, so
    // Android finishes it along with the task and its container is torn down by
    // the normal virtual-display teardown. Nothing to close here — only the
    // pending restore has to go, or the task would be restored after it died.
    if (taskId > 0 && taskId == mDisplacedTaskId) {
        mDisplacedTaskId = -1;
    }
}

void WaylandLayerManager::setContainerSessionId(int32_t taskId, int32_t sessionId) {
    auto* container = findContainerByTaskId(taskId);
    if (container) {
        container->setSessionId(sessionId);
        ALOGI("container taskId=%d: sessionId set to %d", taskId, sessionId);
    }
}

// --- Standalone container management ---

WaylandLayerContainer* WaylandLayerManager::registerStandaloneContainer(
        uint64_t layerId, WaylandWindowCategory category) {
    auto it = mStandaloneContainers.find(layerId);
    if (it != mStandaloneContainers.end()) {
        // The category has to be authoritative, not first-come. A layer's
        // generic metadata arrives one key per setLayerGenericMetadata call, in
        // SF's unordered_map order, so on an IME layer "IsFloatingActivity" can
        // land before "WindowType" and claim it as MODAL; returning that stale
        // container left the keyboard as a WaylandModalWindow, so it never
        // reached Chrome as an input-method surface and nothing was drawn.
        // Rebuild on a category change instead. (This cannot ping-pong: the
        // IsFloatingActivity handler leaves an INPUT_METHOD display alone, so
        // the conversion only ever runs the one way.)
        if (it->second->windowCategory() == category) {
            return it->second.get();
        }
        ALOGI("standalone container layerId=%" PRIu64
              ": category %d -> %d, rebuilding",
              layerId, static_cast<int>(it->second->windowCategory()),
              static_cast<int>(category));
        if (it->second->window()) {
            if (auto* mgr = WaylandInputManager::instance()) {
                mgr->clearFocusIfWindow(it->second->window());
            }
        }
        mStandaloneContainers.erase(it);
    }

    // Create standalone container (taskId=0, empty frame)
    WaylandLayerContainer::Rect emptyFrame = {0, 0, 0, 0};
    auto container = std::make_unique<WaylandLayerContainer>(
            mClient, mOutput,
            layerId, 0 /*taskId*/, emptyFrame);
    container->updateOutputInfo(mOutputWidth, mOutputHeight,
                                mOutputTransform, mScaleFactor, mWorkArea);

    // A dialog split out of its app belongs to the active app's window, and is
    // created as its transient child so ash stacks the two together (see
    // WaylandModalWindow). Like the shelf state below, this goes by the
    // active task: the dialog has no task of its own here.
    if (category == WaylandWindowCategory::MODAL) {
        auto active = mTaskContainers.find(mActiveTaskId);
        if (active != mTaskContainers.end() && !active->second->isClosed() &&
            active->second->windowCategory() == WaylandWindowCategory::APP &&
            active->second->window()) {
            container->setModalParent(active->second->window()->auraSurface());
        }
    }

    // Create the window immediately
    container->ensureWindowForCategory(category, layerId);

    ALOGI("standalone container registered: layerId=%" PRIu64 " category=%d",
          layerId, static_cast<int>(category));

    // Keep the shelf state across a dialog. ash reads the auto-hide flag off
    // whatever window is active, and a modal takes activation as soon as it
    // appears — so a dialog opened over a fullscreen app resets the shelf to
    // visible unless it carries the flag too. That reset is what shrinks the
    // work area, makes Chrome resize the app window to it, and leaves a black
    // band where the shelf was once the shelf auto-hides again.
    if (category == WaylandWindowCategory::MODAL) {
        auto active = mTaskContainers.find(mActiveTaskId);
        if (active != mTaskContainers.end() && active->second->shelfAutoHide()) {
            container->inheritShelfAutoHide();
        }
    }

    WaylandLayerContainer* ptr = container.get();
    mStandaloneContainers[layerId] = std::move(container);
    return ptr;
}

WaylandLayerContainer* WaylandLayerManager::registerStandaloneContainer(
        uint64_t layerId, WaylandWindowCategory category,
        const std::string& notifKey) {
    auto it = mStandaloneContainers.find(layerId);
    if (it != mStandaloneContainers.end()) {
        return it->second.get();
    }

    // Destroy any existing container with the same notification key.
    // This handles the race where closeNotificationWindow → destroyVirtualDisplay
    // hasn't completed yet when createNotificationWindow is called again for
    // the same key. Chrome rejects duplicate zcr_notification_surface_v2 keys.
    if (!notifKey.empty()) {
        for (auto jt = mStandaloneContainers.begin(); jt != mStandaloneContainers.end(); ) {
            if (jt->second->notifKey() == notifKey) {
                ALOGI("registerStandaloneContainer: destroying old container "
                      "layerId=%" PRIu64 " with duplicate notifKey=%s",
                      jt->first, notifKey.c_str());
                if (jt->second->window()) {
                    if (auto* mgr = WaylandInputManager::instance()) {
                        mgr->clearFocusIfWindow(jt->second->window());
                    }
                }
                jt = mStandaloneContainers.erase(jt);
            } else {
                ++jt;
            }
        }
    }

    // Create standalone container with notification key
    WaylandLayerContainer::Rect emptyFrame = {0, 0, 0, 0};
    auto container = std::make_unique<WaylandLayerContainer>(
            mClient, mOutput,
            layerId, 0 /*taskId*/, emptyFrame);
    container->updateOutputInfo(mOutputWidth, mOutputHeight,
                                mOutputTransform, mScaleFactor, mWorkArea);
    container->setNotifKey(notifKey);

    // Create the window immediately
    container->ensureWindowForCategory(category, layerId);

    ALOGI("standalone container registered: layerId=%" PRIu64
          " category=%d notifKey=%s",
          layerId, static_cast<int>(category), notifKey.c_str());

    WaylandLayerContainer* ptr = container.get();
    mStandaloneContainers[layerId] = std::move(container);
    return ptr;
}

WaylandLayerContainer* WaylandLayerManager::findStandaloneContainer(uint64_t layerId) const {
    auto it = mStandaloneContainers.find(layerId);
    if (it != mStandaloneContainers.end() && !it->second->isClosed()) {
        return it->second.get();
    }
    return nullptr;
}

WaylandLayerContainer* WaylandLayerManager::findStandaloneContainerByNotifKey(
        const std::string& notifKey) const {
    if (notifKey.empty()) return nullptr;
    for (auto& [layerId, container] : mStandaloneContainers) {
        (void)layerId;
        if (container->isClosed()) continue;
        if (container->windowCategory() == WaylandWindowCategory::NOTIFICATION &&
            container->notifKey() == notifKey) {
            return container.get();
        }
    }
    return nullptr;
}

WaylandLayerContainer* WaylandLayerManager::findStandaloneContainerByCategory(
        WaylandWindowCategory category) const {
    for (auto& [layerId, container] : mStandaloneContainers) {
        (void)layerId;
        if (container->isClosed()) continue;
        if (container->windowCategory() == category) {
            return container.get();
        }
    }
    return nullptr;
}

void WaylandLayerManager::destroyStandaloneContainer(uint64_t layerId) {
    auto it = mStandaloneContainers.find(layerId);
    if (it != mStandaloneContainers.end()) {
        ALOGI("destroying standalone container layerId=%" PRIu64, layerId);
        // Clear dangling focus pointers before destroying the window
        if (it->second->window()) {
            if (auto* mgr = WaylandInputManager::instance()) {
                mgr->clearFocusIfWindow(it->second->window());
            }
        }
        mStandaloneContainers.erase(it);
        // Hand focus back once the last *modal* is gone. A sign-in flow puts up
        // more than one (the log showed a second dialog created while the first
        // was still alive and destroyed 16ms after it), so restoring on the
        // first teardown would consume the displaced task while a dialog was
        // still on screen.
        //
        // Only modals count. Notification cards, toasts, the IME and overlays
        // share this map and can be alive for long stretches — waiting for the
        // map to be empty would mean a single visible notification stopped focus
        // ever being restored.
        bool modalStillUp = false;
        for (const auto& [id, c] : mStandaloneContainers) {
            (void)id;
            if (c && c->windowCategory() == WaylandWindowCategory::MODAL) {
                modalStillUp = true;
                break;
            }
        }
        if (!modalStillUp) restoreDisplacedTask();
    }
}

// --- Container migration (hotplug disconnect) ---

void WaylandLayerManager::migrateAllContainersTo(WaylandLayerManager* target) {
    // Collect task IDs before moving (iterators invalidated by move)
    std::vector<int32_t> migratedTaskIds;

    // Move task containers
    for (auto& [taskId, container] : mTaskContainers) {
        container->updateOutputInfo(target->mOutputWidth, target->mOutputHeight,
                                    target->mOutputTransform, target->mScaleFactor,
                                    target->mWorkArea);
        container->setOutput(target->mOutput);
        target->mTaskContainers[taskId] = std::move(container);
        migratedTaskIds.push_back(taskId);
        ALOGI("migrated task container taskId=%d to primary display", taskId);
    }
    mTaskContainers.clear();

    // Move standalone containers
    for (auto& [layerId, container] : mStandaloneContainers) {
        container->updateOutputInfo(target->mOutputWidth, target->mOutputHeight,
                                    target->mOutputTransform, target->mScaleFactor,
                                    target->mWorkArea);
        container->setOutput(target->mOutput);
        target->mStandaloneContainers[layerId] = std::move(container);
        ALOGI("migrated standalone container layerId=%" PRIu64 " to primary display", layerId);
    }
    mStandaloneContainers.clear();

    // Tell Android WM to move each task to the primary display (displayId=0).
    // This triggers SF to re-create virtual displays for the tasks on the
    // primary display so they can continue presenting through their containers.
    for (int32_t taskId : migratedTaskIds) {
        if (taskId > 0) {
            mTaskResizer.moveStackToDisplay(taskId, 0 /*primary display*/);
        }
    }
}

// --- Common operations ---

void WaylandLayerManager::updateOutputInfo(int32_t width, int32_t height,
                                           int32_t transform, float scaleFactor,
                                           const OutputRect& workArea) {
    mOutputWidth = width;
    mOutputHeight = height;
    mOutputTransform = transform;
    mScaleFactor = scaleFactor;
    mWorkArea = workArea;

    for (auto& [taskId, container] : mTaskContainers) {
        container->updateOutputInfo(width, height, transform, scaleFactor, workArea);
    }
    for (auto& [layerId, container] : mStandaloneContainers) {
        container->updateOutputInfo(width, height, transform, scaleFactor, workArea);
    }

    // Propagate transform to input manager for overlay drag coordinate rotation
    if (auto* mgr = WaylandInputManager::instance()) {
        mgr->updateOutputInfo(transform);
    }
}

void WaylandLayerManager::applyLayoutMode(ChromeLayoutMode mode) {
    ALOGI("WaylandLayerManager: applyLayoutMode=%s (output %dx%d, %zu task containers)",
          toString(mode), mOutputWidth, mOutputHeight, mTaskContainers.size());
    for (auto& [taskId, container] : mTaskContainers) {
        (void)taskId;
        container->applyLayoutMode(mode, mOutputWidth, mOutputHeight);
    }
}

void WaylandLayerManager::applyFrameColors(uint32_t active, uint32_t inactive) {
    ALOGI("WaylandLayerManager: applyFrameColors active=#%08X inactive=#%08X "
          "(%zu task containers)", active, inactive, mTaskContainers.size());
    for (auto& [taskId, container] : mTaskContainers) {
        (void)taskId;
        container->applyFrameColors(active, inactive);
    }
}

void WaylandLayerManager::clearActiveTask() {
    if (mActiveTaskId <= 0) return;
    ALOGI("active task %d displaced by a task-less surface", mActiveTaskId);
    auto prev = mTaskContainers.find(mActiveTaskId);
    if (prev != mTaskContainers.end()) prev->second->onFocusChanged(false);
    // Remember who was displaced so focus can go back when the dialog closes.
    // Deliberately does not touch Android focus: the dialog is the thing the
    // user is typing into while it is up.
    mDisplacedTaskId = mActiveTaskId;
    mActiveTaskId = -1;
}

void WaylandLayerManager::restoreDisplacedTask() {
    if (mDisplacedTaskId <= 0) return;
    const int32_t taskId = mDisplacedTaskId;
    mDisplacedTaskId = -1;
    // The task may have gone away while the dialog was up.
    if (mTaskContainers.find(taskId) == mTaskContainers.end()) return;
    ALOGI("restoring focus to displaced task %d", taskId);
    // Ask Chrome to activate the window first. Everything else is bookkeeping
    // on our side; until Chrome makes this the active window again, ash has no
    // active fullscreen window to read the shelf auto-hide flag off, so the
    // shelf stays up however many times we re-assert the flag.
    auto it = mTaskContainers.find(taskId);
    if (it != mTaskContainers.end()) it->second->requestActivation();
    // setActiveTask re-runs the normal activation path: the container gets
    // onFocusChanged(true) — which re-asserts the game shelf auto-hide — and
    // Android focus is handed back to the task via setFocusedTask.
    setActiveTask(taskId);
}

void WaylandLayerManager::setActiveTask(int32_t taskId) {
    if (taskId <= 0 || taskId == mActiveTaskId) return;
    ALOGI("active task %d -> %d", mActiveTaskId, taskId);
    // Some task is being activated for real. If it is not the one a modal
    // displaced, the user has moved on — switched to another window while the
    // dialog was up — so drop the pending restore rather than yanking them back
    // to the old task when that dialog finally closes.
    if (mDisplacedTaskId > 0 && mDisplacedTaskId != taskId) {
        ALOGI("dropping pending restore of task %d — task %d activated instead",
              mDisplacedTaskId, taskId);
        mDisplacedTaskId = -1;
    }

    auto prev = mTaskContainers.find(mActiveTaskId);
    if (prev != mTaskContainers.end()) prev->second->onFocusChanged(false);

    mActiveTaskId = taskId;

    auto next = mTaskContainers.find(taskId);
    if (next != mTaskContainers.end()) next->second->onFocusChanged(true);

    mTaskResizer.setFocusedTask(taskId);
}

bool WaylandLayerManager::getActiveTaskWindowOffset(int32_t* outX, int32_t* outY) const {
    if (mActiveTaskId <= 0) return false;
    auto it = mTaskContainers.find(mActiveTaskId);
    if (it == mTaskContainers.end()) return false;
    *outX = it->second->windowOffsetX();
    *outY = it->second->windowOffsetY();
    return true;
}

int32_t WaylandLayerManager::activeDialogTaskId() const {
    for (auto& [taskId, container] : mTaskContainers) {
        if (container->isDialog() && !container->isClosed()) return taskId;
    }
    return -1;
}

WaylandLayerManager::DrainResult WaylandLayerManager::drainClosedContainers() {
    DrainResult result;

    // Drain closed task containers
    for (auto it = mTaskContainers.begin(); it != mTaskContainers.end(); ) {
        if (it->second->isClosed()) {
            ALOGI("draining closed container taskId=%d", it->first);
            result.closedTaskIds.push_back(it->first);
            it = mTaskContainers.erase(it);
        } else {
            ++it;
        }
    }

    // Drain closed standalone containers
    for (auto it = mStandaloneContainers.begin(); it != mStandaloneContainers.end(); ) {
        if (it->second->isClosed()) {
            ALOGI("draining closed standalone container layerId=%" PRIu64, it->first);
            result.closedStandaloneLayerIds.push_back(it->first);
            it = mStandaloneContainers.erase(it);
        } else {
            ++it;
        }
    }

    return result;
}

} // namespace android

#pragma clang diagnostic pop

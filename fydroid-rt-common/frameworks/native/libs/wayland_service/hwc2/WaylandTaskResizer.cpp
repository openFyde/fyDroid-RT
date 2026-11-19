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

// WaylandTaskResizer: synchronizes Android task state with Chrome's window
// geometry. Task operations go through arc-services' "arc_task_controller"
// (IArcTaskController) — a FydeOS-owned AIDL with a generated C++ stub, so the
// transaction codes are always correct (no hardcoded IActivityTaskManager codes,
// which shifted between AOSP versions and called the wrong methods).

#define LOG_TAG "WaylandTaskResizer"

#include "WaylandTaskResizer.h"

#include <binder/IBinder.h>
#include <vector>

#include <binder/IServiceManager.h>
#include <io/fydeos/arc/wm/IArcTaskController.h>
#include <log/log.h>
#include "../WaylandLog.h"
#include "WaylandArcTaskWorker.h"

using ::io::fydeos::arc::wm::IArcTaskController;

namespace android {

sp<IArcTaskController> WaylandTaskResizer::getController() {
    // Look up fresh each call: checkService() is non-blocking, and a cached handle
    // goes stale when system_server restarts (stop/start ui).
    sp<IBinder> service = defaultServiceManager()->checkService(String16("arc_task_controller"));
    if (service == nullptr) {
        ALOGE("arc_task_controller service not yet available");
        return nullptr;
    }
    return interface_cast<IArcTaskController>(service);
}

bool WaylandTaskResizer::resizeTask(int32_t taskId, int32_t left, int32_t top,
                                     int32_t right, int32_t bottom) {
    sp<IArcTaskController> controller = getController();
    if (controller == nullptr) return false;

    bool result = false;
    // RESIZE_MODE_FORCED | RESIZE_MODE_PRESERVE_WINDOW = 3
    binder::Status st = controller->resizeTask(taskId, left, top, right, bottom, 3, &result);
    if (!st.isOk()) {
        ALOGE("resizeTask failed: %s", st.toString8().c_str());
        return false;
    }
    ALOGI("resizeTask taskId=%d bounds=(%d,%d,%d,%d) result=%d",
          taskId, left, top, right, bottom, result);
    return result;
}

bool WaylandTaskResizer::setTaskBoundsUnchecked(int32_t taskId, int32_t left, int32_t top,
                                                int32_t right, int32_t bottom) {
    sp<IArcTaskController> controller = getController();
    if (controller == nullptr) return false;

    bool result = false;
    binder::Status st = controller->setTaskBoundsUnchecked(taskId, left, top, right,
                                                           bottom, &result);
    if (!st.isOk()) {
        ALOGE("setTaskBoundsUnchecked failed: %s", st.toString8().c_str());
        return false;
    }
    ALOGI("setTaskBoundsUnchecked taskId=%d bounds=(%d,%d,%d,%d) result=%d",
          taskId, left, top, right, bottom, result);
    return result;
}

bool WaylandTaskResizer::removeTask(int32_t taskId) {
    sp<IArcTaskController> controller = getController();
    if (controller == nullptr) return false;

    bool result = false;
    binder::Status st = controller->removeTask(taskId, &result);
    if (!st.isOk()) {
        ALOGE("removeTask failed: %s", st.toString8().c_str());
        return false;
    }
    ALOGI("removeTask taskId=%d result=%d", taskId, result);
    return result;
}

bool WaylandTaskResizer::moveStackToDisplay(int32_t taskId, int32_t displayId) {
    // Same deadlock shape as setFocusedTask (void call, service side takes the
    // WM global lock), and nothing consumes the result — run it on the worker.
    WaylandArcTaskWorker::instance().post([taskId, displayId]() {
        sp<IArcTaskController> controller = getController();
        if (controller == nullptr) return;

        binder::Status st = controller->moveTaskToDisplay(taskId, displayId);
        if (!st.isOk()) {
            ALOGE("moveTaskToDisplay failed: %s", st.toString8().c_str());
            return;
        }
        ALOGI("moveStackToDisplay taskId=%d displayId=%d success", taskId, displayId);
    });
    return true;
}

void WaylandTaskResizer::setArcActivated(bool activated) {
    // Same deadlock shape as setFocusedTask (void call, service side takes the
    // WM global lock), and nothing consumes the result — run it on the worker.
    WaylandArcTaskWorker::instance().post([activated]() {
        sp<IArcTaskController> controller = getController();
        if (controller == nullptr) return;

        binder::Status st = controller->setArcActivated(activated);
        if (!st.isOk()) {
            ALOGE("setArcActivated failed: %s", st.toString8().c_str());
            return;
        }
        ALOGI("setArcActivated %d", activated ? 1 : 0);
    });
}

bool WaylandTaskResizer::setFocusedTaskBlocking(int32_t taskId) {
    sp<IArcTaskController> controller = getController();
    if (controller == nullptr) return false;

    binder::Status st = controller->setFocusedTask(taskId);
    if (!st.isOk()) {
        ALOGE("setFocusedTask failed: %s", st.toString8().c_str());
        return false;
    }
    ALOGVT("setFocusedTask taskId=%d", taskId);
    return true;
}

bool WaylandTaskResizer::setFocusedTask(int32_t taskId) {
    WaylandArcTaskWorker::instance().postFocus(taskId);
    return true;
}

// Set on the Wayland event threads only — see allowBlockingAwaitOnThisThread().
static thread_local bool tBlockingAwaitAllowed = false;

void WaylandTaskResizer::allowBlockingAwaitOnThisThread() {
    tBlockingAwaitAllowed = true;
}

bool WaylandTaskResizer::setFocusedTaskAwait(int32_t taskId) {
    WaylandArcTaskWorker& worker = WaylandArcTaskWorker::instance();
    uint64_t seq = worker.postFocus(taskId);
    if (!tBlockingAwaitAllowed) {
        // SurfaceFlinger's main thread (or any other) — post and move on.
        return false;
    }
    return worker.await(seq, kFocusAwaitTimeoutMs);
}

bool WaylandTaskResizer::isTaskResizable(int32_t taskId) {
    sp<IArcTaskController> controller = getController();
    // Assume resizable when the controller is not up yet — that is the
    // behaviour every window had before this was plumbed, and it is the safe
    // direction: at worst Chrome offers a snap it used to offer anyway.
    if (controller == nullptr) return true;

    bool result = true;
    binder::Status st = controller->isTaskResizable(taskId, &result);
    if (!st.isOk()) {
        ALOGE("isTaskResizable failed: %s", st.toString8().c_str());
        return true;
    }
    return result;
}

bool WaylandTaskResizer::getTaskMinSize(int32_t taskId, int32_t* outW, int32_t* outH) {
    if (outW) *outW = -1;
    if (outH) *outH = -1;
    sp<IArcTaskController> controller = getController();
    if (controller == nullptr) return false;

    std::vector<int32_t> size;
    binder::Status st = controller->getTaskMinSize(taskId, &size);
    if (!st.isOk()) {
        ALOGE("getTaskMinSize failed: %s", st.toString8().c_str());
        return false;
    }
    if (size.size() < 2) return false;
    if (outW) *outW = size[0];
    if (outH) *outH = size[1];
    return size[0] > 0 || size[1] > 0;
}

bool WaylandTaskResizer::setTaskWindowingMode(int32_t taskId, int32_t windowingMode,
                                               bool toTop) {
    sp<IArcTaskController> controller = getController();
    if (controller == nullptr) return false;

    bool result = false;
    binder::Status st = controller->setTaskWindowingMode(taskId, windowingMode, toTop, &result);
    if (!st.isOk()) {
        ALOGE("setTaskWindowingMode failed: %s", st.toString8().c_str());
        return false;
    }
    ALOGI("setTaskWindowingMode taskId=%d mode=%d toTop=%d result=%d",
          taskId, windowingMode, toTop, result);
    return result;
}

} // namespace android

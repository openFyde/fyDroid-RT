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

// WaylandHwc2Composer: Wayland-based Hwc2::Composer implementation.
//
// Physical displays: just vsync + flush. No layer presentation.
// Virtual displays: Per-task and per-standalone GPU composition.
// WaylandTaskOutput GPU-composites layers → setClientTarget(buffer) →
// presentDisplay() → convert to wl_buffer → container->presentComposedBuffer().

// TODO(b/129481165): remove the #pragma below and fix conversion issues
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandHwc2Composer"
#define ATRACE_TAG ATRACE_TAG_GRAPHICS

#include "WaylandHwc2Composer.h"
#include "../WaylandClient.h"
#include "../WaylandGlobals.h"
#include "../display/WaylandDisplay.h"
#include "../display/WaylandOutput.h"
#include "../layer/WaylandLayerContainer.h"
#include "../layer/WaylandLayerDrmBufferManager.h"
#include "../layer/WaylandLayerManager.h"
#include "../window/WaylandNotificationWindow.h"
#include "WaylandTaskResizer.h"

#include <log/log.h>
#include "../WaylandLog.h"
#include <sync/sync.h>
#include <wayland-client.h>
#include <cstring>
#include <unistd.h>

namespace android::Hwc2 {

// Parse notification key from generic metadata bytes.
// Metadata value = raw Parcel bytes containing writeString(notifKey).
// Parcel string format: int32_t charLen, char16_t[charLen+1], padding.
static std::string parseNotifKeyFromMetadata(const std::vector<uint8_t>& value) {
    if (value.size() < 4) return "";
    int32_t charLen;
    memcpy(&charLen, value.data(), sizeof(int32_t));
    if (charLen <= 0 || charLen > 512) return "";
    if (value.size() < 4 + static_cast<size_t>(charLen) * 2) return "";
    const char16_t* chars = reinterpret_cast<const char16_t*>(value.data() + 4);
    std::string result;
    result.reserve(charLen);
    for (int32_t i = 0; i < charLen; i++) {
        result += static_cast<char>(chars[i] & 0xFF);  // notif keys are ASCII
    }
    return result;
}

// Process-wide singleton — see header comment on getInstance().
static std::atomic<WaylandHwc2Composer*> sInstance{nullptr};

WaylandHwc2Composer::WaylandHwc2Composer() {
    ALOGI("WaylandHwc2Composer created");
    sInstance.store(this, std::memory_order_release);

    mClient = std::make_unique<WaylandClient>();
    if (!mClient->init()) {
        ALOGW("WaylandClient init failed — falling back to no-op presentation");
        mClient.reset();
    }
}

WaylandHwc2Composer::~WaylandHwc2Composer() {
    WaylandHwc2Composer* expected = this;
    sInstance.compare_exchange_strong(expected, nullptr,
                                       std::memory_order_release);
    for (auto& [id, vd] : mVirtualDisplays) {
        if (vd.clientTargetFence >= 0) close(vd.clientTargetFence);
        for (auto& [layerId, nld] : vd.notifLayers) {
            if (nld.acquireFence >= 0) close(nld.acquireFence);
        }
    }
    mClient.reset();
    ALOGI("WaylandHwc2Composer destroyed");
}

WaylandHwc2Composer* WaylandHwc2Composer::getInstance() {
    return sInstance.load(std::memory_order_acquire);
}

void WaylandHwc2Composer::setTaskRenderTargetSizeCallback(
        TaskRenderTargetSizeCallback cb) {
    std::lock_guard<std::mutex> lock(mTaskRenderMutex);
    mTaskRenderTargetSizeCb = std::move(cb);
}

void WaylandHwc2Composer::scheduleRefresh() {
    if (mCallback && !mDisplays.empty()) {
        mCallback->onComposerHalRefresh(mDisplays[0].hwcDisplayId);
    }
}

void WaylandHwc2Composer::setTaskVirtualDisplaySize(int32_t taskId,
                                                    int32_t width,
                                                    int32_t height) {
    if (taskId <= 0 || width <= 0 || height <= 0) return;
    for (auto& [id, vd] : mVirtualDisplays) {
        if (vd.taskId != taskId) continue;
        if (vd.width == width && vd.height == height) return;
        ALOGI("setTaskVirtualDisplaySize: display=%" PRIu64 " taskId=%d "
              "%dx%d -> %dx%d", id, taskId, vd.width, vd.height, width, height);
        vd.width = width;
        vd.height = height;
        return;
    }
}

void WaylandHwc2Composer::setTaskRenderTargetSize(
        int32_t taskId, int32_t targetW, int32_t targetH) {
    TaskRenderTargetSizeCallback cb;
    {
        std::lock_guard<std::mutex> lock(mTaskRenderMutex);
        cb = mTaskRenderTargetSizeCb;
    }
    if (!cb) {
        ALOGW("setTaskRenderTargetSize: no SF callback registered "
              "(taskId=%d, %dx%d)", taskId, targetW, targetH);
        return;
    }
    cb(taskId, targetW, targetH);

    // The render-target change just updated WaylandTaskOutput state from
    // the wayland event thread, but SF's main thread doesn't know to
    // recomposite — Settings (and any static window) won't redraw on its
    // own, so the buffer keeps the stale size until something else (a
    // pointer event over a hit-test region, an animation tick) wakes
    // composition. Fire IComposerCallback::onRefresh on the primary HWC
    // display to drive SF::onRefreshReceived → repaintEverythingForHWC,
    // which recomposites every output and picks up the new target.
    if (mCallback && !mDisplays.empty()) {
        mCallback->onComposerHalRefresh(mDisplays[0].hwcDisplayId);
    }
}

void WaylandHwc2Composer::setTaskResizeMaskHooks(TaskResizeMaskHooks hooks) {
    std::lock_guard<std::mutex> lock(mTaskRenderMutex);
    mTaskResizeMaskHooks = std::move(hooks);
}

void WaylandHwc2Composer::requestTaskResizeMask(int32_t taskId) {
    std::function<void(int32_t)> cb;
    {
        std::lock_guard<std::mutex> lock(mTaskRenderMutex);
        cb = mTaskResizeMaskHooks.requestMask;
    }
    if (!cb || taskId <= 0) return;
    cb(taskId);
    // The mask is built on the SF thread from the frame on screen; an idle
    // app would otherwise not compose again until the drag is over.
    scheduleRefresh();
}

void WaylandHwc2Composer::awaitTaskResizeContent(int32_t taskId, int32_t w,
                                                 int32_t h) {
    std::function<void(int32_t, int32_t, int32_t)> cb;
    {
        std::lock_guard<std::mutex> lock(mTaskRenderMutex);
        cb = mTaskResizeMaskHooks.awaitContent;
    }
    if (!cb || taskId <= 0) return;
    cb(taskId, w, h);
    // The resized app's first frame may already be latched; look now rather
    // than on its next frame. A cancel (0x0) has nothing to look for.
    if (w > 0 && h > 0) scheduleRefresh();
}

bool WaylandHwc2Composer::presentTaskResizeMask(int32_t taskId,
                                                const sp<GraphicBuffer>& mask,
                                                int32_t maskW, int32_t maskH,
                                                int acquireFence) {
    // A null mask is SF reporting that it could not build one; the container
    // still needs to hear it, or it holds live frames back until the timeout.
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (globals && taskId > 0) {
        for (size_t i = 0; i < globals->displayCount(); i++) {
            auto* wd = globals->display(i);
            if (!wd || !wd->layerManager()) continue;
            auto* container = wd->layerManager()->findContainerByTaskId(taskId);
            if (!container) continue;
            return container->presentResizeMask(
                    wd->layerManager()->bufferManager(), mask, maskW, maskH,
                    acquireFence);
        }
    }
    if (acquireFence >= 0) close(acquireFence);
    return false;
}

void WaylandHwc2Composer::notifyTaskResizeContentReady(int32_t taskId) {
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (!globals || taskId <= 0) return;
    for (size_t i = 0; i < globals->displayCount(); i++) {
        auto* wd = globals->display(i);
        if (!wd || !wd->layerManager()) continue;
        if (auto* container = wd->layerManager()->findContainerByTaskId(taskId)) {
            container->onResizeContentReady();
            return;
        }
    }
}

bool WaylandHwc2Composer::ensureTaskWindowFitsContent(
        int32_t taskId, int32_t contentW, int32_t contentH) {
    if (taskId <= 0 || contentW <= 0 || contentH <= 0) return false;

    // Find the container on whichever display currently owns it (same search
    // presentDisplay uses). Called from the SF composition thread.
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (!globals) return false;
    for (size_t i = 0; i < globals->displayCount(); i++) {
        auto* wd = globals->display(i);
        if (wd && wd->layerManager()) {
            auto* container = wd->layerManager()->findContainerByTaskId(taskId);
            if (container) {
                return container->ensureWindowFitsContent(contentW, contentH);
            }
        }
    }
    return false;
}

bool WaylandHwc2Composer::resizeTaskModalWindow(int32_t taskId, int32_t cardW,
                                                int32_t cardH) {
    if (taskId <= 0 || cardW <= 0 || cardH <= 0) return false;

    auto* globals = mClient ? mClient->globals() : nullptr;
    if (!globals) return false;
    for (size_t i = 0; i < globals->displayCount(); i++) {
        auto* wd = globals->display(i);
        if (wd && wd->layerManager()) {
            auto* container = wd->layerManager()->findContainerByTaskId(taskId);
            if (container) {
                return container->updateModalCardSize(cardW, cardH);
            }
        }
    }
    return false;
}

void WaylandHwc2Composer::setModalScrimAlpha(float alpha, int32_t dimTaskId) {
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (!globals) return;

    // Where the dim takes clicks: the host task's window. A whole-task dialog
    // hosts its own dim, and its "window" is the modal itself, so it gets an
    // empty rect — only the card takes clicks and the desktop around it is
    // merely dimmed.
    int32_t cx = 0, cy = 0, cw = 0, ch = 0;
    if (alpha > 0.0f && dimTaskId > 0) {
        for (size_t i = 0; i < globals->displayCount(); i++) {
            auto* wd = globals->display(i);
            if (!wd || !wd->layerManager()) continue;
            auto* host = wd->layerManager()->findContainerByTaskId(dimTaskId);
            if (!host || host->windowCategory() == WaylandWindowCategory::MODAL) continue;
            if (auto* win = host->window()) {
                cx = win->boundsX();
                cy = win->boundsY();
                cw = win->boundsWidth();
                ch = win->boundsHeight();
            }
            break;
        }
    }
    if (!globals->setModalScrim(alpha, cx, cy, cw, ch)) return;
    for (size_t i = 0; i < globals->displayCount(); i++) {
        auto* wd = globals->display(i);
        if (wd && wd->layerManager()) wd->layerManager()->setModalScrim(alpha, cx, cy, cw, ch);
    }
}

bool WaylandHwc2Composer::isBufferHeldByCompositor(uint64_t bufferId) const {
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (!globals) return false;
    // A buffer is imported into exactly one display's manager, but a window can
    // migrate between displays, so ask every one of them: any manager reporting
    // the buffer as outstanding means Chrome has it.
    for (size_t i = 0; i < globals->displayCount(); i++) {
        auto* wd = globals->display(i);
        if (!wd || !wd->layerManager()) continue;
        auto* bufMgr = wd->layerManager()->bufferManager();
        if (bufMgr && bufMgr->isHeldByCompositor(bufferId)) return true;
    }
    return false;
}

void WaylandHwc2Composer::markTaskDialogThisFrame(int32_t taskId) {
    if (taskId <= 0) return;
    // vd.taskId is assigned in setLayerGenericMetadata during the first frame's
    // composition, so this matches from the second frame onward; on the very
    // first frame the window is created as an app window and recreated as modal
    // once setIsDialog() flips (handled in WaylandLayerContainer).
    for (auto& [id, vd] : mVirtualDisplays) {
        if (vd.taskId == taskId) {
            vd.dialogDetectedThisFrame = true;
            return;
        }
    }
}

void WaylandHwc2Composer::setPendingStandaloneTargetOutput(int32_t outputIndex) {
    mPendingStandaloneTargetOutput = outputIndex;
}

void WaylandHwc2Composer::setPendingStandaloneGeometry(int32_t x, int32_t y,
                                                       int32_t w, int32_t h) {
    mPendingStandaloneX = x;
    mPendingStandaloneY = y;
    mPendingStandaloneW = w;
    mPendingStandaloneH = h;
}

void WaylandHwc2Composer::forgetContainer(const WaylandLayerContainer* container) {
    if (!container) return;
    // Read-only walk: safe to run from inside destroyVirtualDisplay (which is
    // holding an iterator into mVirtualDisplays while it tears its container
    // down) and from inside presentNotificationVirtualDisplay (which holds a
    // reference to the notifLayers entry it is about to reassign).
    for (auto& [display, vd] : mVirtualDisplays) {
        if (vd.container == container) {
            vd.container = nullptr;
            vd.containerDisplayIndex = -1;
            ALOGI("forgetContainer: display=%" PRIu64 " dropped container %p"
                  " (taskId=%d standalone=%" PRIu64 ")",
                  display, container, vd.taskId, vd.standaloneLayerId);
        }
        for (auto& [layerId, nld] : vd.notifLayers) {
            if (nld.container == container) {
                nld.container = nullptr;
                ALOGI("forgetContainer: display=%" PRIu64 " notif layer=%" PRIu64
                      " dropped container %p", display, layerId, container);
            }
        }
    }
}

void WaylandHwc2Composer::setStandaloneScreenGeometry(uint64_t hwcLayerId,
                                                      int32_t x, int32_t y,
                                                      int32_t w, int32_t h) {
    if (hwcLayerId == 0 || w <= 0 || h <= 0) return;
    for (auto& [id, vd] : mVirtualDisplays) {
        if (vd.standaloneLayerId == hwcLayerId) {
            vd.screenX = x;
            vd.screenY = y;
            vd.screenW = w;
            vd.screenH = h;
            return;
        }
    }
}

WaylandDisplay* WaylandHwc2Composer::findDisplay(Display id) const {
    for (const auto& entry : mDisplays) {
        if (entry.hwcDisplayId == id) {
            return entry.waylandDisplay;
        }
    }
    return nullptr;
}

bool WaylandHwc2Composer::isSupported(OptionalFeature) const {
    // No optional composer3 features (refresh-rate switching, expected present
    // time, brightness command, idle timer, physical orientation).
    return false;
}

std::vector<V3_0::Capability> WaylandHwc2Composer::getCapabilities() {
    return {};
}

std::string WaylandHwc2Composer::dumpDebugInfo() {
    std::string result = "WaylandHwc2Composer (all-virtual-display architecture)\n";
    result += "  Virtual displays: " + std::to_string(mVirtualDisplays.size()) + "\n";
    for (const auto& [id, vd] : mVirtualDisplays) {
        result += "    display=" + std::to_string(id);
        result += " taskId=" + std::to_string(vd.taskId);
        result += " " + std::to_string(vd.width) + "x" + std::to_string(vd.height);
        result += " layers=" + std::to_string(vd.layers.size());
        result += " cat=" + std::to_string(static_cast<int>(vd.windowCategory));
        result += "\n";
    }
    return result;
}

void WaylandHwc2Composer::registerCallback(HWC2::ComposerCallback& callback) {
    ALOGI("registerCallback: registering composer callback");
    mCallback = &callback;

    // Build display entries from WaylandGlobals
    mDisplays.clear();
    auto* globals = mClient ? mClient->globals() : nullptr;
    size_t count = globals ? globals->displayCount() : 0;

    if (count == 0) {
        Display id = 1;
        ALOGI("registerCallback: no displays from globals, using fallback id=%" PRIu64, id);
        mDisplays.push_back({id, nullptr});
        callback.onComposerHalHotplug(id, IComposerCallback::Connection::CONNECTED);
        return;
    }

    for (size_t i = 0; i < count; i++) {
        Display id = static_cast<Display>(i + 1);
        auto* waylandDisplay = globals->display(i);
        mDisplays.push_back({id, waylandDisplay});

        ALOGI("registerCallback: firing hotplug CONNECTED for display %" PRIu64
              " (index %zu, %dx%d)", id, i,
              waylandDisplay ? waylandDisplay->width() : 0,
              waylandDisplay ? waylandDisplay->height() : 0);
        announceDisplay(mDisplays.back());

        if (waylandDisplay) {
            waylandDisplay->startVSync(mCallback, id);
        }
    }

    // Register hotplug callback for runtime display connect/disconnect
    globals->setHotplugCallback([this](size_t index, bool connected) {
        onWaylandHotplug(index, connected);
    });
    globals->setDisplayChangedCallback([this](size_t index) {
        onWaylandDisplayChanged(index);
    });
}

void WaylandHwc2Composer::announceDisplay(DisplayEntry& entry) {
    if (entry.waylandDisplay) {
        entry.announcedWidth = entry.waylandDisplay->width();
        entry.announcedHeight = entry.waylandDisplay->height();
    }
    mCallback->onComposerHalHotplug(entry.hwcDisplayId,
                                    IComposerCallback::Connection::CONNECTED);
}

void WaylandHwc2Composer::onWaylandDisplayChanged(size_t displayIndex) {
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (!globals || !mCallback) return;

    const Display id = static_cast<Display>(displayIndex + 1);
    auto* waylandDisplay = globals->display(displayIndex);
    if (!waylandDisplay) return;

    for (auto& entry : mDisplays) {
        if (entry.hwcDisplayId != id) continue;
        // A different WaylandDisplay behind the id is a panel swap, which
        // onWaylandHotplug() rebinds and re-announces itself.
        if (entry.waylandDisplay != waylandDisplay) return;

        const int32_t w = waylandDisplay->width();
        const int32_t h = waylandDisplay->height();
        if (w == entry.announcedWidth && h == entry.announcedHeight) return;

        // width()/height() are the native (pre-rotation) panel size, so
        // rotation and display zoom never get here — only a real mode change.
        ALOGI("onWaylandDisplayChanged: display %" PRIu64 " resized %dx%d -> %dx%d,"
              " re-announcing", id, entry.announcedWidth, entry.announcedHeight, w, h);
        // Same WaylandDisplay, so its vsync is already running: do NOT call
        // startVSync() again. WaylandTiming::start() would assign over a
        // joinable std::thread and abort SurfaceFlinger.
        announceDisplay(entry);
        return;
    }
}

void WaylandHwc2Composer::onWaylandHotplug(size_t displayIndex, bool connected) {
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (!globals || !mCallback) return;

    if (connected) {
        // New display connected
        Display id = static_cast<Display>(displayIndex + 1);
        auto* waylandDisplay = globals->display(displayIndex);

        // An id we already know means the panel behind it was replaced in
        // place (primary hot-swap — WaylandGlobals::replacePrimaryDisplay).
        // Rebind the entry and re-fire CONNECTED so SF rebuilds the display
        // against the new resolution/dpi, instead of dropping it as a dup.
        for (auto& entry : mDisplays) {
            if (entry.hwcDisplayId == id) {
                ALOGI("onWaylandHotplug: display %" PRIu64 " re-announced (%dx%d)",
                      id,
                      waylandDisplay ? waylandDisplay->width() : 0,
                      waylandDisplay ? waylandDisplay->height() : 0);
                entry.waylandDisplay = waylandDisplay;
                announceDisplay(entry);
                if (waylandDisplay) {
                    waylandDisplay->startVSync(mCallback, id);
                }
                return;
            }
        }

        mDisplays.push_back({id, waylandDisplay});
        ALOGI("onWaylandHotplug: CONNECTED display %" PRIu64 " (index %zu, %dx%d)",
              id, displayIndex,
              waylandDisplay ? waylandDisplay->width() : 0,
              waylandDisplay ? waylandDisplay->height() : 0);

        announceDisplay(mDisplays.back());

        if (waylandDisplay) {
            waylandDisplay->startVSync(mCallback, id);
        }
    } else {
        // Display disconnected
        Display id = static_cast<Display>(displayIndex + 1);
        ALOGI("onWaylandHotplug: DISCONNECTED display %" PRIu64 " (index %zu)",
              id, displayIndex);

        // Invalidate all virtual display container caches that referenced
        // the removed display. Containers were migrated to primary by
        // WaylandGlobals, so the raw pointers are still valid but the
        // display index is stale. Force re-lookup on next presentDisplay.
        for (auto& [vdId, vd] : mVirtualDisplays) {
            if (vd.containerDisplayIndex >= 0 &&
                static_cast<size_t>(vd.containerDisplayIndex) >= displayIndex) {
                vd.container = nullptr;
                vd.containerDisplayIndex = -1;
            }
        }

        // Fire disconnect before removing
        mCallback->onComposerHalHotplug(id, IComposerCallback::Connection::DISCONNECTED);

        // Remove from our display list
        for (auto it = mDisplays.begin(); it != mDisplays.end(); ++it) {
            if (it->hwcDisplayId == id) {
                mDisplays.erase(it);
                break;
            }
        }
    }
}

void WaylandHwc2Composer::resetCommands() {
}

Error WaylandHwc2Composer::executeCommands() {
    return Error::NONE;
}

uint32_t WaylandHwc2Composer::getMaxVirtualDisplayCount() {
    return 32;
}

Error WaylandHwc2Composer::createVirtualDisplay(uint32_t width, uint32_t height,
                                                 PixelFormat* format, Display* outDisplay) {
    Display id = mNextVirtualDisplayId++;
    VirtualDisplayData vd;
    vd.hwcDisplayId = id;
    vd.width = static_cast<int32_t>(width);
    vd.height = static_cast<int32_t>(height);
    // Consume the target-output hint armed by CompositionEngineArc just before
    // this allocation (secondary-display standalone window). Preset the
    // container display index too so the WindowType handler creates the
    // container on the right host output on the very first frame. Reset the
    // one-shot hint so unrelated VDs (tasks, notification, capture) stay primary.
    if (mPendingStandaloneTargetOutput >= 0) {
        vd.targetOutputIndex = mPendingStandaloneTargetOutput;
        vd.containerDisplayIndex = mPendingStandaloneTargetOutput;
    }
    mPendingStandaloneTargetOutput = -1;
    // Same for the on-screen geometry: seed it now so the very first present
    // already carries set_bounds_in_output. Without this a standalone window
    // that composites only once is committed with no bounds and Chrome has
    // nowhere to put it.
    //
    // Only consume it for a VD of the armed size — a standalone output is
    // allocated at exactly the window's size, so this is an identity check that
    // keeps a task or notification VD created in the same window from eating
    // the hint (same guard the notification flag below uses). The arming side
    // also clears it as soon as the allocation returns, so a hint can never go
    // stale either.
    if (mPendingStandaloneW > 0 && mPendingStandaloneH > 0 &&
        mPendingStandaloneW == static_cast<int32_t>(width) &&
        mPendingStandaloneH == static_cast<int32_t>(height)) {
        vd.screenX = mPendingStandaloneX;
        vd.screenY = mPendingStandaloneY;
        vd.screenW = mPendingStandaloneW;
        vd.screenH = mPendingStandaloneH;
        vd.isStandalone = true;
        mPendingStandaloneW = 0;
        mPendingStandaloneH = 0;
    }
    // Check if this VD should be the shared notification display.
    // Java arms the flag with the notif VD's pixel size before creating it; we
    // consume it only for the VD whose size matches, so a task VD created in the
    // same window can't steal it (the old "next VD" flag raced — see the taskId
    // guard in presentDisplay).
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (globals && globals->consumeNotificationFlagIfMatch(
                           static_cast<int32_t>(width), static_cast<int32_t>(height))) {
        vd.isNotificationDisplay = true;
        ALOGI("createVirtualDisplay: notification display %" PRIu64 " (%ux%u)",
              id, width, height);
    }
    mVirtualDisplays[id] = std::move(vd);
    *outDisplay = id;
    *format = PixelFormat::RGBA_8888;
    ALOGI("createVirtualDisplay: %ux%u → display=%" PRIu64, width, height, id);
    return Error::NONE;
}

Error WaylandHwc2Composer::destroyVirtualDisplay(Display display) {
    auto it = mVirtualDisplays.find(display);
    if (it == mVirtualDisplays.end()) {
        return Error::BAD_DISPLAY;
    }
    if (it->second.clientTargetFence >= 0) {
        close(it->second.clientTargetFence);
    }

    // Destroy the Wayland container so the window disappears.
    // Search all displays since containers may live on any display's LayerManager.
    auto& vd = it->second;
    // External capture VDs never own a task/standalone container (they only
    // mirror display 0), so there is nothing of theirs to tear down — and the
    // container they would find belongs to the mirrored task's live window.
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (globals && !vd.isOutputBacked) {
        for (size_t di = 0; di < globals->displayCount(); di++) {
            auto* wd = globals->display(di);
            if (!wd || !wd->layerManager()) continue;
            auto* lm = wd->layerManager();
            if (vd.taskId > 0 && lm->findContainerByTaskId(vd.taskId)) {
                lm->destroyContainer(vd.taskId);
                break;
            } else if (vd.standaloneLayerId > 0 && lm->findStandaloneContainer(vd.standaloneLayerId)) {
                lm->destroyStandaloneContainer(vd.standaloneLayerId);
                break;
            }
        }
    }

    // Clean up per-layer notification data
    for (auto& [layerId, nld] : vd.notifLayers) {
        if (nld.acquireFence >= 0) close(nld.acquireFence);
        if (nld.container && globals) {
            for (size_t di = 0; di < globals->displayCount(); di++) {
                auto* wd = globals->display(di);
                if (wd && wd->layerManager() && wd->layerManager()->findStandaloneContainer(layerId)) {
                    wd->layerManager()->destroyStandaloneContainer(layerId);
                    break;
                }
            }
        }
    }

    // Push the surface destroy(s) to Chrome immediately. The container's window
    // destructor queued wl_surface_destroy; without an explicit flush it waits
    // for the next physical present() to flush the shared connection. For a
    // window closing on a secondary display (external-HDMI Presentation), that
    // output may not tick again on its own, leaving a ghost surface on the
    // external panel. Flushing here guarantees the teardown reaches the host.
    if (mClient && mClient->getDisplay()) {
        wl_display_flush(mClient->getDisplay());
    }

    ALOGI("destroyVirtualDisplay: display=%" PRIu64 " taskId=%d%s",
          display, vd.taskId,
          vd.isNotificationDisplay ? " (notification)" : "");
    mVirtualDisplays.erase(it);
    return Error::NONE;
}

Error WaylandHwc2Composer::acceptDisplayChanges(Display display) {
    if (isVirtualDisplay(display)) return Error::NONE;
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    return Error::NONE;
}

Error WaylandHwc2Composer::createLayer(Display display, Layer* outLayer) {
    *outLayer = mNextLayerId.fetch_add(1);
    if (isVirtualDisplay(display)) {
        mVirtualDisplays[display].layers.insert(*outLayer);
        ALOGVT("createLayer: virtual display=%" PRIu64 " layer=%" PRIu64, display, *outLayer);
        return Error::NONE;
    }
    if (!findDisplay(display)) {
        return Error::BAD_DISPLAY;
    }
    // Physical display layers — no tracking needed
    ALOGVT("createLayer: display=%" PRIu64 " layer=%" PRIu64, display, *outLayer);
    return Error::NONE;
}

Error WaylandHwc2Composer::destroyLayer(Display display, Layer layer) {
    if (isVirtualDisplay(display)) {
        auto& vd = mVirtualDisplays[display];
        vd.layers.erase(layer);
        // Clean up per-layer notification data
        auto nit = vd.notifLayers.find(layer);
        if (nit != vd.notifLayers.end()) {
            if (nit->second.acquireFence >= 0) close(nit->second.acquireFence);
            if (nit->second.container) {
                auto* globals = mClient ? mClient->globals() : nullptr;
                if (globals) {
                    for (size_t di = 0; di < globals->displayCount(); di++) {
                        auto* wd = globals->display(di);
                        if (wd && wd->layerManager() && wd->layerManager()->findStandaloneContainer(layer)) {
                            wd->layerManager()->destroyStandaloneContainer(layer);
                            break;
                        }
                    }
                }
            }
            ALOGI("destroyLayer: notif layer=%" PRIu64 " key=%s",
                  layer, nit->second.notifKey.c_str());
            vd.notifLayers.erase(nit);
        }
        vd.deviceCompositionLayers.erase(layer);
        vd.requestedCompositionTypes.erase(layer);
        ALOGVT("destroyLayer: virtual display=%" PRIu64 " layer=%" PRIu64, display, layer);
        return Error::NONE;
    }
    if (!findDisplay(display)) {
        return Error::BAD_DISPLAY;
    }
    // Physical display layers — no cleanup needed
    ALOGVT("destroyLayer: display=%" PRIu64 " layer=%" PRIu64, display, layer);
    return Error::NONE;
}

Error WaylandHwc2Composer::getActiveConfig(Display display, Config* outConfig) {
    if (isVirtualDisplay(display)) {
        *outConfig = kConfigId;
        return Error::NONE;
    }
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    *outConfig = kConfigId;
    return Error::NONE;
}

Error WaylandHwc2Composer::getChangedCompositionTypes(
        Display display, std::vector<Layer>* outLayers,
        std::vector<V3_0::Composition>* outTypes) {
    // AOSP-13: composition type is the composer3 AIDL Composition enum (V3_0).
    outLayers->clear();
    outTypes->clear();
    if (auto* vdp = findVd(display)) {
        auto& vd = *vdp;
        if (vd.isNotificationDisplay) {
            // DEVICE composition for notification VD — each layer presented
            // individually through its own Wayland notification surface.
            for (Layer layer : vd.pendingClientLayers) {
                outLayers->push_back(layer);
                outTypes->push_back(V3_0::Composition::DEVICE);
                vd.deviceCompositionLayers.insert(layer);
            }
        } else {
            // CLIENT composition for regular VDs — GPU composite → Wayland.
            for (Layer layer : vd.pendingClientLayers) {
                outLayers->push_back(layer);
                outTypes->push_back(V3_0::Composition::CLIENT);
            }
        }
        vd.pendingClientLayers.clear();
    }
    return Error::NONE;
}

Error WaylandHwc2Composer::getColorModes(Display display, std::vector<ColorMode>* outModes) {
    if (isVirtualDisplay(display)) {
        outModes->push_back(ColorMode::NATIVE);
        return Error::NONE;
    }
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    outModes->push_back(ColorMode::NATIVE);
    return Error::NONE;
}

Error WaylandHwc2Composer::getDisplayAttribute(Display display, Config config,
                                                IComposerClient::Attribute attribute,
                                                int32_t* outValue) {
    if (isVirtualDisplay(display)) {
        if (config != kConfigId) return Error::BAD_CONFIG;
        const auto& vd = mVirtualDisplays[display];
        switch (attribute) {
            case IComposerClient::Attribute::WIDTH:
                *outValue = vd.width;
                return Error::NONE;
            case IComposerClient::Attribute::HEIGHT:
                *outValue = vd.height;
                return Error::NONE;
            case IComposerClient::Attribute::VSYNC_PERIOD:
                *outValue = 0;  // virtual displays don't have vsync
                return Error::NONE;
            case IComposerClient::Attribute::DPI_X:
            case IComposerClient::Attribute::DPI_Y:
                *outValue = 0;
                return Error::NONE;
            case IComposerClient::Attribute::CONFIG_GROUP:
                *outValue = 0;
                return Error::NONE;
            default:
                return Error::BAD_PARAMETER;
        }
    }

    auto* disp = findDisplay(display);
    if (!disp) return Error::BAD_DISPLAY;
    if (config != kConfigId) return Error::BAD_CONFIG;

    switch (attribute) {
        case IComposerClient::Attribute::WIDTH:
            *outValue = disp ? disp->width() : WaylandDisplay::kFallbackWidth;
            return Error::NONE;
        case IComposerClient::Attribute::HEIGHT:
            *outValue = disp ? disp->height() : WaylandDisplay::kFallbackHeight;
            return Error::NONE;
        case IComposerClient::Attribute::VSYNC_PERIOD:
            *outValue = disp ? disp->vsyncPeriodNs() : WaylandDisplay::kFallbackVsyncPeriodNs;
            return Error::NONE;
        case IComposerClient::Attribute::DPI_X:
            *outValue = disp ? disp->dpiX() : WaylandDisplay::kDefaultDpiX;
            return Error::NONE;
        case IComposerClient::Attribute::DPI_Y:
            *outValue = disp ? disp->dpiY() : WaylandDisplay::kDefaultDpiY;
            return Error::NONE;
        case IComposerClient::Attribute::CONFIG_GROUP:
            *outValue = 0;
            return Error::NONE;
        default:
            return Error::BAD_PARAMETER;
    }
}

Error WaylandHwc2Composer::getDisplayConfigs(Display display, std::vector<Config>* outConfigs) {
    if (isVirtualDisplay(display)) {
        outConfigs->clear();
        outConfigs->push_back(kConfigId);
        return Error::NONE;
    }
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    outConfigs->clear();
    outConfigs->push_back(kConfigId);
    return Error::NONE;
}

Error WaylandHwc2Composer::getDisplayName(Display display, std::string* outName) {
    if (isVirtualDisplay(display)) {
        *outName = "Virtual Display " + std::to_string(display);
        return Error::NONE;
    }
    for (size_t i = 0; i < mDisplays.size(); i++) {
        if (mDisplays[i].hwcDisplayId == display) {
            *outName = "Wayland Display " + std::to_string(i);
            return Error::NONE;
        }
    }
    return Error::BAD_DISPLAY;
}

Error WaylandHwc2Composer::getDisplayRequests(Display /*display*/,
                                               uint32_t* outDisplayRequestMask,
                                               std::vector<Layer>* outLayers,
                                               std::vector<uint32_t>* outLayerRequestMasks) {
    *outDisplayRequestMask = 0;
    outLayers->clear();
    outLayerRequestMasks->clear();
    return Error::NONE;
}

Error WaylandHwc2Composer::getDozeSupport(Display /*display*/, bool* outSupport) {
    *outSupport = false;
    return Error::NONE;
}

Error WaylandHwc2Composer::hasDisplayIdleTimerCapability(Display /*display*/, bool* outSupport) {
    // AOSP-13: new query — Wayland composer has no kernel idle-timer capability.
    *outSupport = false;
    return Error::NONE;
}

Error WaylandHwc2Composer::getHdrCapabilities(Display /*display*/, std::vector<Hdr>* outTypes,
                                               float* outMaxLuminance,
                                               float* outMaxAverageLuminance,
                                               float* outMinLuminance) {
    outTypes->clear();
    *outMaxLuminance = 0.0f;
    *outMaxAverageLuminance = 0.0f;
    *outMinLuminance = 0.0f;
    return Error::NONE;
}

Error WaylandHwc2Composer::getReleaseFences(Display display, std::vector<Layer>* outLayers,
                                             std::vector<int>* outReleaseFences) {
    outLayers->clear();
    outReleaseFences->clear();
    // No per-layer release fences — virtual displays handle buffer lifecycle
    // through setClientTarget/presentDisplay, physical display has no layers.
    return Error::NONE;
}

Error WaylandHwc2Composer::presentDisplay(Display display, int* outPresentFence) {
    *outPresentFence = -1;

    // --- Virtual display: present composed buffer via Wayland ---
    if (auto* vdp = findVd(display)) {
        auto& vd = *vdp;

        // --- Notification VD: per-layer DEVICE composition ---
        // Guard: if a task VD stole the notification flag due to the
        // createVirtualDisplay race (flag set by Java, consumed by
        // syncTaskOutputs before the actual notification VD is created),
        // clear it and fall through to the regular task path.
        if (vd.isNotificationDisplay && vd.taskId >= 0) {
            ALOGW("presentDisplay: VD %" PRIu64 " has notif flag but taskId=%d "
                  "— clearing notif flag (race)", display, vd.taskId);
            vd.isNotificationDisplay = false;
        }
        if (vd.isNotificationDisplay) {
            return presentNotificationVirtualDisplay(display, vd);
        }

        // --- External capture VD (MediaProjection): SF owns the output buffer
        // and delivers the mirrored frames itself; routing it through a task
        // container would hijack the mirrored task's real window. ---
        if (vd.isOutputBacked) {
            return Error::NONE;
        }

        // --- Regular VD: CLIENT composition (GPU composited) ---
        return presentTaskVirtualDisplay(display, vd);
    }

    // --- Physical display: just flush ---
    auto* waylandDisp = findDisplay(display);
    if (!waylandDisp) {
        return Error::BAD_DISPLAY;
    }

    // Drain closed containers on ALL displays' LayerManagers.
    // With multiple physical displays, draining only the current display's
    // manager could leave closed containers on other displays' managers.
    auto* globals = mClient ? mClient->globals() : nullptr;
    if (globals) {
        for (size_t i = 0; i < globals->displayCount(); i++) {
            auto* wd = globals->display(i);
            if (wd && wd->layerManager()) {
                wd->layerManager()->drainClosedContainers();
            }
        }
    }

    // Batch flush all queued Wayland requests from all virtual displays.
    // wl_display_flush() sends all buffered data regardless of which surface
    // it belongs to, so flushing on any physical display's present is fine.
    waylandDisp->flush();

    return Error::NONE;
}

// Notification VD: per-layer DEVICE composition. Each notification layer
// gets its own WaylandNotificationWindow container; per-object present
// work is delegated to the container.
Error WaylandHwc2Composer::presentNotificationVirtualDisplay(
        Display display, VirtualDisplayData& vd) {
    auto* globals = mClient ? mClient->globals() : nullptr;
    WaylandLayerDrmBufferManager* bufMgr = nullptr;
    WaylandLayerManager* lm = nullptr;
    if (globals && globals->displayCount() > 0) {
        auto* waylandDisp = globals->display(0);
        if (waylandDisp && waylandDisp->layerManager()) {
            lm = waylandDisp->layerManager();
            bufMgr = lm->bufferManager();
        }
    }
    if (!bufMgr || !lm) {
        return Error::NONE;
    }

    ALOGVT("presentDisplay: notif VD %" PRIu64 " layers=%zu notifLayers=%zu",
           display, vd.layers.size(), vd.notifLayers.size());
    static int sNotifVdLog = 60;
    if (sNotifVdLog > 0) {
        sNotifVdLog--;
        ALOGI("NOTIFVD: present display=%" PRIu64 " layers=%zu notifLayers=%zu",
              display, vd.layers.size(), vd.notifLayers.size());
    }
    static int sNotifPresentLog = 80;
    for (auto& [layerId, nld] : vd.notifLayers) {
        if (nld.notifKey.empty() || !nld.buffer) {
            if (sNotifPresentLog > 0) {
                sNotifPresentLog--;
                ALOGI("NOTIFPRES: SKIP layer=%" PRIu64 " key='%s' buffer=%p",
                      layerId, nld.notifKey.c_str(), nld.buffer.get());
            }
            continue;
        }
        if (sNotifPresentLog > 0) {
            sNotifPresentLog--;
            ALOGI("NOTIFPRES: present layer=%" PRIu64 " key='%s' buffer=%dx%d",
                  layerId, nld.notifKey.c_str(),
                  nld.buffer->getWidth(), nld.buffer->getHeight());
        }
        // Ensure the WaylandNotificationWindow container exists.
        if (!nld.container || nld.container->isClosed()) {
            nld.container = lm->registerStandaloneContainer(
                    layerId, WaylandWindowCategory::NOTIFICATION, nld.notifKey);
            if (nld.container) {
                const int32_t notifDisplayId = globals->notifDisplayId();
                nld.container->configureNotificationWindow(
                        notifDisplayId, nld.frameLeft, nld.frameTop);
                ALOGI("presentDisplay: created notif container for "
                      "layer=%" PRIu64 " key=%s displayId=%d frame=(%d,%d)",
                      layerId, nld.notifKey.c_str(), notifDisplayId,
                      nld.frameLeft, nld.frameTop);
            }
        }
        if (!nld.container) continue;
        // Display frame may change between frames.
        nld.container->updateNotificationFrame(nld.frameLeft, nld.frameTop);
        nld.container->presentGraphicBuffer(bufMgr, nld.buffer,
                                            &nld.acquireFence);
    }
    wl_display_flush(mClient->getDisplay());
    return Error::NONE;
}

// Regular (task / standalone) VD: CLIENT composition — SF GPU-composites
// the task into vd.clientTarget; the container presents it.
Error WaylandHwc2Composer::presentTaskVirtualDisplay(Display display,
                                                     VirtualDisplayData& vd) {
    // Look up container — re-lookup if cached pointer is closed.
    // Search ALL displays since containers may live on any display's
    // LayerManager (e.g. after hotplug migration or output change).
    if (!vd.container || vd.container->isClosed()) {
        vd.container = nullptr;
        vd.containerDisplayIndex = -1;

        auto* globals = mClient ? mClient->globals() : nullptr;
        if (globals) {
            for (size_t i = 0; i < globals->displayCount(); i++) {
                auto* wd = globals->display(i);
                if (wd && wd->layerManager()) {
                    if (vd.taskId > 0) {
                        vd.container = wd->layerManager()->findContainerByTaskId(vd.taskId);
                    } else if (vd.standaloneLayerId > 0) {
                        vd.container = wd->layerManager()->findStandaloneContainer(vd.standaloneLayerId);
                    }
                    if (vd.container) {
                        vd.containerDisplayIndex = static_cast<int32_t>(i);
                        break;
                    }
                }
            }
        }
    }

    if (!vd.container) {
        ALOGVT("presentDisplay: virtual display=%" PRIu64 " no container"
               " (taskId=%d standalone=%" PRIu64 ")",
               display, vd.taskId, vd.standaloneLayerId);
        return Error::NONE;
    }

    // Apply this frame's dialog flag — the container latches the modal
    // window category and claims Android focus on first detection.
    if (vd.taskId > 0) {
        bool isDialog = vd.dialogDetectedThisFrame;
        vd.dialogDetectedThisFrame = false;
        vd.container->applyDialogState(isDialog);
    }

    if (!vd.clientTarget) {
        ALOGV("presentDisplay: virtual display=%" PRIu64 " no client target",
              display);
        return Error::NONE;
    }

    // Get buffer manager from the container's display (or primary as fallback).
    // DMA-BUF import is display-agnostic, but using the correct display
    // keeps the mapping explicit.
    auto* globals = mClient ? mClient->globals() : nullptr;
    WaylandLayerDrmBufferManager* bufMgr = nullptr;
    if (globals) {
        size_t bufIdx = (vd.containerDisplayIndex >= 0)
                ? static_cast<size_t>(vd.containerDisplayIndex) : 0;
        auto* waylandDisp = globals->display(bufIdx);
        if (waylandDisp && waylandDisp->layerManager()) {
            bufMgr = waylandDisp->layerManager()->bufferManager();
        }
    }

    if (!bufMgr) {
        ALOGE("presentDisplay: virtual display=%" PRIu64
              " no buffer manager", display);
        return Error::NONE;
    }

    // For standalone containers: position the window before presenting. The
    // composed buffer always sits at the VD origin, so the on-screen position
    // is NOT in setLayerDisplayFrame (VD-local 0,0) — we derive it here.
    if (vd.standaloneLayerId > 0) {
        wl_output* output = nullptr;
        WaylandDisplay* waylandDisp = nullptr;
        if (globals) {
            size_t outIdx = (vd.containerDisplayIndex >= 0)
                    ? static_cast<size_t>(vd.containerDisplayIndex) : 0;
            waylandDisp = globals->display(outIdx);
            if (waylandDisp && waylandDisp->output()) {
                output = waylandDisp->output()->wlOutput();
            }
        }

        if (vd.windowCategory == WaylandWindowCategory::TOAST && waylandDisp) {
            // Toast placement is its own policy (centered, 4/5 down) — the
            // WaylandToastWindow owns it. Hand it the raw screen + toast size;
            // both are known on the first frame, so there is no top-left flash.
            const int32_t tw = (vd.screenW > 0) ? vd.screenW : vd.width;
            const int32_t th = (vd.screenH > 0) ? vd.screenH : vd.height;
            // currentWidth/Height follow the live rotation (tablet mode).
            vd.container->updateToastBounds(output, waylandDisp->currentWidth(),
                                            waylandDisp->currentHeight(), tw, th);
        } else if (vd.windowCategory == WaylandWindowCategory::MODAL && waylandDisp
                   && vd.screenW > 0 && vd.screenH > 0) {
            // Standalone floating dialog (e.g. the brightness dialog or the
            // GrantPermissions modal split out of its host app): place the
            // visible window at the CENTER of the display. The on-screen
            // position is decoupled from input: the SF input frame for an
            // IS_FLOATING_ACTIVITY layer is anchored at the screen origin
            // (Layer::fillInputFrameInfo) and the Wayland touch offset for a
            // standalone container is its containerFrame() == (0,0), so taps are
            // surface-relative and land correctly regardless of where the window
            // is shown. currentWidth/Height follow the live rotation.
            // (A whole-task dialog, e.g. PackageInstaller, is the task-keyed
            // MODAL path and is centered by ensureWindow instead.)
            const int32_t dispW = waylandDisp->currentWidth();
            const int32_t dispH = waylandDisp->currentHeight();
            int32_t cx = (dispW - vd.screenW) / 2;
            int32_t cy = (dispH - vd.screenH) / 2;
            if (cx < 0) cx = 0;
            if (cy < 0) cy = 0;
            vd.container->updateStandaloneBounds(output, cx, cy,
                                                 vd.screenW, vd.screenH);
        } else if (vd.windowCategory == WaylandWindowCategory::INPUT_METHOD
                   && waylandDisp && vd.screenW > 0 && vd.screenH > 0) {
            // The IME is the one standalone window that must dodge the shelf.
            // Android lays it out at the bottom of its display, but ash draws
            // the shelf over that strip — kShellWindowId_ShelfContainer stacks
            // above kShellWindowId_ArcVirtualKeyboardContainer — so placing it
            // at the Android frame buries the bottom key row (Gboard's space /
            // enter row) under the shelf.
            //
            // Lift the surface by the work area's BOTTOM INSET. Bottom-aligning
            // to the work area is wrong here: an IME window is usually the full
            // display (Gboard reports 2560x1600 and draws its keys at the
            // bottom of a mostly-transparent window), so bottom-aligning
            // computes a negative top and clamps to zero, moving nothing.
            // Shifting by the inset moves the *keys* clear of the shelf and
            // works for a keyboard-sized window too. The top edge going
            // negative is fine — that part of the window is transparent.
            //
            // workAreaHeight() already tracks the shelf, including a window
            // that hides it, so this needs no knowledge of shelf state.
            const int32_t bottomInset =
                    waylandDisp->currentHeight()
                    - (waylandDisp->workAreaY() + waylandDisp->workAreaHeight());
            const int32_t imeTop =
                    vd.screenY - (bottomInset > 0 ? bottomInset : 0);
            // Android's frame is passed separately so touch offsets keep using
            // the unshifted position (see WaylandInputMethodWindow).
            vd.container->updateInputMethodBounds(output, vd.screenX, imeTop,
                                                  vd.screenW, vd.screenH,
                                                  vd.screenX, vd.screenY,
                                                  vd.imeContentTop,
                                                  vd.imeTouchRects);
        } else if (vd.screenW > 0 && vd.screenH > 0) {
            // Overlay: use the Android window's display frame.
            vd.container->updateStandaloneBounds(output, vd.screenX, vd.screenY,
                                                 vd.screenW, vd.screenH);
        }
    }

    // Wait fence, import DMA-BUF, present via the container's surface.
    // (Returns false on import failure — logged inside — and on the
    // frame-callback throttle, which is routine for occluded windows.)
    vd.container->presentGraphicBuffer(bufMgr, vd.clientTarget,
                                       &vd.clientTargetFence);

    // Buffer consumed — clear for next frame.
    vd.clientTarget = nullptr;

    return Error::NONE;
}

Error WaylandHwc2Composer::setActiveConfig(Display display, Config config) {
    if (isVirtualDisplay(display)) return Error::NONE;
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    if (config != kConfigId) return Error::BAD_CONFIG;
    return Error::NONE;
}

Error WaylandHwc2Composer::setClientTarget(Display display, uint32_t slot,
                                            const sp<GraphicBuffer>& target,
                                            int acquireFence, Dataspace /*dataspace*/,
                                            const std::vector<IComposerClient::Rect>& /*damage*/) {
    // --- Virtual display ---
    if (auto* vdp = findVd(display)) {
        auto& vd = *vdp;

        // Resolve null buffers via per-display slot cache.
        sp<GraphicBuffer> resolved = target;
        if (resolved == nullptr && slot < VirtualDisplayData::kSlotCount) {
            resolved = vd.slotCache[slot];
        }
        if (target != nullptr && slot < VirtualDisplayData::kSlotCount) {
            vd.slotCache[slot] = target;
        }

        vd.clientTarget = resolved;
        if (vd.clientTargetFence >= 0) close(vd.clientTargetFence);
        vd.clientTargetFence = acquireFence;

        ALOGVT("setClientTarget: virtual display=%" PRIu64 " slot=%u buffer=%p %ux%u",
              display, slot, resolved.get(),
              resolved ? resolved->getWidth() : 0,
              resolved ? resolved->getHeight() : 0);
        return Error::NONE;
    }

    // --- Physical display: no-op ---
    if (!findDisplay(display)) {
        if (acquireFence >= 0) close(acquireFence);
        return Error::BAD_DISPLAY;
    }
    // Physical display has no composable layers — client target not needed
    if (acquireFence >= 0) close(acquireFence);
    return Error::NONE;
}

Error WaylandHwc2Composer::setColorMode(Display display, ColorMode /*mode*/,
                                         RenderIntent /*renderIntent*/) {
    if (isVirtualDisplay(display)) return Error::NONE;
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    return Error::NONE;
}

Error WaylandHwc2Composer::setColorTransform(Display /*display*/, const float* /*matrix*/) {
    // AOSP-13: the ColorTransform hint param was dropped.
    return Error::NONE;
}

Error WaylandHwc2Composer::setOutputBuffer(Display display, const native_handle_t* /*buffer*/,
                                            int releaseFence) {
    // Only SF's VirtualDisplaySurface calls setOutputBuffer, and only for
    // external (MediaProjection / screen-capture) virtual displays — fydroid's
    // own per-task VDs composite through setClientTarget and never land here.
    // SF owns and delivers the captured frames via that output buffer, so we
    // don't touch it. We DO use the call as the signal that this VD is external:
    // such a VD AUTO_MIRRORs display 0, so its mirrored layers carry foreign
    // TaskId metadata that would otherwise make us hijack — and on teardown
    // destroy — the real task's Wayland container (the window vanishes).
    if (releaseFence >= 0) close(releaseFence);
    if (isVirtualDisplay(display)) {
        auto& vd = mVirtualDisplays[display];
        // The notification VD has its OWN ImageReader output surface (added for
        // the 0x0-size-race fix + buffer drain), so SF's VirtualDisplaySurface
        // calls setOutputBuffer for it too — but it is NOT an external
        // screen-capture VD. Flagging it output-backed makes
        // setLayerGenericMetadata short-circuit before parsing the
        // NotificationKey, so every per-card surface comes up keyless and
        // presents blank. isNotificationDisplay is set deterministically at
        // createVirtualDisplay (by matching size), well before this call, so
        // this guard is reliable. Only genuinely external VDs are output-backed.
        if (vd.isNotificationDisplay) {
            return Error::NONE;
        }
        if (!vd.isOutputBacked) {
            vd.isOutputBacked = true;
            // The first frame's mirrored layer may already have latched a
            // foreign taskId (setLayerGenericMetadata runs before this call);
            // drop the association without destroying the real task's container.
            vd.taskId = -1;
            vd.container = nullptr;
            vd.containerDisplayIndex = -1;
            vd.standaloneLayerId = 0;
            ALOGI("setOutputBuffer: VD %" PRIu64 " is external (screen capture); "
                  "detached from task container", display);
        }
        return Error::NONE;
    }
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::setPowerMode(Display display, IComposerClient::PowerMode mode) {
    if (isVirtualDisplay(display)) return Error::NONE;
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    ALOGI("setPowerMode: display=%" PRIu64 " mode=%d", display, static_cast<int>(mode));
    return Error::NONE;
}

Error WaylandHwc2Composer::setVsyncEnabled(Display display, IComposerClient::Vsync enabled) {
    if (isVirtualDisplay(display)) return Error::NONE;
    auto* disp = findDisplay(display);
    if (!disp) return Error::BAD_DISPLAY;
    ALOGVT("setVsyncEnabled: display=%" PRIu64 " enabled=%d",
          display, static_cast<int>(enabled));
    disp->setVsyncEnabled(enabled == IComposerClient::Vsync::ENABLE);
    return Error::NONE;
}

Error WaylandHwc2Composer::setClientTargetSlotCount(Display /*display*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::validateDisplay(Display display, nsecs_t /*expectedPresentTime*/,
                                            uint32_t* outNumTypes,
                                            uint32_t* outNumRequests) {
    // AOSP-13: gained nsecs_t expectedPresentTime (unused here).
    *outNumTypes = 0;
    *outNumRequests = 0;
    if (auto* vdp = findVd(display)) {
        auto& vd = *vdp;
        vd.pendingClientLayers.clear();
        if (vd.isNotificationDisplay) {
            // Only request DEVICE for layers not already in DEVICE mode.
            //
            // "Already DEVICE" has to be judged against what SF requested, not
            // just against what we have reported before. SF proposes DEVICE for
            // these layers itself, so reporting DEVICE back is a no-op change
            // that SF rejects with "Invalid device requested composition type
            // change: Device --> Device" — one per notification layer as it
            // appears. deviceCompositionLayers alone never caught that, because
            // the very first report for a layer is the redundant one.
            for (Layer layer : vd.layers) {
                if (vd.deviceCompositionLayers.count(layer) != 0) continue;
                auto rit = vd.requestedCompositionTypes.find(layer);
                if (rit != vd.requestedCompositionTypes.end() &&
                    rit->second == V3_0::Composition::DEVICE) {
                    // SF already wants DEVICE — nothing to change. Record it so
                    // the bookkeeping matches SF's view of the layer.
                    vd.deviceCompositionLayers.insert(layer);
                    continue;
                }
                vd.pendingClientLayers.push_back(layer);
            }
        } else {
            // Regular VDs — always CLIENT (default is CLIENT, so only new layers)
            vd.pendingClientLayers.assign(vd.layers.begin(), vd.layers.end());
        }
        *outNumTypes = static_cast<uint32_t>(vd.pendingClientLayers.size());
        return Error::NONE;
    }
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    return Error::NONE;
}

Error WaylandHwc2Composer::presentOrValidateDisplay(Display display,
                                                     nsecs_t expectedPresentTime,
                                                     uint32_t* outNumTypes,
                                                     uint32_t* outNumRequests,
                                                     int* /*outPresentFence*/,
                                                     uint32_t* state) {
    *state = 0; // 0 = VALIDATED
    return validateDisplay(display, expectedPresentTime, outNumTypes, outNumRequests);
}

// --- Layer property setters ---

Error WaylandHwc2Composer::setCursorPosition(Display /*display*/, Layer /*layer*/,
                                              int32_t /*x*/, int32_t /*y*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerBuffer(Display display, Layer layer,
                                           uint32_t slot,
                                           const sp<GraphicBuffer>& buffer,
                                           int acquireFence) {
    // Notification VD: store per-layer buffer for DEVICE composition.
    auto* vdp = findVd(display);
    if (vdp && vdp->isNotificationDisplay) {
        auto& vd = *vdp;
        auto& nld = vd.notifLayers[layer];
        // Resolve buffer from PER-LAYER slot cache. SF uses the same slot
        // numbers across different layers, so the per-VD slotCache would
        // cause buffer cross-contamination between notification surfaces.
        sp<GraphicBuffer> resolvedBuffer = buffer;
        if (buffer && slot < VirtualDisplayData::kSlotCount) {
            nld.layerSlotCache[slot] = buffer;
        } else if (!buffer && slot < VirtualDisplayData::kSlotCount) {
            resolvedBuffer = nld.layerSlotCache[slot];
        }
        nld.buffer = resolvedBuffer;
        if (nld.acquireFence >= 0) close(nld.acquireFence);
        nld.acquireFence = acquireFence;
        ALOGV("setLayerBuffer: notif display=%" PRIu64 " layer=%" PRIu64
              " slot=%u buffer=%p resolved=%p",
              display, layer, slot,
              buffer.get(), resolvedBuffer.get());
        return Error::NONE;
    }
    // No per-layer buffer tracking — all presentation through setClientTarget.
    if (acquireFence >= 0) close(acquireFence);
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerSurfaceDamage(
        Display /*display*/, Layer /*layer*/,
        const std::vector<IComposerClient::Rect>& /*damage*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerBlendMode(Display /*display*/, Layer /*layer*/,
                                              IComposerClient::BlendMode /*mode*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerColor(Display /*display*/, Layer /*layer*/,
                                          const V3_0::Color& /*color*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerCompositionType(Display display, Layer layer,
                                                    V3_0::Composition type) {
    // Record what SF asked for. We do not act on it — presentation is driven by
    // setClientTarget for task VDs and per-layer buffers for the notification VD
    // — but getChangedCompositionTypes needs it to avoid reporting a type SF
    // already requested, which SF rejects as an invalid no-op change.
    if (auto* vd = findVd(display)) {
        vd->requestedCompositionTypes[layer] = type;
    }
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerDataspace(Display /*display*/, Layer /*layer*/,
                                              Dataspace /*dataspace*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerDisplayFrame(Display display, Layer layer,
                                                 const IComposerClient::Rect& frame) {
    if (auto* vdp = findVd(display)) {
        // Accumulate content bounds (union of all layer display frames).
        // wp_viewport uses these to crop the content region from the buffer.
        auto& vd = *vdp;
        if (frame.left < vd.contentLeft) vd.contentLeft = frame.left;
        if (frame.top < vd.contentTop) vd.contentTop = frame.top;
        if (frame.right > vd.contentRight) vd.contentRight = frame.right;
        if (frame.bottom > vd.contentBottom) vd.contentBottom = frame.bottom;
        // Store per-layer frame for notification VD input coordinate mapping.
        // Use operator[] to auto-create the entry — setLayerDisplayFrame may
        // arrive before setLayerGenericMetadata populates the notifKey.
        if (vd.isNotificationDisplay) {
            auto& nld = vd.notifLayers[layer];
            nld.frameLeft = frame.left;
            nld.frameTop = frame.top;
        }
        return Error::NONE;
    }
    // Physical display: no-op
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerPlaneAlpha(Display /*display*/, Layer /*layer*/,
                                               float /*alpha*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerSidebandStream(Display /*display*/, Layer /*layer*/,
                                                    const native_handle_t* /*stream*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::setLayerSourceCrop(Display /*display*/, Layer /*layer*/,
                                               const IComposerClient::FRect& /*crop*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerTransform(Display /*display*/, Layer /*layer*/,
                                              Transform /*transform*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerVisibleRegion(
        Display /*display*/, Layer /*layer*/,
        const std::vector<IComposerClient::Rect>& /*visible*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerZOrder(Display /*display*/, Layer /*layer*/, uint32_t /*z*/) {
    return Error::NONE;
}

// fydroid-rt (AOSP-13): setLayerInfo was removed from Hwc2::Composer in 12, so SF
// no longer drives window-type classification through it. Kept as a private helper
// (preserving the standalone-container creation logic); TODO: re-wire its trigger on
// 13 (window type now arrives via setLayerGenericMetadata, not setLayerInfo).
Error WaylandHwc2Composer::setLayerInfoInternal(Display display, Layer layer,
                                         uint32_t type, uint32_t /*appId*/) {
    if (isVirtualDisplay(display)) {
        auto& vd = mVirtualDisplays[display];

        // Notification display uses per-layer metadata — skip setLayerInfo handling
        if (vd.isNotificationDisplay) {
            return Error::NONE;
        }

        WaylandWindowCategory cat = classifyWindowType(type);

        // For standalone window types, set category and create container
        if (cat == WaylandWindowCategory::INPUT_METHOD ||
            cat == WaylandWindowCategory::TOAST ||
            cat == WaylandWindowCategory::OVERLAY ||
            cat == WaylandWindowCategory::NOTIFICATION) {
            vd.windowCategory = cat;
            vd.standaloneLayerId = layer;

            // Create/cache standalone container
            if (!vd.container || vd.container->isClosed()) {
                auto* globals = mClient ? mClient->globals() : nullptr;
                if (globals && globals->displayCount() > 0) {
                    auto* waylandDisp = globals->display(0);
                    if (waylandDisp && waylandDisp->layerManager()) {
                        vd.container = waylandDisp->layerManager()
                                ->registerStandaloneContainer(layer, cat);
                        ALOGI("setLayerInfo: virtual display=%" PRIu64
                              " layer=%" PRIu64 " created standalone cat=%d",
                              display, layer, static_cast<int>(cat));
                    }
                }
            }
        }
        return Error::NONE;
    }
    // Physical display: no-op
    return Error::NONE;
}

// --- Composer HAL 2.2 ---

Error WaylandHwc2Composer::setLayerPerFrameMetadata(
        Display /*display*/, Layer /*layer*/,
        const std::vector<IComposerClient::PerFrameMetadata>& /*perFrameMetadatas*/) {
    return Error::NONE;
}

std::vector<IComposerClient::PerFrameMetadataKey>
WaylandHwc2Composer::getPerFrameMetadataKeys(Display /*display*/) {
    return {};
}

Error WaylandHwc2Composer::getRenderIntents(Display display, ColorMode /*colorMode*/,
                                             std::vector<RenderIntent>* outRenderIntents) {
    if (isVirtualDisplay(display)) {
        outRenderIntents->push_back(RenderIntent::COLORIMETRIC);
        return Error::NONE;
    }
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    outRenderIntents->push_back(RenderIntent::COLORIMETRIC);
    return Error::NONE;
}

Error WaylandHwc2Composer::getDataspaceSaturationMatrix(Dataspace /*dataspace*/,
                                                         mat4* outMatrix) {
    *outMatrix = mat4(); // identity
    return Error::NONE;
}

// --- Composer HAL 2.3 ---

Error WaylandHwc2Composer::getDisplayIdentificationData(Display display, uint8_t* outPort,
                                                         std::vector<uint8_t>* outData) {
    if (isVirtualDisplay(display)) {
        return Error::UNSUPPORTED;
    }
    if (!findDisplay(display)) return Error::BAD_DISPLAY;

    // Determine display index for unique EDID generation
    uint8_t displayIndex = 0;
    for (size_t i = 0; i < mDisplays.size(); i++) {
        if (mDisplays[i].hwcDisplayId == display) {
            displayIndex = static_cast<uint8_t>(i);
            break;
        }
    }

    // 128-byte EDID block: PnP ID "GGL" (0x1CEC).
    // Product code and serial vary per display for unique DisplayId in SF.
    uint8_t edid[] = {
        0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00,
        0x1C, 0xEC,
        static_cast<uint8_t>(displayIndex + 1), 0x00,  // product code (unique per display)
        static_cast<uint8_t>(displayIndex + 1), 0x00, 0x00, 0x00,  // serial (unique per display)
        0x01, 0x22,
        0x01, 0x03,
        0x80, 0x00, 0x00, 0x78, 0x0A,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00,
        0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
        0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
        0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0xFC, 0x00,
        'F', 'y', 'd', 'r', 'o', 'i', 'd', 'D', 's', 'p',
        static_cast<uint8_t>('0' + displayIndex),  // "FydroidDsp0", "FydroidDsp1", etc.
        0x0A, 0x20,
        0x00,  // checksum placeholder
        0x00,  // will be recalculated
    };
    static_assert(sizeof(edid) == 128, "EDID block must be 128 bytes");

    // Recalculate EDID checksum: byte 127 = two's complement of sum of bytes 0-126
    uint8_t sum = 0;
    for (size_t i = 0; i < 127; i++) {
        sum += edid[i];
    }
    edid[127] = static_cast<uint8_t>(256 - sum);

    *outPort = displayIndex;
    outData->assign(edid, edid + sizeof(edid));
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerColorTransform(Display /*display*/, Layer /*layer*/,
                                                   const float* /*matrix*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::getDisplayedContentSamplingAttributes(
        Display /*display*/, PixelFormat* /*outFormat*/, Dataspace* /*outDataspace*/,
        uint8_t* /*outComponentMask*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::setDisplayContentSamplingEnabled(Display /*display*/, bool /*enabled*/,
                                                             uint8_t /*componentMask*/,
                                                             uint64_t /*maxFrames*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::getDisplayedContentSample(Display /*display*/, uint64_t /*maxFrames*/,
                                                      uint64_t /*timestamp*/,
                                                      DisplayedFrameStats* /*outStats*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::setLayerPerFrameMetadataBlobs(
        Display /*display*/, Layer /*layer*/,
        const std::vector<PerFrameMetadataBlob>& /*metadata*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setDisplayBrightness(Display /*display*/, float /*brightness*/,
                                                 float /*brightnessNits*/,
                                                 const DisplayBrightnessOptions& /*options*/) {
    // AOSP-13: gained brightnessNits + DisplayBrightnessOptions.
    return Error::UNSUPPORTED;
}

// --- Composer HAL 2.4 ---
// (AOSP-13 removed isVsyncPeriodSwitchSupported from Hwc2::Composer.)

Error WaylandHwc2Composer::getDisplayCapabilities(
        Display display, std::vector<V3_0::DisplayCapability>* outCapabilities) {
    if (isVirtualDisplay(display)) {
        outCapabilities->clear();
        return Error::NONE;
    }
    if (!findDisplay(display)) return Error::BAD_DISPLAY;
    outCapabilities->clear();
    return Error::NONE;
}

V2_4::Error WaylandHwc2Composer::getDisplayConnectionType(
        Display display, IComposerClient::DisplayConnectionType* outType) {
    if (isVirtualDisplay(display)) {
        return static_cast<V2_4::Error>(Error::UNSUPPORTED);
    }
    if (!findDisplay(display)) {
        return static_cast<V2_4::Error>(Error::BAD_DISPLAY);
    }
    *outType = (!mDisplays.empty() && mDisplays[0].hwcDisplayId == display)
            ? IComposerClient::DisplayConnectionType::INTERNAL
            : IComposerClient::DisplayConnectionType::EXTERNAL;
    return V2_4::Error::NONE;
}

V2_4::Error WaylandHwc2Composer::getDisplayVsyncPeriod(Display display,
                                                        VsyncPeriodNanos* outVsyncPeriod) {
    if (isVirtualDisplay(display)) {
        *outVsyncPeriod = 0;
        return V2_4::Error::NONE;
    }
    auto* disp = findDisplay(display);
    if (!disp) return static_cast<V2_4::Error>(Error::BAD_DISPLAY);
    *outVsyncPeriod = disp ? disp->vsyncPeriodNs() : WaylandDisplay::kFallbackVsyncPeriodNs;
    return V2_4::Error::NONE;
}

V2_4::Error WaylandHwc2Composer::setActiveConfigWithConstraints(
        Display /*display*/, Config /*config*/,
        const IComposerClient::VsyncPeriodChangeConstraints& /*vsyncPeriodChangeConstraints*/,
        VsyncPeriodChangeTimeline* /*outTimeline*/) {
    return V2_4::Error::UNSUPPORTED;
}

V2_4::Error WaylandHwc2Composer::setAutoLowLatencyMode(Display /*displayId*/, bool /*on*/) {
    return V2_4::Error::UNSUPPORTED;
}

V2_4::Error WaylandHwc2Composer::getSupportedContentTypes(
        Display /*displayId*/,
        std::vector<IComposerClient::ContentType>* outSupportedContentTypes) {
    outSupportedContentTypes->clear();
    return V2_4::Error::NONE;
}

V2_4::Error WaylandHwc2Composer::setContentType(Display /*displayId*/,
                                                  IComposerClient::ContentType /*contentType*/) {
    return V2_4::Error::UNSUPPORTED;
}

V2_4::Error WaylandHwc2Composer::setLayerGenericMetadata(
        Display display, Layer layer, const std::string& key,
        bool /*mandatory*/, const std::vector<uint8_t>& value) {

    // This runs for every metadata key of every layer of every frame, so reject
    // the common cases before doing any string work.
    //
    // Only virtual displays consume metadata (the physical-display path below
    // is a no-op), and every key we care about shares the ARC prefix. Matching
    // the prefix once and then dispatching on the short suffix replaces what
    // was a chain of up to eight full-length comparisons against ~35-character
    // literals — and lets a foreign key bail after one.
    auto* vdp = findVd(display);
    if (!vdp) {
        // Physical display: metadata is handled via the virtual display path
        // only. No container registration here — avoids ghost windows from
        // stale layers.
        return V2_4::Error::NONE;
    }

    static constexpr char kArcPrefix[] = "org.chromium.arc.V1_0.";
    static constexpr size_t kArcPrefixLen = sizeof(kArcPrefix) - 1;
    // Must match WindowManagerService.IME_TOUCH_REGION_MAX_RECTS.
    static constexpr int32_t kImeTouchRegionMaxRects = 16;
    if (key.size() <= kArcPrefixLen ||
        key.compare(0, kArcPrefixLen, kArcPrefix) != 0) {
        return V2_4::Error::NONE;
    }
    // Suffix of a known key: "TaskId", "IsGame", "WindowType", ...
    const char* const sub = key.c_str() + kArcPrefixLen;

    // --- Virtual display: extract TaskId to associate with container ---
    {
        auto& vd = *vdp;

        // External capture VDs mirror display 0; their layers carry foreign task
        // metadata that must not drive container association (see setOutputBuffer).
        if (vd.isOutputBacked) return V2_4::Error::NONE;

        // A standalone (toast / overlay / IME) VD must never latch a TaskId.
        // The IME window is the case that matters: WM stamps it with the TaskId
        // of the task its animation leash was re-parented under, so latching it
        // here would look up that task's container and present the keyboard's
        // buffer into the app's window. It also has to stay taskId <= 0 for the
        // "WindowType" handler below to create the standalone container at all,
        // and SF walks the metadata keys in unordered_map order — so relying on
        // WindowType arriving before TaskId would be a coin flip.
        if (vd.isStandalone && strcmp(sub, "TaskId") == 0) {
            return V2_4::Error::NONE;
        }

        if (strcmp(sub, "TaskId") == 0 && value.size() >= sizeof(int32_t)) {
            int32_t taskId;
            memcpy(&taskId, value.data(), sizeof(int32_t));
            if (vd.taskId != taskId) {
                vd.taskId = taskId;
                vd.container = nullptr;  // force re-lookup

                ALOGI("setLayerGenericMetadata: virtual display=%" PRIu64
                      " layer=%" PRIu64 " TaskId=%d",
                      display, layer, taskId);

                // Register container in layer manager if not exists.
                // Register on primary display initially; onOutputChanged
                // handles migration when Chrome moves the window to another output.
                if (taskId > 0) {
                    auto* globals = mClient ? mClient->globals() : nullptr;
                    if (globals) {
                        // First check if the container already exists on any display
                        for (size_t di = 0; di < globals->displayCount(); di++) {
                            auto* wd = globals->display(di);
                            if (wd && wd->layerManager()) {
                                vd.container = wd->layerManager()->findContainerByTaskId(taskId);
                                if (vd.container) break;
                            }
                        }
                        // If not found, register on primary display
                        if (!vd.container) {
                            auto* primary = globals->primaryDisplay();
                            if (primary && primary->layerManager()) {
                                // Use content bounds (from setLayerDisplayFrame)
                                // for the task frame, not vd.width/height which
                                // is the display-sized rendering canvas.
                                int32_t cw = (vd.contentRight > vd.contentLeft)
                                        ? (vd.contentRight - vd.contentLeft) : 0;
                                int32_t ch = (vd.contentBottom > vd.contentTop)
                                        ? (vd.contentBottom - vd.contentTop) : 0;
                                WaylandLayerContainer::Rect frame = {0, 0, cw, ch};
                                primary->layerManager()->registerContainer(taskId, frame);
                                vd.container = primary->layerManager()->findContainerByTaskId(taskId);
                            }
                        }
                    }
                }
                // Replay an IsGame that arrived before this TaskId.
                if (vd.container && vd.pendingIsGame >= 0) {
                    vd.container->setIsGame(vd.pendingIsGame != 0);
                }
                // Same for IsSecure — the container must be protected before it
                // ever presents, and SF has already stopped blacking the layer
                // out, so a dropped replay here is a capture leak.
                if (vd.container && vd.pendingIsSecure) {
                    vd.container->setSecure(true);
                }
            }
        }
        if (strcmp(sub, "SessionId") == 0 && value.size() >= sizeof(int32_t)) {
            int32_t sessionId;
            memcpy(&sessionId, value.data(), sizeof(int32_t));
            if (vd.taskId > 0 && sessionId > 0) {
                auto* globals = mClient ? mClient->globals() : nullptr;
                if (globals) {
                    for (size_t di = 0; di < globals->displayCount(); di++) {
                        auto* wd = globals->display(di);
                        if (wd && wd->layerManager() && wd->layerManager()->findContainerByTaskId(vd.taskId)) {
                            wd->layerManager()->setContainerSessionId(vd.taskId, sessionId);
                            break;
                        }
                    }
                }
            }
            ALOGVT("setLayerGenericMetadata: virtual display=%" PRIu64
                   " layer=%" PRIu64 " SessionId=%d", display, layer, sessionId);
        }
        if (strcmp(sub, "IsGame") == 0 && value.size() >= sizeof(int32_t)) {
            int32_t isGame;
            memcpy(&isGame, value.data(), sizeof(int32_t));
            vd.pendingIsGame = isGame;
            if (vd.container) vd.container->setIsGame(isGame != 0);
            ALOGVT("setLayerGenericMetadata: virtual display=%" PRIu64
                   " layer=%" PRIu64 " IsGame=%d container=%d",
                   display, layer, isGame, vd.container ? 1 : 0);
        }
        if (strcmp(sub, "IsSecure") == 0 && value.size() >= sizeof(int32_t)) {
            int32_t isSecure;
            memcpy(&isSecure, value.data(), sizeof(int32_t));
            // SF only ever sends 1 (see OutputLayer's fydroid block), and the
            // container latches on, so this is one-way by construction.
            if (isSecure != 0) {
                vd.pendingIsSecure = true;
                if (vd.container) vd.container->setSecure(true);
            }
            ALOGVT("setLayerGenericMetadata: virtual display=%" PRIu64
                   " layer=%" PRIu64 " IsSecure=%d container=%d",
                   display, layer, isSecure, vd.container ? 1 : 0);
        }
        if (strcmp(sub, "IsDialogTask") == 0 && value.size() >= sizeof(int32_t)) {
            int32_t isDialog;
            memcpy(&isDialog, value.data(), sizeof(int32_t));
            if (isDialog != 0) vd.dialogDetectedThisFrame = true;
        }
        // A floating-themed activity stacked on a non-dialog task (e.g. the
        // brightness dialog over Settings). WM stripped its TaskId, so it lands
        // on a standalone (no-taskId) VD here; its WM window type is a plain
        // TYPE_BASE_APPLICATION (classifyWindowType → APP), so the WindowType
        // handler below won't claim it. Register it as a frameless MODAL window
        // (WaylandModalWindow) so it floats over the host app instead of growing
        // the host task window.
        // Not an IME: WM stamps IsFloatingActivity on the input-method window
        // too, and because metadata keys arrive one per call in unordered_map
        // order this branch can run before the WindowType one and claim the
        // keyboard as a MODAL. INPUT_METHOD wins and is sticky — see the
        // category rebuild in WaylandLayerManager::registerStandaloneContainer,
        // which handles the opposite arrival order.
        if (strcmp(sub, "IsFloatingActivity") == 0
                && value.size() >= sizeof(int32_t)
                && !vd.isNotificationDisplay && vd.taskId <= 0
                && vd.windowCategory != WaylandWindowCategory::INPUT_METHOD) {
            int32_t isFloating;
            memcpy(&isFloating, value.data(), sizeof(int32_t));
            if (isFloating != 0 &&
                (vd.windowCategory != WaylandWindowCategory::MODAL ||
                 vd.standaloneLayerId != layer ||
                 !vd.container || vd.container->isClosed())) {
                vd.windowCategory = WaylandWindowCategory::MODAL;
                vd.standaloneLayerId = layer;
                auto* globals = mClient ? mClient->globals() : nullptr;
                if (globals && globals->displayCount() > 0) {
                    auto* waylandDisp = globals->display(0);
                    if (waylandDisp && waylandDisp->layerManager()) {
                        vd.container = waylandDisp->layerManager()
                                ->registerStandaloneContainer(
                                        layer, WaylandWindowCategory::MODAL);
                        ALOGI("IsFloatingActivity: display=%" PRIu64 " layer=%"
                              PRIu64 " standalone MODAL container=%p",
                              display, layer, vd.container);
                    }
                }
            }
        }
        if (strcmp(sub, "NotificationKey") == 0 && !value.empty()
                && vd.taskId <= 0) {
            // A layer carrying a NotificationKey identifies the notification VD
            // robustly. The createVirtualDisplay notification flag is racy — a
            // task VD created in the same window steals it (see the taskId guard
            // in presentDisplay), leaving the real notification VD un-flagged so
            // its surfaces never present. Only notification windows carry this
            // metadata, so use it as the source of truth.
            if (!vd.isNotificationDisplay) {
                vd.isNotificationDisplay = true;
                // Undo any standalone (overlay) misclassification — notification
                // windows are TYPE_APPLICATION_OVERLAY too.
                vd.windowCategory = WaylandWindowCategory::NOTIFICATION;
                vd.standaloneLayerId = 0;
                ALOGI("NotificationKey: marking display=%" PRIu64
                      " as notification VD", display);
            }
            std::string notifKey = parseNotifKeyFromMetadata(value);
            if (!notifKey.empty()) {
                auto& nld = vd.notifLayers[layer];
                if (nld.notifKey != notifKey) {
                    nld.notifKey = notifKey;
                    nld.container = nullptr;  // force re-lookup
                    ALOGI("NotificationKey: display=%" PRIu64 " layer=%" PRIu64
                          " key=%s", display, layer, notifKey.c_str());
                }
            }
        }
        // Where the IME's keys start inside its full-display window. Without
        // it the input-method surface has no input region and swallows every
        // tap on the desktop; see WindowManagerService.stampImeContentTopIfNeeded.
        if (strcmp(sub, "ImeContentTop") == 0 && value.size() >= sizeof(int32_t)) {
            int32_t contentTop = 0;
            memcpy(&contentTop, value.data(), sizeof(int32_t));
            // Optional tail: rect count + left,top,right,bottom per rect
            // (SurfaceControl.METADATA_IME_CONTENT_TOP). Bounded so a bad
            // count cannot read past the value.
            std::vector<int32_t> rects;
            if (value.size() >= 2 * sizeof(int32_t)) {
                int32_t count = 0;
                memcpy(&count, value.data() + sizeof(int32_t), sizeof(int32_t));
                const size_t avail = (value.size() - 2 * sizeof(int32_t))
                        / (4 * sizeof(int32_t));
                if (count > 0 && static_cast<size_t>(count) <= avail
                        && count <= kImeTouchRegionMaxRects) {
                    rects.resize(static_cast<size_t>(count) * 4);
                    memcpy(rects.data(), value.data() + 2 * sizeof(int32_t),
                           rects.size() * sizeof(int32_t));
                }
            }
            if (vd.imeContentTop != contentTop || vd.imeTouchRects != rects) {
                vd.imeContentTop = contentTop;
                vd.imeTouchRects = std::move(rects);
                ALOGI("ImeContentTop: display=%" PRIu64 " layer=%" PRIu64
                      " top=%d rects=%zu", display, layer, contentTop,
                      vd.imeTouchRects.size() / 4);
            }
        }

        // The Android accessibility window id of a standalone window, which
        // stock AccessibilityWindowManager stamps on the window's surface
        // (METADATA_ACCESSIBILITY_ID; SF inherits it down to the buffer layer).
        // Toasts, overlays and task-less modals have no task and no key, so
        // this is the only exact name for them; becomes
        // zaura_surface.set_accessibility_id, which is how Chrome finds the
        // surface an accessibility tree belongs to.
        //
        // Task and notification VDs are deliberately ignored: every layer in a
        // task VD carries its own window's id (the app, its popups, a joined
        // PIP menu), so no single one names the surface — those are published
        // by ArcAccessibilityHelperService instead. The IME has its own path.
        if (strcmp(sub, "AccessibilityId") == 0 && value.size() >= sizeof(int32_t)
                && !vd.isNotificationDisplay && vd.taskId <= 0) {
            int32_t a11yId;
            memcpy(&a11yId, value.data(), sizeof(int32_t));
            vd.standaloneA11yId = a11yId;
            vd.standaloneA11yLayer = layer;
            if (vd.container && vd.standaloneLayerId == layer
                    && vd.windowCategory != WaylandWindowCategory::INPUT_METHOD) {
                vd.container->setStandaloneAccessibilityId(a11yId);
            }
        }

        // Standalone window classification (toast / IME / overlay).
        // On AOSP-13 HWC2::setLayerInfo (which used to drive this) was removed;
        // the WM window type now arrives here as generic metadata. A standalone
        // layer (no TaskId) gets its own VD from CompositionEngineArc Pass 2 and
        // is GPU-composited; this is the only place that learns its window type
        // and creates the matching Wayland surface so the composed buffer is
        // actually presented. Skip task VDs (APP windows) and notification VDs.
        if (strcmp(sub, "WindowType") == 0
                && value.size() >= sizeof(int32_t)
                && !vd.isNotificationDisplay && vd.taskId <= 0) {
            int32_t windowType;
            memcpy(&windowType, value.data(), sizeof(int32_t));
            WaylandWindowCategory cat =
                    classifyWindowType(static_cast<uint32_t>(windowType));
            // A sub-window with no task is attached to a standalone system
            // window — the spinner dropdown (PopupWindow) of a SystemUIDialog
            // such as the MediaProjection prompt. As APP it got no surface and
            // stayed invisible while still taking input. Present it as an
            // OVERLAY: its own surface at its own display bounds, above the
            // dialog. (Sub-windows of app windows carry a TaskId and never get
            // here.)
            if (cat == WaylandWindowCategory::APP && windowType >= 1000 &&
                windowType <= 1999) {
                cat = WaylandWindowCategory::OVERLAY;
            }
            // MODAL is in this set for TYPE_SYSTEM_ALERT (the ANR / crash
            // dialog) — see classifyWindowType. It cannot collide with the
            // IsFloatingActivity branch above, which also produces MODAL: that
            // one fires on a floating ACTIVITY, whose window type is
            // TYPE_BASE_APPLICATION (classifyWindowType -> APP), so the two
            // paths never claim the same layer.
            if (cat == WaylandWindowCategory::TOAST ||
                cat == WaylandWindowCategory::OVERLAY ||
                cat == WaylandWindowCategory::MODAL ||
                cat == WaylandWindowCategory::INPUT_METHOD) {
                if (vd.windowCategory != cat || vd.standaloneLayerId != layer ||
                    !vd.container || vd.container->isClosed()) {
                    vd.windowCategory = cat;
                    vd.standaloneLayerId = layer;
                    auto* globals = mClient ? mClient->globals() : nullptr;
                    if (globals && globals->displayCount() > 0) {
                        // Create the container on the target output when this VD
                        // belongs to a secondary display (Presentation on the
                        // external HDMI); default to the primary (index 0). The
                        // container binds to whichever WaylandDisplay's
                        // LayerManager it is registered on, so this is what puts
                        // the surface — and its set_bounds_in_output placement —
                        // on the right host wl_output.
                        size_t outIdx = (vd.targetOutputIndex >= 0 &&
                                         static_cast<size_t>(vd.targetOutputIndex)
                                                 < globals->displayCount())
                                ? static_cast<size_t>(vd.targetOutputIndex) : 0;
                        auto* waylandDisp = globals->display(outIdx);
                        if (waylandDisp && waylandDisp->layerManager()) {
                            vd.container = waylandDisp->layerManager()
                                    ->registerStandaloneContainer(layer, cat);
                            vd.containerDisplayIndex = static_cast<int32_t>(outIdx);
                            ALOGI("WindowType: display=%" PRIu64 " layer=%" PRIu64
                                  " windowType=%d cat=%d outIdx=%zu standalone container=%p",
                                  display, layer, windowType,
                                  static_cast<int>(cat), outIdx, vd.container);
                            // Replay an AccessibilityId that arrived first.
                            if (vd.container && vd.standaloneA11yLayer == layer
                                    && vd.standaloneA11yId >= 0
                                    && cat != WaylandWindowCategory::INPUT_METHOD) {
                                vd.container->setStandaloneAccessibilityId(
                                        vd.standaloneA11yId);
                            }
                        }
                    }
                }
            }
        }
    }
    return V2_4::Error::NONE;
}

V2_4::Error WaylandHwc2Composer::getLayerGenericMetadataKeys(
        std::vector<IComposerClient::LayerGenericMetadataKey>* outKeys) {
    outKeys->clear();
    outKeys->push_back({"org.chromium.arc.V1_0.TaskId", false});
    outKeys->push_back({"org.chromium.arc.V1_0.SessionId", false});
    outKeys->push_back({"org.chromium.arc.V1_0.IsGame", false});
    outKeys->push_back({"org.chromium.arc.V1_0.IsSecure", false});
    outKeys->push_back({"org.chromium.arc.V1_0.IsDialogTask", false});
    outKeys->push_back({"org.chromium.arc.V1_0.IsFloatingActivity", false});
    outKeys->push_back({"org.chromium.arc.V1_0.IsHome", false});
    outKeys->push_back({"org.chromium.arc.V1_0.NotificationKey", false});
    outKeys->push_back({"org.chromium.arc.V1_0.WindowType", false});
    outKeys->push_back({"org.chromium.arc.V1_0.ImeContentTop", false});
    outKeys->push_back({"org.chromium.arc.V1_0.AccessibilityId", false});
    return V2_4::Error::NONE;
}

Error WaylandHwc2Composer::getClientTargetProperty(
        Display /*display*/,
        V3_0::ClientTargetPropertyWithBrightness* outClientTargetProperty) {
    // AOSP-13: composer3 ClientTargetPropertyWithBrightness wraps the
    // ClientTargetProperty (AIDL pixelFormat/dataspace) plus a brightness.
    outClientTargetProperty->clientTargetProperty.pixelFormat =
            ::aidl::android::hardware::graphics::common::PixelFormat::RGBA_8888;
    outClientTargetProperty->clientTargetProperty.dataspace =
            ::aidl::android::hardware::graphics::common::Dataspace::UNKNOWN;
    outClientTargetProperty->brightness = 1.0f;
    return Error::NONE;
}

// --- AIDL Composer (new in AOSP-13) ---

Error WaylandHwc2Composer::setLayerBrightness(Display /*display*/, Layer /*layer*/,
                                               float /*brightness*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setLayerBlockingRegion(
        Display /*display*/, Layer /*layer*/,
        const std::vector<IComposerClient::Rect>& /*blocking*/) {
    return Error::NONE;
}

Error WaylandHwc2Composer::setBootDisplayConfig(Display /*displayId*/, Config /*config*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::clearBootDisplayConfig(Display /*displayId*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::getPreferredBootDisplayConfig(Display /*displayId*/,
                                                          Config* /*outConfig*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::getDisplayDecorationSupport(
        Display /*display*/,
        std::optional<::aidl::android::hardware::graphics::common::DisplayDecorationSupport>*
                support) {
    support->reset();  // no display-decoration (R8G8B8A8 alpha) support
    return Error::NONE;
}

Error WaylandHwc2Composer::setIdleTimerEnabled(Display /*displayId*/,
                                                std::chrono::milliseconds /*timeout*/) {
    return Error::UNSUPPORTED;
}

Error WaylandHwc2Composer::getPhysicalDisplayOrientation(Display /*displayId*/,
                                                          AidlTransform* outDisplayOrientation) {
    *outDisplayOrientation = AidlTransform::NONE;
    return Error::NONE;
}

} // namespace android::Hwc2

// TODO(b/129481165): remove the #pragma below and fix conversion issues
#pragma clang diagnostic pop // ignored "-Wconversion"

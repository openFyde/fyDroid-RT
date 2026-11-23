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

// WaylandClient: IWaylandClient implementation.
// Owns WaylandConnection + WaylandGlobals and orchestrates init.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandClient"
#define ATRACE_TAG ATRACE_TAG_GRAPHICS

#include "WaylandClient.h"
#include "WaylandConnection.h"
#include "WaylandDataDevice.h"
#include "WaylandGlobals.h"
#include "display/WaylandDisplay.h"
#include "input/WaylandInputManager.h"
#include "hwc2/WaylandTaskResizer.h"

#include <wayland-client.h>
#include <log/log.h>
#include <poll.h>

namespace android {

static constexpr const char* kWaylandSocket = "/var/run/chrome/wayland-0";

WaylandClient::WaylandClient() = default;

WaylandClient::~WaylandClient() {
    // Stop dispatching before anything the loop touches goes away
    mEventRunning = false;
    if (mEventThread.joinable()) {
        mEventThread.join();
    }
    // Destroy data device first (destroys wl_data_source, wl_data_device)
    mDataDevice.reset();
    // Destroy input manager (closes the InputBridge pipe + Wayland objects)
    mInputManager.reset();
    // Destroy globals (destroys displays → layers → surfaces)
    mGlobals.reset();
    // Then disconnect (close socket last)
    mConnection.reset();
}

bool WaylandClient::init() {
    // 1. Connect to Chrome's Exo compositor
    mConnection = std::make_unique<WaylandConnection>();
    if (!mConnection->connect(kWaylandSocket)) {
        mConnection.reset();
        return false;
    }

    // 2. Set up registry and discover globals
    mGlobals = std::make_unique<WaylandGlobals>(mConnection->display());
    mGlobals->bindRegistry();

    // 3. First roundtrip: discover and bind globals
    mConnection->roundtrip();

    // 4. Second roundtrip: receive initial events from bound globals
    //    (wl_output.mode, zcr_remote_shell_v2 events, output manager, etc.)
    mConnection->roundtrip();

    // 5. Validate globals and create WaylandDisplay(s)
    //    (also subscribes to zcr_vsync_timing_v1 if available)
    if (!mGlobals->finalize(this)) {
        ALOGE("WaylandGlobals::finalize() failed — required globals missing");
        mGlobals.reset();
        mConnection.reset();
        return false;
    }
    startEventThread();

    // 6. Third roundtrip: receive initial zcr_vsync_timing_v1::update event
    mConnection->roundtrip();

    // 7. Initialize input — open the InputBridge pipe and bind wl_pointer/keyboard
    if (mGlobals->seat()) {
        float dsf = 1.0f;
        auto* primary = mGlobals->primaryDisplay();
        if (primary && primary->scaleFactor() > 0.0f) {
            dsf = primary->scaleFactor();
        }
        mInputManager = std::make_unique<WaylandInputManager>();
        mInputManager->initialize(mGlobals->seat(), mGlobals->seatCapabilities(), dsf);

        // Initialize extended input protocols (stylus, relative pointer, gamepad)
        mInputManager->initializeExtended(
                mGlobals->stylus(),
                mGlobals->relativePointerManager(),
                mGlobals->pointerConstraints(),
                mGlobals->gamingInput(),
                mGlobals->seat(),
                mGlobals->keyboardConfiguration(),
                mGlobals->cursorShapes());
        mInputManager->attachKeyboardExtension(mGlobals->keyboardExtension(),
                                               mConnection->display());
    } else {
        ALOGW("No wl_seat available — input will not be forwarded");
    }

    // 8. Initialize clipboard (data device)
    if (mGlobals->dataDeviceManager() && mGlobals->seat()) {
        mDataDevice = std::make_unique<WaylandDataDevice>();
        mDataDevice->init(mGlobals->dataDeviceManager(), mGlobals->seat(),
                          mConnection->display());
    } else {
        ALOGW("No wl_data_device_manager — clipboard will not be available");
    }

    ALOGI("WaylandClient initialized successfully");
    return true;
}

bool WaylandClient::isInitialized() const {
    return mGlobals != nullptr && mGlobals->primaryDisplay() != nullptr;
}

WaylandDisplay* WaylandClient::primaryDisplay() const {
    return mGlobals ? mGlobals->primaryDisplay() : nullptr;
}

int32_t WaylandClient::displayIndexForOutput(wl_output* output) const {
    return mGlobals ? mGlobals->displayIndexForOutput(output) : -1;
}

void WaylandClient::startEventThread() {
    mEventRunning = true;
    mEventThread = std::thread([this]() {
        wl_display* dpy = mConnection->display();
        int fd = wl_display_get_fd(dpy);
        // This thread holds no SurfaceFlinger state, so it may block briefly on
        // an Android focus change before injecting a touch.
        WaylandTaskResizer::allowBlockingAwaitOnThisThread();
        ALOGI("WaylandEventThread started (fd=%d)", fd);

        while (mEventRunning) {
            // Flush outgoing requests
            wl_display_flush(dpy);

            // Poll for incoming events with 100ms timeout
            struct pollfd pfd = { fd, POLLIN, 0 };
            int ret = poll(&pfd, 1, 100);
            if (ret > 0 && (pfd.revents & POLLIN)) {
                if (wl_display_prepare_read(dpy) == 0) {
                    wl_display_read_events(dpy);
                }
                wl_display_dispatch_pending(dpy);
            } else if (ret == 0) {
                // Timeout — dispatch any pending events anyway
                wl_display_dispatch_pending(dpy);
            }

            // Process any pending clipboard text from binder thread
            auto* dataDevice = WaylandDataDevice::instance();
            if (dataDevice) {
                dataDevice->processPending();
            }

            // Same handoff for the caption colours ArcSystemUiService pushes:
            // a binder thread stores them, this thread is the one allowed to
            // put them on a surface.
            auto* globals = WaylandGlobals::instance();
            if (globals) {
                globals->processPendingFrameColors();
                // And the same for a task entering or leaving Android PIP:
                // ArcWindowManagerService records it from a binder thread,
                // the pip/restore request has to leave from here.
                globals->processPendingPipState();
                // And for the accessibility window id ArcAccessibilityHelper-
                // Service pushes for each task, which becomes a zaura request.
                globals->processPendingAccessibilityIds();
            }

            // Pointer capture: InputReaderArc records the request from a
            // binder thread, the zwp_pointer_constraints lock_pointer request
            // (and the relative-pointer listener it arms) leaves from here.
            auto* input = WaylandInputManager::instance();
            if (input) {
                input->processPendingPointerCapture();
            }
        }

        ALOGI("WaylandEventThread stopped");
    });
    pthread_setname_np(mEventThread.native_handle(), "WlEventThread");
}

// --- IWaylandClient interface ---

wl_display* WaylandClient::getDisplay() {
    return mConnection ? mConnection->display() : nullptr;
}

wl_compositor* WaylandClient::getCompositor() {
    return mGlobals ? mGlobals->compositor() : nullptr;
}

wl_subcompositor* WaylandClient::getSubcompositor() {
    return mGlobals ? mGlobals->subcompositor() : nullptr;
}

wl_shm* WaylandClient::getShm() {
    return nullptr; // not bound yet
}

wl_seat* WaylandClient::getSeat() {
    return mGlobals ? mGlobals->seat() : nullptr;
}

zcr_remote_shell_v2* WaylandClient::getRemoteShell() {
    return mGlobals ? mGlobals->remoteShell() : nullptr;
}

zwp_linux_dmabuf_v1* WaylandClient::getLinuxDmabuf() {
    return mGlobals ? mGlobals->dmabuf() : nullptr;
}

zaura_shell* WaylandClient::getAuraShell() {
    return mGlobals ? mGlobals->auraShell() : nullptr;
}

zcr_vsync_feedback_v1* WaylandClient::getVsyncFeedback() {
    return mGlobals ? mGlobals->vsyncFeedback() : nullptr;
}

surface_augmenter* WaylandClient::getSurfaceAugmenter() {
    return mGlobals ? mGlobals->surfaceAugmenter() : nullptr;
}

wl_surface* WaylandClient::createSurface() {
    auto* compositor = getCompositor();
    return compositor ? wl_compositor_create_surface(compositor) : nullptr;
}

wl_region* WaylandClient::createRegion() {
    auto* compositor = getCompositor();
    return compositor ? wl_compositor_create_region(compositor) : nullptr;
}

int WaylandClient::dispatch() {
    auto* display = getDisplay();
    return display ? wl_display_dispatch_pending(display) : -1;
}

int WaylandClient::flush() {
    return mConnection ? mConnection->flush() : -1;
}

int WaylandClient::getFd() {
    return mConnection ? mConnection->fd() : -1;
}

} // namespace android

#pragma clang diagnostic pop

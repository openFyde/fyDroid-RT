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

// WaylandBinderService: Implements org.chromium.arc.wayland.IWaylandService.
//
// Transaction code 6 = getDisplayLayout:
//   Reply: writeNoException(), writeInt(1), ArcDisplayLayout parcel
//
// ArcDisplayLayout parcel (size-prefixed):
//   writeInt(layoutMode)        — 1=CLAMSHELL, 2=TABLET
//   writeLong(primaryPhysicalDisplayId) — matches display 0
//   writeTypedArray(ArcDisplay[])
//   writeFloat(defaultDeviceScaleFactor)
//
// ArcDisplay parcel (size-prefixed):
//   writeLong(physicalDisplayId)
//   writeInt(globalPositionX), writeInt(globalPositionY)
//   writeInt(widthPixels), writeInt(heightPixels)
//   writeInt(transform)  — 0=0°, 1=90°, 2=180°, 3=270°
//   writeInt(insetsLeft/Top/Right/Bottom)  — 4 ints
//   writeInt(stableInsetsLeft/Top/Right/Bottom) — 4 ints
//   writeInt(autoHide)   — boolean as int
//   writeFloat(deviceScaleFactor)
//   writeInt(dpi), writeInt(overrideDpi)
//   writeInt(displayRemapped)  — boolean as int

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"

#define LOG_TAG "WaylandBinderService"

#include "WaylandBinderService.h"
#include "WaylandLayoutParcel.h"
#include "WaylandShellHandlerProxy.h"
#include "../WaylandDataDevice.h"
#include "../WaylandGlobals.h"
#include "../display/WaylandDisplay.h"
#include "../layer/WaylandLayerManager.h"
#include "../hwc2/WaylandSessionIdResolver.h"
#include "../input/WaylandInputManager.h"
#include "../input/WaylandKeyboardHandler.h"

#include <android-base/unique_fd.h>
#include <binder/IServiceManager.h>
#include <binder/Parcel.h>
#include <log/log.h>
#include "../WaylandLog.h"

namespace android {

static const String16 kDescriptor("org.chromium.arc.wayland.IWaylandService");

std::mutex WaylandBinderService::sGlobalsMutex;
WaylandGlobals* WaylandBinderService::sGlobals = nullptr;

void WaylandBinderService::publish() {
    sp<IServiceManager> sm = defaultServiceManager();
    status_t err = sm->addService(String16("Wayland"), new WaylandBinderService());
    if (err != NO_ERROR) {
        ALOGE("Failed to register Wayland binder service: %d", err);
    } else {
        ALOGI("Registered Wayland binder service");
    }
}

void WaylandBinderService::setGlobals(WaylandGlobals* globals) {
    std::lock_guard<std::mutex> lock(sGlobalsMutex);
    sGlobals = globals;
    ALOGI("Globals set: %p (%zu displays)", globals,
          globals ? globals->displayCount() : 0);
}

status_t WaylandBinderService::onTransact(uint32_t code, const Parcel& data,
                                          Parcel* reply, uint32_t /*flags*/) {
    if (code == IBinder::INTERFACE_TRANSACTION) {
        reply->writeString16(kDescriptor);
        return NO_ERROR;
    }

    if (!data.enforceInterface(kDescriptor)) {
        return BAD_TYPE;
    }

    if (code == TRANSACTION_getDisplayLayout) {
        std::lock_guard<std::mutex> lock(sGlobalsMutex);
        WaylandLayoutParcel::writeDisplayLayout(reply, sGlobals);
        return NO_ERROR;
    }

    if (code == TRANSACTION_registerWaylandShellHandler) {
        sp<IBinder> handler = data.readStrongBinder();
        ALOGI("registerWaylandShellHandler: %p", handler.get());
        WaylandShellHandlerProxy::instance()->setHandler(handler);
        // Push the current layout to the freshly-registered handler so
        // ArcDisplayLayoutHandler picks up CLAMSHELL/TABLET state without
        // having to call getDisplayLayout() separately.
        {
            std::lock_guard<std::mutex> lock(sGlobalsMutex);
            WaylandShellHandlerProxy::instance()->pushDisplayLayout(sGlobals);
        }
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_notifyInputBridgePipeOpening) {
        android::base::unique_fd fd;
        status_t err = data.readUniqueFileDescriptor(&fd);
        if (err != NO_ERROR) {
            ALOGE("notifyInputBridgePipeOpening: failed to read fd: %d", err);
            reply->writeInt32(err);
            return NO_ERROR;
        }
        ALOGI("notifyInputBridgePipeOpening: received fd %d", fd.get());
        auto* mgr = WaylandInputManager::instance();
        if (mgr) {
            mgr->setInputBridgePipeFd(fd.release());
        } else {
            ALOGW("notifyInputBridgePipeOpening: InputManager not ready, dropping fd");
        }
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_requestPointerCapture) {
        const bool enable = data.readInt32() != 0;
        auto* mgr = WaylandInputManager::instance();
        if (mgr) {
            mgr->requestPointerCapture(enable);
        } else {
            ALOGW("requestPointerCapture(%d): InputManager not ready", enable);
        }
        if (reply) reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_setClipboardText) {
        String16 text16 = data.readString16();
        String8 text8(text16);
        auto* dev = WaylandDataDevice::instance();
        if (dev) {
            dev->setClipboardText(std::string(text8.string(), text8.size()));
        } else {
            ALOGE("setClipboardText: WaylandDataDevice not ready");
        }
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_registerClipboardCallback) {
        sp<IBinder> callback = data.readStrongBinder();
        auto* dev = WaylandDataDevice::instance();
        if (dev) {
            dev->setClipboardCallback(callback);
        } else {
            ALOGW("registerClipboardCallback: WaylandDataDevice not ready");
        }
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_startDrag) {
        int32_t taskId = data.readInt32();
        // Read String[] mimeTypes
        int32_t count = data.readInt32();
        std::vector<std::string> mimeTypes;
        for (int32_t i = 0; i < count; i++) {
            String8 s(data.readString16());
            mimeTypes.push_back(std::string(s.string(), s.size()));
        }
        ALOGI("startDrag: taskId=%d, %zu MIME types", taskId, mimeTypes.size());
        // TODO: implement Wayland DnD source
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_notifyDropResult) {
        data.readInt32();  // consumed (unused)
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_setClipboardMimes) {
        // Read String[] mimeTypes
        int32_t count = data.readInt32();
        std::vector<std::string> mimeTypes;
        for (int32_t i = 0; i < count; i++) {
            String8 s(data.readString16());
            mimeTypes.push_back(std::string(s.string(), s.size()));
        }
        ALOGI("setClipboardMimes: %zu MIME types", mimeTypes.size());
        auto* dev = WaylandDataDevice::instance();
        if (dev) {
            dev->setClipboardMimes(mimeTypes);
        }
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_receiveDragData) {
        String8 mimeType(data.readString16());
        int32_t offerId = data.readInt32();
        ALOGVT("receiveDragData: mime=%s offerId=%d", mimeType.string(), offerId);
        auto* dev = WaylandDataDevice::instance();
        int fd = dev ? dev->receiveFromOffer(offerId, std::string(mimeType.string()))
                     : -1;
        reply->writeNoException();
        if (fd >= 0) {
            reply->writeFileDescriptor(fd, true);  // transfer ownership
        }
        return NO_ERROR;
    }

    if (code == TRANSACTION_receiveClipboardData) {
        String8 mimeType(data.readString16());
        int32_t offerId = data.readInt32();
        ALOGVT("receiveClipboardData: mime=%s offerId=%d", mimeType.string(), offerId);
        auto* dev = WaylandDataDevice::instance();
        int fd = dev ? dev->receiveFromOffer(offerId, std::string(mimeType.string()))
                     : -1;
        reply->writeNoException();
        if (fd >= 0) {
            reply->writeFileDescriptor(fd, true);  // transfer ownership
        }
        return NO_ERROR;
    }

    if (code == TRANSACTION_releaseDataOffer) {
        int32_t offerId = data.readInt32();
        ALOGVT("releaseDataOffer: offerId=%d", offerId);
        auto* dev = WaylandDataDevice::instance();
        if (dev) {
            dev->releaseOffer(offerId);
        }
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_dragRecipientEntered) {
        ALOGVT("dragRecipientEntered");
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_dragRecipientExited) {
        ALOGVT("dragRecipientExited");
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_notifyTaskCreated) {
        int32_t taskId = data.readInt32();
        int32_t sessionId = data.readInt32();
        WaylandSessionIdResolver::registerTask(taskId, sessionId);
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_getFocusedWindowOffset) {
        reply->writeNoException();
        std::lock_guard<std::mutex> lock(sGlobalsMutex);
        if (sGlobals) {
            auto* disp = sGlobals->primaryDisplay();
            if (disp && disp->layerManager()) {
                int32_t x = 0, y = 0;
                if (disp->layerManager()->getActiveTaskWindowOffset(&x, &y)) {
                    reply->writeInt32(x);
                    reply->writeInt32(y);
                    return NO_ERROR;
                }
            }
        }
        reply->writeInt32(0);
        reply->writeInt32(0);
        return NO_ERROR;
    }

    if (code == TRANSACTION_setNotificationDisplayId) {
        int32_t displayId = data.readInt32();
        int32_t width = data.readInt32();
        int32_t height = data.readInt32();
        ALOGI("setNotificationDisplayId: %d (%dx%d)", displayId, width, height);
        std::lock_guard<std::mutex> lock(sGlobalsMutex);
        if (sGlobals) {
            sGlobals->setNotificationDisplayId(displayId, width, height);
        }
        reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_setFrameColors) {
        uint32_t active = static_cast<uint32_t>(data.readInt32());
        uint32_t inactive = static_cast<uint32_t>(data.readInt32());
        {
            std::lock_guard<std::mutex> lock(sGlobalsMutex);
            if (sGlobals) {
                sGlobals->setFrameColors(active, inactive);
            } else {
                ALOGW("setFrameColors: globals not ready, dropping "
                      "active=#%08X inactive=#%08X", active, inactive);
            }
        }
        // Sent one-way, so there is no reply to write — but the caller may
        // still be a synchronous client, and writeNoException on a live
        // parcel costs nothing.
        if (reply) reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_setTaskFrameColor) {
        int32_t taskId = data.readInt32();
        uint32_t active = static_cast<uint32_t>(data.readInt32());
        uint32_t inactive = static_cast<uint32_t>(data.readInt32());
        {
            std::lock_guard<std::mutex> lock(sGlobalsMutex);
            if (sGlobals) {
                sGlobals->setTaskFrameColor(taskId, active, inactive);
            } else {
                ALOGW("setTaskFrameColor: globals not ready, dropping task %d",
                      taskId);
            }
        }
        if (reply) reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_setTaskPipState) {
        int32_t taskId = data.readInt32();
        bool inPip = data.readInt32() != 0;
        // WM's pinned-task rectangle, in display pixels. This is where the PIP
        // window's SIZE comes from — see ArcWindowManagerService.pushTaskPipState.
        int32_t l = data.readInt32();
        int32_t t = data.readInt32();
        int32_t r = data.readInt32();
        int32_t b = data.readInt32();
        {
            std::lock_guard<std::mutex> lock(sGlobalsMutex);
            if (sGlobals) {
                sGlobals->setTaskPipState(taskId, inPip, l, t, r, b);
            } else {
                ALOGW("setTaskPipState: globals not ready, dropping task %d",
                      taskId);
            }
        }
        if (reply) reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_setAccessibilityWindowId) {
        int32_t target = data.readInt32();
        int32_t taskId = data.readInt32();
        // Java writes a null String as -1 and a present one as a length-
        // prefixed UTF-16 buffer; readString16() handles both, and a null
        // reads back empty, which is what the key-less targets want.
        String16 key16 = data.readString16();
        int32_t windowId = data.readInt32();
        std::string key(String8(key16).c_str());
        {
            std::lock_guard<std::mutex> lock(sGlobalsMutex);
            if (sGlobals) {
                sGlobals->setAccessibilityWindowId(
                        static_cast<WaylandGlobals::AccessibilityTarget>(target),
                        taskId, key, windowId);
            } else {
                ALOGW("setAccessibilityWindowId: globals not ready, dropping "
                      "target %d task %d", target, taskId);
            }
        }
        if (reply) reply->writeNoException();
        return NO_ERROR;
    }

    if (code == TRANSACTION_ackKeyboardKeyByEventTime) {
        int64_t eventTimeNanos = data.readInt64();
        bool handled = data.readInt32() != 0;
        if (auto* keyboard = WaylandKeyboardHandler::instance()) {
            keyboard->ackKey(eventTimeNanos, handled);
        }
        if (reply) reply->writeNoException();
        return NO_ERROR;
    }

    // All other methods: return success with empty reply.
    reply->writeNoException();
    return NO_ERROR;
}

} // namespace android

#pragma clang diagnostic pop

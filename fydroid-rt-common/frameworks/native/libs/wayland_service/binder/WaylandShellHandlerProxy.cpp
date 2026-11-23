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

#define LOG_TAG "WaylandShellHandlerProxy"

#include "WaylandShellHandlerProxy.h"
#include "WaylandLayoutParcel.h"
#include "../WaylandGlobals.h"

#include <binder/IBinder.h>
#include <binder/Parcel.h>
#include <log/log.h>

namespace android {

static const String16 kShellHandlerDescriptor(
        "org.chromium.arc.wayland.IWaylandShellHandler");

WaylandShellHandlerProxy* WaylandShellHandlerProxy::instance() {
    static WaylandShellHandlerProxy sInstance;
    return &sInstance;
}

void WaylandShellHandlerProxy::setHandler(const sp<IBinder>& handler) {
    std::lock_guard<std::mutex> lock(mMutex);
    mHandler = handler;
    ALOGI("setHandler: %p", handler.get());
}

void WaylandShellHandlerProxy::pushDisplayLayout(WaylandGlobals* globals) {
    sp<IBinder> handler;
    {
        std::lock_guard<std::mutex> lock(mMutex);
        handler = mHandler;
    }
    if (handler == nullptr) {
        ALOGV("pushDisplayLayout: no handler registered, dropping");
        return;
    }
    if (!globals || globals->displayCount() == 0) {
        ALOGV("pushDisplayLayout: globals not ready, dropping");
        return;
    }

    Parcel data;
    data.writeInterfaceToken(kShellHandlerDescriptor);
    if (!WaylandLayoutParcel::writeArcDisplayLayout(&data, globals)) {
        ALOGW("pushDisplayLayout: failed to build layout parcel");
        return;
    }

    ALOGI("pushDisplayLayout: layoutMode=%d",
          static_cast<int32_t>(globals->layoutMode()));
    status_t err = handler->transact(TRANSACTION_setDisplayLayout, data,
                                     /*reply=*/nullptr, IBinder::FLAG_ONEWAY);
    if (err != NO_ERROR) {
        ALOGE("pushDisplayLayout: transact failed: %d", err);
    }
}

void WaylandShellHandlerProxy::taskOcclusionChanged(int32_t taskId, float fraction,
                                                    int32_t reason) {
    sp<IBinder> handler;
    {
        std::lock_guard<std::mutex> lock(mMutex);
        handler = mHandler;
    }
    if (handler == nullptr) return;

    Parcel data;
    data.writeInterfaceToken(kShellHandlerDescriptor);
    data.writeInt32(taskId);
    data.writeFloat(fraction);
    data.writeInt32(reason);
    status_t err = handler->transact(TRANSACTION_taskOcclusionChanged, data,
                                     /*reply=*/nullptr, IBinder::FLAG_ONEWAY);
    if (err != NO_ERROR) {
        ALOGE("taskOcclusionChanged: transact failed: %d", err);
    }
}

bool WaylandShellHandlerProxy::resizeTaskWithArStep(int32_t taskId,
        int32_t x, int32_t y, int32_t w, int32_t h, int32_t displayId,
        int32_t* outAppliedX, int32_t* outAppliedY,
        int32_t* outAppliedW, int32_t* outAppliedH) {
    sp<IBinder> handler;
    {
        std::lock_guard<std::mutex> lock(mMutex);
        handler = mHandler;
    }
    if (handler == nullptr) {
        ALOGV("resizeTaskWithArStep: no handler registered");
        return false;
    }

    Parcel data, reply;
    data.writeInterfaceToken(kShellHandlerDescriptor);
    data.writeInt32(taskId);
    data.writeInt32(x);
    data.writeInt32(y);
    data.writeInt32(w);
    data.writeInt32(h);
    data.writeInt32(displayId);

    status_t err = handler->transact(TRANSACTION_resizeTaskWithArStep, data,
                                     &reply, /*flags=*/0);
    if (err != NO_ERROR) {
        ALOGE("resizeTaskWithArStep: transact failed: %d", err);
        return false;
    }
    int32_t exceptionCode = reply.readExceptionCode();
    if (exceptionCode != 0) {
        ALOGE("resizeTaskWithArStep: exception %d", exceptionCode);
        return false;
    }
    int32_t ok = reply.readInt32();
    // Stepped/applied bounds (writeable even on !ok, mirrors the requested rect).
    int32_t aX = reply.readInt32();
    int32_t aY = reply.readInt32();
    int32_t aW = reply.readInt32();
    int32_t aH = reply.readInt32();
    if (outAppliedX) *outAppliedX = aX;
    if (outAppliedY) *outAppliedY = aY;
    if (outAppliedW) *outAppliedW = aW;
    if (outAppliedH) *outAppliedH = aH;
    ALOGI("resizeTaskWithArStep: taskId=%d req=(%d,%d %dx%d) display=%d ok=%d"
          " applied=(%d,%d %dx%d)",
          taskId, x, y, w, h, displayId, ok, aX, aY, aW, aH);
    return ok != 0;
}

} // namespace android

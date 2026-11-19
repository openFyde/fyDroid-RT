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

#define LOG_TAG "WaylandServiceProxy"

#include "WaylandServiceProxy.h"

#include <binder/IServiceManager.h>
#include <binder/Parcel.h>
#include <log/log.h>

namespace android {

static const String16 kServiceName("Wayland");
static const String16 kDescriptor("org.chromium.arc.wayland.IWaylandService");

bool WaylandServiceProxy::connectToWayland() {
    sp<IServiceManager> sm = defaultServiceManager();
    mService = sm->checkService(kServiceName);
    if (mService == nullptr) {
        ALOGV("Wayland binder service not yet available");
        return false;
    }
    return true;
}

bool WaylandServiceProxy::notifyInputBridgePipeOpening(android::base::unique_fd fd) {
    if (mService == nullptr) {
        ALOGE("Not connected to Wayland service");
        return false;
    }

    Parcel data, reply;
    data.writeInterfaceToken(kDescriptor);
    data.writeUniqueFileDescriptor(fd);

    status_t err = mService->transact(TRANSACTION_notifyInputBridgePipeOpening,
                                      data, &reply);
    if (err != NO_ERROR) {
        ALOGE("transact(notifyInputBridgePipeOpening) failed: %d", err);
        return false;
    }

    ALOGI("Sent input bridge pipe fd %d to Wayland service", fd.get());
    return true;
}

bool WaylandServiceProxy::requestPointerCapture(bool enable) {
    if (mService == nullptr) {
        ALOGE("Not connected to Wayland service");
        return false;
    }

    Parcel data, reply;
    data.writeInterfaceToken(kDescriptor);
    data.writeInt32(enable ? 1 : 0);

    status_t err = mService->transact(TRANSACTION_requestPointerCapture, data,
                                      &reply, IBinder::FLAG_ONEWAY);
    if (err != NO_ERROR) {
        ALOGE("transact(requestPointerCapture) failed: %d", err);
        return false;
    }

    ALOGI("Told Wayland service to %s pointer capture", enable ? "take" : "drop");
    return true;
}

} // namespace android

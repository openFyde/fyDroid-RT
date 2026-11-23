// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

//#define LOG_NDEBUG 0
#define LOG_TAG "android.hardware.media.c2@1.0-service-mojo"

#include <C2Component.h>
#include <codec2/hidl/1.0/ComponentStore.h>
#include <hidl/HidlTransportSupport.h>
#include <log/log.h>

#include "MojoComponentStore.h"

int main(int /* argc */, char** /* argv */) {
    ALOGD("Service starting...");

    signal(SIGPIPE, SIG_IGN);

    // Extra threads may be needed to handle a stacked IPC sequence that
    // contains alternating binder and hwbinder calls. (See b/35283480.)
    android::hardware::configureRpcThreadpool(8, true /* callerWillJoin */);

    // Create IComponentStore service.
    {
        using namespace ::android::hardware::media::c2::V1_0;

        ALOGD("Instantiating Codec2's V4L2/Mojo IComponentStore service...");
        android::sp<IComponentStore> store(
                new utils::ComponentStore(android::MojoComponentStore::Create()));
        if (store == nullptr) {
            ALOGE("Cannot create Codec2's IComponentStore service.");
        } else if (store->registerAsService("default") != android::OK) {
            ALOGE("Cannot register Codec2's IComponentStore service.");
        } else {
            ALOGI("Codec2's IComponentStore service created.");
        }
    }

    android::hardware::joinRpcThreadpool();
    ALOGD("Service shutdown.");
    return 0;
}

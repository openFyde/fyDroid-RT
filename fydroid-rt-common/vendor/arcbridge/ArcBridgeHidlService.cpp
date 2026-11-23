/*
 * Copyright (C) 2026 Fyde Innovations
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

#include "ArcBridgeHidlService.h"
#include "ArcBridgeInstance.h"

#include <base/logging.h>
#include <hwbinder/IPCThreadState.h>
#include <hidl/HidlTransportSupport.h>

namespace vendor { namespace google_arc { namespace arcbridge { namespace V1_0 {
namespace implementation {

ArcBridgeService::ArcBridgeService(android::sp<arc::ArcBridgeInstance> instance)
    : instance_(std::move(instance)) {
}

ArcBridgeService::~ArcBridgeService() = default;

android::hardware::Return<void> ArcBridgeService::getMojoBootstrapChannel(
    uint32_t version,
    getMojoBootstrapChannel_cb _hidl_cb) {

    int caller_pid = android::hardware::IPCThreadState::self()->getCallingPid();

    MojoBootstrapChannel channel;
    bool ok = instance_->createMojoBootstrapChannel(version, caller_pid, &channel);
    if (!ok) {
        LOG(ERROR) << "Failed to create Mojo bootstrap channel for PID "
                   << caller_pid;
    }

    _hidl_cb(channel);
    return android::hardware::Void();
}

} // namespace implementation
}}}} // namespace vendor::google_arc::arcbridge::V1_0

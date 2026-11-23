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

#pragma once

#include <vendor/google_arc/arcbridge/1.0/IArcBridgeService.h>

namespace arc {
class ArcBridgeInstance;
}

namespace vendor { namespace google_arc { namespace arcbridge { namespace V1_0 {
namespace implementation {

class ArcBridgeService : public IArcBridgeService {
public:
    explicit ArcBridgeService(android::sp<arc::ArcBridgeInstance> instance);
    ~ArcBridgeService() override;

    // IArcBridgeService
    android::hardware::Return<void> getMojoBootstrapChannel(
        uint32_t version,
        getMojoBootstrapChannel_cb _hidl_cb) override;

private:
    android::sp<arc::ArcBridgeInstance> instance_;
};

} // namespace implementation
}}}} // namespace vendor::google_arc::arcbridge::V1_0

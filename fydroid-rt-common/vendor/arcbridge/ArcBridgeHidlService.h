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

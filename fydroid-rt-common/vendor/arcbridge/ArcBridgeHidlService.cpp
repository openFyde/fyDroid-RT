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

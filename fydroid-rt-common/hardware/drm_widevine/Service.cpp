// android.hardware.drm-service.widevine — fydroid's Widevine DRM HAL.
//
// A thin forwarding HAL: all key exchange and decryption happen in Chrome's
// library CDM, reached over the ARC mojo bridge. See
// device/fydeos/docs/widevine-cdm-bridge-plan.md.
#define LOG_TAG "widevine-main"

// CdmBridge.h must come before the NDK binder headers: it clears the <syslog.h>
// LOG_* macros that would otherwise break libchrome's base/logging.h.
#include "CdmBridge.h"

#include <base/at_exit.h>
#include <base/command_line.h>
#include <base/logging.h>

#include <android/binder_manager.h>
#include <android/binder_process.h>
#include <utils/Log.h>

#include "DrmFactory.h"

using ::fydroid::widevine::CdmBridge;
using ::fydroid::widevine::DrmFactory;

int main(int /*argc*/, char* /*argv*/[]) {
    base::AtExitManager at_exit;
    base::CommandLine::Init(0, nullptr);
    logging::LoggingSettings settings;
    settings.logging_dest = logging::LOG_TO_SYSTEM_DEBUG_LOG;
    logging::InitLogging(settings);

    ABinderProcess_setThreadPoolMaxThreadCount(8);

    // Start the mojo bootstrap first, but do not wait on it: the HAL is brought
    // up by `class hal` and the ARC bridge may not exist for a while. Key
    // operations that arrive early return ERROR_DRM_RESOURCE_BUSY.
    CdmBridge::get()->start();

    std::shared_ptr<DrmFactory> factory = ::ndk::SharedRefBase::make<DrmFactory>();
    const std::string instance = std::string(DrmFactory::descriptor) + "/widevine";
    binder_status_t status =
            AServiceManager_addService(factory->asBinder().get(), instance.c_str());
    if (status != STATUS_OK) {
        ALOGE("failed to register %s: %d", instance.c_str(), status);
        return 1;
    }
    ALOGI("registered %s", instance.c_str());

    ABinderProcess_joinThreadPool();
    return EXIT_FAILURE;  // not reached
}

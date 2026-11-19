#include "CameraHalClient.h"

#include <base/logging.h>

namespace fydroid { namespace camera {

CameraHalClient::~CameraHalClient() {
    if (binding_.is_bound()) {
        binding_.Close();
    }
}

mojo::InterfacePtr<cros::mojom::CameraHalClient>
CameraHalClient::BindOnMojoThread() {
    mojo::InterfacePtr<cros::mojom::CameraHalClient> ptr;
    binding_.Bind(mojo::MakeRequest(&ptr));
    return ptr;
}

void CameraHalClient::SetUpChannel(
        mojo::InterfacePtr<cros::mojom::CameraModule> camera_module) {
    LOG(INFO) << "CameraHalClient::SetUpChannel - CameraModule received";
    RechannelHandler handler;
    {
        std::lock_guard<std::mutex> lock(mutex_);
        if (module_ready_ && rechannel_handler_) {
            handler = rechannel_handler_;
        } else {
            module_ = std::move(camera_module);
            module_ready_ = true;
        }
    }
    if (handler) {
        handler(std::move(camera_module));
        return;
    }
    cv_.notify_all();
}

void CameraHalClient::SetRechannelHandler(RechannelHandler handler) {
    std::lock_guard<std::mutex> lock(mutex_);
    rechannel_handler_ = std::move(handler);
}

bool CameraHalClient::WaitForModule(int timeout_seconds) {
    std::unique_lock<std::mutex> lock(mutex_);
    return cv_.wait_for(lock,
                        std::chrono::seconds(timeout_seconds),
                        [this] { return module_ready_; });
}

}} // namespace fydroid::camera

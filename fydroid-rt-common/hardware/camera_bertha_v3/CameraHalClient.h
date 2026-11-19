#pragma once

#include <functional>
#include <mutex>
#include <condition_variable>

#include <mojo/public/cpp/bindings/binding.h>
#include <arc/MojoProcessSupport.h>

#include <media/capture/video/chromeos/mojom/cros_camera_client.mojom.h>
#include <camera/mojo/camera_common.mojom.h>

namespace fydroid { namespace camera {

// Implements cros::mojom::CameraHalClient.
//
// Chrome OS calls SetUpChannel() to hand us a CameraModule proxy after we
// register via CameraHost::RegisterCameraHalClient. We block in
// WaitForModule() until the module is available.
class CameraHalClient : public cros::mojom::CameraHalClient {
 public:
  CameraHalClient() = default;
  ~CameraHalClient() override;

  // Build an InterfacePtr<CameraHalClient> bound to this object.
  // Must be called on the Mojo thread.
  mojo::InterfacePtr<cros::mojom::CameraHalClient> BindOnMojoThread();

  // Block until SetUpChannel() has been called. Returns false on timeout.
  bool WaitForModule(int timeout_seconds = 10);

  // Returns the CameraModule proxy. Valid after WaitForModule() returns true.
  cros::mojom::CameraModule* module() { return module_.get(); }
  mojo::InterfacePtr<cros::mojom::CameraModule>& module_ptr() { return module_; }

  // Called on the Mojo thread with each CameraModule after the first one,
  // i.e. when Chrome re-runs SetUpChannel after cros_camera_service restarts.
  using RechannelHandler =
      std::function<void(mojo::InterfacePtr<cros::mojom::CameraModule>)>;
  void SetRechannelHandler(RechannelHandler handler);

  // cros::mojom::CameraHalClient override.
  void SetUpChannel(
      mojo::InterfacePtr<cros::mojom::CameraModule> camera_module) override;

 private:
  mojo::Binding<cros::mojom::CameraHalClient> binding_{this};

  mojo::InterfacePtr<cros::mojom::CameraModule> module_;

  std::mutex mutex_;
  std::condition_variable cv_;
  bool module_ready_ = false;
  RechannelHandler rechannel_handler_;
};

}} // namespace fydroid::camera

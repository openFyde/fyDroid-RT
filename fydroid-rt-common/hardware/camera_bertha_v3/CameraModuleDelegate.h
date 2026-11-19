#pragma once

#include <map>
#include <mutex>
#include <set>
#include <memory>

#include <hardware/camera_common.h>
#include <system/camera_metadata.h>

#include <mojo/public/cpp/bindings/binding.h>
#include <base/synchronization/waitable_event.h>
#include <base/task_runner.h>

#include <arc/MojoProcessSupport.h>
#include <camera/mojo/camera_common.mojom.h>
#include <camera/mojo/camera3.mojom.h>

#include "PipeHealth.h"

namespace fydroid { namespace camera {

class Camera3DeviceOpsDelegate;
class CameraModuleCallbacksImpl;

// Wraps cros::mojom::CameraModuleProxy.
//
// All Mojo calls are posted to the Mojo thread and waited on synchronously,
// so callers on the camera framework thread block until Chrome OS responds.
class CameraModuleDelegate {
 public:
  CameraModuleDelegate(arc::MojoProcessSupport* mojo_support,
                       mojo::InterfacePtr<cros::mojom::CameraModule> module);
  ~CameraModuleDelegate() = default;

  int GetNumberOfCameras();
  int GetCameraInfo(int camera_id, camera_info* info);
  int SetTorchMode(int camera_id, bool enabled);
  // Opens a camera device. On success, returns 0 and sets *delegate to a new
  // Camera3DeviceOpsDelegate (caller takes ownership).
  int OpenDevice(int camera_id, Camera3DeviceOpsDelegate** delegate);

  // Registers |callbacks| with Chrome OS over the CameraModule pipe via
  // SetCallbacksAssociated, so hot-plug (external camera) status changes are
  // delivered. Fire-and-forget: posts to the Mojo thread and returns; the
  // framework does not need to block on registration.
  void RegisterCallbacksAssociated(CameraModuleCallbacksImpl* callbacks);

  // Mojo thread only. Swap in the CameraModule Chrome hands over after
  // cros_camera_service restarts, and make the delegate usable again.
  void Rebind(mojo::InterfacePtr<cros::mojom::CameraModule> module);

  // Returns the CameraModuleProxy (for GetVendorTagOps etc.).
  cros::mojom::CameraModule* proxy() { return module_.get(); }

 private:
  // Posted to the Mojo thread at construction; see Camera3DeviceOpsDelegate.
  void InstallErrorHandler();
  static void InstallErrorHandlerOnMojoThread(
          mojo::InterfacePtr<cros::mojom::CameraModule>* module,
          std::shared_ptr<PipeHealth> health);

  arc::MojoProcessSupport* mojo_support_;
  mojo::InterfacePtr<cros::mojom::CameraModule> module_;
  // Separate from each device's pipe on purpose: a wedged capture session must
  // not stop the module from enumerating cameras or opening a fresh one.
  std::shared_ptr<PipeHealth> pipe_ = std::make_shared<PipeHealth>();

  // Each camera's ANDROID_REQUEST_AVAILABLE_REQUEST_KEYS, recorded by
  // GetCameraInfo (which the framework always calls before OpenDevice) and
  // handed to the device it opens; see Camera3DeviceOpsDelegate.
  std::mutex request_keys_lock_;
  std::map<int, std::set<uint32_t>> request_keys_;
};

}} // namespace fydroid::camera

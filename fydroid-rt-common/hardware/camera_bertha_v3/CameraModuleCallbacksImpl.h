#pragma once

#include <base/callback.h>
#include <mojo/public/cpp/bindings/associated_binding.h>

#include <camera/mojo/camera_common.mojom.h>

namespace fydroid { namespace camera {

// Implements cros::mojom::CameraModuleCallbacks.
//
// This is the channel Chrome OS uses to announce hot-pluggable (external)
// cameras. An external camera's ID is >= the built-in count returned by
// GetNumberOfCameras, so it never appears through the static enumeration path —
// it exists ONLY as a CameraDeviceStatusChange notification. Without a receiver
// bound here, Chrome's "camera N is present" event has nowhere to land and the
// ARC camera framework never learns the external camera exists (which is why a
// USB/UVC webcam is invisible to Android while Chrome sees it fine).
//
// Chrome invokes these methods on the Mojo thread; the forwarding callbacks run
// there and hand off to the Android camera_module_callbacks_t.
class CameraModuleCallbacksImpl : public cros::mojom::CameraModuleCallbacks {
 public:
  // (camera_id, new_status). new_status is the raw enum value, which is
  // numerically identical between the cros::mojom and Android HAL enums
  // (NOT_PRESENT/PRESENT/ENUMERATING = 0/1/2, torch statuses likewise).
  using StatusCallback = base::RepeatingCallback<void(int, int)>;

  CameraModuleCallbacksImpl(StatusCallback device_status_cb,
                            StatusCallback torch_status_cb);
  ~CameraModuleCallbacksImpl() override;

  // Makes a fresh associated pair, binds this object to the receiver end, and
  // returns the remote end to hand to Chrome via
  // CameraModule::SetCallbacksAssociated. Must be called on the Mojo thread.
  cros::mojom::CameraModuleCallbacksAssociatedPtrInfo BindOnMojoThread();

  // cros::mojom::CameraModuleCallbacks overrides (invoked on the Mojo thread).
  void CameraDeviceStatusChange(
      int32_t camera_id,
      cros::mojom::CameraDeviceStatus new_status) override;
  void TorchModeStatusChange(
      int32_t camera_id,
      cros::mojom::TorchModeStatus new_status) override;

 private:
  StatusCallback device_status_cb_;
  StatusCallback torch_status_cb_;
  mojo::AssociatedBinding<cros::mojom::CameraModuleCallbacks> binding_{this};
};

}} // namespace fydroid::camera

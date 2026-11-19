#pragma once

#include <hardware/camera3.h>
#include <map>
#include <mojo/public/cpp/bindings/binding.h>
#include <camera/mojo/camera3.mojom.h>
#include "BufferAdapter.h"

namespace fydroid { namespace camera {

// Implements cros::mojom::Camera3CallbackOps.
//
// Chrome calls ProcessCaptureResult() / Notify() on the Mojo thread.
// We translate the Mojo structs to camera3_* C structs and forward them to
// the camera3_callback_ops_t provided by the Android camera framework.
class Camera3CallbackOpsImpl : public cros::mojom::Camera3CallbackOps {
 public:
  // stream_id_to_ptr: reverse map from Chrome stream_id → camera3_stream_t*.
  // Must remain valid for the lifetime of this object (owned by
  // Camera3DeviceOpsDelegate and rebuilt in-place on ConfigureStreams).
  Camera3CallbackOpsImpl(const camera3_callback_ops_t* fw_callbacks,
                         BufferAdapter* buf_adapter,
                         const std::map<uint64_t, camera3_stream_t*>* stream_id_to_ptr);
  ~Camera3CallbackOpsImpl() override;

  // Bind and return an InterfacePtr. Must be called on the Mojo thread.
  mojo::InterfacePtr<cros::mojom::Camera3CallbackOps> BindOnMojoThread();

  // cros::mojom::Camera3CallbackOps overrides.
  void ProcessCaptureResult(cros::mojom::Camera3CaptureResultPtr result) override;
  void Notify(cros::mojom::Camera3NotifyMsgPtr msg) override;
  void RequestStreamBuffers(
      std::vector<cros::mojom::Camera3BufferRequestPtr> buffer_reqs,
      const RequestStreamBuffersCallback& callback) override;
  void ReturnStreamBuffers(
      std::vector<cros::mojom::Camera3StreamBufferPtr> buffers) override;

 private:
  const camera3_callback_ops_t* fw_callbacks_;
  BufferAdapter* buf_adapter_;
  const std::map<uint64_t, camera3_stream_t*>* stream_id_to_ptr_;
  mojo::Binding<cros::mojom::Camera3CallbackOps> binding_{this};
};

}} // namespace fydroid::camera

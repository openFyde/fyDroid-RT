#pragma once

#include <hardware/camera3.h>
#include <memory>
#include <set>

#include <base/synchronization/waitable_event.h>
#include <mojo/public/cpp/bindings/binding.h>
#include <arc/MojoProcessSupport.h>
#include <camera/mojo/camera3.mojom.h>

#include "Camera3CallbackOpsImpl.h"
#include "BufferAdapter.h"
#include "PipeHealth.h"

namespace fydroid { namespace camera {

// The device pipe is shared, not owned outright, because tasks queued on the
// Mojo thread outlive the delegate.
//
// It used to be safe to hand tasks a bare `&ops_`: every call blocked on an
// untimed WaitableEvent, so the task had provably already run by the time the
// caller returned, and nothing could still reference the member at delete time.
// Timeouts break that invariant — a call can now return (and the framework can
// close and delete the device) while its task is still queued. Holding the
// InterfacePtr by shared_ptr keeps it alive for whatever is still pending.
using DeviceOpsPtr =
        std::shared_ptr<mojo::InterfacePtr<cros::mojom::Camera3DeviceOps>>;

// Per-device delegate wrapping cros::mojom::Camera3DeviceOpsProxy.
//
// Presents a camera3_device_t to the Android camera framework.
// All Mojo calls are posted to the Mojo thread and waited on.
//
// Chrome fills Android's DMA-BUF buffers directly (zero-copy).
// We send Android's buffer handle in ProcessCaptureRequest, and Chrome
// imports and writes to it. ProcessCaptureResult returns the original
// buffer pointer for Camera3OutputStream's container_of recovery.
class Camera3DeviceOpsDelegate {
 public:
  // request_keys: the camera's ANDROID_REQUEST_AVAILABLE_REQUEST_KEYS; empty
  // when unknown, in which case requests are passed through unfiltered.
  Camera3DeviceOpsDelegate(arc::MojoProcessSupport* mojo_support,
                            mojo::InterfacePtr<cros::mojom::Camera3DeviceOps> ops,
                            std::set<uint32_t> request_keys);
  ~Camera3DeviceOpsDelegate();

  // Returns the camera3_device_t to hand to the framework.
  camera3_device_t* GetCamera3Device() { return &device_; }

  // camera3_device_ops_t implementations (called via C thunks in CameraHal).
  int Initialize(const camera3_callback_ops_t* callbacks);
  int ConfigureStreams(camera3_stream_configuration_t* stream_list);
  const camera_metadata_t* ConstructDefaultRequestSettings(int type);
  int ProcessCaptureRequest(camera3_capture_request_t* request);
  void Dump(int fd);
  int Flush();
  int Close();

 private:
  // Posted to the Mojo thread at construction: watches the device pipe so a
  // dropped connection marks it dead immediately, instead of every later call
  // paying a full timeout before finding out.
  void InstallErrorHandler();

  arc::MojoProcessSupport* mojo_support_;
  DeviceOpsPtr ops_;
  std::shared_ptr<PipeHealth> pipe_ = std::make_shared<PipeHealth>();

  camera3_device_t device_;

  // shared_ptr for the same reason as ops_ — Initialize's task binds this on
  // the Mojo thread and may still be queued when the delegate goes away.
  std::shared_ptr<Camera3CallbackOpsImpl> callback_ops_impl_;
  std::unique_ptr<BufferAdapter> buf_adapter_;

  // Maps stream pointer → stream_id assigned by Chrome, and reverse.
  // Both are rebuilt on every ConfigureStreams call.
  std::map<camera3_stream_t*, uint64_t> stream_ids_;
  std::map<uint64_t, camera3_stream_t*> reverse_stream_ids_;
  uint64_t next_stream_id_ = 1;

  // Cached default request settings, indexed by template type.
  std::map<int, camera_metadata_t*> default_settings_cache_;
  // The last non-null request settings, resent when the framework passes null
  // ("same settings as before"); see ProcessCaptureRequest.
  camera_metadata_t* last_request_settings_ = nullptr;

  // Request settings are cut down to the keys the HAL declared it accepts.
  // The Android 17 framework adds request keys newer than the Chrome OS
  // camera metadata tables; Chrome's DeserializeCameraMetadata() fails the
  // whole request on a tag it has no type for ("Inconsistent tag type"), and
  // the USB HAL then aborts on settings with no AE target fps range.
  const std::set<uint32_t> request_keys_;
  bool logged_dropped_keys_ = false;
};

}} // namespace fydroid::camera

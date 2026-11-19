#pragma once

#include <map>
#include <mutex>
#include <hardware/camera3.h>
#include <hardware/gralloc.h>
#include <camera/mojo/camera3.mojom.h>

namespace fydroid { namespace camera {

// Translates between Android buffer_handle_t and cros::mojom::CameraBufferHandle.
//
// Chrome OS identifies buffers by a uint64 buffer_id cookie.  We maintain a
// map from cookie → buffer_handle_t so that incoming results can look up the
// original Android handle.
class BufferAdapter {
 public:
  BufferAdapter();
  ~BufferAdapter();

  // Register an Android buffer handle and return its cookie.
  // |buffer_ptr| is the original &(anb->handle) from camera3_stream_buffer_t.
  // Subsequent calls with the same handle return the same cookie.
  uint64_t RegisterBuffer(buffer_handle_t handle, const buffer_handle_t* buffer_ptr);

  // Look up the original buffer_handle_t* (pointer into ANativeWindowBuffer).
  // Camera3OutputStream uses container_of(buffer.buffer, ANativeWindowBuffer, handle)
  // so we MUST return the original pointer, not a copy.
  const buffer_handle_t* GetBufferPtr(uint64_t buffer_id);

  // Remove a buffer from the tracking map (call when stream is torn down).
  void UnregisterBuffer(buffer_handle_t handle);

  // Convert a camera3_stream_buffer_t to a CameraBufferHandle mojom struct.
  // Extracts DMA-BUF fds from the gralloc handle via gralloc0 perform ops.
  // Returns nullptr on failure.
  cros::mojom::CameraBufferHandlePtr BuildBufferHandle(
      const camera3_stream_buffer_t& sb);

 private:
  uint64_t next_cookie_ = 1;
  std::mutex mutex_;
  std::map<buffer_handle_t, uint64_t> handle_to_cookie_;
  std::map<uint64_t, const buffer_handle_t*> cookie_to_buffer_ptr_;

  const hw_module_t* gralloc_module_ = nullptr;
};

}} // namespace fydroid::camera

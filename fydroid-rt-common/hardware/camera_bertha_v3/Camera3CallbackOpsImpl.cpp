#include "MetadataSize.h"
#include "Camera3CallbackOpsImpl.h"

#include <base/logging.h>
#include <unistd.h>
#include <system/camera_metadata.h>
#include <mojo/public/cpp/system/platform_handle.h>

#define LOG_TAG "CameraBerthaV3"
#include <utils/Log.h>

namespace fydroid { namespace camera {

Camera3CallbackOpsImpl::Camera3CallbackOpsImpl(
        const camera3_callback_ops_t* fw_callbacks,
        BufferAdapter* buf_adapter,
        const std::map<uint64_t, camera3_stream_t*>* stream_id_to_ptr)
        : fw_callbacks_(fw_callbacks),
          buf_adapter_(buf_adapter),
          stream_id_to_ptr_(stream_id_to_ptr) {
}

Camera3CallbackOpsImpl::~Camera3CallbackOpsImpl() {
    if (binding_.is_bound()) {
        binding_.Close();
    }
}

mojo::InterfacePtr<cros::mojom::Camera3CallbackOps>
Camera3CallbackOpsImpl::BindOnMojoThread() {
    mojo::InterfacePtr<cros::mojom::Camera3CallbackOps> ptr;
    binding_.Bind(mojo::MakeRequest(&ptr));
    return ptr;
}

// ---------------------------------------------------------------------------
// ProcessCaptureResult
// ---------------------------------------------------------------------------

void Camera3CallbackOpsImpl::ProcessCaptureResult(
        cros::mojom::Camera3CaptureResultPtr result) {
    if (!fw_callbacks_) return;

    camera3_capture_result_t r = {};
    r.frame_number = result->frame_number;
    r.partial_result = result->partial_result;

    // Translate result metadata (if present).
    camera_metadata_t* result_meta = nullptr;
    const auto& meta = result->result;
    if (meta && meta->entries && !meta->entries->empty()) {
        size_t num_entries = meta->entries->size();
        result_meta = allocate_camera_metadata(num_entries,
                MetadataDataCapacity(*meta->entries));
        if (result_meta) {
            for (const auto& e : *meta->entries) {
                if (add_camera_metadata_entry(result_meta,
                        static_cast<uint32_t>(e->tag),
                        e->data.empty() ? nullptr : e->data.data(),
                        static_cast<size_t>(e->count)) != 0) {
                    ALOGE("result metadata: dropped tag 0x%x",
                          static_cast<uint32_t>(e->tag));
                }
            }
        }
    }
    r.result = result_meta;

    // Translate output buffers.
    // Return the ORIGINAL buffer_handle_t* (&anb->handle) from the request.
    // Camera3OutputStream uses container_of(buffer.buffer, ANativeWindowBuffer, handle)
    // to recover the ANativeWindowBuffer and queue it back to the BufferQueue.
    std::vector<camera3_stream_buffer_t> out_bufs;
    if (result->output_buffers) {
        const size_t n = result->output_buffers->size();
        out_bufs.reserve(n);
        for (const auto& sb : *result->output_buffers) {
            ALOGV("ProcessCaptureResult: frame=%u stream_id=%llu buffer_id=%llu status=%d",
                  result->frame_number,
                  (unsigned long long)sb->stream_id,
                  (unsigned long long)sb->buffer_id,
                  (int)sb->status);

            // Get the ORIGINAL &(anb->handle) pointer stored during RegisterBuffer.
            const buffer_handle_t* buf_ptr = buf_adapter_->GetBufferPtr(sb->buffer_id);
            if (!buf_ptr) {
                ALOGE("ProcessCaptureResult: buffer_id=%llu NOT FOUND!",
                      (unsigned long long)sb->buffer_id);
            }

            camera3_stream_buffer_t b = {};
            b.buffer = const_cast<buffer_handle_t*>(buf_ptr);
            if (stream_id_to_ptr_) {
                auto it = stream_id_to_ptr_->find(sb->stream_id);
                if (it != stream_id_to_ptr_->end()) {
                    b.stream = it->second;
                } else {
                    ALOGE("ProcessCaptureResult: stream_id=%llu NOT FOUND!",
                          (unsigned long long)sb->stream_id);
                }
            }
            b.status = static_cast<int>(sb->status);
            if (sb->release_fence.is_valid()) {
                auto ph = mojo::UnwrapPlatformHandle(std::move(sb->release_fence));
                b.release_fence = ph.TakeFD().release();
            } else {
                b.release_fence = -1;
            }
            b.acquire_fence = -1;
            out_bufs.push_back(b);
        }
    }
    r.output_buffers = out_bufs.empty() ? nullptr : out_bufs.data();
    r.num_output_buffers = out_bufs.size();

    fw_callbacks_->process_capture_result(fw_callbacks_, &r);

    if (result_meta) {
        free_camera_metadata(result_meta);
    }
    for (auto& b : out_bufs) {
        if (b.release_fence >= 0) {
            close(b.release_fence);
        }
    }
}

// ---------------------------------------------------------------------------
// Notify
// ---------------------------------------------------------------------------

void Camera3CallbackOpsImpl::Notify(cros::mojom::Camera3NotifyMsgPtr msg) {
    if (!fw_callbacks_) return;

    camera3_notify_msg_t m = {};
    m.type = static_cast<int>(msg->type);

    switch (msg->type) {
        case cros::mojom::Camera3MsgType::CAMERA3_MSG_ERROR: {
            auto& e = msg->message->get_error();
            m.message.error.frame_number = e->frame_number;
            m.message.error.error_code = static_cast<int>(e->error_code);
            m.message.error.error_stream = nullptr;
            break;
        }
        case cros::mojom::Camera3MsgType::CAMERA3_MSG_SHUTTER: {
            auto& s = msg->message->get_shutter();
            m.message.shutter.frame_number = s->frame_number;
            m.message.shutter.timestamp = s->timestamp;
            break;
        }
        default:
            break;
    }

    fw_callbacks_->notify(fw_callbacks_, &m);
}

// ---------------------------------------------------------------------------
// RequestStreamBuffers / ReturnStreamBuffers (MinVersion=5, stub)
// ---------------------------------------------------------------------------

void Camera3CallbackOpsImpl::RequestStreamBuffers(
        std::vector<cros::mojom::Camera3BufferRequestPtr> buffer_reqs,
        const RequestStreamBuffersCallback& callback) {
    ALOGW("RequestStreamBuffers: not implemented (num_requests=%zu)", buffer_reqs.size());
    callback.Run(cros::mojom::Camera3BufferRequestStatus::CAMERA3_BUF_REQ_FAILED_UNKNOWN,
                 {});
}

void Camera3CallbackOpsImpl::ReturnStreamBuffers(
        std::vector<cros::mojom::Camera3StreamBufferPtr> buffers) {
    ALOGW("ReturnStreamBuffers: not implemented (num_buffers=%zu)", buffers.size());
}

}} // namespace fydroid::camera

#include "MetadataSize.h"
#include "Camera3DeviceOpsDelegate.h"
#include "MojoCallWaiter.h"

#include <string>
#include <errno.h>

#include <base/bind.h>
#include <base/containers/flat_map.h>
#include <base/files/scoped_file.h>
#include <base/logging.h>
#include <base/synchronization/waitable_event.h>
#include <system/camera_metadata.h>
#include <system/graphics.h>
#include <mojo/public/cpp/system/platform_handle.h>

#define LOG_TAG "CameraBerthaV3"
#include <utils/Log.h>

// Forward declaration of the C-thunk function defined in CameraHal.cpp.
// The camera3_device_t::ops pointer must point to a valid camera3_device_ops_t
// whose function pointers are C functions that dispatch to the delegate.
extern "C" {
static int camera3_initialize(const camera3_device_t* dev,
                               const camera3_callback_ops_t* ops);
static int camera3_configure_streams(const camera3_device_t* dev,
                                      camera3_stream_configuration_t* stream_list);
static const camera_metadata_t* camera3_construct_default_request_settings(
        const camera3_device_t* dev, int type);
static int camera3_process_capture_request(const camera3_device_t* dev,
                                            camera3_capture_request_t* request);
static void camera3_dump(const camera3_device_t* dev, int fd);
static int camera3_flush(const camera3_device_t* dev);
static int camera3_close(hw_device_t* dev);
}  // extern "C"

static camera3_device_ops_t g_camera3_ops = {
    .initialize                = camera3_initialize,
    .configure_streams         = camera3_configure_streams,
    .register_stream_buffers   = nullptr,  // deprecated
    .construct_default_request_settings = camera3_construct_default_request_settings,
    .process_capture_request   = camera3_process_capture_request,
    .get_metadata_vendor_tag_ops = nullptr,  // deprecated
    .dump                      = camera3_dump,
    .flush                     = camera3_flush,
    .reserved = {nullptr},
};

namespace fydroid { namespace camera {

Camera3DeviceOpsDelegate::Camera3DeviceOpsDelegate(
        arc::MojoProcessSupport* mojo_support,
        mojo::InterfacePtr<cros::mojom::Camera3DeviceOps> ops,
        std::set<uint32_t> request_keys)
        : mojo_support_(mojo_support),
          ops_(std::make_shared<
                  mojo::InterfacePtr<cros::mojom::Camera3DeviceOps>>(
                          std::move(ops))),
          buf_adapter_(std::make_unique<BufferAdapter>()),
          request_keys_(std::move(request_keys)) {
    memset(&device_, 0, sizeof(device_));
    device_.common.tag = HARDWARE_DEVICE_TAG;
    device_.common.version = CAMERA_DEVICE_API_VERSION_3_6;
    device_.common.close = camera3_close;
    device_.ops = &g_camera3_ops;
    device_.priv = this;  // back-pointer for C thunks

    InstallErrorHandler();
}

void Camera3DeviceOpsDelegate::InstallErrorHandler() {
    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              std::shared_ptr<PipeHealth> health) {
                ops->set_connection_error_handler(base::BindOnce(
                        [](std::shared_ptr<PipeHealth> health) {
                            if (health->MarkDead()) {
                                ALOGE("Camera3DeviceOps pipe disconnected — "
                                      "failing further calls fast");
                            }
                        }, health));
            }, ops_, pipe_));
}

Camera3DeviceOpsDelegate::~Camera3DeviceOpsDelegate() {
    for (auto& [type, meta] : default_settings_cache_) {
        if (meta) free_camera_metadata(meta);
    }
    if (last_request_settings_) free_camera_metadata(last_request_settings_);

    // ops_ and the callback binding are sequence-affine: both were bound on the
    // Mojo thread, but camera3_close() deletes this object from a binder
    // thread. Destroying them here would tear them down off-sequence and drop
    // any in-flight response callbacks on the floor — the exact way a caller
    // ends up blocked on a reply that can never arrive. Hand both to the Mojo
    // thread and let them die there.
    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr,
                              std::shared_ptr<Camera3CallbackOpsImpl>) {},
                           std::move(ops_), std::move(callback_ops_impl_)));
}

// ---------------------------------------------------------------------------
// Initialize
// ---------------------------------------------------------------------------

int Camera3DeviceOpsDelegate::Initialize(const camera3_callback_ops_t* callbacks) {
    if (!pipe_->ok()) return -ENODEV;

    callback_ops_impl_ = std::make_shared<Camera3CallbackOpsImpl>(callbacks,
                                                                    buf_adapter_.get(),
                                                                    &reverse_stream_ids_);

    auto pending = MakePendingReply<int32_t>();
    std::shared_ptr<Camera3CallbackOpsImpl> impl = callback_ops_impl_;

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              std::shared_ptr<Camera3CallbackOpsImpl> impl,
                              std::shared_ptr<PendingReply<int32_t>> p) {
                mojo::InterfacePtr<cros::mojom::Camera3CallbackOps> cb_ptr =
                        impl->BindOnMojoThread();
                (*ops)->Initialize(std::move(cb_ptr),
                        base::BindRepeating(
                                [](std::shared_ptr<PendingReply<int32_t>> p,
                                   int32_t r) {
                                    p->value = r;
                                    p->event.Signal();
                                }, p));
            }, ops_, impl, pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "Camera3DeviceOps::Initialize")) {
        return -ENODEV;
    }
    return pending->value;
}

// ---------------------------------------------------------------------------
// ConfigureStreams
// ---------------------------------------------------------------------------

int Camera3DeviceOpsDelegate::ConfigureStreams(
        camera3_stream_configuration_t* stream_list) {
    ALOGI("ConfigureStreams: num_streams=%u operation_mode=%u",
          stream_list->num_streams, stream_list->operation_mode);

    auto config = cros::mojom::Camera3StreamConfiguration::New();
    config->operation_mode = static_cast<cros::mojom::Camera3StreamConfigurationMode>(
            stream_list->operation_mode);

    stream_ids_.clear();
    reverse_stream_ids_.clear();
    for (uint32_t i = 0; i < stream_list->num_streams; i++) {
        camera3_stream_t* s = stream_list->streams[i];
        uint64_t sid = next_stream_id_++;
        stream_ids_[s] = sid;
        reverse_stream_ids_[sid] = s;

        ALOGI("  stream[%u]: type=%d %ux%u fmt=0x%x usage=0x%x max_buf=%u ds=%d rot=%d",
              i, s->stream_type, s->width, s->height, s->format,
              (unsigned)s->usage, s->max_buffers, s->data_space, s->rotation);

        auto ms = cros::mojom::Camera3Stream::New();
        ms->id = sid;
        ms->stream_type = static_cast<cros::mojom::Camera3StreamType>(s->stream_type);
        ms->width = s->width;
        ms->height = s->height;
        ms->format = static_cast<cros::mojom::HalPixelFormat>(s->format);
        ms->usage = s->usage;
        ms->max_buffers = s->max_buffers;
        ms->data_space = s->data_space;
        ms->rotation = static_cast<cros::mojom::Camera3StreamRotation>(s->rotation);
        // Chrome DCHECKs that physical_camera_id has_value(). Empty string means
        // this is not a physical sub-stream of a logical camera.
        ms->physical_camera_id = std::string("");
        config->streams.push_back(std::move(ms));
    }

    struct MojoResult {
        int32_t status = -1;
        cros::mojom::Camera3StreamConfigurationPtr updated_config;
    };

    if (!pipe_->ok()) return -ENODEV;

    auto pending = MakePendingReply<MojoResult>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              cros::mojom::Camera3StreamConfigurationPtr cfg,
                              std::shared_ptr<PendingReply<MojoResult>> p) {
                (*ops)->ConfigureStreamsAndGetAllocatedBuffers(std::move(cfg),
                        base::BindRepeating(
                                [](std::shared_ptr<PendingReply<MojoResult>> p,
                                   int32_t status,
                                   cros::mojom::Camera3StreamConfigurationPtr uc,
                                   base::flat_map<uint64_t, std::vector<cros::mojom::Camera3StreamBufferPtr>> /*ab*/) {
                                    p->value.status = status;
                                    p->value.updated_config = std::move(uc);
                                    p->event.Signal();
                                }, p));
            }, ops_, std::move(config), pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "Camera3DeviceOps::ConfigureStreams", kMojoOpenTimeoutMs)) {
        return -ENODEV;
    }
    const MojoResult& r = pending->value;

    ALOGI("ConfigureStreamsAndGetAllocatedBuffers: status=%d updated_config=%s",
          r.status, r.updated_config ? "yes" : "null");

    if (r.status == 0 && r.updated_config) {
        for (const auto& ms : r.updated_config->streams) {
            for (auto& [ptr, sid] : stream_ids_) {
                if (sid == ms->id) {
                    ptr->max_buffers = ms->max_buffers;
                    ptr->usage |= ms->usage;
                    ALOGI("  updated stream id=%llu max_buf=%u usage=0x%x fmt=0x%x",
                          (unsigned long long)ms->id, ms->max_buffers,
                          (unsigned)ms->usage, ptr->format);
                    break;
                }
            }
        }
    }
    return r.status;
}

// ---------------------------------------------------------------------------
// ConstructDefaultRequestSettings
// ---------------------------------------------------------------------------

const camera_metadata_t* Camera3DeviceOpsDelegate::ConstructDefaultRequestSettings(
        int type) {
    auto it = default_settings_cache_.find(type);
    if (it != default_settings_cache_.end()) {
        return it->second;
    }

    if (!pipe_->ok()) return nullptr;

    auto pending = MakePendingReply<cros::mojom::CameraMetadataPtr>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              cros::mojom::Camera3RequestTemplate tmpl,
                              std::shared_ptr<PendingReply<cros::mojom::CameraMetadataPtr>> p) {
                (*ops)->ConstructDefaultRequestSettings(tmpl,
                        base::BindRepeating(
                                [](std::shared_ptr<PendingReply<cros::mojom::CameraMetadataPtr>> p,
                                   cros::mojom::CameraMetadataPtr m) {
                                    p->value = std::move(m);
                                    p->event.Signal();
                                }, p));
            }, ops_,
            static_cast<cros::mojom::Camera3RequestTemplate>(type),
            pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "Camera3DeviceOps::ConstructDefaultRequestSettings")) {
        return nullptr;
    }
    const cros::mojom::CameraMetadataPtr& meta = pending->value;

    if (!meta || !meta->entries || meta->entries->empty()) {
        default_settings_cache_[type] = nullptr;
        return nullptr;
    }

    size_t num_entries = meta->entries->size();
    camera_metadata_t* m = allocate_camera_metadata(num_entries,
            MetadataDataCapacity(*meta->entries));
    if (m) {
        for (const auto& e : *meta->entries) {
            if (add_camera_metadata_entry(m,
                    static_cast<uint32_t>(e->tag),
                    e->data.empty() ? nullptr : e->data.data(),
                    static_cast<size_t>(e->count)) != 0) {
                ALOGE("default request settings (template %d): dropped tag 0x%x",
                      type, static_cast<uint32_t>(e->tag));
            }
        }
    }
    default_settings_cache_[type] = m;
    return m;
}

// ---------------------------------------------------------------------------
// ProcessCaptureRequest
// ---------------------------------------------------------------------------

int Camera3DeviceOpsDelegate::ProcessCaptureRequest(
        camera3_capture_request_t* request) {
    // Checked before doing any marshalling work: this is the per-frame hot
    // path, and once the pipe is dead every request has to fail immediately so
    // the framework's error/teardown path can run inside the watchdog budget.
    if (!pipe_->ok()) return -ENODEV;

    auto req = cros::mojom::Camera3CaptureRequest::New();
    req->frame_number = request->frame_number;

    // Null settings mean "same as the previous request" (camera3.h). The mojom
    // has no null: an empty struct reaches the Chrome OS HAL as settings with
    // no entries, which it adopts as the new settings, and the USB HAL then
    // aborts on the missing AE target fps range. Resend the last settings
    // instead, which is what null stands for.
    if (request->settings) {
        if (last_request_settings_) free_camera_metadata(last_request_settings_);
        last_request_settings_ = clone_camera_metadata(request->settings);
    }
    const camera_metadata_t* settings =
            request->settings ? request->settings : last_request_settings_;

    // settings is non-nullable in the mojom — always send a valid struct.
    req->settings = cros::mojom::CameraMetadata::New();
    if (settings) {
        size_t ne = get_camera_metadata_entry_count(settings);
        size_t dc = 0;
        std::string dropped;
        req->settings->entries = std::vector<cros::mojom::CameraMetadataEntryPtr>();
        for (size_t i = 0; i < ne; i++) {
            camera_metadata_ro_entry_t entry = {};
            if (get_camera_metadata_entry(
                    const_cast<camera_metadata_t*>(settings), i,
                    reinterpret_cast<camera_metadata_entry_t*>(&entry)) != 0) {
                continue;
            }
            if (!request_keys_.empty() && request_keys_.count(entry.tag) == 0) {
                if (!logged_dropped_keys_) {
                    char tag[16];
                    snprintf(tag, sizeof(tag), " 0x%x", entry.tag);
                    dropped += tag;
                }
                continue;
            }
            dc += calculate_camera_metadata_entry_data_size(entry.type, entry.count);
            auto me = cros::mojom::CameraMetadataEntry::New();
            me->index = static_cast<uint32_t>(req->settings->entries->size());
            me->tag = static_cast<cros::mojom::CameraMetadataTag>(entry.tag);
            me->type = static_cast<cros::mojom::EntryType>(entry.type);
            me->count = static_cast<uint32_t>(entry.count);
            // Matches camera_metadata_type_size[] in system/camera_metadata.h:
            // BYTE=1, INT32=4, FLOAT=4, INT64=8, DOUBLE=8, RATIONAL=8
            static const size_t kTypeSize[] = {1, 4, 4, 8, 8, 8};
            size_t type_idx = entry.type < 6 ? entry.type : 0;
            size_t byte_count = entry.count * kTypeSize[type_idx];
            me->data.assign(
                    reinterpret_cast<const uint8_t*>(entry.data.u8),
                    reinterpret_cast<const uint8_t*>(entry.data.u8) + byte_count);
            req->settings->entries->push_back(std::move(me));
        }
        const uint32_t kept = static_cast<uint32_t>(req->settings->entries->size());
        req->settings->entry_count = kept;
        req->settings->entry_capacity = kept;
        req->settings->data_count = static_cast<uint32_t>(dc);
        req->settings->data_capacity = static_cast<uint32_t>(dc);
        if (!dropped.empty()) {
            ALOGI("ProcessCaptureRequest: not sending request keys the HAL does "
                  "not accept:%s", dropped.c_str());
            logged_dropped_keys_ = true;
        }
    }

    // Chrome DCHECKs physcam_settings has_value — set to empty array (no physical sub-cameras).
    req->physcam_settings = std::vector<cros::mojom::Camera3PhyscamMetadataPtr>();

    for (size_t i = 0; i < request->num_output_buffers; i++) {
        const auto& sb = request->output_buffers[i];
        auto msb = cros::mojom::Camera3StreamBuffer::New();
        auto it = stream_ids_.find(sb.stream);
        uint64_t stream_id = (it != stream_ids_.end()) ? it->second : 0;
        msb->stream_id = stream_id;
        msb->status = cros::mojom::Camera3BufferStatus::CAMERA3_BUFFER_STATUS_OK;
        if (sb.acquire_fence >= 0) {
            msb->acquire_fence = mojo::WrapPlatformHandle(
                    mojo::PlatformHandle(base::ScopedFD(sb.acquire_fence)));
        }

        // Send Android's buffer — Chrome imports our DMA-BUF and fills it.
        // Pass sb.buffer (&anb->handle) so we can return the exact pointer
        // to Camera3OutputStream (which uses container_of to recover the ANB).
        msb->buffer_id = buf_adapter_->RegisterBuffer(*sb.buffer, sb.buffer);
        msb->buffer_handle = buf_adapter_->BuildBufferHandle(sb);
        ALOGV("ProcessCaptureRequest: frame=%u buf[%zu] stream_id=%llu "
              "buffer_id=%llu fmt=0x%x %ux%u",
              request->frame_number, i,
              (unsigned long long)stream_id,
              (unsigned long long)msb->buffer_id,
              sb.stream ? sb.stream->format : -1,
              sb.stream ? sb.stream->width : 0,
              sb.stream ? sb.stream->height : 0);
        req->output_buffers.push_back(std::move(msb));
    }

    auto pending = MakePendingReply<int32_t>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              cros::mojom::Camera3CaptureRequestPtr rq,
                              std::shared_ptr<PendingReply<int32_t>> p) {
                (*ops)->ProcessCaptureRequest(std::move(rq),
                        base::BindRepeating(
                                [](std::shared_ptr<PendingReply<int32_t>> p,
                                   int32_t s) {
                                    p->value = s;
                                    p->event.Signal();
                                }, p));
            }, ops_, std::move(req), pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "Camera3DeviceOps::ProcessCaptureRequest",
                    kMojoCaptureTimeoutMs)) {
        return -ENODEV;
    }
    return pending->value;
}

// ---------------------------------------------------------------------------
// Dump / Flush / Close
// ---------------------------------------------------------------------------

void Camera3DeviceOpsDelegate::Dump(int fd) {
    // Dump has no response callback — post and don't wait.
    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              int file_descriptor) {
                mojo::ScopedHandle h = mojo::WrapPlatformHandle(
                        mojo::PlatformHandle(base::ScopedFD(dup(file_descriptor))));
                (*ops)->Dump(std::move(h));
            }, ops_, fd));
}

int Camera3DeviceOpsDelegate::Flush() {
    if (!pipe_->ok()) return -ENODEV;

    auto pending = MakePendingReply<int32_t>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              std::shared_ptr<PendingReply<int32_t>> p) {
                (*ops)->Flush(base::BindRepeating(
                        [](std::shared_ptr<PendingReply<int32_t>> p, int32_t s) {
                            p->value = s;
                            p->event.Signal();
                        }, p));
            }, ops_, pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "Camera3DeviceOps::Flush")) {
        return -ENODEV;
    }
    return pending->value;
}

int Camera3DeviceOpsDelegate::Close() {
    // A dead pipe means Chrome already has nothing left to close, and the
    // caller is on the watchdog-monitored disconnect path — return at once.
    if (!pipe_->ok()) return 0;

    auto pending = MakePendingReply<int32_t>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](DeviceOpsPtr ops,
                              std::shared_ptr<PendingReply<int32_t>> p) {
                (*ops)->Close(base::BindRepeating(
                        [](std::shared_ptr<PendingReply<int32_t>> p, int32_t s) {
                            p->value = s;
                            p->event.Signal();
                        }, p));
            }, ops_, pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "Camera3DeviceOps::Close")) {
        // Report success anyway. The framework is tearing the device down and
        // has no recovery for a failed close; propagating an error here only
        // risks it retrying while the watchdog clock runs. The pipe is now
        // marked dead and ops_ is destroyed on the Mojo thread by ~Delegate.
        return 0;
    }
    return pending->value;
}

}} // namespace fydroid::camera

// ---------------------------------------------------------------------------
// C thunks
// ---------------------------------------------------------------------------

using fydroid::camera::Camera3DeviceOpsDelegate;

static Camera3DeviceOpsDelegate* GetDelegate(const camera3_device_t* dev) {
    return static_cast<Camera3DeviceOpsDelegate*>(dev->priv);
}

extern "C" {

static int camera3_initialize(const camera3_device_t* dev,
                               const camera3_callback_ops_t* ops) {
    return GetDelegate(dev)->Initialize(ops);
}

static int camera3_configure_streams(const camera3_device_t* dev,
                                      camera3_stream_configuration_t* stream_list) {
    return GetDelegate(dev)->ConfigureStreams(stream_list);
}

static const camera_metadata_t* camera3_construct_default_request_settings(
        const camera3_device_t* dev, int type) {
    return GetDelegate(dev)->ConstructDefaultRequestSettings(type);
}

static int camera3_process_capture_request(const camera3_device_t* dev,
                                            camera3_capture_request_t* request) {
    return GetDelegate(dev)->ProcessCaptureRequest(request);
}

static void camera3_dump(const camera3_device_t* dev, int fd) {
    GetDelegate(dev)->Dump(fd);
}

static int camera3_flush(const camera3_device_t* dev) {
    return GetDelegate(dev)->Flush();
}

static int camera3_close(hw_device_t* dev) {
    camera3_device_t* cam_dev = reinterpret_cast<camera3_device_t*>(dev);
    Camera3DeviceOpsDelegate* delegate = GetDelegate(cam_dev);
    int r = delegate->Close();
    delete delegate;
    return r;
}

}  // extern "C"

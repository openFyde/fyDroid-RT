#include "BufferAdapter.h"

#include <base/files/scoped_file.h>
#include <base/logging.h>
#include <hardware/gralloc.h>
#include <mojo/public/cpp/system/platform_handle.h>

#define LOG_TAG "CameraBerthaV3"
#include <utils/Log.h>

// CrOS minigbm gralloc handle layout, matching
// platform/minigbm/cros_gralloc/cros_gralloc_handle.h (r144).
// Used to extract DRM format, strides, offsets from the native handle.
#define DRV_MAX_PLANES 4
#define DRV_MAX_FDS (DRV_MAX_PLANES + 1)
#define CROS_GRALLOC_MAGIC 0xABCDDCBA

struct cros_gralloc_handle_local {
    native_handle_t base;
    int32_t fds[DRV_MAX_FDS];
    uint32_t strides[DRV_MAX_PLANES];
    uint32_t offsets[DRV_MAX_PLANES];
    uint32_t sizes[DRV_MAX_PLANES];
    uint32_t id;
    uint32_t width;
    uint32_t height;
    uint32_t format;        /* DRM fourcc format */
    uint32_t tiling;
    uint64_t format_modifier;
    uint64_t use_flags;
    uint32_t magic;
    uint32_t pixel_stride;
    int32_t droid_format;
    int64_t usage;
    uint32_t num_planes;
    uint64_t reserved_region_size;
    uint64_t total_size;
} __attribute__((packed));

static const cros_gralloc_handle_local* ToCrosHandle(buffer_handle_t handle) {
    if (!handle) return nullptr;
    const auto* h = reinterpret_cast<const cros_gralloc_handle_local*>(handle);
    if (h->magic != CROS_GRALLOC_MAGIC) return nullptr;
    return h;
}

namespace fydroid { namespace camera {

BufferAdapter::BufferAdapter() {
    if (hw_get_module(GRALLOC_HARDWARE_MODULE_ID,
                      &gralloc_module_) != 0) {
        LOG(WARNING) << "BufferAdapter: hw_get_module(gralloc) failed";
    }
}

BufferAdapter::~BufferAdapter() = default;

uint64_t BufferAdapter::RegisterBuffer(buffer_handle_t handle,
                                       const buffer_handle_t* buffer_ptr) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = handle_to_cookie_.find(handle);
    if (it != handle_to_cookie_.end()) {
        // Update buffer_ptr in case the same handle is reused with a new ANB.
        cookie_to_buffer_ptr_[it->second] = buffer_ptr;
        return it->second;
    }
    uint64_t cookie = next_cookie_++;
    handle_to_cookie_[handle] = cookie;
    cookie_to_buffer_ptr_[cookie] = buffer_ptr;
    return cookie;
}

const buffer_handle_t* BufferAdapter::GetBufferPtr(uint64_t buffer_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = cookie_to_buffer_ptr_.find(buffer_id);
    if (it == cookie_to_buffer_ptr_.end()) {
        return nullptr;
    }
    return it->second;
}

void BufferAdapter::UnregisterBuffer(buffer_handle_t handle) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = handle_to_cookie_.find(handle);
    if (it == handle_to_cookie_.end()) return;
    uint64_t cookie = it->second;
    cookie_to_buffer_ptr_.erase(cookie);
    handle_to_cookie_.erase(it);
}

cros::mojom::CameraBufferHandlePtr BufferAdapter::BuildBufferHandle(
        const camera3_stream_buffer_t& sb) {
    if (!sb.buffer || !*sb.buffer) {
        return nullptr;
    }
    buffer_handle_t handle = *sb.buffer;
    uint64_t cookie = RegisterBuffer(handle, sb.buffer);

    auto bh = cros::mojom::CameraBufferHandle::New();
    bh->buffer_id = cookie;

    const cros_gralloc_handle_local* cros_handle = ToCrosHandle(handle);
    if (cros_handle) {
        uint32_t num_planes = cros_handle->num_planes;
        if (num_planes == 0 || num_planes > DRV_MAX_PLANES) {
            LOG(WARNING) << "BufferAdapter: unexpected num_planes=" << num_planes
                         << ", clamping to 1";
            num_planes = 1;
        }

        // Send only the plane FDs (not the reserved region fd).
        for (uint32_t i = 0; i < num_planes; i++) {
            int fd = cros_handle->fds[i];
            if (fd >= 0) {
                mojo::ScopedHandle h = mojo::WrapPlatformHandle(
                        mojo::PlatformHandle(base::ScopedFD(dup(fd))));
                if (h.is_valid()) {
                    bh->fds.push_back(std::move(h));
                }
            }
        }

        bh->drm_format = cros_handle->format;
        bh->hal_pixel_format = static_cast<cros::mojom::HalPixelFormat>(
                cros_handle->droid_format);
        bh->width = cros_handle->width;
        bh->height = cros_handle->height;

        for (uint32_t i = 0; i < num_planes; i++) {
            bh->strides.push_back(cros_handle->strides[i]);
            bh->offsets.push_back(cros_handle->offsets[i]);
        }

        // sizes is optional (MinVersion=3) but include it for completeness.
        bh->sizes = std::vector<uint32_t>();
        for (uint32_t i = 0; i < num_planes; i++) {
            bh->sizes->push_back(cros_handle->sizes[i]);
        }

        bh->has_modifier = true;
        bh->modifier = cros_handle->format_modifier;

        ALOGV("BuildBufferHandle: cookie=%llu drm_fmt=0x%x hal_fmt=0x%x "
              "%ux%u planes=%u strides=[%u,%u] offsets=[%u,%u] "
              "modifier=0x%llx fds=%zu",
              (unsigned long long)cookie, bh->drm_format, (int)bh->hal_pixel_format,
              bh->width, bh->height, num_planes,
              bh->strides.size() > 0 ? bh->strides[0] : 0,
              bh->strides.size() > 1 ? bh->strides[1] : 0,
              bh->offsets.size() > 0 ? bh->offsets[0] : 0,
              bh->offsets.size() > 1 ? bh->offsets[1] : 0,
              (unsigned long long)bh->modifier, bh->fds.size());
    } else {
        // Fallback: not a cros gralloc handle. Copy all FDs, use stream dims.
        LOG(WARNING) << "BufferAdapter: not a cros gralloc handle, using fallback";
        for (int i = 0; i < handle->numFds; i++) {
            mojo::ScopedHandle h = mojo::WrapPlatformHandle(
                    mojo::PlatformHandle(base::ScopedFD(dup(handle->data[i]))));
            if (h.is_valid()) {
                bh->fds.push_back(std::move(h));
            }
        }
        if (sb.stream) {
            bh->width = sb.stream->width;
            bh->height = sb.stream->height;
        }
        // Must have at least one stride/offset per fd sent.
        for (size_t i = 0; i < bh->fds.size(); i++) {
            bh->strides.push_back(sb.stream ? sb.stream->width : 0);
            bh->offsets.push_back(0);
        }
        bh->drm_format = 0;
        bh->hal_pixel_format =
                cros::mojom::HalPixelFormat::HAL_PIXEL_FORMAT_IMPLEMENTATION_DEFINED;
    }

    return bh;
}

}} // namespace fydroid::camera

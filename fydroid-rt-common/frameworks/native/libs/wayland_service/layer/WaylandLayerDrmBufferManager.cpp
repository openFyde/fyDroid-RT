/*
 * Copyright (C) 2024 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// WaylandLayerDrmBufferManager: DMA-BUF buffer lifecycle management.
// Creates wl_buffer objects from GraphicBuffer DMA-BUF fds via
// zwp_linux_dmabuf_v1, tracks busy/free buffers, handles release callbacks.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandLayerDrmBufferManager"

#include "WaylandLayerDrmBufferManager.h"

#include <drm_fourcc.h>
#include <linux-dmabuf-unstable-v1-client-protocol.h>
#include <wayland-client.h>

#include <cros_gralloc/cros_gralloc_handle.h>
#include <cutils/properties.h>
#include <log/log.h>
#include "../WaylandLog.h"
#include <system/graphics.h>

// sw_sync lives in libsync but the header isn't in the default export path
extern "C" {
int sw_sync_timeline_create(void);
int sw_sync_timeline_inc(int fd, unsigned count);
int sw_sync_fence_create(int fd, const char *name, unsigned value);
}

namespace android {

void ReleaseFence::signal() {
    if (timelineFd >= 0) {
        sw_sync_timeline_inc(timelineFd, 1);
        close(timelineFd);
        timelineFd = -1;
    }
}

static void bufferRelease(void* data, struct wl_buffer* /*buffer*/) {
    auto* entry = static_cast<BufferEntry*>(data);
    std::shared_ptr<ReleaseFence> fence;
    {
        std::lock_guard<std::mutex> lock(entry->entryMutex);
        // Unpin the GraphicBuffer — VirtualDisplaySurface can now reclaim it.
        // Under the lock: getOrCreateBuffer re-pins this same sp<> on the SF
        // composition thread, and a concurrent assignment trips RefBase's race
        // detector (SIGABRT). Heavy buffer churn (e.g. screen capture) hits it.
        entry->graphicBuffer = nullptr;
        fence = entry->currentFence;
    }
    // Cleared LAST, after every other access to the entry. In per-frame mode
    // this is also what makes the entry reapable, and the reaper runs on the SF
    // thread — publishing it only once this callback is done touching the entry
    // is what keeps that destruction safe.
    entry->heldByCompositor.store(false, std::memory_order_release);
    if (!fence) return;
    if (fence->pending.fetch_sub(1, std::memory_order_acq_rel) == 1) {
        fence->signal();
    }
}

static const struct wl_buffer_listener sBufferListener = {
    .release = bufferRelease,
};

WaylandLayerDrmBufferManager::WaylandLayerDrmBufferManager(
        zwp_linux_dmabuf_v1* dmabuf, wl_display* display)
    : mDmabuf(dmabuf), mDisplay(display) {}

// Re-import a fresh wl_buffer on every present instead of caching one per
// GraphicBuffer. Needed where the compositor snapshots the DMA-BUF at import
// rather than reading it on each commit — see the header. Off by default; it
// costs a zwp_linux_dmabuf import per frame per window.
static const bool sWlBufferPerFrame =
        property_get_bool("persist.fydroid.sf.wl_buffer_per_frame", 0);

WaylandLayerDrmBufferManager::~WaylandLayerDrmBufferManager() {
    for (auto& [id, entries] : mBufferCache) {
        for (auto& entry : entries) {
            if (entry->buffer) wl_buffer_destroy(entry->buffer);
        }
    }
    mBufferCache.clear();
}

void WaylandLayerDrmBufferManager::reapReleasedLocked(
        std::vector<std::unique_ptr<BufferEntry>>& entries) {
    // Keep the newest — it is the one currently attached — and drop any older
    // entry the compositor has finished with.
    if (entries.size() <= 1) return;
    size_t keep = 0;
    for (size_t i = 0; i + 1 < entries.size(); i++) {
        if (entries[i]->heldByCompositor.load(std::memory_order_acquire)) {
            entries[keep++] = std::move(entries[i]);
        } else if (entries[i]->buffer) {
            wl_buffer_destroy(entries[i]->buffer);
        }
    }
    entries[keep++] = std::move(entries.back());
    entries.resize(keep);
}

wl_buffer* WaylandLayerDrmBufferManager::getOrCreateBuffer(
        const sp<GraphicBuffer>& buffer) {
    uint64_t id = buffer->getId();

    auto it = mBufferCache.find(id);
    if (it != mBufferCache.end() && !it->second.empty()) {
        if (!sWlBufferPerFrame) {
            // Pin the GraphicBuffer to prevent VirtualDisplaySurface from
            // reclaiming and overwriting the DMA-BUF while Chrome is using it.
            // Released in bufferRelease() when Chrome is done. Under entryMutex —
            // bufferRelease() clears this same sp<> on the Wayland event thread.
            auto& entry = it->second.back();
            {
                std::lock_guard<std::mutex> lock(entry->entryMutex);
                entry->graphicBuffer = buffer;
            }
            return entry->buffer;
        }
        // Per-frame mode: fall through and import again, first dropping the
        // wl_buffers the compositor has handed back.
        reapReleasedLocked(it->second);
    }

    // Cast to cros_gralloc_handle to read DRM format, modifier, strides, offsets
    const native_handle_t* handle = buffer->getNativeBuffer()->handle;
    if (!handle || handle->numFds < 1) {
        ALOGE("buffer %" PRIu64 " has no file descriptors", id);
        return nullptr;
    }

    auto* crosHandle = reinterpret_cast<const struct cros_gralloc_handle*>(handle);
    uint32_t width = buffer->getWidth();
    uint32_t height = buffer->getHeight();
    uint32_t drmFormat = crosHandle->format;
    uint64_t modifier = crosHandle->format_modifier;
    uint32_t modHi = static_cast<uint32_t>(modifier >> 32);
    uint32_t modLo = static_cast<uint32_t>(modifier & 0xFFFFFFFF);

    // fds[] holds num_planes color-plane fds followed by an optional
    // metadata reserved-region shm fd (gralloc4 allocates one for every
    // buffer). That shm fd must NOT be exported as a dmabuf plane —
    // Chrome's gbm_bo_import rejects the whole buffer. Trust num_planes
    // when the handle layout is verified by the magic field; otherwise
    // fall back to counting fds that carry a non-zero stride.
    constexpr uint32_t kCrosGrallocMagic = 0xABCDDCBA; // cros_gralloc_helpers.h
    uint32_t numPlanes = 0;
    if (crosHandle->magic == kCrosGrallocMagic) {
        numPlanes = crosHandle->num_planes;
    }
    if (numPlanes < 1 || numPlanes > static_cast<uint32_t>(handle->numFds)) {
        numPlanes = 1; // plane 0 always exists
        for (uint32_t i = 1; i < DRV_MAX_PLANES && i < static_cast<uint32_t>(handle->numFds); i++) {
            if (crosHandle->fds[i] >= 0 && crosHandle->strides[i] > 0) {
                numPlanes = i + 1;
            }
        }
    }

    ALOGI("creating wl_buffer: %" PRIu64 " %ux%u fmt=0x%08x mod=0x%" PRIx64
          " planes=%u fd=%d numFds=%d numInts=%d",
          id, width, height, drmFormat, modifier, numPlanes,
          handle->data[0], handle->numFds, handle->numInts);

    // Dump raw handle data at verbose level for debugging struct alignment.
    // data[0..numFds-1] = fds, data[numFds..] = ints (strides, offsets, etc.)
    {
        int total = handle->numFds + handle->numInts;
        if (total > 40) total = 40;
        char hexbuf[512];
        int pos = 0;
        for (int i = 0; i < total && pos < 480; i++) {
            pos += snprintf(hexbuf + pos, sizeof(hexbuf) - pos,
                            "%s[%d]=0x%08x", (i > 0 ? " " : ""), i,
                            static_cast<uint32_t>(handle->data[i]));
        }
        ALOGVT("raw handle data: %s", hexbuf);
    }

    // Create DMA-BUF params — add each plane from the cros_gralloc handle
    struct zwp_linux_buffer_params_v1* params =
            zwp_linux_dmabuf_v1_create_params(mDmabuf);

    for (uint32_t i = 0; i < numPlanes; i++) {
        int fd = crosHandle->fds[i];
        uint32_t offset = crosHandle->offsets[i];
        uint32_t stride = crosHandle->strides[i];
        ALOGI("  plane %u: fd=%d offset=%u stride=%u", i, fd, offset, stride);
        zwp_linux_buffer_params_v1_add(params, fd, i, offset, stride, modHi, modLo);
    }

    wl_buffer* wlBuffer = zwp_linux_buffer_params_v1_create_immed(
            params, width, height, drmFormat, /*flags=*/0);
    zwp_linux_buffer_params_v1_destroy(params);

    if (!wlBuffer) {
        ALOGE("zwp_linux_buffer_params_v1_create_immed failed for buffer %" PRIu64, id);
        return nullptr;
    }

    // Roundtrip to detect protocol errors from create_immed — the compositor
    // may reject the buffer asynchronously. Once for the session: it is a
    // synchronous round-trip to Chrome, which per-frame mode cannot afford, and
    // a rejection here is about the format/modifier, which does not change.
    if (!mImportVerified) {
        wl_display_roundtrip(mDisplay);
        int err = wl_display_get_error(mDisplay);
        if (err) {
            ALOGE("Wayland error %d after create_immed for buffer %" PRIu64
                  " (fmt=0x%08x mod=0x%" PRIx64 " %ux%u) — buffer import rejected",
                  err, id, drmFormat, modifier, width, height);
            return nullptr;
        }
        mImportVerified = true;
    }

    auto entry = std::make_unique<BufferEntry>();
    entry->buffer = wlBuffer;
    entry->graphicBuffer = buffer;  // pin while Chrome uses it
    BufferEntry* entryPtr = entry.get();
    mBufferCache[id].push_back(std::move(entry));

    wl_buffer_add_listener(wlBuffer, &sBufferListener, entryPtr);
    ALOGVT("wl_buffer created successfully for %" PRIu64, id);
    return wlBuffer;
}

int WaylandLayerDrmBufferManager::beginFrame() {
    int timeline = sw_sync_timeline_create();
    if (timeline < 0) {
        ALOGE("sw_sync_timeline_create failed: %s", strerror(errno));
        mFrameFence.reset();
        return -1;
    }
    int fence = sw_sync_fence_create(timeline, "LayerRelease", 1);
    if (fence < 0) {
        ALOGE("sw_sync_fence_create failed: %s", strerror(errno));
        close(timeline);
        mFrameFence.reset();
        return -1;
    }
    mFrameFence = std::make_shared<ReleaseFence>(timeline);
    return fence;  // real sync fence fd — works with sync_merge
}

void WaylandLayerDrmBufferManager::markPresented(uint64_t bufferId) {
    auto it = mBufferCache.find(bufferId);
    if (it == mBufferCache.end() || it->second.empty()) return;
    // The newest entry is the one just handed out and attached.
    auto& entry = it->second.back();
    // The buffer is now Chrome's until it sends wl_buffer.release. This is
    // independent of the frame-fence bookkeeping below, which only runs when a
    // caller has opened a frame with beginFrame().
    entry->heldByCompositor.store(true, std::memory_order_release);

    if (!mFrameFence) return;
    {
        std::lock_guard<std::mutex> lock(entry->entryMutex);
        entry->currentFence = mFrameFence;
    }
    mFrameFence->pending.fetch_add(1, std::memory_order_relaxed);
}

bool WaylandLayerDrmBufferManager::isHeldByCompositor(uint64_t bufferId) const {
    auto it = mBufferCache.find(bufferId);
    if (it == mBufferCache.end()) return false;
    // Per-frame mode can have several wl_buffers alive for one GraphicBuffer;
    // the DMA-BUF is busy while the compositor still holds any of them.
    for (const auto& entry : it->second) {
        if (entry->heldByCompositor.load(std::memory_order_acquire)) return true;
    }
    return false;
}

void WaylandLayerDrmBufferManager::finishFrame() {
    if (mFrameFence && mFrameFence->pending.load(std::memory_order_acquire) == 0) {
        // No buffers were presented — signal fence immediately
        mFrameFence->signal();
    }
}

} // namespace android

#pragma clang diagnostic pop

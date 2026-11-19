/*
 * Copyright (C) 2024 The FydeOS Authors
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

// TaskRenderSurface: GraphicBuffer pool for per-task GPU composition.
//
// Each WaylandTaskOutput has its own TaskRenderSurface. RenderEngine
// composites the task's layers into a dequeued buffer. queueBuffer()
// routes the composed buffer through HWC via setClientTarget() on the
// associated virtual display. No Wayland objects — all presentation
// goes through the HWC path.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#undef LOG_TAG
#define LOG_TAG "TaskRenderSurface"

#include "TaskRenderSurface.h"

#include "DisplayHardware/HWComposer.h"
#include "hwc2/WaylandHwc2Composer.h"

#include <algorithm>
#include <cutils/properties.h>
#include <log/log.h>
#include <renderengine/RenderEngine.h>
#include <renderengine/impl/ExternalTexture.h>
#include <sync/sync.h>
#include <ui/Fence.h>
#include <ui/GraphicBuffer.h>

namespace android {

// Honour wl_buffer.release when choosing which pool slot to render into.
// On by default; set persist.fydroid.sf.buf_release_track=0 to fall back to the
// old "first FREE slot wins" behaviour, which is useful for A/B-ing a flicker
// report on one binary. Read once — SF must be restarted for a change to apply.
static const bool sTrackBufferRelease =
        property_get_bool("persist.fydroid.sf.buf_release_track", 1);

// Checksum every composed frame before it is handed to the compositor
// (persist.fydroid.sf.hash_frames=1). This is the one measurement that says
// which side of SurfaceFlinger a stuck picture is on: if the hash advances every
// frame the composed output is fine and the content is being lost downstream, in
// the compositor or the host; if it repeats with a short period the loss is
// upstream and SF is faithfully re-compositing stale content.
//
// Costs a CPU lock/unlock and a sparse read per frame, and forces SW_READ usage
// on the pool, so it is off by default.
static const bool sHashFrames =
        property_get_bool("persist.fydroid.sf.hash_frames", 0);

// A vendor usage bit in BufferUsage::VENDOR_MASK_HI (bits 48-63). Consumed by
// minigbm's cros_gralloc_convert_usage as BUFFER_USAGE_ARC_COMPOSER_EXPORT ->
// BO_USE_ARC_COMPOSER_EXPORT; the v3d backend treats it as a hard "must be
// CMA-backed and exported as the dma-heap fd" requirement. Inert on every other
// backend and on non-Pi boards.
//
// Deliberately not GRALLOC_USAGE_PRIVATE_0 (bit 28): minigbm already spends that
// on BUFFER_USAGE_FRONT_RENDERING_PRIVATE and would convert this to
// BO_USE_FRONT_RENDERING instead, with no error anywhere.
static constexpr uint64_t kUsageArcComposerExport = 1ULL << 55;

// Size of the per-task GraphicBuffer pool. Runtime-settable because it doubles
// as a diagnostic — see the note on kMaxBufferCount in the header. Read once;
// kill -9 surfaceflinger to re-read.
const int TaskRenderSurface::sBufferCount = std::clamp(
        property_get_int32("persist.fydroid.sf.task_buffer_count", 3), 2,
        TaskRenderSurface::kMaxBufferCount);

ui::Size TaskRenderSurface::roundUpSize(const ui::Size& size) {
    int w = (size.width + kAllocGranularity - 1) / kAllocGranularity * kAllocGranularity;
    int h = (size.height + kAllocGranularity - 1) / kAllocGranularity * kAllocGranularity;
    return ui::Size(w, h);
}

TaskRenderSurface::TaskRenderSurface(const ui::Size& size,
                                     HWComposer& hwComposer,
                                     renderengine::RenderEngine& renderEngine,
                                     DisplayId displayId,
                                     int32_t taskId)
    : mSize(size),
      mAllocSize(roundUpSize(size)),
      mHwComposer(hwComposer),
      mRenderEngine(renderEngine),
      mDisplayId(displayId),
      mTaskId(taskId) {
    mAcquireFence = Fence::NO_FENCE;
}

TaskRenderSurface::~TaskRenderSurface() = default;

bool TaskRenderSurface::isValid() const {
    return mSize.width > 0 && mSize.height > 0;
}

void TaskRenderSurface::initialize() {
    // No-op — buffers are allocated lazily in dequeueBuffer.
}

const ui::Size& TaskRenderSurface::getSize() const {
    return mSize;
}

bool TaskRenderSurface::isProtected() const {
    return false;
}

const sp<Fence>& TaskRenderSurface::getClientTargetAcquireFence() const {
    return mAcquireFence;
}

void TaskRenderSurface::setDisplaySize(const ui::Size& size) {
    setSize(size);
}

void TaskRenderSurface::setSize(const ui::Size& size) {
    if (mSize == size) return;
    mSize = size;

    // Grow-only: reuse existing buffers when the new size fits within the
    // current allocation.  During resize animations the size changes ~5px per
    // frame — reallocating every frame would flood DMA-BUF imports and exhaust
    // kernel GEM handles, causing a NULL-pointer panic.  Grow-only + rounding
    // means the first frame's allocation covers all subsequent frames.
    if (size.width <= mAllocSize.width && size.height <= mAllocSize.height) {
        ALOGV("TaskRenderSurface[%d]: resized to %dx%d (alloc %dx%d still fits)",
              mTaskId, size.width, size.height, mAllocSize.width, mAllocSize.height);
        return;
    }

    // The allocation is a high-water mark: when it has to change, it takes the
    // union of the old and new extents, never just the new one.  A task VD size
    // tracks the composed bounding box, which during a rotation includes
    // transient animation frames — and one of those can be *shorter* than the
    // real window while being wider.  Taking the new size alone then shrank the
    // allocation, and the frame composed into it was exported to Chrome as a
    // wl_buffer of that height, under a viewport still asking for the full
    // window.  Everything below the short buffer composited black and stayed
    // that way, because the next size fit the (now smaller) allocation and
    // never triggered a realloc.  Measured on device: a 1274x1520 window took a
    // 1520x794 animation frame, allocated 1536x1024, and the right half of the
    // split was black from row 1024 down until the app was resized again.
    const ui::Size grown(std::max(size.width, mAllocSize.width),
                         std::max(size.height, mAllocSize.height));
    mAllocSize = roundUpSize(grown);
    for (int i = 0; i < sBufferCount; i++) {
        mBuffers[i] = nullptr;
        mBufferState[i] = FREE;
    }
    mCurrentBuffer = -1;
    ALOGI("TaskRenderSurface[%d]: resized to %dx%d (alloc grown to %dx%d)",
          mTaskId, size.width, size.height, mAllocSize.width, mAllocSize.height);
}

void TaskRenderSurface::setBufferDataspace(ui::Dataspace) {
    // No-op — we always use the default dataspace.
}

void TaskRenderSurface::setBufferPixelFormat(ui::PixelFormat) {
    // No-op — we always use RGBA_8888.
}

void TaskRenderSurface::setProtected(bool) {
    // No-op — we don't support protected content.
}

status_t TaskRenderSurface::beginFrame(bool /*mustRecompose*/) {
    return NO_ERROR;
}

void TaskRenderSurface::prepareFrame(bool /*usesClientComposition*/,
                                     bool /*usesDeviceComposition*/) {
    // No-op — no DisplaySurface to notify.
}

// True while the ChromeOS compositor still has this buffer attached. A buffer
// that was never presented — or that was composed and then refused by the
// frame-callback throttle — reads false, because it never reached Chrome.
static bool heldByCompositor(const sp<GraphicBuffer>& buffer) {
    if (buffer == nullptr) return false;
    auto* hwc2 = Hwc2::WaylandHwc2Composer::getInstance();
    return hwc2 != nullptr && hwc2->isBufferHeldByCompositor(buffer->getId());
}

std::shared_ptr<renderengine::ExternalTexture> TaskRenderSurface::dequeueBuffer(
        base::unique_fd* bufferFence) {
    *bufferFence = base::unique_fd();

    mDequeueCount++;

    // Find a FREE buffer the compositor is not still reading.
    //
    // FREE only means "SF is not rendering into it and it is not the slot most
    // recently handed to HWC" — queueBuffer marks every other slot FREE the
    // instant it queues a new one, without waiting for anybody. It says nothing
    // about whether Chrome is done with the buffer, and Chrome keeps an
    // attached wl_buffer until it has composited the NEXT one. So the slot that
    // comes back around is routinely the one still on screen, and rendering
    // into it rewrites the visible frame underneath the compositor.
    //
    // A static window never notices: it composes so rarely that Chrome has long
    // since released. Video is the case that breaks — SF composes every vsync
    // (CompositionEngineArc::present recomposes every task output every frame),
    // so the pool turns over as fast as Chrome consumes it. A frame refused by
    // the frame-callback throttle makes it certain rather than likely: that
    // buffer never reached Chrome, so the previous one is still on screen AND
    // is the next one this loop would pick.
    int idx = -1;
    for (int i = 0; i < sBufferCount; i++) {
        if (mBufferState[i] != FREE) continue;
        if (heldByCompositor(mBuffers[i])) {
            // The slot we would otherwise have taken is still on screen.
            // Counted either way so the two policies can be compared on one
            // binary: with tracking on this is a slot we stepped over, with it
            // off it is a frame we painted over while Chrome was showing it.
            mHeldSkipCount++;
            if (sTrackBufferRelease) continue;
        }
        idx = i;
        break;
    }

    if (idx < 0) {
        // Every free slot is still on screen. Overwriting one is visible
        // flicker, but stalling composition is worse (dequeueBuffer is
        // synchronous on SF's main thread and there is nothing to wait on), so
        // take the oldest free slot and count it — a non-zero counter here
        // means the pool is too small for this window's frame rate.
        for (int i = 0; i < sBufferCount; i++) {
            if (mBufferState[i] == FREE) {
                idx = i;
                mOverwriteCount++;
                break;
            }
        }
    }

    if (idx < 0) {
        // No free buffers — force-reclaim the oldest QUEUED one.
        for (int i = 0; i < sBufferCount; i++) {
            if (mBufferState[i] == QUEUED) {
                idx = i;
                ALOGW("TaskRenderSurface: force-reclaiming queued buffer %d", i);
                break;
            }
        }
    }

    if (idx < 0) {
        ALOGE("TaskRenderSurface: no available buffer");
        return nullptr;
    }

    logDequeueStats();

    // Allocate buffer if needed (first use or after resize).
    // Uses mAllocSize (rounded up) so small resize steps reuse buffers.
    if (mBuffers[idx] == nullptr) {
        mBuffers[idx] = new GraphicBuffer(
                static_cast<uint32_t>(mAllocSize.width),
                static_cast<uint32_t>(mAllocSize.height),
                PIXEL_FORMAT_RGBA_8888,
                // layerCount is spelled out to reach the uint64_t-usage
                // constructor: the 5-argument one takes a uint32_t and would
                // silently truncate kUsageArcComposerExport away.
                /*layerCount=*/1u,
                GraphicBuffer::USAGE_HW_RENDER | GraphicBuffer::USAGE_HW_COMPOSER |
                        // Sampled by RenderEngine as the source of the
                        // drag-resize mask (CompositionEngineArc); GLES
                        // RenderEngine aborts on a source without it.
                        GraphicBuffer::USAGE_HW_TEXTURE |
                        // These are the only buffers fydroid hands to Chrome, and
                        // Chrome imports them on vc4 (card0), which has no IOMMU on
                        // BCM2711. A pageable v3d BO is refused there with EINVAL —
                        // the window then exists with no content at all. The flag
                        // makes minigbm's v3d backend take the CMA dma-heap path and
                        // export the heap fd, which vc4 can import.
                        kUsageArcComposerExport |
                        // SW_READ so the composed frame can be checksummed —
                        // see sHashFrames. Without it the buffer cannot be
                        // locked for CPU read and a dump comes back all zeros,
                        // which has previously been mistaken for a black frame.
                        (sHashFrames ? GraphicBuffer::USAGE_SW_READ_OFTEN : 0),
                "TaskRenderSurface");
        if (mBuffers[idx]->initCheck() != NO_ERROR) {
            ALOGE("TaskRenderSurface: GraphicBuffer allocation failed for %dx%d",
                  mAllocSize.width, mAllocSize.height);
            mBuffers[idx] = nullptr;
            return nullptr;
        }
        // New (or reallocated) buffer — drop the stale ExternalTexture wrapper.
        mExternalTextures[idx] = nullptr;
        ALOGI("TaskRenderSurface[%d]: allocated buffer %d (%dx%d for logical %dx%d)",
              mTaskId, idx, mAllocSize.width, mAllocSize.height,
              mSize.width, mSize.height);
    }

    mBufferState[idx] = RENDERING;
    mCurrentBuffer = idx;
    // AOSP-13: RenderSurface::dequeueBuffer returns an ExternalTexture (GraphicBuffer
    // wrapped + imported into RenderEngine). Cache the wrapper per buffer slot.
    if (!mExternalTextures[idx]) {
        mExternalTextures[idx] = std::make_shared<renderengine::impl::ExternalTexture>(
                mBuffers[idx], mRenderEngine,
                renderengine::impl::ExternalTexture::Usage::READABLE |
                        renderengine::impl::ExternalTexture::Usage::WRITEABLE);
    }
    return mExternalTextures[idx];
}

// Sparse FNV-1a over the composed pixels. Sparse on purpose: this runs per frame
// per window, and a stride coprime with the row pitch still visits every part of
// the image, which is all that is needed to tell one frame from another.
static uint64_t hashComposedFrame(const sp<GraphicBuffer>& buffer,
                                  const base::unique_fd& readyFence) {
    if (buffer == nullptr) return 0;
    // The GPU (or llvmpipe) may still be writing; the hash is meaningless until
    // it is done.
    if (readyFence.get() >= 0) sync_wait(readyFence.get(), 1000);

    void* vaddr = nullptr;
    if (buffer->lock(GraphicBuffer::USAGE_SW_READ_OFTEN, &vaddr) != NO_ERROR ||
        vaddr == nullptr) {
        return 0;
    }
    const auto* bytes = static_cast<const uint8_t*>(vaddr);
    const size_t span = static_cast<size_t>(buffer->getStride()) * 4 *
            buffer->getHeight();
    uint64_t hash = 1469598103934665603ULL;
    for (size_t i = 0; i < span; i += 997) {
        hash = (hash ^ bytes[i]) * 1099511628211ULL;
    }
    buffer->unlock();
    return hash;
}

std::shared_ptr<renderengine::ExternalTexture> TaskRenderSurface::lastQueuedTexture(
        ui::Size* contentSize) const {
    if (contentSize) *contentSize = mLastQueuedSize;
    return mLastQueuedTexture;
}

void TaskRenderSurface::queueBuffer(base::unique_fd readyFence) {
    if (mCurrentBuffer < 0 || mCurrentBuffer >= sBufferCount) return;

    if (sHashFrames) {
        const uint64_t hash = hashComposedFrame(mBuffers[mCurrentBuffer], readyFence);
        ALOGI("TaskRenderSurface[%d]: frame %u slot %d bufId %" PRIu64
              " readyFence %d hash %016" PRIx64 "%s",
              mTaskId, mPageFlipCount, mCurrentBuffer,
              mBuffers[mCurrentBuffer]->getId(), readyFence.get(), hash,
              hash == mLastFrameHash ? "  <-- SAME AS PREVIOUS" : "");
        mLastFrameHash = hash;
    }

    mBufferState[mCurrentBuffer] = QUEUED;
    mLastQueuedTexture = mExternalTextures[mCurrentBuffer];
    mLastQueuedSize = mSize;

    // Convert the readyFence to an sp<Fence> for HWC.
    sp<Fence> acquireFence = Fence::NO_FENCE;
    if (readyFence.get() >= 0) {
        acquireFence = new Fence(readyFence.release());
    }

    // Keep it: this is the fence every source layer is released on.
    //
    // readyFence signals when RenderEngine has finished reading those layers
    // and writing this buffer. Output::postFramebuffer() asks for it back via
    // getClientTargetAcquireFence(), and because a WaylandTaskOutput always
    // reports usesClientComposition it becomes the release fence of EVERY layer
    // composited here:
    //
    //     releaseFence = Fence::merge("LayerRelease", releaseFence,
    //                                 frame.clientTargetAcquireFence);
    //     layer->getLayerFE().onLayerDisplayed(releaseFence);
    //
    // It used to be dropped on the floor — assigned to the local above, handed
    // to setClientTarget, and never stored — so mAcquireFence kept the
    // Fence::NO_FENCE it was given in the constructor and every layer was
    // released with no fence at all. To a producer that means "the consumer is
    // finished, reuse this immediately", which is false while the composition
    // is still in flight. A software video decoder takes it at its word: it
    // dequeues the buffer it just handed over and starts writing the next frame
    // into it with the CPU while the GPU is still sampling it.
    //
    // There are no HWC layer fences to merge with either — WaylandHwc2Composer::
    // getReleaseFences() returns an empty list because presentation goes through
    // setClientTarget rather than per-layer buffers — so this is the ONLY thing
    // holding a source buffer for the duration of the composition that reads it.
    mAcquireFence = acquireFence;

    // Route the composed buffer through HWC via setClientTarget.
    // This stores the buffer in the HWC virtual display, ready for
    // presentDisplay() to present it via Wayland.
    // AOSP-13: setClientTarget takes a HalDisplayId.
    const auto halDisplayId = HalDisplayId::tryCast(mDisplayId);
    status_t err = halDisplayId ? mHwComposer.setClientTarget(
            *halDisplayId,
            static_cast<uint32_t>(mCurrentBuffer),  // slot
            acquireFence,
            mBuffers[mCurrentBuffer],
            ui::Dataspace::V0_SRGB) : BAD_VALUE;
    if (err != NO_ERROR) {
        ALOGE("TaskRenderSurface: setClientTarget failed: %d", err);
    }

    // Previous queued buffers can now be freed.
    for (int i = 0; i < sBufferCount; i++) {
        if (i != mCurrentBuffer && mBufferState[i] == QUEUED) {
            mBufferState[i] = FREE;
        }
    }

    mPageFlipCount++;
}

void TaskRenderSurface::logDequeueStats() {
    // Silent unless the pool is actually under pressure: a window composing at
    // its own pace never skips or overwrites anything.
    if (mHeldSkipCount == 0 && mOverwriteCount == 0) return;

    const nsecs_t now = systemTime(SYSTEM_TIME_MONOTONIC);
    if (mStatsNextLogNs == 0) {
        mStatsNextLogNs = now + s2ns(1);
        return;
    }
    if (now < mStatsNextLogNs) return;

    ALOGI("TaskRenderSurface[%d]: dequeues=%u held-hits=%u(%s) overwrites=%u "
          "(%dx%d alloc %dx%d)",
          mTaskId, mDequeueCount, mHeldSkipCount,
          sTrackBufferRelease ? "skipped" : "OVERWRITTEN", mOverwriteCount,
          mSize.width, mSize.height, mAllocSize.width, mAllocSize.height);
    mDequeueCount = 0;
    mHeldSkipCount = 0;
    mOverwriteCount = 0;
    mStatsNextLogNs = now + s2ns(1);
}

void TaskRenderSurface::onPresentDisplayCompleted() {
    // No-op — presentation tracking is handled by the HWC virtual display.
}

void TaskRenderSurface::flip() {
    // No-op — no DisplaySurface to advance.
}

void TaskRenderSurface::dump(std::string& result) const {
    result += "TaskRenderSurface: ";
    result += std::to_string(mSize.width) + "x" + std::to_string(mSize.height);
    result += " alloc=" + std::to_string(mAllocSize.width) + "x" + std::to_string(mAllocSize.height);
    result += " displayId=" + std::to_string(mDisplayId.value);
    result += " buffers=[";
    for (int i = 0; i < sBufferCount; i++) {
        if (i > 0) result += ",";
        result += (mBufferState[i] == FREE ? "F" :
                   mBufferState[i] == RENDERING ? "R" : "Q");
    }
    result += "] current=" + std::to_string(mCurrentBuffer);
    result += " flips=" + std::to_string(mPageFlipCount);
    result += "\n";
}

std::uint32_t TaskRenderSurface::getPageFlipCount() const {
    return mPageFlipCount;
}

} // namespace android

#pragma clang diagnostic pop

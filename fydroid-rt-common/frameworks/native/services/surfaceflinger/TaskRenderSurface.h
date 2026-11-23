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

#pragma once

#include <compositionengine/RenderSurface.h>
#include <renderengine/ExternalTexture.h>
#include <ui/GraphicBuffer.h>
#include <ui/Size.h>
#include <utils/StrongPointer.h>
#include <utils/Timers.h>

#include <ui/DisplayId.h>

namespace android {

namespace renderengine {
class RenderEngine;
}

class HWComposer;

// RenderSurface for per-task GPU composition outputs.
//
// Manages a small pool of GraphicBuffers (task-sized) for GPU composition.
// RenderEngine composites task layers into a dequeued buffer, then
// queueBuffer() routes the composed buffer through HWC via setClientTarget()
// on the associated virtual display. No Wayland objects — all presentation
// goes through the HWC virtual display path.
class TaskRenderSurface : public compositionengine::RenderSurface {
public:
    // AOSP-13: needs a RenderEngine to wrap dequeued buffers in ExternalTextures.
    // taskId is for logging only: buffer sizing bugs are invisible when every
    // task's allocations log under the same tag (a snapped pair renders two
    // interleaved streams of them).
    TaskRenderSurface(const ui::Size& size, HWComposer& hwComposer,
                      renderengine::RenderEngine& renderEngine, DisplayId displayId,
                      int32_t taskId);
    ~TaskRenderSurface() override;

    // compositionengine::RenderSurface
    bool isValid() const override;
    void initialize() override;
    const ui::Size& getSize() const override;
    bool isProtected() const override;
    const sp<Fence>& getClientTargetAcquireFence() const override;
    void setDisplaySize(const ui::Size& size) override;
    void setBufferDataspace(ui::Dataspace) override;
    void setBufferPixelFormat(ui::PixelFormat) override;
    void setProtected(bool useProtected) override;
    status_t beginFrame(bool mustRecompose) override;
    void prepareFrame(bool usesClientComposition, bool usesDeviceComposition) override;
    // AOSP-13: RenderSurface::dequeueBuffer returns an ExternalTexture.
    std::shared_ptr<renderengine::ExternalTexture> dequeueBuffer(
            base::unique_fd* bufferFence) override;
    void queueBuffer(base::unique_fd readyFence) override;
    void onPresentDisplayCompleted() override;
    void flip() override;
    void dump(std::string& result) const override;
    std::uint32_t getPageFlipCount() const override;
    // AOSP-13: new RenderSurface pure virtual — no client-composition prediction.
    bool supportsCompositionStrategyPrediction() const override { return false; }

    // Update buffer size (e.g., when task resizes).
    void setSize(const ui::Size& size);

    // The most recently composed frame — what the window shows, give or take a
    // frame the throttle dropped — for the drag-resize mask. The image is the
    // contentSize region at the buffer origin; the buffer itself is allocated
    // larger (kAllocGranularity). Null before the first frame.
    std::shared_ptr<renderengine::ExternalTexture> lastQueuedTexture(
            ui::Size* contentSize) const;

private:
    ui::Size mSize;       // logical (exact) size for rendering viewport
    ui::Size mAllocSize;  // rounded-up size for buffer allocation (grow-only)

    // Round up to this granularity to minimize unique buffer sizes across
    // windows and avoid reallocation during resize animations.
    static constexpr int kAllocGranularity = 256;
    static ui::Size roundUpSize(const ui::Size& size);

    // Three slots by default, not two. The pool has no BufferQueue in front of
    // it, so the only thing keeping SF from rendering over a frame the ChromeOS
    // compositor is still showing is the held-by-compositor check in
    // dequeueBuffer — and that check can only move us off a busy slot if a spare
    // one exists. Chrome holds the attached buffer until it has composited the
    // next one, so at any moment up to two of our buffers can be outstanding
    // (the one on screen and the one just committed). Slots are allocated
    // lazily, so a window that never cycles this fast still only ever owns two
    // buffers.
    //
    // The count is settable at runtime (persist.fydroid.sf.task_buffer_count)
    // because it is also a diagnostic: each pool slot gets exactly one cached
    // wl_buffer, so on a board where the compositor snapshots a buffer's
    // contents at import instead of re-reading them, the number of distinct
    // frames the picture cycles through equals the pool size. Changing the count
    // and watching the loop length change identifies that failure precisely.
    static constexpr int kMaxBufferCount = 16;
    static const int sBufferCount;
    enum BufferState { FREE, RENDERING, QUEUED };
    sp<GraphicBuffer> mBuffers[kMaxBufferCount];
    // AOSP-13: cache the ExternalTexture wrapper parallel to each buffer so we
    // don't re-import into RenderEngine every dequeue (cleared on realloc).
    std::shared_ptr<renderengine::ExternalTexture> mExternalTextures[kMaxBufferCount];
    BufferState mBufferState[kMaxBufferCount] = {FREE};
    int mCurrentBuffer = -1;

    // Rendering into a buffer Chrome still has attached rewrites the frame on
    // screen. dequeueBuffer avoids it when it can and counts it when it can't.
    uint32_t mDequeueCount = 0;
    uint32_t mHeldSkipCount = 0;      // moved off a busy slot — the fix working
    uint32_t mOverwriteCount = 0;     // no safe slot left — visible flicker
    nsecs_t mStatsNextLogNs = 0;
    void logDequeueStats();

    // Previous frame's checksum, for the repeat marker in the hash log.
    uint64_t mLastFrameHash = 0;

    sp<Fence> mAcquireFence;

    // See lastQueuedTexture(). Holds its own reference so a pool reallocation
    // cannot free the frame between queueBuffer and the mask being built.
    std::shared_ptr<renderengine::ExternalTexture> mLastQueuedTexture;
    ui::Size mLastQueuedSize;

    uint32_t mPageFlipCount = 0;

    // HWC routing — no Wayland objects
    HWComposer& mHwComposer;
    renderengine::RenderEngine& mRenderEngine;
    DisplayId mDisplayId;
    int32_t mTaskId = 0;  // logging only
};

} // namespace android

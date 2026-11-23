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

#include <atomic>
#include <memory>
#include <mutex>
#include <unordered_map>
#include <vector>

#include <ui/GraphicBuffer.h>
#include <utils/StrongPointer.h>
#include <unistd.h>

struct wl_buffer;
struct wl_display;
struct zwp_linux_dmabuf_v1;

namespace android {

// Ref-counted release fence shared by all buffers presented in a single frame.
// Uses a sw_sync timeline: when all pending counts reach 0, the timeline is
// incremented, signaling the sync fence that SF holds for this frame.
struct ReleaseFence {
    int timelineFd;            // sw_sync timeline — incremented to signal
    std::atomic<int> pending;  // count of unreleased buffers this frame

    explicit ReleaseFence(int timeline) : timelineFd(timeline), pending(0) {}
    ~ReleaseFence() { if (timelineFd >= 0) close(timelineFd); }

    // Signal the fence by incrementing the timeline past the fence point.
    void signal();

    ReleaseFence(const ReleaseFence&) = delete;
    ReleaseFence& operator=(const ReleaseFence&) = delete;
};

// Per-wl_buffer tracking: the Wayland buffer and its current release fence.
// Holds a strong reference to the GraphicBuffer while Chrome is displaying it
// to prevent VirtualDisplaySurface from reclaiming and overwriting the DMA-BUF.
struct BufferEntry {
    wl_buffer* buffer = nullptr;
    // entryMutex guards both the GraphicBuffer pin and the release fence: the
    // pin is set on the SF composition thread (getOrCreateBuffer) but cleared on
    // the Wayland event thread (bufferRelease), so the sp<> assignment must be
    // synchronized or libutils' RefBase race detector aborts the process.
    std::mutex entryMutex;
    sp<GraphicBuffer> graphicBuffer;               // pin while in use; guarded by entryMutex
    std::shared_ptr<ReleaseFence> currentFence;    // guarded by entryMutex
    // True from the commit that attached this wl_buffer until the compositor
    // sends wl_buffer.release. Chrome is sampling this DMA-BUF for as long as
    // it is set, so nothing may render into it. Set on the SF composition
    // thread (markPresented), cleared on the Wayland event thread
    // (bufferRelease) — hence atomic rather than under entryMutex, which the
    // reader (TaskRenderSurface::dequeueBuffer, per frame per window) must not
    // contend on.
    std::atomic<bool> heldByCompositor{false};
};

// Shared DMA-BUF buffer cache: imports GraphicBuffers into Wayland via
// zwp_linux_dmabuf_v1 and caches wl_buffer objects for reuse.
//
// SurfaceFlinger rotates through a small set of GraphicBuffers per layer,
// so caching avoids re-importing the same DMA-BUF every frame.
//
// Also manages per-frame release fences: one sw_sync timeline per frame,
// shared by all presented buffers. Signaled when all release events fire.
class WaylandLayerDrmBufferManager {
public:
    WaylandLayerDrmBufferManager(zwp_linux_dmabuf_v1* dmabuf, wl_display* display);
    ~WaylandLayerDrmBufferManager();

    WaylandLayerDrmBufferManager(const WaylandLayerDrmBufferManager&) = delete;
    WaylandLayerDrmBufferManager& operator=(const WaylandLayerDrmBufferManager&) = delete;

    // Get or create a wl_buffer for the given GraphicBuffer via DMA-BUF import.
    //
    // Normally one wl_buffer is created per GraphicBuffer and reused for the
    // life of the buffer, which is what you want: the import is expensive and
    // the compositor reads the DMA-BUF's current contents on every commit.
    //
    // That last assumption does not hold on a guest where the compositor's GPU
    // cannot read guest memory directly. There the import snapshots the buffer
    // host-side and later CPU writes into the same pages are never picked up, so
    // a cached wl_buffer replays whatever it held at import time — with a pool
    // of N slots the picture cycles through N frames forever. Setting
    // persist.fydroid.sf.wl_buffer_per_frame=1 imports a fresh wl_buffer for
    // every present, which forces the compositor to re-read the contents.
    wl_buffer* getOrCreateBuffer(const sp<GraphicBuffer>& buffer);

    // Create a new sw_sync fence for this frame. Returns a sync fence fd
    // that signals when all presented buffers are released by the compositor.
    // Returns -1 on failure (callers treat as "no fence").
    int beginFrame();

    // Associate a buffer with the current frame's fence (increment pending) and
    // mark it as held by the compositor.
    // Call after the commit that attached the buffer actually went out — a
    // buffer whose present was refused (frame-callback throttle, hidden window)
    // never reached Chrome and must NOT be marked, or it would be treated as
    // busy forever.
    void markPresented(uint64_t bufferId);

    // True while Chrome still holds this GraphicBuffer's wl_buffer: attached by
    // a commit and not yet released. SurfaceFlinger renders into these buffers
    // directly (TaskRenderSurface owns the pool, there is no BufferQueue to
    // block on), so this is the only back-pressure that keeps a frame from
    // being overwritten while the ChromeOS compositor is still sampling it.
    // Unknown buffers read false — never imported means never handed over.
    bool isHeldByCompositor(uint64_t bufferId) const;

    // Signal the frame fence immediately if no buffers were marked presented.
    // Call after all layers have been presented.
    void finishFrame();

private:
    // Destroy the wl_buffers of entries the compositor has released, keeping the
    // newest. Only ever called on the SF composition thread, and only for
    // entries whose release callback has fully run (heldByCompositor is cleared
    // last), so the Wayland thread can never be inside one being destroyed.
    void reapReleasedLocked(std::vector<std::unique_ptr<BufferEntry>>& entries);

    zwp_linux_dmabuf_v1* mDmabuf;  // not owned
    wl_display* mDisplay;           // not owned
    // Per GraphicBuffer id. Normally one entry; per-frame mode appends a new one
    // each present and reaps the released ones (see getOrCreateBuffer).
    std::unordered_map<uint64_t, std::vector<std::unique_ptr<BufferEntry>>> mBufferCache;
    std::shared_ptr<ReleaseFence> mFrameFence;  // current frame's fence
    bool mImportVerified = false;  // first create roundtripped to check for errors
};

} // namespace android

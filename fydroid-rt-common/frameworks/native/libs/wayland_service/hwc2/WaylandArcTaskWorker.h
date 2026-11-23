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

#include <condition_variable>
#include <cstdint>
#include <deque>
#include <functional>
#include <memory>
#include <mutex>
#include <thread>

namespace android {

// Runs the void-returning IArcTaskController calls (focus, move-to-display) on
// a dedicated thread.
//
// Why: those are synchronous binder calls into system_server, and their service
// side takes the WM global lock. system_server takes that same lock across
// WindowManagerService.relayoutWindow(), which calls back into SurfaceFlinger
// (createSurfaceControl → createSurface) and blocks there. If SurfaceFlinger's
// main thread is the one sitting in setFocusedTask, the two processes wait on
// each other, system_server ANRs and the watchdog restarts the session (see
// docs/surfaceflinger-arctaskcontroller-binder-deadlock.md).
//
// Moving the call to a thread that holds no SurfaceFlinger state breaks the
// cycle: system_server's createSurface is still answered while the focus call
// is outstanding.
//
// Requests are applied in post order. Focus requests that have not started yet
// coalesce into the newest one — focus is last-write-wins, so an entry/exit
// burst costs a single binder round trip.
class WaylandArcTaskWorker {
public:
    static WaylandArcTaskWorker& instance();

    // Request Android focus for `taskId`. Never blocks. The returned sequence
    // number can be handed to await() by callers that must not proceed until
    // the focus change (and its syncInputTransactions) has landed.
    uint64_t postFocus(int32_t taskId);

    // Fire-and-forget job, run on the worker thread in post order.
    void post(std::function<void()> job);

    // Block until the request identified by `seq` — or any newer request that
    // superseded it — has been applied. Returns false if `timeoutMs` elapsed
    // first. Must never be called from the SurfaceFlinger main thread.
    bool await(uint64_t seq, int64_t timeoutMs);

private:
    WaylandArcTaskWorker();

    struct Job {
        uint64_t seq;
        bool isFocus;
        int32_t taskId;            // isFocus only
        std::function<void()> fn;  // !isFocus only
    };

    void threadLoop();

    std::mutex mLock;
    std::condition_variable mWork;  // worker wakes on new jobs
    std::condition_variable mDone;  // await() waiters wake on completion
    std::deque<std::shared_ptr<Job>> mQueue;
    // The queued focus job that has not started yet, if any. Held so a newer
    // focus request can overwrite it in place instead of queueing behind it.
    std::shared_ptr<Job> mPendingFocus;
    uint64_t mSeq = 0;      // last assigned sequence number
    uint64_t mDoneSeq = 0;  // highest sequence number already applied
    std::thread mThread;
};

}  // namespace android

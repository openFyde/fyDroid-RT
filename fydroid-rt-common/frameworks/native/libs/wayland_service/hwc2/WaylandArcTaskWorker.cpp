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

#define LOG_TAG "WaylandArcTaskWorker"

#include "WaylandArcTaskWorker.h"

#include <pthread.h>

#include <cinttypes>

#include <log/log.h>

#include "WaylandTaskResizer.h"

namespace android {

WaylandArcTaskWorker& WaylandArcTaskWorker::instance() {
    // Static storage duration: the thread runs for the lifetime of the process
    // and is never joined.
    static WaylandArcTaskWorker* sInstance = new WaylandArcTaskWorker();
    return *sInstance;
}

WaylandArcTaskWorker::WaylandArcTaskWorker() {
    mThread = std::thread([this]() {
        pthread_setname_np(pthread_self(), "ArcTaskWorker");
        threadLoop();
    });
    mThread.detach();
}

uint64_t WaylandArcTaskWorker::postFocus(int32_t taskId) {
    uint64_t seq;
    {
        std::lock_guard<std::mutex> lock(mLock);
        seq = ++mSeq;
        if (mPendingFocus) {
            // Coalesce: keep the queue position, adopt the newer target and
            // sequence number so waiters on either request are satisfied when
            // this one completes.
            mPendingFocus->taskId = taskId;
            mPendingFocus->seq = seq;
        } else {
            auto job = std::make_shared<Job>();
            job->seq = seq;
            job->isFocus = true;
            job->taskId = taskId;
            mPendingFocus = job;
            mQueue.push_back(job);
        }
    }
    mWork.notify_one();
    return seq;
}

void WaylandArcTaskWorker::post(std::function<void()> fn) {
    if (!fn) return;
    {
        std::lock_guard<std::mutex> lock(mLock);
        auto job = std::make_shared<Job>();
        job->seq = ++mSeq;
        job->isFocus = false;
        job->taskId = -1;
        job->fn = std::move(fn);
        mQueue.push_back(job);
    }
    mWork.notify_one();
}

bool WaylandArcTaskWorker::await(uint64_t seq, int64_t timeoutMs) {
    std::unique_lock<std::mutex> lock(mLock);
    bool applied = mDone.wait_for(lock, std::chrono::milliseconds(timeoutMs),
                                  [this, seq]() { return mDoneSeq >= seq; });
    if (!applied) {
        ALOGW("timed out after %" PRId64 "ms waiting for arc task request %" PRIu64
              " (done=%" PRIu64 ")",
              timeoutMs, seq, mDoneSeq);
    }
    return applied;
}

void WaylandArcTaskWorker::threadLoop() {
    for (;;) {
        std::shared_ptr<Job> job;
        {
            std::unique_lock<std::mutex> lock(mLock);
            mWork.wait(lock, [this]() { return !mQueue.empty(); });
            job = mQueue.front();
            mQueue.pop_front();
            // Once dequeued the job is no longer coalescable, so its fields are
            // stable for the rest of this iteration and can be read unlocked.
            if (job == mPendingFocus) mPendingFocus.reset();
        }

        if (job->isFocus) {
            WaylandTaskResizer::setFocusedTaskBlocking(job->taskId);
        } else {
            job->fn();
        }

        {
            std::lock_guard<std::mutex> lock(mLock);
            // max(): a coalesced focus job can carry a sequence number higher
            // than a non-focus job queued after it.
            if (job->seq > mDoneSeq) mDoneSeq = job->seq;
        }
        mDone.notify_all();
    }
}

}  // namespace android

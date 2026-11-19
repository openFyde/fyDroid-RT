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

// InputBridge: Pipe writer for BridgeInputEvent structs.
// The pipe fd is provided by InputReaderArc via binder (transaction 29).
// Before the fd is set, writeEvent() drops events (logged once).

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "InputBridge"

#include "InputBridge.h"

#include <cerrno>
#include <cstring>
#include <unistd.h>

#include <log/log.h>

namespace android {

InputBridge::InputBridge() = default;

InputBridge::~InputBridge() {
    std::lock_guard<std::mutex> lock(mMutex);
    if (mFd >= 0) {
        ::close(mFd);
    }
}

void InputBridge::setFd(int fd) {
    std::lock_guard<std::mutex> lock(mMutex);
    if (mFd >= 0) {
        ALOGI("Closing old pipe fd %d", mFd);
        ::close(mFd);
    }
    mFd = fd;
    ALOGI("Pipe fd set: %d", mFd);
}

bool InputBridge::writeEvent(const BridgeInputEvent& event) {
    std::lock_guard<std::mutex> lock(mMutex);
    if (mFd < 0) {
        if (!mLoggedNoFd) {
            mLoggedNoFd = true;
            ALOGW("writeEvent: no pipe fd yet — dropping events");
        }
        return false;
    }

    ssize_t written = ::write(mFd, &event, sizeof(event));
    if (written < 0) {
        if (errno == EAGAIN || errno == EWOULDBLOCK) {
            // The reader (InputReaderArc) is not draining the pipe and it has
            // filled up. Every event from here on is dropped, and because this
            // branch used to be silent the input loss looked like it came from
            // nowhere. Log the transition and then rate-limit.
            mEagainCount++;
            if (mEagainCount == 1 || (mEagainCount % 100) == 0) {
                ALOGW("writeEvent: pipe full (EAGAIN), dropped %llu event(s) "
                      "— reader is not draining fd=%d",
                      static_cast<unsigned long long>(mEagainCount), mFd);
            }
            return false;
        }
        ALOGE("Failed to write input event: %s", strerror(errno));
        return false;
    }
    if (static_cast<size_t>(written) != sizeof(event)) {
        ALOGW("Partial write: %zd/%zu bytes", written, sizeof(event));
        return false;
    }
    return true;
}

} // namespace android

#pragma clang diagnostic pop

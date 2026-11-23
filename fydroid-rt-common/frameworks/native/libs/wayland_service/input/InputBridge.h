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

// InputBridge: Writes BridgeInputEvent structs to a pipe fd received via binder.
// InputReaderArc creates a pipe(), keeps the read end, and passes the write end
// to SurfaceFlinger's Wayland binder service via notifyInputBridgePipeOpening().

#pragma once

#include "BridgeInputEvent.h"
#include <cstdint>
#include <mutex>

namespace android {

class InputBridge {
public:
    InputBridge();
    ~InputBridge();

    InputBridge(const InputBridge&) = delete;
    InputBridge& operator=(const InputBridge&) = delete;

    // Set the pipe fd (received from InputReaderArc via binder).
    // Takes ownership. Thread-safe.
    void setFd(int fd);

    // Write a BridgeInputEvent to the pipe. Returns false if no fd set
    // or write fails.
    bool writeEvent(const BridgeInputEvent& event);

private:
    std::mutex mMutex;
    int mFd = -1;
    // Diagnostics for silently-dropped events (see writeEvent).
    uint64_t mEagainCount = 0;
    bool mLoggedNoFd = false;
};

} // namespace android

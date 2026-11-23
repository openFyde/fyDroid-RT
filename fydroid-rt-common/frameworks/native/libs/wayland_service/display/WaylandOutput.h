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

#include <cstdint>
#include <functional>

struct wl_output;

namespace android {

// Wraps a wl_output — owns the Wayland listener, stores mode/transform.
// Takes ownership of wl_output* (destroys in destructor).
class WaylandOutput {
public:
    using ChangeCallback = std::function<void()>;

    // Takes ownership of |output|. Optional callback fired on mode/geometry changes.
    WaylandOutput(wl_output* output, ChangeCallback onChange = nullptr);
    ~WaylandOutput();

    WaylandOutput(const WaylandOutput&) = delete;
    WaylandOutput& operator=(const WaylandOutput&) = delete;

    wl_output* wlOutput() const { return mOutput; }
    int32_t width() const { return mWidth; }
    int32_t height() const { return mHeight; }
    int32_t refreshMHz() const { return mRefreshMHz; }
    int32_t transform() const { return mTransform; }

    // Called by static wl_output listener functions
    void onMode(uint32_t flags, int32_t width, int32_t height, int32_t refresh);
    void onGeometryTransform(int32_t transform);

private:
    wl_output* mOutput;
    int32_t mWidth = 0;
    int32_t mHeight = 0;
    int32_t mRefreshMHz = 0;
    int32_t mTransform = 0;
    ChangeCallback mOnChange;
};

} // namespace android

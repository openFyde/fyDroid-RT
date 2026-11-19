// Copyright (C) 2024 Fyde Innovations
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Native C++ enum for arc.mojom.VideoPixelFormat, ABI-compatible with the
// original closed-source libarcbridge.so.

#ifndef ARC_VIDEO_PIXEL_FORMAT_H_
#define ARC_VIDEO_PIXEL_FORMAT_H_

#include <cstdint>

namespace arc {

// Values match arc.mojom.VideoPixelFormat and media::VideoPixelFormat.
enum VideoPixelFormat {
  PIXEL_FORMAT_UNKNOWN = 0,
  PIXEL_FORMAT_I420 = 1,
  PIXEL_FORMAT_YV12 = 2,
  PIXEL_FORMAT_NV12 = 6,
  PIXEL_FORMAT_NV21 = 7,
  PIXEL_FORMAT_ARGB = 10,
  PIXEL_FORMAT_ABGR = 27,
  PIXEL_FORMAT_XBGR = 28,
};

}  // namespace arc

#endif  // ARC_VIDEO_PIXEL_FORMAT_H_

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

// Native C++ type for arc.mojom.VideoFramePlane, ABI-compatible with the
// original closed-source libarcbridge.so.

#ifndef ARC_VIDEO_FRAME_PLANE_H_
#define ARC_VIDEO_FRAME_PLANE_H_

#include <cstdint>

namespace arc {

struct VideoFramePlane {
  int32_t offset;
  int32_t stride;

  VideoFramePlane();
  VideoFramePlane(int32_t off, int32_t st);
  ~VideoFramePlane();
};

}  // namespace arc

#endif  // ARC_VIDEO_FRAME_PLANE_H_

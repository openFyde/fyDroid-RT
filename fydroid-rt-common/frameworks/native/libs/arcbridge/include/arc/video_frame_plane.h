// Copyright 2024 The FydeOS Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

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

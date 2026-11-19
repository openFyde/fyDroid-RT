// Copyright 2024 The FydeOS Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

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

// Copyright 2024 The FydeOS Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "arc/video_frame_plane.h"

namespace arc {

VideoFramePlane::VideoFramePlane() : offset(0), stride(0) {}

VideoFramePlane::VideoFramePlane(int32_t off, int32_t st)
    : offset(off), stride(st) {}

VideoFramePlane::~VideoFramePlane() = default;

}  // namespace arc

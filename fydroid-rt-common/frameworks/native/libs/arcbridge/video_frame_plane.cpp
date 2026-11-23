// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

#include "arc/video_frame_plane.h"

namespace arc {

VideoFramePlane::VideoFramePlane() : offset(0), stride(0) {}

VideoFramePlane::VideoFramePlane(int32_t off, int32_t st)
    : offset(off), stride(st) {}

VideoFramePlane::~VideoFramePlane() = default;

}  // namespace arc

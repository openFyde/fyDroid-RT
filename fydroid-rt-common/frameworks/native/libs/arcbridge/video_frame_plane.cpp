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

#include "arc/video_frame_plane.h"

namespace arc {

VideoFramePlane::VideoFramePlane() : offset(0), stride(0) {}

VideoFramePlane::VideoFramePlane(int32_t off, int32_t st)
    : offset(off), stride(st) {}

VideoFramePlane::~VideoFramePlane() = default;

}  // namespace arc

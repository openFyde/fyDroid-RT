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

// StructTraits for mapping arc.mojom.VideoFramePlane to arc::VideoFramePlane.

#ifndef ARC_VIDEO_FRAME_PLANE_MOJOM_TRAITS_H_
#define ARC_VIDEO_FRAME_PLANE_MOJOM_TRAITS_H_

#include <mojo/public/cpp/bindings/struct_traits.h>

#include "arc/video_common.mojom-shared.h"
#include "arc/video_frame_plane.h"

namespace mojo {

template <>
struct StructTraits<arc::mojom::VideoFramePlaneDataView, arc::VideoFramePlane> {
  static int32_t offset(const arc::VideoFramePlane& input) {
    return input.offset;
  }

  static int32_t stride(const arc::VideoFramePlane& input) {
    return input.stride;
  }

  static bool Read(arc::mojom::VideoFramePlaneDataView data,
                   arc::VideoFramePlane* out) {
    out->offset = data.offset();
    out->stride = data.stride();
    return true;
  }
};

}  // namespace mojo

#endif  // ARC_VIDEO_FRAME_PLANE_MOJOM_TRAITS_H_

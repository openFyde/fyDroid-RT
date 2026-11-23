// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

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

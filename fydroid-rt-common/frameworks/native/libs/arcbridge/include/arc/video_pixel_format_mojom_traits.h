// Copyright 2024 The FydeOS Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// EnumTraits for mapping arc.mojom.VideoPixelFormat to arc::VideoPixelFormat.

#ifndef ARC_VIDEO_PIXEL_FORMAT_MOJOM_TRAITS_H_
#define ARC_VIDEO_PIXEL_FORMAT_MOJOM_TRAITS_H_

#include <mojo/public/cpp/bindings/enum_traits.h>

#include "arc/video_common.mojom-shared.h"
#include "arc/video_pixel_format.h"

namespace mojo {

template <>
struct EnumTraits<arc::mojom::VideoPixelFormat, arc::VideoPixelFormat> {
  static arc::mojom::VideoPixelFormat ToMojom(arc::VideoPixelFormat input) {
    return static_cast<arc::mojom::VideoPixelFormat>(input);
  }

  static bool FromMojom(arc::mojom::VideoPixelFormat input,
                        arc::VideoPixelFormat* output) {
    *output = static_cast<arc::VideoPixelFormat>(input);
    return true;
  }
};

}  // namespace mojo

#endif  // ARC_VIDEO_PIXEL_FORMAT_MOJOM_TRAITS_H_

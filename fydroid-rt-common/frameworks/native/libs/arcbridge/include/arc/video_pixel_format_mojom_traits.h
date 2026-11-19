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

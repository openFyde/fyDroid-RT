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

// StructTraits for mapping arc.mojom.Size to gfx::Size.

#ifndef ARC_GFX_SIZE_MOJOM_TRAITS_H_
#define ARC_GFX_SIZE_MOJOM_TRAITS_H_

#include <mojo/public/cpp/bindings/struct_traits.h>
#include <ui/gfx/geometry/size.h>

#include "arc/gfx.mojom-shared.h"

namespace mojo {

template <>
struct StructTraits<arc::mojom::SizeDataView, gfx::Size> {
  static int32_t width(const gfx::Size& input) {
    return input.width();
  }

  static int32_t height(const gfx::Size& input) {
    return input.height();
  }

  static bool Read(arc::mojom::SizeDataView data, gfx::Size* out) {
    out->SetSize(data.width(), data.height());
    return true;
  }
};

}  // namespace mojo

#endif  // ARC_GFX_SIZE_MOJOM_TRAITS_H_

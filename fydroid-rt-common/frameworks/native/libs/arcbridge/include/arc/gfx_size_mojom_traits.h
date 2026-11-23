// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

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

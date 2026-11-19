/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#include "WaylandSurfaceData.h"

#include <wayland-client.h>

namespace android {

// The data that answers for `surface`: its own, or its input target's.
static const WaylandSurfaceData* resolve(wl_surface* surface) {
    if (!surface) return nullptr;
    auto* sd = static_cast<const WaylandSurfaceData*>(wl_surface_get_user_data(surface));
    return (sd && sd->inputTarget) ? sd->inputTarget : sd;
}

int32_t WaylandSurfaceData::getTaskId(wl_surface* surface) {
    auto* sd = resolve(surface);
    return sd ? sd->taskId : 0;
}

WaylandLayerContainer* WaylandSurfaceData::getSurfaceContainer(wl_surface* surface) {
    auto* sd = resolve(surface);
    return sd ? sd->container : nullptr;
}

WaylandWindow* WaylandSurfaceData::getSurfaceWindow(wl_surface* surface) {
    auto* sd = resolve(surface);
    return sd ? sd->window : nullptr;
}

void WaylandSurfaceData::getInputOrigin(wl_surface* surface, double* x, double* y) {
    *x = 0;
    *y = 0;
    if (!surface) return;
    auto* sd = static_cast<const WaylandSurfaceData*>(wl_surface_get_user_data(surface));
    if (!sd || !sd->inputTarget) return;
    *x = sd->inputOriginX;
    *y = sd->inputOriginY;
}

} // namespace android

#pragma clang diagnostic pop

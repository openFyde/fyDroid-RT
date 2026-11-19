/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

// WaylandNotificationWindow: Per-notification rendered content surface.
// Uses zcr_notification_surface_v2 — Chrome matches the surface to the
// notification popup by the notification_key string.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandNotifWindow"

#include "WaylandNotificationWindow.h"

#include <inttypes.h>
#include <cmath>
#include <remote-shell-unstable-v2-client-protocol.h>
#include <aura-shell-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>
#include "WaylandAccessibilityId.h"

namespace android {

WaylandNotificationWindow::WaylandNotificationWindow(wl_compositor* compositor,
                                                     zcr_remote_shell_v2* remoteShell,
                                                     uint64_t layerId,
                                                     const std::string& notifKey,
                                                     float scaleFactor,
                                                     zaura_shell* auraShell)
    : mLayerId(layerId), mNotifKey(notifKey) {

    // The notification VD renders content at an integer density (scaleFactor),
    // so the buffer is scaleFactor× larger than its logical DIP size. Unlike
    // zcr_toast_surface_v2, the notification surface has no set_scale_factor —
    // the scale is carried by the standard wl_surface buffer_scale. Without
    // this, Chrome treats the buffer as 1× DIP and downscales it into the
    // message-center column, then re-rasterizes at the output DSF: a
    // downscale→upscale round-trip that halves the effective resolution
    // (blurry, "low density" notifications). Setting buffer_scale makes the
    // buffer map 1:1 with the physical pixels Chrome allocates for the popup.
    mBufferScale = scaleFactor > 0.0f ? static_cast<int32_t>(lroundf(scaleFactor)) : 1;
    if (mBufferScale < 1) mBufferScale = 1;

    mSurfaceData.layerId = layerId;
    mSurfaceData.window = this;

    ALOGI("WaylandNotificationWindow: creating for layer %" PRIu64 " key=%s",
          layerId, notifKey.c_str());

    mSurface = wl_compositor_create_surface(compositor);
    if (!mSurface) {
        ALOGE("failed to create wl_surface for notification layer %" PRIu64, layerId);
        return;
    }

    wl_surface_set_user_data(mSurface, &mSurfaceData);

    // The zaura_surface exists only to carry the accessibility window id, and
    // it is created before the role object below because that is the order exo
    // documents for aura state that must be in place when the role is assigned
    // (WaylandAppWindow does the same). It sends nothing on its own and takes
    // no listener: this window type has no zaura events to act on.
    if (auraShell) {
        mAuraSurface = zaura_shell_get_aura_surface(auraShell, mSurface);
        if (!mAuraSurface) {
            ALOGW("failed to create zaura_surface for layer %" PRIu64
                  "; this window will not be readable by a screen "
                  "reader", layerId);
        }
    }

    // Buffer is rendered at mBufferScale× density — tell Chrome so it maps the
    // buffer 1:1 to physical pixels instead of downscaling it as 1× DIP.
    if (mBufferScale > 1) {
        wl_surface_set_buffer_scale(mSurface, mBufferScale);
    }

    mNotifSurface = zcr_remote_shell_v2_get_notification_surface(
            remoteShell, mSurface, notifKey.c_str());
    if (!mNotifSurface) {
        ALOGE("failed to create zcr_notification_surface_v2 for layer %" PRIu64
              " key=%s", layerId, notifKey.c_str());
        wl_surface_destroy(mSurface);
        mSurface = nullptr;
        return;
    }

    // Extract package name from notification key format:
    // "userId|packageName|notifId|tag|uid"
    std::string appId = "org.chromium.guest_os.arc";
    size_t first = notifKey.find('|');
    if (first != std::string::npos) {
        size_t second = notifKey.find('|', first + 1);
        if (second != std::string::npos) {
            std::string pkg = notifKey.substr(first + 1, second - first - 1);
            if (!pkg.empty()) {
                appId = "org.chromium.guest_os.arc." + pkg;
            }
        }
    }
    zcr_notification_surface_v2_set_app_id(mNotifSurface, appId.c_str());
    ALOGI("WaylandNotificationWindow: set_app_id=%s", appId.c_str());

    // Commit initial state
    wl_surface_commit(mSurface);

    ALOGI("WaylandNotificationWindow: created layer %" PRIu64 " key=%s",
          layerId, notifKey.c_str());
}

WaylandNotificationWindow::~WaylandNotificationWindow() {
    ALOGI("WaylandNotificationWindow: destroying layer %" PRIu64 " key=%s",
          mLayerId, mNotifKey.c_str());
    if (mAuraSurface) {
        zaura_surface_destroy(mAuraSurface);
    }
    if (mNotifSurface) {
        zcr_notification_surface_v2_destroy(mNotifSurface);
    }
    if (mSurface) {
        wl_surface_destroy(mSurface);
    }
}

void WaylandNotificationWindow::setBoundsInOutput(wl_output* /*output*/,
                                                   int32_t x, int32_t y,
                                                   int32_t w, int32_t h) {
    // zcr_notification_surface_v2 has no set_bounds_in_output —
    // Chrome controls the position within the notification panel.
    // Just track bounds locally.
    mBoundsX = x;
    mBoundsY = y;
    mBoundsWidth = w;
    mBoundsHeight = h;
}


void WaylandNotificationWindow::setAccessibilityId(int32_t id) {
    publishAccessibilityId(mAuraSurface, id, mAccessibilityId, "notif",
                           mLayerId);
}

} // namespace android

#pragma clang diagnostic pop

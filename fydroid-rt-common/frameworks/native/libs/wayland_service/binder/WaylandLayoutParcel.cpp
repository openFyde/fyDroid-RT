/*
 * Copyright (C) 2024 The FydeOS Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"

#define LOG_TAG "WaylandLayoutParcel"

#include "WaylandLayoutParcel.h"
#include "../WaylandGlobals.h"
#include "../display/WaylandDisplay.h"

#include <cutils/properties.h>
#include <gui/SurfaceComposerClient.h>
#include <inttypes.h>
#include <log/log.h>

namespace android {
namespace WaylandLayoutParcel {

// ro.sf.lcd_density, read once. Read-only properties cannot change for the
// lifetime of the process, and this is consumed inside the per-display loop of
// every layout push.
static int32_t lcdDensity() {
    static const int32_t sDensity = []() {
        char value[PROPERTY_VALUE_MAX] = {};
        property_get("ro.sf.lcd_density", value, "0");
        return static_cast<int32_t>(atoi(value));
    }();
    return sDensity;
}

// Helper: write a size-prefixed block matching Java Parcelable convention.
// Java's writeToParcel writes size = (endPos - startPos), i.e. the total byte
// count INCLUDING the 4-byte size field itself. readFromParcel checks
// (dataPosition - posBeforeSizeField) < size, so the size must include the
// size field or the last payload field will be skipped.
template <typename Fn>
static void writeSizePrefixed(Parcel* p, Fn fn) {
    size_t startPos = p->dataPosition();
    p->writeInt32(0);  // placeholder
    fn();
    size_t endPos = p->dataPosition();
    p->setDataPosition(startPos);
    p->writeInt32(static_cast<int32_t>(endPos - startPos));
    p->setDataPosition(endPos);
}

// Internal: writes the size-prefixed ArcDisplayLayout body into `p`.
// Caller is responsible for any header (writeNoException, non-null marker,
// interface token). Returns false if globals are empty.
static bool writeArcDisplayLayoutBody(Parcel* p, WaylandGlobals* globals) {
    if (!globals || globals->displayCount() == 0) {
        return false;
    }

    // Default device scale factor from zcr_remote_shell_v2 — Chrome's effective
    // scale including display zoom. Used for overrideDpi and defaultDeviceScaleFactor.
    float defaultDsf = globals->defaultDeviceScaleFactor();
    if (defaultDsf <= 0.0f) {
        // Fallback: use output manager's hardware scale
        auto* primary = globals->primaryDisplay();
        defaultDsf = (primary && primary->scaleFactor() > 0.0f)
                      ? primary->scaleFactor() : 1.0f;
    }
    int32_t layoutMode = static_cast<int32_t>(globals->layoutMode());
    int32_t numDisplays = static_cast<int32_t>(globals->displayCount());

    // Query physical display IDs from SurfaceFlinger for ArcDisplay parcel.
    auto physIds = SurfaceComposerClient::getPhysicalDisplayIds();
    // AOSP-13: PhysicalDisplayId is a struct wrapping a uint64_t (.value).
    int64_t primaryPhysId = physIds.empty() ? 0
                            : static_cast<int64_t>(physIds[0].value);

    ALOGI("writeArcDisplayLayout: %d display(s), defaultDsf=%.2f, layoutMode=%d, "
          "primaryPhysId=%" PRId64,
          numDisplays, defaultDsf, layoutMode, primaryPhysId);

    // ArcDisplayLayout (size-prefixed)
    writeSizePrefixed(p, [&]() {
        Parcel* reply = p;  // alias for the captured-in-lambda body below
        reply->writeInt32(layoutMode);  // layoutMode: 1=CLAMSHELL, 2=TABLET
        reply->writeInt64(primaryPhysId); // primaryPhysicalDisplayId

        // writeTypedArray: length + entries
        reply->writeInt32(numDisplays);

        for (int32_t i = 0; i < numDisplays; i++) {
            auto* d = globals->display(static_cast<size_t>(i));
            if (!d || d->width() == 0 || d->height() == 0) {
                reply->writeInt32(0);   // null marker — skip
                continue;
            }

            // Use matching SF physical display ID for this display index
            int64_t physId = (static_cast<size_t>(i) < physIds.size())
                             ? static_cast<int64_t>(physIds[i].value)
                             : static_cast<int64_t>(i);

            int32_t w = d->width();
            int32_t h = d->height();
            // Android's display rotation is how far the device is turned from
            // its natural orientation, and natural here is the physical panel
            // (see WaylandDisplay::width()). That is simply the user's display
            // rotation plus the panel's fixed mount angle:
            //
            //     rotation = logical_transform + panel mount
            //
            // This used to be computed as the inverse of panel_transform alone
            // (WL 0→ROT_0, 1→ROT_270, 2→ROT_180, 3→ROT_90). Since exo sends
            // panel_transform = OutputTransform(display.rotation() + mount),
            // that is a *reflection* of the rotation rather than the rotation,
            // and it only coincides with the truth when logical_transform is
            // even. On the Fydetab Duo (mount 90°) it therefore reported ROT_0
            // where the device was really at ROT_180, and ROT_180 where it was
            // at ROT_0.
            //
            // Nothing in the display path noticed: ROT_0/ROT_180 (and
            // ROT_90/ROT_270) have identical dimensions so layout is
            // unaffected, on-screen pixel orientation comes from Chrome's own
            // panel_transform, and input maps coordinates from
            // displayTransform() rather than from this value. The camera did
            // notice — the sensor is bolted to the device, so apps computing
            // (SENSOR_ORIENTATION - Display.getRotation()) had the preview
            // upside down in exactly the two orientations where the old
            // mapping disagreed.
            int32_t transform;
            const int32_t lt = d->logicalTransform();
            const int32_t pt = d->panelTransform();
            if (lt >= 0 && lt <= 3 && pt >= 0 && pt <= 3) {
                transform = (lt + d->panelBaseRotationDegrees() / 90) % 4;
            } else if (pt >= 0 && pt <= 3) {
                // Mount angle not derivable without both transforms; fall back
                // to the old inverse, which is right at the panel-native and
                // half-turn positions.
                static const int32_t kPanelToAndroid[] = {0, 3, 2, 1};
                transform = kPanelToAndroid[pt];
            } else {
                // panel_transform not yet received from output_manager_v2.
                transform = d->displayTransform();
            }
            float scale = d->scaleFactor();
            if (scale <= 0.0f) scale = 1.0f;

            // dpi = ro.sf.lcd_density (base Android density). A read-only
            // property, so read it once rather than on every layout push —
            // this runs per display, and a layout push happens on every
            // display/layout-mode change.
            int32_t dpi = lcdDensity();
            if (dpi <= 0) dpi = static_cast<int32_t>(scale * 160);

            // overrideDpi: single source of truth in WaylandDisplay —
            // Chrome's DSF-equivalent density × the touch bonus.
            int32_t overrideDpi = d->overrideDpi();
            if (overrideDpi <= 0) {
                overrideDpi = static_cast<int32_t>(
                        defaultDsf * 160.0f * WaylandDisplay::kDensityTouchScale);
            }

            int32_t insetsLeft = d->workAreaLeft();
            int32_t insetsTop = d->workAreaTop();
            int32_t insetsRight = d->workAreaRight();
            int32_t insetsBottom = d->workAreaBottom();

            ALOGI("  display[%d]: %dx%d transform=%d scale=%.2f "
                  "dpi=%d overrideDpi=%d insets=(%d,%d,%d,%d)",
                  i, w, h, transform, scale, dpi, overrideDpi,
                  insetsLeft, insetsTop, insetsRight, insetsBottom);

            reply->writeInt32(1);       // non-null marker

            // ArcDisplay (size-prefixed)
            writeSizePrefixed(reply, [&]() {
                reply->writeInt64(physId); // physicalDisplayId
                reply->writeInt32(0);   // globalPositionX
                reply->writeInt32(0);   // globalPositionY
                reply->writeInt32(w);
                reply->writeInt32(h);
                reply->writeInt32(transform);
                // insets (left, top, right, bottom)
                reply->writeInt32(insetsLeft);
                reply->writeInt32(insetsTop);
                reply->writeInt32(insetsRight);
                reply->writeInt32(insetsBottom);
                // stable insets (left, top, right, bottom)
                reply->writeInt32(insetsLeft);
                reply->writeInt32(insetsTop);
                reply->writeInt32(insetsRight);
                reply->writeInt32(insetsBottom);
                reply->writeInt32(0);   // autoHide: false
                reply->writeFloat(scale);
                reply->writeInt32(dpi);
                reply->writeInt32(overrideDpi);
                reply->writeInt32(0);   // displayRemapped: false
            });
        }

        reply->writeFloat(defaultDsf); // defaultDeviceScaleFactor
    });

    return true;
}

void writeDisplayLayout(Parcel* reply, WaylandGlobals* globals) {
    reply->writeNoException();
    writeArcDisplayLayout(reply, globals);
}

bool writeArcDisplayLayout(Parcel* p, WaylandGlobals* globals) {
    if (!globals || globals->displayCount() == 0) {
        p->writeInt32(0);  // null Parcelable marker
        return false;
    }
    p->writeInt32(1);  // non-null Parcelable marker
    return writeArcDisplayLayoutBody(p, globals);
}

} // namespace WaylandLayoutParcel
} // namespace android

#pragma clang diagnostic pop

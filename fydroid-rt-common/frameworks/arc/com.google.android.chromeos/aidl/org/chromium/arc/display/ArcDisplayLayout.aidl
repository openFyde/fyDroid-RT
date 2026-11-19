package org.chromium.arc.display;

import org.chromium.arc.display.ArcDisplay;

/**
 * Snapshot of the Chrome OS display layout pushed from wayland_service.
 *
 * layoutMode: 1 = WINDOWED (clamshell), 2 = TABLET (matches LayoutMode).
 *
 * Field order matches the proprietary org.chromium.arc.display.ArcDisplayLayout
 * stable AIDL parcelable bit-for-bit.
 */
parcelable ArcDisplayLayout {
    int layoutMode;
    long primaryPhysicalDisplayId;
    ArcDisplay[] displays;
    float defaultDeviceScaleFactor;
}

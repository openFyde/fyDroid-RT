/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

#pragma once

#include <cstdint>

namespace android {

// Chrome OS layout mode — wire values of the zcr_remote_shell_v2 /
// zaura_shell `layout_mode` events and of ArcDisplayLayout.layoutMode
// (numeric values are protocol contract; do not renumber).
enum class ChromeLayoutMode : int32_t {
    kUnknown = 0,    // not yet received / not yet applied to a window
    kClamshell = 1,  // windowed desktop (freeform)
    kTablet = 2,     // maximized touch UI (fullscreen)
};

inline const char* toString(ChromeLayoutMode mode) {
    switch (mode) {
        case ChromeLayoutMode::kClamshell: return "CLAMSHELL";
        case ChromeLayoutMode::kTablet: return "TABLET";
        default: return "UNKNOWN";
    }
}

// Classification of Android window types for Wayland surface dispatch.
enum class WaylandWindowCategory {
    APP,            // TYPE_APPLICATION range (1-99) + sub-windows (1000-1999)
    MODAL,          // Dialog task (IsDialogTask) - frameless WaylandModalWindow
    INPUT_METHOD,   // TYPE_INPUT_METHOD (2011)
    TOAST,          // TYPE_TOAST (2005), also status bar (2000) as buffer sink
    OVERLAY,        // TYPE_APPLICATION_OVERLAY (2038) - modal overlay window
    NOTIFICATION,   // Per-notification rendered content - zcr_notification_surface_v2
    SKIP,           // System UI that can be skipped (nav bar, etc.)
};

// Maps Android window type to the Wayland surface category.
// See android.view.WindowManager.LayoutParams for type constants.
inline WaylandWindowCategory classifyWindowType(uint32_t windowType) {
    // Application windows (1-99) and sub-windows (1000-1999)
    if (windowType <= 99 || (windowType >= 1000 && windowType <= 1999))
        return WaylandWindowCategory::APP;

    // System windows (2000-2999)
    if (windowType == 2011) return WaylandWindowCategory::INPUT_METHOD;
    if (windowType == 2005) return WaylandWindowCategory::TOAST;
    if (windowType == 2038) return WaylandWindowCategory::OVERLAY;

    // System modal dialogs — TYPE_SYSTEM_ALERT (2003), TYPE_SYSTEM_DIALOG
    // (2008) and TYPE_SYSTEM_ERROR (2010). 2003 is what
    // android.app.BaseErrorDialog gives the ANR ("isn't responding") and crash
    // ("has stopped") dialogs; 2008 and 2010 are the sibling system/error
    // dialog types. system_server owns all three, so they have no task and no
    // activity.
    //
    // These MUST NOT fall through to SKIP. SKIP is returned but handled
    // nowhere (it is the only occurrence of the enumerator in the tree), so no
    // standalone container and no Wayland surface is ever created and the
    // dialog is never presented on the ChromeOS desktop — while Android keeps
    // it entirely alive: an ANR dialog is focusable, sits at mBaseLayer=121000
    // (above every app layer at 21000) and is TOUCH-MODAL, so InputDispatcher
    // gives it a touchable region of the whole display expanded outward
    // (measured: [-1366,-768][2732,1536] on a 1366x768 panel). Every ARC task
    // sits at (0,0) on display 0, so it is topmost over all of them and claims
    // every MotionEvent.
    //
    // The result was that a single ANR silently and permanently locked ALL
    // Android input, with no way for the user to dismiss the dialog because it
    // was never drawn — recoverable only by adb/ssh or a reboot
    // ("Android apps intermittently become unclickable").
    //
    // MODAL is the right category rather than OVERLAY: WaylandModalWindow
    // creates its surface with CONTAINER_DEFAULT + FRAME_TYPE_SHADOW, which is
    // what a system dialog wants (no caption, Chrome's own scrim, centered by
    // the standalone-MODAL present branch). OVERLAY would put it in ash's
    // system modal container, where exo rejects every frame type but NONE from
    // the commit path and logs "A surface in system modal container should not
    // have a frame:2" once per frame.
    //
    // Note on 2010: it is deprecated, and an app targeting O+ that asks for it
    // is remapped by WM to TYPE_APPLICATION_OVERLAY (2038 -> OVERLAY above),
    // so what still arrives here is the system's own error window. A legacy
    // pre-O app holding SYSTEM_ALERT_WINDOW could in principle still reach
    // this line and would be centered rather than placed at its own bounds —
    // acceptable, because today such a window is invisible AND steals input.
    // Revisit if a real app is found relying on 2010 for a positioned overlay.
    if (windowType == 2003 || windowType == 2008 || windowType == 2010)
        return WaylandWindowCategory::MODAL;

    // TYPE_PHONE (2002) is what system_server's AppWarnings dialogs use:
    // DeprecatedTargetSdkVersionDialog, UnsupportedCompileSdkDialog and
    // UnsupportedDisplaySizeDialog. This build has
    // ro.build.version.min_supported_target_sdk=23, so launching any app that
    // targets SDK < 23 raises one. Left to SKIP it is the 2003 failure again:
    // never drawn, yet focused — and an app that waits for focus before its
    // first frame (NativeActivity samples, VkCube) never draws at all. Apps
    // targeting O+ cannot create TYPE_PHONE, so only system dialogs reach this.
    if (windowType == 2002) return WaylandWindowCategory::MODAL;

    // TYPE_STATUS_BAR_SUB_PANEL (2017): SystemUIDialog.applyFlags() gives every
    // SystemUIDialog this type on 13 too. One shown from an activity (the
    // MediaProjection prompt) is attached to that activity's task and carries
    // its TaskId, so it never reaches this classification; a task-less one
    // (volume safety warning, power warnings) does, and would be the same
    // invisible-but-modal window as 2003 if left to SKIP.
    if (windowType == 2017) return WaylandWindowCategory::MODAL;

    // TYPE_PRESENTATION (2037): an android.app.Presentation on a secondary
    // display (external HDMI customer display). It is a plain full-surface
    // window with no Chrome decoration; route it as a lightweight OVERLAY
    // (zcr_toast_surface_v2 + set_bounds_in_output + set_scale_factor). The
    // host output it lands on is chosen by which display's LayerManager the
    // container is registered on (targetOutputIndex), not by this category.
    if (windowType == 2037) return WaylandWindowCategory::OVERLAY;

    // Status bar (2000) needs a rendering surface so SystemUI can
    // render notifications. Without it, AlertWindowNotification
    // triggers "Width must be positive" crash in ArcSystemUI.
    // Uses TOAST (zcr_toast_surface_v2) as a lightweight buffer sink.
    //
    // In practice this no longer runs for the status bar: CompositionEngineArc
    // Pass 2 drops TYPE_STATUS_BAR before a standalone virtual display is
    // allocated, because presenting it put a stray Android status bar over the
    // ChromeOS shelf whenever SystemUI drew a frame into it. The mapping stays
    // for any path that does reach here with one, so the sink behaviour above
    // is what it gets rather than an unhandled category.
    if (windowType == 2000) return WaylandWindowCategory::TOAST;

    // TYPE_SYSTEM_OVERLAY (2006) — used for per-notification VirtualDisplay content.
    // Only reaches here when on a notification-keyed VirtualDisplay (handled by
    // WaylandHwc2Composer), but classify it for completeness.
    if (windowType == 2006) return WaylandWindowCategory::NOTIFICATION;

    // Everything else: nav bar (2019), system alert, etc.
    return WaylandWindowCategory::SKIP;
}

} // namespace android

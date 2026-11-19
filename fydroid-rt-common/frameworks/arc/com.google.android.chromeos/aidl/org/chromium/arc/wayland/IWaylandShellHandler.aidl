package org.chromium.arc.wayland;

import org.chromium.arc.display.ArcDisplayLayout;

/**
 * Callback interface registered by ArcSystemUI / arc-services with
 * wayland_service. wayland_service drives Android-side window state via
 * this binder (display layout, task lifecycle, drag, lock task, etc).
 *
 * One-way: every method matches the proprietary
 * org.chromium.arc.wayland.IWaylandShellHandler, which uses
 * Binder.transact(..., null, FLAG_ONEWAY).
 *
 * Method declaration order matches the proprietary transaction codes
 * (1..12) one-for-one.
 *
 * Bounds-change reasons used by taskBoundsChanged:
 *   1 DRAG_MOVE   2 DRAG_RESIZE   3 SNAP_TO_LEFT   4 SNAP_TO_RIGHT
 *   5 MOVE        6 RESIZE        7 PIP            8 FLOAT
 *
 * Occlusion-change reasons used by taskOcclusionChanged:
 *   1 USER_ACTION   2 PROGRAM_ACTION
 */
oneway interface IWaylandShellHandler {
    const int BOUNDS_CHANGE_REASON_DRAG_MOVE   = 1;
    const int BOUNDS_CHANGE_REASON_DRAG_RESIZE = 2;
    const int BOUNDS_CHANGE_REASON_SNAP_TO_LEFT  = 3;
    const int BOUNDS_CHANGE_REASON_SNAP_TO_RIGHT = 4;
    const int BOUNDS_CHANGE_REASON_MOVE   = 5;
    const int BOUNDS_CHANGE_REASON_RESIZE = 6;
    const int BOUNDS_CHANGE_REASON_PIP    = 7;
    const int BOUNDS_CHANGE_REASON_FLOAT  = 8;

    const int OCCLUSION_CHANGE_REASON_USER_ACTION    = 1;
    const int OCCLUSION_CHANGE_REASON_PROGRAM_ACTION = 2;

    // 1
    void removeTask(int taskId);
    // 2
    void setTaskWindowState(int taskId, int state);
    // 3
    void setDisplayLayout(in ArcDisplayLayout layout);
    // 4
    void moveTaskToFront(int taskId, boolean fromChrome);
    // 5
    void startLockTaskMode(int taskId, boolean trusted);
    // 6
    void stopLockTaskMode(int taskId);
    // 7
    void taskBoundsChanged(int taskId, long physicalDisplayId,
            int left, int top, int right, int bottom, int reason);
    // 8
    void taskDragStarted(int taskId, int ctrlType);
    // 9
    void taskDragFinished(int taskId, boolean canceled);
    // 10
    void taskOcclusionChanged(int taskId, float occlusionFraction, int reason);
    // 11
    void noTaskActivated();
    // 12
    void hideInputMethodIfDimmed();
}

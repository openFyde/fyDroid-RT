/*
 * Copyright (C) 2026 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */
package io.fydeos.arc.wm;

/**
 * FydeOS-owned task-control interface. Implemented by arc-services (system_server)
 * using the real ActivityTaskManager/WindowManager APIs; called by the native
 * wayland composer (SurfaceFlinger) via the generated C++ stub.
 *
 * This replaces WaylandTaskResizer's raw binder transacts to IActivityTaskManager
 * with hardcoded transaction codes — those codes are positional and shifted between
 * AOSP-11 and AOSP-13 (e.g. setFocusedTask's old code 29 hit reportAssistContextExtras
 * and NPE'd). Built with both java and cpp backends so the composer's BpArcTaskController
 * gets auto-generated, always-correct transaction codes.
 *
 * Only primitive (int/boolean) parameters are used so the cpp backend needs no
 * cross-language parcelable support (bounds are passed as 4 ints instead of a Rect).
 */
interface IArcTaskController {
    /** ActivityTaskManager.resizeTask(taskId, Rect(l,t,r,b), resizeMode). */
    boolean resizeTask(int taskId, int left, int top, int right, int bottom, int resizeMode);

    /** ActivityTaskManager.removeTask(taskId). */
    boolean removeTask(int taskId);

    /** ActivityTaskManager.moveRootTaskToDisplay(taskId, displayId). */
    void moveTaskToDisplay(int taskId, int displayId);

    /** ActivityTaskManager.setFocusedTask(taskId). */
    void setFocusedTask(int taskId);

    /**
     * Set the task's windowing mode (Task.setWindowingMode via the WM lock).
     * Replaces IActivityTaskManager.setTaskWindowingMode, removed in AOSP-13.
     */
    boolean setTaskWindowingMode(int taskId, int windowingMode, boolean toTop);

    /**
     * Whether the task's activity can actually be resized
     * (Task.isResizeable() && mResizeMode != RESIZE_MODE_UNRESIZEABLE).
     *
     * The composer advertises a window's resize limits to Chrome, and Chrome
     * decides from those whether the window may be snapped. Without this it
     * advertised every ARC window as freely resizable, so Chrome offered split
     * view even for activities that cannot take a half-screen bound.
     */
    boolean isTaskResizable(int taskId);

    /**
     * The activity's declared minimum size as {width, height} in px
     * (Task.mMinWidth/mMinHeight, from ActivityInfo.windowLayout), each -1 when
     * the activity declares none.
     *
     * Advertised to Chrome as the window's minimum, which is what Chrome
     * measures against half the screen when deciding whether a window may be
     * snapped — so an activity that genuinely needs more than half a screen
     * stops being offered split view.
     *
     * Deliberately the *declared* value, unlike
     * IArcWindowManager.getTaskMinWidthAndHeight(), which substitutes the
     * density-scaled mDefaultMinSizeOfResizeableTaskDp when nothing is
     * declared. The composer needs to tell "declares a minimum" apart from
     * "declares nothing": for the latter it advertises a 1x1 floor on purpose,
     * so Chrome's reported geometry always matches the buffer we render (see
     * WaylandAppWindow::setResizeLimits).
     */
    int[] getTaskMinSize(int taskId);

    // ---------------------------------------------------------------------
    // APPEND ONLY BELOW THIS LINE.
    //
    // AIDL assigns transaction codes AND C++ vtable slots by declaration
    // order, so inserting a method anywhere above shifts every method after
    // it. The composer (SurfaceFlinger) and the implementation (system_server)
    // are separate binaries deployed independently, and a single stale object
    // file on either side then calls through the wrong slot. That is not a
    // clean failure: it crashed SurfaceFlinger with SIGSEGV at pc=0x20 inside
    // getTaskMinSize — a jump through a vtable slot one past the end — on the
    // first app window, every time. Add new methods here, never in the middle.
    // ---------------------------------------------------------------------

    /**
     * Set a task's bounds directly (Task#setBounds), bypassing the windowing-mode
     * gate in ActivityTaskManager#resizeTask.
     *
     * That gate is WindowConfiguration#canResizeTask(), which is FREEFORM ||
     * MULTI_WINDOW only — so resizeTask refuses every WINDOWING_MODE_PINNED task
     * and logs "resizeTask not allowed". A PIP window's rectangle is owned by ash
     * and has to reach the Android task somehow, and upstream has the same
     * problem: the WM Shell sizes pinned tasks with a
     * WindowContainerTransaction#setBounds, which never consults canResizeTask()
     * either.
     *
     * Use this ONLY for tasks the normal path refuses (today: pinned/PIP).
     * Everything else must keep going through resizeTask, which does the
     * reparenting and resize-mode handling this deliberately skips.
     */
    boolean setTaskBoundsUnchecked(int taskId, int left, int top, int right, int bottom);

    /**
     * Whether any ARC window currently holds Chrome OS activation.
     *
     * Chrome may activate a window Android knows nothing about (a browser
     * window, a system dialog, the launcher). While false, ordinary app and
     * game activities report themselves unfocusable, so WM's own focus
     * computation moves focus off them — otherwise the last ARC window to hold
     * focus kept it forever, blinking a caret and holding its IME up after the
     * user had clicked away.
     *
     * Focus only: activities are never paused or hidden, their windows are
     * still on screen beside Chrome's, and background playback is untouched.
     * The ARC IME placeholder is exempt (android:alwaysFocusable), so Chrome
     * can still drive an Android IME for its own text fields.
     *
     * The compositor is the only side that sees the activation change
     * (zaura_shell.activated with a null gained surface), which is why this is
     * pushed in rather than observed here.
     */
    void setArcActivated(boolean activated);
}

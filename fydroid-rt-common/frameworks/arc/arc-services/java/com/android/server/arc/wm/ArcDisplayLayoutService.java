/*
 * Copyright (C) 2024 The fydroid-rt Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.server.arc.wm;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.WindowConfiguration;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.Display;
import android.view.IWindowManager;

import java.util.List;

import com.android.server.SystemService;
import com.android.server.wm.IArcWindowManager;

/**
 * Bridges Chrome OS display layout updates from wayland_service into Android.
 *
 * On the proprietary stack this lived in ArcSystemUI.apk
 * (org.chromium.arc.systemui.wm.ArcDisplayLayoutHandler). We host the same
 * logic in arc-services.jar so the open-source runtime does not depend on
 * the closed-source ArcSystemUI APK.
 *
 * Flow:
 *   1. onStart connects to the "Wayland" binder
 *      (org.chromium.arc.wayland.IWaylandService) — published from
 *      surfaceflinger via WaylandBinderService.
 *   2. We register an IWaylandShellHandler stub.
 *   3. wayland_service pushes setDisplayLayout(ArcDisplayLayout) when
 *      Chrome's layout mode flips between CLAMSHELL (1) and TABLET (2).
 *   4. We translate the layout mode into the corresponding display
 *      windowing mode (FREEFORM for clamshell, FULLSCREEN for tablet) and
 *      apply it via {@link IWindowManager#setWindowingMode}.
 *   5. We poke arc_window_manager to restart any size-compat activities
 *      that need to follow the new display configuration.
 *
 * IWaylandService and IWaylandShellHandler live in
 * com.google.android.chromeos.jar, which is NOT on system_server's class
 * path (only loaded as a {@code <uses-library>} for ordinary apps). We
 * therefore talk to wayland_service via raw {@link Binder#transact} and
 * implement the IWaylandShellHandler binder by extending {@link Binder}
 * directly. This keeps system_server free of any chromeos.jar
 * dependency.
 */
public class ArcDisplayLayoutService extends SystemService {
    private static final String TAG = "ArcDisplayLayoutService";
    private static final String WAYLAND_SERVICE_NAME = "Wayland";
    private static final String IWAYLAND_SERVICE_DESCRIPTOR =
            "org.chromium.arc.wayland.IWaylandService";
    private static final String IWAYLAND_SHELL_HANDLER_DESCRIPTOR =
            "org.chromium.arc.wayland.IWaylandShellHandler";

    // IWaylandService transaction codes (must match the proprietary
    // org.chromium.arc.wayland.IWaylandService stub).
    private static final int TX_REGISTER_WAYLAND_SHELL_HANDLER = 25;

    // IWaylandShellHandler transaction codes (1..12) — match the proprietary
    // AIDL declaration order.
    private static final int TX_SH_REMOVE_TASK = 1;
    private static final int TX_SH_SET_TASK_WINDOW_STATE = 2;
    private static final int TX_SH_SET_DISPLAY_LAYOUT = 3;
    private static final int TX_SH_MOVE_TASK_TO_FRONT = 4;
    private static final int TX_SH_START_LOCK_TASK_MODE = 5;
    private static final int TX_SH_STOP_LOCK_TASK_MODE = 6;
    private static final int TX_SH_TASK_BOUNDS_CHANGED = 7;
    private static final int TX_SH_TASK_DRAG_STARTED = 8;
    private static final int TX_SH_TASK_DRAG_FINISHED = 9;
    private static final int TX_SH_TASK_OCCLUSION_CHANGED = 10;
    private static final int TX_SH_NO_TASK_ACTIVATED = 11;
    private static final int TX_SH_HIDE_INPUT_METHOD_IF_DIMMED = 12;
    // fydroid-rt extension (not in the proprietary AIDL): after a drag
    // resize finishes, ask Java to translate the user's dragged bounds
    // into the next supported aspect ratio for an unresizable activity
    // and apply the resize. Resizable activities keep the requested
    // bounds verbatim. See ArcAspectRatioStepper.
    private static final int TX_SH_RESIZE_TASK_WITH_AR_STEP = 100;

    // wayland_service is published from surfaceflinger which usually beats
    // system_server, but we still defend against the race.
    private static final long REGISTER_RETRY_MS = 500;
    private static final int REGISTER_MAX_ATTEMPTS = 60;  // ~30 seconds

    // zaura_surface occlusion_reason: the user changed which window is active.
    // The other reason Chrome reports is a programmatic change, which must not
    // move an app into PIP.
    private static final int OCCLUSION_CHANGE_REASON_USER_ACTION = 1;

    /**
     * How covered a window must be before it is asked to enter PIP.
     *
     * 0.85 rather than 1.0 is Google's number, read out of a shipping ARCVM
     * image. It has to be below 1.0 because a covering window rarely hides
     * every pixel — rounded corners and shadows leave slivers — and it has to
     * be high enough that a window merely overlapped at the edge is left alone.
     */
    private static final float OCCLUSION_PIP_THRESHOLD = 0.85f;

    /**
     * Don't ask for PIP again for the same task inside this window.
     *
     * Sized to cover a PIP entry animation and the occlusion churn around it,
     * while still letting a genuine second switch-away act promptly.
     */
    private static final long PIP_REQUEST_COOLDOWN_MS = 3000;

    /** When we last asked for PIP per task, for the cooldown. */
    private final android.util.SparseArray<Long> mLastPipRequest =
            new android.util.SparseArray<>();

    /** Last occlusion fraction per task, for the rising-edge test. */
    private final android.util.SparseArray<Float> mOcclusion =
            new android.util.SparseArray<>();

    private final HandlerThread mThread;
    private final Handler mHandler;
    private int mLayoutMode = -1;  // unknown until first push
    private int mCurrentTransform = -1;  // unknown until first push
    private int mCurrentOverrideDpi = 0;
    private int mAttempts;
    private volatile boolean mBootCompleted;
    private int mPendingTransform = -1;  // deferred until boot phase 600
    private int mPendingOverrideDpi = 0;
    private int mPendingNativeDpi = 0;

    public ArcDisplayLayoutService(Context context) {
        super(context);
        mThread = new HandlerThread("ArcDisplayLayoutHandler");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    @Override
    public void onStart() {
        Slog.i(TAG, "Starting; will register IWaylandShellHandler");
        mHandler.post(this::tryRegister);
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == SystemService.PHASE_THIRD_PARTY_APPS_CAN_START) {
            mHandler.post(() -> {
                mBootCompleted = true;
                // Apply deferred rotation and density now that displays
                // are ready but before apps create visible windows.
                if (mPendingOverrideDpi > 0) {
                    Slog.i(TAG, "Phase 600; applying deferred overrideDpi="
                            + mPendingOverrideDpi);
                    applyDensity(mPendingOverrideDpi, mPendingNativeDpi);
                }
                if (mPendingTransform >= 0) {
                    Slog.i(TAG, "Phase 600; applying deferred transform="
                            + mPendingTransform);
                    applyTransform(mPendingTransform);
                }
            });
        }
    }

    private void tryRegister() {
        IBinder binder = ServiceManager.getService(WAYLAND_SERVICE_NAME);
        if (binder == null) {
            mAttempts++;
            if (mAttempts >= REGISTER_MAX_ATTEMPTS) {
                Slog.e(TAG, "Wayland binder service never appeared; giving up");
                return;
            }
            mHandler.postDelayed(this::tryRegister, REGISTER_RETRY_MS);
            return;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IWAYLAND_SERVICE_DESCRIPTOR);
            data.writeStrongBinder(mShellHandler);
            binder.transact(TX_REGISTER_WAYLAND_SHELL_HANDLER, data, reply, 0);
            reply.readException();
            Slog.i(TAG, "registerWaylandShellHandler succeeded");
        } catch (RemoteException e) {
            Slog.e(TAG, "registerWaylandShellHandler failed", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    private void onDisplayLayoutChanged(int layoutMode, int transform,
            int overrideDpi, int nativeDpi) {
        // --- 1. Layout mode (safe to apply early) ---
        if (layoutMode != mLayoutMode) {
            int prev = mLayoutMode;
            mLayoutMode = layoutMode;
            int windowingMode = (layoutMode == 2)
                    ? WindowConfiguration.WINDOWING_MODE_FULLSCREEN
                    : WindowConfiguration.WINDOWING_MODE_FREEFORM;
            Slog.i(TAG, "setDisplayLayout: layoutMode " + prev + " -> " + layoutMode
                    + " (windowingMode=" + windowingMode + ")");

            try {
                IWindowManager wm = IWindowManager.Stub.asInterface(
                        ServiceManager.getService("window"));
                if (wm != null) {
                    wm.setWindowingMode(0, windowingMode);
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "setWindowingMode failed", e);
            }

            try {
                IBinder awmBinder = ServiceManager.getService("arc_window_manager");
                if (awmBinder != null) {
                    IArcWindowManager.Stub.asInterface(awmBinder)
                            .restartSizeCompatActivitiesIfNeeded(0);
                }
            } catch (RemoteException e) {
                Slog.w(TAG, "restartSizeCompatActivitiesIfNeeded failed", e);
            }
        }

        // --- 2. Rotation & density — defer until boot phase 600 ---
        // Applying rotation during early boot crashes VirtualDisplayDevice
        // (nativeSetDisplaySurface fails on not-yet-ready notification surface).
        if (!mBootCompleted) {
            mPendingTransform = transform;
            mPendingOverrideDpi = overrideDpi;
            mPendingNativeDpi = nativeDpi;
            Slog.i(TAG, "setDisplayLayout: deferring transform=" + transform
                    + " overrideDpi=" + overrideDpi + " until boot phase 600");
            return;
        }

        applyDensity(overrideDpi, nativeDpi);
        applyTransform(transform);
    }

    private void applyTransform(int transform) {
        if (transform >= 0 && transform != mCurrentTransform) {
            Slog.i(TAG, "setDisplayLayout: transform " + mCurrentTransform
                    + " -> " + transform);
            mCurrentTransform = transform;
            try {
                IWindowManager wm = IWindowManager.Stub.asInterface(
                        ServiceManager.getService("window"));
                if (wm != null) {
                    wm.freezeDisplayRotation(0, transform);
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "freezeDisplayRotation failed", e);
            }
        }
    }

    private void applyDensity(int overrideDpi, int nativeDpi) {
        if (overrideDpi > 0 && overrideDpi != mCurrentOverrideDpi) {
            int prevDpi = mCurrentOverrideDpi;
            mCurrentOverrideDpi = overrideDpi;
            // Pass 1.0: apply the new density WITHOUT rescaling task bounds.
            //
            // processDisplayDensityChange multiplies freeform task bounds by this
            // factor, which is right for classic ARC++ where a task's pixels are
            // the window's pixels, so a density change implies a window resize.
            // It is wrong here. An ARC task is rasterized at the latched
            // mAndroidDsf (wayland WaylandLayerContainer), Chrome holds the
            // window at constant dip across a zoom, and SF scales source->target
            // — so the task's pixel size must NOT move with density.
            //
            // Rescaling here also never healed: the compositor only re-issues
            // resizeTask when geometry-derived bounds differ from
            // mLastResizedBounds, and a zoom leaves the dip geometry unchanged,
            // so the recomputed size matches what the compositor last set and no
            // correction is sent. The task simply kept the rescaled size and
            // drifted further on every zoom (observed: a 1312x1292 task down to
            // 1076x1059, 18% adrift, after a few zoom round-trips) while the
            // compositor still composed it as 1312x1292.
            float scale = 1.0f;
            Slog.i(TAG, "setDisplayLayout: overrideDpi " + prevDpi
                    + " -> " + overrideDpi + " (bounds unscaled)");
            try {
                IBinder awmBinder = ServiceManager.getService("arc_window_manager");
                if (awmBinder != null) {
                    IArcWindowManager.Stub.asInterface(awmBinder)
                            .processDisplayDensityChange(0, overrideDpi, scale);
                } else {
                    Slog.w(TAG, "arc_window_manager not available");
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "processDisplayDensityChange failed", e);
            }

            // setForcedDisplayDensityForUser above raises a display configuration
            // change, so ordinary activities are relaunched by the framework and
            // come back laid out for the new scale (and those declaring
            // configChanges=density adapt in place). Activities in size-compat
            // mode are the exception: the framework deliberately keeps them
            // frozen at their original density instead of relaunching, so they
            // would stay rendered at the pre-zoom scale until something else
            // restarted them. Kick those explicitly — the same thing the
            // layout-mode branch does for a tablet/clamshell switch.
            try {
                IBinder awmBinder = ServiceManager.getService("arc_window_manager");
                if (awmBinder != null) {
                    IArcWindowManager.Stub.asInterface(awmBinder)
                            .restartSizeCompatActivitiesIfNeeded(0);
                }
            } catch (RemoteException e) {
                Slog.w(TAG, "restartSizeCompatActivitiesIfNeeded failed", e);
            }
        }
    }

    /**
     * Binder implementing the IWaylandShellHandler interface without
     * referring to any class outside arc-services. wayland_service treats
     * the registered callback as a raw IBinder, so the only thing that
     * matters is the interface descriptor and the on-the-wire parcel
     * format.
     *
     * The interface is {@code oneway} on the proprietary side: replies
     * are not expected, so we just return true after handling each known
     * code.
     */
    private final Binder mShellHandler = new Binder() {
        {
            attachInterface(null, IWAYLAND_SHELL_HANDLER_DESCRIPTOR);
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(IWAYLAND_SHELL_HANDLER_DESCRIPTOR);
                return true;
            }
            switch (code) {
                case TX_SH_SET_DISPLAY_LAYOUT: {
                    data.enforceInterface(IWAYLAND_SHELL_HANDLER_DESCRIPTOR);
                    // Wire format for an in-parameter ArcDisplayLayout (stable AIDL parcelable):
                    //   int  hasLayout         (0 = null, 1 = present)
                    //   int  parcelableSize    (bytes of payload after this field)
                    //   int  layoutMode        (1 = CLAMSHELL, 2 = TABLET)
                    //   long primaryPhysicalDisplayId
                    //   ArcDisplay[] displays  (typed array, each element size-prefixed)
                    //   float defaultDeviceScaleFactor
                    int hasLayout = data.readInt();
                    if (hasLayout != 0) {
                        data.readInt();  // parcelableSize — discard
                        int layoutMode = data.readInt();
                        long primaryPhysicalDisplayId = data.readLong();

                        // Parse ArcDisplay[] to extract transform and overrideDpi
                        // from the primary display (first element).
                        int transform = -1;
                        int overrideDpi = 0;
                        int nativeDpi = 0;
                        int arrayLen = data.readInt();
                        for (int i = 0; i < arrayLen; i++) {
                            int nonNull = data.readInt();  // typed array null marker
                            if (nonNull == 0) continue;
                            int elemStart = data.dataPosition();
                            int elemSize = data.readInt();
                            if (i == 0) {
                                // ArcDisplay field order (stable AIDL):
                                //   long physicalDisplayId
                                //   int  globalPositionX, globalPositionY
                                //   int  widthPixels, heightPixels
                                //   int  transform
                                //   int  insetsLeft/Top/Right/Bottom    (4 ints)
                                //   int  stableInsetsLeft/Top/Right/Bottom (4 ints)
                                //   int  autoHide (boolean)
                                //   float deviceScaleFactor
                                //   int  dpi
                                //   int  overrideDpi
                                //   int  displayRemapped (boolean)
                                data.readLong();   // physicalDisplayId
                                data.readInt();    // globalPositionX
                                data.readInt();    // globalPositionY
                                data.readInt();    // widthPixels
                                data.readInt();    // heightPixels
                                transform = data.readInt();
                                // Skip 8 inset ints
                                data.setDataPosition(data.dataPosition() + 8 * 4);
                                data.readInt();    // autoHide
                                data.readFloat();  // deviceScaleFactor
                                nativeDpi = data.readInt();
                                overrideDpi = data.readInt();
                            }
                            // Advance past this element
                            data.setDataPosition(elemStart + elemSize);
                        }

                        final int fTransform = transform;
                        final int fOverrideDpi = overrideDpi;
                        final int fNativeDpi = nativeDpi;
                        mHandler.post(() -> onDisplayLayoutChanged(
                                layoutMode, fTransform, fOverrideDpi, fNativeDpi));
                    } else {
                        Slog.w(TAG, "setDisplayLayout: null layout");
                    }
                    return true;
                }
                case TX_SH_TASK_OCCLUSION_CHANGED: {
                    data.enforceInterface(IWAYLAND_SHELL_HANDLER_DESCRIPTOR);
                    // Wire format: int taskId, float fraction, int reason
                    final int taskId = data.readInt();
                    final float fraction = data.readFloat();
                    final int reason = data.readInt();
                    mHandler.post(() -> onTaskOcclusionChanged(taskId, fraction, reason));
                    return true;
                }
                case TX_SH_RESIZE_TASK_WITH_AR_STEP: {
                    data.enforceInterface(IWAYLAND_SHELL_HANDLER_DESCRIPTOR);
                    // Wire format:
                    //   int taskId
                    //   int dragX, dragY, dragW, dragH  (bounds the user dragged to)
                    //   int displayId
                    // Reply:
                    //   int ok
                    //   int appliedX, appliedY, appliedW, appliedH
                    final int taskId = data.readInt();
                    final int dx = data.readInt();
                    final int dy = data.readInt();
                    final int dw = data.readInt();
                    final int dh = data.readInt();
                    final int displayId = data.readInt();
                    Rect applied = new Rect(dx, dy, dx + dw, dy + dh);
                    boolean ok = false;
                    try {
                        Rect r = resizeTaskWithArStep(taskId, dx, dy, dw, dh, displayId);
                        if (r != null) {
                            applied = r;
                            ok = true;
                        }
                    } catch (Exception e) {
                        Slog.w(TAG, "resizeTaskWithArStep failed", e);
                    }
                    reply.writeNoException();
                    reply.writeInt(ok ? 1 : 0);
                    reply.writeInt(applied.left);
                    reply.writeInt(applied.top);
                    reply.writeInt(applied.width());
                    reply.writeInt(applied.height());
                    return true;
                }
                // All other oneway methods are unused for layout transitions.
                case TX_SH_REMOVE_TASK:
                case TX_SH_SET_TASK_WINDOW_STATE:
                case TX_SH_MOVE_TASK_TO_FRONT:
                case TX_SH_START_LOCK_TASK_MODE:
                case TX_SH_STOP_LOCK_TASK_MODE:
                case TX_SH_TASK_BOUNDS_CHANGED:
                case TX_SH_TASK_DRAG_STARTED:
                case TX_SH_TASK_DRAG_FINISHED:
                case TX_SH_NO_TASK_ACTIVATED:
                case TX_SH_HIDE_INPUT_METHOD_IF_DIMMED:
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    };

    /**
     * Auto-PIP: a window that has just been covered up drops into PIP.
     *
     * This is the trigger fydroid has to supply itself. On a phone, switching
     * apps PAUSES the previous activity and it gets {@code onUserLeaveHint()};
     * here every visible window stays resumed, so clicking another window
     * pauses nothing and that callback never fires. Measured with VLC: pressing
     * Home with VLC's own "switch to PiP" setting enabled produced no
     * onUserLeaveHint and no enterPictureInPictureMode — the video simply played
     * on. Without this path, NO app can reach PIP except by its own in-app
     * button. Google's ARC solved it the same way, and the thresholds below are
     * theirs, read out of a shipping ARCVM image.
     *
     * Deliberately thin: the real eligibility gates already live in
     * {@link IArcWindowManager#requestPictureInPictureMode} — already-pinned,
     * virtual display, {@code checkEnterPictureInPictureState}, and the Netflix
     * diversion. What can only be decided here is *when* to ask.
     */
    private void onTaskOcclusionChanged(int taskId, float fraction, int reason) {
        // Only an activation change — the user brought another window forward.
        // Chrome also reports occlusion for programmatic changes, and acting on
        // those would PIP an app because something else happened to open.
        if (reason != OCCLUSION_CHANGE_REASON_USER_ACTION) {
            return;
        }
        final float previous;
        synchronized (mOcclusion) {
            final Float prev = mOcclusion.get(taskId);
            previous = (prev != null) ? prev : 0f;
            mOcclusion.put(taskId, fraction);
        }
        if (fraction < OCCLUSION_PIP_THRESHOLD) {
            return;
        }
        // Rising edge only. Occlusion is re-reported on every activation change,
        // so without this an already-covered window would be asked to PIP again
        // on every click elsewhere — and after the user has expanded it back
        // out, it would be dragged straight into PIP a second time.
        if (previous > OCCLUSION_PIP_THRESHOLD) {
            return;
        }
        // Cooldown. Even with the rising-edge test, occlusion oscillates while a
        // PIP is entering — the covering window lands, the PIP window appears,
        // the source is briefly uncovered again — so the fraction can legitimately
        // cross the threshold several times a second. Measured on device in tablet
        // mode: 0.996 → 0.789 → 1.0 inside 15ms. One ask per task per interval is
        // plenty; the window is not going anywhere.
        final long now = android.os.SystemClock.uptimeMillis();
        synchronized (mOcclusion) {
            final Long last = mLastPipRequest.get(taskId);
            if (last != null && now - last < PIP_REQUEST_COOLDOWN_MS) {
                return;
            }
            mLastPipRequest.put(taskId, now);
        }
        Slog.i(TAG, "task " + taskId + " occluded " + fraction
                + " (was " + previous + ") — requesting PIP");
        try {
            final IArcWindowManager awm = IArcWindowManager.Stub.asInterface(
                    ServiceManager.getService("arc_window_manager"));
            if (awm == null) {
                Slog.w(TAG, "no arc_window_manager; cannot auto-PIP task " + taskId);
                return;
            }
            awm.requestPictureInPictureMode(taskId);
        } catch (Exception e) {
            Slog.w(TAG, "auto-PIP request failed for task " + taskId, e);
        }
    }

    /** Forget a task's occlusion history when it goes away. */
    void forgetTaskOcclusion(int taskId) {
        synchronized (mOcclusion) {
            mOcclusion.delete(taskId);
            mLastPipRequest.delete(taskId);
        }
    }

    /**
     * Apply ArcAspectRatioStepper to the user's drag-resize. Resizable
     * activities get the bounds they were dragged to. Unresizable
     * activities get bounds that snap to the closest supported aspect
     * ratio; ATMS sees the unresizable activity can't handle the config
     * change and relaunches it inside the new bounds.
     */
    private Rect resizeTaskWithArStep(int taskId, int dx, int dy,
            int dw, int dh, int displayId) throws RemoteException {
        final IActivityTaskManager atm = ActivityTaskManager.getService();
        if (atm == null) return null;

        // Find the topmost ActivityInfo for this task.
        ActivityInfo info = findTopActivityInfo(atm, taskId);
        if (info == null) {
            Slog.w(TAG, "resizeTaskWithArStep: no activity for taskId=" + taskId);
            return null;
        }

        Rect targetBounds = new Rect(dx, dy, dx + dw, dy + dh);

        if (!ActivityInfo.isResizeableMode(info.resizeMode)) {
            // Unresizable: pick next supported AR via the stepper.
            final Rect display = getDisplayBounds(displayId);
            if (display != null) {
                Rect stepped = ArcAspectRatioStepper.computeNextBounds(
                        info, targetBounds, display);
                if (stepped != null) {
                    targetBounds = stepped;
                    Slog.i(TAG, "resizeTaskWithArStep taskId=" + taskId
                            + " activity=" + info.name
                            + " dragged=(" + dx + "," + dy + " "
                            + dw + "x" + dh + ") -> stepped=" + targetBounds);
                }
            }
        }

        // Pass (0, 0, W, H) to ATMS — every container task is rendered
        // into its own SF virtual display, and InputDispatcher uses the
        // WMS frame to dispatch pointer events that arrive in
        // surface-local (0,0)-origin coords. A non-zero WMS origin
        // makes every click land outside the window frame and get
        // dropped (same root cause as patch 0027's launch-side fix,
        // which only covered centerBounds). The dragged position is
        // returned to the C++ side, which uses it for the matching
        // set_bounds_in_output → Chrome resizes its visible frame to
        // the snapped rect (and Chrome owns the on-screen position
        // anyway).
        // RESIZE_MODE_USER (1) — let ATMS handle the relaunch path for
        // unresizable activities.
        final Rect originBounds = new Rect(
                0, 0, targetBounds.width(), targetBounds.height());
        atm.resizeTask(taskId, originBounds, /*resizeMode=*/ 1);
        // WM may clamp the request (minimal task dimensions); report what it
        // applied so the caller awaits, and pushes to Chrome, the real size.
        final Rect applied = atm.getTaskBounds(taskId);
        if (applied != null && !applied.isEmpty()) {
            targetBounds.set(dx, dy, dx + applied.width(), dy + applied.height());
        }
        return targetBounds;
    }

    private static ActivityInfo findTopActivityInfo(IActivityTaskManager atm,
            int taskId) throws RemoteException {
        // Walk recent tasks looking for the one with our taskId, then
        // pull its topActivity ComponentName -> info. We can't go
        // directly because IActivityTaskManager doesn't expose
        // task->topActivity in AOSP 11 R.
        List<ActivityManager.RecentTaskInfo> recents = atm.getRecentTasks(
                /*maxNum=*/ 200, /*flags=*/ 0,
                /*userId=*/ android.os.UserHandle.USER_CURRENT).getList();
        for (ActivityManager.RecentTaskInfo r : recents) {
            if (r.taskId == taskId && r.topActivityInfo != null) {
                return r.topActivityInfo;
            }
        }
        return null;
    }

    private Rect getDisplayBounds(int displayId) {
        DisplayManager dm = getContext().getSystemService(DisplayManager.class);
        if (dm == null) return null;
        Display d = dm.getDisplay(displayId);
        if (d == null) return null;
        DisplayMetrics m = new DisplayMetrics();
        d.getRealMetrics(m);
        return new Rect(0, 0, m.widthPixels, m.heightPixels);
    }
}

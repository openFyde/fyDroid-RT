/*
 * Copyright (C) 2026 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
 */

package com.android.server.power;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import android.view.Display;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;

import com.android.server.input.InputManagerService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * fydroid: tells JobScheduler the device is idle while Chrome OS is awake but
 * nobody is using Android.
 *
 * JobScheduler only reaches "idle" after the screen has been off for
 * config_jobSchedulerInactivityIdleThreshold (31 min). Chrome OS turns the
 * display off 1 min before it suspends (AC 7m30s / 8m30s, battery 5m30s /
 * 6m30s), and suspend freezes the container, so that never happens: every
 * IDLE-constrained job starves. Play's auto-updates are such jobs, which left
 * Google Play services at the GApps pack's 2022 version and broke Google
 * sign-in in games.
 *
 * Idle here means: no input reached Android for {@link #INPUT_IDLE_MS} and the
 * whole device used less than {@link #CPU_IDLE_FRACTION} of its CPU over that
 * time (/proc/stat is the host's in the container). It is reported the way a
 * charging dock reports it — ACTION_DOCK_IDLE, then ACTION_TRIGGER_IDLE so the
 * tracker does not wait out the 31 min — and the next input ends it with
 * ACTION_DOCK_ACTIVE. Only Android's input counts: a user typing in a Chrome
 * window looks idle to Android, which is the point (jobs then run while
 * Android is not in use), and the CPU bound keeps it from starting while the
 * device is busy. Screen off is left to the tracker's own path.
 */
final class ArcIdleMonitor {
    private static final String TAG = "ArcIdleMonitor";

    static final long INPUT_IDLE_MS = 60_000;
    static final double CPU_IDLE_FRACTION = 0.10;
    private static final long CHECK_INTERVAL_MS = 15_000;
    // CPU samples one check apart, covering INPUT_IDLE_MS.
    private static final int CPU_SAMPLES = (int) (INPUT_IDLE_MS / CHECK_INTERVAL_MS) + 1;

    // ActivityManagerService.ACTION_TRIGGER_IDLE (am idle-maintenance).
    private static final String ACTION_TRIGGER_IDLE = "com.android.server.ACTION_TRIGGER_IDLE";

    private final Context mContext;
    private final Handler mHandler;
    private final long[] mCpuBusy = new long[CPU_SAMPLES];
    private final long[] mCpuTotal = new long[CPU_SAMPLES];
    private int mCpuSampleCount;
    private int mCpuSampleNext;

    // All on mHandler's thread.
    private InputEventReceiver mReceiver;
    private boolean mInteractive = true;
    private boolean mReportedIdle;
    private long mLastInputUptime;
    private double mLastCpuFraction = -1;

    private final Runnable mCheck = this::check;

    ArcIdleMonitor(Context context, Looper looper) {
        mContext = context;
        mHandler = new Handler(looper);
    }

    /** Starts watching. Call once the input service is up (boot completed). */
    void start() {
        mHandler.post(() -> {
            InputManagerService input =
                    (InputManagerService) ServiceManager.getService(Context.INPUT_SERVICE);
            // A global monitor gets every key and motion event the dispatcher
            // delivers on the display, whichever window it goes to.
            InputChannel channel = input.monitorInput(TAG, Display.DEFAULT_DISPLAY);
            mReceiver = new InputEventReceiver(channel, mHandler.getLooper()) {
                @Override
                public void onInputEvent(InputEvent event) {
                    onUserInput();
                    finishInputEvent(event, false);
                }
            };
            mLastInputUptime = SystemClock.uptimeMillis();
            mHandler.postDelayed(mCheck, CHECK_INTERVAL_MS);
            Slog.i(TAG, "watching for idle: no input for " + INPUT_IDLE_MS / 1000
                    + "s and CPU < " + (int) (CPU_IDLE_FRACTION * 100) + "%");
        });
    }

    /** Android's interactivity (Chrome OS display on/off) changed. */
    void setInteractive(boolean interactive) {
        mHandler.post(() -> {
            if (mInteractive == interactive) return;
            mInteractive = interactive;
            // SCREEN_ON / SCREEN_OFF reset the tracker's dock state, so ours too.
            mReportedIdle = false;
            mCpuSampleCount = 0;
            mLastInputUptime = SystemClock.uptimeMillis();
        });
    }

    private void onUserInput() {
        mLastInputUptime = SystemClock.uptimeMillis();
        if (!mReportedIdle) return;
        mReportedIdle = false;
        Slog.i(TAG, "user input: device active");
        sendDockBroadcast(Intent.ACTION_DOCK_ACTIVE);
    }

    private void check() {
        mHandler.postDelayed(mCheck, CHECK_INTERVAL_MS);
        sampleCpu();
        if (!mInteractive || mReportedIdle) return;
        if (SystemClock.uptimeMillis() - mLastInputUptime < INPUT_IDLE_MS) return;
        mLastCpuFraction = cpuFractionOverWindow();
        if (mLastCpuFraction < 0 || mLastCpuFraction >= CPU_IDLE_FRACTION) return;

        mReportedIdle = true;
        Slog.i(TAG, String.format("device idle: no input for %ds, CPU %.1f%%",
                (SystemClock.uptimeMillis() - mLastInputUptime) / 1000,
                mLastCpuFraction * 100));
        sendDockBroadcast(Intent.ACTION_DOCK_IDLE);
        Intent trigger = new Intent(ACTION_TRIGGER_IDLE)
                .setPackage("android")
                .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        mContext.sendBroadcastAsUser(trigger, UserHandle.ALL);
    }

    // As JobSchedulerService#triggerDockState sends them.
    private void sendDockBroadcast(String action) {
        Intent intent = new Intent(action)
                .setPackage("android")
                .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY
                        | Intent.FLAG_RECEIVER_FOREGROUND);
        mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sampleCpu() {
        long[] busyTotal = readCpuTimes();
        if (busyTotal == null) return;
        mCpuBusy[mCpuSampleNext] = busyTotal[0];
        mCpuTotal[mCpuSampleNext] = busyTotal[1];
        mCpuSampleNext = (mCpuSampleNext + 1) % CPU_SAMPLES;
        if (mCpuSampleCount < CPU_SAMPLES) mCpuSampleCount++;
    }

    /** Busy fraction between the oldest and newest sample; -1 until the window is full. */
    private double cpuFractionOverWindow() {
        if (mCpuSampleCount < CPU_SAMPLES) return -1;
        int newest = (mCpuSampleNext + CPU_SAMPLES - 1) % CPU_SAMPLES;
        int oldest = mCpuSampleNext;
        long total = mCpuTotal[newest] - mCpuTotal[oldest];
        if (total <= 0) return -1;
        return (double) (mCpuBusy[newest] - mCpuBusy[oldest]) / total;
    }

    /** {busy, total} jiffies of all CPUs from /proc/stat, or null. */
    private static long[] readCpuTimes() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"))) {
            String line = reader.readLine();
            if (line == null || !line.startsWith("cpu ")) return null;
            String[] fields = line.trim().split("\\s+");
            long total = 0;
            long idle = 0;
            // user nice system idle iowait irq softirq steal (guest is already in user).
            for (int i = 1; i <= 8 && i < fields.length; i++) {
                long value = Long.parseLong(fields[i]);
                total += value;
                if (i == 4 || i == 5) idle += value;
            }
            return new long[] {total - idle, total};
        } catch (IOException | NumberFormatException e) {
            Slog.w(TAG, "cannot read /proc/stat", e);
            return null;
        }
    }

    void dump(PrintWriter pw) {
        pw.println("ArcIdleMonitor:");
        pw.println("  interactive=" + mInteractive + " reportedIdle=" + mReportedIdle);
        pw.println("  sinceLastInput="
                + (SystemClock.uptimeMillis() - mLastInputUptime) / 1000 + "s"
                + " lastCpu=" + (mLastCpuFraction < 0 ? "n/a"
                        : String.format("%.1f%%", mLastCpuFraction * 100)));
    }
}

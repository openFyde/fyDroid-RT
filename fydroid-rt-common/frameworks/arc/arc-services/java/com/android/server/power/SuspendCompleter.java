package com.android.server.power;

import org.chromium.arc.mojom.PowerInstance;

final class SuspendCompleter {
    private boolean mActivityManagerWakeLockReleased = false;
    private boolean mDisplayOffComplete = false;
    private PowerInstance.SuspendResponse mSuspendReadyCallback;

    SuspendCompleter(PowerInstance.SuspendResponse suspendResponse) {
        if (suspendResponse == null) {
            throw new NullPointerException("Suspend response callback can't be null");
        }
        mSuspendReadyCallback = suspendResponse;
    }

    private void suspendIfReadyLocked() {
        if (mActivityManagerWakeLockReleased && mDisplayOffComplete) {
            mSuspendReadyCallback.call();
            mSuspendReadyCallback = null;
        }
    }

    synchronized void forceSuspend() {
        if (mSuspendReadyCallback == null) return;
        mSuspendReadyCallback.call();
        mSuspendReadyCallback = null;
        mActivityManagerWakeLockReleased = false;
        mDisplayOffComplete = false;
    }

    synchronized boolean isComplete() {
        return mSuspendReadyCallback == null;
    }

    synchronized void notifyActivityManagerSleepWakeLockReleased() {
        if (mSuspendReadyCallback == null) return;
        mActivityManagerWakeLockReleased = true;
        suspendIfReadyLocked();
    }

    synchronized void notifyDisplayOffComplete() {
        if (mSuspendReadyCallback == null) return;
        mDisplayOffComplete = true;
        suspendIfReadyLocked();
    }
}

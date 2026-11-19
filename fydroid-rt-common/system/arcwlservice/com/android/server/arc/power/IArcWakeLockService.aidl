// AIDL interface for the Android-side wake lock service.
// Package and descriptor confirmed from arcwlservice binary symbol:
//   IArcWakeLockService::descriptor @ BSS 0xa068
//   BnArcWakeLockService::onTransact dispatches acquirePartialWakeLock / releasePartialWakeLock
//
// Java PowerManager in system_server calls this Binder service (registered as
// "arc_wake_lock_service") when Android apps acquire/release PARTIAL_WAKE_LOCK.
package com.android.server.arc.power;

interface IArcWakeLockService {
    // Acquire a partial wake lock on Chrome OS. Returns true on success.
    boolean acquirePartialWakeLock();
    // Release the partial wake lock on Chrome OS. Returns true on success.
    boolean releasePartialWakeLock();
}

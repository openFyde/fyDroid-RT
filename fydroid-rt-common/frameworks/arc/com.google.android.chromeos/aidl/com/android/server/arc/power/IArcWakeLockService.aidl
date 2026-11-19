/* Open source replacement for com.android.server.arc.power.IArcWakeLockService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.power;

/**
 * Synchronous binder through which Android's PowerManager surrogate
 * acquires / releases the Chrome OS partial wake lock so the device
 * won't suspend while the ARC container needs CPU time.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads a reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..2:
 *   1 acquirePartialWakeLock
 *   2 releasePartialWakeLock
 */
interface IArcWakeLockService {
    // 1
    boolean acquirePartialWakeLock();
    // 2
    boolean releasePartialWakeLock();
}

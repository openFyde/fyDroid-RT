package com.android.server.screen_capture;

/**
 * Open source replacement for the proprietary
 * com.android.server.screen_capture.IArcScreenCaptureServiceCallback
 * binder interface from the ARC++ com.google.android.chromeos jar.
 *
 * All methods are one-way in the proprietary Stub/Proxy
 * (transact(..., null, FLAG_ONEWAY)), so the whole interface is oneway.
 *
 * Method declaration order matches the proprietary transaction codes
 * (1..3) one-for-one:
 *   1 bufferFinished
 *   2 forceUpdate
 *   3 sessionClosed
 */
oneway interface IArcScreenCaptureServiceCallback {
    // 1
    void bufferFinished(int sessionId);
    // 2
    void forceUpdate(int sessionId);
    // 3
    void sessionClosed(int sessionId);
}

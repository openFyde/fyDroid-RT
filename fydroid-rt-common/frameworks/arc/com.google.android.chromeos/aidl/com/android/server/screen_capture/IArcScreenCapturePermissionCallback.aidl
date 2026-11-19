package com.android.server.screen_capture;

/**
 * Open source replacement for the proprietary
 * com.android.server.screen_capture.IArcScreenCapturePermissionCallback
 * binder interface from the ARC++ com.google.android.chromeos jar.
 *
 * Single one-way method — the proprietary Proxy uses
 * transact(..., null, FLAG_ONEWAY).
 *
 * Transaction code 1 = permissionResult.
 */
oneway interface IArcScreenCapturePermissionCallback {
    // 1
    void permissionResult(boolean result);
}

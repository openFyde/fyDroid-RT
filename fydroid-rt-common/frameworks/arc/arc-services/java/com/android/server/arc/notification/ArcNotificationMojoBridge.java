package com.android.server.arc.notification;

import android.content.Context;
import android.util.Slog;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.NotificationsInstance;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;

/**
 * Manages the Mojo bridge lifecycle for the notification service.
 * Registers NotificationsInstance with ArcBridgeHost and handles reconnection.
 */
class ArcNotificationMojoBridge implements ArcService, ConnectionErrorHandler {
    private static final String TAG = "ArcNotifBridge";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    private final Context mContext;
    private final ArcNotificationService mService;
    private final NotificationsInstance mInstance;

    ArcNotificationMojoBridge(Context context, ArcNotificationService service,
            NotificationsInstance instance) {
        mContext = context;
        mService = service;
        mInstance = instance;
    }

    void start() {
        Slog.i(TAG, "Requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering NotificationsInstance");
        host.onNotificationsInstanceReady(mInstance);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- ConnectionErrorHandler ---

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mService.setHost(null);
        reRequestBridge();
    }

    private void reRequestBridge() {
        Slog.i(TAG, "Re-requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

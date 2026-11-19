package com.android.server.screen_capture;

import android.content.Context;
import android.os.Binder;
import android.util.Log;
import com.android.server.SystemService;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.ScreenCaptureHost;
import org.chromium.arc.mojom.ScreenCaptureInstance;
import org.chromium.mojo.system.MojoException;

/**
 * Screen capture service bridging to Chrome OS via Mojo ScreenCaptureHost.
 * Publishes "arc_screen_capture_service" binder.
 *
 * Note: Uses lowercase 'c' in Lifecycle to match proprietary class name.
 */
public final class ArcScreenCaptureService
        implements ScreenCaptureInstance, ArcService {
    private static final String TAG = "ArcScreenCapture";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 42;
    private final Context mContext;
    private ScreenCaptureHost mScreenCaptureHost;

    public static class Lifecycle extends SystemService {
        private final ArcScreenCaptureService mService;

        public Lifecycle(Context context) {
            super(context);
            mService = new ArcScreenCaptureService(context);
        }

        @Override
        public void onStart() {
            Log.i(TAG, "onStart");
            publishBinderService("arc_screen_capture_service", new Binder());
            mService.initialize();
        }
    }

    private ArcScreenCaptureService(Context context) {
        mContext = context;
    }

    void initialize() {
        Log.i(TAG, "Initializing, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- ScreenCaptureInstance ---

    @Override
    public void init(ScreenCaptureHost host, InitResponse response) {
        Log.i(TAG, "init: ScreenCaptureHost connected");
        mScreenCaptureHost = host;
        response.call();
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        mScreenCaptureHost = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        mScreenCaptureHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering instance");
        host.onScreenCaptureInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

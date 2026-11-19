package com.android.server.arc.boot_phase_monitor;

import android.content.Context;
import android.util.Log;
import com.android.server.SystemService;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.BootPhaseMonitorHost;
import org.chromium.arc.mojom.BootPhaseMonitorInstance;
import org.chromium.mojo.system.MojoException;

/**
 * Notifies Chrome OS that Android boot is complete via Mojo BootPhaseMonitorHost.
 */
class ArcBootPhaseMonitorService implements BootPhaseMonitorInstance, ArcService {
    private static final String TAG = "ArcBootPhaseMonitor";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 19;
    private final Context mContext;

    public static class LifeCycle extends SystemService {
        private final ArcBootPhaseMonitorService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcBootPhaseMonitorService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
        }
    }

    ArcBootPhaseMonitorService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Log.i(TAG, "Android boot completed, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- BootPhaseMonitorInstance ---

    @Override
    public void init(BootPhaseMonitorHost host, InitResponse response) {
        Log.i(TAG, "init: notifying Chrome that boot is complete");
        response.call();
        host.onBootCompleted();
        host.close();
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering instance");
        host.onBootPhaseMonitorInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

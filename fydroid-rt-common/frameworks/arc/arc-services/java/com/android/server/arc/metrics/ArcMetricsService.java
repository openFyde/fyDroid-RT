package com.android.server.arc.metrics;

import android.content.Context;
import android.os.Binder;
import android.util.Log;
import com.android.server.SystemService;
import java.util.function.BiConsumer;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.MetricsHost;
import org.chromium.arc.mojom.MetricsInstance;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;

/**
 * Metrics service bridging boot progress, ANR stats, memory PSI,
 * app kills, and other metrics to Chrome OS via Mojo MetricsHost.
 * Publishes "arc_metrics_service" binder for local use by other ARC services.
 */
public final class ArcMetricsService extends Binder
        implements MetricsInstance, ArcService, ConnectionErrorHandler {
    public static final String ARC_METRICS_SERVICE = "arc_metrics_service";
    private static final String TAG = "ArcMetrics";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 10;
    private final Context mContext;
    private MetricsHost mMetricsHost = null;
    private int mMetricsHostVersion = -1;

    public static class LifeCycle extends SystemService {
        private final ArcMetricsService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcMetricsService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
            publishBinderService(ARC_METRICS_SERVICE, mService, true);
        }
    }

    ArcMetricsService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- MetricsInstance ---

    @Override
    public void init(MetricsHost host, final InitResponse response) {
        Log.i(TAG, "init: setting up thread-safe metrics host proxy");
        ArcBridgeSupport.buildThreadSafeProxy(host, MetricsHost.MANAGER, this,
                new BiConsumer<MetricsHost, Integer>() {
                    @Override
                    public void accept(MetricsHost proxy, Integer version) {
                        mMetricsHost = proxy;
                        mMetricsHostVersion = version;
                        response.call();
                        Log.i(TAG, "MetricsHost ready, version=" + version);
                    }
                });
    }

    @Override
    public void getGfxMetrics(String packageName, GetGfxMetricsResponse response) {
        // GFX metrics require dumpsys gfxinfo parsing
        // Return null for now
        response.call(null);
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        mMetricsHost = null;
        mMetricsHostVersion = -1;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        mMetricsHost = null;
        mMetricsHostVersion = -1;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering instance");
        host.onMetricsInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

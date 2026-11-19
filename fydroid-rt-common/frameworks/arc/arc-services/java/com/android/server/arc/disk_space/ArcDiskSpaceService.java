package com.android.server.arc.disk_space;

import android.content.Context;
import android.os.Binder;
import android.util.Slog;
import com.android.server.SystemService;
import java.util.function.BiConsumer;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.DiskSpaceHost;
import org.chromium.arc.mojom.DiskSpaceInstance;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;

/**
 * Disk space service bridging quota queries to Chrome OS via Mojo DiskSpaceHost.
 * Publishes "arc_disk_space_service" binder.
 */
public final class ArcDiskSpaceService
        implements DiskSpaceInstance, ArcService, ConnectionErrorHandler {
    private static final String TAG = "ArcDiskSpace";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 19;
    private final Context mContext;
    private DiskSpaceHost mDiskSpaceHost;
    private int mDiskSpaceHostVersion = -1;

    public static class LifeCycle extends SystemService {
        public static final String BINDER_SERVICE_NAME = "arc_disk_space_service";
        private final ArcDiskSpaceService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcDiskSpaceService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
            publishBinderService(BINDER_SERVICE_NAME, new Binder());
        }
    }

    private ArcDiskSpaceService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Slog.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- DiskSpaceInstance ---

    @Override
    public void init(DiskSpaceHost host, final InitResponse response) {
        Slog.i(TAG, "init: setting up thread-safe disk space host proxy");
        ArcBridgeSupport.buildThreadSafeProxy(host, DiskSpaceHost.MANAGER, this,
                new BiConsumer<DiskSpaceHost, Integer>() {
                    @Override
                    public void accept(DiskSpaceHost proxy, Integer version) {
                        mDiskSpaceHost = proxy;
                        mDiskSpaceHostVersion = version;
                        response.call();
                        Slog.i(TAG, "DiskSpaceHost ready, version=" + version);
                    }
                });
    }

    @Override
    public void getApplicationsSize(GetApplicationsSizeResponse response) {
        // TODO: Use PackageManager to compute application sizes
        response.call(false, null);
    }

    @Override
    public void resizeStorageBalloon(long freeSpaceBytes) {
        Slog.i(TAG, "resizeStorageBalloon: " + freeSpaceBytes + " bytes");
        // Storage balloon is an ARCVM concept, no-op for container
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mDiskSpaceHost = null;
        mDiskSpaceHostVersion = -1;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mDiskSpaceHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering instance");
        host.onDiskSpaceInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

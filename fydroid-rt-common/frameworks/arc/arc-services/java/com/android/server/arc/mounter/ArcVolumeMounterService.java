package com.android.server.arc.mounter;

import android.content.Context;
import android.os.Binder;
import android.util.Slog;
import com.android.server.SystemService;
import java.util.function.BiConsumer;
import org.chromium.arc.CancellationRelay;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.MountPointInfo;
import org.chromium.arc.mojom.VolumeMounterHost;
import org.chromium.arc.mojom.VolumeMounterInstance;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo_base.mojom.FilePath;

/**
 * Volume mounter service bridging Chrome OS mount points to Android.
 * Handles MyFiles mount path and removable media from Chrome OS.
 */
public final class ArcVolumeMounterService
        implements VolumeMounterInstance, ArcService, ConnectionErrorHandler {
    private static final String TAG = "ArcVolumeMounter";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 25;
    private final Context mContext;
    private VolumeMounterHost mVolumeMounterHost;
    private int mVolumeMounterHostVersion = -1;
    private CancellationRelay mCancellationRelay;

    public static class LifeCycle extends SystemService {
        private static final String BINDER_SERVICE_NAME = "arc_volume_mounter_service";
        private final ArcVolumeMounterService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcVolumeMounterService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
            publishBinderService(BINDER_SERVICE_NAME, new Binder(), true);
        }
    }

    private ArcVolumeMounterService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Slog.i(TAG, "System booted, requesting ARC bridge proxy");
        mCancellationRelay = new CancellationRelay();
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- VolumeMounterInstance ---

    @Override
    public void init(VolumeMounterHost host, final InitResponse response) {
        Slog.i(TAG, "init: setting up thread-safe volume mounter host proxy");
        ArcBridgeSupport.buildThreadSafeProxy(host, VolumeMounterHost.MANAGER,
                this, new BiConsumer<VolumeMounterHost, Integer>() {
                    @Override
                    public void accept(VolumeMounterHost proxy, Integer version) {
                        mVolumeMounterHost = proxy;
                        mVolumeMounterHostVersion = version;
                        response.call();
                        Slog.i(TAG, "VolumeMounterHost ready, version=" + version);
                        // Request all currently mounted volumes
                        proxy.requestAllMountPoints();
                    }
                });
    }

    @Override
    public void onMountEvent(MountPointInfo mountPointInfo) {
        Slog.i(TAG, "onMountEvent: " + mountPointInfo.mountPath
                + " event=" + mountPointInfo.mountEvent);
        // TODO: Use IVold to create/remove stub volume for mountPointInfo
    }

    @Override
    public void prepareForRemovableMediaUnmount(FilePath path,
            PrepareForRemovableMediaUnmountResponse response) {
        Slog.i(TAG, "prepareForRemovableMediaUnmount: " + path.path);
        // TODO: Flush pending I/O for the path
        response.call(true);
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mVolumeMounterHost = null;
        mVolumeMounterHostVersion = -1;
        if (mCancellationRelay != null) {
            mCancellationRelay.cancel(true);
        }
        // Re-register for reconnection
        retryBridgeRequest();
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        if (mVolumeMounterHost != null) {
            mVolumeMounterHost.close();
            mVolumeMounterHost = null;
        }
        retryBridgeRequest();
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering instance");
        host.onVolumeMounterInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    private void retryBridgeRequest() {
        mCancellationRelay = new CancellationRelay();
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

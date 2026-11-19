package com.android.server.arc.memory;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import com.android.server.SystemService;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.MemoryInstance;
import org.chromium.arc.mojom.ReclaimRequest;
import org.chromium.arc.mojom.ReclaimResult;
import org.chromium.mojo.system.MojoException;

/**
 * Memory management bridge to Chrome OS via Mojo MemoryHost.
 * Handles drop_caches and memory reclaim requests from Chrome.
 */
public final class ArcMemoryService implements MemoryInstance, ArcService {
    private static final String TAG = "ArcMemory";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 59;
    private static final int DROP_CACHES_TIMEOUT_MS = 5000;
    private static final int DROP_CACHES_POLL_MS = 100;

    private final Context mContext;
    private final ArcBridgeSupport mArcBridgeSupport;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private int mDropCachesToken = 0;

    public static class LifeCycle extends SystemService {
        private final ArcMemoryService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcMemoryService(context, ArcBridgeSupport.getInstance());
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

    ArcMemoryService(Context context, ArcBridgeSupport bridgeSupport) {
        mContext = context;
        mArcBridgeSupport = bridgeSupport;
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        mArcBridgeSupport.requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- MemoryInstance ---

    @Override
    public void dropCaches(final DropCachesResponse response) {
        final int token = ++mDropCachesToken;
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean result = doDropCaches(token);
                final boolean r = result;
                mArcBridgeSupport.runOnMojoThread(new Runnable() {
                    @Override
                    public void run() {
                        response.call(r);
                    }
                });
            }
        });
    }

    @Override
    public void reclaim(ReclaimRequest request, final ReclaimResponse response) {
        // type 1 = ALL (all pages), other = ANON (anonymous pages only)
        final String reclaimType = (request.type == 1) ? "all" : "anon";
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ReclaimResult result = doReclaim(reclaimType);
                final ReclaimResult r = result;
                mArcBridgeSupport.runOnMojoThread(new Runnable() {
                    @Override
                    public void run() {
                        response.call(r);
                    }
                });
            }
        });
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
        host.onMemoryInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- Internal ---

    private boolean doDropCaches(int token) {
        try {
            // Trigger sync
            Runtime.getRuntime().exec("/vendor/bin/sync").waitFor();
        } catch (Exception e) {
            Log.w(TAG, "sync failed", e);
        }

        // Signal the container init to drop caches via system property
        String tokenStr = String.valueOf(token);
        SystemProperties.set("arc.drop_caches", tokenStr);

        // Poll for completion
        long deadline = System.currentTimeMillis() + DROP_CACHES_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            String done = SystemProperties.get("arc.drop_caches_done");
            if (tokenStr.equals(done)) {
                Log.i(TAG, "drop_caches completed");
                return true;
            }
            try {
                Thread.sleep(DROP_CACHES_POLL_MS);
            } catch (InterruptedException e) {
                break;
            }
        }
        Log.w(TAG, "drop_caches timed out");
        return false;
    }

    private ReclaimResult doReclaim(String type) {
        int reclaimed = 0;
        int unreclaimed = 0;
        Path procDir = Paths.get("/proc");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(procDir)) {
            for (Path entry : stream) {
                String name = entry.getFileName().toString();
                if (!isNumeric(name)) continue;

                Path reclaimFile = entry.resolve("reclaim");
                if (!Files.exists(reclaimFile)) continue;

                try {
                    Files.write(reclaimFile, type.getBytes());
                    reclaimed++;
                } catch (IOException e) {
                    unreclaimed++;
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to iterate /proc", e);
        }
        Log.i(TAG, "reclaim(" + type + "): reclaimed=" + reclaimed
                + " unreclaimed=" + unreclaimed);
        ReclaimResult result = new ReclaimResult();
        result.reclaimed = reclaimed;
        result.unreclaimed = unreclaimed;
        return result;
    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return !str.isEmpty();
    }
}

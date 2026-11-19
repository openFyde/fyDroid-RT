package com.android.server.arc.process;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import com.android.server.SystemService;
import java.util.List;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.ArcMemoryDump;
import org.chromium.arc.mojom.LowMemoryKillCounts;
import org.chromium.arc.mojom.ProcessInstance;
import org.chromium.arc.mojom.RunningAppProcessInfo;
import org.chromium.mojo.system.MojoException;

/**
 * Process management service bridging to Chrome OS via Mojo.
 * Handles host memory pressure, process list reporting, low-memory kill counts.
 */
public final class ArcProcessService implements ProcessInstance, ArcService {
    public static final String TAG = "ArcProcess";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;
    private final Context mContext;

    public static class LifeCycle extends SystemService {
        private final ArcProcessService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcProcessService(context);
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

    ArcProcessService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- ProcessInstance ---

    @Override
    public void killProcess(int pid, String reason) {
        Log.i(TAG, "killProcess pid=" + pid + " reason=" + reason);
        android.os.Process.killProcess(pid);
    }

    @Override
    public void requestProcessList(RequestProcessListResponse response) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procs =
                am.getRunningAppProcesses();
        RunningAppProcessInfo[] result;
        if (procs != null) {
            result = new RunningAppProcessInfo[procs.size()];
            for (int i = 0; i < procs.size(); i++) {
                ActivityManager.RunningAppProcessInfo p = procs.get(i);
                RunningAppProcessInfo info = new RunningAppProcessInfo();
                info.processName = p.processName;
                info.pid = p.pid;
                info.processState = toArcProcessState(p.processState);
                info.packages = p.pkgList;
                info.isFocused = (p.importance
                        == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
                info.lastActivityTime = 0;
                result[i] = info;
            }
        } else {
            result = new RunningAppProcessInfo[0];
        }
        response.call(result);
    }

    /**
     * Android 13's PROCESS_STATE_* renumbered into the values Chrome reads.
     *
     * arc.mojom's ProcessState carries two numberings in one enum: the
     * un-prefixed constants are Android P's and the R_-prefixed ones are
     * Android R's. Everything on the Chrome side — the task manager's row
     * prefix, IsPersistent(), IsCached(), IsBackgroundProtected() — is written
     * against the un-prefixed set, so a container has to hand over P's
     * numbering. R inserted PROCESS_STATE_BOUND_TOP at 3 and pushed every state
     * above it up by one, so shift them back down.
     *
     * (Reporting RunningAppProcessInfo.importance here instead, as this used
     * to, sends values Chrome's enum has no member for at all: every process
     * then reads as an ordinary foreground app — never persistent, never
     * cached, and all of them killable from the task manager.)
     */
    private static int toArcProcessState(int processState) {
        if (processState < 0) return -1;              // UNKNOWN
        if (processState <= 2) return processState;   // PERSISTENT, PERSISTENT_UI, TOP
        if (processState == 3) return 2;              // BOUND_TOP has no P value; it is a TOP client
        return processState - 1;
    }

    @Override
    public void requestApplicationProcessMemoryInfo(
            RequestApplicationProcessMemoryInfoResponse response) {
        // Would need Debug.MemoryInfo per process — return empty for now
        response.call(new ArcMemoryDump[0]);
    }

    @Override
    public void requestSystemProcessMemoryInfo(int[] nspids,
            RequestSystemProcessMemoryInfoResponse response) {
        response.call(new ArcMemoryDump[0]);
    }

    @Override
    public void applyHostMemoryPressureDeprecated(int level, long reclaimTarget,
            ApplyHostMemoryPressureDeprecatedResponse response) {
        // Deprecated — return zeros
        response.call(0, 0L);
    }

    @Override
    public void applyHostMemoryPressure(int level, long reclaimTarget,
            ApplyHostMemoryPressureResponse response) {
        // Apply memory pressure by trimming processes
        Log.i(TAG, "applyHostMemoryPressure level=" + level
                + " target=" + reclaimTarget);
        // For now return zeros — real implementation needs LMKD interaction
        response.call(0, 0L);
    }

    @Override
    public void requestLowMemoryKillCounts(
            RequestLowMemoryKillCountsResponse response) {
        LowMemoryKillCounts counts = new LowMemoryKillCounts();
        // Return zeros — real counts need KillEventsMonitor
        response.call(counts);
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
        host.onProcessInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

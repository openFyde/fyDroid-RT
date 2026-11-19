package com.android.server.arc.crash_collector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.DropBoxManager;
import android.os.FileObserver;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.util.Log;

import com.android.server.SystemService;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.CrashCollectorHost;
import org.chromium.arc.mojom.CrashCollectorInstance;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.UntypedHandle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Bridges Android crash reports to Chrome OS crash reporting via Mojo.
 *
 * Monitors three crash sources:
 * 1. DropBoxManager entries (app crashes, ANRs, tombstones)
 * 2. Native crash minidumps in /data/vendor/arc_native_crash_reports/
 * 3. Kernel crash logs from pstore (ramoops)
 */
class ArcCrashCollectorService implements CrashCollectorInstance, ArcService {
    private static final String TAG = "ArcCrashCollector";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    private static final String NATIVE_CRASH_DIR = "/data/vendor/arc_native_crash_reports";
    private static final String PSTORE_RAMOOPS = "/sys/fs/pstore/dmesg-ramoops-0";

    // DropBox tags we monitor
    private static final String[] DROPBOX_TAGS = {
            "system_app_crash",
            "system_app_anr",
            "system_app_wtf",
            "data_app_crash",
            "data_app_anr",
            "SYSTEM_TOMBSTONE",
    };

    // CrashType constants matching CrashCollectorHost expectations
    private static final String CRASH_TYPE_JAVA = "java_crash";
    private static final String CRASH_TYPE_ANR = "anr";
    private static final String CRASH_TYPE_TOMBSTONE = "tombstone";

    private final Context mContext;
    private CrashCollectorHost mHost;
    private FileObserver mNativeCrashObserver;
    private BroadcastReceiver mDropBoxReceiver;

    public static class LifeCycle extends SystemService {
        private final ArcCrashCollectorService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcCrashCollectorService(context);
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

    ArcCrashCollectorService(Context context) {
        mContext = context;
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- CrashCollectorInstance ---

    @Override
    public void init(CrashCollectorHost host, InitResponse response) {
        Log.i(TAG, "init: Chrome connected");
        mHost = host;
        response.call();

        // Send build properties
        host.setBuildProperties(
                Build.DEVICE,
                Build.BOARD,
                Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "",
                Build.FINGERPRINT);

        // Start monitoring
        startDropBoxMonitor();
        startNativeCrashMonitor();
        checkKernelCrash();
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        stopMonitors();
        mHost = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        stopMonitors();
        mHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering CrashCollectorInstance");
        host.onCrashCollectorInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version);
    }

    // --- DropBox monitoring ---

    private void startDropBoxMonitor() {
        DropBoxManager dropBox = (DropBoxManager)
                mContext.getSystemService(Context.DROPBOX_SERVICE);
        if (dropBox == null) {
            Log.w(TAG, "DropBoxManager not available");
            return;
        }

        mDropBoxReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tag = intent.getStringExtra(
                        DropBoxManager.EXTRA_TAG);
                long time = intent.getLongExtra(
                        DropBoxManager.EXTRA_TIME, 0);
                if (tag != null) {
                    handleDropBoxEntry(dropBox, tag, time);
                }
            }
        };
        IntentFilter filter = new IntentFilter(
                DropBoxManager.ACTION_DROPBOX_ENTRY_ADDED);
        mContext.registerReceiver(mDropBoxReceiver, filter);
        Log.i(TAG, "DropBox monitor started");
    }

    private void handleDropBoxEntry(DropBoxManager dropBox, String tag,
            long time) {
        // Only handle tags we care about
        boolean relevant = false;
        for (String t : DROPBOX_TAGS) {
            if (t.equals(tag)) {
                relevant = true;
                break;
            }
        }
        if (!relevant) return;

        CrashCollectorHost host = mHost;
        if (host == null) return;

        try {
            // time-1 to get the entry at exactly this timestamp
            DropBoxManager.Entry entry = dropBox.getNextEntry(tag,
                    time > 0 ? time - 1 : 0);
            if (entry == null) return;

            try {
                String text = entry.getText(256 * 1024); // 256KB max
                if (text == null || text.isEmpty()) return;

                String crashType;
                if (tag.contains("crash")) {
                    crashType = CRASH_TYPE_JAVA;
                } else if (tag.contains("anr")) {
                    crashType = CRASH_TYPE_ANR;
                } else {
                    crashType = CRASH_TYPE_TOMBSTONE;
                }

                UntypedHandle handle = textToMojoHandle(text);
                if (handle != null && handle.isValid()) {
                    long uptimeMs = SystemClock.uptimeMillis();
                    org.chromium.mojo_base.mojom.TimeDelta timeDelta =
                            new org.chromium.mojo_base.mojom.TimeDelta();
                    timeDelta.microseconds = uptimeMs * 1000L;
                    host.dumpCrash(crashType, handle, timeDelta);
                    Log.i(TAG, "Forwarded " + tag + " crash to Chrome");
                }
            } finally {
                entry.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to handle DropBox entry: " + tag, e);
        }
    }

    // --- Native crash monitoring ---

    private void startNativeCrashMonitor() {
        File dir = new File(NATIVE_CRASH_DIR);
        if (!dir.exists()) {
            Log.d(TAG, "Native crash dir doesn't exist: " + NATIVE_CRASH_DIR);
            return;
        }

        mNativeCrashObserver = new FileObserver(NATIVE_CRASH_DIR, FileObserver.CREATE) {
            @Override
            public void onEvent(int event, String path) {
                if (path == null) return;
                handleNativeCrash(new File(NATIVE_CRASH_DIR, path));
            }
        };
        mNativeCrashObserver.startWatching();
        Log.i(TAG, "Native crash monitor started on " + NATIVE_CRASH_DIR);
    }

    private void handleNativeCrash(File file) {
        CrashCollectorHost host = mHost;
        if (host == null) return;

        try {
            // Parse PID and timestamp from filename (format: <execname>.<pid>.<timestamp>.dmp)
            String name = file.getName();
            String[] parts = name.split("\\.");
            String execName = parts.length > 0 ? parts[0] : "unknown";
            int pid = 0;
            long timestamp = 0;

            if (parts.length >= 3) {
                try {
                    pid = Integer.parseInt(parts[1]);
                    timestamp = Long.parseLong(parts[2]);
                } catch (NumberFormatException e) {
                    // Use defaults
                }
            }

            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
                    file, ParcelFileDescriptor.MODE_READ_ONLY);
            int fd = pfd.detachFd();
            UntypedHandle handle = ArcBridgeSupport.getInstance()
                    .convertFileDescriptorToMojoHandle(fd);

            if (handle != null && handle.isValid()) {
                host.dumpNativeCrash(execName, pid, timestamp, handle);
                Log.i(TAG, "Forwarded native crash: " + name);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to handle native crash: " + file, e);
        }
    }

    // --- Kernel crash check ---

    private void checkKernelCrash() {
        CrashCollectorHost host = mHost;
        if (host == null) return;

        File ramoops = new File(PSTORE_RAMOOPS);
        if (!ramoops.exists()) return;

        try {
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
                    ramoops, ParcelFileDescriptor.MODE_READ_ONLY);
            int fd = pfd.detachFd();
            UntypedHandle handle = ArcBridgeSupport.getInstance()
                    .convertFileDescriptorToMojoHandle(fd);

            if (handle != null && handle.isValid()) {
                host.dumpKernelCrash(handle);
                Log.i(TAG, "Forwarded kernel crash from pstore");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to read kernel crash log", e);
        }
    }

    // --- Helpers ---

    private void stopMonitors() {
        if (mNativeCrashObserver != null) {
            mNativeCrashObserver.stopWatching();
            mNativeCrashObserver = null;
        }
        if (mDropBoxReceiver != null) {
            try {
                mContext.unregisterReceiver(mDropBoxReceiver);
            } catch (Exception e) {
                // ignore
            }
            mDropBoxReceiver = null;
        }
    }

    /**
     * Write text to a pipe and return the read end as a Mojo handle.
     */
    private UntypedHandle textToMojoHandle(String text) {
        try {
            ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor readEnd = pipe[0];
            ParcelFileDescriptor writeEnd = pipe[1];

            // Write crash text on a background thread to avoid blocking
            byte[] data = text.getBytes(StandardCharsets.UTF_8);
            new Thread(() -> {
                try (FileOutputStream fos = new FileOutputStream(
                        writeEnd.getFileDescriptor())) {
                    fos.write(data);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to write crash data to pipe", e);
                } finally {
                    try { writeEnd.close(); } catch (IOException e) { /* ignore */ }
                }
            }, "crash-pipe-writer").start();

            int fd = readEnd.detachFd();
            return ArcBridgeSupport.getInstance()
                    .convertFileDescriptorToMojoHandle(fd);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create pipe for crash data", e);
            return null;
        }
    }
}

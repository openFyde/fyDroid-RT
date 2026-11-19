package com.android.server.midi;

import android.content.Context;
import android.util.Slog;
import com.android.server.SystemService;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.MidisHost;
import org.chromium.arc.mojom.MidisInstance;
import org.chromium.mojo.system.MojoException;

/**
 * MIDI device manager bridging Chrome OS MIDI (midis) to Android
 * via Mojo MidisHost.
 */
public final class ArcMidiManager implements MidisInstance, ArcService {
    private static final String TAG = "ArcMidiManager";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 19;
    private final Context mContext;
    private MidisHost mMidisHost;

    public static class LifeCycle extends SystemService {
        private final ArcMidiManager mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcMidiManager(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.systemReady();
            }
        }

        @Override
        public void onStart() {
        }
    }

    ArcMidiManager(Context context) {
        mContext = context;
    }

    void systemReady() {
        Slog.i(TAG, "System ready, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- MidisInstance ---

    @Override
    public void init(MidisHost host, InitResponse response) {
        Slog.i(TAG, "init: MidisHost connected");
        mMidisHost = host;
        response.call();
        // TODO: Connect client to server via mMidisHost.connect()
        // to receive device add/remove notifications
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mMidisHost = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mMidisHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering instance");
        host.onMidisInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

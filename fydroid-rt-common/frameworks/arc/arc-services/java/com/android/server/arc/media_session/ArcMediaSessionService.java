package com.android.server.arc.media_session;

import android.content.Context;
import android.util.Log;

import com.android.server.SystemService;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.MediaSessionInstance;
import org.chromium.media_session.mojom.AudioFocusManager;
import org.chromium.mojo.system.MojoException;

/**
 * Bridges media session / audio focus between Android and Chrome OS.
 *
 * Chrome calls enableAudioFocus() to pass an AudioFocusManager that Android
 * uses to request audio focus from Chrome's media session service. Every
 * Android media session that plays is mirrored through it by
 * {@link ArcMediaSessionMirror}, which is what puts the app in Chrome's global
 * media controls — the media button in the shelf status area and the media row
 * in quick settings.
 */
class ArcMediaSessionService implements MediaSessionInstance, ArcService {
    private static final String TAG = "ArcMediaSession";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    private final Context mContext;
    private final ArcMediaSessionMirror mMirror;
    private AudioFocusManager mAudioFocusManager;

    public static class LifeCycle extends SystemService {
        private final ArcMediaSessionService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcMediaSessionService(context);
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

    ArcMediaSessionService(Context context) {
        mContext = context;
        mMirror = new ArcMediaSessionMirror(context);
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- MediaSessionInstance ---

    @Override
    public void enableAudioFocus(AudioFocusManager manager) {
        Log.i(TAG, "enableAudioFocus: Chrome connected audio focus manager");
        mAudioFocusManager = manager;
        mMirror.start(manager);
    }

    @Override
    public void disableAudioFocus() {
        Log.i(TAG, "disableAudioFocus");
        mMirror.stop();
        if (mAudioFocusManager != null) {
            try {
                mAudioFocusManager.close();
            } catch (Exception e) {
                // ignore
            }
            mAudioFocusManager = null;
        }
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        mMirror.stop();
        mAudioFocusManager = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        mMirror.stop();
        mAudioFocusManager = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering MediaSessionInstance");
        host.onMediaSessionInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version);
    }
}

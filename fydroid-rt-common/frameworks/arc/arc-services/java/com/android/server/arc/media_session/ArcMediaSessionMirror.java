package com.android.server.arc.media_session;

import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.ArrayMap;
import android.util.Log;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.media_session.mojom.AudioFocusManager;
import org.chromium.media_session.mojom.AudioFocusRequestClient;
import org.chromium.media_session.mojom.AudioFocusType;
import org.chromium.media_session.mojom.MediaSessionInfo;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.system.Pair;
import org.chromium.mojo.system.impl.CoreImpl;
import org.chromium.mojo_base.mojom.UnguessableToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirrors Android's media sessions into Chrome's media session service.
 *
 * Chrome hands us an {@link AudioFocusManager} (see ArcMediaSessionService).
 * Everything Chrome's global media controls show — the media button in the
 * shelf status area and the media row in quick settings — is driven off audio
 * focus requests made through it: each request carries a MediaSession endpoint
 * that Chrome then observes for metadata, playback state, position and artwork,
 * and calls back into for play/pause/next/previous.
 *
 * Android already tracks the same thing in MediaSessionManager, so this class
 * is a straight bridge: watch the active sessions, and give every session that
 * actually starts playing a matching audio focus request on the Chrome side.
 *
 * Two threads meet here:
 *   - a private HandlerThread owns all Android state (the session list, the
 *     MediaController callbacks, the snapshots we build from them);
 *   - the Mojo thread owns every proxy handed to us by Chrome.
 * Nothing is shared across the two except immutable snapshots, and hops are
 * always explicit (mHandler.post / ArcBridgeSupport.runOnMojoThread).
 */
class ArcMediaSessionMirror {
    private static final String TAG = "ArcMediaSession";

    private final Context mContext;
    private final Handler mHandler;

    /** Sessions we are mirroring, keyed by session token. Handler thread only. */
    private final ArrayMap<android.media.session.MediaSession.Token, MirroredMediaSession>
            mSessions = new ArrayMap<>();

    private MediaSessionManager mSessionManager;
    private boolean mListening;

    /** Chrome's audio focus manager. Mojo thread only. */
    private AudioFocusManager mAudioFocusManager;

    private final MediaSessionManager.OnActiveSessionsChangedListener mSessionsListener =
            this::onActiveSessionsChanged;

    ArcMediaSessionMirror(Context context) {
        mContext = context;
        HandlerThread thread = new HandlerThread("ArcMediaSession");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }

    /** Called on the Mojo thread when Chrome enables audio focus integration. */
    void start(AudioFocusManager manager) {
        mAudioFocusManager = manager;
        mHandler.post(this::startListening);
    }

    /** Called on the Mojo thread when Chrome disables it, or the pipe drops. */
    void stop() {
        mAudioFocusManager = null;
        mHandler.post(this::stopListening);
    }

    // --- Handler thread ---

    private void startListening() {
        if (mListening) {
            return;
        }
        if (mSessionManager == null) {
            mSessionManager = mContext.getSystemService(MediaSessionManager.class);
        }
        if (mSessionManager == null) {
            Log.e(TAG, "No MediaSessionManager; media controls will not work");
            return;
        }
        try {
            // system_server is exempt from the MEDIA_CONTENT_CONTROL check, so no
            // notification listener component is needed here.
            mSessionManager.addOnActiveSessionsChangedListener(mSessionsListener, null, mHandler);
            mListening = true;
            onActiveSessionsChanged(mSessionManager.getActiveSessions(null));
        } catch (SecurityException e) {
            Log.e(TAG, "Not allowed to watch media sessions", e);
        }
    }

    private void stopListening() {
        if (mListening) {
            mSessionManager.removeOnActiveSessionsChangedListener(mSessionsListener);
            mListening = false;
        }
        for (int i = 0; i < mSessions.size(); i++) {
            mSessions.valueAt(i).shutdown();
        }
        mSessions.clear();
    }

    private void onActiveSessionsChanged(List<MediaController> controllers) {
        if (!mListening) {
            return;
        }
        List<android.media.session.MediaSession.Token> live = new ArrayList<>();
        if (controllers != null) {
            for (MediaController controller : controllers) {
                android.media.session.MediaSession.Token token = controller.getSessionToken();
                live.add(token);
                if (mSessions.containsKey(token)) {
                    continue;
                }
                MirroredMediaSession session =
                        new MirroredMediaSession(mContext, this, controller, mHandler);
                mSessions.put(token, session);
                session.start();
            }
        }
        for (int i = mSessions.size() - 1; i >= 0; i--) {
            if (!live.contains(mSessions.keyAt(i))) {
                mSessions.valueAt(i).shutdown();
                mSessions.removeAt(i);
            }
        }
    }

    /** Called by a session on the handler thread once it starts playing. */
    void requestAudioFocus(MirroredMediaSession session, MediaSessionInfo info) {
        ArcBridgeSupport.getInstance().runOnMojoThread(() -> {
            AudioFocusManager manager = mAudioFocusManager;
            if (manager == null) {
                return;
            }
            Pair<AudioFocusRequestClient.Proxy, InterfaceRequest<AudioFocusRequestClient>> pipe =
                    AudioFocusRequestClient.MANAGER.getInterfaceRequest(CoreImpl.getInstance());
            MirroredMediaSession.Binding binding = session.onAudioFocusRequested(pipe.first);
            // Note: do not call AudioFocusManager.getFocusRequests() from here.
            // Chrome's browser process traps (CHECK) a few seconds after
            // serving that call to ARC, taking the session and the container
            // down with it. Everything we need flows the other way anyway.
            manager.requestAudioFocus(pipe.second, binding, info, AudioFocusType.GAIN,
                    (UnguessableToken requestId) -> Log.i(TAG, "Audio focus granted to "
                            + session.getPackageName()));
        });
    }
}

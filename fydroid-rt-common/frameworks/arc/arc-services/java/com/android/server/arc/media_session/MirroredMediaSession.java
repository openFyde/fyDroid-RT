package com.android.server.arc.media_session;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.gfx.mojom.Size;
import org.chromium.media_session.mojom.AudioFocusRequestClient;
import org.chromium.media_session.mojom.MediaImage;
import org.chromium.media_session.mojom.MediaImageBitmap;
import org.chromium.media_session.mojom.MediaImageBitmapColorType;
import org.chromium.media_session.mojom.MediaMetadata;
import org.chromium.media_session.mojom.MediaPictureInPictureState;
import org.chromium.media_session.mojom.MediaPlaybackState;
import org.chromium.media_session.mojom.MediaPosition;
import org.chromium.media_session.mojom.MediaSession;
import org.chromium.media_session.mojom.MediaSessionAction;
import org.chromium.media_session.mojom.MediaSessionDebugInfo;
import org.chromium.media_session.mojom.MediaSessionImageType;
import org.chromium.media_session.mojom.MediaSessionInfo;
import org.chromium.media_session.mojom.MediaSessionObserver;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo_base.mojom.String16;
import org.chromium.mojo_base.mojom.TimeDelta;
import org.chromium.mojo_base.mojom.TimeTicks;
import org.chromium.url.mojom.Url;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One Android media session, mirrored into Chrome.
 *
 * The Android half (a MediaController and its callbacks) runs on the mirror's
 * handler thread and turns every change into an immutable {@link Snapshot}.
 * The Chrome half is {@link Binding}: a Mojo MediaSession endpoint, created
 * when we ask for audio focus and dropped when we give it up, which pushes
 * snapshots at Chrome's observers and translates Chrome's transport commands
 * back into MediaController calls.
 */
class MirroredMediaSession {
    private static final String TAG = "ArcMediaSession";

    // Chrome identifies artwork by URL but always fetches the pixels back
    // through getMediaImageBitmap(), so any stable URL will do. The .png
    // suffix is what scores it highest in Chrome's MediaImageManager.
    private static final String ARTWORK_URL = "arc://media-session/artwork.png";

    // How long a session may sit stopped before we give its audio focus back.
    // Long enough to cover the gap between two tracks, short enough that a
    // session the user really stopped leaves the media controls promptly.
    private static final long ABANDON_GRACE_MS = 5000;

    private static final int CHANGE_INFO = 1;
    private static final int CHANGE_METADATA = 1 << 1;
    private static final int CHANGE_ACTIONS = 1 << 2;
    private static final int CHANGE_POSITION = 1 << 3;
    private static final int CHANGE_IMAGES = 1 << 4;
    private static final int CHANGE_ALL =
            CHANGE_INFO | CHANGE_METADATA | CHANGE_ACTIONS | CHANGE_POSITION | CHANGE_IMAGES;

    /** Everything Chrome needs to know about the session at one instant. */
    static final class Snapshot {
        MediaSessionInfo info;
        MediaMetadata metadata;
        int[] actions;
        MediaPosition position;
        MediaImage[] artwork;
        Bitmap artworkBitmap;
    }

    private final Context mContext;
    private final ArcMediaSessionMirror mMirror;
    private final MediaController mController;
    private final Handler mHandler;
    private final String mSourceTitle;

    // --- Handler thread ---
    private boolean mStarted;
    private boolean mFocusRequested;
    private boolean mAbandonScheduled;
    private final Runnable mAbandonTask = this::abandonAfterGrace;

    // --- Written on the handler thread, read on the Mojo thread ---
    private volatile Snapshot mSnapshot = new Snapshot();

    // --- Mojo thread ---
    private Binding mBinding;

    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            update();
        }

        @Override
        public void onMetadataChanged(android.media.MediaMetadata metadata) {
            update();
        }

        @Override
        public void onSessionDestroyed() {
            shutdown();
        }
    };

    MirroredMediaSession(Context context, ArcMediaSessionMirror mirror,
            MediaController controller, Handler handler) {
        mContext = context;
        mMirror = mirror;
        mController = controller;
        mHandler = handler;
        mSourceTitle = resolveAppLabel(context, controller.getPackageName());
    }

    String getPackageName() {
        return mController.getPackageName();
    }

    // --- Handler thread ---

    void start() {
        if (mStarted) {
            return;
        }
        mStarted = true;
        mController.registerCallback(mCallback, mHandler);
        update();
    }

    void shutdown() {
        if (!mStarted) {
            return;
        }
        mStarted = false;
        mController.unregisterCallback(mCallback);
        mAbandonScheduled = false;
        mHandler.removeCallbacks(mAbandonTask);
        if (mFocusRequested) {
            mFocusRequested = false;
            abandonAudioFocus();
        }
    }

    /** The session stopped and stayed stopped; let Chrome retire the item. */
    private void abandonAfterGrace() {
        mAbandonScheduled = false;
        if (!mFocusRequested) {
            return;
        }
        Log.i(TAG, getPackageName() + " stopped playing, releasing audio focus");
        mFocusRequested = false;
        abandonAudioFocus();
    }

    private void update() {
        if (!mStarted) {
            return;
        }
        PlaybackState state = mController.getPlaybackState();
        android.media.MediaMetadata metadata = mController.getMetadata();

        Snapshot previous = mSnapshot;
        Snapshot snapshot = buildSnapshot(state, metadata);
        mSnapshot = snapshot;

        if (!mFocusRequested) {
            // Chrome only shows a media item for a session that holds audio
            // focus, so nothing is mirrored until the session actually plays.
            if (isPlaying(state)) {
                mFocusRequested = true;
                mMirror.requestAudioFocus(this, snapshot.info);
            }
            return;
        }

        if (isFinished(state)) {
            // Most apps dip through STOPPED or NONE between tracks. Abandoning
            // audio focus there ends Chrome's media item, and the next track
            // arrives under a fresh request id — so instead of the panel
            // updating in place, Chrome tears it down and builds a new one.
            // Hold the request across the gap and only let go if the session
            // really has stopped for good.
            if (!mAbandonScheduled) {
                mAbandonScheduled = true;
                mHandler.postDelayed(mAbandonTask, ABANDON_GRACE_MS);
            }
        } else if (mAbandonScheduled) {
            mAbandonScheduled = false;
            mHandler.removeCallbacks(mAbandonTask);
        }

        int changes = diff(previous, snapshot);
        if (changes != 0) {
            push(snapshot, changes);
        }
    }

    private void push(Snapshot snapshot, int changes) {
        ArcBridgeSupport.getInstance().runOnMojoThread(() -> {
            if (mBinding != null) {
                mBinding.push(snapshot, changes);
            }
        });
    }

    private void abandonAudioFocus() {
        ArcBridgeSupport.getInstance().runOnMojoThread(() -> {
            if (mBinding != null) {
                mBinding.abandon();
                mBinding = null;
            }
        });
    }

    // --- Mojo thread ---

    /** Creates the Chrome-facing endpoint for a fresh audio focus request. */
    Binding onAudioFocusRequested(AudioFocusRequestClient.Proxy focusClient) {
        if (mBinding != null) {
            mBinding.abandon();
        }
        mBinding = new Binding(focusClient);
        return mBinding;
    }

    /**
     * The Mojo MediaSession Chrome talks to. One per audio focus request: when
     * focus is abandoned this endpoint is closed and a later request gets a new
     * one, so a stale binding can never deliver commands to a live session.
     */
    final class Binding implements MediaSession {
        private final List<MediaSessionObserver> mObservers = new ArrayList<>();
        private AudioFocusRequestClient.Proxy mFocusClient;
        private boolean mClosed;

        Binding(AudioFocusRequestClient.Proxy focusClient) {
            mFocusClient = focusClient;
        }

        void push(Snapshot snapshot, int changes) {
            if (mClosed) {
                return;
            }
            if ((changes & CHANGE_INFO) != 0 && mFocusClient != null) {
                mFocusClient.mediaSessionInfoChanged(snapshot.info);
            }
            for (MediaSessionObserver observer : mObservers) {
                pushTo(observer, snapshot, changes);
            }
        }

        private void pushTo(MediaSessionObserver observer, Snapshot snapshot, int changes) {
            if ((changes & CHANGE_INFO) != 0) {
                observer.mediaSessionInfoChanged(snapshot.info);
            }
            if ((changes & CHANGE_METADATA) != 0) {
                observer.mediaSessionMetadataChanged(snapshot.metadata);
            }
            if ((changes & CHANGE_ACTIONS) != 0) {
                observer.mediaSessionActionsChanged(snapshot.actions);
            }
            if ((changes & CHANGE_POSITION) != 0) {
                observer.mediaSessionPositionChanged(snapshot.position);
            }
            if ((changes & CHANGE_IMAGES) != 0) {
                Map<Integer, MediaImage[]> images = new HashMap<>();
                if (snapshot.artwork != null) {
                    images.put(MediaSessionImageType.ARTWORK, snapshot.artwork);
                }
                observer.mediaSessionImagesChanged(images);
            }
        }

        void abandon() {
            if (mClosed) {
                return;
            }
            mClosed = true;
            if (mFocusClient != null) {
                mFocusClient.abandonAudioFocus();
                mFocusClient.close();
                mFocusClient = null;
            }
            for (MediaSessionObserver observer : mObservers) {
                observer.close();
            }
            mObservers.clear();
        }

        // --- MediaSession: state Chrome asks for ---

        @Override
        public void getMediaSessionInfo(GetMediaSessionInfoResponse response) {
            response.call(mSnapshot.info);
        }

        @Override
        public void getDebugInfo(GetDebugInfoResponse response) {
            Snapshot snapshot = mSnapshot;
            MediaSessionDebugInfo info = new MediaSessionDebugInfo();
            info.name = mSourceTitle;
            info.owner = getPackageName();
            info.state = String.valueOf(snapshot.info.state);
            response.call(info);
        }

        @Override
        public void getMediaImageBitmap(MediaImage image, int minimumSizePx, int desiredSizePx,
                GetMediaImageBitmapResponse response) {
            // Scaling happens off the Mojo thread, but the reply has to go back
            // out on it — the bindings' message pipes are not thread safe.
            mHandler.post(() -> {
                MediaImageBitmap bitmap =
                        buildImageBitmap(mSnapshot.artworkBitmap, minimumSizePx, desiredSizePx);
                ArcBridgeSupport.getInstance().runOnMojoThread(() -> {
                    if (!mClosed) {
                        response.call(bitmap);
                    }
                });
            });
        }

        @Override
        public void addObserver(MediaSessionObserver observer) {
            if (mClosed) {
                observer.close();
                return;
            }
            Log.i(TAG, "Chrome is observing " + getPackageName());
            mObservers.add(observer);
            pushTo(observer, mSnapshot, CHANGE_ALL);
        }

        // --- MediaSession: commands from Chrome ---

        @Override
        public void suspend(int suspendType) {
            Log.i(TAG, "Chrome asked " + getPackageName() + " to pause");
            mHandler.post(() -> mController.getTransportControls().pause());
        }

        @Override
        public void resume(int suspendType) {
            Log.i(TAG, "Chrome asked " + getPackageName() + " to play");
            mHandler.post(() -> mController.getTransportControls().play());
        }

        @Override
        public void stop(int suspendType) {
            mHandler.post(() -> mController.getTransportControls().stop());
        }

        @Override
        public void previousTrack() {
            mHandler.post(() -> mController.getTransportControls().skipToPrevious());
        }

        @Override
        public void nextTrack() {
            mHandler.post(() -> mController.getTransportControls().skipToNext());
        }

        @Override
        public void seek(TimeDelta seekTime) {
            final long deltaMs = seekTime.microseconds / 1000;
            mHandler.post(() -> {
                PlaybackState state = mController.getPlaybackState();
                long position = state != null ? state.getPosition() : 0;
                mController.getTransportControls().seekTo(Math.max(0, position + deltaMs));
            });
        }

        @Override
        public void seekTo(TimeDelta seekTime) {
            seekToMs(seekTime.microseconds / 1000);
        }

        @Override
        public void scrubTo(TimeDelta seekTime) {
            seekToMs(seekTime.microseconds / 1000);
        }

        private void seekToMs(long positionMs) {
            mHandler.post(() ->
                    mController.getTransportControls().seekTo(Math.max(0, positionMs)));
        }

        @Override
        public void raise() {
            mHandler.post(() -> {
                PendingIntent intent = mController.getSessionActivity();
                if (intent == null) {
                    return;
                }
                try {
                    intent.send();
                } catch (PendingIntent.CanceledException e) {
                    Log.w(TAG, "Could not raise " + getPackageName(), e);
                }
            });
        }

        // --- MediaSession: things ARC sessions cannot do ---

        @Override
        public void startDucking() {
            // Chrome OS enforces audio focus by suspending, not ducking, and
            // Android gives us no way to duck another app's stream.
        }

        @Override
        public void stopDucking() {
        }

        @Override
        public void skipAd() {
        }

        @Override
        public void enterPictureInPicture() {
        }

        @Override
        public void exitPictureInPicture() {
        }

        @Override
        public void setAudioSinkId(String id) {
        }

        @Override
        public void toggleMicrophone() {
        }

        @Override
        public void toggleCamera() {
        }

        @Override
        public void hangUp() {
        }

        @Override
        public void setMute(boolean mute) {
        }

        @Override
        public void requestMediaRemoting() {
        }

        @Override
        public void previousSlide() {
        }

        @Override
        public void nextSlide() {
        }

        // --- Mojo lifecycle ---

        @Override
        public void close() {
            Log.i(TAG, "Chrome dropped the session for " + getPackageName());
            detach();
        }

        @Override
        public void onConnectionError(MojoException e) {
            Log.e(TAG, "Session pipe error for " + getPackageName(), e);
            detach();
        }

        private void detach() {
            mClosed = true;
            mObservers.clear();
            mFocusClient = null;
            if (mBinding == this) {
                mBinding = null;
            }
        }
    }

    // --- Snapshot building (handler thread) ---

    private Snapshot buildSnapshot(PlaybackState state, android.media.MediaMetadata metadata) {
        Snapshot snapshot = new Snapshot();
        snapshot.info = buildInfo(state);
        snapshot.metadata = buildMetadata(metadata);
        snapshot.actions = buildActions(state);
        snapshot.position = buildPosition(state, metadata);
        snapshot.artworkBitmap = extractArtwork(metadata);
        snapshot.artwork = buildArtworkList(snapshot.artworkBitmap);
        return snapshot;
    }

    private MediaSessionInfo buildInfo(PlaybackState state) {
        MediaSessionInfo info = new MediaSessionInfo();
        if (isPlaying(state)) {
            info.state = MediaSessionInfo.SessionState.ACTIVE;
            info.playbackState = MediaPlaybackState.PLAYING;
        } else {
            // Anything that is not playing reads as paused, including the
            // momentary stop between two tracks: reporting INACTIVE there
            // makes Chrome hide the item and show it again a moment later.
            // A session that has really finished loses its focus request
            // instead, which is what retires the item.
            info.state = MediaSessionInfo.SessionState.SUSPENDED;
            info.playbackState = MediaPlaybackState.PAUSED;
        }
        // Every Android session can be driven through its transport controls,
        // and Chrome hides the controls of a session that says otherwise.
        info.isControllable = true;
        info.forceDuck = false;
        info.preferStopForGainFocusLoss = false;
        info.isSensitive = false;
        info.muted = false;
        info.hasPresentation = false;
        info.pictureInPictureState = MediaPictureInPictureState.NOT_IN_PICTURE_IN_PICTURE;
        return info;
    }

    private MediaMetadata buildMetadata(android.media.MediaMetadata metadata) {
        MediaMetadata out = new MediaMetadata();
        out.title = toString16(firstOf(metadata,
                android.media.MediaMetadata.METADATA_KEY_DISPLAY_TITLE,
                android.media.MediaMetadata.METADATA_KEY_TITLE));
        out.artist = toString16(firstOf(metadata,
                android.media.MediaMetadata.METADATA_KEY_ARTIST,
                android.media.MediaMetadata.METADATA_KEY_ALBUM_ARTIST,
                android.media.MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE));
        out.album = toString16(firstOf(metadata,
                android.media.MediaMetadata.METADATA_KEY_ALBUM));
        out.sourceTitle = toString16(mSourceTitle);
        return out;
    }

    private static int[] buildActions(PlaybackState state) {
        if (state == null) {
            return new int[0];
        }
        long actions = state.getActions();
        List<Integer> out = new ArrayList<>();
        if ((actions & (PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE)) != 0) {
            out.add(MediaSessionAction.PLAY);
        }
        if ((actions & (PlaybackState.ACTION_PAUSE | PlaybackState.ACTION_PLAY_PAUSE)) != 0) {
            out.add(MediaSessionAction.PAUSE);
        }
        if ((actions & PlaybackState.ACTION_SKIP_TO_PREVIOUS) != 0) {
            out.add(MediaSessionAction.PREVIOUS_TRACK);
        }
        if ((actions & PlaybackState.ACTION_SKIP_TO_NEXT) != 0) {
            out.add(MediaSessionAction.NEXT_TRACK);
        }
        if ((actions & PlaybackState.ACTION_REWIND) != 0) {
            out.add(MediaSessionAction.SEEK_BACKWARD);
        }
        if ((actions & PlaybackState.ACTION_FAST_FORWARD) != 0) {
            out.add(MediaSessionAction.SEEK_FORWARD);
        }
        if ((actions & PlaybackState.ACTION_STOP) != 0) {
            out.add(MediaSessionAction.STOP);
        }
        if ((actions & PlaybackState.ACTION_SEEK_TO) != 0) {
            out.add(MediaSessionAction.SEEK_TO);
            out.add(MediaSessionAction.SCRUB_TO);
        }
        int[] result = new int[out.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = out.get(i);
        }
        return result;
    }

    private static MediaPosition buildPosition(PlaybackState state,
            android.media.MediaMetadata metadata) {
        if (state == null) {
            return null;
        }
        long durationMs = metadata != null
                ? metadata.getLong(android.media.MediaMetadata.METADATA_KEY_DURATION) : 0;
        if (durationMs < 0) {
            // Live streams report -1; Chrome wants a non-negative duration.
            durationMs = 0;
        }
        MediaPosition position = new MediaPosition();
        // Chrome extrapolates the playhead from this rate, so a paused session
        // has to report zero or its progress bar keeps running.
        position.playbackRate = isPlaying(state) ? state.getPlaybackSpeed() : 0.0;
        position.duration = micros(durationMs * 1000);
        position.position = micros(Math.max(0, state.getPosition()) * 1000);
        position.lastUpdatedTime = ticks(monotonicMicros(state.getLastPositionUpdateTime()));
        position.endOfMedia = false;
        return position;
    }

    private static Bitmap extractArtwork(android.media.MediaMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        Bitmap art = metadata.getBitmap(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART);
        if (art == null) {
            art = metadata.getBitmap(android.media.MediaMetadata.METADATA_KEY_ART);
        }
        if (art == null) {
            art = metadata.getBitmap(android.media.MediaMetadata.METADATA_KEY_DISPLAY_ICON);
        }
        return art;
    }

    private static MediaImage[] buildArtworkList(Bitmap artwork) {
        if (artwork == null) {
            return null;
        }
        MediaImage image = new MediaImage();
        image.src = new Url();
        image.src.url = ARTWORK_URL;
        image.type = toString16("image/png");
        Size size = new Size();
        size.width = artwork.getWidth();
        size.height = artwork.getHeight();
        image.sizes = new Size[] {size};
        return new MediaImage[] {image};
    }

    /**
     * Repacks an album art bitmap the way Chrome's MediaImageBitmap traits
     * expect it: tightly packed rows, premultiplied alpha, colour type stated
     * explicitly.
     */
    private static MediaImageBitmap buildImageBitmap(Bitmap source, int minimumSizePx,
            int desiredSizePx) {
        if (source == null) {
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        if (width <= 0 || height <= 0 || Math.max(width, height) < minimumSizePx) {
            return null;
        }
        if (desiredSizePx > 0 && Math.max(width, height) > desiredSizePx) {
            float scale = (float) desiredSizePx / Math.max(width, height);
            width = Math.max(1, Math.round(width * scale));
            height = Math.max(1, Math.round(height * scale));
        }

        Bitmap scaled;
        try {
            scaled = Bitmap.createScaledBitmap(source, width, height, true);
        } catch (RuntimeException e) {
            Log.w(TAG, "Could not scale artwork", e);
            return null;
        }
        if (scaled.getConfig() != Bitmap.Config.ARGB_8888) {
            Bitmap converted = scaled.copy(Bitmap.Config.ARGB_8888, false);
            if (converted == null) {
                return null;
            }
            scaled = converted;
        }

        byte[] pixels = new byte[width * height * 4];
        if (scaled.getRowBytes() == width * 4) {
            // ARGB_8888 is R,G,B,A in memory order and already premultiplied.
            scaled.copyPixelsToBuffer(ByteBuffer.wrap(pixels));
        } else {
            int[] argb = new int[width * height];
            scaled.getPixels(argb, 0, width, 0, 0, width, height);
            for (int i = 0; i < argb.length; i++) {
                int color = argb[i];
                int alpha = (color >>> 24) & 0xff;
                pixels[i * 4] = premultiply((color >>> 16) & 0xff, alpha);
                pixels[i * 4 + 1] = premultiply((color >>> 8) & 0xff, alpha);
                pixels[i * 4 + 2] = premultiply(color & 0xff, alpha);
                pixels[i * 4 + 3] = (byte) alpha;
            }
        }

        MediaImageBitmap bitmap = new MediaImageBitmap();
        bitmap.width = width;
        bitmap.height = height;
        bitmap.pixelData = pixels;
        bitmap.colorType = MediaImageBitmapColorType.RGBA_8888;
        return bitmap;
    }

    private static byte premultiply(int channel, int alpha) {
        return (byte) ((channel * alpha + 127) / 255);
    }

    // --- Change detection ---

    private static int diff(Snapshot previous, Snapshot current) {
        int changes = 0;
        if (previous.info == null || !sameInfo(previous.info, current.info)) {
            changes |= CHANGE_INFO;
        }
        if (!sameMetadata(previous.metadata, current.metadata)) {
            changes |= CHANGE_METADATA;
        }
        if (!Arrays.equals(previous.actions, current.actions)) {
            changes |= CHANGE_ACTIONS;
        }
        if (!samePosition(previous.position, current.position)) {
            changes |= CHANGE_POSITION;
        }
        if (previous.artworkBitmap != current.artworkBitmap) {
            changes |= CHANGE_IMAGES;
        }
        return changes;
    }

    private static boolean sameInfo(MediaSessionInfo a, MediaSessionInfo b) {
        return a.state == b.state && a.playbackState == b.playbackState
                && a.isControllable == b.isControllable && a.muted == b.muted;
    }

    private static boolean sameMetadata(MediaMetadata a, MediaMetadata b) {
        if (a == null || b == null) {
            return a == b;
        }
        return sameString16(a.title, b.title) && sameString16(a.artist, b.artist)
                && sameString16(a.album, b.album) && sameString16(a.sourceTitle, b.sourceTitle);
    }

    private static boolean sameString16(String16 a, String16 b) {
        if (a == null || b == null) {
            return a == b;
        }
        return Arrays.equals(a.data, b.data);
    }

    private static boolean samePosition(MediaPosition a, MediaPosition b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.playbackRate == b.playbackRate
                && a.duration.microseconds == b.duration.microseconds
                && a.position.microseconds == b.position.microseconds
                && a.lastUpdatedTime.internalValue == b.lastUpdatedTime.internalValue;
    }

    // --- Playback state helpers ---

    private static boolean isPlaying(PlaybackState state) {
        if (state == null) {
            return false;
        }
        switch (state.getState()) {
            case PlaybackState.STATE_PLAYING:
            case PlaybackState.STATE_BUFFERING:
            case PlaybackState.STATE_CONNECTING:
            case PlaybackState.STATE_FAST_FORWARDING:
            case PlaybackState.STATE_REWINDING:
            case PlaybackState.STATE_SKIPPING_TO_NEXT:
            case PlaybackState.STATE_SKIPPING_TO_PREVIOUS:
            case PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM:
                return true;
            default:
                return false;
        }
    }

    /** True once the session has stopped for good rather than merely paused. */
    private static boolean isFinished(PlaybackState state) {
        if (state == null) {
            return true;
        }
        switch (state.getState()) {
            case PlaybackState.STATE_NONE:
            case PlaybackState.STATE_STOPPED:
            case PlaybackState.STATE_ERROR:
                return true;
            default:
                return false;
        }
    }

    // --- Small conversions ---

    private static String resolveAppLabel(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        if (pm == null || packageName == null) {
            return packageName != null ? packageName : "";
        }
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            return pm.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    private static String firstOf(android.media.MediaMetadata metadata, String... keys) {
        if (metadata == null) {
            return "";
        }
        for (String key : keys) {
            String value = metadata.getString(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return "";
    }

    private static String16 toString16(String value) {
        String16 out = new String16();
        String text = value != null ? value : "";
        out.data = new short[text.length()];
        for (int i = 0; i < text.length(); i++) {
            out.data[i] = (short) text.charAt(i);
        }
        return out;
    }

    private static TimeDelta micros(long microseconds) {
        TimeDelta delta = new TimeDelta();
        delta.microseconds = microseconds;
        return delta;
    }

    private static TimeTicks ticks(long microseconds) {
        TimeTicks time = new TimeTicks();
        time.internalValue = microseconds;
        return time;
    }

    /**
     * Converts a PlaybackState timestamp (elapsed realtime, which keeps running
     * across suspend) into the CLOCK_MONOTONIC microseconds Chrome's TimeTicks
     * are measured in.
     */
    private static long monotonicMicros(long elapsedRealtimeMs) {
        long now = SystemClock.uptimeMillis();
        if (elapsedRealtimeMs <= 0) {
            return now * 1000;
        }
        long ageMs = SystemClock.elapsedRealtime() - elapsedRealtimeMs;
        return Math.max(0, now - ageMs) * 1000;
    }
}

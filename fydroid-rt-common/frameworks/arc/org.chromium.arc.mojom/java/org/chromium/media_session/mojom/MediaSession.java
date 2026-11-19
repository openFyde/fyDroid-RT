package org.chromium.media_session.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.TimeDelta;

public interface MediaSession extends Interface {
    public static final Interface.Manager<MediaSession, Proxy> MANAGER = MediaSession_Internal.MANAGER;

    public interface GetDebugInfoResponse extends Callbacks.Callback1<MediaSessionDebugInfo> {
    }

    public interface GetMediaImageBitmapResponse extends Callbacks.Callback1<MediaImageBitmap> {
    }

    public interface GetMediaSessionInfoResponse extends Callbacks.Callback1<MediaSessionInfo> {
    }

    public interface Proxy extends MediaSession, Interface.Proxy {
    }

    public static final class SuspendType {
        public static final int CONTENT = 2;
        public static final int SYSTEM = 0;
        public static final int UI = 1;

        private SuspendType() {
        }

        public static boolean isKnownValue(int i) {
            return i == 0 || i == 1 || i == 2;
        }

        public static void validate(int i) {
        }
    }

    void addObserver(MediaSessionObserver mediaSessionObserver);

    void enterPictureInPicture();

    void exitPictureInPicture();

    void getDebugInfo(GetDebugInfoResponse getDebugInfoResponse);

    void getMediaImageBitmap(MediaImage mediaImage, int i, int i2, GetMediaImageBitmapResponse getMediaImageBitmapResponse);

    void getMediaSessionInfo(GetMediaSessionInfoResponse getMediaSessionInfoResponse);

    void hangUp();

    void nextSlide();

    void nextTrack();

    void previousSlide();

    void previousTrack();

    void raise();

    void requestMediaRemoting();

    void resume(int i);

    void scrubTo(TimeDelta timeDelta);

    void seek(TimeDelta timeDelta);

    void seekTo(TimeDelta timeDelta);

    void setAudioSinkId(String str);

    void setMute(boolean z);

    void skipAd();

    void startDucking();

    void stop(int i);

    void stopDucking();

    void suspend(int i);

    void toggleCamera();

    void toggleMicrophone();
}

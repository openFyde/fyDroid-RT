package org.chromium.media_session.mojom;

public final class MediaAudioVideoState {
    public static final int AUDIO_ONLY = 1;
    public static final int AUDIO_VIDEO = 2;
    public static final int DEPRECATED_UNKNOWN = 0;
    public static final int VIDEO_ONLY = 3;

    private MediaAudioVideoState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

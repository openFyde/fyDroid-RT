package org.chromium.media_session.mojom;

public final class MediaPlaybackState {
    public static final int PAUSED = 0;
    public static final int PLAYING = 1;

    private MediaPlaybackState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

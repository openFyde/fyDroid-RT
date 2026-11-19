package org.chromium.media_session.mojom;

public final class MediaPictureInPictureState {
    public static final int IN_PICTURE_IN_PICTURE = 1;
    public static final int NOT_IN_PICTURE_IN_PICTURE = 0;

    private MediaPictureInPictureState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

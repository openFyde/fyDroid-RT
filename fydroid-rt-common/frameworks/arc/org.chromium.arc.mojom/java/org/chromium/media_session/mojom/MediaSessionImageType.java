package org.chromium.media_session.mojom;

public final class MediaSessionImageType {
    public static final int ARTWORK = 0;
    public static final int SOURCE_ICON = 1;

    private MediaSessionImageType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

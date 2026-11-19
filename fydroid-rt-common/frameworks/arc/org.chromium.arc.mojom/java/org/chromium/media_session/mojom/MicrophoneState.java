package org.chromium.media_session.mojom;

public final class MicrophoneState {
    public static final int MUTED = 1;
    public static final int UNKNOWN = 0;
    public static final int UNMUTED = 2;

    private MicrophoneState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

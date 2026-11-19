package org.chromium.arc.mojom;

public final class MessageCenterVisibility {
    public static final int VISIBILITY_MESSAGE_CENTER = 1;
    public static final int VISIBILITY_TRANSIENT = 0;

    private MessageCenterVisibility() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

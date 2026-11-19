package org.chromium.arc.mojom;

public final class AudioSwitch {
    public static final int SW_HEADPHONE_INSERT = 2;
    public static final int SW_MICROPHONE_INSERT = 4;

    private AudioSwitch() {
    }

    public static boolean isKnownValue(int i) {
        return i == 2 || i == 4;
    }

    public static void validate(int i) {
    }
}

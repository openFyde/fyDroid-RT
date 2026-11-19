package org.chromium.arc.mojom;

public final class MountEvent {
    public static final int MOUNTING = 0;
    public static final int UNMOUNTING = 1;

    private MountEvent() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class QosCategory {
    public static final int MULTIMEDIA_CONFERENCING = 2;
    public static final int REALTIME_INTERACTIVE = 1;
    public static final int UNKNOWN = 0;

    private QosCategory() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

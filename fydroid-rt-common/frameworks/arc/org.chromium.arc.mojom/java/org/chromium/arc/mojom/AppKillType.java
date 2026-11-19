package org.chromium.arc.mojom;

public final class AppKillType {
    public static final int GMS_START_KILL = 3;
    public static final int GMS_UPDATE_KILL = 2;
    public static final int LMKD_KILL = 0;
    public static final int OOM_KILL = 1;

    private AppKillType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

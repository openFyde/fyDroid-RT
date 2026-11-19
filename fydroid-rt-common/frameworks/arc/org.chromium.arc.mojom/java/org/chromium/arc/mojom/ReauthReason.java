package org.chromium.arc.mojom;

public final class ReauthReason {
    public static final int ANDROID_ID_MISSING = 0;
    public static final int DPC_OUT_OF_SYNC = 2;
    public static final int MAIN_ACCOUNT_UNKNOWN = 1;
    public static final int NOT_NEEDED = 3;

    private ReauthReason() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}

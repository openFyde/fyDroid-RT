package org.chromium.arc.mojom;

public final class MetricsType {
    public static final int ACCOUNT_CHECK_MILLISECONDS = 4;
    public static final int CHECKIN_ATTEMPTS = 1;
    public static final int CHECKIN_TIME_MILLISECONDS = 2;
    public static final int NETWORK_WAITING_TIME_MILLISECONDS = 0;
    public static final int SIGNIN_TIME_MILLISECONDS = 3;

    private MetricsType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

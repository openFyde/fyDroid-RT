package org.chromium.arc.mojom;

public final class AccountCheckStatus {
    public static final int CHECK_FAILED = 4;
    public static final int NEED_REAUTH = 2;
    public static final int NEW = 1;
    public static final int UNKNOWN = 3;
    public static final int UP_TO_DATE = 0;

    private AccountCheckStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class ChromeAccountType {
    public static final int ACTIVE_DIRECTORY_ACCOUNT = 3;
    public static final int CHILD_ACCOUNT = 4;
    public static final int OFFLINE_DEMO_ACCOUNT = 5;
    public static final int ROBOT_ACCOUNT = 2;
    public static final int UNKNOWN = 0;
    public static final int USER_ACCOUNT = 1;

    private ChromeAccountType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    public static void validate(int i) {
    }
}

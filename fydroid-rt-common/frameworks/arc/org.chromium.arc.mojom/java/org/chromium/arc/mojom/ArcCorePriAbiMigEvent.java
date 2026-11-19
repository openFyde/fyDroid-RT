package org.chromium.arc.mojom;

public final class ArcCorePriAbiMigEvent {
    public static final int GMS_DOWNGRADE_FAILURE = 4;
    public static final int GMS_DOWNGRADE_SUCCESS = 3;
    public static final int GMS_NO_DOWNGRADE = 2;
    public static final int MIGRATION_COMPLETED = 1;
    public static final int UNSUPPORTED = 0;
    public static final int WEBVIEW_DOWNGRADE_FAILURE = 7;
    public static final int WEBVIEW_DOWNGRADE_SUCCESS = 6;
    public static final int WEBVIEW_NO_DOWNGRADE = 5;

    private ArcCorePriAbiMigEvent() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

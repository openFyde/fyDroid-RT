package org.chromium.arc.mojom;

public final class AppDiscoveryRequestState {
    public static final int CANCELED = 1;
    public static final int ERROR_DEPRECATED = 2;
    public static final int FAILED_TO_CALL_CANCEL = 4;
    public static final int FAILED_TO_CALL_FINDAPPS = 5;
    public static final int PHONESKY_APP_DISCOVERY_NOT_AVAILABLE = 11;
    public static final int PHONESKY_INTERNAL_ERROR = 15;
    public static final int PHONESKY_MALFORMED_QUERY = 14;
    public static final int PHONESKY_REQUEST_REQUEST_CODE_UNMATCHED = 10;
    public static final int PHONESKY_RESULT_INVALID_DATA = 16;
    public static final int PHONESKY_RESULT_REQUEST_CODE_UNMATCHED = 8;
    public static final int PHONESKY_RESULT_SESSION_ID_UNMATCHED = 9;
    public static final int PHONESKY_UNEXPECTED_EXCEPTION = 13;
    public static final int PHONESKY_VERSION_NOT_SUPPORTED = 12;
    public static final int PLAY_STORE_PROXY_NOT_AVAILABLE = 3;
    public static final int REQUEST_HAS_INVALID_PARAMS = 6;
    public static final int REQUEST_TIMEOUT = 7;
    public static final int SUCCESS = 0;

    private AppDiscoveryRequestState() {
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
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

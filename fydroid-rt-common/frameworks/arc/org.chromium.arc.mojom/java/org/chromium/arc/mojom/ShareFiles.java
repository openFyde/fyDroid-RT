package org.chromium.arc.mojom;

public final class ShareFiles {
    public static final int BAD_RESPONSE_FROM_DOCUMENTS_UI = 6;
    public static final int MULTIPLE_FILES_BACKROLLED = 4;
    public static final int MULTIPLE_FILES_MISC_ERROR = 5;
    public static final int MULTIPLE_FILES_SUCCESS = 3;
    public static final int SINGLE_FILE_BACKROLLED = 1;
    public static final int SINGLE_FILE_MISC_ERROR = 2;
    public static final int SINGLE_FILE_SUCCESS = 0;

    private ShareFiles() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}

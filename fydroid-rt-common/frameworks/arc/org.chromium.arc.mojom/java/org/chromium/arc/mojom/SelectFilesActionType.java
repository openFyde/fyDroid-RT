package org.chromium.arc.mojom;

public final class SelectFilesActionType {
    public static final int CREATE_DOCUMENT = 3;
    public static final int GET_CONTENT = 0;
    public static final int OPEN_DOCUMENT = 1;
    public static final int OPEN_DOCUMENT_TREE = 2;
    public static final int OPEN_MEDIA_STORE_FILES = 4;

    private SelectFilesActionType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}

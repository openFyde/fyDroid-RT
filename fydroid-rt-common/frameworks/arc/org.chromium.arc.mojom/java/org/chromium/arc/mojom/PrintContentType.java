package org.chromium.arc.mojom;

public final class PrintContentType {
    public static final int DOCUMENT = 0;
    public static final int PHOTO = 1;
    public static final int UNKNOWN = -1;

    private PrintContentType() {
    }

    public static boolean isKnownValue(int i) {
        return i == -1 || i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

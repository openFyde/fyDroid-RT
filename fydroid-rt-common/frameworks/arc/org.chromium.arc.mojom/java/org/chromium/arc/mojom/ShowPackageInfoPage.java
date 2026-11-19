package org.chromium.arc.mojom;

public final class ShowPackageInfoPage {
    public static final int MAIN = 0;
    public static final int MANAGE_LINKS = 1;
    public static final int MANAGE_PERMISSIONS = 2;

    private ShowPackageInfoPage() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

package org.chromium.arc.mojom;

public final class AppShortcutItemType {
    public static final int DYNAMIC = 1;
    public static final int STATIC = 0;

    private AppShortcutItemType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}

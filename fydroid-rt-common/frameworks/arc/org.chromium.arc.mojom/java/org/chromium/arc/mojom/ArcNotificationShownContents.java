package org.chromium.arc.mojom;

public final class ArcNotificationShownContents {
    public static final int CONTENTS_SHOWN = 0;
    public static final int SETTINGS_SHOWN = 1;
    public static final int SNOOZE_SHOWN = 2;

    private ArcNotificationShownContents() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}

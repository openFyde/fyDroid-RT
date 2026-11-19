package io.fydeos.arc.policymanager;

/**
 * App install disposition from the ArcPolicy "installType" field. The ordinal is what
 * PolicyDbHelper stores, so new values are only ever appended.
 */
enum InstallType {
    AVAILABLE, BLOCKED, FORCE_INSTALLED,
    /** Not force-installed: FydroidPolicyManager opens its Play Store page once for the user. */
    PLAY_PROMPT,
    /**
     * PLAY_PROMPT that may not be uninstalled once the user has installed it: what Google Admin
     * console's force types (FORCE_INSTALLED without downloadUrl, REQUIRED, KIOSK,
     * REQUIRED_FOR_SETUP) translate to. Never named in JSON; derived from the parsed entry.
     */
    PLAY_PROMPT_LOCKED;

    boolean isPlayPrompt() {
        return this == PLAY_PROMPT || this == PLAY_PROMPT_LOCKED;
    }

    int toInt() {
        return ordinal();
    }

    /** Case-insensitive lookup by name; returns null for unknown/unsupported types. */
    static InstallType byName(String input) {
        if (input == null) {
            return null;
        }
        for (InstallType t : values()) {
            if (t.name().equalsIgnoreCase(input)) {
                return t;
            }
        }
        return null;
    }

    static InstallType byIndex(int index) {
        InstallType[] values = values();
        return (index >= 0 && index < values.length) ? values[index] : AVAILABLE;
    }
}

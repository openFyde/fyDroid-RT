package io.fydeos.arc.installpolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The app-install part of an ArcPolicy JSON document: the {@code applications} entries plus the
 * top-level install keys. One parser for FydroidPolicyManager and ArcPolicyService, so both accept
 * and skip exactly the same entries and resolve a package to the same entry.
 *
 * <p>ArcPolicy reaches the device from either the FydeOS admin console (the format below) or the
 * Google Admin console (Android Management API names). Both arrive through the same Chrome
 * {@code ArcPolicy} path, so this parser accepts either, field by field, and translates Google's
 * into ours (see {@link #translateInstallType} and the {@code KEY_GOOGLE_*} keys):
 * <ul>
 *   <li>{@code playStoreMode} WHITELIST = ALLOWLIST; BLACKLIST and SUPERVISED = BLOCKLIST.
 *   <li>{@code installUnknownSourcesDisabled: true} = {@code installUnknownSourcesAllowed: false}
 *       (ours wins when both are present).
 *   <li>{@code installType} INSTALL_TYPE_UNSPECIFIED = AVAILABLE. Google's force types —
 *       FORCE_INSTALLED without a {@code downloadUrl}, REQUIRED, KIOSK, REQUIRED_FOR_SETUP — mean
 *       "Play installs it silently", which fydroid cannot do (no Play install API), so they become
 *       PLAY_PROMPT with {@link Entry#uninstallBlocked}: the user is sent to the Play page once and
 *       cannot uninstall the app afterwards. PRELOAD becomes a plain PLAY_PROMPT.
 *   <li>A missing per-app {@code defaultPermissionPolicy} falls back to the top-level one, then
 *       PERMISSION_POLICY_UNSPECIFIED.
 * </ul>
 *
 * <pre>
 * {
 *   "playStoreMode": "ALLOWLIST" | "BLOCKLIST",       // default BLOCKLIST
 *   "installUnknownSourcesAllowed": true | false,     // absent = no restriction
 *   "playStoreEnabled": true | false,                 // absent = enabled
 *   "applications": [
 *     { "packageName": "com.example.app",             // literal, "*", "prefix*" or regex
 *       "installType": "AVAILABLE" | "BLOCKED" | "FORCE_INSTALLED" | "PLAY_PROMPT",
 *       "defaultPermissionPolicy": "PROMPT" | "GRANT" | "DENY" | "PERMISSION_POLICY_UNSPECIFIED",
 *                                                     // optional, see the fallback below
 *       "downloadUrl": "https://..." }                 // FORCE_INSTALLED: FPM downloads it
 *   ]
 * }
 * </pre>
 *
 * <p>An entry missing {@code packageName}, with an unknown {@code installType} or
 * {@code defaultPermissionPolicy}, or with a malformed {@code downloadUrl}, is skipped (and listed
 * in {@link #skipped}). FORCE_INSTALLED without a {@code downloadUrl} is Google's meaning (see
 * below), not an error. When several entries share a
 * {@code packageName} the last one wins, as in FydroidPolicyManager's database.
 *
 * <p>{@code ALLOWLIST} is implemented as an implicit {@code "*" BLOCKED} entry (unless the policy
 * already has an explicit {@code "*"}). It is the least specific entry possible, so every listed
 * package, literal or pattern, still governs itself.
 */
public final class AppInstallPolicy {
    public static final String AVAILABLE = "AVAILABLE";
    public static final String BLOCKED = "BLOCKED";
    public static final String FORCE_INSTALLED = "FORCE_INSTALLED";
    public static final String PLAY_PROMPT = "PLAY_PROMPT";

    // Google Admin console (Android Management API) spellings, translated on parse.
    private static final String G_UNSPECIFIED = "INSTALL_TYPE_UNSPECIFIED";
    private static final String G_REQUIRED = "REQUIRED";
    private static final String G_KIOSK = "KIOSK";
    private static final String G_REQUIRED_FOR_SETUP = "REQUIRED_FOR_SETUP";
    private static final String G_PRELOAD = "PRELOAD";
    private static final String[] INSTALL_TYPES = {
        AVAILABLE, BLOCKED, FORCE_INSTALLED, PLAY_PROMPT,
        G_UNSPECIFIED, G_REQUIRED, G_KIOSK, G_REQUIRED_FOR_SETUP, G_PRELOAD
    };

    private static final String[] PERMISSION_POLICIES = {
        "PERMISSION_POLICY_UNSPECIFIED", "PROMPT", "GRANT", "DENY"
    };

    public static final String KEY_APPLICATIONS = "applications";
    public static final String KEY_PLAY_STORE_MODE = "playStoreMode";
    public static final String KEY_INSTALL_UNKNOWN_SOURCES_ALLOWED = "installUnknownSourcesAllowed";
    public static final String KEY_PLAY_STORE_ENABLED = "playStoreEnabled";
    /** Google's inverse of {@link #KEY_INSTALL_UNKNOWN_SOURCES_ALLOWED}. */
    public static final String KEY_GOOGLE_INSTALL_UNKNOWN_SOURCES_DISABLED =
            "installUnknownSourcesDisabled";
    /** Google's policy-wide default for entries without their own. */
    public static final String KEY_DEFAULT_PERMISSION_POLICY = "defaultPermissionPolicy";

    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_INSTALL_TYPE = "installType";
    private static final String KEY_PERMISSION_POLICY = "defaultPermissionPolicy";
    private static final String KEY_DOWNLOAD_URL = "downloadUrl";

    /** {@code playStoreMode}. */
    public enum PlayStoreMode {
        /** Only listed packages may be installed (implicit {@code "*" BLOCKED}). */
        ALLOWLIST,
        /** Everything not explicitly BLOCKED may be installed. The default. */
        BLOCKLIST
    }

    /** One valid {@code applications} entry. */
    public static final class Entry {
        public final String packageName;
        /** False for a literal package name, true for {@code *}, a prefix or a regex. */
        public final boolean isPattern;
        /** One of the {@code AVAILABLE} / {@code BLOCKED} / ... constants (upper case). */
        public final String installType;
        /** One of PERMISSION_POLICY_UNSPECIFIED / PROMPT / GRANT / DENY (upper case). */
        public final String permissionPolicy;
        /** Present (and a valid URL) for FORCE_INSTALLED; may be null otherwise. */
        public final String downloadUrl;
        /**
         * Once installed, the app may not be uninstalled: always for FORCE_INSTALLED, and for a
         * PLAY_PROMPT translated from one of Google's force types.
         */
        public final boolean uninstallBlocked;
        /** True only for the entry ALLOWLIST synthesizes. */
        public final boolean implicit;

        public Entry(String packageName, String installType, String permissionPolicy,
                String downloadUrl, boolean uninstallBlocked, boolean implicit) {
            this.packageName = packageName;
            this.isPattern = !PackagePatterns.isLiteral(packageName);
            this.installType = installType;
            this.permissionPolicy = permissionPolicy;
            this.downloadUrl = downloadUrl;
            this.uninstallBlocked = uninstallBlocked;
            this.implicit = implicit;
        }

        public boolean matches(String target) {
            return PackagePatterns.matches(packageName, isPattern, target);
        }

        @Override
        public String toString() {
            return packageName + ":" + installType + (uninstallBlocked ? "+locked" : "")
                    + (implicit ? " (implicit)" : "");
        }
    }

    /** Most specific entry first; see {@link PackagePatterns}. */
    public static final Comparator<Entry> PRECEDENCE = (a, b) ->
            PackagePatterns.compare(a.packageName, a.isPattern, b.packageName, b.isPattern);

    /** Valid entries, in precedence order, including the ALLOWLIST one. */
    public final List<Entry> entries;
    public final PlayStoreMode playStoreMode;
    /** Null when the key is absent. */
    public final Boolean installUnknownSourcesAllowed;
    /** Null when the key is absent. */
    public final Boolean playStoreEnabled;
    /** Why each skipped entry was skipped, for logging by the caller. */
    public final List<String> skipped;

    private AppInstallPolicy(List<Entry> entries, PlayStoreMode playStoreMode,
            Boolean installUnknownSourcesAllowed, Boolean playStoreEnabled, List<String> skipped) {
        this.entries = Collections.unmodifiableList(entries);
        this.playStoreMode = playStoreMode;
        this.installUnknownSourcesAllowed = installUnknownSourcesAllowed;
        this.playStoreEnabled = playStoreEnabled;
        this.skipped = Collections.unmodifiableList(skipped);
    }

    /** Parses a whole ArcPolicy document. Throws only if it is not a JSON object. */
    public static AppInstallPolicy parse(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        List<String> skipped = new ArrayList<>();
        String defaultPerm = canonical(root.optString(KEY_DEFAULT_PERMISSION_POLICY, null),
                PERMISSION_POLICIES);
        if (defaultPerm == null) {
            defaultPerm = "PERMISSION_POLICY_UNSPECIFIED";
        }
        Map<String, Entry> byName = new LinkedHashMap<>();
        JSONArray apps = root.optJSONArray(KEY_APPLICATIONS);
        if (apps != null) {
            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.optJSONObject(i);
                String why = null;
                Entry entry = null;
                if (app == null) {
                    why = "not an object";
                } else {
                    String name = app.optString(KEY_PACKAGE_NAME, null);
                    String rawType = canonical(app.optString(KEY_INSTALL_TYPE, null), INSTALL_TYPES);
                    boolean hasPerm = app.has(KEY_PERMISSION_POLICY)
                            && !app.isNull(KEY_PERMISSION_POLICY);
                    String perm = hasPerm
                            ? canonical(app.optString(KEY_PERMISSION_POLICY, null),
                                    PERMISSION_POLICIES)
                            : defaultPerm;
                    String url = app.has(KEY_DOWNLOAD_URL) && !app.isNull(KEY_DOWNLOAD_URL)
                            ? app.optString(KEY_DOWNLOAD_URL) : null;
                    if (name == null || name.isEmpty()) {
                        why = "no packageName";
                    } else if (rawType == null) {
                        why = name + ": unsupported installType";
                    } else if (perm == null) {
                        why = name + ": unsupported defaultPermissionPolicy";
                    } else if (url != null && !isValidUrl(url)) {
                        why = name + ": malformed downloadUrl";
                    } else {
                        String type = translateInstallType(rawType, url != null);
                        boolean locked = FORCE_INSTALLED.equals(type)
                                || (PLAY_PROMPT.equals(type) && isGoogleForceType(rawType, url != null));
                        entry = new Entry(name, type, perm, url, locked, false);
                    }
                }
                if (entry != null) {
                    byName.remove(entry.packageName); // last one wins, and keeps its position
                    byName.put(entry.packageName, entry);
                } else {
                    skipped.add("applications[" + i + "]: " + why);
                }
            }
        }

        PlayStoreMode mode = PlayStoreMode.BLOCKLIST;
        String modeName = root.optString(KEY_PLAY_STORE_MODE, null);
        if (modeName != null) {
            switch (modeName.toUpperCase(Locale.ROOT)) {
                case "ALLOWLIST":
                case "WHITELIST": // Google
                    mode = PlayStoreMode.ALLOWLIST;
                    break;
                case "BLOCKLIST":
                case "BLACKLIST": // Google
                case "SUPERVISED": // Google, child accounts: Play's own parental controls apply
                    mode = PlayStoreMode.BLOCKLIST;
                    break;
                default:
                    skipped.add(KEY_PLAY_STORE_MODE + ": unknown value " + modeName
                            + ", using BLOCKLIST");
            }
        }

        Boolean unknownSourcesAllowed = optBoolean(root, KEY_INSTALL_UNKNOWN_SOURCES_ALLOWED);
        if (unknownSourcesAllowed == null) {
            Boolean disabled = optBoolean(root, KEY_GOOGLE_INSTALL_UNKNOWN_SOURCES_DISABLED);
            if (disabled != null) {
                unknownSourcesAllowed = !disabled;
            }
        }

        List<Entry> entries = new ArrayList<>(byName.values());
        if (mode == PlayStoreMode.ALLOWLIST && !byName.containsKey("*")) {
            entries.add(new Entry("*", BLOCKED, "PERMISSION_POLICY_UNSPECIFIED", null, false, true));
        }
        entries.sort(PRECEDENCE);
        return new AppInstallPolicy(entries, mode, unknownSourcesAllowed,
                optBoolean(root, KEY_PLAY_STORE_ENABLED), skipped);
    }

    /** Maps an accepted installType (ours or Google's) to one of ours. */
    static String translateInstallType(String type, boolean hasDownloadUrl) {
        switch (type) {
            case G_UNSPECIFIED:
                return AVAILABLE;
            case FORCE_INSTALLED:
                return hasDownloadUrl ? FORCE_INSTALLED : PLAY_PROMPT;
            case G_REQUIRED:
            case G_KIOSK:
            case G_REQUIRED_FOR_SETUP:
            case G_PRELOAD:
                return PLAY_PROMPT;
            default:
                return type; // AVAILABLE, BLOCKED, PLAY_PROMPT
        }
    }

    /** Google's "Play installs it and the user can't remove it" types (PRELOAD is removable). */
    private static boolean isGoogleForceType(String type, boolean hasDownloadUrl) {
        return (FORCE_INSTALLED.equals(type) && !hasDownloadUrl) || G_REQUIRED.equals(type)
                || G_KIOSK.equals(type) || G_REQUIRED_FOR_SETUP.equals(type);
    }

    /** The entry that governs {@code packageName}, or null if none matches. */
    public Entry resolve(String packageName) {
        for (Entry e : entries) {
            if (e.matches(packageName)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Whether policy lets {@code packageName} be installed: false only when the governing entry
     * is BLOCKED. Who is installing and whether the package is a system app are the caller's
     * business (see ArcPolicyService's gate).
     */
    public boolean isInstallAllowed(String packageName) {
        Entry e = resolve(packageName);
        return e == null || !BLOCKED.equals(e.installType);
    }

    private static String canonical(String value, String[] allowed) {
        if (value == null) {
            return null;
        }
        for (String a : allowed) {
            if (a.equalsIgnoreCase(value)) {
                return a;
            }
        }
        return null;
    }

    private static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static Boolean optBoolean(JSONObject root, String key) {
        if (!root.has(key) || root.isNull(key)) {
            return null;
        }
        Object v = root.opt(key);
        if (v instanceof Boolean) {
            return (Boolean) v;
        }
        if ("true".equalsIgnoreCase(String.valueOf(v))) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(String.valueOf(v))) {
            return Boolean.FALSE;
        }
        return null;
    }
}

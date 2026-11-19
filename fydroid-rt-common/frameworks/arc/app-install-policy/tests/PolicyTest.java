import io.fydeos.arc.installpolicy.AppInstallPolicy;
import io.fydeos.arc.installpolicy.AppInstallPolicy.Entry;
import io.fydeos.arc.installpolicy.PackagePatterns;

import java.util.List;

/** Host test of the shared ArcPolicy app-install library. */
public class PolicyTest {
    static int failures = 0;

    static void check(String what, Object got, Object want) {
        boolean ok = (got == null) ? want == null : got.equals(want);
        System.out.println((ok ? "PASS " : "FAIL ") + what + " -> " + got + (ok ? "" : " (want " + want + ")"));
        if (!ok) failures++;
    }

    static String type(AppInstallPolicy p, String pkg) {
        Entry e = p.resolve(pkg);
        return e == null ? null : e.packageName + ":" + e.installType + (e.uninstallBlocked ? "+locked" : "");
    }

    /** What FPM's PolicyDbHelper.getPolicy does: best matching row by PackagePatterns.compare. */
    static String fpmPick(List<Entry> rows, String pkg) {
        Entry best = null;
        for (Entry r : rows) {
            if (r.matches(pkg) && (best == null
                    || PackagePatterns.compare(r.packageName, r.isPattern, best.packageName, best.isPattern) < 0)) {
                best = r;
            }
        }
        return best == null ? null : best.packageName;
    }

    static String app(String name, String type) {
        return "{\"packageName\":\"" + name + "\",\"installType\":\"" + type
                + "\",\"defaultPermissionPolicy\":\"PROMPT\"}";
    }

    public static void main(String[] a) throws Exception {
        // --- matching ---
        check("literal", PackagePatterns.isLiteral("com.foo"), true);
        check("star not literal", PackagePatterns.isLiteral("*"), false);
        check("prefix match", PackagePatterns.matches("com.foo.*", true, "com.foo.bar"), true);
        check("prefix no match", PackagePatterns.matches("com.foo.*", true, "com.food"), false);
        check("regex find", PackagePatterns.matches("^com\\.foo$", true, "com.foo"), true);
        check("bad regex false", PackagePatterns.matches("com.(foo", true, "com.foo"), false);

        // --- #4: exact beats any pattern, even a longer one; ties deterministic ---
        AppInstallPolicy p4 = AppInstallPolicy.parse("{\"applications\":["
                + app("com.foo", "AVAILABLE") + "," + app("^com\\\\.foo(\\\\..*)?$", "BLOCKED") + ","
                + app("com.fo*", "BLOCKED") + "]}");
        check("#4 exact beats longer regex", type(p4, "com.foo"), "com.foo:AVAILABLE");
        check("#4 longer pattern wins for others", type(p4, "com.foo.x"), "^com\\.foo(\\..*)?$:BLOCKED");
        check("#4 FPM pick == gate resolve", fpmPick(p4.entries, "com.foo"), "com.foo");
        AppInstallPolicy tie = AppInstallPolicy.parse("{\"applications\":["
                + app("com.b*", "BLOCKED") + "," + app("com.a*", "AVAILABLE") + "," + app("^com.*", "AVAILABLE") + "]}");
        check("tie: same-length patterns ordered by string", tie.entries.get(0).packageName, "^com.*");

        // --- #2 scenario: allowlist via "*" BLOCKED + exact AVAILABLE ---
        AppInstallPolicy p2 = AppInstallPolicy.parse("{\"applications\":["
                + app("*", "BLOCKED") + "," + app("com.allowed", "AVAILABLE") + "]}");
        check("#2 allowed app allowed", p2.isInstallAllowed("com.allowed"), true);
        check("#2 other app blocked", p2.isInstallAllowed("com.other"), false);

        // --- playStoreMode ALLOWLIST: implicit "*" BLOCKED, least specific ---
        AppInstallPolicy al = AppInstallPolicy.parse("{\"playStoreMode\":\"ALLOWLIST\",\"applications\":["
                + app("com.ok", "AVAILABLE") + "," + app("org.ok.*", "AVAILABLE") + "]}");
        check("ALLOWLIST listed exact", al.isInstallAllowed("com.ok"), true);
        check("ALLOWLIST listed prefix", al.isInstallAllowed("org.ok.app"), true);
        check("ALLOWLIST unlisted", al.isInstallAllowed("com.evil"), false);
        check("ALLOWLIST implicit entry last", al.entries.get(al.entries.size() - 1).implicit, true);
        AppInstallPolicy alStar = AppInstallPolicy.parse("{\"playStoreMode\":\"ALLOWLIST\",\"applications\":["
                + app("*", "AVAILABLE") + "]}");
        check("ALLOWLIST with explicit * AVAILABLE", alStar.isInstallAllowed("com.any"), true);
        AppInstallPolicy bl = AppInstallPolicy.parse("{\"applications\":[" + app("com.bad", "BLOCKED") + "]}");
        check("BLOCKLIST default allows unlisted", bl.isInstallAllowed("com.any"), true);
        check("BLOCKLIST blocks listed", bl.isInstallAllowed("com.bad"), false);
        check("no policy entries -> allow", AppInstallPolicy.parse("{}").isInstallAllowed("x.y"), true);

        // --- FydeOS format keys ---
        AppInstallPolicy f = AppInstallPolicy.parse("{\"installUnknownSourcesAllowed\":false,"
                + "\"playStoreEnabled\":false,\"applications\":["
                + "{\"packageName\":\"com.dl\",\"installType\":\"FORCE_INSTALLED\",\"defaultPermissionPolicy\":\"GRANT\","
                + "\"downloadUrl\":\"https://example.com/a.apk\"},"
                + app("com.pp", "PLAY_PROMPT") + ","
                + "{\"packageName\":\"com.badurl\",\"installType\":\"FORCE_INSTALLED\",\"defaultPermissionPolicy\":\"GRANT\","
                + "\"downloadUrl\":\"not a url\"},"
                + "{\"packageName\":\"com.badtype\",\"installType\":\"NOPE\"}]}");
        check("fydeos unknownSources", f.installUnknownSourcesAllowed, false);
        check("fydeos playStoreEnabled", f.playStoreEnabled, false);
        check("fydeos FORCE_INSTALLED+url", type(f, "com.dl"), "com.dl:FORCE_INSTALLED+locked");
        check("fydeos PLAY_PROMPT unlocked", type(f, "com.pp"), "com.pp:PLAY_PROMPT");
        check("malformed url skipped", f.resolve("com.badurl"), null);
        check("bad type skipped", f.resolve("com.badtype"), null);
        check("skipped count", f.skipped.size(), 2);

        // --- Google Admin console format ---
        AppInstallPolicy g = AppInstallPolicy.parse("{\"playStoreMode\":\"WHITELIST\","
                + "\"installUnknownSourcesDisabled\":true,\"defaultPermissionPolicy\":\"GRANT\","
                + "\"guid\":\"x\",\"cameraDisabled\":false,\"applications\":["
                + "{\"packageName\":\"com.g.force\",\"installType\":\"FORCE_INSTALLED\"},"
                + "{\"packageName\":\"com.g.req\",\"installType\":\"REQUIRED\"},"
                + "{\"packageName\":\"com.g.kiosk\",\"installType\":\"KIOSK\"},"
                + "{\"packageName\":\"com.g.setup\",\"installType\":\"REQUIRED_FOR_SETUP\"},"
                + "{\"packageName\":\"com.g.pre\",\"installType\":\"PRELOAD\"},"
                + "{\"packageName\":\"com.g.unspec\",\"installType\":\"INSTALL_TYPE_UNSPECIFIED\"},"
                + "{\"packageName\":\"com.g.avail\",\"installType\":\"AVAILABLE\",\"defaultPermissionPolicy\":\"DENY\","
                + "\"managedConfiguration\":{\"k\":1}},"
                + "{\"packageName\":\"com.g.block\",\"installType\":\"BLOCKED\"}]}");
        check("google WHITELIST", g.playStoreMode, AppInstallPolicy.PlayStoreMode.ALLOWLIST);
        check("google unknownSourcesDisabled", g.installUnknownSourcesAllowed, false);
        check("google FORCE_INSTALLED no url", type(g, "com.g.force"), "com.g.force:PLAY_PROMPT+locked");
        check("google REQUIRED", type(g, "com.g.req"), "com.g.req:PLAY_PROMPT+locked");
        check("google KIOSK", type(g, "com.g.kiosk"), "com.g.kiosk:PLAY_PROMPT+locked");
        check("google REQUIRED_FOR_SETUP", type(g, "com.g.setup"), "com.g.setup:PLAY_PROMPT+locked");
        check("google PRELOAD", type(g, "com.g.pre"), "com.g.pre:PLAY_PROMPT");
        check("google UNSPECIFIED", type(g, "com.g.unspec"), "com.g.unspec:AVAILABLE");
        check("google perm fallback top-level", g.resolve("com.g.force").permissionPolicy, "GRANT");
        check("google per-app perm", g.resolve("com.g.avail").permissionPolicy, "DENY");
        check("google allowlist: listed force allowed", g.isInstallAllowed("com.g.force"), true);
        check("google allowlist: unlisted blocked", g.isInstallAllowed("com.unlisted"), false);
        check("google blocked", g.isInstallAllowed("com.g.block"), false);
        check("google skipped none", g.skipped.size(), 0);
        check("google BLACKLIST", AppInstallPolicy.parse("{\"playStoreMode\":\"BLACKLIST\"}").playStoreMode,
                AppInstallPolicy.PlayStoreMode.BLOCKLIST);
        check("google SUPERVISED", AppInstallPolicy.parse("{\"playStoreMode\":\"SUPERVISED\"}").playStoreMode,
                AppInstallPolicy.PlayStoreMode.BLOCKLIST);
        check("ours wins over google unknown sources",
                AppInstallPolicy.parse("{\"installUnknownSourcesAllowed\":true,\"installUnknownSourcesDisabled\":true}")
                        .installUnknownSourcesAllowed, true);
        check("duplicate name: last wins",
                type(AppInstallPolicy.parse("{\"applications\":[" + app("com.d", "BLOCKED") + ","
                        + app("com.d", "AVAILABLE") + "]}"), "com.d"), "com.d:AVAILABLE");

        System.out.println(failures == 0 ? "ALL PASS" : failures + " FAILURE(S)");
        System.exit(failures == 0 ? 0 : 1);
    }
}

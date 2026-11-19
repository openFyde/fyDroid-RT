/* Open source replacement for org.chromium.arc.applauncher.AppShortcutConstants
 * from the proprietary com.google.android.chromeos jar.
 *
 * Bundle keys used between arc-services (ArcAppShortcutsArcHomeService) and
 * its clients. Values copied verbatim from the proprietary class so that
 * Bundles produced by either implementation deserialize identically.
 */
package org.chromium.arc.applauncher;

public class AppShortcutConstants {
    public static final String BUNDLE_KEY_BACKGROUND_ICON =
            "ArcAppShortcutsArcHomeService.backgroundIcon";
    public static final String BUNDLE_KEY_FOREGROUND_ICON =
            "ArcAppShortcutsArcHomeService.foregroundIcon";
    public static final String BUNDLE_KEY_ICON =
            "ArcAppShortcutsArcHomeService.icon";
    public static final String BUNDLE_KEY_IS_ADAPTIVE_ICON =
            "ArcAppShortcutsArcHomeService.isAdaptiveIcon";
    public static final String BUNDLE_KEY_PACKAGE_NAME =
            "ArcAppShortcutsArcHomeService.packageName";
    public static final String BUNDLE_KEY_RANK =
            "ArcAppShortcutsArcHomeService.rank";
    public static final String BUNDLE_KEY_SHORTCUT_ID =
            "ArcAppShortcutsArcHomeService.shortcutId";
    public static final String BUNDLE_KEY_SHORT_LABEL =
            "ArcAppShortcutsArcHomeService.shortLabel";
    public static final String BUNDLE_KEY_TYPE =
            "ArcAppShortcutsArcHomeService.type";

    private AppShortcutConstants() {
    }
}

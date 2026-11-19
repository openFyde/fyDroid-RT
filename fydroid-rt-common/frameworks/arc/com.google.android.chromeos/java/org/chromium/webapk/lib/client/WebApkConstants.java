/* Open source replacement for org.chromium.webapk.lib.client.WebApkConstants from the proprietary com.google.android.chromeos jar (originally from Chromium). */
package org.chromium.webapk.lib.client;

/**
 * Constants shared between Chrome and WebAPK shell APKs. These are part of
 * the open-source Chromium WebAPK client library.
 */
public class WebApkConstants {
    public static final String ACTION_OPEN =
            "com.google.android.apps.chrome.webapps.WebappManager.ACTION_START_WEBAPP";
    public static final String EXTRA_PACKAGE_NAME =
            "org.chromium.chrome.browser.webapk_package_name";
    public static final String EXTRA_SCOPE =
            "org.chromium.chrome.browser.webapp_scope";
    public static final String EXTRA_URL =
            "org.chromium.chrome.browser.webapp_url";
    public static final String EXTRA_WEBAPK_SHARE_TARGET_ACTIVITY_NAME =
            "org.chromium.webapk.selected_share_target_activity_class_name";
    public static final String MANIFEST_ICON_URLS_AND_ICON_MURMUR2_HASHES =
            "org.chromium.webapk.shell_apk.iconUrlsAndIconMurmur2Hashes";
    public static final String MANIFEST_NAME =
            "org.chromium.webapk.shell_apk.name";
    public static final String MANIFEST_SCOPE_URL =
            "org.chromium.webapk.shell_apk.scope";
    public static final String MANIFEST_SHARE_ACTION =
            "org.chromium.webapk.shell_apk.shareAction";
    public static final String MANIFEST_SHARE_ENCTYPE =
            "org.chromium.webapk.shell_apk.shareEnctype";
    public static final String MANIFEST_SHARE_METHOD =
            "org.chromium.webapk.shell_apk.shareMethod";
    public static final String MANIFEST_SHARE_PARAM_ACCEPTS =
            "org.chromium.webapk.shell_apk.shareParamAccepts";
    public static final String MANIFEST_SHARE_PARAM_NAMES =
            "org.chromium.webapk.shell_apk.shareParamNames";
    public static final String MANIFEST_SHARE_PARAM_TEXT =
            "org.chromium.webapk.shell_apk.shareParamText";
    public static final String MANIFEST_SHARE_PARAM_TITLE =
            "org.chromium.webapk.shell_apk.shareParamTitle";
    public static final String MANIFEST_SHARE_PARAM_URL =
            "org.chromium.webapk.shell_apk.shareParamUrl";
    public static final String MANIFEST_SHELL_APK_VERSION =
            "org.chromium.webapk.shell_apk.shellApkVersion";
    public static final String MANIFEST_SHORT_NAME =
            "org.chromium.webapk.shell_apk.shortName";
    public static final String MANIFEST_START_URL =
            "org.chromium.webapk.shell_apk.startUrl";
    public static final String MANIFEST_THEME_COLOR =
            "org.chromium.webapk.shell_apk.themeColor";
    public static final String MANIFEST_WEB_URL =
            "org.chromium.webapk.shell_apk.webManifestUrl";

    /**
     * DER-encoded X.509 SubjectPublicKeyInfo for the EC public key used to
     * verify the comment-signed WebAPK signature. Bytes are copied verbatim
     * from the proprietary jar so any caller that compares against this
     * constant keeps working unchanged.
     */
    public static final byte[] PUBLIC_KEY = {
            48, 89, 48, 19, 6, 7, 42, -122, 72, -50, 61, 2, 1, 6, 8, 42,
            -122, 72, -50, 61, 3, 1, 7, 3, 66, 0, 4, -25, 45, 2, 49, 44,
            -60, 107, -108, -45, 27, -40, -8, -116, 44, 7, -38, -103, 52, -81, 33, -90,
            -80, -94, 125, -3, -67, 51, -125, -63, 6, -127, 89, 32, 53, 83, -120, -106,
            -113, -121, -39, 115, -50, 15, 117, 66, 78, -89, -124, -120, 4, -61, 8, -90,
            -67, -6, 71, -120, -120, 23, 23, 77, 75, 103, -28
    };
}

/* Open source replacement for org.chromium.arc.applauncher.WebAppHelper
 * from the proprietary com.google.android.chromeos jar.
 *
 * Inspects a package to decide whether it is a WebAPK or a web-only TWA,
 * and extracts the manifest fields (title, start URL, scope URL, theme
 * color, ...) into a WebAppInfo mojom struct. Depends on the
 * org.chromium.webapk.lib.client.* helpers (cloned separately).
 *
 * Public API preserved so existing callers compile and link unchanged.
 */
package org.chromium.arc.applauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import org.chromium.arc.mojom.WebAppInfo;
import org.chromium.webapk.lib.client.WebApkConstants;
import org.chromium.webapk.lib.client.WebApkMetaDataUtils;
import org.chromium.webapk.lib.client.WebApkValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.function.Function;

public class WebAppHelper {
    private static final String TAG = "ArcWebAppHelper";

    private static final String GENERATED_WEBAPK_PACKAGE_PREFIX = "org.chromium.webapk.";
    private static final char[] HEX_CHAR_LOOKUP = "0123456789ABCDEF".toCharArray();
    private static final long INVALID_COLOR = 2147483648L;
    private static final String RESOURCE_LOCATION_RAW = "raw";
    private static final String RESOURCE_STRING_TYPE = "string";
    private static final String WEB_APP_MANIFEST_FIELD_NAME = "name";
    private static final String WEB_APP_MANIFEST_FIELD_SCOPE = "scope";
    private static final String WEB_APP_MANIFEST_FIELD_SHORT_NAME = "short_name";
    private static final String WEB_APP_MANIFEST_FIELD_START_URL = "start_url";
    private static final String WEB_APP_MANIFEST_FIELD_THEME_COLOR = "theme_color";

    static final String WEB_APP_MANIFEST_URL_KEY = "web_manifest_url";
    static final String WEB_APP_RAW_RESOURCE_MANIFEST_JSON = "web_app_manifest";
    private static final String WEB_APP_RESOURCE_NAME = "name";
    private static final String WEB_APP_RESOURCE_SHORT_NAME = "short_name";

    private static String sFingerprintForTest;

    public static boolean isValidWebAppApk(Context context, String packageName) {
        return isValidWebOnlyTwa(context, packageName)
                || WebApkValidator.isValidWebApk(context, packageName);
    }

    public static boolean isGeneratedWebApk(String packageName) {
        return packageName.startsWith(GENERATED_WEBAPK_PACKAGE_PREFIX);
    }

    public static WebAppInfo getWebAppInfoForPackage(
            Context context, PackageManager packageManager, String packageName) {
        if (WebApkValidator.isValidWebApk(context, packageName)) {
            WebAppInfo webAppInfo = getWebAppInfoFromWebApk(packageManager, packageName);
            if (webAppInfo != null) {
                return webAppInfo;
            }
        }
        if (isValidWebOnlyTwa(context, packageName)) {
            WebAppInfo webAppInfo = getWebAppInfoFromWebOnlyTwa(packageManager, packageName);
            if (webAppInfo != null) {
                return webAppInfo;
            }
        }
        return null;
    }

    private static WebAppInfo getWebAppInfoFromWebApk(
            PackageManager packageManager, String packageName) {
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA);
            Bundle metadata = info.metaData;
            Resources res = packageManager.getResourcesForApplication(packageName);
            int nameId = res.getIdentifier(WEB_APP_RESOURCE_NAME, RESOURCE_STRING_TYPE,
                    packageName);
            int shortNameId = res.getIdentifier(WEB_APP_RESOURCE_SHORT_NAME,
                    RESOURCE_STRING_TYPE, packageName);
            String name = nameId != 0
                    ? res.getString(nameId)
                    : metadata.getString(WebApkConstants.MANIFEST_NAME);
            String shortName = shortNameId != 0
                    ? res.getString(shortNameId)
                    : metadata.getString(WebApkConstants.MANIFEST_SHORT_NAME);
            String title = name != null ? name : shortName;
            String startUrl = metadata.getString(WebApkConstants.MANIFEST_START_URL);
            String scopeUrl = metadata.getString(WebApkConstants.MANIFEST_SCOPE_URL);
            long themeColor = WebApkMetaDataUtils.getLongFromMetaData(
                    metadata, WebApkConstants.MANIFEST_THEME_COLOR, INVALID_COLOR);
            if (title != null && startUrl != null && scopeUrl != null) {
                WebAppInfo webAppInfo = new WebAppInfo();
                webAppInfo.title = title;
                webAppInfo.startUrl = startUrl;
                webAppInfo.scopeUrl = scopeUrl;
                webAppInfo.themeColor = themeColor;
                webAppInfo.isWebOnlyTwa = false;
                return webAppInfo;
            }
            Log.w(TAG, "Web app info could not be retrieved for " + packageName);
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Cannot return web app info for unknown package: " + packageName);
            return null;
        }
    }

    private static WebAppInfo getWebAppInfoFromWebOnlyTwa(
            PackageManager packageManager, String packageName) {
        try {
            Optional<String> webManifestUrl = getWebOnlyTwaManifestUrl(
                    packageManager, packageName);
            if (!webManifestUrl.isPresent()) {
                return null;
            }
            Resources res = packageManager.getResourcesForApplication(packageName);
            int webAppManifestId = res.getIdentifier(
                    WEB_APP_RAW_RESOURCE_MANIFEST_JSON, RESOURCE_LOCATION_RAW, packageName);
            if (webAppManifestId == 0) {
                Log.w(TAG, "File res/raw/web_app_manifest.json not found for "
                        + "web-only TWA package: " + packageName);
                return null;
            }
            String manifestPath = getContainingDirectoryOfUrl(webManifestUrl.get()) + "/";
            if (manifestPath == null) {
                Log.e(TAG, "Error when loading web-only TWA metadata from " + packageName
                        + ": Could not resolve the containing directory of "
                        + "\"web_manifest_url\".");
                return null;
            }
            JSONObject webManifest = readJsonObjectFromRawFile(res, webAppManifestId);
            if (webManifest == null) {
                Log.e(TAG, "Error when loading web-only TWA metadata from " + packageName
                        + ": res/raw/web_app_manifest.json is not valid JSON.");
                return null;
            }
            String name = readStringFromJsonObject(webManifest, WEB_APP_MANIFEST_FIELD_NAME);
            String shortName = readStringFromJsonObject(webManifest,
                    WEB_APP_MANIFEST_FIELD_SHORT_NAME);
            String title = name != null ? name : shortName;
            if (title == null) {
                Log.e(TAG, "Error when loading web-only TWA metadata from " + packageName
                        + ": res/raw/web_app_manifest.json is missing the required "
                        + "\"name\" field.");
                return null;
            }
            String rawStartUrl = readStringFromJsonObject(
                    webManifest, WEB_APP_MANIFEST_FIELD_START_URL);
            if (rawStartUrl == null) {
                Log.e(TAG, "Error when loading web-only TWA metadata from " + packageName
                        + ": res/raw/web_app_manifest.json is missing the required "
                        + "\"start_url\" field.");
                return null;
            }
            String startUrl = resolveRelativeURL(rawStartUrl, manifestPath);
            if (startUrl == null) {
                Log.e(TAG, "Error when loading web-only TWA metadata from " + packageName
                        + ": could not resolve the \"start_url\" field into a "
                        + "valid URL.");
                return null;
            }
            String scope = readStringFromJsonObject(webManifest,
                    WEB_APP_MANIFEST_FIELD_SCOPE);
            String scopeUrl;
            if (scope == null) {
                scopeUrl = getContainingDirectoryOfUrl(startUrl) + "/";
            } else {
                scopeUrl = resolveRelativeURL(scope, manifestPath);
            }
            if (scopeUrl == null) {
                Log.e(TAG, "Error when loading web-only TWA metadata from " + packageName
                        + ": could not resolve the \"scope\" field into a valid URL.");
                return null;
            }
            long themeColor = readHexLongFromJsonObject(
                    webManifest, WEB_APP_MANIFEST_FIELD_THEME_COLOR, INVALID_COLOR);
            WebAppInfo webAppInfo = new WebAppInfo();
            webAppInfo.title = title;
            webAppInfo.startUrl = startUrl;
            webAppInfo.scopeUrl = scopeUrl;
            webAppInfo.themeColor = themeColor;
            webAppInfo.isWebOnlyTwa = true;
            webAppInfo.certificateSha256Fingerprint =
                    getCertificateSHA256FingerprintForPackage(packageManager, packageName);
            return webAppInfo;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String resolveRelativeURL(String childUrl, String parentUrl) {
        try {
            URI parentUri = new URI(parentUrl);
            return parentUri.resolve(childUrl).toString();
        } catch (IllegalArgumentException | URISyntaxException e) {
            Log.e(TAG, "Error resolving web-only TWA URL: ", e);
            return null;
        }
    }

    private static JSONObject readJsonObjectFromRawFile(
            Resources resources, int fileResourceId) {
        try {
            InputStream fileStream = resources.openRawResource(fileResourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
            return new JSONObject(builder.toString());
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    private static String readStringFromJsonObject(JSONObject json, String field) {
        try {
            return json.getString(field);
        } catch (JSONException e) {
            return null;
        }
    }

    private static long readHexLongFromJsonObject(
            JSONObject json, String field, long defaultValue) {
        String number = readStringFromJsonObject(json, field);
        if (number != null) {
            try {
                if (number.length() > 0) {
                    return Long.parseLong(number.substring(1), 16);
                }
            } catch (NumberFormatException e) {
                // Fall through to default.
            }
        }
        return defaultValue;
    }

    public static String getContainingDirectoryOfUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            int lastSlashIndex = path == null ? -1 : path.lastIndexOf("/");
            if (lastSlashIndex < 0) {
                path = "/";
            } else if (lastSlashIndex < path.length() - 1) {
                path = path.substring(0, lastSlashIndex + 1);
            }
            URI directory = new URI(
                    uri.getScheme(), null, uri.getHost(), uri.getPort(),
                    path, null, null);
            String directoryPath = directory.toString();
            return directoryPath.substring(0, directoryPath.length() - 1);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error resolving containing directory of web-only TWA URL: ", e);
            return null;
        }
    }

    public static boolean isValidWebOnlyTwa(Context context, String packageName) {
        try {
            if (!getWebOnlyTwaManifestUrl(context.getPackageManager(), packageName)
                    .isPresent()) {
                return false;
            }
            Resources res = context.getPackageManager()
                    .getResourcesForApplication(packageName);
            int webAppManifestId = res.getIdentifier(
                    WEB_APP_RAW_RESOURCE_MANIFEST_JSON, RESOURCE_LOCATION_RAW, packageName);
            return webAppManifestId != 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static Optional<String> getWebOnlyTwaManifestUrl(
            PackageManager packageManager, String packageName) {
        try {
            return Optional.ofNullable(
                    packageManager.getApplicationInfo(
                            packageName, PackageManager.GET_META_DATA))
                    .map(new Function<ApplicationInfo, Bundle>() {
                        @Override
                        public Bundle apply(ApplicationInfo info) {
                            return info.metaData;
                        }
                    })
                    .map(new Function<Bundle, String>() {
                        @Override
                        public String apply(Bundle bundle) {
                            return bundle.getString(WEB_APP_MANIFEST_URL_KEY);
                        }
                    });
        } catch (PackageManager.NameNotFoundException e) {
            return Optional.empty();
        }
    }

    static void setCertificateFingerprintForTest(String fingerprint) {
        sFingerprintForTest = fingerprint;
    }

    private static String getCertificateSHA256FingerprintForPackage(
            PackageManager packageManager, String packageName)
            throws PackageManager.NameNotFoundException {
        if (sFingerprintForTest != null) {
            return sFingerprintForTest;
        }
        PackageInfo packageInfo = packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNATURES);
        InputStream input = new ByteArrayInputStream(
                packageInfo.signatures[0].toByteArray());
        try {
            X509Certificate certificate = (X509Certificate) CertificateFactory
                    .getInstance("X509").generateCertificate(input);
            return byteArrayToHexString(
                    MessageDigest.getInstance("SHA256").digest(certificate.getEncoded()));
        } catch (CertificateEncodingException e) {
            Log.w(TAG, "Certificate type X509 encoding failed");
            return null;
        } catch (NoSuchAlgorithmException | CertificateException e) {
            return null;
        }
    }

    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder((byteArray.length * 3) - 1);
        for (int i = 0; i < byteArray.length; i++) {
            hexString.append(HEX_CHAR_LOOKUP[(byteArray[i] & 0xF0) >>> 4]);
            hexString.append(HEX_CHAR_LOOKUP[byteArray[i] & 0x0F]);
            if (i < byteArray.length - 1) {
                hexString.append(':');
            }
        }
        return hexString.toString();
    }
}

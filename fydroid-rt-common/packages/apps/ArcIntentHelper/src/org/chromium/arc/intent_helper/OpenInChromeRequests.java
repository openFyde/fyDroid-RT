package org.chromium.arc.intent_helper;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Converts an intent an Android app sent to the container's browser into the
 * request OpenInChromeReceiver (arc-services) turns into an IntentHelperHost
 * call. The conversions follow stock ARC's IntentToUrlConverter, except that
 * geo: goes to OpenStreetMap and searches use Chrome's default search engine.
 */
final class OpenInChromeRequests {
    private static final String TAG = "ArcIntentHelperActivity";

    // Must match OpenInChromeReceiver in arc-services.
    private static final String ACTION_OPEN_URL = "org.chromium.arc.intent_helper.OPEN_URL";
    private static final String ACTION_OPEN_SEARCH = "org.chromium.arc.intent_helper.OPEN_SEARCH";
    private static final String ACTION_OPEN_WEB_APP =
            "org.chromium.arc.intent_helper.OPEN_WEB_APP";
    private static final String ACTION_OPEN_WEBAPK = "org.chromium.arc.intent_helper.OPEN_WEBAPK";
    private static final String ACTION_OPEN_DOWNLOADS =
            "org.chromium.arc.intent_helper.OPEN_DOWNLOADS";
    private static final String ACTION_OPEN_SCAN_APP =
            "org.chromium.arc.intent_helper.OPEN_SCAN_APP";
    private static final String EXTRA_URL = "url";
    private static final String EXTRA_QUERY = "query";
    private static final String EXTRA_WEBAPK_PACKAGE = "webapk_package";
    private static final String EXTRA_SHARE_ACTION = "share_action";
    private static final String EXTRA_SUBJECT = "subject";
    private static final String EXTRA_TEXT = "text";

    // Chromium's WebAPK shell (chrome/android/webapk/libs/common WebApkConstants).
    private static final String ACTION_START_WEBAPP =
            "com.google.android.apps.chrome.webapps.WebappManager.ACTION_START_WEBAPP";
    private static final String EXTRA_WEBAPK_PACKAGE_NAME =
            "org.chromium.chrome.browser.webapk_package_name";
    private static final String EXTRA_WEBAPP_URL = "org.chromium.chrome.browser.webapp_url";
    private static final String EXTRA_WEBAPK_SHARE_TARGET =
            "org.chromium.webapk.selected_share_target_activity_class_name";
    // androidx.browser TrustedWebActivityIntentBuilder.
    private static final String EXTRA_LAUNCH_AS_TRUSTED_WEB_ACTIVITY =
            "android.support.customtabs.extra.LAUNCH_AS_TRUSTED_WEB_ACTIVITY";

    // Public: asks for the ChromeOS Scan app, which scans with the host's
    // scanners and saves to My files.
    private static final String ACTION_SCAN = "org.chromium.arc.intent_helper.action.SCAN";

    private static final String OSM = "https://www.openstreetmap.org/";
    private static final int OSM_DEFAULT_ZOOM = 15;
    private static final int OSM_MAX_ZOOM = 19;

    private OpenInChromeRequests() {
    }

    /** Returns the request for {@code intent}, or null if Chrome cannot handle it. */
    static Intent fromIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return null;
        }
        Uri data = intent.getData();
        String scheme = data == null ? null : data.getScheme();
        switch (action) {
            case Intent.ACTION_VIEW:
                if ("http".equals(scheme) || "https".equals(scheme)) {
                    return fromWebIntent(intent);
                }
                if ("mailto".equals(scheme)) {
                    return openUrl(buildMailToUrl(intent));
                }
                if ("geo".equals(scheme)) {
                    return openUrl(buildOpenStreetMapUrl(data));
                }
                return null;
            case Intent.ACTION_SENDTO:
                return "mailto".equals(scheme) ? openUrl(buildMailToUrl(intent)) : null;
            case Intent.ACTION_SEND:
                return "message/rfc822".equals(intent.getType())
                        ? openUrl(buildMailToUrl(intent)) : null;
            case Intent.ACTION_WEB_SEARCH:
            case SearchManager.INTENT_ACTION_GLOBAL_SEARCH: {
                String query = intent.getStringExtra(SearchManager.QUERY);
                if (Intent.ACTION_WEB_SEARCH.equals(action) && query != null
                        && (query.startsWith("http://") || query.startsWith("https://"))) {
                    return openUrl(query);
                }
                return new Intent(ACTION_OPEN_SEARCH).putExtra(EXTRA_QUERY, query);
            }
            case ACTION_START_WEBAPP:
                return fromWebApkIntent(intent);
            case DownloadManager.ACTION_VIEW_DOWNLOADS:
                return new Intent(ACTION_OPEN_DOWNLOADS);
            case ACTION_SCAN:
                return new Intent(ACTION_OPEN_SCAN_APP);
            default:
                return null;
        }
    }

    private static Intent openUrl(String url) {
        return new Intent(ACTION_OPEN_URL).putExtra(EXTRA_URL, url);
    }

    private static Intent fromWebIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_WEBAPK_PACKAGE_NAME)) {
            return fromWebApkIntent(intent);
        }
        String url = intent.getDataString();
        if (intent.getBooleanExtra(EXTRA_LAUNCH_AS_TRUSTED_WEB_ACTIVITY, false)) {
            return new Intent(ACTION_OPEN_WEB_APP).putExtra(EXTRA_URL, url);
        }
        return openUrl(url);
    }

    private static Intent fromWebApkIntent(Intent intent) {
        String packageName = intent.getStringExtra(EXTRA_WEBAPK_PACKAGE_NAME);
        String url = intent.getStringExtra(EXTRA_WEBAPP_URL);
        if (url == null) {
            url = intent.getDataString();
        }
        if (packageName == null || !isWebAppUrl(url)) {
            Log.e(TAG, "Invalid WebAPK launch: " + intent);
            return null;
        }
        Intent request = new Intent(ACTION_OPEN_WEBAPK)
                .putExtra(EXTRA_WEBAPK_PACKAGE, packageName)
                .putExtra(EXTRA_URL, url);
        if (intent.getStringExtra(EXTRA_WEBAPK_SHARE_TARGET) != null) {
            request.putExtra(EXTRA_SHARE_ACTION, Intent.ACTION_SEND)
                    .putExtra(EXTRA_SUBJECT, getText(intent.getExtras(), Intent.EXTRA_SUBJECT))
                    .putExtra(EXTRA_TEXT, getText(intent.getExtras(), Intent.EXTRA_TEXT));
            if (intent.hasExtra(Intent.EXTRA_STREAM)) {
                Log.w(TAG, "WebAPK share: files are not forwarded to Chrome");
            }
        }
        return request;
    }

    /** Chrome only launches web apps for https, or http on localhost. */
    private static boolean isWebAppUrl(String url) {
        if (url == null) {
            return false;
        }
        Uri uri = Uri.parse(url);
        return "https".equals(uri.getScheme())
                || ("http".equals(uri.getScheme()) && "localhost".equals(uri.getHost()));
    }

    /** Merges a mailto: URI with the EXTRA_EMAIL/CC/BCC/SUBJECT/TEXT extras. */
    static String buildMailToUrl(Intent intent) {
        Bundle extras = intent.getExtras();
        String data = intent.getDataString();
        MailTo mailTo = MailTo.isMailTo(data) ? MailTo.parse(data) : null;

        List<String> to = getAddresses(extras, Intent.EXTRA_EMAIL,
                mailTo == null ? null : mailTo.getTo());
        List<String> cc = getAddresses(extras, Intent.EXTRA_CC,
                mailTo == null ? null : mailTo.getCc());
        List<String> bcc = getAddresses(extras, Intent.EXTRA_BCC,
                mailTo == null ? null : mailTo.getHeaders().get("bcc"));
        String subject = getText(extras, Intent.EXTRA_SUBJECT);
        if (subject == null && mailTo != null) {
            subject = mailTo.getSubject();
        }
        String body = getText(extras, Intent.EXTRA_TEXT);
        if (body == null && mailTo != null) {
            body = mailTo.getBody();
        }

        StringBuilder url = new StringBuilder("mailto:");
        url.append(Uri.encode(TextUtils.join(",", to), "@,"));
        List<String> params = new ArrayList<>();
        if (!cc.isEmpty()) {
            params.add("cc=" + Uri.encode(TextUtils.join(",", cc), "@,"));
        }
        if (!bcc.isEmpty()) {
            params.add("bcc=" + Uri.encode(TextUtils.join(",", bcc), "@,"));
        }
        if (subject != null) {
            params.add("subject=" + Uri.encode(subject));
        }
        if (body != null) {
            params.add("body=" + Uri.encode(body));
        }
        if (!params.isEmpty()) {
            url.append('?').append(TextUtils.join("&", params));
        }
        return url.toString();
    }

    private static List<String> getAddresses(Bundle extras, String key, String fromUri) {
        List<String> addresses = new ArrayList<>();
        if (extras != null) {
            String[] array = extras.getStringArray(key);
            if (array != null) {
                Collections.addAll(addresses, array);
            } else if (extras.getString(key) != null) {
                addresses.add(extras.getString(key));
            }
        }
        if (!TextUtils.isEmpty(fromUri)) {
            Collections.addAll(addresses, TextUtils.split(fromUri, ","));
        }
        return addresses;
    }

    private static String getText(Bundle extras, String key) {
        CharSequence text = extras == null ? null : extras.getCharSequence(key);
        return text == null ? null : text.toString();
    }

    /**
     * geo:lat,lng[;u=..][?z=zoom] opens a marker there; geo:0,0?q=lat,lng(label)
     * likewise; geo:0,0?q=address searches for the address.
     */
    static String buildOpenStreetMapUrl(Uri geo) {
        String ssp = geo.getEncodedSchemeSpecificPart();
        if (ssp == null) {
            return OSM;
        }
        String coordinates = ssp;
        String query = null;
        int queryStart = ssp.indexOf('?');
        if (queryStart >= 0) {
            coordinates = ssp.substring(0, queryStart);
            query = ssp.substring(queryStart + 1);
        }
        int paramsStart = coordinates.indexOf(';');
        if (paramsStart >= 0) {
            coordinates = coordinates.substring(0, paramsStart);
        }

        String zoom = null;
        String q = null;
        if (query != null) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq < 0) {
                    continue;
                }
                String value = Uri.decode(pair.substring(eq + 1).replace('+', ' '));
                switch (pair.substring(0, eq)) {
                    case "z":
                        zoom = value;
                        break;
                    case "q":
                        q = value;
                        break;
                }
            }
        }

        String[] latLng = parseLatLng(Uri.decode(coordinates));
        if (latLng != null && !isOrigin(latLng)) {
            return buildMarkerUrl(latLng, zoom);
        }
        if (!TextUtils.isEmpty(q)) {
            int labelStart = q.indexOf('(');
            String[] queryLatLng = parseLatLng(labelStart >= 0 ? q.substring(0, labelStart) : q);
            if (queryLatLng != null) {
                return buildMarkerUrl(queryLatLng, zoom);
            }
            return OSM + "search?query=" + Uri.encode(q);
        }
        return OSM;
    }

    private static String buildMarkerUrl(String[] latLng, String zoom) {
        int z = OSM_DEFAULT_ZOOM;
        try {
            if (zoom != null) {
                z = Math.max(0, Math.min(OSM_MAX_ZOOM, Integer.parseInt(zoom.trim())));
            }
        } catch (NumberFormatException e) {
            // Keep the default zoom.
        }
        return String.format(Locale.ROOT, "%s?mlat=%s&mlon=%s#map=%d/%s/%s",
                OSM, latLng[0], latLng[1], z, latLng[0], latLng[1]);
    }

    /** Returns {lat, lng} as given, or null unless both are in range. */
    private static String[] parseLatLng(String text) {
        String[] parts = text.split(",");
        if (parts.length < 2) {
            return null;
        }
        try {
            String lat = parts[0].trim();
            String lng = parts[1].trim();
            double latValue = Double.parseDouble(lat);
            double lngValue = Double.parseDouble(lng);
            // Written so NaN fails too.
            if (!(Math.abs(latValue) <= 90 && Math.abs(lngValue) <= 180)) {
                return null;
            }
            return new String[] {lat, lng};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isOrigin(String[] latLng) {
        return Double.parseDouble(latLng[0]) == 0 && Double.parseDouble(latLng[1]) == 0;
    }
}

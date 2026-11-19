package com.android.server.arc.intent_helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.chromium.arc.mojom.ChromePage;
import org.chromium.arc.mojom.IntentHelperHost;
import org.chromium.arc.mojom.LaunchIntent;
import org.chromium.url.mojom.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Supplier;

/**
 * Turns requests from ArcIntentHelperActivity (packages/apps/ArcIntentHelper)
 * into IntentHelperHost calls, so links, searches, web apps, the downloads
 * page and the Scan app opened from Android apps show up in Chrome.
 *
 * The activity converts the Android intent (mailto:, geo:, ...) into one of the
 * actions below. This side owns what the activity cannot see: the Mojo host,
 * Chrome's default search engine and other packages' WebAPK metadata.
 */
final class OpenInChromeReceiver extends BroadcastReceiver {
    private static final String TAG = "ArcIntentHelper";

    // Must match packages/apps/ArcIntentHelper. The permission is signature
    // level, so apps cannot skip the activity (and its background-start
    // restrictions) by broadcasting directly.
    private static final String PERMISSION =
            "org.chromium.arc.intent_helper.permission.OPEN_IN_CHROME";
    private static final String ACTION_OPEN_URL =
            "org.chromium.arc.intent_helper.OPEN_URL";
    private static final String ACTION_OPEN_SEARCH =
            "org.chromium.arc.intent_helper.OPEN_SEARCH";
    private static final String ACTION_OPEN_WEB_APP =
            "org.chromium.arc.intent_helper.OPEN_WEB_APP";
    private static final String ACTION_OPEN_WEBAPK =
            "org.chromium.arc.intent_helper.OPEN_WEBAPK";
    private static final String ACTION_OPEN_DOWNLOADS =
            "org.chromium.arc.intent_helper.OPEN_DOWNLOADS";
    private static final String ACTION_OPEN_SCAN_APP =
            "org.chromium.arc.intent_helper.OPEN_SCAN_APP";
    // The only chrome:// URL Chrome takes from ARC (chromium patch 0206); it
    // opens the Scan app. An unpatched Chrome drops it.
    private static final String SCAN_APP_URL = "chrome://scanning/";
    private static final String EXTRA_URL = "url";
    private static final String EXTRA_QUERY = "query";
    private static final String EXTRA_WEBAPK_PACKAGE = "webapk_package";
    private static final String EXTRA_SHARE_ACTION = "share_action";
    private static final String EXTRA_SUBJECT = "subject";
    private static final String EXTRA_TEXT = "text";

    private static final String WEBAPK_PACKAGE_PREFIX = "org.chromium.webapk";
    private static final String WEBAPK_META_START_URL = "org.chromium.webapk.shell_apk.startUrl";

    private final Context mContext;
    private final Supplier<IntentHelperHost> mHost;

    // Chrome's default search engine URL for a placeholder query, pushed by
    // Chrome's ArcSettingsService (SET_DEFAULT_SEARCH_URL) on every ARC boot
    // and whenever the user changes engines. Empty until then, or when Chrome
    // has no usable engine.
    private volatile SearchUrl mSearchUrl = new SearchUrl("", "");

    private static final class SearchUrl {
        final String url;
        final String placeholder;

        SearchUrl(String url, String placeholder) {
            this.url = url;
            this.placeholder = placeholder;
        }
    }

    OpenInChromeReceiver(Context context, Supplier<IntentHelperHost> host) {
        mContext = context;
        mHost = host;
    }

    void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OPEN_URL);
        filter.addAction(ACTION_OPEN_SEARCH);
        filter.addAction(ACTION_OPEN_WEB_APP);
        filter.addAction(ACTION_OPEN_WEBAPK);
        filter.addAction(ACTION_OPEN_DOWNLOADS);
        filter.addAction(ACTION_OPEN_SCAN_APP);
        mContext.registerReceiver(this, filter, PERMISSION, null, Context.RECEIVER_EXPORTED);
    }

    /** Handles Chrome's SET_DEFAULT_SEARCH_URL settings broadcast. */
    void setDefaultSearchUrl(String extras) {
        try {
            JSONObject json = new JSONObject(extras);
            SearchUrl searchUrl = new SearchUrl(
                    json.optString("searchUrl"), json.optString("placeholder"));
            mSearchUrl = searchUrl;
            Log.i(TAG, "Default search URL from Chrome: " + searchUrl.url);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to apply SET_DEFAULT_SEARCH_URL", e);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        IntentHelperHost host = mHost.get();
        if (host == null) {
            Log.w(TAG, "Chrome is not connected, dropping " + intent.getAction());
            return;
        }
        switch (intent.getAction()) {
            case ACTION_OPEN_URL:
                openUrl(host, intent.getStringExtra(EXTRA_URL));
                break;
            case ACTION_OPEN_SEARCH:
                openSearch(host, intent.getStringExtra(EXTRA_QUERY));
                break;
            case ACTION_OPEN_WEB_APP: {
                String url = intent.getStringExtra(EXTRA_URL);
                if (url != null) {
                    Log.i(TAG, "Opening web app in Chrome: " + url);
                    host.onOpenWebApp(url);
                }
                break;
            }
            case ACTION_OPEN_WEBAPK:
                openWebApk(host, intent);
                break;
            case ACTION_OPEN_DOWNLOADS:
                Log.i(TAG, "Opening chrome://downloads");
                host.onOpenChromePage(ChromePage.ABOUTDOWNLOADS);
                break;
            case ACTION_OPEN_SCAN_APP:
                Log.i(TAG, "Opening the Scan app");
                host.onOpenUrl(SCAN_APP_URL);
                break;
        }
    }

    private static void openUrl(IntentHelperHost host, String url) {
        if (url == null) {
            return;
        }
        // Chrome fixes the URL up and only accepts http, https, content, file
        // and mailto.
        Log.i(TAG, "Opening in Chrome: " + url);
        host.onOpenUrl(url);
    }

    private void openSearch(IntentHelperHost host, String query) {
        SearchUrl searchUrl = mSearchUrl;
        if (searchUrl.url.isEmpty() || searchUrl.placeholder.isEmpty()
                || !searchUrl.url.contains(searchUrl.placeholder)) {
            Log.w(TAG, "Chrome sent no default search engine, dropping web search");
            return;
        }
        openUrl(host, searchUrl.url.replace(searchUrl.placeholder,
                Uri.encode(query == null ? "" : query)));
    }

    /**
     * Launches the web app a WebAPK wraps. Chrome only launches it if the app
     * is installed in Chrome; otherwise it opens the URL in a tab.
     */
    private void openWebApk(IntentHelperHost host, Intent intent) {
        String packageName = intent.getStringExtra(EXTRA_WEBAPK_PACKAGE);
        String startUrl = getWebApkStartUrl(packageName);
        if (startUrl == null) {
            Log.w(TAG, "Not a WebAPK, ignoring launch from " + packageName);
            return;
        }
        Url start = new Url();
        start.url = startUrl;
        LaunchIntent launch = new LaunchIntent();
        String url = intent.getStringExtra(EXTRA_URL);
        if (url != null) {
            launch.data = new Url();
            launch.data.url = url;
        }
        String shareAction = intent.getStringExtra(EXTRA_SHARE_ACTION);
        if (shareAction != null) {
            launch.action = shareAction;
            launch.extraSubject = intent.getStringExtra(EXTRA_SUBJECT);
            launch.extraText = intent.getStringExtra(EXTRA_TEXT);
        } else {
            launch.action = Intent.ACTION_VIEW;
        }
        Log.i(TAG, "Opening WebAPK " + packageName + " in Chrome: " + startUrl);
        host.onOpenAppWithIntent(start, launch);
    }

    /**
     * Same check stock ARC shipped (its signature check was switched off): the
     * package must be a WebAPK and carry a start URL.
     */
    private String getWebApkStartUrl(String packageName) {
        if (packageName == null || !packageName.startsWith(WEBAPK_PACKAGE_PREFIX)) {
            return null;
        }
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA);
            Bundle metaData = info.metaData;
            String startUrl = metaData == null ? null : metaData.getString(WEBAPK_META_START_URL);
            return TextUtils.isEmpty(startUrl) ? null : startUrl;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}

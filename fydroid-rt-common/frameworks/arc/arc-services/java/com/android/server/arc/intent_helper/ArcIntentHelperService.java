package com.android.server.arc.intent_helper;

import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import com.android.server.SystemService;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.AccessibilityFeatures;
import org.chromium.arc.mojom.ActivityIcon;
import org.chromium.arc.mojom.ActivityName;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.CaptionStyle;
import org.chromium.arc.mojom.IntentHandlerInfo;
import org.chromium.arc.mojom.IntentHelperHost;
import org.chromium.arc.mojom.IntentHelperInstance;
import org.chromium.arc.mojom.IntentInfo;
import org.chromium.arc.mojom.RawIconPngData;
import org.chromium.arc.mojom.TextSelectionAction;
import org.chromium.arc.mojom.UrlWithMimeType;
import org.chromium.arc.mojom.WindowInfo;
import org.chromium.arc.util.LocaleUtils;
import org.chromium.mojo.system.MojoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Bridges intent handling between Android and Chrome OS via Mojo.
 *
 * Handles URL dispatch, intent resolution, activity icon loading, and
 * intent filter tracking for Chrome's app launcher integration.
 */
class ArcIntentHelperService implements IntentHelperInstance, ArcService {
    private static final String TAG = "ArcIntentHelper";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;
    private static final int DEFAULT_ICON_SIZE = 48;

    private static final String ACTION_SET_LOCALE =
            "org.chromium.arc.intent_helper.SET_LOCALE";
    private static final String ACTION_SET_TIME_ZONE =
            "org.chromium.arc.intent_helper.SET_TIME_ZONE";
    private static final String ACTION_SET_AUTO_TIME_ZONE =
            "org.chromium.arc.intent_helper.SET_AUTO_TIME_ZONE";
    private static final String ACTION_SET_USE_24_HOUR_CLOCK =
            "org.chromium.arc.intent_helper.SET_USE_24_HOUR_CLOCK";
    private static final String ACTION_SET_FONT_SCALE =
            "org.chromium.arc.intent_helper.SET_FONT_SCALE";
    private static final String ACTION_SET_BLUETOOTH_STATE =
            "org.chromium.arc.intent_helper.SET_BLUETOOTH_STATE";

    // From Chrome's ArcSettingsService; see OpenInChromeReceiver.
    private static final String ACTION_SET_DEFAULT_SEARCH_URL =
            "org.chromium.arc.intent_helper.SET_DEFAULT_SEARCH_URL";

    // packages/apps/ArcIntentHelper: the container's browser, which hands links
    // and searches from Android apps to Chrome through OpenInChromeReceiver.
    private static final String INTENT_HELPER_PACKAGE = "org.chromium.arc.intent_helper";

    private final Context mContext;
    private final OpenInChromeReceiver mOpenInChromeReceiver;
    private IntentHelperHost mHost;
    private BroadcastReceiver mPackageReceiver;

    public static class LifeCycle extends SystemService {
        private final ArcIntentHelperService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcIntentHelperService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
        }
    }

    ArcIntentHelperService(Context context) {
        mContext = context;
        mOpenInChromeReceiver = new OpenInChromeReceiver(context, () -> mHost);
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        registerPackageReceiver();
        mOpenInChromeReceiver.register();
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- IntentHelperInstance ---

    @Override
    public void init(IntentHelperHost host, InitResponse response) {
        Log.i(TAG, "init: Chrome connected");
        mHost = host;
        response.call();

        // Push initial intent filters for all packages
        pushAllIntentFilters();
    }

    @Override
    public void handleUrl(String url, String packageName) {
        Log.i(TAG, "handleUrl: " + url + " pkg=" + packageName);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (packageName != null && !packageName.isEmpty()) {
                intent.setPackage(packageName);
            }
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to handle URL: " + url, e);
        }
    }

    private static final String ACTION_INSTALL_PACKAGE =
            "io.fydeos.arc.ArcGateway.action.INSTALL_PACKAGE";
    private static final ComponentName INSTALL_RECEIVER =
            new ComponentName("io.fydeos.arc.gateway",
                    "io.fydeos.arc.gateway.InstallReceiver");

    @Override
    public void handleIntent(IntentInfo intentInfo, ActivityName activityName) {
        Log.i(TAG, "handleIntent: " + intentInfo.action);
        try {
            Intent intent = convertIntentInfo(intentInfo);
            if (activityName != null && activityName.packageName != null
                    && activityName.activityName != null) {
                intent.setComponent(new ComponentName(
                        activityName.packageName,
                        activityName.activityName));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Route ArcGateway install intents to ArcGateway system app
            // via explicit broadcast (must be explicit for background
            // execution policy).
            if (ACTION_INSTALL_PACKAGE.equals(intentInfo.action)) {
                intent.setComponent(INSTALL_RECEIVER);
                mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
                Log.i(TAG, "Routed install intent to ArcGateway");
                return;
            }

            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to handle intent", e);
        }
    }

    @Override
    public void handleIntentWithWindowInfo(IntentInfo intentInfo,
            ActivityName activityName, WindowInfo windowInfo) {
        // Window info is used by Chrome for window placement — we just launch
        handleIntent(intentInfo, activityName);
    }

    @Override
    public void sendBroadcast(String action, String pkg, String cls,
            String extras) {
        Log.i(TAG, "sendBroadcast: " + action);
        switch (action) {
            case ACTION_SET_LOCALE:
                setLocaleFromExtras(extras);
                return;
            case ACTION_SET_TIME_ZONE:
            case ACTION_SET_AUTO_TIME_ZONE:
            case ACTION_SET_USE_24_HOUR_CLOCK:
            case ACTION_SET_FONT_SCALE:
                applySettingFromExtras(action, extras);
                return;
            case ACTION_SET_BLUETOOTH_STATE:
                applyBluetoothStateFromExtras(extras);
                return;
            case ACTION_SET_DEFAULT_SEARCH_URL:
                mOpenInChromeReceiver.setDefaultSearchUrl(extras);
                return;
        }
        try {
            Intent intent = new Intent(action);
            if (pkg != null && !pkg.isEmpty() && cls != null && !cls.isEmpty()) {
                intent.setComponent(new ComponentName(pkg, cls));
            } else if (pkg != null && !pkg.isEmpty()) {
                intent.setPackage(pkg);
            }
            mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send broadcast: " + action, e);
        }
    }

    private void setLocaleFromExtras(String extras) {
        try {
            JSONObject json = new JSONObject(extras);
            String locale = json.optString("locale");
            if (locale.isEmpty()) {
                return;
            }
            LocaleUtils.setLocale(mContext, locale,
                    json.optString("preferredLanguages"));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to apply SET_LOCALE", e);
        }
    }

    @SuppressWarnings("deprecation")
    private void applyBluetoothStateFromExtras(String extras) {
        try {
            JSONObject json = new JSONObject(extras);
            BluetoothManager manager =
                    mContext.getSystemService(BluetoothManager.class);
            BluetoothAdapter adapter =
                    manager == null ? null : manager.getAdapter();
            if (adapter == null) {
                Log.e(TAG, "SET_BLUETOOTH_STATE with no adapter");
                return;
            }
            if (json.getBoolean("enable")) {
                adapter.enable();
            } else {
                adapter.disable();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to apply SET_BLUETOOTH_STATE", e);
        }
    }

    private void applySettingFromExtras(String action, String extras) {
        try {
            JSONObject json = new JSONObject(extras);
            ContentResolver cr = mContext.getContentResolver();
            switch (action) {
                case ACTION_SET_TIME_ZONE: {
                    String zone = json.optString("olsonTimeZone");
                    if (zone.isEmpty()) {
                        Log.e(TAG, "SET_TIME_ZONE is missing olsonTimeZone");
                        return;
                    }
                    mContext.getSystemService(AlarmManager.class).setTimeZone(zone);
                    return;
                }
                case ACTION_SET_AUTO_TIME_ZONE:
                    Settings.Global.putInt(cr, Settings.Global.AUTO_TIME_ZONE,
                            json.getBoolean("autoTimeZone") ? 1 : 0);
                    Settings.Global.putInt(cr, Settings.Global.AUTO_TIME, 1);
                    return;
                case ACTION_SET_USE_24_HOUR_CLOCK:
                    Settings.System.putString(cr, Settings.System.TIME_12_24,
                            json.getBoolean("use24HourClock") ? "24" : "12");
                    return;
                case ACTION_SET_FONT_SCALE: {
                    double scale = json.optDouble("scale", -1.0);
                    if (scale <= 0 || scale > 1.5) {
                        Log.e(TAG, "Invalid font scale: " + scale);
                        return;
                    }
                    float current = Settings.System.getFloat(
                            cr, Settings.System.FONT_SCALE, 1.0f);
                    if (Math.abs(scale - current) > 1e-4) {
                        Settings.System.putFloat(cr, Settings.System.FONT_SCALE,
                                (float) scale);
                    }
                    return;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to apply " + action, e);
        }
    }

    @Override
    public void requestUrlHandlerList(String url,
            RequestUrlHandlerListResponse response) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            response.call(queryHandlers(intent));
        } catch (Exception e) {
            Log.e(TAG, "requestUrlHandlerList failed", e);
            response.call(new IntentHandlerInfo[0]);
        }
    }

    @Override
    public void requestIntentHandlerList(IntentInfo intentInfo,
            RequestIntentHandlerListResponse response) {
        try {
            Intent intent = convertIntentInfo(intentInfo);
            response.call(queryHandlers(intent));
        } catch (Exception e) {
            Log.e(TAG, "requestIntentHandlerList failed", e);
            response.call(new IntentHandlerInfo[0]);
        }
    }

    @Override
    public void requestUrlListHandlerList(UrlWithMimeType[] urls,
            RequestUrlListHandlerListResponse response) {
        // Return handlers for the first URL (Chrome typically sends one)
        if (urls != null && urls.length > 0) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(urls[0].url));
                if (urls[0].mimeType != null && !urls[0].mimeType.isEmpty()) {
                    intent.setType(urls[0].mimeType);
                }
                response.call(queryHandlers(intent));
                return;
            } catch (Exception e) {
                Log.e(TAG, "requestUrlListHandlerList failed", e);
            }
        }
        response.call(new IntentHandlerInfo[0]);
    }

    @Override
    public void requestActivityIcons(ActivityName[] activities,
            int scaleFactor,
            RequestActivityIconsResponse response) {
        PackageManager pm = mContext.getPackageManager();
        int size = DEFAULT_ICON_SIZE * Math.max(1, scaleFactor);
        ArrayList<ActivityIcon> icons = new ArrayList<>();

        for (ActivityName an : activities) {
            ActivityIcon actIcon = new ActivityIcon();
            actIcon.activity = an;
            actIcon.width = size;
            actIcon.height = size;

            Drawable drawable = null;
            try {
                if (an.activityName != null && !an.activityName.isEmpty()) {
                    try {
                        ComponentName comp = new ComponentName(
                                an.packageName, an.activityName);
                        ActivityInfo ai = pm.getActivityInfo(comp, 0);
                        drawable = ai.loadIcon(pm);
                    } catch (PackageManager.NameNotFoundException e) {
                        // fall through
                    }
                }
                if (drawable == null) {
                    drawable = pm.getApplicationIcon(an.packageName);
                }
            } catch (Exception e) {
                // Unresolvable package — fall through to the default icon.
            }
            if (drawable == null) {
                drawable = pm.getDefaultActivityIcon();
            }
            // The `icon` field carries raw BGRA8888 pixels; PNG bytes belong
            // in `iconPngData` (the only field modern hosts read). An entry
            // without iconPngData is skipped by the host and a fully-skipped
            // reply crashes unpatched Chrome, so always provide an icon.
            byte[] png = drawableToPng(drawable, size);
            if (png != null) {
                RawIconPngData raw = new RawIconPngData();
                raw.isAdaptiveIcon = false;
                raw.iconPngData = png;
                actIcon.iconPngData = raw;
            }
            actIcon.icon = new byte[0];
            icons.add(actIcon);
        }
        response.call(icons.toArray(new ActivityIcon[0]));
    }

    @Override
    public void requestTextSelectionActions(String text, int scaleFactor,
            RequestTextSelectionActionsResponse response) {
        // Chrome handles text selection — return empty
        response.call(new TextSelectionAction[0]);
    }

    @Override
    public void handleCameraResult(int intentId, int action,
            byte[] data, HandleCameraResultResponse response) {
        // Camera result forwarding — not needed without camera intent flow
        response.call(false);
    }

    @Override
    public void requestDomainVerificationStatusUpdate() {
        // No-op — domain verification is a Play Store feature
    }

    @Override
    public void addPreferredPackage(String packageName) {
        // No-op
    }

    @Override
    public void setVerifiedLinks(String[] packageNames, boolean alwaysOpen) {
        // No-op — verified links require Play Store
    }

    @Override
    public void setCaptionStyle(CaptionStyle style) {
        // No-op — caption styling handled by Android's CaptioningManager
    }

    @Override
    public void enableAccessibilityFeatures(
            AccessibilityFeatures features) {
        // No-op — accessibility features managed by AccessibilityHelper
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        mHost = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        mHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering IntentHelperInstance");
        host.onIntentHelperInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version);
    }

    // --- Package change tracking ---

    private void registerPackageReceiver() {
        mPackageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Uri data = intent.getData();
                if (data == null) return;
                String packageName = data.getSchemeSpecificPart();
                if (packageName == null) return;

                IntentHelperHost host = mHost;
                if (host == null) return;

                try {
                    org.chromium.arc.mojom.IntentFilter[] filters =
                            getIntentFiltersForPackage(packageName);
                    host.onIntentFiltersUpdatedForPackage(
                            packageName, filters);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to update filters for "
                            + packageName, e);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        mContext.registerReceiver(mPackageReceiver, filter);
    }

    private void pushAllIntentFilters() {
        IntentHelperHost host = mHost;
        if (host == null) return;

        try {
            PackageManager pm = mContext.getPackageManager();
            ArrayList<org.chromium.arc.mojom.IntentFilter> allFilters =
                    new ArrayList<>();

            // Iterate all packages, read manifest filters directly
            // (matches proprietary ArcIntentHelper approach)
            int totalPkgs = 0;
            int totalManifestFilters = 0;
            for (PackageInfo pi : pm.getInstalledPackages(0)) {
                totalPkgs++;
                List<IntentFilter> manifestFilters = pm.getAllIntentFilters(pi.packageName);
                totalManifestFilters += manifestFilters.size();
                List<org.chromium.arc.mojom.IntentFilter> pkgFilters =
                        getFiltersForPackage(pm, pi.packageName);
                if (!pkgFilters.isEmpty()) {
                    Log.i(TAG, "  " + pi.packageName + ": " + pkgFilters.size() + " filters");
                }
                allFilters.addAll(pkgFilters);
            }
            Log.i(TAG, "Scanned " + totalPkgs + " packages, " + totalManifestFilters + " manifest filters");

            host.onIntentFiltersUpdated(allFilters.toArray(
                    new org.chromium.arc.mojom.IntentFilter[0]));
            Log.i(TAG, "Pushed " + allFilters.size()
                    + " initial intent filters");
        } catch (Exception e) {
            Log.e(TAG, "Failed to push intent filters", e);
            host.onIntentFiltersUpdated(
                    new org.chromium.arc.mojom.IntentFilter[0]);
        }
    }

    /**
     * Get all relevant intent filters for a package by reading its manifest
     * directly via getAllIntentFilters(). Filters are classified as:
     * - URL filters: ACTION_VIEW + BROWSABLE + http/https, no MIME types
     * - File filters: ACTION_VIEW + MIME types + (no scheme or content://)
     */
    private List<org.chromium.arc.mojom.IntentFilter> getFiltersForPackage(
            PackageManager pm, String packageName) {
        ArrayList<org.chromium.arc.mojom.IntentFilter> result =
                new ArrayList<>();
        // The intent helper's catch-all http/https filter stands for Chrome
        // itself; it must not reach Chrome as an Android app claiming every link.
        if (INTENT_HELPER_PACKAGE.equals(packageName)) {
            return result;
        }

        for (IntentFilter f : pm.getAllIntentFilters(packageName)) {
            if (isUrlFilter(f) || isFileFilter(f)) {
                // Resolve which activity handles this filter
                String activityName = null;
                String activityLabel = null;
                if (isFileFilter(f)) {
                    // Build a synthetic intent to find the activity
                    Map.Entry<String, String> activity =
                            findActivityForFilter(pm, packageName, f);
                    if (activity != null) {
                        activityName = activity.getKey();
                        activityLabel = activity.getValue();
                    }
                }
                result.add(convertManifestFilter(
                        packageName, f, activityName, activityLabel));
            }
        }
        return result;
    }

    private static boolean isUrlFilter(IntentFilter f) {
        return f.hasAction(Intent.ACTION_VIEW)
                && f.hasCategory(Intent.CATEGORY_BROWSABLE)
                && (f.hasDataScheme("http") || f.hasDataScheme("https"))
                && f.countDataSchemeSpecificParts() == 0
                && f.countDataTypes() == 0;
    }

    private static boolean isFileFilter(IntentFilter f) {
        // ACTION_VIEW + MIME types + (no scheme or content://)
        return f.hasAction(Intent.ACTION_VIEW)
                && f.countDataTypes() > 0
                && f.countDataPaths() == 0
                && f.countDataAuthorities() == 0
                && (f.countDataSchemes() == 0
                    || f.hasDataScheme("content"));
    }

    /**
     * Find which activity in the package handles the given filter.
     * First tries queryIntentActivities; falls back to getPackageInfo.
     */
    private Map.Entry<String, String> findActivityForFilter(
            PackageManager pm, String packageName, IntentFilter f) {
        // Try queryIntentActivities first (matches proprietary approach)
        if (f.countActions() > 0) {
            Intent intent = new Intent(f.getAction(0));
            intent.setPackage(packageName);
            String mimeType = null;
            if (f.countDataTypes() > 0) {
                mimeType = f.getDataType(0).replaceAll("\\*", "anytype");
                if (!mimeType.contains("/")) mimeType += "/anytype";
            }
            Uri uri = null;
            if (f.countDataSchemes() > 0) {
                uri = Uri.parse(f.getDataScheme(0) + "://");
            }
            if (uri != null && mimeType != null) {
                intent.setDataAndType(uri, mimeType);
            } else if (mimeType != null) {
                intent.setType(mimeType);
            } else if (uri != null) {
                intent.setData(uri);
            }
            for (ResolveInfo ri : pm.queryIntentActivities(intent, 64)) {
                if (ri.activityInfo.packageName.equals(packageName)) {
                    return new java.util.AbstractMap.SimpleEntry<>(
                            ri.activityInfo.name,
                            ri.loadLabel(pm).toString());
                }
            }
        }

        // Fallback: scan activities from PackageInfo
        try {
            PackageInfo pi = pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
            if (pi.activities != null) {
                for (ActivityInfo ai : pi.activities) {
                    if (ai.exported) {
                        return new java.util.AbstractMap.SimpleEntry<>(
                                ai.name,
                                ai.loadLabel(pm).toString());
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            // ignore
        }
        return null;
    }

    // --- Helpers ---

    private Intent convertIntentInfo(IntentInfo info) {
        Intent intent = new Intent();
        if (info.action != null) intent.setAction(info.action);
        if (info.data != null) intent.setData(Uri.parse(info.data));
        if (info.type != null) {
            if (info.data != null) {
                intent.setDataAndType(Uri.parse(info.data), info.type);
            } else {
                intent.setType(info.type);
            }
        }
        if (info.categories != null) {
            for (String cat : info.categories) {
                intent.addCategory(cat);
            }
        }
        if (info.extras != null) {
            for (java.util.Map.Entry<String, String> entry
                    : info.extras.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        return intent;
    }

    private IntentHandlerInfo[] queryHandlers(Intent intent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resolvers = pm.queryIntentActivities(intent, 0);
        ArrayList<IntentHandlerInfo> handlers = new ArrayList<>();

        for (ResolveInfo ri : resolvers) {
            IntentHandlerInfo info = new IntentHandlerInfo();
            info.name = ri.loadLabel(pm).toString();
            info.packageName = ri.activityInfo.packageName;
            info.activityName = ri.activityInfo.name;
            info.actionType = 0; // VIEW
            info.isPreferred = ri.isDefault;
            handlers.add(info);
        }
        return handlers.toArray(new IntentHandlerInfo[0]);
    }

    private org.chromium.arc.mojom.IntentFilter[] getIntentFiltersForPackage(
            String packageName) {
        PackageManager pm = mContext.getPackageManager();
        List<org.chromium.arc.mojom.IntentFilter> filters =
                getFiltersForPackage(pm, packageName);
        return filters.toArray(
                new org.chromium.arc.mojom.IntentFilter[0]);
    }

    /**
     * Convert an Android manifest IntentFilter to a Mojo IntentFilter.
     */
    private org.chromium.arc.mojom.IntentFilter convertManifestFilter(
            String packageName, android.content.IntentFilter f,
            String activityName, String activityLabel) {
        org.chromium.arc.mojom.IntentFilter mf =
                new org.chromium.arc.mojom.IntentFilter();

        // Actions
        ArrayList<String> actions = new ArrayList<>();
        for (int i = 0; i < f.countActions(); i++) {
            actions.add(f.getAction(i));
        }
        mf.actions = actions.toArray(new String[0]);

        // Categories
        ArrayList<String> categories = new ArrayList<>();
        for (int i = 0; i < f.countCategories(); i++) {
            categories.add(f.getCategory(i));
        }
        mf.categories = categories.toArray(new String[0]);

        // Data schemes
        ArrayList<String> schemes = new ArrayList<>();
        for (int i = 0; i < f.countDataSchemes(); i++) {
            schemes.add(f.getDataScheme(i));
        }
        mf.dataSchemes = schemes.toArray(new String[0]);

        // MIME types
        if (f.countDataTypes() > 0) {
            ArrayList<String> mimeTypes = new ArrayList<>();
            Iterator<String> it = f.typesIterator();
            while (it.hasNext()) {
                mimeTypes.add(it.next());
            }
            mf.mimeTypes = mimeTypes.toArray(new String[0]);
        }

        // Data authorities
        if (f.countDataAuthorities() > 0) {
            // Not expected for file/URL filters, but populate if present
            mf.dataAuthorities = null;
        }

        // Data paths
        if (f.countDataPaths() > 0) {
            // Not expected for file/URL filters, but populate if present
            mf.dataPaths = null;
        }

        mf.packageName = packageName;
        mf.activityName = activityName;
        mf.activityLabel = activityLabel;

        return mf;
    }

    private static byte[] drawableToPng(Drawable drawable, int size) {
        if (drawable == null) return null;
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null
                    && (bitmap.getWidth() != size
                    || bitmap.getHeight() != size)) {
                bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
            }
        } else {
            bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, size, size);
            drawable.draw(canvas);
        }
        if (bitmap == null) return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }
}

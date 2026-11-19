/*
 * Copyright (C) 2024 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fydeos.arc.policymanager;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageInstaller.SessionParams;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.UserManager;
import android.util.Log;

import io.fydeos.arc.installpolicy.AppInstallPolicy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Device-owner policy engine. Receives an ArcPolicy JSON blob (from arc-services / the Chrome policy
 * bridge), then for each package: force-installs (download + PackageInstaller), blocks (disable +
 * uninstall-block), or makes available, and applies the default runtime-permission grant state via
 * DevicePolicyManager. Active policies are persisted so they survive reboots and so dropped entries
 * can be reverted. Only operates while this app is the device owner.
 */
public class PolicyService extends ServiceWithNotification {
    private static final String TAG = "PolicyManagerService";
    /** Play Store, hidden by {@code playStoreEnabled: false} and opened by PLAY_PROMPT. */
    private static final String PLAY_STORE_PACKAGE = "com.android.vending";
    private static final String PREFS = "policy_state";
    /** Packages whose Play page PLAY_PROMPT has already opened (once per package). */
    private static final String PREF_PLAY_PROMPTED = "play_prompted";
    private static final int MAX_DOWN_ATTEMPTS = 5;
    private static final int MAX_JOB_COUNT = 3;
    private static final int INVALID_INT = -1;
    private static final String PROFILE_NAME = "FydeOS Policy Manager";

    public static final String APPLY_POLICY = "io.fydeos.arc.ArcGateway.action.APPLY_POLICY";
    public static final String REMOVED_PACKAGE = "io.fydeos.arc.ArcGateway.action.REMOVED_PACKAGE";
    public static final String STOP_POLICY_MANAGER =
            "io.fydeos.arc.policymanager.action.STOP_MANAGER";
    public static final String SET_COMPONENT_STATE =
            "io.fydeos.arc.policymanager.action.SET_COMPONENT_STATE";
    /**
     * Fired after {@link #setPackagePolicySafe} finishes for one package — its install state and
     * permission grants are now stable. arc-services' ArcPolicyService listens for this so it
     * knows when to {@code PolicyHost.reportCompliance} to Chrome. Without this signal Chrome
     * would race the still-in-progress setPermissionGrantState loop and kill the just-started
     * kiosk app process via {@code onPackageModified}.
     */
    public static final String POLICY_APPLIED =
            "io.fydeos.arc.policymanager.action.POLICY_APPLIED";
    public static final String EXTRA_PACKAGE_NAME = "package";
    /** Preferred channel for the policy JSON; falls back to the intent data string. */
    public static final String EXTRA_POLICY_JSON = "io.fydeos.arc.policymanager.extra.POLICY_JSON";

    private final ExecutorService mPolicyExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService mDownloadExecutor = Executors.newFixedThreadPool(MAX_JOB_COUNT);
    private final Set<DownloadPackage> mInstallWatchSet = Collections.synchronizedSet(new HashSet<>());
    /** Packages with an in-flight download/install; guards against duplicate install sessions. */
    private final Set<String> mPendingInstalls = Collections.synchronizedSet(new HashSet<>());

    private DevicePolicyManager mDpm;
    private ComponentName mDeviceAdmin;
    private PolicyDbHelper mDbHelper;
    private ArcSessionCallback mSessionCallback;
    /** {@code playStoreEnabled} of the policy last applied; PLAY_PROMPT needs the Store. */
    private volatile boolean mPlayStoreEnabled = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mDpm = getSystemService(DevicePolicyManager.class);
        mDeviceAdmin = new ComponentName(this, ArcDeviceAdminReceiver.class);
        mDbHelper = new PolicyDbHelper(getBaseContext());
        mSessionCallback = new ArcSessionCallback(this);
        getPackageManager().getPackageInstaller().registerSessionCallback(mSessionCallback);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R
                && mDpm.isDeviceOwnerApp(getApplicationContext().getPackageName())) {
            try {
                mDpm.setProfileName(mDeviceAdmin, PROFILE_NAME);
                mDpm.setProfileEnabled(mDeviceAdmin);
            } catch (SecurityException e) {
                // setProfileEnabled is a managed-profile (profile-owner) API and throws for a
                // device owner ("can not enable the profile outside a managed profile"). Harmless
                // in device-owner mode, so ignore.
                Log.d(TAG, "skip setProfile* (device owner): " + e.getMessage());
            }
        }
        PackageInstallStatusReceiver.registerPackageInstallStatusReceiver(getApplicationContext());
    }

    private void stopService() {
        PackageInstallStatusReceiver.unregisterPackageInstallStatusReceiver(getApplicationContext());
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Runs as a persistent background service (no foreground service), so nothing to post here.
        if (!mDpm.isDeviceOwnerApp(getApplicationContext().getPackageName())) {
            Log.d(TAG, "Not device owner; nothing to enforce.");
            stopService();
            return START_NOT_STICKY;
        }

        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            Log.e(TAG, "Null action");
            stopIfIdle();
            return START_NOT_STICKY;
        }
        // Async branches dispatch to an executor that reconciles the managed notification itself; the
        // synchronous branches reconcile here.
        boolean reconcileNotification = true;
        switch (action) {
            case APPLY_POLICY: {
                String json = extractPolicyJson(intent);
                if (json == null) {
                    Log.e(TAG, "PolicyAction with empty data");
                } else {
                    Log.d(TAG, json);
                    reconcileNotification = false;
                    mPolicyExecutor.execute(() -> handlePolicies(json));
                }
                break;
            }
            case STOP_POLICY_MANAGER:
                reconcileNotification = false;
                stopService();
                break;
            case ArcSessionCallback.ACTION_INSTALL_FEEDBACK:
                Log.d(TAG, "Install feedback: "
                        + intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME) + " : "
                        + intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE));
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                Log.d(TAG, "Boot Completed.");
                break;
            case Intent.ACTION_PACKAGE_ADDED:
                if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        && intent.getData() != null) {
                    String pkg = intent.getData().getSchemeSpecificPart();
                    Log.d(TAG, "new installed " + pkg + " apply policy");
                    reconcileNotification = false;
                    mPolicyExecutor.execute(() -> setPackagePolicy(pkg));
                }
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                Log.d(TAG, intent.toString());
                break;
            case SET_COMPONENT_STATE: {
                String pkg = intent.getStringExtra("pkg");
                String cls = intent.getStringExtra("cls");
                int state = intent.getIntExtra("state",
                        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                if (pkg != null && cls != null) {
                    Log.d(TAG, "set component:" + pkg + ", cls:" + cls + ", state:" + state);
                    getPackageManager().setComponentEnabledSetting(
                            new ComponentName(pkg, cls), state, 0);
                }
                break;
            }
            default:
                Log.e(TAG, "Unknown action:" + action);
        }
        if (reconcileNotification) {
            stopIfIdle();
        }
        return START_NOT_STICKY;
    }

    private String extractPolicyJson(Intent intent) {
        String json = intent.getStringExtra(EXTRA_POLICY_JSON);
        if (json == null) {
            json = intent.getDataString();
        }
        return json;
    }

    /** Stop the service when there's no pending policy to track; otherwise keep running. The
     *  process stays resident (persistent app) regardless. Chrome OS shows the managed disclosure,
     *  so this app posts no "managed" notification of its own. */
    private void stopIfIdle() {
        if (!mDbHelper.hasPolicy()) {
            stopSelf();
        }
    }

    // --- Policy parsing / application (runs on mPolicyExecutor) ---

    private void handlePolicies(String jsonString) {
        if (!mDpm.isDeviceOwnerApp(getApplicationContext().getPackageName())) {
            Log.d(TAG, getApplicationContext().getPackageName()
                    + " is not device owner, skip any policy!");
            return;
        }
        AppInstallPolicy installPolicy = null;
        try {
            installPolicy = AppInstallPolicy.parse(jsonString);
        } catch (Exception e) {
            Log.e(TAG, "Parse policy error: " + e.getMessage());
        }
        if (installPolicy != null) {
            for (String why : installPolicy.skipped) {
                Log.e(TAG, "Skip " + why);
            }
            applyInstallSettings(installPolicy);
        }
        applyPolicies(toPackagePolicies(installPolicy));
        if (installPolicy != null) {
            forgetStalePlayPrompts(installPolicy);
        }
    }

    /**
     * The validated entries, including ALLOWLIST's implicit {@code "*" BLOCKED}: stored like any
     * other entry, it disables unlisted apps now, blocks new ones on PACKAGE_ADDED, and is reverted
     * by the usual dropped-entry path when the mode goes back to BLOCKLIST.
     */
    private List<PackagePolicy> toPackagePolicies(AppInstallPolicy installPolicy) {
        List<PackagePolicy> policies = new ArrayList<>();
        if (installPolicy == null) {
            return policies;
        }
        for (AppInstallPolicy.Entry entry : installPolicy.entries) {
            try {
                policies.add(PackagePolicy.fromEntry(entry));
            } catch (Exception e) {
                Log.e(TAG, "Failed to build policy for " + entry + ", skip: " + e.getMessage());
            }
        }
        return policies;
    }

    /** The top-level install keys. Absent means the unrestricted default, so dropping a key reverts it. */
    private void applyInstallSettings(AppInstallPolicy installPolicy) {
        try {
            if (Boolean.FALSE.equals(installPolicy.installUnknownSourcesAllowed)) {
                mDpm.addUserRestriction(mDeviceAdmin,
                        UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY);
            } else {
                mDpm.clearUserRestriction(mDeviceAdmin,
                        UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to apply installUnknownSourcesAllowed: " + e);
        }
        // The Play Store is a system app, which per-package policy never touches (see
        // setPackagePolicySafe); this key is the one deliberate exception.
        mPlayStoreEnabled = !Boolean.FALSE.equals(installPolicy.playStoreEnabled);
        if (isPackageInstalled(PLAY_STORE_PACKAGE)) {
            try {
                mDpm.setApplicationHidden(mDeviceAdmin, PLAY_STORE_PACKAGE, !mPlayStoreEnabled);
            } catch (Exception e) {
                Log.e(TAG, "Failed to apply playStoreEnabled: " + e);
            }
        }
        Log.d(TAG, "playStoreMode=" + installPolicy.playStoreMode
                + " installUnknownSourcesAllowed=" + installPolicy.installUnknownSourcesAllowed
                + " playStoreEnabled=" + installPolicy.playStoreEnabled);
    }

    /**
     * PLAY_PROMPT: open the package's Play Store page so the user can tap Install; the rest of
     * the policy is applied on PACKAGE_ADDED like any other install. Once per package, so a policy
     * refresh does not reopen it; the record is dropped when the entry leaves the policy.
     * The device owner may start activities from the background, so no extra permission.
     */
    private void maybePromptPlayInstall(PackagePolicy policy) {
        if (policy.isRegex) {
            Log.w(TAG, "PLAY_PROMPT needs a literal package name, skip " + policy.packageName);
            return;
        }
        if (!mPlayStoreEnabled || !isPackageInstalled(PLAY_STORE_PACKAGE)) {
            Log.w(TAG, "PLAY_PROMPT " + policy.packageName + ": Play Store unavailable, skip");
            return;
        }
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        Set<String> prompted = new HashSet<>(prefs.getStringSet(PREF_PLAY_PROMPTED,
                Collections.emptySet()));
        if (!prompted.add(policy.packageName)) {
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + policy.packageName))
                    .setPackage(PLAY_STORE_PACKAGE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            prefs.edit().putStringSet(PREF_PLAY_PROMPTED, prompted).apply();
            Log.i(TAG, "PLAY_PROMPT opened Play for " + policy.packageName);
        } catch (Exception e) {
            Log.e(TAG, "PLAY_PROMPT failed for " + policy.packageName + ": " + e);
        }
    }

    /** Forget prompts for packages that are no longer PLAY_PROMPT, so re-adding one prompts again. */
    private void forgetStalePlayPrompts(AppInstallPolicy installPolicy) {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        Set<String> prompted = prefs.getStringSet(PREF_PLAY_PROMPTED, Collections.emptySet());
        if (prompted.isEmpty()) {
            return;
        }
        Set<String> keep = new HashSet<>();
        for (AppInstallPolicy.Entry e : installPolicy.entries) {
            if (AppInstallPolicy.PLAY_PROMPT.equals(e.installType) && prompted.contains(e.packageName)) {
                keep.add(e.packageName);
            }
        }
        if (keep.size() != prompted.size()) {
            prefs.edit().putStringSet(PREF_PLAY_PROMPTED, keep).apply();
        }
    }

    private void applyPolicies(List<PackagePolicy> policies) {
        policies.addAll(createRestorePolicies(policies));
        List<PackagePolicy> sorted = new ArrayList<>(policies);
        // Locally synthesized reverts last, then the shared precedence (literal before pattern,
        // longer pattern first) — the same order getPolicy() and the install-time gate use.
        sorted.sort(Comparator
                .comparing((PackagePolicy p) -> p.origin == Origin.LOCAL)
                .thenComparing(PackagePolicy::comparePrecedence));

        Set<String> appliedPackages = new HashSet<>();
        for (PackagePolicy policy : sorted) {
            Log.d(TAG, "Apply: " + policy);
            storePolicy(policy);
            if (policy.isRegex) {
                for (ApplicationInfo app : getPackageManager().getInstalledApplications(0)) {
                    if (!appliedPackages.contains(app.packageName)
                            && setPackagePolicySafe(policy, app)) {
                        appliedPackages.add(app.packageName);
                    }
                }
            } else if (!isPackageInstalled(policy.packageName)) {
                if (policy.installType.isPlayPrompt()) {
                    maybePromptPlayInstall(policy);
                } else if (policy.installType == InstallType.FORCE_INSTALLED
                        && policy.installUrl != null) {
                    if (mPendingInstalls.add(policy.packageName)) {
                        addToDownloadQueue(new DownloadPackage(policy.packageName, policy.installUrl));
                    } else {
                        Log.d(TAG, "Install already in flight for " + policy.packageName + ", skip");
                    }
                }
            } else {
                try {
                    ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                            policy.packageName, PackageManager.GET_META_DATA);
                    if (!appliedPackages.contains(policy.packageName)
                            && setPackagePolicySafe(policy, appInfo)) {
                        appliedPackages.add(policy.packageName);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "Package vanished: " + policy.packageName);
                }
            }
        }
        stopIfIdle();
    }

    /** Builds AVAILABLE/UNSPECIFIED revert policies for packages no longer in the current set. */
    private List<PackagePolicy> createRestorePolicies(List<PackagePolicy> policies) {
        List<PackagePolicy> revert = new ArrayList<>();
        String[] packageNames = new String[policies.size()];
        for (int i = 0; i < policies.size(); i++) {
            packageNames[i] = policies.get(i).packageName;
        }
        for (PackagePolicy stored : mDbHelper.getExclusivePolicies(packageNames)) {
            revert.add(new PackagePolicy(stored.packageName, InstallType.AVAILABLE,
                    PermissionPolicy.PERMISSION_POLICY_UNSPECIFIED, null, stored.origin));
        }
        return revert;
    }

    private void storePolicy(PackagePolicy packagePolicy) {
        if (packagePolicy.origin == Origin.LOCAL) { // Don't persist locally synthesized reverts.
            return;
        }
        if (mDbHelper.storePolicy(packagePolicy)) {
            Log.d(TAG, "Save " + packagePolicy + " to record");
        } else {
            Log.e(TAG, "Failed to insert " + packagePolicy + " to database");
        }
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSystemApp(ApplicationInfo appInfo) {
        int flags = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return appInfo.sourceDir.startsWith("/system") || (appInfo.flags & flags) != 0;
    }

    private void setPackageEnableState(String packageName, int newState) {
        getPackageManager().setApplicationEnabledSetting(packageName, newState, 0);
    }

    private int grantStateFor(PermissionPolicy policy) {
        switch (policy) {
            case PROMPT:
                return DevicePolicyManager.PERMISSION_GRANT_STATE_DEFAULT;
            case GRANT:
                return DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;
            case DENY:
                return DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED;
            default:
                return INVALID_INT;
        }
    }

    private void setPermissionGrantState(String packageName, String permission, int grantState) {
        if (grantState == INVALID_INT) {
            Log.d(TAG, "Invalid grantState for package:" + packageName + " permission:" + permission);
            return;
        }
        try {
            if (mDpm.getPermissionGrantState(mDeviceAdmin, packageName, permission) != grantState) {
                mDpm.setPermissionGrantState(mDeviceAdmin, packageName, permission, grantState);
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private boolean setPackagePolicySafe(PackagePolicy policy, ApplicationInfo appInfo) {
        String pkgName = appInfo.packageName;
        if (!policy.match(pkgName) || isSystemApp(appInfo) || pkgName.equals(getPackageName())) {
            return false;
        }
        try {
            switch (policy.installType) {
                case BLOCKED:
                    setPackageEnableState(pkgName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                    mDpm.setUninstallBlocked(mDeviceAdmin, pkgName, false);
                    mSessionCallback.showBlockNotification(
                            getPackageManager().getApplicationLabel(appInfo).toString());
                    break;
                case AVAILABLE:
                case PLAY_PROMPT:
                    setPackageEnableState(pkgName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                    mDpm.setUninstallBlocked(mDeviceAdmin, pkgName, false);
                    break;
                case FORCE_INSTALLED:
                case PLAY_PROMPT_LOCKED:
                    setPackageEnableState(pkgName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                    mDpm.setUninstallBlocked(mDeviceAdmin, pkgName, true);
                    break;
            }
            int grantState = grantStateFor(policy.permissionPolicy);
            PackageInfo pInfo = getPackageManager().getPackageInfo(pkgName,
                    PackageManager.GET_PERMISSIONS);
            if (pInfo.requestedPermissions != null) {
                for (String permission : pInfo.requestedPermissions) {
                    if (!permission.startsWith("android.permission.")) {
                        continue;
                    }
                    // DPM.setPermissionGrantState blocks ~20s on an unknown perm
                    // (e.g. API 33+ POST_NOTIFICATIONS / READ_MEDIA_IMAGES on AOSP 11).
                    try {
                        getPackageManager().getPermissionInfo(permission, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        continue;
                    }
                    Log.d(TAG, "set package: " + pkgName + " " + permission + " "
                            + policy.permissionPolicy.name());
                    setPermissionGrantState(pkgName, permission, grantState);
                }
            }
            announcePolicyApplied(pkgName);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

    /**
     * Tell arc-services this package's policy is now applied (enable-state set, install-block set,
     * permissions granted). ArcPolicyService uses this to defer {@code PolicyHost.reportCompliance}
     * until the package is stable; otherwise Chrome's KioskArcvmAppService races the permission
     * grants and AM kills the just-started kiosk app process via {@code onPackageModified}.
     */
    private void announcePolicyApplied(String pkgName) {
        Intent applied = new Intent(POLICY_APPLIED);
        applied.putExtra(EXTRA_PACKAGE_NAME, pkgName);
        sendBroadcast(applied);
    }

    /**
     * Applies the stored policy to a freshly installed package. Rows are kept: deleting an exact
     * AVAILABLE row after the first install let an allowed app that was uninstalled and installed
     * again fall through to a {@code "*" BLOCKED} entry until the next policy refresh. Dropped
     * entries are cleaned up by the revert path in applyPolicies instead.
     */
    private void setPackagePolicy(String packageName) {
        try {
            PackagePolicy policy = mDbHelper.getPolicy(packageName);
            if (policy == null) {
                Log.d(TAG, "There is no policy for " + packageName);
                return;
            }
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA);
            setPackagePolicySafe(policy, appInfo);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set permissions of " + packageName + ": " + e.getMessage());
        } finally {
            stopIfIdle();
        }
    }

    // --- Download + install (runs on mDownloadExecutor, bounded to MAX_JOB_COUNT) ---

    private void addToDownloadQueue(DownloadPackage downloadPackage) {
        if (downloadPackage.retries < MAX_DOWN_ATTEMPTS) {
            mDownloadExecutor.execute(() -> {
                downloadPackage.retries++;
                downloadAndInstallPackage(downloadPackage);
            });
        } else {
            mPendingInstalls.remove(downloadPackage.packageName);
        }
    }

    private void downloadAndInstallPackage(DownloadPackage downloadPackage) {
        int sessionId = -1;
        PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();
        try {
            SessionParams params = new SessionParams(SessionParams.MODE_FULL_INSTALL);
            params.setInstallReason(PackageManager.INSTALL_REASON_POLICY);
            params.setAppPackageName(downloadPackage.packageName);
            sessionId = packageInstaller.createSession(params);
            if (sessionId < 1) {
                throw new Exception("Failed to get session id");
            }
            Log.d(TAG, "Create new session id:" + sessionId);
            mSessionCallback.addToWatchList(sessionId);
            downloadPackage.sessionId = sessionId;

            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
            HttpURLConnection conn = (HttpURLConnection) downloadPackage.url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Failed to connect " + downloadPackage.url);
            }
            try (InputStream in = conn.getInputStream();
                 OutputStream out = session.openWrite(downloadPackage.packageName, 0, -1)) {
                byte[] buffer = new byte[65536];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                session.fsync(out);
            }
            Intent intent = new Intent(getBaseContext(), PolicyService.class)
                    .setAction(ArcSessionCallback.ACTION_INSTALL_FEEDBACK);
            // MUTABLE: the system fills EXTRA_STATUS* into the commit callback.
            PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), sessionId,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            session.commit(pendingIntent.getIntentSender());
            Log.i(TAG, "session:" + sessionId + " is committed.");
            mInstallWatchSet.add(downloadPackage);
            session.close();
        } catch (Exception e) {
            Log.e(TAG, "Error downloading " + downloadPackage.packageName + " from "
                    + downloadPackage.url + " retry:" + downloadPackage.retries + " err: " + e);
            if (sessionId != -1) {
                packageInstaller.abandonSession(sessionId);
                mSessionCallback.removeFromWatchList(sessionId, false);
            }
            addToDownloadQueue(downloadPackage);
        }
    }

    @Override
    void unWatch(int sessionId, String errMsg) {
        DownloadPackage match = null;
        synchronized (mInstallWatchSet) {
            for (DownloadPackage dp : mInstallWatchSet) {
                if (dp.sessionId == sessionId) {
                    match = dp;
                    break;
                }
            }
            if (match != null) {
                mInstallWatchSet.remove(match);
            }
        }
        if (match == null) {
            return;
        }
        if (errMsg == null) {
            mPendingInstalls.remove(match.packageName);
            super.unWatch(sessionId, match.packageName + " is installed");
        } else {
            super.unWatch(sessionId, errMsg);
            addToDownloadQueue(match);
        }
    }

    @Override
    public void onDestroy() {
        getPackageManager().getPackageInstaller().unregisterSessionCallback(mSessionCallback);
        mPolicyExecutor.shutdown();
        mDownloadExecutor.shutdown();
        mDbHelper.close();
        super.onDestroy();
    }
}

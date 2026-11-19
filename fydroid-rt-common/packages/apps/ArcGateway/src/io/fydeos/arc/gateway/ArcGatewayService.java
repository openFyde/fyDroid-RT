package io.fydeos.arc.gateway;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * System app service that handles APK installation requests from Chrome OS.
 *
 * Listens for INSTALL_PACKAGE / INSTALL_PACKAGE_BY_POLICY broadcasts,
 * verifies the APK with a remote server, and installs it via PackageInstaller.
 */
public class ArcGatewayService extends Service {
    private static final String TAG = "ArcGateway";

    static final String ACTION_INSTALL_PACKAGE =
            "io.fydeos.arc.ArcGateway.action.INSTALL_PACKAGE";
    static final String ACTION_INSTALL_PACKAGE_BY_POLICY =
            "io.fydeos.arc.ArcGateway.action.INSTALL_PACKAGE_BY_POLICY";
    static final String ACTION_INSTALL_FEEDBACK =
            "io.fydeos.arc.ArcGateway.action.INSTALL_FEEDBACK";
    static final String EXTRA_VERIFY_URL =
            "io.fydeos.arc.ArcGateway.extra.VERIFY_URL";

    static final int INSTALL_MODE_USER = 0;
    static final int INSTALL_MODE_POLICY = 1;

    private static final String NOTIFICATION_CHANNEL_ID = "ArcGatewayNotification";
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    private static final Pattern PATTERN_VERIFY_URL =
            Pattern.compile("^https://[^/]*\\.fydeos\\.(io|com)/.*$");

    private HandlerThread mWorkerThread;
    private Handler mWorkerHandler;
    private NotificationManager mNotificationManager;
    private PackageInstaller mPackageInstaller;
    private final Map<Integer, String> mOnGoingSessions = new HashMap<>();
    private final Map<Integer, String> mSessionLabels = new HashMap<>();
    private final Map<Integer, Integer> mSessionModes = new HashMap<>();
    private boolean mInitialized;

    @Override
    public void onCreate() {
        super.onCreate();
        mWorkerThread = new HandlerThread("ArcGatewayWorker");
        mWorkerThread.start();
        mWorkerHandler = new Handler(mWorkerThread.getLooper());
        mNotificationManager = getSystemService(NotificationManager.class);
        mPackageInstaller = getPackageManager().getPackageInstaller();

        createNotificationChannel();
        registerSessionCallback();
        mInitialized = true;

        Log.i(TAG, "ArcGatewayService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (ACTION_INSTALL_PACKAGE.equals(action)
                    || ACTION_INSTALL_PACKAGE_BY_POLICY.equals(action)) {
                handleInstallIntent(intent);
            } else if (ACTION_INSTALL_FEEDBACK.equals(action)) {
                handleInstallFeedback(intent);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWorkerThread != null) {
            mWorkerThread.quitSafely();
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(getString(R.string.notification_channel_description));
        mNotificationManager.createNotificationChannel(channel);
    }

    private void registerSessionCallback() {
        mPackageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
            @Override
            public void onCreated(int sessionId) {}

            @Override
            public void onBadgingChanged(int sessionId) {}

            @Override
            public void onActiveChanged(int sessionId, boolean active) {}

            @Override
            public void onProgressChanged(int sessionId, float progress) {
                if (!mOnGoingSessions.containsKey(sessionId)) return;
                String label = mSessionLabels.get(sessionId);
                if (label == null) label = "package";
                String modeStr = getModeString(sessionId);
                Notification notification = new Notification.Builder(
                        ArcGatewayService.this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setContentTitle(getString(R.string.installing,
                                modeStr, label))
                        .setProgress(100, Math.round(progress * 100), false)
                        .setOngoing(true)
                        .build();
                mNotificationManager.notify(sessionId, notification);
            }

            @Override
            public void onFinished(int sessionId, boolean success) {
                mOnGoingSessions.remove(sessionId);
                // Cancel progress notification — handleInstallFeedback
                // will post the final notification with package name.
                mNotificationManager.cancel(sessionId);
                Log.i(TAG, "Install session " + sessionId
                        + (success ? " succeeded" : " failed"));
            }
        }, mWorkerHandler);
    }


    // --- URI resolution ---

    private static class ApkInfo {
        final String filePath;
        final String displayName;
        final long size;

        ApkInfo(String filePath, String displayName, long size) {
            this.filePath = filePath;
            this.displayName = displayName;
            this.size = size;
        }
    }

    /**
     * Resolve APK metadata. For file:// URIs, stat the file directly.
     * For content:// URIs, query the content provider for display name
     * and size (no FLAG_ONEWAY issue since we run in our own process).
     */
    private ApkInfo resolveApkInfo(Uri uri) {
        if ("file".equals(uri.getScheme())) {
            String path = uri.getPath();
            if (path != null) {
                File f = new File(path);
                return new ApkInfo(path, f.getName(), f.length());
            }
        }

        // content:// URI — query content provider
        String displayName = null;
        long fileSize = -1;
        try (Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (nameIdx >= 0) displayName = cursor.getString(nameIdx);
                if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) {
                    fileSize = cursor.getLong(sizeIdx);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to query URI: " + uri, e);
        }

        // Fallback: extract name from URI path
        if (displayName == null) {
            displayName = uri.getLastPathSegment();
            if (displayName != null) {
                int slash = displayName.lastIndexOf('/');
                if (slash >= 0) displayName = displayName.substring(slash + 1);
                int colon = displayName.lastIndexOf(':');
                if (colon >= 0) displayName = displayName.substring(colon + 1);
                displayName = Uri.decode(displayName);
            }
        }

        return new ApkInfo(null, displayName, fileSize);
    }

    // --- Installation logic ---

    private void handleInstallIntent(Intent intent) {
        final Uri fileUri = intent.getData();
        final String verifyUrl = intent.getStringExtra(EXTRA_VERIFY_URL);
        final int mode = ACTION_INSTALL_PACKAGE_BY_POLICY.equals(
                intent.getAction()) ? INSTALL_MODE_POLICY : INSTALL_MODE_USER;
        if (fileUri == null) {
            Log.e(TAG, "Missing file URI");
            return;
        }
        Log.i(TAG, "Install request (" +
                (mode == INSTALL_MODE_POLICY ? "policy" : "user")
                + "): " + fileUri);
        mWorkerHandler.post(() ->
                handleActionInstallPackage(fileUri, verifyUrl, mode));
    }

    private void handleActionInstallPackage(Uri fileUri, String verifyUrl,
            int mode) {
        Log.d(TAG, "install-package: " + fileUri + " verify: " + verifyUrl
                + " mode: " + (mode == INSTALL_MODE_POLICY ? "policy" : "user"));

        int sessionId = -1;
        ApkInfo apkInfo = null;
        String modeStr = getString(mode == INSTALL_MODE_POLICY
                ? R.string.mode_policy : R.string.mode_user);

        try {
            apkInfo = resolveApkInfo(fileUri);
            Log.d(TAG, "resolved: name=" + apkInfo.displayName
                    + " path=" + apkInfo.filePath
                    + " size=" + apkInfo.size);

            // Open input stream
            InputStream apkStream = null;
            if (apkInfo.filePath != null) {
                File f = new File(apkInfo.filePath);
                if (f.canRead()) {
                    apkStream = new FileInputStream(f);
                }
            }
            if (apkStream == null) {
                apkStream = getContentResolver().openInputStream(fileUri);
            }
            if (apkStream == null) {
                throw new Exception("Cannot open APK stream: " + fileUri);
            }

            Md5Result result = calculateMd5AndPrepareSession(
                    apkStream, apkInfo.size, mode);
            sessionId = result.sessionId;

            if (!result.success || sessionId == -1) {
                throw new Exception("Failed to prepare install session");
            }

            String label = apkInfo.displayName;
            Log.d(TAG, "prepared pkg: " + label
                    + " md5: " + result.md5sum + " session: " + sessionId);

            mOnGoingSessions.put(sessionId, apkInfo.filePath);
            mSessionLabels.put(sessionId, label);
            mSessionModes.put(sessionId, mode);

            // Show installing notification
            Notification notification = new Notification.Builder(this,
                    NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle(getString(R.string.installing,
                            modeStr, label))
                    .setProgress(100, 0, true)
                    .setOngoing(true)
                    .build();
            mNotificationManager.notify(sessionId, notification);

            // Remote verification
            if (verifyUrl != null && !verifyUrl.isEmpty()) {
                if (!PATTERN_VERIFY_URL.matcher(verifyUrl).matches()) {
                    throw new Exception("Invalid verification URL");
                }
                if (!remoteCheck(verifyUrl, result.md5sum)) {
                    throw new Exception("Remote verification failed");
                }
            }

            beginInstall(sessionId);

        } catch (Exception e) {
            Log.e(TAG, "Install failed: " + e.getMessage(), e);
            removeSessionOnError(sessionId);
            String failLabel = apkInfo != null && apkInfo.displayName != null
                    ? apkInfo.displayName : fileUri.getLastPathSegment();
            Notification notification = new Notification.Builder(this,
                    NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle(getString(R.string.install_failed,
                            modeStr, failLabel))
                    .build();
            mNotificationManager.notify(TAG.hashCode(), notification);
        }
    }

    private static class Md5Result {
        final boolean success;
        final String md5sum;
        final int sessionId;

        Md5Result(boolean success, String md5sum, int sessionId) {
            this.success = success;
            this.md5sum = md5sum;
            this.sessionId = sessionId;
        }
    }

    private Md5Result calculateMd5AndPrepareSession(InputStream apkStream,
            long packageSize, int mode) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            if (packageSize > 0) {
                params.setSize(packageSize);
            }
            params.setInstallReason(mode == INSTALL_MODE_POLICY
                    ? PackageManager.INSTALL_REASON_POLICY
                    : PackageManager.INSTALL_REASON_USER);

            int sessionId = mPackageInstaller.createSession(params);
            if (sessionId == -1) {
                Log.e(TAG, "Failed to create session");
                return new Md5Result(false, "", -1);
            }

            PackageInstaller.Session session = mPackageInstaller.openSession(sessionId);
            OutputStream outStream = session.openWrite("base.apk", 0, packageSize);
            DigestOutputStream digestOutputStream =
                    new DigestOutputStream(outStream, digest);

            byte[] buffer = new byte[65536];
            int bytesRead;
            while ((bytesRead = apkStream.read(buffer)) != -1) {
                digestOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] md5Bytes = digest.digest();
            String md5Hex = new BigInteger(1, md5Bytes).toString(16);
            while (md5Hex.length() < 32) {
                md5Hex = "0" + md5Hex;
            }

            session.fsync(outStream);
            digestOutputStream.close();
            apkStream.close();
            session.close();

            return new Md5Result(true, md5Hex, sessionId);

        } catch (Exception e) {
            Log.e(TAG, "MD5/session failed", e);
            return new Md5Result(false, "", -1);
        }
    }

    private boolean remoteCheck(String verifyUrl, String md5) {
        try {
            String reqBody = URLEncoder.encode("md5", "UTF-8") + "="
                    + URLEncoder.encode(md5, "UTF-8");
            URL url = new URL(verifyUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(reqBody);
            writer.flush();
            writer.close();
            Log.d(TAG, "POST " + reqBody + " to " + verifyUrl);
            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            Log.e(TAG, "Remote check failed: " + e.getMessage());
            return false;
        }
    }

    private void beginInstall(int sessionId) {
        Intent intent = new Intent(this, ArcGatewayService.class);
        intent.setAction(ACTION_INSTALL_FEEDBACK);
        // FLAG_MUTABLE is required on S+ (PendingIntent rejects a creation with
        // neither FLAG_IMMUTABLE nor FLAG_MUTABLE). It must be MUTABLE — not
        // immutable — because PackageInstaller fills the install-result extras
        // (EXTRA_STATUS, EXTRA_PACKAGE_NAME, ...) into this intent before
        // dispatching it back to ACTION_INSTALL_FEEDBACK.
        PendingIntent pendingIntent = PendingIntent.getService(
                this, sessionId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        try {
            PackageInstaller.Session session =
                    mPackageInstaller.openSession(sessionId);
            session.commit(pendingIntent.getIntentSender());
            Log.d(TAG, "Session " + sessionId + " committed");
            session.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to commit session " + sessionId, e);
            removeSessionOnError(sessionId);
        }
    }

    private void handleInstallFeedback(Intent intent) {
        int sessionId = intent.getIntExtra(
                PackageInstaller.EXTRA_SESSION_ID, -1);
        int status = intent.getIntExtra(
                PackageInstaller.EXTRA_STATUS, -1);
        String packageName = intent.getStringExtra(
                PackageInstaller.EXTRA_PACKAGE_NAME);

        // Prefer real package name, fall back to filename label
        String label = mSessionLabels.remove(sessionId);
        if (packageName != null && !packageName.isEmpty()) {
            label = packageName;
        }
        if (label == null) label = "package";

        String modeStr = getModeString(sessionId);
        mSessionModes.remove(sessionId);

        // Cancel progress notification and show final result
        mNotificationManager.cancel(sessionId);

        if (status == PackageInstaller.STATUS_SUCCESS) {
            Notification notification = new Notification.Builder(
                    this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle(getString(R.string.installed,
                            modeStr, label))
                    .build();
            mNotificationManager.notify(sessionId, notification);
            Log.i(TAG, "Installed: " + label);
        } else {
            String msg = intent.getStringExtra(
                    PackageInstaller.EXTRA_STATUS_MESSAGE);
            Log.w(TAG, "Install failed: " + label + " status=" + status
                    + " msg=" + msg);
            Notification notification = new Notification.Builder(
                    this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle(getString(R.string.install_failed,
                            modeStr, label))
                    .build();
            mNotificationManager.notify(sessionId, notification);
        }
    }

    // --- Helpers ---

    private String getModeString(int sessionId) {
        Integer mode = mSessionModes.get(sessionId);
        return getString((mode != null && mode == INSTALL_MODE_POLICY)
                ? R.string.mode_policy : R.string.mode_user);
    }

    private void removeSessionOnError(int sessionId) {
        if (sessionId > -1) {
            mOnGoingSessions.remove(sessionId);
            mSessionLabels.remove(sessionId);
            mSessionModes.remove(sessionId);
            mNotificationManager.cancel(sessionId);
            try {
                mPackageInstaller.abandonSession(sessionId);
            } catch (Exception e) {
                Log.w(TAG, "Failed to abandon session " + sessionId, e);
            }
        }
    }
}

/* Open source replacement for org.chromium.webapk.lib.client.WebApkValidator from the proprietary com.google.android.chromeos jar (originally from Chromium). */
package org.chromium.webapk.lib.client;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Validates that an installed package is a real WebAPK. Matches the
 * public API of the proprietary class: consumers call {@link #init(byte[])}
 * once with the expected public key, then {@link #isValidWebApk(Context, String)}
 * to check individual packages.
 */
public class WebApkValidator {
    private static final boolean DEBUG = false;
    private static final String KEY_FACTORY = "EC";
    private static final String TAG = "WebApkValidator";

    private static PublicKey sCommentSignedPublicKey;
    private static byte[] sCommentSignedPublicKeyBytes;
    private static boolean sOverrideValidation = true;

    /**
     * Returns whether the package installed under {@code webappPackageName}
     * looks like a valid WebAPK. Requires a prior call to
     * {@link #init(byte[])} — without one this method returns {@code false}
     * and logs a {@code wtf}.
     */
    public static boolean isValidWebApk(Context context, String webappPackageName) {
        if (sCommentSignedPublicKeyBytes == null) {
            Log.wtf(TAG, "WebApk validation failure - expected signature not set."
                    + "missing call to WebApkValidator.init");
            return false;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    webappPackageName, 192);
            if (isNotWebApkQuick(packageInfo)) {
                return false;
            }
            if (sOverrideValidation) {
                return true;
            }
            return verifyCommentSignedWebApk(packageInfo);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cheap up-front rejection: if the package has no application info, no
     * meta-data, or no {@code MANIFEST_START_URL} value it cannot be a WebAPK.
     */
    private static boolean isNotWebApkQuick(PackageInfo packageInfo) {
        if (packageInfo.applicationInfo == null || packageInfo.applicationInfo.metaData == null) {
            return true;
        }
        String startUrl = packageInfo.applicationInfo.metaData.getString(
                WebApkConstants.MANIFEST_START_URL);
        return TextUtils.isEmpty(startUrl);
    }

    /**
     * Verifies the ECDSA signature embedded in the APK ZIP comment against
     * the configured public key. Returns {@code true} on successful
     * verification, {@code false} on any failure.
     */
    private static boolean verifyCommentSignedWebApk(PackageInfo packageInfo) {
        PublicKey commentSignedPublicKey;
        try {
            commentSignedPublicKey = getCommentSignedPublicKey();
        } catch (Exception e) {
            Log.e(TAG, "WebApk failed to get Public Key", e);
            return false;
        }
        if (commentSignedPublicKey == null) {
            Log.e(TAG, "WebApk validation failure - unable to decode public key");
            return false;
        }
        if (packageInfo.applicationInfo == null || packageInfo.applicationInfo.sourceDir == null) {
            Log.e(TAG, "WebApk validation failure - missing applicationInfo sourcedir");
            return false;
        }
        String packageFilename = packageInfo.applicationInfo.sourceDir;
        RandomAccessFile file = null;
        FileChannel inChannel = null;
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        try {
            file = new RandomAccessFile(packageFilename, "r");
            inChannel = file.getChannel();
            MappedByteBuffer buf =
                    inChannel.map(FileChannel.MapMode.READ_ONLY, 0L, inChannel.size());
            buf.load();
            WebApkVerifySignature v = new WebApkVerifySignature(buf);
            int result = v.read();
            if (result != 0) {
                Log.e(TAG, String.format("Failure reading %s: %s", packageFilename,
                        Integer.valueOf(result)));
                return false;
            }
            return v.verifySignature(commentSignedPublicKey) == 0;
        } catch (Exception e) {
            Log.e(TAG, "WebApk file error for file " + packageFilename, e);
            return false;
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    // Ignored.
                }
            }
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    // Ignored.
                }
            }
        }
    }

    /**
     * Installs the DER-encoded X.509 SubjectPublicKeyInfo bytes used to
     * verify WebAPK comment signatures. Only the first call takes effect.
     */
    public static void init(byte[] v2PublicKeyBytes) {
        if (sCommentSignedPublicKeyBytes == null) {
            sCommentSignedPublicKeyBytes = v2PublicKeyBytes;
        }
    }

    private static PublicKey getCommentSignedPublicKey() throws Exception {
        if (sCommentSignedPublicKey == null) {
            sCommentSignedPublicKey = KeyFactory.getInstance(KEY_FACTORY)
                    .generatePublic(new X509EncodedKeySpec(sCommentSignedPublicKeyBytes));
        }
        return sCommentSignedPublicKey;
    }
}

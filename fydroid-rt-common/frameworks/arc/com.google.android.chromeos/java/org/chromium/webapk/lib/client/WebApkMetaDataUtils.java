/* Open source replacement for org.chromium.webapk.lib.client.WebApkMetaDataUtils from the proprietary com.google.android.chromeos jar (originally from Chromium). */
package org.chromium.webapk.lib.client;

import android.os.Bundle;
import android.util.Log;

/**
 * Helpers for reading WebAPK-specific values from an AndroidManifest
 * meta-data {@link Bundle}. Matches the Chromium open-source implementation.
 */
public class WebApkMetaDataUtils {
    static final String TAG = "WebApkMetaDataUtils";

    /**
     * Reads a {@code long} meta-data value that was written as a string with
     * an {@code "L"} suffix (to work around manifest meta-data not supporting
     * {@code long} directly).
     *
     * @param metaData     the meta-data bundle to read from
     * @param name         the key to look up
     * @param defaultValue value returned if the key is missing, does not end
     *                     with {@code "L"}, or fails to parse
     * @return the parsed value or {@code defaultValue}
     */
    public static long getLongFromMetaData(Bundle metaData, String name, long defaultValue) {
        String value = metaData.getString(name);
        if (value == null || !value.endsWith("L")) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.substring(0, value.length() - 1));
        } catch (NumberFormatException e) {
            Log.w(TAG, "Could not read " + name + " long value from bundle.");
            return defaultValue;
        }
    }
}

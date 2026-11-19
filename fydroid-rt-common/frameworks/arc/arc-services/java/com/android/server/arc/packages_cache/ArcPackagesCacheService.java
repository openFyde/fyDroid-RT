package com.android.server.arc.packages_cache;

import android.content.Context;
import android.os.FileUtils;
import android.os.SystemProperties;
import android.util.Log;
import com.android.server.SystemService;
import java.io.File;
import java.io.IOException;

/**
 * Copies packages.xml to packages_copy.xml on boot when requested
 * via ro.boot.copy_packages_cache system property.
 * Values: "0" = default (no copy), "1" = copy on init, "2" = skip setup copy on init.
 */
class ArcPackagesCacheService {
    private static final String PACKAGE_CACHE_COPY_ON_INIT = "1";
    private static final String PACKAGE_CACHE_DEFAULT = "0";
    private static final String PACKAGE_CACHE_SKIP_SETUP_COPY_ON_INIT = "2";
    private static final String TAG = "ArcPackagesCacheService";

    public static class LifeCycle extends SystemService {
        public LifeCycle(Context context) {
            super(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase != SystemService.PHASE_THIRD_PARTY_APPS_CAN_START) {
                return;
            }
            String mode = SystemProperties.get("ro.boot.copy_packages_cache");
            if (mode.isEmpty()) {
                return;
            }
            switch (mode) {
                case PACKAGE_CACHE_DEFAULT:
                    // No copy needed
                    return;
                case PACKAGE_CACHE_COPY_ON_INIT:
                case PACKAGE_CACHE_SKIP_SETUP_COPY_ON_INIT:
                    try {
                        File dir = new File("/data/system");
                        File src = new File(dir, "packages.xml");
                        File dst = new File(dir, "packages_copy.xml");
                        FileUtils.copyFileOrThrow(src, dst);
                        Log.i(TAG, "packages.xml copied to " + dst.getAbsolutePath()
                                + " for cache mode: " + mode);
                    } catch (IOException e) {
                        Log.wtf(TAG, "Failed to copy packages.xml", e);
                    }
                    break;
                default:
                    Log.wtf(TAG, "Invalid ro.boot.copy_packages_cache=" + mode);
                    break;
            }
        }

        @Override
        public void onStart() {
        }
    }

    ArcPackagesCacheService() {
    }
}

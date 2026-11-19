package com.android.server.arc.health;

import android.content.Context;
import android.util.Slog;
import com.android.server.SystemService;

/**
 * System health service for ARC++. The proprietary version handles Google account
 * hash migration and metrics reporting — neither applies to fydroid-rt.
 * This stub prevents ClassNotFoundException during boot.
 */
public class ArcSystemHealthService extends SystemService {
    private static final String TAG = "ArcSystemHealthService";

    public ArcSystemHealthService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        Slog.i(TAG, "ArcSystemHealthService started (stub)");
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == PHASE_BOOT_COMPLETED) {
            Slog.i(TAG, "Boot completed");
        }
    }
}

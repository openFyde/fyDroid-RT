package com.android.server.arc.abimigration;

import android.content.Context;
import android.util.Slog;
import com.android.server.SystemService;

/**
 * ABI migration service stub. The proprietary version handles 32→64-bit ABI migration
 * and GMS metrics — not relevant to fydroid-rt.
 */
public class ArcAbiMigrationService extends SystemService {
    private static final String TAG = "ArcAbiMigrationService";

    public ArcAbiMigrationService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        Slog.i(TAG, "ArcAbiMigrationService started (stub)");
    }

    @Override
    public void onBootPhase(int phase) {
    }
}

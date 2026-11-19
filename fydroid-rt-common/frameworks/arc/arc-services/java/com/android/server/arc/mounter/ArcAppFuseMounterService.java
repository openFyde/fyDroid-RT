package com.android.server.arc.mounter;

import android.content.Context;
import android.util.Slog;
import com.android.server.SystemService;

public class ArcAppFuseMounterService {
    private static final String TAG = "ArcAppFuseMounter";

    public static class LifeCycle extends SystemService {
        public LifeCycle(Context context) {
            super(context);
        }

        @Override
        public void onStart() {
            Slog.i(TAG, "ArcAppFuseMounterService started (stub)");
        }
    }
}

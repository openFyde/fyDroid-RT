package com.android.server.arc.mounter;

import android.content.Context;
import android.util.Slog;
import com.android.server.SystemService;

public class ArcObbMounterService {
    private static final String TAG = "ArcObbMounter";

    public static class LifeCycle extends SystemService {
        public LifeCycle(Context context) {
            super(context);
        }

        @Override
        public void onStart() {
            Slog.i(TAG, "ArcObbMounterService started (stub)");
        }
    }
}

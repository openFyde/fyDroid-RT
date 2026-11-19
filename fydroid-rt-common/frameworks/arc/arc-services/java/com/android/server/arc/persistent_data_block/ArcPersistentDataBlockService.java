package com.android.server.arc.persistent_data_block;

import android.content.Context;
import android.util.Slog;
import com.android.server.SystemService;

public class ArcPersistentDataBlockService extends SystemService {
    private static final String TAG = "ArcPersistentDataBlock";

    public ArcPersistentDataBlockService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        Slog.i(TAG, "ArcPersistentDataBlockService started (stub)");
    }
}

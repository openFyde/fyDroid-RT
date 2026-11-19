package org.chromium.arc.home;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

public class HomeActivity extends Activity {
    private static final String TAG = "ArcHome";

    @Override
    public void onBackPressed() {
        // No-op — home activity cannot go back.
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // Create a 1×1 non-focusable window. Chrome OS provides the actual
        // launcher UI; this activity just satisfies the HOME intent filter.
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 1;
        lp.height = 1;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "Skip configuration change event:" + newConfig + ".");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}

package org.chromium.arc.view_downloads_handler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ArcViewDownloadsHandlerActivity extends Activity {
    private static final String TAG = "ArcViewDownloadsHandlerActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && "android.intent.action.VIEW_DOWNLOADS".equals(intent.getAction())) {
            forwardIntentToIntentHelper(intent);
            setResult(RESULT_OK);
        } else {
            Log.e(TAG, "Doing nothing.");
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    // Stock ARC forwarded to ArcAppLauncher's ChromeBrowserProxyActivity, which
    // fydroid does not have. ArcIntentHelper opens chrome://downloads instead.
    private void forwardIntentToIntentHelper(Intent intent) {
        intent.setComponent(new ComponentName("org.chromium.arc.intent_helper",
                "org.chromium.arc.intent_helper.ArcIntentHelperActivity"));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to forward intent to ArcIntentHelper", e);
        }
    }
}

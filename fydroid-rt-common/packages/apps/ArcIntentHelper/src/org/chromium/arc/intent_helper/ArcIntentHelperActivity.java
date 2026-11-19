package org.chromium.arc.intent_helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Hands links, web searches, mailto:/geo: links, WebAPK launches, the
 * downloads page and scan requests from Android apps to Chrome.
 *
 * There is no Android browser in the container: this activity is the only
 * http/https handler, so it is what apps reach when they open a link. It
 * converts the intent (OpenInChromeRequests) and sends the result to
 * ArcIntentHelperService in system_server, which owns the IntentHelperHost Mojo
 * connection.
 */
public class ArcIntentHelperActivity extends Activity {
    private static final String TAG = "ArcIntentHelperActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Intent request = intent == null ? null : OpenInChromeRequests.fromIntent(intent);
        if (request != null) {
            sendBroadcast(request.setPackage("android")
                    .addFlags(Intent.FLAG_RECEIVER_FOREGROUND));
            setResult(RESULT_OK);
        } else {
            Log.e(TAG, "Chrome cannot handle, ignoring: " + intent);
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}

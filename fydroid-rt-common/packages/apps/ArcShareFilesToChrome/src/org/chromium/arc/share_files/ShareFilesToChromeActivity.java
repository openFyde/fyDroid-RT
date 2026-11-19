package org.chromium.arc.share_files;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ShareFilesToChromeActivity extends Activity {
    private static final String TAG = ShareFilesToChromeActivity.class.getSimpleName();
    private static final int REQUEST_CREATE_DOCUMENT = 1;
    private static final int REQUEST_OPEN_DOCUMENT_TREE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        Uri data = intent.getData();
        ClipData clipData = intent.getClipData();
        String action = intent.getAction();

        if (data == null && clipData == null) {
            finish();
            return;
        }

        if ("android.intent.action.SEND".equals(action)) {
            Intent createDoc = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            createDoc.addCategory(Intent.CATEGORY_OPENABLE);
            createDoc.setType(intent.getType());
            if (clipData != null && clipData.getItemAt(0) != null) {
                data = clipData.getItemAt(0).getUri();
            }
            if (data != null) {
                createDoc.putExtra(Intent.EXTRA_TITLE,
                        ShareFilesToChromeService.getSuggestedNameForUri(
                                getApplicationContext(), data, 0));
                startActivityForResult(createDoc, REQUEST_CREATE_DOCUMENT);
                setResult(RESULT_OK);
            } else {
                Log.e(TAG, "Unable to retrieve the file's Uri.");
                finish();
            }
        } else if ("android.intent.action.SEND_MULTIPLE".equals(action)) {
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                    REQUEST_OPEN_DOCUMENT_TREE);
            setResult(RESULT_OK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.i(TAG, "Bad response received. resultCode: " + resultCode);
            finish();
            return;
        }
        if (data == null || data.getData() == null) {
            Log.e(TAG, "Incomplete information received");
            finish();
            return;
        }

        Uri destinationUri = data.getData();
        ClipData clipData = getIntent().getClipData();
        Uri sourceUri = getIntent().getData();

        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName("org.chromium.arc.share_files",
                "org.chromium.arc.share_files.ShareFilesToChromeService"));
        serviceIntent.putExtra("destination", destinationUri);
        serviceIntent.putExtra("result", requestCode);

        if (clipData != null) {
            serviceIntent.setClipData(clipData);
        } else {
            serviceIntent.setClipData(
                    ClipData.newUri(getContentResolver(), "sourceUri", sourceUri));
        }
        serviceIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        getContentResolver().takePersistableUriPermission(
                destinationUri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));

        if (requestCode == REQUEST_CREATE_DOCUMENT || requestCode == REQUEST_OPEN_DOCUMENT_TREE) {
            startForegroundService(serviceIntent);
        } else {
            Log.wtf(TAG, "Unexpected requestCode: " + requestCode);
        }
        finish();
    }
}

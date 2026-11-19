package org.chromium.arc.share_files;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import libcore.io.IoUtils;

public class ShareFilesToChromeService extends Service {
    private static final String TAG = ShareFilesToChromeService.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID = TAG;
    private static final int NOTIFICATION_ID = 42;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT));
        startForeground(NOTIFICATION_ID,
                new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(TAG).build());

        int actionCode = intent.getIntExtra("result", 0);
        List<Uri> srcUris = extractUrisFromClipData(intent.getClipData());
        Uri destination = intent.getParcelableExtra("destination");

        if (srcUris == null || srcUris.isEmpty()) {
            Log.e(TAG, "Unable to retrieve data via getClipData()");
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        new AsyncCopyFiles(this, destination, srcUris, actionCode, startId).execute();
        return START_NOT_STICKY;
    }

    private List<Uri> extractUrisFromClipData(ClipData clipData) {
        if (clipData == null) return null;
        int count = clipData.getItemCount();
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            if (uri != null) {
                uris.add(uri);
            } else {
                Log.e(TAG, "The Uri for item at index " + i + " in clipData is null");
            }
        }
        return uris;
    }

    static String getSuggestedNameForUri(Context context, Uri uri, int index) {
        Cursor cursor = context.getContentResolver().query(
                uri, new String[]{"_display_name"}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst() && cursor.getCount() == 1
                        && cursor.getColumnCount() == 1) {
                    return cursor.getString(0);
                }
            } finally {
                IoUtils.closeQuietly(cursor);
            }
        }
        return Integer.toString(index);
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    private static class AsyncCopyFiles {
        private final ShareFilesToChromeService mService;
        private final Uri mDestinationUri;
        private final List<Uri> mSrcUriList;
        private final int mActionCode;
        private final int mStartId;
        private final List<Uri> mDestinationUriList = new ArrayList<>();
        private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

        AsyncCopyFiles(ShareFilesToChromeService service, Uri destination,
                List<Uri> srcUris, int actionCode, int startId) {
            mService = service;
            mDestinationUri = destination;
            mSrcUriList = srcUris;
            mActionCode = actionCode;
            mStartId = startId;
        }

        void execute() {
            final Handler handler = new Handler(Looper.getMainLooper());
            mExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    final boolean success;
                    if (mActionCode == 1) {
                        // Single file
                        mDestinationUriList.add(mDestinationUri);
                        ParcelFileDescriptor dst = getPFD(mDestinationUri, "w");
                        ParcelFileDescriptor src = getPFD(mSrcUriList.get(0), "r");
                        success = copyFile(src, dst);
                    } else if (mActionCode == 2) {
                        // Multiple files
                        success = createAndCopyFiles(mDestinationUri, mSrcUriList);
                    } else {
                        Log.e(TAG, "Received an invalid request from the activity");
                        success = false;
                    }

                    if (!success) {
                        boolean cleaned = deletePartialCopy();
                        Log.e(TAG, "Deleting partially copied files. Success? " + cleaned);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mService.showToast(success
                                    ? R.string.files_app : R.string.rollback_toast);
                            mService.stopSelf(mStartId);
                        }
                    });
                }
            });
        }

        private boolean copyFile(ParcelFileDescriptor src, ParcelFileDescriptor dst) {
            if (src == null || dst == null) {
                IoUtils.closeQuietly(src);
                IoUtils.closeQuietly(dst);
                return false;
            }
            byte[] buf = new byte[8192];
            ParcelFileDescriptor.AutoCloseInputStream in =
                    new ParcelFileDescriptor.AutoCloseInputStream(src);
            ParcelFileDescriptor.AutoCloseOutputStream out =
                    new ParcelFileDescriptor.AutoCloseOutputStream(dst);
            try {
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Cannot read/write file.", e);
                return false;
            } finally {
                IoUtils.closeQuietly(in);
                IoUtils.closeQuietly(out);
            }
        }

        private ParcelFileDescriptor getPFD(Uri uri, String mode) {
            try {
                return mService.getContentResolver().openFileDescriptor(uri, mode);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Invalid Uri: " + uri + " or mode: " + mode);
                return null;
            }
        }

        private boolean createAndCopyFiles(Uri treeUri, List<Uri> srcUris) {
            if (!DocumentsContract.isTreeUri(treeUri)) {
                Log.e(TAG, "Should always receive a treeUri for SEND_MULTIPLE");
                return false;
            }
            Uri parentUri = DocumentsContract.buildDocumentUriUsingTree(
                    treeUri, DocumentsContract.getTreeDocumentId(treeUri));

            for (int i = 0; i < srcUris.size(); i++) {
                try {
                    String mimeType = mService.getContentResolver().getType(srcUris.get(i));
                    String name = getSuggestedNameForUri(mService, srcUris.get(i), i);
                    Uri newDoc = DocumentsContract.createDocument(
                            mService.getContentResolver(), parentUri, mimeType, name);
                    if (newDoc == null) {
                        Log.e(TAG, "Unable to create a new file");
                        return false;
                    }
                    mDestinationUriList.add(newDoc);
                    if (!copyFile(getPFD(srcUris.get(i), "r"), getPFD(newDoc, "w"))) {
                        Log.e(TAG, "Unable to copy file.");
                        return false;
                    }
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Cannot create the document in the new location.", e);
                    return false;
                }
            }
            return true;
        }

        private boolean deletePartialCopy() {
            if (mDestinationUriList.isEmpty()) return true;
            boolean allDeleted = true;
            for (Uri uri : mDestinationUriList) {
                try {
                    allDeleted &= DocumentsContract.deleteDocument(
                            mService.getContentResolver(), uri);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Unable to find the file to delete.");
                    allDeleted = false;
                }
            }
            if (!allDeleted) {
                Log.w(TAG, "Task unable to rollback all the new files.");
            }
            return allDeleted;
        }
    }
}

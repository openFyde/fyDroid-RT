package org.chromium.arc.apkcacheprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileNotFoundException;

/**
 * Stub ContentProvider for APK caching. Returns empty results for all queries.
 * Chrome checks for this provider's existence and queries for cached APKs.
 * A full implementation would use Room ORM with session-based upload management,
 * LRU eviction, and CloudDPC eligibility checking.
 */
public class ApkCacheProvider extends ContentProvider {
    private static final String TAG = "ApkCacheProvider";

    // Column names matching the expected V1/V2 API schema
    private static final String[] COLUMNS = {
        "_id",
        "package_name",
        "version_code",
        "uri",
        "hash",
        "is_installed",
        "session_id",
    };

    @Override
    public boolean onCreate() {
        Log.i(TAG, "ApkCacheProvider created (stub)");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // Return empty cursor with appropriate columns
        String[] cols = (projection != null) ? projection : COLUMNS;
        return new MatrixCursor(cols, 0);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        throw new FileNotFoundException("APK cache is not available (stub)");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}

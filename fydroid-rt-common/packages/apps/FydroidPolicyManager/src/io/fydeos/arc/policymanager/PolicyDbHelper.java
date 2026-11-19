package io.fydeos.arc.policymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** Persists active package policies so they can be re-applied (and reverted) across reboots. */
final class PolicyDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "PolicyManagerDB";

    static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "Policy.db";

    private static final String TABLE_NAME = "fydeos_policy_table";
    private static final String COLUMN_NAME_PACKAGE = "package_name";
    private static final String COLUMN_NAME_POLICY = "policy";
    private static final String COLUMN_NAME_INSTALL_TYPE = "install_type";
    private static final String COLUMN_NAME_IS_REGEX = "is_regex";

    private static final String SQL_CREATE_POLICIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_NAME_PACKAGE + " TEXT UNIQUE,"
                    + COLUMN_NAME_INSTALL_TYPE + " INTEGER,"
                    + COLUMN_NAME_POLICY + " INTEGER,"
                    + COLUMN_NAME_IS_REGEX + " INTEGER)";

    private static final String SQL_DELETE_POLICIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // The persisted policy carries no download URL; force-install records reconstructed from the
    // DB are only used for enforcement/revert, never for downloading, so a placeholder is enough.
    private static final URL DUMMY_URL;
    static {
        URL u = null;
        try {
            u = new URL("http://");
        } catch (MalformedURLException ignored) {
        }
        DUMMY_URL = u;
    }

    PolicyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POLICIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_POLICIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    boolean storePolicy(PackagePolicy packagePolicy) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_PACKAGE, packagePolicy.packageName);
        values.put(COLUMN_NAME_INSTALL_TYPE, packagePolicy.installType.toInt());
        values.put(COLUMN_NAME_POLICY, packagePolicy.permissionPolicy.toInt());
        values.put(COLUMN_NAME_IS_REGEX, packagePolicy.isRegex ? 1 : 0);
        long newRowId = db.replace(TABLE_NAME, null, values);
        return newRowId != -1;
    }

    private PackagePolicy fromCursor(Cursor cursor) {
        return new PackagePolicy(
                cursor.getString(0),
                InstallType.byIndex(cursor.getInt(1)),
                PermissionPolicy.byIndex(cursor.getInt(2)),
                DUMMY_URL,
                Origin.LOCAL,
                cursor.getInt(3) == 1);
    }

    PackagePolicy getPolicy(String packageName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                COLUMN_NAME_PACKAGE, COLUMN_NAME_INSTALL_TYPE, COLUMN_NAME_POLICY, COLUMN_NAME_IS_REGEX
        };
        String selection = COLUMN_NAME_PACKAGE + " = ? OR " + COLUMN_NAME_IS_REGEX + " = 1";
        String[] selectionArgs = {packageName};
        // The governing row is chosen with the shared precedence (a literal beats every pattern,
        // then the longer pattern), never by SQL order: LENGTH() alone let an equal-length or
        // longer regex beat the exact entry, and made ties depend on row order. ArcPolicyService's
        // install-time gate resolves with the same rule, so the two cannot disagree.
        PackagePolicy best = null;
        try (Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null,
                null)) {
            while (cursor.moveToNext()) {
                PackagePolicy policy = fromCursor(cursor);
                if (policy.match(packageName)
                        && (best == null || policy.comparePrecedence(best) < 0)) {
                    best = policy;
                }
            }
        }
        return best;
    }

    /**
     * Returns (and clears) all stored policies whose package is NOT in {@code packageNames}.
     * Used to build revert policies for apps that dropped out of the latest policy set; the table
     * is wiped afterwards because the caller re-stores the still-current entries.
     */
    List<PackagePolicy> getExclusivePolicies(String[] packageNames) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                COLUMN_NAME_PACKAGE, COLUMN_NAME_INSTALL_TYPE, COLUMN_NAME_POLICY, COLUMN_NAME_IS_REGEX
        };
        String selection = null;
        String[] selectionArgs = null;
        if (packageNames.length > 0) {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < packageNames.length; i++) {
                placeholders.append(i == 0 ? "?" : ",?");
            }
            selection = COLUMN_NAME_PACKAGE + " NOT IN (" + placeholders + ")";
            selectionArgs = packageNames;
        }
        List<PackagePolicy> policies = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null,
                null)) {
            while (cursor.moveToNext()) {
                policies.add(fromCursor(cursor));
            }
        }
        int removed = getWritableDatabase().delete(TABLE_NAME, null, null);
        Log.d(TAG, "removed " + removed + " record(s)");
        return policies;
    }

    boolean hasPolicy() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME_PACKAGE}, null, null,
                null, null, null)) {
            return cursor.moveToNext();
        }
    }
}

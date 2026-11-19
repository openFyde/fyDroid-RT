package org.chromium.arc.home;

import android.app.Service;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

import android.app.ActivityOptions;

import org.chromium.arc.applauncher.AppShortcutItemInfo;
import org.chromium.arc.applauncher.IArcAppShortcutsReceiver;
import org.chromium.arc.applauncher.IArcAppShortcutsService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArcAppShortcutsArcHomeService extends Service {
    private static final String TAG = "ArcAppShortcutsArcHomeService";
    private static final int ICON_SIZE_PX = 96; // XHDPI default

    private LauncherApps mLauncherApps;

    private interface Filter {
        boolean match(ShortcutInfo info);
    }

    private final IArcAppShortcutsService.Stub mBinder = new IArcAppShortcutsService.Stub() {
        @Override
        public void getAppShortcutGlobalQueryItems(String query, int maxResults,
                IArcAppShortcutsReceiver receiver) {
            final String lowerQuery = query.toLowerCase();
            List<ShortcutInfo> shortcuts = getShortcutInfoList(null, new Filter() {
                @Override
                public boolean match(ShortcutInfo info) {
                    CharSequence label = info.getShortLabel();
                    return label != null
                            && label.toString().toLowerCase().contains(lowerQuery);
                }
            });

            // Sort by label length (shortest first)
            Collections.sort(shortcuts, new Comparator<ShortcutInfo>() {
                @Override
                public int compare(ShortcutInfo a, ShortcutInfo b) {
                    int lenA = a.getShortLabel() != null ? a.getShortLabel().length() : 0;
                    int lenB = b.getShortLabel() != null ? b.getShortLabel().length() : 0;
                    return Integer.compare(lenA, lenB);
                }
            });

            if (maxResults > 0 && shortcuts.size() > maxResults) {
                shortcuts = shortcuts.subList(0, maxResults);
            }

            List<AppShortcutItemInfo> results = new ArrayList<>();
            for (ShortcutInfo info : shortcuts) {
                results.add(new AppShortcutItemInfo(parseShortcutInfo(info)));
            }
            notifyResults(results, receiver);
        }

        @Override
        public void getAppShortcutItems(String packageName, IArcAppShortcutsReceiver receiver) {
            List<ShortcutInfo> shortcuts = getShortcutInfoList(packageName, new Filter() {
                @Override
                public boolean match(ShortcutInfo info) {
                    // Exclude settings shortcuts and empty labels
                    if ("com.android.settings".equals(info.getPackage())) {
                        return false;
                    }
                    CharSequence label = info.getShortLabel();
                    return label != null && label.length() > 0;
                }
            });

            List<AppShortcutItemInfo> results = new ArrayList<>();
            for (ShortcutInfo info : shortcuts) {
                results.add(new AppShortcutItemInfo(parseShortcutInfo(info)));
            }
            notifyResults(results, receiver);
        }

        @Override
        public void launchAppShortcutItem(String packageName, String shortcutId, int displayId) {
            UserHandle user = getUserHandle();
            if (user == null) return;

            try {
                Bundle opts = null;
                if (displayId != -1) {
                    opts = ActivityOptions.makeBasic()
                            .setLaunchDisplayId(displayId).toBundle();
                }
                mLauncherApps.startShortcut(packageName, shortcutId, null, opts, user);
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch shortcut " + shortcutId, e);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mLauncherApps = (LauncherApps) getSystemService(LAUNCHER_APPS_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private UserHandle getUserHandle() {
        UserManager um = (UserManager) getSystemService(USER_SERVICE);
        List<UserHandle> profiles = um.getUserProfiles();
        return profiles.isEmpty() ? null : profiles.get(0);
    }

    private List<ShortcutInfo> getShortcutInfoList(String packageName, Filter filter) {
        UserHandle user = getUserHandle();
        if (user == null || mLauncherApps == null) {
            return Collections.emptyList();
        }

        LauncherApps.ShortcutQuery query = new LauncherApps.ShortcutQuery();
        query.setQueryFlags(LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
                | LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC);
        if (packageName != null) {
            query.setPackage(packageName);
        }

        List<ShortcutInfo> shortcuts;
        try {
            shortcuts = mLauncherApps.getShortcuts(query, user);
        } catch (Exception e) {
            Log.e(TAG, "Failed to query shortcuts", e);
            return Collections.emptyList();
        }

        if (shortcuts == null) {
            return Collections.emptyList();
        }

        if (filter == null) {
            return shortcuts;
        }

        List<ShortcutInfo> filtered = new ArrayList<>();
        for (ShortcutInfo info : shortcuts) {
            if (filter.match(info)) {
                filtered.add(info);
            }
        }
        return filtered;
    }

    private Bundle parseShortcutInfo(ShortcutInfo info) {
        Bundle bundle = new Bundle();
        String prefix = "ArcAppShortcutsArcHomeService.";
        bundle.putString(prefix + "shortcutId", info.getId());
        CharSequence label = info.getShortLabel();
        bundle.putString(prefix + "shortLabel", label != null ? label.toString() : "");
        bundle.putString(prefix + "packageName", info.getPackage());
        bundle.putInt(prefix + "type", info.isDeclaredInManifest() ? 0 : 1);
        bundle.putInt(prefix + "rank", info.getRank());

        parseShortcutIconInfo(info, bundle);
        return bundle;
    }

    private void parseShortcutIconInfo(ShortcutInfo info, Bundle bundle) {
        String prefix = "ArcAppShortcutsArcHomeService.";
        UserHandle user = getUserHandle();
        if (user == null) return;

        try {
            Drawable icon = mLauncherApps.getShortcutIconDrawable(info, ICON_SIZE_PX);
            if (icon == null) {
                bundle.putBoolean(prefix + "isAdaptiveIcon", false);
                return;
            }

            if (icon instanceof AdaptiveIconDrawable) {
                AdaptiveIconDrawable adaptiveIcon = (AdaptiveIconDrawable) icon;
                bundle.putBoolean(prefix + "isAdaptiveIcon", true);
                bundle.putByteArray(prefix + "icon", drawableToPng(icon, ICON_SIZE_PX));
                Drawable fg = adaptiveIcon.getForeground();
                Drawable bg = adaptiveIcon.getBackground();
                if (fg != null) {
                    bundle.putByteArray(prefix + "foregroundIcon",
                            drawableToPng(fg, ICON_SIZE_PX));
                }
                if (bg != null) {
                    bundle.putByteArray(prefix + "backgroundIcon",
                            drawableToPng(bg, ICON_SIZE_PX));
                }
            } else {
                bundle.putBoolean(prefix + "isAdaptiveIcon", false);
                bundle.putByteArray(prefix + "icon", drawableToPng(icon, ICON_SIZE_PX));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get icon for shortcut " + info.getId(), e);
            bundle.putBoolean(prefix + "isAdaptiveIcon", false);
        }
    }

    private static byte[] drawableToPng(Drawable drawable, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, size, size);
        drawable.draw(canvas);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        bitmap.recycle();
        return baos.toByteArray();
    }

    private void notifyResults(List<AppShortcutItemInfo> results,
            IArcAppShortcutsReceiver receiver) {
        try {
            receiver.onReceiveResults(results);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to send results to receiver", e);
        }
    }
}

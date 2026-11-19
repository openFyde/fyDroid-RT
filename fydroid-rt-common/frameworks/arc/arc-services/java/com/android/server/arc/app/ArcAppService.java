package com.android.server.arc.app;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.TaskStackListener;
import android.compat.Compatibility;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.android.internal.compat.CompatibilityChangeConfig;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.compat.PlatformCompat;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.AppDiscoveryResult;
import org.chromium.arc.mojom.AppHost;
import org.chromium.arc.mojom.AppInfo;
import org.chromium.arc.mojom.AppInstance;
import org.chromium.arc.mojom.AppShortcutItem;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.ArcPackageInfo;
import org.chromium.arc.mojom.RawIconPngData;
import org.chromium.arc.mojom.Rect;
import org.chromium.arc.mojom.WindowInfo;
import org.chromium.mojo.system.MojoException;
import java.util.List;

/**
 * ARC app service bridging Android task lifecycle to Chrome OS via Mojo AppHost.
 *
 * Notifies Chrome of task creation, destruction, and activation so it can manage
 * ARC windows. Also pushes task→sessionId mappings to native SurfaceFlinger via
 * the Wayland binder service for window matching.
 */
class ArcAppService implements AppInstance, ArcService {
    private static final String TAG = "ArcAppService";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    // arc::mojom::ShowPackageInfoPage, from
    // frameworks/native/libs/arcbridge/mojom/arc/app.mojom.
    private static final int SHOW_PACKAGE_INFO_MAIN = 0;
    private static final int SHOW_PACKAGE_INFO_MANAGE_LINKS = 1;
    private static final int SHOW_PACKAGE_INFO_MANAGE_PERMISSIONS = 2;

    // fydroid: user-flagged "this app is a game" set, stored in Settings.Secure
    // as a ':'-separated package list (written by Settings > App info > "Open
    // with game control panel"). Sideloaded games carry
    // ApplicationInfo.category == UNDEFINED
    // because no Play Store ever called setApplicationCategoryHint(), so Chrome
    // never treats them as games. Flagging a package here makes getAppCategory()
    // and the app-list refresh report CATEGORY_GAME, which lets ChromeOS attach
    // the Game Dashboard, the Game Controls mouse/keyboard->touch overlay, and
    // full-screen game efficiency (IsGame). Key must match
    // GameCategoryPreferenceController.SECURE_KEY in packages/apps/Settings.
    static final String GAME_PKGS_SECURE_KEY = "fydroid_game_packages";

    // fydroid: render games at a target resolution and upscale.
    //
    // AOSP's app-compat framework can make an app render into a buffer that is
    // a fraction of the window size and have WM upscale it to fill the window
    // (CompatModePackages.DOWNSCALED + one DOWNSCALE_<pct> change). That is a
    // pure fill-rate saving: the app's own GL/Vulkan rasterisation shrinks with
    // the buffer, which is the part that actually costs GPU time. Note this is
    // NOT what WaylandLayerContainer's game-efficiency mode does — that only
    // resizes SF's composition target, leaving the app rendering every pixel.
    //
    // Measured on Crossy Road: at the native panel resolution the GPU sits at
    // ~99.8% busy and the game manages ~28 fps with ~18% of frames missing
    // their deadline. Rendering at 60% (amd64 / i3-1005G1, 2736x1824) or 50%
    // (arm64 / RK3588, 2560x1600) gives a steady 60 fps with no late frames.
    //
    // The policy is a target height, not a percentage: what the GPU pays for
    // is pixels, and a fixed fraction of whatever panel a board happens to
    // drive is wrong at both ends. 50% renders a 2.5K panel at a sensible 800p
    // but a 1080p panel at 540p, and a 768p panel at 384p. The height is
    // measured along the panel's short side (720 = 720p), so rotation does not
    // change it; the step is picked from the default display at sweep time.
    //
    // The user's own choice wins over both properties: fydroid runs on
    // everything from an RK3588 tablet to a box with a discrete GPU, so the
    // board default is a starting point rather than an answer, and Settings >
    // Display > "Game rendering quality" lets the user pick 30%-100% of their
    // panel. That slider stores the height the percentage works out to, not the
    // percentage, so the choice keeps its meaning when the panel changes. Key
    // must match GameRenderQualityPreferenceController.SECURE_KEY in
    // packages/apps/Settings. Unset = follow the board.
    static final String GAME_RENDER_HEIGHT_SECURE_KEY = "fydroid_game_render_height";

    // "persist." wins over "ro." so a board can be retuned without a rebuild.
    // An unset property means the default below, not "off": the container's
    // build.prop is generated by the ChromeOS host rather than taken from the
    // image, so the ro. value is only there if the host carries it. 0 disables.
    private static final String GAME_RENDER_HEIGHT_PROP_PERSIST =
            "persist.fydroid.game_render_height";
    private static final String GAME_RENDER_HEIGHT_PROP_RO =
            "ro.fydroid.game_render_height";
    private static final int DEFAULT_GAME_RENDER_HEIGHT = 720;

    // Applying a step force-stops every running game, so writes to the setting
    // are coalesced rather than swept one by one.
    private static final long GAME_RENDER_HEIGHT_DEBOUNCE_MS = 500;

    // This service is the only writer of the DOWNSCALED / DOWNSCALE_<pct>
    // overrides. AOSP's GameManagerService writes the same ones — it disables
    // DOWNSCALED for every game at user unlock and on each package update — so
    // fydroid pins that off (FydroidDefaults.ENABLE_GAME_MODE_DOWNSCALING in
    // frameworks/base). Re-enabling it brings back a last-writer-wins race.

    // CompatModePackages.DOWNSCALED — the gatekeeper: a DOWNSCALE_<pct> change
    // does nothing unless this is enabled too. Duplicated as a literal because
    // those constants live in services.core (com.android.server.wm), which this
    // arc-services module does not link against.
    private static final long CHANGE_DOWNSCALED = 168419799L;

    // Percentage -> CompatModePackages.DOWNSCALE_<pct> change id, largest step
    // first (scalePercentFor() relies on that order). Only these steps exist,
    // so the rendered height lands within one 5% step of the target.
    private static final Map<Integer, Long> DOWNSCALE_CHANGE_BY_PCT =
            new LinkedHashMap<>();
    static {
        DOWNSCALE_CHANGE_BY_PCT.put(90, 182811243L);
        DOWNSCALE_CHANGE_BY_PCT.put(85, 189969734L);
        DOWNSCALE_CHANGE_BY_PCT.put(80, 176926753L);
        DOWNSCALE_CHANGE_BY_PCT.put(75, 189969779L);
        DOWNSCALE_CHANGE_BY_PCT.put(70, 176926829L);
        DOWNSCALE_CHANGE_BY_PCT.put(65, 189969744L);
        DOWNSCALE_CHANGE_BY_PCT.put(60, 176926771L);
        DOWNSCALE_CHANGE_BY_PCT.put(55, 189970036L);
        DOWNSCALE_CHANGE_BY_PCT.put(50, 176926741L);
        DOWNSCALE_CHANGE_BY_PCT.put(45, 189969782L);
        DOWNSCALE_CHANGE_BY_PCT.put(40, 189970038L);
        DOWNSCALE_CHANGE_BY_PCT.put(35, 189969749L);
        DOWNSCALE_CHANGE_BY_PCT.put(30, 189970040L);
    }

    private final Context mContext;
    private AppHost mHost;
    private BroadcastReceiver mPackageReceiver;
    // Downscale percentage the last full sweep applied (0 = native), or -1
    // before the first one. mTaskHandler thread only.
    private int mGameRenderScalePct = -1;
    // Held as a field so a burst of setting changes can be collapsed into one
    // sweep (removeCallbacks needs the same Runnable instance).
    private final Runnable mRefreshGameRenderScale = this::refreshGameRenderScale;

    // Task tracking state (proprietary AppTaskTracker pattern). All access on
    // mTaskHandler thread; no extra locking needed.
    private HandlerThread mTaskThread;
    private Handler mTaskHandler;
    private SparseArray<TaskSnapshot> mPreviousTasks = new SparseArray<>();
    /**
     * The launcher components last published to Chrome, keyed by package.
     *
     * ACTION_PACKAGE_CHANGED is ordinary background traffic — WorkManager
     * toggles a receiver component in most apps that use it — so a package's
     * app list is only re-published when this set has actually moved.
     */
    private final Map<String, Set<String>> mPublishedLauncherActivities =
            new HashMap<>();
    /** Debounced launcher-refresh runnables, keyed by package. */
    private final Map<String, Runnable> mPendingLauncherRefresh = new HashMap<>();
    /**
     * How long to let a package's components settle before re-publishing its
     * launcher entries.
     *
     * Swapping a launcher icon is two calls, not one: the app disables the old
     * &lt;activity-alias&gt; and then enables the new one, and each is its own
     * ACTION_PACKAGE_CHANGED. Acting on the first alone would tell Chrome the
     * package has no launcher entry at all, dropping the app from the launcher
     * and the shelf for the instant until the second arrives — and Chrome does
     * not put a removed app back where it was.
     */
    private static final long LAUNCHER_REFRESH_SETTLE_MS = 1000;
    private int mLastActiveTaskId = -1;
    private final Runnable mRefreshTasksRunnable = this::refreshTasks;

    // Icon sent with a task description. Chrome rescales it to the 48dip window
    // icon, so 128px keeps it sharp on a 2x panel without shipping a huge PNG
    // over the bridge on every label change.
    private static final int TASK_DESCRIPTION_ICON_SIZE = 128;

    private static final class TaskSnapshot {
        final ComponentName component;
        final int sessionId;
        // What Chrome will call this window, and the theme colours the activity
        // asked for. Chrome learns a task's name from nowhere else: the only
        // path to a title on the aura window is
        // ArcAppWindow::SetDescription(), reached exclusively from
        // AppHost::OnTaskDescriptionChanged(). Without it every ARC window
        // keeps the placeholder title the compositor put on its wl_surface
        // ("fydroid-task-<id>"), which is then what the shelf, overview,
        // alt-tab, ChromeVox and the Chrome task manager all show.
        //
        // Null unless the app called setTaskDescription() with a label of its
        // own — resolving the fallback costs a PackageManager lookup, and
        // refreshTasks() runs on every task-stack change, so that is deferred
        // to the point where a description is actually pushed.
        final String descriptionLabel;
        final int primaryColor;
        final int statusBarColor;

        TaskSnapshot(ComponentName c, int s, String label, int primary, int statusBar) {
            component = c;
            sessionId = s;
            descriptionLabel = label;
            primaryColor = primary;
            statusBarColor = statusBar;
        }

        boolean sameDescriptionAs(TaskSnapshot other) {
            return other != null
                    && component.equals(other.component)
                    && Objects.equals(descriptionLabel, other.descriptionLabel)
                    && primaryColor == other.primaryColor
                    && statusBarColor == other.statusBarColor;
        }
    }

    // --- LifeCycle ---

    public static class LifeCycle extends SystemService {
        private final ArcAppService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcAppService(context);
        }

        @Override
        public void onBootPhase(int phase) {
            if (phase == PHASE_BOOT_COMPLETED) {
                mService.onSystemBooted();
            }
        }

        @Override
        public void onStart() {
        }

        // fydroid-rt (AOSP-13): SystemService.onUnlockUser(int) was replaced by
        // onUserUnlocked(TargetUser) in 11/12.
        @Override
        public void onUserUnlocked(TargetUser user) {
            if (user.getUserIdentifier() == 0) {
                mService.onUserUnlocked();
            }
        }
    }

    ArcAppService(Context context) {
        mContext = context;
    }

    void onUserUnlocked() {
        // First complete app list: pre-unlock sends are suppressed in
        // sendAppList() because they would omit direct-boot-unaware apps.
        Log.i(TAG, "User unlocked, sending app list");
        sendAppList();
        // Same reason sendAppList() waits for unlock: before it,
        // getInstalledApplications() omits direct-boot-unaware apps, so a sweep
        // run at PHASE_BOOT_COMPLETED silently skips most games. mTaskHandler
        // is created in onSystemBooted(), which SystemService runs first, but
        // fall back to this thread rather than dropping the sweep if that ever
        // changes.
        if (mTaskHandler != null) {
            mTaskHandler.post(this::refreshGameRenderScale);
            registerDisplayListener();
        } else {
            refreshGameRenderScale();
        }
    }

    // The downscale step is derived from the panel size, so a different panel
    // needs a new sweep — a monitor swapped at runtime replaces display 0 in
    // place. onDisplayChanged also fires for rotation, power and refresh-rate
    // changes; those leave the short side, and so the step, where it was.
    // Only an actual change of step sweeps, because applying one restarts
    // every running game. Registered after unlock for the reason the first
    // sweep waits for it, and after that sweep is queued so it runs first.
    private void registerDisplayListener() {
        DisplayManager dm = mContext.getSystemService(DisplayManager.class);
        if (dm == null) return;
        dm.registerDisplayListener(new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int displayId) {
            }

            @Override
            public void onDisplayRemoved(int displayId) {
            }

            @Override
            public void onDisplayChanged(int displayId) {
                if (displayId != Display.DEFAULT_DISPLAY) return;
                final int pct = gameRenderScalePercent();
                if (pct >= 0 && pct != mGameRenderScalePct) {
                    refreshGameRenderScale();
                }
            }
        }, mTaskHandler);
    }

    void onSystemBooted() {
        Log.i(TAG, "System booted, requesting ARC bridge proxy");
        mTaskThread = new HandlerThread("ArcAppTaskTracker");
        mTaskThread.start();
        mTaskHandler = new Handler(mTaskThread.getLooper());
        registerTaskStackListener();
        registerPackageReceiver();
        registerGamePackagesObserver();
        registerGameRenderHeightObserver();
        grantManageExternalStorageAppop();
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // Watch the user-flagged game-package list. When the user toggles "Is a
    // game" in Settings, re-push the whole app list so the updated appCategory
    // reaches Chrome's ArcAppListPrefs cache (which Game Controls reads). The
    // live getAppCategory() mojo already reflects the change immediately; this
    // keeps the cached path in sync too.
    private void registerGamePackagesObserver() {
        mContext.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(GAME_PKGS_SECURE_KEY), false,
                new ContentObserver(mTaskHandler) {
                    @Override
                    public void onChange(boolean selfChange) {
                        Log.i(TAG, "game-package flags changed, re-sending app list");
                        sendAppList();
                        refreshGameRenderScale();
                    }
                });
    }

    // Watch the user's "Game rendering quality" choice (Settings > Display).
    // Settings only writes it once the user confirms the change, and the sweep
    // is debounced on top of that, because applying a new step restarts every
    // running game.
    private void registerGameRenderHeightObserver() {
        mContext.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(GAME_RENDER_HEIGHT_SECURE_KEY), false,
                new ContentObserver(mTaskHandler) {
                    @Override
                    public void onChange(boolean selfChange) {
                        Log.i(TAG, "Game render height changed, re-sweeping");
                        mTaskHandler.removeCallbacks(mRefreshGameRenderScale);
                        mTaskHandler.postDelayed(mRefreshGameRenderScale,
                                GAME_RENDER_HEIGHT_DEBOUNCE_MS);
                    }
                });
    }

    // DocumentsUI's ArcVolumeProvider opens files under /storage/emulated/0/ on
    // behalf of PackageInstaller (and other consumers) when Chrome's Files app
    // hands off a content URI. The privapp-permissions XML grants the
    // MANAGE_EXTERNAL_STORAGE permission to it, but the corresponding appop
    // defaults to "default" (= deny) on first boot — so ParcelFileDescriptor.
    // open() fails with EACCES. Flip the appop to ALLOWED on every boot so the
    // file open succeeds. Idempotent and survives package reinstalls.
    private void grantManageExternalStorageAppop() {
        try {
            int uid = mContext.getPackageManager().getPackageUid(
                    "com.android.documentsui", 0);
            AppOpsManager appOps = mContext.getSystemService(AppOpsManager.class);
            appOps.setUidMode(AppOpsManager.OPSTR_MANAGE_EXTERNAL_STORAGE,
                    uid, AppOpsManager.MODE_ALLOWED);
            Log.i(TAG, "Granted MANAGE_EXTERNAL_STORAGE appop to "
                    + "com.android.documentsui (uid=" + uid + ")");
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "DocumentsUI not installed; skipping appop grant");
        } catch (Exception e) {
            Log.e(TAG, "Failed to grant MANAGE_EXTERNAL_STORAGE appop", e);
        }
    }

    private void registerTaskStackListener() {
        // Match the proprietary AppTaskTracker pattern: never trust ATMS's
        // raw onTaskCreated/onTaskRemoved callbacks (they fire for restored
        // phantom tasks at user-unlock time). Instead, every stack mutation
        // simply triggers a debounced refresh that diffs ATMS's authoritative
        // running task list against our previous snapshot.
        try {
            ActivityManager.getService().registerTaskStackListener(
                    new TaskStackListener() {
                        @Override
                        public void onTaskStackChanged() {
                            Log.v(TAG, "TaskStackListener.onTaskStackChanged");
                            scheduleRefresh();
                        }

                        @Override
                        public void onTaskRemoved(int taskId) {
                            Log.v(TAG, "TaskStackListener.onTaskRemoved("
                                    + taskId + ")");
                            scheduleRefresh();
                        }

                        @Override
                        public void onTaskMovedToFront(int taskId) {
                            Log.v(TAG, "TaskStackListener.onTaskMovedToFront("
                                    + taskId + ")");
                            scheduleRefresh();
                        }

                        @Override
                        public void onTaskDescriptionChanged(
                                ActivityManager.RunningTaskInfo taskInfo) {
                            // An activity called setTaskDescription(), so the
                            // task's label or theme colours may have changed.
                            // Diffed with everything else rather than forwarded
                            // straight through, for the reason above.
                            Log.v(TAG, "TaskStackListener.onTaskDescriptionChanged("
                                    + taskInfo.taskId + ")");
                            scheduleRefresh();
                        }
                    });
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register task stack listener", e);
        }

        // Backup trigger: ACTION_PACKAGE_RESTARTED fires on `am force-stop`
        // and similar events that finish all activities of a package without
        // necessarily firing onTaskRemoved. Without this, Chrome can keep a
        // stale "running" indicator for a force-stopped app.
        BroadcastReceiver forceStopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Uri data = intent.getData();
                String pkg = data == null ? null : data.getSchemeSpecificPart();
                Log.v(TAG, "ACTION_PACKAGE_RESTARTED pkg=" + pkg);
                scheduleRefresh();
            }
        };
        IntentFilter filter = new IntentFilter(
                Intent.ACTION_PACKAGE_RESTARTED);
        filter.addDataScheme("package");
        mContext.registerReceiver(forceStopReceiver, filter);
    }

    // --- Task tracking (diff-based, proprietary AppTaskTracker pattern) ---

    private static final String ARC_HOME_PACKAGE = "org.chromium.arc.home";
    /**
     * The IME placeholder is not an app and must never reach the shelf.
     *
     * It is the invisible 1x1 editor that lets an Android IME serve a Chrome OS
     * text field (see ArcInputMethodManagerService), and it holds a real
     * standard task for as long as that field is focused. Chrome renders that
     * task as a running app — with no name and no icon, since the activity has
     * no launcher entry — so selecting Gboard put a nameless item in the shelf.
     */
    private static final ComponentName PLACEHOLDER_COMPONENT = new ComponentName(
            "org.chromium.arc.applauncher",
            "org.chromium.arc.applauncher.PlaceholderActivity");
    // Packages that don't work in ARC++ and should be hidden from Chrome shelf.
    private static final java.util.Set<String> HIDDEN_PACKAGES = new java.util.HashSet<>(
            java.util.Arrays.asList(
                    "com.android.htmlviewer",
                    "com.android.documentsui"
            ));
    // Specific activities to hide from file handler list.
    private static final java.util.Set<String> HIDDEN_ACTIVITIES = new java.util.HashSet<>(
            java.util.Arrays.asList(
                    "com.android.settings/com.android.settings.Settings$ApnEditorActivity"
            ));

    private void scheduleRefresh() {
        if (mTaskHandler == null) return;
        mTaskHandler.removeCallbacks(mRefreshTasksRunnable);
        mTaskHandler.post(mRefreshTasksRunnable);
    }

    /**
     * Snapshot ATMS's running tasks and diff against the previous snapshot,
     * firing onTaskCreated / onTaskDestroyed / onTaskSetActive to Chrome.
     *
     * IActivityManager.getTasks() filters to tasks whose top activity is
     * non-finishing — phantom tasks restored from disk (no activity) never
     * appear here, so Chrome never sees a phantom shelf-running indicator.
     */
    private void refreshTasks() {
        AppHost host = mHost;
        if (host == null) {
            Log.v(TAG, "refreshTasks: skipped (no AppHost)");
            return;
        }

        SparseArray<TaskSnapshot> currentTasks = new SparseArray<>();
        int activeTaskId = -1;
        try {
            List<ActivityManager.RunningTaskInfo> tasks =
                    ActivityManager.getService().getTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo info : tasks) {
                ComponentName component = info.origActivity != null
                        ? info.origActivity : info.realActivity;
                if (component == null) continue;
                if (ARC_HOME_PACKAGE.equals(component.getPackageName())) {
                    continue;
                }
                if (PLACEHOLDER_COMPONENT.equals(component)) {
                    continue;
                }
                int sessionId = info.baseIntent != null
                        ? parseWindowSessionId(info.baseIntent.getIdentifier())
                        : 0;
                ActivityManager.TaskDescription description = info.taskDescription;
                currentTasks.put(info.taskId, new TaskSnapshot(component, sessionId,
                        description != null ? description.getLabel() : null,
                        description != null ? description.getPrimaryColor() : 0,
                        description != null ? description.getStatusBarColor() : 0));
                if (activeTaskId == -1) activeTaskId = info.taskId;
            }
        } catch (Exception e) {
            Log.e(TAG, "refreshTasks: getTasks failed", e);
            return;
        }
        Log.v(TAG, "refreshTasks: prev=" + mPreviousTasks.size()
                + " curr=" + currentTasks.size()
                + " active=" + activeTaskId);

        // New tasks → onTaskCreated
        for (int i = 0; i < currentTasks.size(); i++) {
            int taskId = currentTasks.keyAt(i);
            if (mPreviousTasks.indexOfKey(taskId) >= 0) continue;
            TaskSnapshot snap = currentTasks.valueAt(i);
            try {
                Log.i(TAG, "onTaskCreated taskId=" + taskId
                        + " pkg=" + snap.component.getPackageName()
                        + " sessionId=" + snap.sessionId);
                host.onTaskCreated(taskId,
                        snap.component.getPackageName(),
                        snap.component.getClassName(),
                        "" /* name */, "" /* intent */, snap.sessionId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to notify host of task creation", e);
            }
            // Strictly after onTaskCreated: Chrome drops a description for a
            // task it has not been told about yet.
            pushTaskDescription(host, taskId, snap);
        }

        // Label or theme colour changed on a task Chrome already knows about
        // (a new top activity, or the app calling setTaskDescription()).
        for (int i = 0; i < currentTasks.size(); i++) {
            int taskId = currentTasks.keyAt(i);
            TaskSnapshot previous = mPreviousTasks.get(taskId);
            if (previous == null) continue;  // handled by the loop above
            TaskSnapshot snap = currentTasks.valueAt(i);
            if (snap.sameDescriptionAs(previous)) continue;
            pushTaskDescription(host, taskId, snap);
        }

        // Removed tasks → onTaskDestroyed
        for (int i = 0; i < mPreviousTasks.size(); i++) {
            int taskId = mPreviousTasks.keyAt(i);
            if (currentTasks.indexOfKey(taskId) >= 0) continue;
            try {
                Log.i(TAG, "onTaskDestroyed taskId=" + taskId);
                removeSessionIdFromNative(taskId);
                host.onTaskDestroyed(taskId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to notify host of task destruction", e);
            }
        }

        // Active task changed → onTaskSetActive
        if (activeTaskId != -1 && activeTaskId != mLastActiveTaskId) {
            mLastActiveTaskId = activeTaskId;
            try {
                Log.d(TAG, "onTaskSetActive taskId=" + activeTaskId);
                host.onTaskSetActive(activeTaskId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to notify host of task activation", e);
            }
        }

        mPreviousTasks = currentTasks;
    }

    /**
     * Tell Chrome what to call this window and which icon to give it.
     *
     * Chrome puts a title on an ARC window only here — OnTaskDescriptionChanged
     * is the single caller of ArcAppWindow::SetDescription(), which is the
     * single caller of aura::Window::SetTitle() for ARC. It also throws the
     * whole update away when the icon is empty, taking the label with it, so an
     * icon is not optional.
     */
    private void pushTaskDescription(AppHost host, int taskId, TaskSnapshot snap) {
        try {
            String label = resolveTaskLabel(snap);
            RawIconPngData icon = loadAppIcon(snap.component.getPackageName(),
                    snap.component.getClassName(), TASK_DESCRIPTION_ICON_SIZE);
            if (icon.iconPngData == null || icon.iconPngData.length == 0) {
                // Rather than lose the label with it, fall back to the
                // platform's default app icon.
                icon.isAdaptiveIcon = false;
                icon.foregroundIconPngData = null;
                icon.backgroundIconPngData = null;
                icon.iconPngData = drawableToPng(
                        mContext.getPackageManager().getDefaultActivityIcon(),
                        TASK_DESCRIPTION_ICON_SIZE);
                if (icon.iconPngData == null) icon.iconPngData = new byte[0];
            }
            Log.i(TAG, "onTaskDescriptionChanged taskId=" + taskId
                    + " label=" + label);
            host.onTaskDescriptionChanged(taskId, label, icon,
                    snap.primaryColor, snap.statusBarColor);
        } catch (Exception e) {
            Log.e(TAG, "Failed to notify host of task description", e);
        }
    }

    /**
     * The name a user would recognise this task by: what the app set through
     * setTaskDescription() if anything, else the activity's label, else the
     * application's, else the package name.
     */
    private String resolveTaskLabel(TaskSnapshot snap) {
        if (snap.descriptionLabel != null && !snap.descriptionLabel.isEmpty()) {
            return snap.descriptionLabel;
        }
        ComponentName component = snap.component;
        PackageManager pm = mContext.getPackageManager();
        try {
            CharSequence label = pm.getActivityInfo(component, 0).loadLabel(pm);
            if (label != null && label.length() > 0) return label.toString();
        } catch (PackageManager.NameNotFoundException e) {
            // Fall through to the application label.
        }
        try {
            CharSequence label = pm.getApplicationInfo(
                    component.getPackageName(), 0).loadLabel(pm);
            if (label != null && label.length() > 0) return label.toString();
        } catch (PackageManager.NameNotFoundException e) {
            // Nothing better than the package name.
        }
        return component.getPackageName();
    }

    private static int parseWindowSessionId(String identifier) {
        if (identifier == null) return 0;
        final String prefix = "window_session_id=";
        int idx = identifier.indexOf(prefix);
        if (idx < 0) return 0;
        int start = idx + prefix.length();
        int end = start;
        while (end < identifier.length()
                && Character.isDigit(identifier.charAt(end))) {
            end++;
        }
        if (end == start) return 0;
        try {
            return Integer.parseInt(identifier.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // --- Package broadcast receiver ---

    private void registerPackageReceiver() {
        mPackageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Uri data = intent.getData();
                if (data == null) return;
                String packageName = data.getSchemeSpecificPart();
                if (packageName == null) return;

                AppHost host = mHost;
                if (host == null) return;

                try {
                    if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                        handlePackageAdded(host, packageName);
                    } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                        if (!intent.getBooleanExtra(
                                Intent.EXTRA_REPLACING, false)) {
                            cancelLauncherAppsRefresh(packageName);
                            synchronized (mPublishedLauncherActivities) {
                                mPublishedLauncherActivities.remove(packageName);
                            }
                            host.onPackageRemoved(packageName);
                            Log.i(TAG, "onPackageRemoved: " + packageName);
                        }
                    } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                            || Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                        handlePackageModified(host, packageName);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to handle package event: "
                            + action + " " + packageName, e);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        mContext.registerReceiver(mPackageReceiver, filter);
        Log.i(TAG, "Package broadcast receiver registered");
    }

    /**
     * Re-publish a package's launcher entries once its components stop moving.
     *
     * Every publish is posted to the task thread, so the immediate one an
     * install makes and the debounced one a component change makes cannot
     * overtake each other.
     */
    private void scheduleLauncherAppsRefresh(final String packageName,
            long delayMs) {
        final Handler handler = mTaskHandler;
        if (handler == null) {
            // Before onSystemBooted() there is no task thread — and the package
            // receiver is registered there too, so this should not happen.
            AppHost host = mHost;
            if (host != null) publishLauncherApps(host, packageName);
            return;
        }
        synchronized (mPendingLauncherRefresh) {
            Runnable pending = mPendingLauncherRefresh.remove(packageName);
            if (pending != null) handler.removeCallbacks(pending);
            Runnable refresh = new Runnable() {
                @Override
                public void run() {
                    synchronized (mPendingLauncherRefresh) {
                        mPendingLauncherRefresh.remove(packageName);
                    }
                    AppHost host = mHost;
                    if (host != null) publishLauncherApps(host, packageName);
                }
            };
            mPendingLauncherRefresh.put(packageName, refresh);
            handler.postDelayed(refresh, delayMs);
        }
    }

    private void cancelLauncherAppsRefresh(String packageName) {
        final Handler handler = mTaskHandler;
        synchronized (mPendingLauncherRefresh) {
            Runnable pending = mPendingLauncherRefresh.remove(packageName);
            if (pending != null && handler != null) {
                handler.removeCallbacks(pending);
            }
        }
    }

    /**
     * Publish this package's launcher entries to Chrome, when they have moved.
     *
     * An Android app changes its launcher icon at runtime by enabling one
     * &lt;activity-alias&gt; of its launch activity and disabling another —
     * Duolingo swaps <code>.app.LoginActivity</code> for
     * <code>.app.StreakSocietyLauncher</code>, two aliases of the same
     * LaunchActivity carrying different android:icon, when the user joins the
     * Streak Society. Chrome keys an ARC app — its launcher tile, its shelf
     * item and its on-disk icon cache — on package name + activity name, so
     * that swap is a *different* app to Chrome rather than the same app with a
     * new icon, and OnPackageAppListRefreshed is the only call that replaces
     * one with the other.
     *
     * OnPackageModified alone is not enough: ArcAppListPrefs drops it unless
     * package_version also changed, which an icon swap never does — leaving
     * the tile showing the old icon and pointing at a component that is now
     * disabled.
     */
    private void publishLauncherApps(AppHost host, String packageName) {
        if (ARC_HOME_PACKAGE.equals(packageName)
                || HIDDEN_PACKAGES.contains(packageName)) {
            return;
        }
        AppInfo[] apps = getAppInfosForPackage(packageName);
        Set<String> activities = new HashSet<>();
        for (AppInfo app : apps) {
            activities.add(app.activity);
        }
        synchronized (mPublishedLauncherActivities) {
            Set<String> published =
                    mPublishedLauncherActivities.get(packageName);
            if (activities.equals(published == null
                    ? Collections.emptySet() : published)) {
                return;
            }
            mPublishedLauncherActivities.put(packageName, activities);
        }
        // An empty list is meaningful and safe: Chrome only drops the
        // launchable apps of this one package, which is exactly right for an
        // app that has just hidden its launcher entry.
        host.onPackageAppListRefreshed(packageName, apps);
        Log.i(TAG, "onPackageAppListRefreshed: " + packageName + " -> "
                + activities);
    }

    private void handlePackageAdded(AppHost host, String packageName) {
        try {
            ArcPackageInfo pkgInfo = buildPackageInfo(packageName);
            if (pkgInfo != null) {
                host.onPackageAdded(pkgInfo);
            }
            scheduleLauncherAppsRefresh(packageName, 0);
            Log.i(TAG, "onPackageAdded: " + packageName);
            // A newly installed game must be downscaled before its first
            // launch, otherwise it renders at full resolution until something
            // else re-triggers the sweep.
            refreshGameRenderScaleFor(packageName);
        } catch (Exception e) {
            Log.e(TAG, "handlePackageAdded failed: " + packageName, e);
        }
    }

    private void handlePackageModified(AppHost host, String packageName) {
        try {
            ArcPackageInfo pkgInfo = buildPackageInfo(packageName);
            if (pkgInfo != null) {
                host.onPackageModified(pkgInfo);
            }
            Log.i(TAG, "onPackageModified: " + packageName);
            // A package whose components changed may have swapped its launcher
            // entry — that is how an app changes its icon at runtime.
            scheduleLauncherAppsRefresh(packageName, LAUNCHER_REFRESH_SETTLE_MS);
            // An update can change ApplicationInfo.category, so re-evaluate.
            refreshGameRenderScaleFor(packageName);
        } catch (Exception e) {
            Log.e(TAG, "handlePackageModified failed: " + packageName, e);
        }
    }

    // --- App list enumeration ---

    private void sendAppList() {
        AppHost host = mHost;
        if (host == null) return;
        // Chrome treats an app-list refresh as authoritative and uninstalls
        // every app missing from it, destroying launcher folder placement.
        // Before user 0 unlock, queryIntentActivities() only returns
        // direct-boot-aware apps, so never publish a snapshot while locked;
        // onUserUnlocked() sends the first complete list.
        if (!mContext.getSystemService(UserManager.class)
                .isUserUnlocked(UserHandle.SYSTEM)) {
            Log.i(TAG, "sendAppList skipped: user 0 still locked");
            return;
        }

        try {
            PackageManager pm = mContext.getPackageManager();

            // Get all launchable activities
            Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos =
                    pm.queryIntentActivities(launcherIntent, 0);

            ArrayList<AppInfo> appInfos = new ArrayList<>();
            java.util.HashSet<String> seenActivities = new java.util.HashSet<>();
            java.util.HashSet<String> seenPackages = new java.util.HashSet<>();
            Map<String, Set<String>> launcherActivities = new HashMap<>();
            for (ResolveInfo ri : resolveInfos) {
                if (ARC_HOME_PACKAGE.equals(ri.activityInfo.packageName)
                        || HIDDEN_PACKAGES.contains(ri.activityInfo.packageName)) {
                    continue;
                }
                appInfos.add(buildAppInfo(ri));
                seenActivities.add(ri.activityInfo.packageName
                        + "/" + ri.activityInfo.name);
                seenPackages.add(ri.activityInfo.packageName);
                launcherActivities
                        .computeIfAbsent(ri.activityInfo.packageName,
                                k -> new HashSet<>())
                        .add(ri.activityInfo.name);
            }
            // This sweep is what Chrome now holds, so it is also the baseline
            // publishLauncherApps() diffs later package changes against.
            synchronized (mPublishedLauncherActivities) {
                mPublishedLauncherActivities.clear();
                mPublishedLauncherActivities.putAll(launcherActivities);
            }

            // Also include non-launcher apps that handle file-type intents
            // (e.g. packageinstaller for APK files). Chrome needs these in
            // the app list to attach intent filters as file handlers.
            // Dedupe by package: a launchable app like WPS Office declares
            // many file-handler activities (doc2pdf, pdf_tools, ...) — those
            // belong on the launcher entry's intent filters, not as separate
            // apps. Only packages without a launcher activity (e.g.
            // packageinstaller) need a fresh entry from the file-handler
            // pass.
            for (PackageInfo pi : pm.getInstalledPackages(0)) {
                if (HIDDEN_PACKAGES.contains(pi.packageName)) continue;
                if (seenPackages.contains(pi.packageName)) continue;
                for (android.content.IntentFilter f
                        : pm.getAllIntentFilters(pi.packageName)) {
                    if (f.hasAction(Intent.ACTION_VIEW)
                            && f.countDataTypes() > 0
                            // vnd.android.cursor.* are ContentProvider row
                            // types (contact actions etc.), never files.
                            && !f.getDataType(0).startsWith("vnd.android.cursor.")
                            && f.countDataPaths() == 0
                            && f.countDataAuthorities() == 0) {
                        // This is a file-type handler; find its activity
                        addFileHandlerApps(pm, pi.packageName, f,
                                seenActivities, seenPackages, appInfos);
                    }
                }
            }

            AppInfo[] appArray = appInfos.toArray(new AppInfo[0]);
            host.onAppListRefreshed(appArray);
            Log.i(TAG, "onAppListRefreshed: " + appArray.length + " apps");

            // Get all packages
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            ArrayList<ArcPackageInfo> pkgInfos = new ArrayList<>();
            for (PackageInfo pi : packages) {
                ArcPackageInfo info = buildPackageInfoFromPkgInfo(pi);
                if (info != null) {
                    pkgInfos.add(info);
                }
            }
            ArcPackageInfo[] pkgArray =
                    pkgInfos.toArray(new ArcPackageInfo[0]);
            host.onPackageListRefreshed(pkgArray);
            Log.i(TAG, "onPackageListRefreshed: " + pkgArray.length
                    + " packages");
        } catch (Exception e) {
            Log.e(TAG, "Failed to send app list", e);
        }
    }

    /**
     * For a file-type intent filter, find the activity that handles it and
     * add it to the app list if not already present.
     */
    private void addFileHandlerApps(PackageManager pm, String packageName,
            android.content.IntentFilter f,
            java.util.HashSet<String> seenActivities,
            java.util.HashSet<String> seenPackages,
            ArrayList<AppInfo> appInfos) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(packageName);
        String mimeType = f.getDataType(0).replaceAll("\\*", "anytype");
        if (!mimeType.contains("/")) mimeType += "/anytype";
        if (f.countDataSchemes() > 0) {
            intent.setDataAndType(
                    Uri.parse(f.getDataScheme(0) + "://"), mimeType);
        } else {
            intent.setType(mimeType);
        }
        for (ResolveInfo ri : pm.queryIntentActivities(intent, 0)) {
            String pkg = ri.activityInfo.packageName;
            if (seenPackages.contains(pkg)) continue;
            String key = pkg + "/" + ri.activityInfo.name;
            if (!seenActivities.contains(key) && !HIDDEN_ACTIVITIES.contains(key)) {
                seenActivities.add(key);
                seenPackages.add(pkg);
                AppInfo info = buildAppInfo(ri);
                appInfos.add(info);
                Log.i(TAG, "Added file handler app: " + key);
            }
        }
    }

    // --- Category helpers ---

    private Set<String> userGamePackages() {
        String csv = Settings.Secure.getString(
                mContext.getContentResolver(), GAME_PKGS_SECURE_KEY);
        if (csv == null || csv.isEmpty()) return Collections.emptySet();
        Set<String> set = new HashSet<>();
        for (String p : csv.split(":")) {
            if (!p.isEmpty()) set.add(p);
        }
        return set;
    }

    // Returns CATEGORY_GAME for user-flagged games, otherwise the PM-reported
    // category. Applied to every path Chrome reads app category from.
    private int resolveAppCategory(String packageName, int rawCategory) {
        return userGamePackages().contains(packageName)
                ? ApplicationInfo.CATEGORY_GAME : rawCategory;
    }

    // --- Game render downscaling ---

    // Height games rasterise at, in pixels along the panel's short side.
    // 0 means "render at full resolution".
    private int gameRenderHeight() {
        int height = Settings.Secure.getInt(mContext.getContentResolver(),
                GAME_RENDER_HEIGHT_SECURE_KEY, -1);
        if (height < 0) {
            height = SystemProperties.getInt(GAME_RENDER_HEIGHT_PROP_PERSIST, -1);
        }
        if (height < 0) {
            height = SystemProperties.getInt(GAME_RENDER_HEIGHT_PROP_RO,
                    DEFAULT_GAME_RENDER_HEIGHT);
        }
        return height;
    }

    // Short side of the default display in pixels, or 0 if it is not known.
    //
    // Read from DisplayManagerInternal, not DisplayManager.getDisplay(). The
    // public API goes through DisplayManagerGlobal's PropertyInvalidatedCache,
    // whose invalidation is a system-property nonce; read from onDisplayChanged()
    // it still returned the display as it was before the change (on device every
    // sweep saw the previous resolution), so a resolution switch looked like
    // no change at all. The internal service hands back the live LogicalDisplay.
    private int defaultDisplayShortSide() {
        DisplayManagerInternal dmi = LocalServices.getService(DisplayManagerInternal.class);
        DisplayInfo info = dmi != null ? dmi.getDisplayInfo(Display.DEFAULT_DISPLAY) : null;
        if (info == null) return 0;
        return Math.min(info.logicalWidth, info.logicalHeight);
    }

    // The DOWNSCALE step that renders targetHeight on a panel whose short side
    // is shortSide, or 0 for native resolution. It is the smallest step that
    // still renders at least the target, so a game never drops below it:
    // 1080 -> 70% (756p), 1600 -> 45% (720p), 1824 -> 40% (730p). A panel
    // close enough to the target that even 90% would undershoot it (768p) is
    // left native, and 30% is the floor on a panel so large it overshoots.
    private static int scalePercentFor(int shortSide, int targetHeight) {
        if (targetHeight <= 0) return 0;
        int pct = 0;
        for (int step : DOWNSCALE_CHANGE_BY_PCT.keySet()) {
            if ((long) shortSide * step < (long) targetHeight * 100) break;
            pct = step;
        }
        return pct;
    }

    // Percentage for the current panel, or -1 while its size is unknown —
    // callers then leave every package alone rather than clearing downscaling
    // on the strength of a missing display.
    private int gameRenderScalePercent() {
        final int shortSide = defaultDisplayShortSide();
        return shortSide > 0 ? scalePercentFor(shortSide, gameRenderHeight()) : -1;
    }

    private boolean isGamePackage(ApplicationInfo ai) {
        return resolveAppCategory(ai.packageName, ai.category)
                == ApplicationInfo.CATEGORY_GAME;
    }

    // Re-evaluate every installed app. Runs at boot, whenever the user
    // flags/unflags a game, and when the panel size moves the step. Cheap in
    // the steady state: nothing is written for a package whose effective state
    // already matches, so the usual sweep makes zero PlatformCompat writes.
    private void refreshGameRenderScale() {
        final int shortSide = defaultDisplayShortSide();
        if (shortSide <= 0) {
            Log.w(TAG, "Default display size unknown; skipping render scale");
            return;
        }
        final int height = gameRenderHeight();
        final int pct = scalePercentFor(shortSide, height);
        final Long target = DOWNSCALE_CHANGE_BY_PCT.get(pct);
        Log.i(TAG, "Game render scale " + (pct == 0 ? "native" : pct + "%")
                + ": " + height + "p target on a " + shortSide + "p panel");
        PlatformCompat compat = platformCompat();
        if (compat == null) return;
        try {
            List<ApplicationInfo> apps =
                    mContext.getPackageManager().getInstalledApplications(0);
            for (ApplicationInfo ai : apps) {
                applyGameRenderScale(compat, ai.packageName, isGamePackage(ai),
                        pct, target);
            }
            mGameRenderScalePct = pct;
        } catch (Exception e) {
            Log.e(TAG, "Failed to refresh game render scale", e);
        }
    }

    private void refreshGameRenderScaleFor(String packageName) {
        final int pct = gameRenderScalePercent();
        if (pct < 0) return;
        final Long target = DOWNSCALE_CHANGE_BY_PCT.get(pct);
        PlatformCompat compat = platformCompat();
        if (compat == null) return;
        try {
            ApplicationInfo ai = mContext.getPackageManager()
                    .getApplicationInfo(packageName, 0);
            applyGameRenderScale(compat, packageName, isGamePackage(ai), pct,
                    target);
        } catch (PackageManager.NameNotFoundException e) {
            // Uninstalled between the broadcast and here; nothing to do.
        } catch (Exception e) {
            Log.e(TAG, "Failed to refresh render scale for " + packageName, e);
        }
    }

    private PlatformCompat platformCompat() {
        PlatformCompat compat = (PlatformCompat)
                ServiceManager.getService(Context.PLATFORM_COMPAT_SERVICE);
        if (compat == null) {
            Log.w(TAG, "platform_compat unavailable; skipping render scale");
        }
        return compat;
    }

    // setOverrides() kills the package, which is what we want (the buffer size
    // is fixed when the app's window is created, so a running app must restart
    // to pick the change up) but also why this must be a no-op when the state
    // is already right: an unconditional write would kill every game on every
    // boot and on every unrelated game-flag edit.
    private void applyGameRenderScale(PlatformCompat compat, String packageName,
            boolean isGame, int pct, Long target) {
        final boolean want = isGame && pct != 0 && target != null;
        final int userId = UserHandle.myUserId();
        try {
            boolean gateOn = compat.isChangeEnabledByPackageName(
                    CHANGE_DOWNSCALED, packageName, userId);
            boolean scaleOn = want && compat.isChangeEnabledByPackageName(
                    target, packageName, userId);
            if (gateOn == want && (!want || scaleOn)) return;

            Set<Long> enabled = new HashSet<>();
            Set<Long> disabled = new HashSet<>();
            if (want) {
                enabled.add(CHANGE_DOWNSCALED);
                enabled.add(target);
            } else {
                disabled.add(CHANGE_DOWNSCALED);
            }
            // Clear every other step explicitly. Retuning a board from 60% to
            // 50% otherwise leaves both changes enabled, and WM applies the
            // first one it finds rather than the one we just asked for.
            for (Map.Entry<Integer, Long> e
                    : DOWNSCALE_CHANGE_BY_PCT.entrySet()) {
                if (!want || !e.getValue().equals(target)) {
                    disabled.add(e.getValue());
                }
            }
            compat.setOverrides(new CompatibilityChangeConfig(
                    new Compatibility.ChangeConfig(enabled, disabled)),
                    packageName);
            Log.i(TAG, want
                    ? "Render downscaling set to " + pct + "% for " + packageName
                    : "Render downscaling cleared for " + packageName);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set render scale for " + packageName, e);
        }
    }

    // --- Build helpers ---

    private AppInfo buildAppInfo(ResolveInfo ri) {
        AppInfo info = new AppInfo();
        info.name = ri.loadLabel(mContext.getPackageManager()).toString();
        info.packageName = ri.activityInfo.packageName;
        info.activity = ri.activityInfo.name;
        info.sticky = false;
        info.notificationsEnabled = true;
        info.suspended = false;
        info.needFixup = false;
        info.resizeLockState = 0; // UNDEFINED
        int rawCategory = ri.activityInfo.applicationInfo != null
                ? ri.activityInfo.applicationInfo.category : -1;
        info.appCategory = resolveAppCategory(info.packageName, rawCategory);
        return info;
    }

    private ArcPackageInfo buildPackageInfo(String packageName) {
        try {
            PackageInfo pi = mContext.getPackageManager()
                    .getPackageInfo(packageName, 0);
            return buildPackageInfoFromPkgInfo(pi);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private ArcPackageInfo buildPackageInfoFromPkgInfo(PackageInfo pi) {
        ArcPackageInfo info = new ArcPackageInfo();
        info.packageName = pi.packageName;
        info.packageVersion = pi.versionCode;
        info.lastBackupAndroidId = 0;
        info.lastBackupTime = 0;
        info.sync = false;
        info.preinstalled = (pi.applicationInfo != null
                && (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        info.versionName = pi.versionName;
        return info;
    }

    private AppInfo[] getAppInfosForPackage(String packageName) {
        PackageManager pm = mContext.getPackageManager();
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launcherIntent.setPackage(packageName);
        List<ResolveInfo> resolveInfos =
                pm.queryIntentActivities(launcherIntent, 0);

        ArrayList<AppInfo> result = new ArrayList<>();
        for (ResolveInfo ri : resolveInfos) {
            result.add(buildAppInfo(ri));
        }
        return result.toArray(new AppInfo[0]);
    }

    // --- Icon helpers ---

    private static byte[] drawableToPng(Drawable drawable, int size) {
        if (drawable == null) return null;
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && (bitmap.getWidth() != size || bitmap.getHeight() != size)) {
                bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
            }
        } else {
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, size, size);
            drawable.draw(canvas);
        }
        if (bitmap == null) return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    private RawIconPngData loadAppIcon(String packageName, String activityName, int size) {
        PackageManager pm = mContext.getPackageManager();
        RawIconPngData result = new RawIconPngData();

        try {
            Drawable icon = null;
            // Try activity-specific icon first
            if (activityName != null && !activityName.isEmpty()) {
                try {
                    ComponentName comp = new ComponentName(packageName, activityName);
                    ActivityInfo ai = pm.getActivityInfo(comp, 0);
                    icon = ai.loadIcon(pm);
                } catch (PackageManager.NameNotFoundException e) {
                    // Fall through to package icon
                }
            }
            // Fall back to package icon
            if (icon == null) {
                icon = pm.getApplicationIcon(packageName);
            }

            if (icon instanceof AdaptiveIconDrawable) {
                AdaptiveIconDrawable adaptive = (AdaptiveIconDrawable) icon;
                result.isAdaptiveIcon = true;
                result.foregroundIconPngData = drawableToPng(adaptive.getForeground(), size);
                result.backgroundIconPngData = drawableToPng(adaptive.getBackground(), size);
                result.iconPngData = drawableToPng(icon, size);
            } else {
                result.isAdaptiveIcon = false;
                result.iconPngData = drawableToPng(icon, size);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Icon not found for " + packageName);
            result.iconPngData = new byte[0];
        }

        if (result.iconPngData == null) result.iconPngData = new byte[0];
        return result;
    }

    private RawIconPngData loadPackageIcon(String packageName, int size) {
        return loadAppIcon(packageName, null, size);
    }

    // --- Helpers ---

    private void pushSessionIdToNative(int taskId, int sessionId) {
        try {
            IBinder wayland = ServiceManager.getService("Wayland");
            if (wayland != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(
                            "org.chromium.arc.wayland.IWaylandService");
                    data.writeInt(taskId);
                    data.writeInt(sessionId);
                    wayland.transact(41, data, reply, 0);
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to push session ID to native", e);
        }
    }

    private void removeSessionIdFromNative(int taskId) {
        // Push sessionId=0 to signal removal
        pushSessionIdToNative(taskId, 0);
    }

    // --- AppInstance (Mojo from Chrome) ---

    @Override
    public void init(AppHost host, InitResponse response) {
        Log.i(TAG, "init: Chrome connected");
        mHost = host;
        response.call();
        // Reset diff state so refresh sends a full snapshot to the new host.
        mTaskHandler.post(() -> {
            mPreviousTasks = new SparseArray<>();
            mLastActiveTaskId = -1;
            refreshTasks();
        });
        sendAppList();
    }

    @Override
    public void closeTask(int taskId) {
        Log.i(TAG, "closeTask taskId=" + taskId);
        try {
            ActivityManager.getService().removeTask(taskId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to close task " + taskId, e);
        }
    }

    @Override
    public void setTaskActive(int taskId) {
        Log.i(TAG, "setTaskActive taskId=" + taskId);
        try {
            ActivityManager am = (ActivityManager)
                    mContext.getSystemService(Context.ACTIVITY_SERVICE);
            am.moveTaskToFront(taskId, 0);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set task active " + taskId, e);
        }
    }

    // --- AppInstance stubs (not yet needed) ---

    @Override
    public void getAndroidId(GetAndroidIdResponse response) {
        response.call(0L);
    }

    @Override
    public void getAppCategory(String packageName,
            GetAppCategoryResponse response) {
        try {
            ApplicationInfo ai = mContext.getPackageManager()
                    .getApplicationInfo(packageName, 0);
            response.call(resolveAppCategory(packageName, ai.category));
        } catch (PackageManager.NameNotFoundException e) {
            response.call(-1);  // AppCategory.UNDEFINED
        }
    }

    @Override
    public void getAppIcon(String packageName, String activityName,
            int size, GetAppIconResponse response) {
        if (size <= 0) size = 48;
        response.call(loadAppIcon(packageName, activityName, size));
    }

    @Override
    public void getAppShortcutGlobalQueryItems(String query, int maxResults,
            GetAppShortcutGlobalQueryItemsResponse response) {
        response.call(new AppShortcutItem[0]);
    }

    @Override
    public void getAppShortcutIcon(String packageName, int maxSize,
            GetAppShortcutIconResponse response) {
        // Shortcut icons: fall back to package icon for now
        if (maxSize <= 0) maxSize = 48;
        response.call(loadPackageIcon(packageName, maxSize));
    }

    @Override
    public void getAppShortcutItems(String packageName,
            GetAppShortcutItemsResponse response) {
        response.call(new AppShortcutItem[0]);
    }

    @Override
    public void getPackageIcon(String packageName, int size,
            boolean normalize, GetPackageIconResponse response) {
        if (size <= 0) size = 48;
        response.call(loadPackageIcon(packageName, size));
    }

    @Override
    public void getRecentAndSuggestedAppsFromPlayStore(String query,
            int maxResults,
            GetRecentAndSuggestedAppsFromPlayStoreResponse response) {
        response.call(0, new AppDiscoveryResult[0]);
    }

    @Override
    public void installPackage(ArcPackageInfo packageInfo) {
        Log.d(TAG, "installPackage: stub");
    }

    @Override
    public void isInstallable(String packageName,
            IsInstallableResponse response) {
        response.call(false);
    }

    @Override
    public void launchAppShortcutItem(String packageName, String shortcutId,
            long displayId) {
        Log.d(TAG, "launchAppShortcutItem: stub");
    }

    @Override
    public void launchAppWithWindowInfo(String packageName,
            String activityName, WindowInfo windowInfo) {
        Log.i(TAG, "launchAppWithWindowInfo: " + packageName
                + "/" + activityName + " windowId="
                + (windowInfo != null ? windowInfo.windowId : -1));
        try {
            Intent intent;
            if (activityName != null && !activityName.isEmpty()) {
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(
                        new ComponentName(packageName, activityName));
            } else {
                intent = mContext.getPackageManager()
                        .getLaunchIntentForPackage(packageName);
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityWithWindowInfo(intent, windowInfo);
            } else {
                Log.w(TAG, "No launch intent for " + packageName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch " + packageName, e);
        }
    }

    @Override
    public void launchIntentWithWindowInfo(String intentUri,
            WindowInfo windowInfo) {
        Log.i(TAG, "launchIntentWithWindowInfo: " + intentUri
                + " windowId="
                + (windowInfo != null ? windowInfo.windowId : -1));
        try {
            Intent intent = Intent.parseUri(intentUri, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityWithWindowInfo(intent, windowInfo);
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch intent: " + intentUri, e);
        }
    }

    /**
     * Launch an Activity carrying the Chrome-supplied WindowInfo.
     *
     * The ghost-window id is plumbed through the intent identifier so that
     * OnTaskCreated can echo it back to Chrome as session_id, letting Chrome
     * replace the ghost window with the real Android task window. Without
     * this, Chrome's ghost window (and its shelf "running" indicator) stays
     * attached to the ghost even after the real window closes.
     */
    private void startActivityWithWindowInfo(Intent intent,
            WindowInfo windowInfo) {
        if (windowInfo != null && windowInfo.windowId > 0) {
            intent.setIdentifier("window_session_id=" + windowInfo.windowId);
        }
        mContext.startActivity(intent);
    }

    @Override
    public void removeCachedIcon(String packageName) {
    }

    @Override
    public void requestAppIcon(String packageName, String activityName,
            int size, RequestAppIconResponse response) {
        if (size <= 0) size = 48;
        RawIconPngData icon = loadAppIcon(packageName, activityName, size);
        response.call(icon.iconPngData != null ? icon.iconPngData : new byte[0]);
    }

    @Override
    public void requestPackageIcon(String packageName, int size,
            boolean normalize, RequestPackageIconResponse response) {
        if (size <= 0) size = 48;
        RawIconPngData icon = loadPackageIcon(packageName, size);
        response.call(icon.iconPngData != null ? icon.iconPngData : new byte[0]);
    }

    @Override
    public void requestShortcutIcon(String packageName, int size,
            RequestShortcutIconResponse response) {
        if (size <= 0) size = 48;
        RawIconPngData icon = loadPackageIcon(packageName, size);
        response.call(icon.iconPngData != null ? icon.iconPngData : new byte[0]);
    }

    @Override
    public void setAppLocale(String packageName, String locale) {
    }

    @Override
    public void setNotificationsEnabled(String packageName, boolean enabled) {
    }

    @Override
    public void showPackageInfoDeprecated(String packageName, Rect bounds) {
        showPackageInfoPage(packageName, SHOW_PACKAGE_INFO_MAIN);
    }

    @Override
    public void showPackageInfoOnPage(String packageName, int page,
            long displayId) {
        showPackageInfoPage(packageName, page);
    }

    @Override
    public void showPackageInfoOnPageDeprecated(String packageName, int page,
            Rect bounds) {
        showPackageInfoPage(packageName, page);
    }

    /**
     * Open the Android settings page Chrome's app-management UI asked for.
     *
     * ChromeOS Settings > Apps > <app> ends its permission card with a row
     * that hands the app back to Android: AppService's OpenNativeSettings,
     * which arrives here. On T that row is "Manage permissions" and the page
     * is MANAGE_PERMISSIONS, because Chrome's own permission toggles are
     * read-only from T on (arc::IsReadOnlyPermissionsEnabled() is SDK >= T),
     * so granting and revoking has to happen in Android's own permission UI.
     * Before T the same row is "More settings and permissions" and asks for
     * MAIN, the App info page.
     *
     * The page is launched on the default display: Chrome only ever passes the
     * primary display id here, and unlike the app-launch path this call
     * carries no WindowInfo to place a window with.
     */
    private void showPackageInfoPage(String packageName, int page) {
        Log.i(TAG, "showPackageInfo: " + packageName + " page=" + page);
        if (packageName == null || packageName.isEmpty()) {
            return;
        }
        Intent intent = null;
        switch (page) {
            case SHOW_PACKAGE_INFO_MANAGE_PERMISSIONS:
                // PermissionController's per-app permission list. Takes the
                // package in an extra rather than the "package:" data URI the
                // Settings actions below use.
                intent = new Intent(Intent.ACTION_MANAGE_APP_PERMISSIONS)
                        .putExtra(Intent.EXTRA_PACKAGE_NAME, packageName);
                break;
            case SHOW_PACKAGE_INFO_MANAGE_LINKS:
                intent = new Intent(
                        Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                        Uri.parse("package:" + packageName));
                break;
            default:
                break;
        }
        if (intent != null && mContext.getPackageManager()
                .resolveActivity(intent, 0) == null) {
            Log.w(TAG, "No activity for page " + page
                    + "; showing App info instead");
            intent = null;
        }
        if (intent == null) {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to show package info for " + packageName, e);
        }
    }

    @Override
    public void startFastAppReinstallFlow(String[] packageNames) {
    }

    @Override
    public void startPaiFlow(StartPaiFlowResponse response) {
        response.call(0);
    }

    @Override
    public void uninstallPackage(String packageName) {
        Log.i(TAG, "uninstallPackage: " + packageName);
        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to uninstall " + packageName, e);
        }
    }

    @Override
    public void updateAppDetails(String packageName) {
    }

    @Override
    public void updateWindowInfo(WindowInfo windowInfo) {
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        mHost = null;
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        mHost = null;
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Log.i(TAG, "Bridge ready, registering AppInstance");
        host.onAppInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

/*
 * Copyright (C) 2024 The FydeOS Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.power;

import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.SystemSensorManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.input.InputManagerInternal;
import android.net.Uri;
import android.os.BatteryManagerInternal;
import android.os.BatterySaverPolicyConfig;
import android.os.Binder;
import android.os.IWakeLockCallback;
import android.os.ParcelDuration;
import android.os.Handler;
import android.os.IBatteryPropertiesRegistrar;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.view.KeyEvent;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.Display;

import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.am.BatteryStatsService;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.power.batterysaver.BatterySaverController;
import com.android.server.power.batterysaver.BatterySaverPolicy;
import com.android.server.power.batterysaver.BatterySaverStateMachine;
import com.android.server.power.batterysaver.BatterySavingStats;

import org.chromium.arc.CancellationRelay;
import org.chromium.arc.FutureCallback;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.BatterySaverModeState;
import org.chromium.arc.mojom.PowerHost;
import org.chromium.arc.mojom.PowerInstance;
import org.chromium.mojo.system.MojoException;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * ARC++ power manager service. Replaces stock PowerManagerService for the
 * Android container running on Chrome OS. Publishes the "power" binder service
 * (IPowerManager) and PowerManagerInternal.
 *
 * Key differences from stock PowerManagerService:
 * - No actual hardware suspend/resume (container shares host kernel)
 * - Display state controlled by Chrome OS host via Mojo PowerHost
 * - Wake locks forwarded to Chrome OS via PowerHost
 * - Simplified brightness handling (forwards to host)
 */
public final class ArcPowerManagerService extends SystemService
        implements Watchdog.Monitor, PowerInstance, ArcService {

    private static final String ACTIVITY_MANAGER_SLEEP_TAG = "ActivityManager-Sleep";
    private static final String TAG = "ArcPowerManagerService";

    private static final int DIRTY_WAKE_LOCKS = 1;
    private static final int DIRTY_WAKEFULNESS = 2;
    private static final int DIRTY_USER_ACTIVITY = 4;
    private static final int DIRTY_ACTUAL_DISPLAY_POWER_STATE_UPDATED = 8;
    private static final int DIRTY_BOOT_COMPLETED = 16;
    private static final int DIRTY_SETTINGS = 32;
    private static final int DIRTY_IS_POWERED = 64;
    private static final int DIRTY_STAY_ON = 128;
    private static final int DIRTY_BATTERY_STATE = 256;
    private static final int DIRTY_PROXIMITY_POSITIVE = 512;
    private static final int DIRTY_DOCK_STATE = 1024;
    private static final int DIRTY_SCREEN_BRIGHTNESS_BOOST = 2048;
    private static final int DIRTY_QUIESCENT = 4096;
    private static final int DIRTY_VR_MODE_CHANGED = 8192;

    private static final int MSG_USER_ACTIVITY_TIMEOUT = 1;
    private static final int MSG_SANDMAN = 2;
    private static final int MSG_SCREEN_BRIGHTNESS_BOOST_TIMEOUT = 3;
    private static final int MSG_CHECK_FOR_LONG_WAKELOCKS = 4;

    static final long MIN_LONG_WAKE_CHECK_INTERVAL = 60000;
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    private ActivityManagerInternal mActivityManagerInternal;
    private BatteryManagerInternal mBatteryManagerInternal;
    private final BatterySaverController mBatterySaverController;
    private final BatterySaverPolicy mBatterySaverPolicy;
    private final BatterySaverStateMachine mBatterySaverStateMachine;
    private final BatterySavingStats mBatterySavingStats;
    private IBatteryStats mBatteryStats;
    private CancellationRelay mCancellationRelay;
    final Constants mConstants;
    private final Context mContext;
    private boolean mDeviceIdleMode;
    int[] mDeviceIdleTempWhitelist;
    int[] mDeviceIdleWhitelist;
    private int mDirty;
    private DisplayManagerInternal mDisplayManagerInternal;
    private int mDisplayState;
    private final Handler mHandler;
    private final ServiceThread mHandlerThread;
    private float mHostScreenBrightness;
    // fydroid: JobScheduler idle while Chrome OS is awake.
    private ArcIdleMonitor mIdleMonitor;
    private InputManagerInternal mInputManagerInternal;
    private InputMethodManagerInternal mInputMethodManagerInternal;
    private boolean mIsPowered;
    private int mLastSleepReason;
    private boolean mLightDeviceIdleMode;
    private final Object mLock;
    private Notifier mNotifier;
    private long mNotifyLongDispatched;
    private long mNotifyLongNextCheck;
    private long mNotifyLongScheduled;
    private WindowManagerPolicy mPolicy;
    private PowerHost mPowerHost;
    private int mPowerHostVersion;
    private final Intent mScreenOffIntent;
    private final Intent mScreenOnIntent;
    private SettingsObserver mSettingsObserver;
    private SuspendCompleter mSuspendCompleter;
    private boolean mSystemReady;
    private final SparseArray<UidState> mUidState;
    private boolean mUidsChanged;
    private boolean mUidsChanging;
    private final ArrayList<WakeLock> mWakeLocks;
    private int mWakefulness;

    // --- Inner classes ---

    final class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (mLock) {
                updateIsPoweredLocked();
            }
        }
    }

    final class BinderService extends IPowerManager.Stub {

        @Override
        public void acquireWakeLock(IBinder lock, int flags, String tag, String packageName,
                WorkSource ws, String historyTag, int displayId, IWakeLockCallback callback) {
            if (lock == null) throw new IllegalArgumentException("lock must not be null");
            if (packageName == null) throw new IllegalArgumentException("packageName must not be null");
            PowerManager.validateWakeLockParameters(flags, tag);
            mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
            if ((flags & DIRTY_IS_POWERED) != 0) {
                mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            }
            WorkSource validWs = null;
            if (ws != null && !ws.isEmpty()) {
                mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", null);
                validWs = ws;
            }
            int uid = Binder.getCallingUid();
            int pid = Binder.getCallingPid();
            long ident = Binder.clearCallingIdentity();
            try {
                acquireWakeLockInternal(lock, flags, tag, packageName, validWs, historyTag, uid, pid);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public void acquireWakeLockWithUid(IBinder lock, int flags, String tag,
                String packageName, int uid, int displayId, IWakeLockCallback callback) {
            if (uid < 0) uid = Binder.getCallingUid();
            acquireWakeLock(lock, flags, tag, packageName, new WorkSource(uid), null,
                    displayId, callback);
        }

        @Override
        public void boostScreenBrightness(long eventTime) {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
        }

        @Override
        public void crash(String message) {
            mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
            long ident = Binder.clearCallingIdentity();
            try {
                crashInternal(message);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (!DumpUtils.checkDumpPermission(mContext, TAG, pw)) return;
            long ident = Binder.clearCallingIdentity();
            boolean proto = false;
            for (String arg : args) {
                if ("--proto".equals(arg)) proto = true;
            }
            try {
                if (proto) {
                    dumpProto(fd);
                } else {
                    dumpInternal(pw);
                }
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public boolean forceSuspend() {
            throw new UnsupportedOperationException("forceSuspend not supported.");
        }

        @Override
        public float getBrightnessConstraint(int constraint) {
            int value;
            switch (constraint) {
                case 0: case 3: value = 10; break;
                case 1: case 6: value = 255; break;
                case 2: value = 102; break;
                case 4: value = 1; break;
                case 5: value = 79; break;
                case 7: value = 86; break;
                default:
                    Slog.e(TAG, "Unknown brightness constraint ID: " + constraint);
                    return 0.0f;
            }
            return value / 255.0f;
        }

        @Override
        public int getLastShutdownReason() {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            return 0;
        }

        @Override
        public int getLastSleepReason() {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            synchronized (mLock) {
                return mLastSleepReason;
            }
        }

        @Override
        public int getPowerSaveModeTrigger() {
            mContext.enforceCallingOrSelfPermission("android.permission.POWER_SAVER", null);
            long ident = Binder.clearCallingIdentity();
            try {
                return Settings.Global.getInt(mContext.getContentResolver(),
                        "automatic_power_save_mode", 0);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public PowerSaveState getPowerSaveState(int serviceType) {
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (mLock) {
                    return mBatterySaverPolicy.getBatterySaverPolicy(serviceType);
                }
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public void goToSleep(long eventTime, int reason, int flags) {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            synchronized (mLock) {
                mLastSleepReason = reason;
                updateWakefulnessLocked(3);
                updateDisplayPowerStateLocked(0);
            }
        }

        @Override
        public boolean isAmbientDisplayAvailable() { return false; }

        @Override
        public boolean isAmbientDisplaySuppressed() { return true; }

        @Override
        public boolean isAmbientDisplaySuppressedForToken(String token) { return true; }

        @Override
        public boolean isAmbientDisplaySuppressedForTokenByApp(String token, int appUid) { return true; }

        @Override
        public boolean isDeviceIdleMode() {
            synchronized (mLock) { return mDeviceIdleMode; }
        }

        @Override
        public boolean isInteractive() {
            synchronized (mLock) { return mDisplayState == 2; }
        }

        @Override
        public boolean isLightDeviceIdleMode() {
            synchronized (mLock) { return mLightDeviceIdleMode; }
        }

        @Override
        public boolean isPowerSaveMode() {
            long ident = Binder.clearCallingIdentity();
            try {
                return isLowPowerModeInternal();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public boolean isScreenBrightnessBoosted() { return false; }

        @Override
        public boolean isWakeLockLevelSupported(int level) {
            long ident = Binder.clearCallingIdentity();
            try {
                return isWakeLockLevelSupportedInternal(level);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public void nap(long eventTime) {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            synchronized (mLock) {
                updateWakefulnessLocked(2);
            }
        }

        @Override
        public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err,
                String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
            // fydroid-rt (AOSP-13): PowerManagerShellCommand's ctor now requires the real
            // PowerManagerService.BinderService (Context, BinderService), which this ARC
            // fork can't supply — no shell command surface for the ARC power service.
        }

        // fydroid-rt (AOSP-13): powerHint(int,int) was removed from IPowerManager in
        // 12 (replaced by setPowerBoost/setPowerMode, implemented below).

        @Override
        public void reboot(boolean confirm, String reason, boolean wait) {
            mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
            if ("recovery".equals(reason)) {
                mContext.enforceCallingOrSelfPermission("android.permission.RECOVERY", null);
            }
        }

        @Override
        public void rebootSafeMode(boolean confirm, boolean wait) {
            mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
        }

        @Override
        public void releaseWakeLock(IBinder lock, int flags) {
            if (lock == null) throw new IllegalArgumentException("lock must not be null");
            mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
            long ident = Binder.clearCallingIdentity();
            try {
                releaseWakeLockInternal(lock, flags);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public boolean setAdaptivePowerSaveEnabled(boolean enabled) {
            if (mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER",
                        "setAdaptivePowerSaveEnabled");
            }
            long ident = Binder.clearCallingIdentity();
            try {
                return mBatterySaverStateMachine.setAdaptiveBatterySaverEnabled(enabled);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig config) {
            if (mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER",
                        "setAdaptivePowerSavePolicy");
            }
            long ident = Binder.clearCallingIdentity();
            try {
                return mBatterySaverStateMachine.setAdaptiveBatterySaverPolicy(config);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override
        public void setAttentionLight(boolean on, int color) {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
        }

        @Override
        public void setDozeAfterScreenOff(boolean on) {}

        @Override
        public boolean setDynamicPowerSaveHint(boolean powerSaveHint, int disableThreshold) {
            mContext.enforceCallingOrSelfPermission("android.permission.POWER_SAVER", null);
            long ident = Binder.clearCallingIdentity();
            try {
                ContentResolver cr = mContext.getContentResolver();
                boolean result = Settings.Global.putInt(cr,
                        "dynamic_power_savings_disable_threshold", disableThreshold);
                if (result) {
                    result &= Settings.Global.putInt(cr,
                            "dynamic_power_savings_enabled", powerSaveHint ? 1 : 0);
                }
                return result;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override public void setPowerBoost(int boost, int durationMs) {}
        @Override public void setPowerMode(int mode, boolean enabled) {}
        @Override public boolean setPowerModeChecked(int mode, boolean enabled) { return false; }

        @Override
        public boolean setPowerSaveModeEnabled(boolean enabled) {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            long ident = Binder.clearCallingIdentity();
            try {
                return setLowPowerModeInternal(enabled);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override public void setStayOnSetting(int val) {}

        @Override
        public void shutdown(boolean confirm, String reason, boolean wait) {
            mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
        }

        @Override public void suppressAmbientDisplay(String token, boolean suppress) {}

        @Override
        public void updateWakeLockUids(IBinder lock, int[] uids) {
            WorkSource ws = null;
            if (uids != null) {
                ws = new WorkSource();
                for (int uid : uids) ws.add(uid);
            }
            updateWakeLockWorkSource(lock, ws, null);
        }

        @Override
        public void updateWakeLockWorkSource(IBinder lock, WorkSource ws, String historyTag) {
            if (lock == null) throw new IllegalArgumentException("lock must not be null");
            mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
            if (ws != null && ws.isEmpty()) ws = null;
            if (ws != null) {
                mContext.enforceCallingOrSelfPermission(
                        "android.permission.UPDATE_DEVICE_STATS", null);
            }
            int uid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                updateWakeLockWorkSourceInternal(lock, ws, historyTag, uid);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override public void userActivity(int displayId, long eventTime, int event, int flags) {}

        // fydroid-rt (AOSP-13): new/async IPowerManager methods added in 12+.
        @Override
        public void acquireWakeLockAsync(IBinder lock, int flags, String tag, String packageName,
                WorkSource ws, String historyTag) {
            acquireWakeLock(lock, flags, tag, packageName, ws, historyTag, 0 /* displayId */,
                    null /* callback */);
        }

        @Override
        public void releaseWakeLockAsync(IBinder lock, int flags) {
            releaseWakeLock(lock, flags);
        }

        @Override
        public void updateWakeLockUidsAsync(IBinder lock, int[] uids) {
            updateWakeLockUids(lock, uids);
        }

        @Override
        public void updateWakeLockCallback(IBinder lock, IWakeLockCallback callback) {}

        @Override
        public BatterySaverPolicyConfig getFullPowerSavePolicy() { return null; }

        @Override
        public boolean setFullPowerSavePolicy(BatterySaverPolicyConfig config) { return false; }

        @Override
        public void setBatteryDischargePrediction(ParcelDuration timeRemaining,
                boolean isCustomized) {}

        @Override
        public ParcelDuration getBatteryDischargePrediction() { return null; }

        @Override
        public boolean isBatteryDischargePredictionPersonalized() { return false; }

        @Override
        public boolean isLowPowerStandbySupported() { return false; }

        @Override
        public boolean isLowPowerStandbyEnabled() { return false; }

        @Override
        public void setLowPowerStandbyEnabled(boolean enabled) {}

        @Override
        public void setLowPowerStandbyActiveDuringMaintenance(boolean activeDuringMaintenance) {}

        @Override
        public void forceLowPowerStandbyActive(boolean active) {}

        @Override
        public void wakeUp(long eventTime, int reason, String details, String opPackageName) {
            mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            synchronized (mLock) {
                updateWakefulnessLocked(1);
                updateDisplayPowerStateLocked(3);
                if (mPowerHost == null) {
                    Log.e(TAG, "PowerHost not available");
                } else {
                    mPowerHost.onAcquireDisplayWakeLock(0);
                    mPowerHost.onReleaseDisplayWakeLock(0);
                }
            }
        }
    }

    final class Constants extends ContentObserver {
        private static final boolean DEFAULT_NO_CACHED_WAKE_LOCKS = true;
        private static final String KEY_NO_CACHED_WAKE_LOCKS = "no_cached_wake_locks";
        public boolean NO_CACHED_WAKE_LOCKS = DEFAULT_NO_CACHED_WAKE_LOCKS;
        private final KeyValueListParser mParser = new KeyValueListParser(',');
        private ContentResolver mResolver;

        Constants(Handler handler) { super(handler); }

        private void updateConstants() {
            synchronized (mLock) {
                try {
                    mParser.setString(Settings.Global.getString(mResolver,
                            "power_manager_constants"));
                } catch (IllegalArgumentException e) {
                    Slog.e(TAG, "Bad power manager settings", e);
                }
                NO_CACHED_WAKE_LOCKS = mParser.getBoolean(
                        KEY_NO_CACHED_WAKE_LOCKS, DEFAULT_NO_CACHED_WAKE_LOCKS);
            }
        }

        void dump(PrintWriter pw) {
            pw.println("  Settings power_manager_constants:");
            pw.print("    "); pw.print(KEY_NO_CACHED_WAKE_LOCKS);
            pw.print("="); pw.println(NO_CACHED_WAKE_LOCKS);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) { updateConstants(); }

        public void start(ContentResolver resolver) {
            mResolver = resolver;
            resolver.registerContentObserver(
                    Settings.Global.getUriFor("power_manager_constants"), false, this);
            updateConstants();
        }
    }

    final class DisplayPowerCallbacksArc implements DisplayManagerInternal.DisplayPowerCallbacks {
        // fydroid-rt (AOSP-13): acquire/releaseSuspendBlocker gained a String id.
        @Override public void acquireSuspendBlocker(String id) {}

        @Override
        public void onDisplayStateChange(boolean allInactive, boolean allOff) {
            synchronized (mLock) {
                // fydroid-rt (AOSP-13): signature changed from onDisplayStateChange(int state)
                // to (boolean allInactive, boolean allOff). Map to our int mDisplayState
                // (2 = interactive/on, 1 = off), preserving the suspend-completion notify.
                int state = allOff ? 1 : 2;
                if (state != mDisplayState) {
                    Log.i(TAG, "Display state change old: " + mDisplayState + " new: " + state);
                    mDisplayState = state;
                    if (isSuspendCompletionPendingLocked() && allOff) {
                        mSuspendCompleter.notifyDisplayOffComplete();
                    }
                }
            }
        }

        @Override public void onProximityNegative() {}
        @Override public void onProximityPositive() {}
        @Override public void onStateChanged() {}
        @Override public void releaseSuspendBlocker(String id) {}
    }

    final class LocalService extends PowerManagerInternal {
        private void handleUidStateChangeLocked() {
            if (mUidsChanging) {
                mUidsChanged = true;
            } else {
                updateWakeLockDisabledStatesLocked();
            }
        }

        @Override public void finishUidChanges() {
            synchronized (mLock) {
                mUidsChanging = false;
                if (mUidsChanged) {
                    updateWakeLockDisabledStatesLocked();
                    mUidsChanged = false;
                }
            }
        }

        @Override public PowerManager.WakeData getLastWakeup() { return null; }

        // fydroid-rt (AOSP-13): new PowerManagerInternal abstract method (12+).
        @Override public PowerManager.SleepData getLastGoToSleep() { return null; }

        @Override
        public PowerSaveState getLowPowerState(int serviceType) {
            synchronized (mLock) {
                return mBatterySaverPolicy.getBatterySaverPolicy(serviceType);
            }
        }

        // fydroid-rt (AOSP-13): PowerManagerInternal.powerHint(int,int) was removed in
        // 12 (replaced by setPowerBoost/setPowerMode, implemented below).

        @Override
        public void registerLowPowerModeObserver(LowPowerModeListener listener) {
            mBatterySaverController.addListener(listener);
        }

        @Override
        public boolean setDeviceIdleMode(boolean enabled) {
            synchronized (mLock) {
                if (mDeviceIdleMode == enabled) return false;
                mDeviceIdleMode = enabled;
                updateWakeLockDisabledStatesLocked();
                return true;
            }
        }

        @Override
        public void setDeviceIdleTempWhitelist(int[] appids) {
            synchronized (mLock) {
                mDeviceIdleTempWhitelist = appids;
                if (mDeviceIdleMode) updateWakeLockDisabledStatesLocked();
            }
        }

        @Override
        public void setDeviceIdleWhitelist(int[] appids) {
            synchronized (mLock) {
                mDeviceIdleWhitelist = appids;
                if (mDeviceIdleMode) updateWakeLockDisabledStatesLocked();
            }
        }

        @Override public void setDozeOverrideFromDreamManager(int screenState, int brightness) {}
        @Override public void setDrawWakeLockOverrideFromSidekick(boolean enabled) {}

        @Override
        public boolean setLightDeviceIdleMode(boolean enabled) {
            synchronized (mLock) {
                if (mLightDeviceIdleMode == enabled) return false;
                mLightDeviceIdleMode = enabled;
                return true;
            }
        }

        @Override public void setMaximumScreenOffTimeoutFromDeviceAdmin(int userId, long timeout) {}
        @Override public void setPowerBoost(int boost, int durationMs) {}
        @Override public void setPowerMode(int mode, boolean enabled) {}
        @Override public void setScreenBrightnessOverrideFromWindowManager(float brightness) {}
        @Override public void setUserActivityTimeoutOverrideFromWindowManager(long timeout) {}
        @Override public void setUserInactiveOverrideFromWindowManager() {}

        @Override
        public void startUidChanges() {
            synchronized (mLock) { mUidsChanging = true; }
        }

        @Override public void uidActive(int uid) { uidActiveInternal(uid); }
        @Override public void uidGone(int uid) { uidGoneInternal(uid); }
        @Override public void uidIdle(int uid) { uidIdleInternal(uid); }

        @Override
        public void updateUidProcState(int uid, int procState) {
            synchronized (mLock) {
                UidState state = mUidState.get(uid);
                if (state == null) {
                    state = new UidState(uid);
                    mUidState.put(uid, state);
                }
                boolean wasForeground = state.mProcState <= 11;
                state.mProcState = procState;
                if (state.mNumWakeLocks > 0) {
                    if (mDeviceIdleMode) {
                        handleUidStateChangeLocked();
                    } else if (!state.mActive) {
                        boolean isForeground = procState <= 11;
                        if (wasForeground != isForeground) {
                            handleUidStateChangeLocked();
                        }
                    }
                }
            }
        }

        @Override
        public boolean wasDeviceIdleFor(long ms) {
            Log.e(TAG, "wasDeviceIdleFor is not implemented");
            return false;
        }

        @Override
        public boolean interceptPowerKeyDown(KeyEvent event) {
            return false;
        }

        // fydroid-rt (AOSP-13): new PowerManagerInternal abstract methods (12+).
        @Override public void nap(long eventTime, boolean allowWake) {}

        @Override public boolean isAmbientDisplaySuppressed() { return true; }

        @Override public void setLowPowerStandbyAllowlist(int[] uids) {}

        @Override public void setLowPowerStandbyActive(boolean active) {}
    }

    final class PowerManagerHandler extends Handler {
        PowerManagerHandler(Looper looper) { super(looper, null, true); }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CHECK_FOR_LONG_WAKELOCKS) {
                checkForLongWakeLocks();
            }
        }
    }

    final class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) { super(handler); }

        @Override
        public void onChange(boolean selfChange) {
            synchronized (mLock) { handleSettingsChangedLocked(); }
        }
    }

    final class SuspendBlockerImpl implements SuspendBlocker {
        private final String mName;
        private final String mTraceName;
        private int mReferenceCount;

        SuspendBlockerImpl(String name) {
            mName = name;
            mTraceName = "SuspendBlocker (" + name + ")";
        }

        @Override
        public void acquire() {
            synchronized (this) {
                if (++mReferenceCount == 1) {
                    Trace.asyncTraceBegin(131072L, mTraceName, 0);
                }
            }
        }

        // fydroid-rt (AOSP-13): SuspendBlocker gained id-tagged acquire/release.
        @Override
        public void acquire(String id) {
            acquire();
        }

        @Override
        public void release() {
            synchronized (this) {
                if (--mReferenceCount == 0) {
                    Trace.asyncTraceEnd(131072L, mTraceName, 0);
                } else if (mReferenceCount < 0) {
                    Slog.wtf(TAG, "Suspend blocker \"" + mName
                            + "\" was released without being acquired!", new Throwable());
                    mReferenceCount = 0;
                }
            }
        }

        @Override
        public void release(String id) {
            release();
        }


        @Override
        public String toString() {
            synchronized (this) {
                return mName + ": ref count=" + mReferenceCount;
            }
        }

        public void dumpDebug(ProtoOutputStream proto, long fieldId) {
            long token = proto.start(fieldId);
            synchronized (this) {
                proto.write(1138166333441L, mName);
                proto.write(1120986464258L, mReferenceCount);
            }
            proto.end(token);
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                if (mReferenceCount != 0) {
                    Slog.wtf(TAG, "Suspend blocker \"" + mName
                            + "\" was finalized without being released!");
                    mReferenceCount = 0;
                    Trace.asyncTraceEnd(131072L, mTraceName, 0);
                }
            } finally {
                super.finalize();
            }
        }
    }

    final class UidState {
        boolean mActive;
        int mNumWakeLocks;
        int mProcState;
        final int mUid;
        UidState(int uid) { mUid = uid; }
    }

    final class WakeLock implements IBinder.DeathRecipient {
        public long mAcquireTime;
        public boolean mDisabled;
        public int mFlags;
        public String mHistoryTag;
        public final IBinder mLock;
        public boolean mNotifiedAcquired;
        public boolean mNotifiedLong;
        public final int mOwnerPid;
        public final int mOwnerUid;
        public final String mPackageName;
        public String mTag;
        public final UidState mUidState;
        public WorkSource mWorkSource;

        WakeLock(IBinder lock, int flags, String tag, String packageName,
                WorkSource ws, String historyTag, int ownerUid, int ownerPid,
                UidState uidState) {
            mLock = lock;
            mFlags = flags;
            mTag = tag;
            mPackageName = packageName;
            mWorkSource = copyWorkSource(ws);
            mHistoryTag = historyTag;
            mOwnerUid = ownerUid;
            mOwnerPid = ownerPid;
            mUidState = uidState;
        }

        @Override
        public void binderDied() { handleWakeLockDeath(this); }

        public boolean hasSameProperties(int flags, String tag, String packageName,
                WorkSource ws, int uid) {
            return mFlags == flags && mTag.equals(tag) && mPackageName.equals(packageName)
                    && hasSameWorkSource(ws) && mOwnerUid == uid;
        }

        public boolean hasSameWorkSource(WorkSource ws) {
            return Objects.equals(mWorkSource, ws);
        }

        public void updateProperties(int flags, String tag, String packageName,
                WorkSource ws, String historyTag, int uid) {
            if (!mPackageName.equals(packageName)) {
                throw new IllegalStateException("Existing wake lock package name changed: "
                        + mPackageName + " to " + packageName);
            }
            if (mOwnerUid != uid) {
                throw new IllegalStateException("Existing wake lock uid changed: "
                        + mOwnerUid + " to " + uid);
            }
            mFlags = flags;
            mTag = tag;
            updateWorkSource(ws);
            mHistoryTag = historyTag;
        }

        public void updateWorkSource(WorkSource ws) {
            mWorkSource = copyWorkSource(ws);
        }

        private String getLockLevelString() {
            switch (mFlags & 65535) {
                case 1: return "PARTIAL_WAKE_LOCK             ";
                case 6: return "SCREEN_DIM_WAKE_LOCK          ";
                case 10: return "SCREEN_BRIGHT_WAKE_LOCK       ";
                case 26: return "FULL_WAKE_LOCK                ";
                case 32: return "PROXIMITY_SCREEN_OFF_WAKE_LOCK";
                case 64: return "DOZE_WAKE_LOCK                ";
                case 128: return "DRAW_WAKE_LOCK                ";
                default: return "???                           ";
            }
        }

        private String getLockFlagsString() {
            String s = (mFlags & 268435456) != 0 ? " ACQUIRE_CAUSES_WAKEUP" : "";
            if ((mFlags & 536870912) != 0) s += " ON_AFTER_RELEASE";
            return s;
        }

        @Override
        public String toString() {
            return getLockLevelString() + " '" + mTag + "'" + getLockFlagsString()
                    + (mDisabled ? " DISABLED" : "")
                    + " (uid=" + mOwnerUid + ", ws=" + mWorkSource + ")";
        }
    }

    // --- Constructor ---

    public ArcPowerManagerService(Context context) {
        super(context);
        mLock = new Object();
        mWakeLocks = new ArrayList<>();
        mSuspendCompleter = null;
        mIsPowered = false;
        mDisplayState = 2; // Display.STATE_ON
        mWakefulness = 1; // WAKEFULNESS_AWAKE
        mPowerHost = null;
        mCancellationRelay = null;
        mPowerHostVersion = -1;
        mLastSleepReason = 4; // GO_TO_SLEEP_REASON_TIMEOUT
        mUidState = new SparseArray<>();
        mDeviceIdleWhitelist = new int[0];
        mDeviceIdleTempWhitelist = new int[0];
        mContext = context;
        ServiceThread handlerThread = new ServiceThread(TAG, 0, false);
        mHandlerThread = handlerThread;
        handlerThread.start();
        mHandler = new PowerManagerHandler(mHandlerThread.getLooper());
        mConstants = new Constants(mHandler);
        mBatterySavingStats = new BatterySavingStats(mLock);
        mBatterySaverPolicy = new BatterySaverPolicy(mLock, mContext, mBatterySavingStats);
        mBatterySaverController = new BatterySaverController(mLock, mContext,
                BackgroundThread.get().getLooper(), mBatterySaverPolicy, mBatterySavingStats);
        mBatterySaverStateMachine = new BatterySaverStateMachine(mLock, mContext,
                mBatterySaverController);
        synchronized (mLock) {
            mScreenOnIntent = new Intent(Intent.ACTION_SCREEN_ON);
            mScreenOnIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY
                    | Intent.FLAG_RECEIVER_FOREGROUND);
            mScreenOffIntent = new Intent(Intent.ACTION_SCREEN_OFF);
            mScreenOffIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY
                    | Intent.FLAG_RECEIVER_FOREGROUND);
        }
    }

    // --- SystemService lifecycle ---

    @Override
    public void onStart() {
        mActivityManagerInternal = LocalServices.getService(ActivityManagerInternal.class);
        publishBinderService("power", new BinderService());
        publishLocalService(PowerManagerInternal.class, new LocalService());
        Watchdog.getInstance().addMonitor(this);
        Watchdog.getInstance().addThread(mHandler);
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == 500) { // PHASE_SYSTEM_SERVICES_READY
            synchronized (mLock) {
                mPolicy = getLocalService(WindowManagerPolicy.class);
                mBatteryManagerInternal = getLocalService(BatteryManagerInternal.class);
                mDisplayManagerInternal = getLocalService(DisplayManagerInternal.class);
                mInputManagerInternal = LocalServices.getService(InputManagerInternal.class);
                mInputMethodManagerInternal = LocalServices.getService(
                        InputMethodManagerInternal.class);
                mSystemReady = true;
                mSettingsObserver = new SettingsObserver(mHandler);
                mContext.getContentResolver().registerContentObserver(
                        Settings.System.getUriFor("screen_brightness_float"),
                        false, mSettingsObserver, -1);
                updateIsPoweredLocked();
                mDisplayManagerInternal.initPowerManagement(
                        new DisplayPowerCallbacksArc(), mHandler,
                        new SystemSensorManager(mContext, mHandler.getLooper()));
                mBatteryStats = BatteryStatsService.getService();
                // fydroid-rt (AOSP-13): Notifier gained FaceDownDetector, ScreenUndimDetector
                // and a background Executor (same package, so no imports needed).
                mNotifier = new Notifier(Looper.getMainLooper(), mContext, mBatteryStats,
                        createSuspendBlockerLocked("PowerManagerService.Broadcasts"), mPolicy,
                        new FaceDownDetector(flip -> { }), new ScreenUndimDetector(),
                        BackgroundThread.getExecutor());
            }
            mBatterySaverController.systemReady();
            mBatterySaverPolicy.systemReady();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.setPriority(1000);
            mContext.registerReceiver(new BatteryReceiver(), filter, null, mHandler);
            ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(mContext, this,
                    MINIMUM_ARC_BRIDGE_HOST_VERSION);
        } else if (phase == 600) { // PHASE_THIRD_PARTY_APPS_CAN_START
            incrementBootCount();
        } else if (phase == 1000) { // PHASE_BOOT_COMPLETED
            mBatterySaverStateMachine.onBootCompleted();
            mIdleMonitor = new ArcIdleMonitor(mContext, mHandler.getLooper());
            mIdleMonitor.start();
        }
    }

    // --- Watchdog.Monitor ---

    @Override
    public void monitor() {
        synchronized (mLock) { /* just test that we can acquire the lock */ }
    }

    // --- ArcService / Mojo PowerInstance ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        host.onPowerInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Log.e(TAG, "PowerInstance not supported on host bridge version " + version
                + ", need at least version " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    @Override
    public void onConnectionError(MojoException e) {
        Log.e(TAG, "Mojo connection error", e);
        synchronized (mLock) {
            if (mPowerHost == null) return;
            mPowerHost.close();
            close();
        }
    }

    @Override
    public void close() {
        Log.i(TAG, "Mojo connection closed");
        synchronized (mLock) {
            if (mPowerHost == null) return;
            mPowerHost = null;
            mCancellationRelay.cancel(true);
            mPowerHostVersion = -1;
            ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(mContext, this,
                    MINIMUM_ARC_BRIDGE_HOST_VERSION);
        }
    }

    @Override
    public void init(PowerHost host, final PowerInstance.InitResponse callback) {
        ArcBridgeSupport.buildThreadSafeProxy(host, PowerHost.MANAGER, this,
                (PowerHost proxy, Integer version) -> {
                    synchronized (mLock) {
                        Log.i(TAG, "Host version " + version);
                        mPowerHost = proxy;
                        mPowerHostVersion = version;
                        mCancellationRelay = new CancellationRelay();
                    }
                    callback.call();
                });
    }

    @Override
    public void suspend(PowerInstance.SuspendResponse callback) {
        synchronized (mLock) {
            if (isSuspendCompletionPendingLocked()) {
                Log.wtf(TAG, "Host called consecutive suspends without a resume");
            }
            mSuspendCompleter = new SuspendCompleter(callback);
            updateWakefulnessLocked(0); // WAKEFULNESS_ASLEEP
            updateDisplayPowerStateLocked(0); // POLICY_OFF
        }
    }

    @Override
    public void resume() {
        Slog.d(TAG, "Suspend is over; resuming all apps");
        synchronized (mLock) {
            if (isSuspendCompletionPendingLocked()) {
                mSuspendCompleter.forceSuspend();
            }
            mSuspendCompleter = null;
            updateWakefulnessLocked(1); // WAKEFULNESS_AWAKE
            updateDisplayPowerStateLocked(3); // POLICY_BRIGHT
        }
    }

    @Override
    public void setIdleState(int idleState) {
        synchronized (mLock) {
            if (idleState == 2) { // FORCE_INACTIVE
                Slog.w(TAG, "FORCE_INACTIVE is currently not supported.");
                return;
            }
            int wakefulness;
            int displayPolicy;
            if (idleState == 0) { // ACTIVE
                wakefulness = 1; // AWAKE
                displayPolicy = 3; // BRIGHT
            } else { // INACTIVE
                if (!PowerManagerInternal.isInteractive(mWakefulness)) return;
                wakefulness = 3; // DOZING
                displayPolicy = 0; // OFF
            }
            updateWakefulnessLocked(wakefulness);
            updateDisplayPowerStateLocked(displayPolicy);
        }
    }

    @Override
    public void setInteractiveDeprecated(boolean interactive) {
        setIdleState(interactive ? 0 : 1);
    }

    @Override
    public void powerSupplyInfoChanged() {
        try {
            IBatteryPropertiesRegistrar.Stub.asInterface(
                    ServiceManager.getService("batteryproperties")).scheduleUpdate();
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to notify power supply info change: ", e);
        }
    }

    @Override
    public void getWakefulnessMode(PowerInstance.GetWakefulnessModeResponse callback) {
        callback.call(-1);
    }

    @Override
    public void updateScreenBrightnessSettings(double percent) {
        synchronized (mLock) {
            mHostScreenBrightness = (float) (percent / 100.0);
            Settings.System.putFloat(mContext.getContentResolver(),
                    "screen_brightness_float", mHostScreenBrightness);
        }
    }

    @Override
    public void onBatterySaverModeStateChanged(BatterySaverModeState state) {
        Log.i(TAG, "battery saver mode changed (no-op for R container): " + state);
    }

    @Override
    public void onCpuRestrictionChanged(int restriction) {
        Log.i(TAG, "cpu restriction changed (no-op for R container): " + restriction);
    }

    // --- Internal methods ---

    private void acquireWakeLockInternal(IBinder lock, int flags, String tag,
            String packageName, WorkSource ws, String historyTag, int uid, int pid) {
        synchronized (mLock) {
            int index = findWakeLockIndexLocked(lock);
            WakeLock wakeLock;
            boolean notifyAcquire = false;
            if (index >= 0) {
                wakeLock = mWakeLocks.get(index);
                if (!wakeLock.hasSameProperties(flags, tag, packageName, ws, uid)) {
                    notifyWakeLockChangingLocked(wakeLock, flags, tag, packageName,
                            uid, pid, ws, historyTag);
                    wakeLock.updateProperties(flags, tag, packageName, ws, historyTag, uid);
                }
            } else {
                UidState uidState = mUidState.get(uid);
                if (uidState == null) {
                    uidState = new UidState(uid);
                    uidState.mProcState = 20; // PROCESS_STATE_NONEXISTENT
                    mUidState.put(uid, uidState);
                }
                uidState.mNumWakeLocks++;
                wakeLock = new WakeLock(lock, flags, tag, packageName, ws, historyTag,
                        uid, pid, uidState);
                try {
                    lock.linkToDeath(wakeLock, 0);
                } catch (RemoteException e) {
                    throw new IllegalArgumentException("Wake lock is already dead.");
                }
                mWakeLocks.add(wakeLock);
                setWakeLockDisabledStateLocked(wakeLock);
                if (isCrosCompatibleFlags(flags) && !isPartialWakeLockFlag(flags)) {
                    if (mPowerHost == null) {
                        Log.e(TAG, "PowerHost not available");
                        return;
                    }
                    mPowerHost.onAcquireDisplayWakeLock(translateFlags(flags));
                }
                notifyAcquire = true;
            }
            if (notifyAcquire) {
                notifyWakeLockAcquiredLocked(wakeLock);
            }
        }
    }

    private void releaseWakeLockInternal(IBinder lock, int flags) {
        synchronized (mLock) {
            int index = findWakeLockIndexLocked(lock);
            if (index < 0) return;
            WakeLock wakeLock = mWakeLocks.get(index);
            wakeLock.mLock.unlinkToDeath(wakeLock, 0);
            removeWakeLockLocked(wakeLock, index);
        }
    }

    private void removeWakeLockLocked(WakeLock wakeLock, int index) {
        mWakeLocks.remove(index);
        UidState uidState = wakeLock.mUidState;
        uidState.mNumWakeLocks--;
        if (uidState.mNumWakeLocks <= 0 && uidState.mProcState == 20) {
            mUidState.remove(uidState.mUid);
        }
        int flags = wakeLock.mFlags;
        if (isCrosCompatibleFlags(flags) && !isPartialWakeLockFlag(flags)) {
            if (mPowerHost == null) {
                Log.e(TAG, "PowerHost not available");
                return;
            }
            mPowerHost.onReleaseDisplayWakeLock(translateFlags(flags));
        }
        notifyWakeLockReleasedLocked(wakeLock);
        if (wakeLock.mTag.equals(ACTIVITY_MANAGER_SLEEP_TAG)
                && isSuspendCompletionPendingLocked()) {
            Slog.d(TAG, "Marking ready for suspend");
            mSuspendCompleter.notifyActivityManagerSleepWakeLockReleased();
        }
    }

    private void handleWakeLockDeath(WakeLock wakeLock) {
        synchronized (mLock) {
            int index = mWakeLocks.indexOf(wakeLock);
            if (index < 0) return;
            removeWakeLockLocked(wakeLock, index);
        }
    }

    private int findWakeLockIndexLocked(IBinder lock) {
        for (int i = 0, size = mWakeLocks.size(); i < size; i++) {
            if (mWakeLocks.get(i).mLock == lock) return i;
        }
        return -1;
    }

    private void updateWakeLockWorkSourceInternal(IBinder lock, WorkSource ws,
            String historyTag, int callingUid) {
        synchronized (mLock) {
            int index = findWakeLockIndexLocked(lock);
            if (index < 0) {
                throw new IllegalArgumentException("Wake lock not active: " + lock
                        + " from uid " + callingUid);
            }
            WakeLock wakeLock = mWakeLocks.get(index);
            if (!wakeLock.hasSameWorkSource(ws)) {
                notifyWakeLockChangingLocked(wakeLock, wakeLock.mFlags, wakeLock.mTag,
                        wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid,
                        ws, historyTag);
                wakeLock.mHistoryTag = historyTag;
                wakeLock.updateWorkSource(ws);
            }
        }
    }

    /**
     * Determine whether a wake lock should be disabled based on UID state
     * and device idle mode.
     */
    private boolean setWakeLockDisabledStateLocked(WakeLock wakeLock) {
        int flagLevel = wakeLock.mFlags & 0xffff;
        if (flagLevel != 1) return false; // Only partial wake locks can be disabled

        int appId = UserHandle.getAppId(wakeLock.mOwnerUid);
        boolean disabled;

        if (appId < 10000) { // Process.FIRST_APPLICATION_UID
            disabled = false;
        } else {
            disabled = false;
            // Check NO_CACHED_WAKE_LOCKS policy
            if (mConstants.NO_CACHED_WAKE_LOCKS) {
                UidState us = wakeLock.mUidState;
                if (!us.mActive && us.mProcState != 20 && us.mProcState > 11) {
                    disabled = true;
                }
            }
            // Device idle mode check
            if (mDeviceIdleMode) {
                UidState us = wakeLock.mUidState;
                if (Arrays.binarySearch(mDeviceIdleWhitelist, appId) < 0
                        && Arrays.binarySearch(mDeviceIdleTempWhitelist, appId) < 0
                        && us.mProcState != 20
                        && us.mProcState > 5) {
                    disabled = true;
                }
            }
        }

        if (wakeLock.mDisabled != disabled) {
            wakeLock.mDisabled = disabled;
            return true;
        }
        return false;
    }

    private void updateWakeLockDisabledStatesLocked() {
        boolean changed = false;
        for (int i = 0, size = mWakeLocks.size(); i < size; i++) {
            WakeLock wl = mWakeLocks.get(i);
            if ((wl.mFlags & 0xffff) == 1 && setWakeLockDisabledStateLocked(wl)) {
                if (wl.mDisabled) {
                    notifyWakeLockReleasedLocked(wl);
                } else {
                    notifyWakeLockAcquiredLocked(wl);
                }
                changed = true;
            }
        }
        if (changed) {
            mDirty |= DIRTY_WAKE_LOCKS;
            updatePowerStateLocked();
        }
    }

    private void notifyWakeLockAcquiredLocked(WakeLock wakeLock) {
        if (!mSystemReady || wakeLock.mDisabled) return;
        wakeLock.mNotifiedAcquired = true;
        // fydroid-rt (AOSP-13): onWakeLock* gained a trailing IWakeLockCallback; this
        // fork doesn't track wakelock callbacks, so pass null.
        mNotifier.onWakeLockAcquired(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName,
                wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource,
                wakeLock.mHistoryTag, null /* callback */);
        restartNotifyLongTimerLocked(wakeLock);
    }

    private void notifyWakeLockChangingLocked(WakeLock wakeLock, int flags, String tag,
            String packageName, int uid, int pid, WorkSource ws, String historyTag) {
        if (mSystemReady && wakeLock.mNotifiedAcquired) {
            mNotifier.onWakeLockChanging(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName,
                    wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource,
                    wakeLock.mHistoryTag, null /* callback */, flags, tag, packageName, uid, pid,
                    ws, historyTag, null /* newCallback */);
            notifyWakeLockLongFinishedLocked(wakeLock);
            restartNotifyLongTimerLocked(wakeLock);
        }
    }

    private void notifyWakeLockReleasedLocked(WakeLock wakeLock) {
        if (mSystemReady && wakeLock.mNotifiedAcquired) {
            wakeLock.mNotifiedAcquired = false;
            wakeLock.mAcquireTime = 0;
            mNotifier.onWakeLockReleased(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName,
                    wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource,
                    wakeLock.mHistoryTag, null /* callback */);
            notifyWakeLockLongFinishedLocked(wakeLock);
        }
    }

    private void notifyWakeLockLongStartedLocked(WakeLock wakeLock) {
        if (!mSystemReady || wakeLock.mDisabled) return;
        wakeLock.mNotifiedLong = true;
        mNotifier.onLongPartialWakeLockStart(wakeLock.mTag, wakeLock.mOwnerUid,
                wakeLock.mWorkSource, wakeLock.mHistoryTag);
    }

    private void notifyWakeLockLongFinishedLocked(WakeLock wakeLock) {
        if (wakeLock.mNotifiedLong) {
            wakeLock.mNotifiedLong = false;
            mNotifier.onLongPartialWakeLockFinish(wakeLock.mTag, wakeLock.mOwnerUid,
                    wakeLock.mWorkSource, wakeLock.mHistoryTag);
        }
    }

    private void restartNotifyLongTimerLocked(WakeLock wakeLock) {
        long now = SystemClock.uptimeMillis();
        wakeLock.mAcquireTime = now;
        if ((wakeLock.mFlags & 0xffff) == 1 && mNotifyLongScheduled == 0) {
            enqueueNotifyLongMsgLocked(now + MIN_LONG_WAKE_CHECK_INTERVAL);
        }
    }

    private void enqueueNotifyLongMsgLocked(long time) {
        mNotifyLongScheduled = time;
        Message msg = mHandler.obtainMessage(MSG_CHECK_FOR_LONG_WAKELOCKS);
        msg.setAsynchronous(true);
        mHandler.sendMessageAtTime(msg, time);
    }

    void checkForLongWakeLocks() {
        synchronized (mLock) {
            long now = SystemClock.uptimeMillis();
            mNotifyLongDispatched = now;
            long threshold = now - MIN_LONG_WAKE_CHECK_INTERVAL;
            long nextCheck = Long.MAX_VALUE;
            for (int i = 0, size = mWakeLocks.size(); i < size; i++) {
                WakeLock wl = mWakeLocks.get(i);
                if ((wl.mFlags & 0xffff) == 1 && wl.mNotifiedAcquired && !wl.mNotifiedLong) {
                    if (wl.mAcquireTime < threshold) {
                        notifyWakeLockLongStartedLocked(wl);
                    } else {
                        long when = wl.mAcquireTime + MIN_LONG_WAKE_CHECK_INTERVAL;
                        if (when < nextCheck) nextCheck = when;
                    }
                }
            }
            mNotifyLongScheduled = 0;
            mHandler.removeMessages(MSG_CHECK_FOR_LONG_WAKELOCKS);
            if (nextCheck != Long.MAX_VALUE) {
                mNotifyLongNextCheck = nextCheck;
                enqueueNotifyLongMsgLocked(nextCheck);
            } else {
                mNotifyLongNextCheck = 0;
            }
        }
    }

    private void updateWakefulnessLocked(final int newWakefulness) {
        final int oldWakefulness = mWakefulness;
        mWakefulness = newWakefulness;
        mHandler.post(() -> {
            boolean isInteractive = PowerManagerInternal.isInteractive(newWakefulness);
            if (PowerManagerInternal.isInteractive(oldWakefulness) != isInteractive) {
                if (mSystemReady) {
                    mInputManagerInternal.setInteractive(isInteractive);
                    mInputMethodManagerInternal.setInteractive(isInteractive);
                }
                sendInteractivityBroadcast(isInteractive);
                if (mIdleMonitor != null) mIdleMonitor.setInteractive(isInteractive);
            }
            mActivityManagerInternal.onWakefulnessChanged(newWakefulness);
        });
    }

    private void updateDisplayPowerStateLocked(int policy) {
        DisplayManagerInternal.DisplayPowerRequest request =
                new DisplayManagerInternal.DisplayPowerRequest();
        request.policy = policy;
        // fydroid-rt (AOSP-13): requestPowerState gained a leading display-group id.
        mDisplayManagerInternal.requestPowerState(
                android.view.Display.DEFAULT_DISPLAY_GROUP, request, false);
    }

    private void updateIsPoweredLocked() {
        mIsPowered = mBatteryManagerInternal.isPowered(7);
        mBatterySaverStateMachine.setBatteryStatus(mIsPowered,
                mBatteryManagerInternal.getBatteryLevel(), false);
    }

    private void updatePowerStateLocked() { /* no-op in container */ }

    private void handleSettingsChangedLocked() {
        float brightness = Settings.System.getFloat(mContext.getContentResolver(),
                "screen_brightness_float", Float.NaN);
        if (Float.isNaN(brightness) || brightness < 0.0f || brightness > 1.0f) {
            Log.e(TAG, "Screen brightness settings have an invalid value");
            return;
        }
        if (mPowerHost == null) {
            Log.e(TAG, "PowerHost not available");
            return;
        }
        if (BrightnessSynchronizer.floatEquals(brightness, mHostScreenBrightness)) return;
        mPowerHost.onScreenBrightnessUpdateRequest(brightness * 100.0f);
    }

    private boolean isSuspendCompletionPendingLocked() {
        return mSuspendCompleter != null && !mSuspendCompleter.isComplete();
    }

    private boolean isLowPowerModeInternal() {
        return mBatterySaverController.isEnabled();
    }

    private boolean setLowPowerModeInternal(boolean enabled) {
        synchronized (mLock) {
            if (mIsPowered) return false;
            mBatterySaverStateMachine.setBatterySaverEnabledManually(enabled);
            return true;
        }
    }

    private boolean isWakeLockLevelSupportedInternal(int level) {
        synchronized (mLock) {
            return level == 1 || level == 6 || level == 10 || level == 26
                    || level == 64 || level == 128;
        }
    }

    private void sendInteractivityBroadcast(boolean interactive) {
        if (ActivityManagerNative.isSystemReady()) {
            mContext.sendBroadcastAsUser(
                    interactive ? mScreenOnIntent : mScreenOffIntent, UserHandle.ALL);
        }
    }

    private void crashInternal(final String message) {
        Thread t = new Thread("ArcPowerManagerService.crash()") {
            @Override
            public void run() { throw new RuntimeException(message); }
        };
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            Slog.wtf(TAG, e);
        }
    }

    private void incrementBootCount() {
        synchronized (mLock) {
            int count = Settings.Global.getInt(getContext().getContentResolver(),
                    "boot_count", 0);
            Settings.Global.putInt(getContext().getContentResolver(), "boot_count", count + 1);
        }
    }

    private SuspendBlocker createSuspendBlockerLocked(String name) {
        return new SuspendBlockerImpl(name);
    }

    private static boolean isCrosCompatibleFlags(int flags) {
        int level = flags & 0xffff;
        if (level == 1 || level == 6 || level == 10 || level == 26) return true;
        Log.e(TAG, "Incompatible flags: " + flags);
        return false;
    }

    private static boolean isPartialWakeLockFlag(int flags) {
        return (flags & 1) != 0;
    }

    private static int translateFlags(int flags) {
        int level = flags & 0xffff;
        if (level == 6) return 1;
        if (level == 10 || level == 26) return 0;
        throw new IllegalArgumentException("Unrecognized flags: " + flags);
    }

    static WorkSource copyWorkSource(WorkSource ws) {
        return ws != null ? new WorkSource(ws) : null;
    }

    void uidActiveInternal(int uid) {
        synchronized (mLock) {
            UidState state = mUidState.get(uid);
            if (state == null) {
                state = new UidState(uid);
                state.mProcState = 19;
                mUidState.put(uid, state);
            }
            state.mActive = true;
            if (state.mNumWakeLocks > 0) {
                if (mUidsChanging) mUidsChanged = true;
                else updateWakeLockDisabledStatesLocked();
            }
        }
    }

    void uidGoneInternal(int uid) {
        synchronized (mLock) {
            int index = mUidState.indexOfKey(uid);
            if (index >= 0) {
                UidState state = mUidState.valueAt(index);
                state.mProcState = 20;
                state.mActive = false;
                mUidState.removeAt(index);
                if (mDeviceIdleMode && state.mNumWakeLocks > 0) {
                    if (mUidsChanging) mUidsChanged = true;
                    else updateWakeLockDisabledStatesLocked();
                }
            }
        }
    }

    void uidIdleInternal(int uid) {
        synchronized (mLock) {
            UidState state = mUidState.get(uid);
            if (state != null) {
                state.mActive = false;
                if (state.mNumWakeLocks > 0) {
                    if (mUidsChanging) mUidsChanged = true;
                    else updateWakeLockDisabledStatesLocked();
                }
            }
        }
    }

    private void dumpInternal(PrintWriter pw) {
        pw.println("POWER MANAGER (dumpsys power)\n");
        if (mIdleMonitor != null) mIdleMonitor.dump(pw);
        synchronized (mLock) {
            pw.println("Power Manager State:");
            pw.println("  mSystemReady=" + mSystemReady);
            pw.println("  mWakefulness="
                    + PowerManagerInternal.wakefulnessToString(mWakefulness));
            pw.println("  mIsPowered=" + mIsPowered);
            pw.println();
            pw.println("Wake Locks: size=" + mWakeLocks.size());
            for (WakeLock wl : mWakeLocks) {
                pw.println("  " + wl);
            }
            pw.println();
            pw.println("Display Power: state=" + Display.stateToString(mDisplayState));
            mBatterySaverPolicy.dump(pw);
            mBatterySaverStateMachine.dump(pw);
        }
    }

    private void dumpProto(FileDescriptor fd) {
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        synchronized (mLock) {
            long token = proto.start(1146756268071L);
            proto.write(1120986464275L, 1);
            proto.write(1120986464276L, Integer.MAX_VALUE);
            proto.write(1108101562389L, 1);
            proto.write(1120986464278L, 1);
            proto.write(1172526071831L, -1L);
            proto.write(1120986464280L, Integer.MAX_VALUE);
            proto.write(1176821039133L, -1L);
            long settings = proto.start(1146756268065L);
            proto.write(1108101562372L, 0);
            proto.write(1108101562373L, 100);
            proto.write(1108101562374L, 50);
            proto.end(settings);
            proto.end(token);
            long looper = proto.start(1146756268078L);
            mHandler.getLooper().dumpDebug(proto, 1146756268078L);
            proto.end(looper);
        }
        proto.flush();
    }
}

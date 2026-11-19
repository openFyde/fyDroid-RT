package com.android.server.arc.chrome_feature_flags;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.server.SystemService;
import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.ChromeFeatureFlagsInstance;
import org.chromium.arc.mojom.FeatureFlags;
import org.chromium.mojo.system.MojoException;

/**
 * Receives Chrome feature flags via Mojo and sets system properties.
 */
public final class ArcChromeFeatureFlagsService
        implements ChromeFeatureFlagsInstance, ArcService {
    private static final String TAG = "ArcChromeFeatureFlags";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 65;
    private final Context mContext;

    public static class LifeCycle extends SystemService {
        private final ArcChromeFeatureFlagsService mService;

        public LifeCycle(Context context) {
            super(context);
            mService = new ArcChromeFeatureFlagsService(context);
        }

        @Override
        public void onStart() {
            mService.initialize();
        }
    }

    ArcChromeFeatureFlagsService(Context context) {
        mContext = context;
    }

    private void initialize() {
        Slog.i(TAG, "Initializing, requesting ARC bridge proxy");
        // Set defaults until Chrome sends flags
        setDefaultIfEmpty("persist.sys.ui.jellyColors", "0");
        setDefaultIfEmpty("persist.sys.ui.pip_double_tap", "0");
        setDefaultIfEmpty("persist.sys.ui.arc_notifications_by_chrome", "0");
        setDefaultIfEmpty("persist.sys.ui.game_dashboard", "0");
        setDefaultIfEmpty("persist.sys.ui.notification_width_increase", "0");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    // --- ChromeFeatureFlagsInstance ---

    @Override
    public void notifyFeatureFlags(FeatureFlags flags) {
        Slog.i(TAG, "Received feature flags from Chrome");
        SystemProperties.set("persist.sys.ui.jellyColors",
                flags.jellyColors ? "1" : "0");
        SystemProperties.set("persist.sys.ui.roundedWindowCompatStrategy",
                String.valueOf(flags.roundedWindowCompatStrategy));
        SystemProperties.set("persist.sys.ui.roundedWindowRadius",
                String.valueOf(flags.roundedWindowRadius));
        SystemProperties.set("persist.sys.ui.pip_double_tap",
                flags.enablePipDoubleTap ? "1" : "0");
        SystemProperties.set("persist.sys.ui.arc_notifications_by_chrome",
                flags.renderArcNotificationsByChrome ? "1" : "0");
        SystemProperties.set("persist.sys.ui.game_dashboard",
                flags.gameDashboard ? "1" : "0");
        SystemProperties.set("persist.sys.ui.notification_width_increase",
                flags.notificationWidthIncrease ? "1" : "0");
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering instance");
        host.onChromeFeatureFlagsInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    private static void setDefaultIfEmpty(String key, String defaultValue) {
        if (SystemProperties.get(key).isEmpty()) {
            SystemProperties.set(key, defaultValue);
        }
    }
}

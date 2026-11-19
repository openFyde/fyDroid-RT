package com.android.server.arc.net;

import android.content.Context;
import android.provider.Settings;
import android.util.Slog;

import com.android.server.SystemService;

import org.chromium.arc.mojom.NetworkConfiguration;
import org.chromium.arc.mojom.NetworkType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * System service that bridges Chrome OS networking into Android.
 * Loaded by SystemServer as "com.android.server.arc.net.ArcNetworkService".
 *
 * Manages:
 * - ArcNetworkBridge (Mojo NetInstance impl)
 * - ArcNetworkAgent per CrOS network interface (for ConnectivityService)
 * - ArcWifiService (binder "wifi" service)
 * - ArcWifiScannerService (binder "wifiscanner" service)
 */
public class ArcNetworkService extends SystemService {
    private static final String TAG = "ArcNetworkService";

    private ArcNetworkBridge mBridge;
    private ArcWifiService mWifiService;
    private ArcWifiScannerService mWifiScannerService;
    private ArcVpnMonitor mVpnMonitor;
    private final Map<String, ArcNetworkAgent> mAgents = new HashMap<>();

    public ArcNetworkService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        Slog.i(TAG, "ArcNetworkService starting");

        // Disable captive portal detection — Chrome OS handles this
        try {
            Settings.Global.putInt(getContext().getContentResolver(),
                    Settings.Global.CAPTIVE_PORTAL_MODE,
                    Settings.Global.CAPTIVE_PORTAL_MODE_IGNORE);
        } catch (Exception e) {
            Slog.w(TAG, "Failed to disable captive portal", e);
        }

        // Register WiFi binder services early — NetworkPolicyManagerService
        // calls WifiManager.getConnectionInfo() on any network event, and
        // it crashes with NPE if the "wifi" service isn't registered yet.
        mWifiService = new ArcWifiService(getContext(), this);
        mWifiScannerService = new ArcWifiScannerService();
        mWifiService.register();
        mWifiScannerService.register();
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == PHASE_BOOT_COMPLETED) {
            onBootCompleted();
        }
    }

    private void onBootCompleted() {
        Slog.i(TAG, "Boot completed, initializing network bridge");

        // Create and start the Mojo bridge
        mBridge = new ArcNetworkBridge(getContext(), this);
        mBridge.start();

        // Report in-container Android VPNs to Chrome as a system-level VPN
        // (Chrome/shill/patchpanel already implement their side of this).
        mVpnMonitor = new ArcVpnMonitor(getContext(), this);
        mVpnMonitor.start();
    }

    /** Called from ArcNetworkBridge when Chrome pushes network state. */
    void onActiveNetworksChanged(NetworkConfiguration[] configs) {
        Slog.i(TAG, "activeNetworksChanged: " + configs.length + " networks");

        Set<String> currentInterfaces = new HashSet<>();
        NetworkConfiguration wifiConfig = null;

        for (NetworkConfiguration config : configs) {
            String iface = config.arcNetworkInterface;
            if (iface == null || iface.isEmpty()) {
                Slog.d(TAG, "Skipping config with null/empty arcNetworkInterface");
                continue;
            }
            currentInterfaces.add(iface);

            // Track WiFi config for ArcWifiService
            if (config.type == NetworkType.WIFI) {
                wifiConfig = config;
            }

            ArcNetworkAgent agent = mAgents.get(iface);
            if (agent != null) {
                agent.update(config);
            } else {
                Slog.i(TAG, "Creating new agent for " + iface);
                agent = new ArcNetworkAgent(getContext(), config);
                mAgents.put(iface, agent);
            }
        }

        // Remove agents for interfaces that disappeared
        Set<String> removed = new HashSet<>(mAgents.keySet());
        removed.removeAll(currentInterfaces);
        for (String iface : removed) {
            Slog.i(TAG, "Removing agent for " + iface);
            ArcNetworkAgent agent = mAgents.remove(iface);
            if (agent != null) {
                agent.disconnect();
            }
        }

        // Update WiFi service with current config
        if (mWifiService != null) {
            mWifiService.onNetworkConfigChanged(wifiConfig);
        }
    }

    /** Called from ArcNetworkBridge when WiFi enabled state changes. */
    void onWifiEnabledStateChanged(boolean enabled) {
        Slog.i(TAG, "wifiEnabledStateChanged: " + enabled);
        if (mWifiService != null) {
            mWifiService.onWifiEnabledStateChanged(enabled);
        }
    }

    /** Called from ArcNetworkBridge when a scan completes. */
    void onScanCompleted() {
        Slog.i(TAG, "scanCompleted");
        if (mWifiService != null) {
            mWifiService.onScanCompleted();
        }
    }

    /** Get the Mojo bridge (for forwarding commands to Chrome OS). */
    ArcNetworkBridge getBridge() {
        return mBridge;
    }
}

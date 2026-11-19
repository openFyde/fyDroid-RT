package com.android.server.arc.net;

import android.content.Context;
import android.util.Slog;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.bridge.ArcService;
import org.chromium.arc.mojom.ArcBridgeHost;
import org.chromium.arc.mojom.ArcDnsResolutionTestResult;
import org.chromium.arc.mojom.ArcHttpTestResult;
import org.chromium.arc.mojom.ArcPingTestResult;
import org.chromium.arc.mojom.GetNetworksRequestType;
import org.chromium.arc.mojom.NetHost;
import org.chromium.arc.mojom.NetInstance;
import org.chromium.arc.mojom.NetworkConfiguration;
import org.chromium.mojo.bindings.ConnectionErrorHandler;
import org.chromium.mojo.system.MojoException;
import org.chromium.url.mojom.Url;

/**
 * Mojo bridge implementing NetInstance. Receives network state from Chrome OS
 * via activeNetworksChanged() and forwards to ArcNetworkService.
 */
class ArcNetworkBridge implements NetInstance, ArcService, ConnectionErrorHandler {
    private static final String TAG = "ArcNetworkBridge";
    private static final int MINIMUM_ARC_BRIDGE_HOST_VERSION = 0;

    private final Context mContext;
    private final ArcNetworkService mService;
    private volatile NetHost mNetHost;

    ArcNetworkBridge(Context context, ArcNetworkService service) {
        mContext = context;
        mService = service;
    }

    void start() {
        Slog.i(TAG, "Requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    NetHost getNetHost() {
        return mNetHost;
    }

    // --- NetInstance (Chrome calls these) ---

    @Override
    public void init(NetHost host, InitResponse response) {
        Slog.i(TAG, "init: NetHost connected");
        ArcBridgeSupport.buildThreadSafeProxy(host, NetHost.MANAGER, this,
                (proxy, version) -> {
                    mNetHost = proxy;
                    Slog.i(TAG, "NetHost proxy ready, version=" + version);
                    // Resync the active networks. After a system_server restart
                    // a fresh NetInstance binds against the existing NetHost,
                    // but Chrome only pushes activeNetworksChanged on state
                    // transitions -- the existing connected network never gets
                    // re-published, so ConnectivityService has no default
                    // network until the next change (a real disconnect/connect).
                    // Pull the current state ourselves on every (re)connect.
                    proxy.getNetworks(GetNetworksRequestType.ACTIVE_ONLY,
                            resp -> {
                                if (resp == null || resp.networks == null) {
                                    Slog.w(TAG, "getNetworks(ACTIVE_ONLY) returned null");
                                    return;
                                }
                                Slog.i(TAG, "init resync: " + resp.networks.length
                                        + " active networks (status=" + resp.status + ")");
                                mService.onActiveNetworksChanged(resp.networks);
                            });
                });
        response.call();
    }

    @Override
    public void activeNetworksChanged(NetworkConfiguration[] configs) {
        Slog.i(TAG, "activeNetworksChanged: " + configs.length + " networks");
        for (NetworkConfiguration c : configs) {
            Slog.i(TAG, "  iface=" + c.arcNetworkInterface
                    + " type=" + c.type
                    + " state=" + c.connectionState
                    + " default=" + c.isDefaultNetwork
                    + " ip=" + c.arcIpv4Address);
        }
        mService.onActiveNetworksChanged(configs);
    }

    @Override
    public void wifiEnabledStateChanged(boolean enabled) {
        Slog.i(TAG, "wifiEnabledStateChanged: " + enabled);
        mService.onWifiEnabledStateChanged(enabled);
    }

    @Override
    public void scanCompleted() {
        Slog.i(TAG, "scanCompleted");
        mService.onScanCompleted();
    }

    @Override
    public void setUpFlag(int flag, boolean value) {
        Slog.d(TAG, "setUpFlag: flag=" + flag + " value=" + value);
    }

    @Override
    public void configureAndroidVpn() {
        Slog.d(TAG, "configureAndroidVpn (stub)");
    }

    @Override
    public void disconnectAndroidVpn() {
        Slog.d(TAG, "disconnectAndroidVpn (stub)");
    }

    @Override
    public void pingTest(String device, String address, PingTestResponse response) {
        Slog.d(TAG, "pingTest: device=" + device + " address=" + address);
        ArcPingTestResult result = new ArcPingTestResult();
        result.isSuccessful = false;
        result.durationMs = 0;
        response.call(result);
    }

    @Override
    public void dnsResolutionTest(String device, String hostname,
            DnsResolutionTestResponse response) {
        Slog.d(TAG, "dnsResolutionTest: device=" + device + " hostname=" + hostname);
        ArcDnsResolutionTestResult result = new ArcDnsResolutionTestResult();
        result.isSuccessful = false;
        result.durationMs = 0;
        response.call(result);
    }

    @Override
    public void httpTest(String device, Url url, HttpTestResponse response) {
        Slog.d(TAG, "httpTest: device=" + device);
        ArcHttpTestResult result = new ArcHttpTestResult();
        result.isSuccessful = false;
        result.durationMs = 0;
        result.statusCode = 0;
        response.call(result);
    }

    // --- ConnectionErrorHandler ---

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mNetHost = null;
        reRequestBridge();
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mNetHost = null;
        reRequestBridge();
    }

    // --- ArcService ---

    @Override
    public void onBridgeReady(ArcBridgeHost host) {
        Slog.i(TAG, "Bridge ready, registering NetInstance");
        host.onNetInstanceReady(this);
    }

    @Override
    public void onBridgeVersionMismatch(int version) {
        Slog.e(TAG, "Bridge version mismatch: " + version
                + " < " + MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }

    private void reRequestBridge() {
        Slog.i(TAG, "Re-requesting ARC bridge proxy");
        ArcBridgeSupport.getInstance().requestArcBridgeProxyAsync(
                mContext, this, MINIMUM_ARC_BRIDGE_HOST_VERSION);
    }
}

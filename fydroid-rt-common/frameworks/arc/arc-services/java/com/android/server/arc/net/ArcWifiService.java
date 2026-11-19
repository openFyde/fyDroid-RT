package com.android.server.arc.net;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.DhcpOption;
import android.net.Network;
import android.net.wifi.CoexUnsafeChannel;
import android.net.wifi.IActionListener;
import android.net.wifi.IBooleanListener;
import android.net.wifi.ICoexCallback;
import android.net.wifi.IInterfaceCreationInfoCallback;
import android.net.wifi.ILastCallerListener;
import android.net.wifi.IOnWifiDriverCountryCodeChangedListener;
import android.net.wifi.IPnoScanResultsCallback;
import android.net.wifi.ISubsystemRestartCallback;
import android.net.wifi.ISuggestionUserApprovalStatusListener;
import android.net.wifi.IWifiVerboseLoggingStatusChangedListener;
import android.net.wifi.WifiAvailableChannel;
import android.net.wifi.WifiSsid;
import android.net.wifi.IDppCallback;
import android.net.wifi.ILocalOnlyHotspotCallback;
import android.net.wifi.INetworkRequestMatchCallback;
import android.net.wifi.IOnWifiActivityEnergyInfoListener;
import android.net.wifi.IOnWifiUsabilityStatsListener;
import android.net.wifi.IScanResultsCallback;
import android.net.wifi.ISoftApCallback;
import android.net.wifi.ISuggestionConnectionStatusListener;
import android.net.wifi.ITrafficStateCallback;
import android.net.wifi.IWifiConnectedNetworkScorer;
import android.net.wifi.IWifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.net.wifi.hotspot2.IProvisioningCallback;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.WorkSource;
import android.util.Slog;

// framework-wifi.impl jarjars com.android.modules.utils.** -> com.android.wifi.x.@0,
// so IWifiManager's ParceledListSlice return type is the prefixed class.
import com.android.wifi.x.com.android.modules.utils.ParceledListSlice;

import org.chromium.arc.mojom.ConnectionStateType;
import org.chromium.arc.mojom.NetHost;
import org.chromium.arc.mojom.NetworkConfiguration;
import org.chromium.arc.mojom.NetworkType;
import org.chromium.arc.mojom.SecurityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WiFi binder service replacing AOSP's WifiService for ARC++ container.
 * Registered as "wifi" so WifiManager works for Android apps.
 *
 * WiFi state and connection info come from Chrome OS via ArcNetworkBridge.
 * WiFi commands (scan, connect) are forwarded to Chrome OS via NetHost.
 */
class ArcWifiService extends IWifiManager.Stub {
    private static final String TAG = "ArcWifiService";

    private final Context mContext;
    private final ArcNetworkService mService;
    private volatile NetworkConfiguration mWifiConfig;
    private volatile int mWifiState = WifiManager.WIFI_STATE_ENABLED;
    private final List<ScanResult> mScanResults = new ArrayList<>();

    ArcWifiService(Context context, ArcNetworkService service) {
        mContext = context;
        mService = service;
    }

    void register() {
        ServiceManager.addService("wifi", this);
        Slog.i(TAG, "Registered 'wifi' binder service");
    }

    /** Called when Chrome pushes new WiFi NetworkConfiguration. */
    void onNetworkConfigChanged(NetworkConfiguration config) {
        mWifiConfig = config;
        if (config != null) {
            mWifiState = WifiManager.WIFI_STATE_ENABLED;
        }
    }

    /** Called when Chrome reports WiFi enabled/disabled. */
    void onWifiEnabledStateChanged(boolean enabled) {
        mWifiState = enabled
                ? WifiManager.WIFI_STATE_ENABLED
                : WifiManager.WIFI_STATE_DISABLED;
    }

    /** Called when Chrome reports scan completed. */
    void onScanCompleted() {
        // Build scan results from current WiFi config
        NetworkConfiguration config = mWifiConfig;
        if (config != null && config.wifi != null) {
            ScanResult sr = new ScanResult();
            sr.SSID = hexSsidToString(config.wifi.hexSsid);
            sr.BSSID = config.wifi.bssid;
            sr.frequency = config.wifi.frequency;
            sr.level = config.wifi.rssi;
            sr.timestamp = android.os.SystemClock.elapsedRealtime() * 1000;
            sr.capabilities = securityTypeToCapabilities(config.wifi.security);
            synchronized (mScanResults) {
                mScanResults.clear();
                mScanResults.add(sr);
            }
        }
    }

    // === IWifiManager overrides (regenerated against AOSP-13 IWifiManager.aidl) ===
    // This is a stub WifiService: WiFi state/connection come from Chrome OS via
    // ArcNetworkBridge; most controls are no-ops. Signatures must match 13 exactly.

    @Override
    public long getSupportedFeatures() {
        return 0L;
    }

    @Override
    public void getWifiActivityEnergyInfoAsync(
            IOnWifiActivityEnergyInfoListener listener) {
        // No-op
    }

    @Override
    public void setScreenOnScanSchedule(int[] scanScheduleSeconds, int[] scanType) {
        // No-op
    }

    @Override
    public void setOneShotScreenOnConnectivityScanDelayMillis(int delayMs) {
        // No-op
    }

    @Override
    public ParceledListSlice getConfiguredNetworks(String packageName, String featureId,
            boolean callerNetworksOnly) {
        // Return empty list — Chrome manages networks
        return ParceledListSlice.emptyList();
    }

    @Override
    public ParceledListSlice getPrivilegedConfiguredNetworks(String packageName,
            String featureId, Bundle extras) {
        return ParceledListSlice.emptyList();
    }

    @Override
    public WifiConfiguration getPrivilegedConnectedNetwork(String packageName,
            String featureId, Bundle extras) {
        return null;
    }

    @Override
    public Map getAllMatchingFqdnsForScanResults(List<ScanResult> scanResults) {
        return Collections.emptyMap();
    }

    @Override
    public void setSsidsAllowlist(String packageName, List<WifiSsid> ssids) {
        // No-op
    }

    @Override
    public List getSsidsAllowlist(String packageName) {
        return Collections.emptyList();
    }

    @Override
    public Map getMatchingOsuProviders(List<ScanResult> scanResults) {
        return Collections.emptyMap();
    }

    @Override
    public Map getMatchingPasspointConfigsForOsuProviders(List<OsuProvider> providers) {
        return Collections.emptyMap();
    }

    @Override
    public int addOrUpdateNetwork(WifiConfiguration config, String packageName, Bundle extras) {
        Slog.d(TAG, "addOrUpdateNetwork (stub)");
        return -1;
    }

    @Override
    public WifiManager.AddNetworkResult addOrUpdateNetworkPrivileged(WifiConfiguration config,
            String packageName) {
        return null;
    }

    @Override
    public boolean addOrUpdatePasspointConfiguration(PasspointConfiguration config,
            String packageName) {
        return false;
    }

    @Override
    public boolean removePasspointConfiguration(String fqdn, String packageName) {
        return false;
    }

    @Override
    public List<PasspointConfiguration> getPasspointConfigurations(String packageName) {
        return Collections.emptyList();
    }

    @Override
    public List<WifiConfiguration> getWifiConfigsForPasspointProfiles(List<String> fqdnList) {
        return Collections.emptyList();
    }

    @Override
    public void queryPasspointIcon(long bssid, String fileName) {
        // No-op
    }

    @Override
    public int matchProviderWithCurrentNetwork(String fqdn) {
        return 0;
    }

    @Override
    public boolean removeNetwork(int netId, String packageName) {
        return false;
    }

    @Override
    public boolean removeNonCallerConfiguredNetworks(String packageName) {
        return false;
    }

    @Override
    public boolean enableNetwork(int netId, boolean disableOthers, String packageName) {
        return false;
    }

    @Override
    public boolean disableNetwork(int netId, String packageName) {
        return false;
    }

    @Override
    public void allowAutojoinGlobal(boolean choice) {
        // No-op
    }

    @Override
    public void queryAutojoinGlobal(IBooleanListener listener) {
        // No-op
    }

    @Override
    public void allowAutojoin(int netId, boolean choice) {
        // No-op
    }

    @Override
    public void allowAutojoinPasspoint(String fqdn, boolean enableAutoJoin) {
        // No-op
    }

    @Override
    public void setMacRandomizationSettingPasspointEnabled(String fqdn, boolean enable) {
        // No-op
    }

    @Override
    public void setPasspointMeteredOverride(String fqdn, int meteredOverride) {
        // No-op
    }

    @Override
    public boolean startScan(String packageName, String featureId) {
        Slog.d(TAG, "startScan from " + packageName);
        NetHost host = getNetHost();
        if (host != null) {
            host.startScan();
            return true;
        }
        return false;
    }

    @Override
    public List<ScanResult> getScanResults(String callingPackage, String callingFeatureId) {
        synchronized (mScanResults) {
            return new ArrayList<>(mScanResults);
        }
    }

    @Override
    public boolean disconnect(String packageName) {
        return false;
    }

    @Override
    public boolean reconnect(String packageName) {
        return false;
    }

    @Override
    public boolean reassociate(String packageName) {
        return false;
    }

    @Override
    public WifiInfo getConnectionInfo(String callingPackage, String callingFeatureId) {
        WifiInfo info = createWifiInfo();
        if (info == null) {
            Slog.e(TAG, "Failed to create WifiInfo");
            return null;
        }

        NetworkConfiguration config = mWifiConfig;

        if (config != null && config.type == NetworkType.WIFI
                && (config.connectionState == ConnectionStateType.CONNECTED
                    || config.connectionState == ConnectionStateType.ONLINE)) {

            if (config.wifi != null) {
                String ssid = hexSsidToString(config.wifi.hexSsid);
                if (ssid != null) {
                    invokeHidden(info, "setSSID", createWifiSsid(ssid));
                }
                invokeHidden(info, "setBSSID", config.wifi.bssid);
                invokeHidden(info, "setFrequency", config.wifi.frequency);
                invokeHidden(info, "setRssi", (int) config.wifi.rssi);
            }

            if (config.linkSpeed != null && config.linkSpeed.downlinkSpeedKbps > 0) {
                invokeHidden(info, "setLinkSpeed", config.linkSpeed.downlinkSpeedKbps / 1000);
            }

            if (config.arcIpv4Address != null) {
                invokeHidden(info, "setInetAddress",
                        parseInetAddress(config.arcIpv4Address));
            }

            invokeHidden(info, "setNetworkId", 1);
            invokeHidden(info, "setSupplicantState",
                    android.net.wifi.SupplicantState.COMPLETED);
        } else {
            invokeHidden(info, "setSupplicantState",
                    android.net.wifi.SupplicantState.DISCONNECTED);
        }

        return info;
    }

    @Override
    public boolean setWifiEnabled(String packageName, boolean enable) {
        Slog.i(TAG, "setWifiEnabled: " + enable + " from " + packageName);
        NetHost host = getNetHost();
        if (host != null) {
            host.setWifiEnabledState(enable, (success) -> {
                Slog.i(TAG, "setWifiEnabledState response: " + success);
            });
            return true;
        }
        return false;
    }

    @Override
    public int getWifiEnabledState() {
        return mWifiState;
    }

    @Override
    public void registerDriverCountryCodeChangedListener(
            IOnWifiDriverCountryCodeChangedListener listener, String packageName,
            String featureId) {
        // No-op
    }

    @Override
    public void unregisterDriverCountryCodeChangedListener(
            IOnWifiDriverCountryCodeChangedListener listener) {
        // No-op
    }

    @Override
    public String getCountryCode(String packageName, String featureId) {
        return "US";
    }

    @Override
    public void setOverrideCountryCode(String country) {
        // No-op
    }

    @Override
    public void clearOverrideCountryCode() {
        // No-op
    }

    @Override
    public void setDefaultCountryCode(String country) {
        // No-op
    }

    @Override
    public boolean is24GHzBandSupported() {
        return true;
    }

    @Override
    public boolean is5GHzBandSupported() {
        return true;
    }

    @Override
    public boolean is6GHzBandSupported() {
        return false;
    }

    @Override
    public boolean is60GHzBandSupported() {
        return false;
    }

    @Override
    public boolean isWifiStandardSupported(int standard) {
        return false;
    }

    @Override
    public DhcpInfo getDhcpInfo(String packageName) {
        DhcpInfo info = new DhcpInfo();
        NetworkConfiguration config = mWifiConfig;
        if (config != null) {
            if (config.arcIpv4Address != null) {
                info.ipAddress = inetAddressToInt(config.arcIpv4Address);
            }
            if (config.arcIpv4Gateway != null) {
                info.gateway = inetAddressToInt(config.arcIpv4Gateway);
            }
            if (config.hostDnsAddresses != null && config.hostDnsAddresses.length > 0) {
                info.dns1 = inetAddressToInt(config.hostDnsAddresses[0]);
                if (config.hostDnsAddresses.length > 1) {
                    info.dns2 = inetAddressToInt(config.hostDnsAddresses[1]);
                }
            }
            if (config.arcIpv4PrefixLength > 0) {
                info.netmask = prefixLengthToNetmask(config.arcIpv4PrefixLength);
            }
        }
        return info;
    }

    @Override
    public void setScanAlwaysAvailable(boolean isAvailable, String packageName) {
        // No-op
    }

    @Override
    public boolean isScanAlwaysAvailable() {
        return false;
    }

    @Override
    public boolean acquireWifiLock(IBinder lock, int lockType, String tag, WorkSource ws) {
        return true;
    }

    @Override
    public void updateWifiLockWorkSource(IBinder lock, WorkSource ws) {
        // No-op
    }

    @Override
    public boolean releaseWifiLock(IBinder lock) {
        return true;
    }

    @Override
    public void initializeMulticastFiltering() {
        // No-op
    }

    @Override
    public boolean isMulticastEnabled() {
        return true;
    }

    @Override
    public void acquireMulticastLock(IBinder binder, String tag) {
        // No-op
    }

    @Override
    public void releaseMulticastLock(String tag) {
        // No-op
    }

    @Override
    public void updateInterfaceIpState(String ifaceName, int mode) {
        // No-op
    }

    @Override
    public boolean isDefaultCoexAlgorithmEnabled() {
        return false;
    }

    @Override
    public void setCoexUnsafeChannels(List<CoexUnsafeChannel> unsafeChannels,
            int mandatoryRestrictions) {
        // No-op
    }

    @Override
    public void registerCoexCallback(ICoexCallback callback) {
        // No-op
    }

    @Override
    public void unregisterCoexCallback(ICoexCallback callback) {
        // No-op
    }

    @Override
    public boolean startSoftAp(WifiConfiguration wifiConfig, String packageName) {
        return false;
    }

    @Override
    public boolean startTetheredHotspot(SoftApConfiguration softApConfig, String packageName) {
        return false;
    }

    @Override
    public boolean stopSoftAp() {
        return false;
    }

    @Override
    public int startLocalOnlyHotspot(ILocalOnlyHotspotCallback callback,
            String packageName, String featureId, SoftApConfiguration customConfig,
            Bundle extras) {
        return WifiManager.LocalOnlyHotspotCallback.ERROR_GENERIC;
    }

    @Override
    public void stopLocalOnlyHotspot() {
        // No-op
    }

    @Override
    public void registerLocalOnlyHotspotSoftApCallback(ISoftApCallback callback, Bundle extras) {
        // No-op
    }

    @Override
    public void unregisterLocalOnlyHotspotSoftApCallback(ISoftApCallback callback, Bundle extras) {
        // No-op
    }

    @Override
    public void startWatchLocalOnlyHotspot(ILocalOnlyHotspotCallback callback) {
        // No-op
    }

    @Override
    public void stopWatchLocalOnlyHotspot() {
        // No-op
    }

    @Override
    public int getWifiApEnabledState() {
        return WifiManager.WIFI_AP_STATE_DISABLED;
    }

    @Override
    public WifiConfiguration getWifiApConfiguration() {
        return null;
    }

    @Override
    public SoftApConfiguration getSoftApConfiguration() {
        return null;
    }

    @Override
    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig,
            String packageName) {
        return false;
    }

    @Override
    public boolean setSoftApConfiguration(SoftApConfiguration softApConfig,
            String packageName) {
        return false;
    }

    @Override
    public void notifyUserOfApBandConversion(String packageName) {
        // No-op
    }

    @Override
    public void enableTdls(String remoteIPAddress, boolean enable) {
        // No-op
    }

    @Override
    public void enableTdlsWithMacAddress(String remoteMacAddress, boolean enable) {
        // No-op
    }

    @Override
    public String getCurrentNetworkWpsNfcConfigurationToken() {
        return null;
    }

    @Override
    public void enableVerboseLogging(int verbose) {
        // No-op
    }

    @Override
    public int getVerboseLoggingLevel() {
        return 0;
    }

    @Override
    public void disableEphemeralNetwork(String SSID, String packageName) {
        // No-op
    }

    @Override
    public void factoryReset(String packageName) {
        // No-op
    }

    @Override
    public Network getCurrentNetwork() {
        return null;
    }

    @Override
    public byte[] retrieveBackupData() {
        return new byte[0];
    }

    @Override
    public void restoreBackupData(byte[] data) {
        // No-op
    }

    @Override
    public byte[] retrieveSoftApBackupData() {
        return new byte[0];
    }

    @Override
    public SoftApConfiguration restoreSoftApBackupData(byte[] data) {
        return null;
    }

    @Override
    public void restoreSupplicantBackupData(byte[] supplicantData, byte[] ipConfigData) {
        // No-op
    }

    @Override
    public void startSubscriptionProvisioning(OsuProvider provider,
            IProvisioningCallback callback) {
        // No-op
    }

    @Override
    public void registerSoftApCallback(ISoftApCallback callback) {
        // No-op
    }

    @Override
    public void unregisterSoftApCallback(ISoftApCallback callback) {
        // No-op
    }

    @Override
    public void addWifiVerboseLoggingStatusChangedListener(
            IWifiVerboseLoggingStatusChangedListener listener) {
        // No-op
    }

    @Override
    public void removeWifiVerboseLoggingStatusChangedListener(
            IWifiVerboseLoggingStatusChangedListener listener) {
        // No-op
    }

    @Override
    public void addOnWifiUsabilityStatsListener(IOnWifiUsabilityStatsListener listener) {
        // No-op
    }

    @Override
    public void removeOnWifiUsabilityStatsListener(IOnWifiUsabilityStatsListener listener) {
        // No-op
    }

    @Override
    public void registerTrafficStateCallback(ITrafficStateCallback callback) {
        // No-op
    }

    @Override
    public void unregisterTrafficStateCallback(ITrafficStateCallback callback) {
        // No-op
    }

    @Override
    public void registerNetworkRequestMatchCallback(INetworkRequestMatchCallback callback) {
        // No-op
    }

    @Override
    public void unregisterNetworkRequestMatchCallback(INetworkRequestMatchCallback callback) {
        // No-op
    }

    @Override
    public int addNetworkSuggestions(List<WifiNetworkSuggestion> suggestions,
            String packageName, String featureId) {
        return WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS;
    }

    @Override
    public int removeNetworkSuggestions(List<WifiNetworkSuggestion> suggestions,
            String packageName, int action) {
        return WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS;
    }

    @Override
    public List<WifiNetworkSuggestion> getNetworkSuggestions(String packageName) {
        return Collections.emptyList();
    }

    @Override
    public String[] getFactoryMacAddresses() {
        return new String[0];
    }

    @Override
    public void setDeviceMobilityState(int state) {
        // No-op
    }

    @Override
    public void startDppAsConfiguratorInitiator(IBinder binder, String packageName,
            String enrolleeUri, int selectedNetworkId, int netRole, IDppCallback callback) {
        // No-op
    }

    @Override
    public void startDppAsEnrolleeInitiator(IBinder binder, String configuratorUri,
            IDppCallback callback) {
        // No-op
    }

    @Override
    public void startDppAsEnrolleeResponder(IBinder binder, String deviceInfo, int curve,
            IDppCallback callback) {
        // No-op
    }

    @Override
    public void stopDppSession() {
        // No-op
    }

    @Override
    public void updateWifiUsabilityScore(int seqNum, int score,
            int predictionHorizonSec) {
        // No-op
    }

    @Override
    public void connect(WifiConfiguration config, int netId, IActionListener listener,
            String packageName) {
        Slog.d(TAG, "connect netId=" + netId);
        try {
            if (listener != null) {
                listener.onSuccess();
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "connect callback failed", e);
        }
    }

    @Override
    public void save(WifiConfiguration config, IActionListener listener, String packageName) {
        try {
            if (listener != null) {
                listener.onSuccess();
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "save callback failed", e);
        }
    }

    @Override
    public void forget(int netId, IActionListener listener) {
        try {
            if (listener != null) {
                listener.onSuccess();
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "forget callback failed", e);
        }
    }

    @Override
    public void registerScanResultsCallback(IScanResultsCallback callback) {
        // No-op — scan results available via getScanResults()
    }

    @Override
    public void unregisterScanResultsCallback(IScanResultsCallback callback) {
        // No-op
    }

    @Override
    public void registerSuggestionConnectionStatusListener(
            ISuggestionConnectionStatusListener listener,
            String packageName, String featureId) {
        // No-op
    }

    @Override
    public void unregisterSuggestionConnectionStatusListener(
            ISuggestionConnectionStatusListener listener, String packageName) {
        // No-op
    }

    @Override
    public int calculateSignalLevel(int rssi) {
        return WifiManager.calculateSignalLevel(rssi, 5);
    }

    @Override
    public List<WifiConfiguration> getWifiConfigForMatchedNetworkSuggestionsSharedWithUser(
            List<ScanResult> scanResults) {
        return Collections.emptyList();
    }

    @Override
    public boolean setWifiConnectedNetworkScorer(IBinder binder,
            IWifiConnectedNetworkScorer scorer) {
        return false;
    }

    @Override
    public void clearWifiConnectedNetworkScorer() {
        // No-op
    }

    @Override
    public void setExternalPnoScanRequest(IBinder binder, IPnoScanResultsCallback callback,
            List<WifiSsid> ssids, int[] frequencies, String packageName, String featureId) {
        // No-op
    }

    @Override
    public void clearExternalPnoScanRequest() {
        // No-op
    }

    @Override
    public void getLastCallerInfoForApi(int api, ILastCallerListener listener) {
        // No-op
    }

    @Override
    public Map getMatchingScanResults(List<WifiNetworkSuggestion> suggestions,
            List<ScanResult> scanResults, String callingPackage,
            String callingFeatureId) {
        return Collections.emptyMap();
    }

    @Override
    public void setScanThrottleEnabled(boolean enable) {
        // No-op
    }

    @Override
    public boolean isScanThrottleEnabled() {
        return false;
    }

    @Override
    public Map getAllMatchingPasspointProfilesForScanResults(
            List<ScanResult> scanResults) {
        return Collections.emptyMap();
    }

    @Override
    public void setAutoWakeupEnabled(boolean enable) {
        // No-op
    }

    @Override
    public boolean isAutoWakeupEnabled() {
        return false;
    }

    @Override
    public void startRestrictingAutoJoinToSubscriptionId(int subId) {
        // No-op
    }

    @Override
    public void stopRestrictingAutoJoinToSubscriptionId() {
        // No-op
    }

    @Override
    public void setCarrierNetworkOffloadEnabled(int subscriptionId, boolean merged,
            boolean enabled) {
        // No-op
    }

    @Override
    public boolean isCarrierNetworkOffloadEnabled(int subscriptionId, boolean merged) {
        return false;
    }

    @Override
    public void registerSubsystemRestartCallback(ISubsystemRestartCallback callback) {
        // No-op
    }

    @Override
    public void unregisterSubsystemRestartCallback(ISubsystemRestartCallback callback) {
        // No-op
    }

    @Override
    public void restartWifiSubsystem() {
        // No-op
    }

    @Override
    public void addSuggestionUserApprovalStatusListener(
            ISuggestionUserApprovalStatusListener listener, String packageName) {
        // No-op
    }

    @Override
    public void removeSuggestionUserApprovalStatusListener(
            ISuggestionUserApprovalStatusListener listener, String packageName) {
        // No-op
    }

    @Override
    public void setEmergencyScanRequestInProgress(boolean inProgress) {
        // No-op
    }

    @Override
    public void removeAppState(int targetAppUid, String targetApppackageName) {
        // No-op
    }

    @Override
    public boolean setWifiScoringEnabled(boolean enabled) {
        return false;
    }

    @Override
    public void flushPasspointAnqpCache(String packageName) {
        // No-op
    }

    @Override
    public List<WifiAvailableChannel> getUsableChannels(int band, int mode, int filter) {
        return Collections.emptyList();
    }

    @Override
    public boolean isWifiPasspointEnabled() {
        return false;
    }

    @Override
    public void setWifiPasspointEnabled(boolean enabled) {
        // No-op
    }

    @Override
    public int getStaConcurrencyForMultiInternetMode() {
        return 0;
    }

    @Override
    public boolean setStaConcurrencyForMultiInternetMode(int mode) {
        return false;
    }

    @Override
    public void notifyMinimumRequiredWifiSecurityLevelChanged(int level) {
        // No-op
    }

    @Override
    public void notifyWifiSsidPolicyChanged(int policyType, List<WifiSsid> ssids) {
        // No-op
    }

    @Override
    public String[] getOemPrivilegedWifiAdminPackages() {
        return new String[0];
    }

    @Override
    public void replyToP2pInvitationReceivedDialog(int dialogId, boolean accepted,
            String optionalPin) {
        // No-op
    }

    @Override
    public void replyToSimpleDialog(int dialogId, int reply) {
        // No-op
    }

    @Override
    public void addCustomDhcpOptions(WifiSsid ssid, byte[] oui, List<DhcpOption> options) {
        // No-op
    }

    @Override
    public void removeCustomDhcpOptions(WifiSsid ssid, byte[] oui) {
        // No-op
    }

    @Override
    public void reportCreateInterfaceImpact(String packageName, int interfaceType,
            boolean requireNewInterface, IInterfaceCreationInfoCallback callback) {
        // No-op
    }

    // === Reflection helpers (WifiInfo setters are hidden) ===

    private static WifiInfo createWifiInfo() {
        try {
            java.lang.reflect.Constructor<WifiInfo> ctor =
                    WifiInfo.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception e) {
            Slog.w(TAG, "Failed to create WifiInfo via reflection", e);
            return null;
        }
    }

    private static Object createWifiSsid(String ssid) {
        try {
            Class<?> cls = Class.forName("android.net.wifi.WifiSsid");
            java.lang.reflect.Method m = cls.getMethod(
                    "createFromAsciiEncoded", String.class);
            return m.invoke(null, ssid);
        } catch (Exception e) {
            Slog.w(TAG, "Failed to create WifiSsid", e);
            return null;
        }
    }

    private static void invokeHidden(Object obj, String methodName, Object arg) {
        if (arg == null) return;
        try {
            for (java.lang.reflect.Method m : obj.getClass().getDeclaredMethods()) {
                if (m.getName().equals(methodName) && m.getParameterCount() == 1) {
                    Class<?> paramType = m.getParameterTypes()[0];
                    if (paramType.isInstance(arg)
                            || (paramType == int.class && arg instanceof Integer)
                            || (paramType == boolean.class && arg instanceof Boolean)) {
                        m.setAccessible(true);
                        m.invoke(obj, arg);
                        return;
                    }
                }
            }
            Slog.w(TAG, "No matching method: " + methodName + "(" + arg.getClass().getSimpleName() + ")");
        } catch (Exception e) {
            Slog.w(TAG, "Failed to invoke " + methodName, e);
        }
    }

    // === Helpers ===

    private static String securityTypeToCapabilities(int security) {
        switch (security) {
            case SecurityType.WPA_PSK:
                return "[WPA-PSK-CCMP][RSN-PSK-CCMP][ESS]";
            case SecurityType.WPA_EAP:
                return "[WPA-EAP-CCMP][RSN-EAP-CCMP][ESS]";
            case SecurityType.WEP_PSK:
            case SecurityType.WEP_8021X:
                return "[WEP][ESS]";
            case SecurityType.NONE:
            default:
                return "[ESS]";
        }
    }

    private NetHost getNetHost() {
        ArcNetworkBridge bridge = mService.getBridge();
        return bridge != null ? bridge.getNetHost() : null;
    }

    private static String hexSsidToString(String hexSsid) {
        if (hexSsid == null || hexSsid.isEmpty()) return null;
        try {
            byte[] bytes = hexStringToByteArray(hexSsid);
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return hexSsid;
        }
    }

    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private static java.net.InetAddress parseInetAddress(String addr) {
        if (addr == null || addr.isEmpty()) return null;
        try {
            return java.net.InetAddress.getByName(addr);
        } catch (java.net.UnknownHostException e) {
            return null;
        }
    }

    private static int inetAddressToInt(String addr) {
        if (addr == null || addr.isEmpty()) return 0;
        try {
            java.net.InetAddress a = java.net.InetAddress.getByName(addr);
            byte[] b = a.getAddress();
            if (b.length == 4) {
                return (b[0] & 0xFF) | ((b[1] & 0xFF) << 8)
                        | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);
            }
        } catch (java.net.UnknownHostException e) {
            // ignore
        }
        return 0;
    }

    private static int prefixLengthToNetmask(int prefixLength) {
        if (prefixLength <= 0 || prefixLength > 32) return 0;
        int mask = 0xFFFFFFFF << (32 - prefixLength);
        // Reverse byte order for DhcpInfo (little-endian int)
        return Integer.reverseBytes(mask);
    }
}

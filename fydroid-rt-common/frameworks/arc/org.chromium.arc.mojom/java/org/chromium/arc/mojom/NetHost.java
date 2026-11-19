// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface NetHost extends Interface {
    public static final Interface.Manager<NetHost, Proxy> MANAGER = NetHost_Internal.MANAGER;

    public interface CreateNetworkResponse extends Callbacks.Callback1<String> {
    }

    public interface ForgetNetworkResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetNetworksResponse extends Callbacks.Callback1<GetNetworksResponseType> {
    }

    public interface GetWifiEnabledStateResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends NetHost, Interface.Proxy {
    }

    public interface RequestPasspointAppApprovalResponse extends Callbacks.Callback1<PasspointApprovalResponse> {
    }

    public interface SetWifiEnabledStateResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface StartConnectResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StartDisconnectResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StartLohsResponse extends Callbacks.Callback1<Integer> {
    }

    public interface UpdateWifiNetworkResponse extends Callbacks.Callback1<Integer> {
    }

    void addPasspointCredentials(PasspointCredentials passpointCredentials);

    void androidVpnConnected(AndroidVpnConfiguration androidVpnConfiguration);

    void androidVpnDisconnected();

    void androidVpnUpdated(AndroidVpnConfiguration androidVpnConfiguration);

    void createNetwork(WifiConfiguration wifiConfiguration, CreateNetworkResponse createNetworkResponse);

    void deprecatedAndroidVpnStateChanged(int i);

    void disconnectHostVpn();

    void forgetNetwork(String str, ForgetNetworkResponse forgetNetworkResponse);

    void getNetworks(int i, GetNetworksResponse getNetworksResponse);

    void getWifiEnabledState(GetWifiEnabledStateResponse getWifiEnabledStateResponse);

    void notifyAndroidWifiMulticastLockChange(boolean z);

    void notifyArcvpnSocketConnectionEvent(SocketConnectionEvent socketConnectionEvent);

    void notifySocketConnectionEvent(SocketConnectionEvent socketConnectionEvent);

    void removePasspointCredentials(PasspointRemovalProperties passpointRemovalProperties);

    void requestPasspointAppApproval(PasspointApprovalRequest passpointApprovalRequest, RequestPasspointAppApprovalResponse requestPasspointAppApprovalResponse);

    void setAlwaysOnVpn(String str, boolean z);

    void setWifiEnabledState(boolean z, SetWifiEnabledStateResponse setWifiEnabledStateResponse);

    void startConnect(String str, StartConnectResponse startConnectResponse);

    void startDisconnect(String str, StartDisconnectResponse startDisconnectResponse);

    void startLohs(LohsConfig lohsConfig, StartLohsResponse startLohsResponse);

    void startScan();

    void stopLohs();

    void updateWifiNetwork(String str, WifiConfiguration wifiConfiguration, UpdateWifiNetworkResponse updateWifiNetworkResponse);
}

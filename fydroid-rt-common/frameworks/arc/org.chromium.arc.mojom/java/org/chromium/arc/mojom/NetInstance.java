// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.url.mojom.Url;

public interface NetInstance extends Interface {
    public static final Interface.Manager<NetInstance, Proxy> MANAGER = NetInstance_Internal.MANAGER;

    public interface DnsResolutionTestResponse extends Callbacks.Callback1<ArcDnsResolutionTestResult> {
    }

    public interface HttpTestResponse extends Callbacks.Callback1<ArcHttpTestResult> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface PingTestResponse extends Callbacks.Callback1<ArcPingTestResult> {
    }

    public interface Proxy extends NetInstance, Interface.Proxy {
    }

    void activeNetworksChanged(NetworkConfiguration[] networkConfigurationArr);

    void configureAndroidVpn();

    void disconnectAndroidVpn();

    void dnsResolutionTest(String str, String str2, DnsResolutionTestResponse dnsResolutionTestResponse);

    void httpTest(String str, Url url, HttpTestResponse httpTestResponse);

    void init(NetHost netHost, InitResponse initResponse);

    void pingTest(String str, String str2, PingTestResponse pingTestResponse);

    void scanCompleted();

    void setUpFlag(int i, boolean z);

    void wifiEnabledStateChanged(boolean z);
}

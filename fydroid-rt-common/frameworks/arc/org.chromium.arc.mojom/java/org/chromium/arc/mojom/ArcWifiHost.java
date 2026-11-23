// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ArcWifiHost extends Interface {
    public static final Interface.Manager<ArcWifiHost, Proxy> MANAGER = ArcWifiHost_Internal.MANAGER;

    public interface GetScanResultsResponse extends Callbacks.Callback1<WifiScanResult[]> {
    }

    public interface GetWifiEnabledStateResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends ArcWifiHost, Interface.Proxy {
    }

    public interface SetWifiEnabledStateResponse extends Callbacks.Callback1<Boolean> {
    }

    void getScanResults(GetScanResultsResponse getScanResultsResponse);

    void getWifiEnabledState(GetWifiEnabledStateResponse getWifiEnabledStateResponse);

    void setWifiEnabledState(boolean z, SetWifiEnabledStateResponse setWifiEnabledStateResponse);

    void startScan();
}

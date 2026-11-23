// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface MidisServer extends Interface {
    public static final Interface.Manager<MidisServer, Proxy> MANAGER = MidisServer_Internal.MANAGER;

    public interface ListDevicesResponse extends Callbacks.Callback1<MidisDeviceInfo[]> {
    }

    public interface Proxy extends MidisServer, Interface.Proxy {
    }

    public interface RequestPortResponse extends Callbacks.Callback1<UntypedHandle> {
    }

    void closeDevice(MidisRequest midisRequest);

    void listDevices(ListDevicesResponse listDevicesResponse);

    void requestPort(MidisRequest midisRequest, RequestPortResponse requestPortResponse);
}

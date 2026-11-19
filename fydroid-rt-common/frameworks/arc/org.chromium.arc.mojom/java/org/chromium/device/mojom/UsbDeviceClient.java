// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.device.mojom;

import org.chromium.mojo.bindings.Interface;

public interface UsbDeviceClient extends Interface {
    public static final Interface.Manager<UsbDeviceClient, Proxy> MANAGER = UsbDeviceClient_Internal.MANAGER;

    public interface Proxy extends UsbDeviceClient, Interface.Proxy {
    }

    void onDeviceClosed();

    void onDeviceOpened();
}

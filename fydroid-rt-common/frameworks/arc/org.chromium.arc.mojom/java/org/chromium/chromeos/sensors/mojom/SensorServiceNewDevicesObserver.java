// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SensorServiceNewDevicesObserver extends Interface {
    public static final Interface.Manager<SensorServiceNewDevicesObserver, Proxy> MANAGER = SensorServiceNewDevicesObserver_Internal.MANAGER;

    public interface Proxy extends SensorServiceNewDevicesObserver, Interface.Proxy {
    }

    void onNewDeviceAdded(int i, int[] iArr);
}

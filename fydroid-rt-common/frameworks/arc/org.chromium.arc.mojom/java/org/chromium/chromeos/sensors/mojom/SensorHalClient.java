// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SensorHalClient extends Interface {
    public static final Interface.Manager<SensorHalClient, Proxy> MANAGER = SensorHalClient_Internal.MANAGER;

    public interface Proxy extends SensorHalClient, Interface.Proxy {
    }

    void setUpChannel(SensorService sensorService);
}

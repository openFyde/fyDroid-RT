// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.chromeos.sensors.mojom.SensorHalClient;
import org.chromium.mojo.bindings.Interface;

public interface IioSensorHost extends Interface {
    public static final Interface.Manager<IioSensorHost, Proxy> MANAGER = IioSensorHost_Internal.MANAGER;

    public interface Proxy extends IioSensorHost, Interface.Proxy {
    }

    void registerSensorHalClient(SensorHalClient sensorHalClient);
}

// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface SensorHalServer extends Interface {
    public static final Interface.Manager<SensorHalServer, Proxy> MANAGER = SensorHalServer_Internal.MANAGER;

    public interface Proxy extends SensorHalServer, Interface.Proxy {
    }

    void createChannel(InterfaceRequest<SensorService> interfaceRequest);
}

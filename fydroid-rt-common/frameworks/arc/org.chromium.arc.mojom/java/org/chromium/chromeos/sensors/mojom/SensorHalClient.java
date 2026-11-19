package org.chromium.chromeos.sensors.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SensorHalClient extends Interface {
    public static final Interface.Manager<SensorHalClient, Proxy> MANAGER = SensorHalClient_Internal.MANAGER;

    public interface Proxy extends SensorHalClient, Interface.Proxy {
    }

    void setUpChannel(SensorService sensorService);
}

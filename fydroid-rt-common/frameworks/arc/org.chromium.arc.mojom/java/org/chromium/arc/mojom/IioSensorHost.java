package org.chromium.arc.mojom;

import org.chromium.chromeos.sensors.mojom.SensorHalClient;
import org.chromium.mojo.bindings.Interface;

public interface IioSensorHost extends Interface {
    public static final Interface.Manager<IioSensorHost, Proxy> MANAGER = IioSensorHost_Internal.MANAGER;

    public interface Proxy extends IioSensorHost, Interface.Proxy {
    }

    void registerSensorHalClient(SensorHalClient sensorHalClient);
}

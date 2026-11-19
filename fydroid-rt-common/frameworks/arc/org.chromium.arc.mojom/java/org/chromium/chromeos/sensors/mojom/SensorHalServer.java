package org.chromium.chromeos.sensors.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface SensorHalServer extends Interface {
    public static final Interface.Manager<SensorHalServer, Proxy> MANAGER = SensorHalServer_Internal.MANAGER;

    public interface Proxy extends SensorHalServer, Interface.Proxy {
    }

    void createChannel(InterfaceRequest<SensorService> interfaceRequest);
}

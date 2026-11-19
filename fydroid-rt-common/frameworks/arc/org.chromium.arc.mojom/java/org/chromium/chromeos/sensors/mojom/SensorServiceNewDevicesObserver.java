package org.chromium.chromeos.sensors.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SensorServiceNewDevicesObserver extends Interface {
    public static final Interface.Manager<SensorServiceNewDevicesObserver, Proxy> MANAGER = SensorServiceNewDevicesObserver_Internal.MANAGER;

    public interface Proxy extends SensorServiceNewDevicesObserver, Interface.Proxy {
    }

    void onNewDeviceAdded(int i, int[] iArr);
}

package org.chromium.chromeos.sensors.mojom;

import java.util.Map;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface SensorService extends Interface {
    public static final Interface.Manager<SensorService, Proxy> MANAGER = SensorService_Internal.MANAGER;

    public interface GetAllDeviceIdsResponse extends Callbacks.Callback1<Map<Integer, int[]>> {
    }

    public interface GetDeviceIdsResponse extends Callbacks.Callback1<int[]> {
    }

    public interface Proxy extends SensorService, Interface.Proxy {
    }

    void getAllDeviceIds(GetAllDeviceIdsResponse getAllDeviceIdsResponse);

    void getDevice(int i, InterfaceRequest<SensorDevice> interfaceRequest);

    void getDeviceIds(int i, GetDeviceIdsResponse getDeviceIdsResponse);

    void registerNewDevicesObserver(SensorServiceNewDevicesObserver sensorServiceNewDevicesObserver);
}

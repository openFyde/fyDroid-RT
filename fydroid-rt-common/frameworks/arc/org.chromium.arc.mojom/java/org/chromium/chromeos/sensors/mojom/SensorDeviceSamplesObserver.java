package org.chromium.chromeos.sensors.mojom;

import java.util.Map;
import org.chromium.mojo.bindings.Interface;

public interface SensorDeviceSamplesObserver extends Interface {
    public static final Interface.Manager<SensorDeviceSamplesObserver, Proxy> MANAGER = SensorDeviceSamplesObserver_Internal.MANAGER;

    public interface Proxy extends SensorDeviceSamplesObserver, Interface.Proxy {
    }

    void onErrorOccurred(int i);

    void onSampleUpdated(Map<Integer, Long> map);
}

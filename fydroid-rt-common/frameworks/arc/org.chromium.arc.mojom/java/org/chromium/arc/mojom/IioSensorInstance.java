package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface IioSensorInstance extends Interface {
    public static final Interface.Manager<IioSensorInstance, Proxy> MANAGER = IioSensorInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends IioSensorInstance, Interface.Proxy {
    }

    void init(IioSensorHost iioSensorHost, InitResponse initResponse);

    void onTabletModeChanged(boolean z);
}

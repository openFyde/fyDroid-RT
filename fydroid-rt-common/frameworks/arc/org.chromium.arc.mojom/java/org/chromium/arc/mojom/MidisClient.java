package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface MidisClient extends Interface {
    public static final Interface.Manager<MidisClient, Proxy> MANAGER = MidisClient_Internal.MANAGER;

    public interface Proxy extends MidisClient, Interface.Proxy {
    }

    void onDeviceAdded(MidisDeviceInfo midisDeviceInfo);

    void onDeviceRemoved(MidisDeviceInfo midisDeviceInfo);
}

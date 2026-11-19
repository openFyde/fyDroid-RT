package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.Interface;

public interface CameraHalClient extends Interface {
    public static final Interface.Manager<CameraHalClient, Proxy> MANAGER = CameraHalClient_Internal.MANAGER;

    public interface Proxy extends CameraHalClient, Interface.Proxy {
    }

    void setUpChannel(CameraModule cameraModule);
}

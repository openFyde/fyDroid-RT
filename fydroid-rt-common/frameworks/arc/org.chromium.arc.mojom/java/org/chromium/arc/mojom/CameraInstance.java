package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface CameraInstance extends Interface {
    public static final Interface.Manager<CameraInstance, Proxy> MANAGER = CameraInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends CameraInstance, Interface.Proxy {
    }

    void init(CameraHost cameraHost, InitResponse initResponse);
}

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AppfuseInstance extends Interface {
    public static final Interface.Manager<AppfuseInstance, Proxy> MANAGER = AppfuseInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends AppfuseInstance, Interface.Proxy {
    }

    void init(AppfuseHost appfuseHost, InitResponse initResponse);
}

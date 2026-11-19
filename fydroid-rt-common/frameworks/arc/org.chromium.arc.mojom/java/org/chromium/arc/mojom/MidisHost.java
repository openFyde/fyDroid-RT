package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface MidisHost extends Interface {
    public static final Interface.Manager<MidisHost, Proxy> MANAGER = MidisHost_Internal.MANAGER;

    public interface Proxy extends MidisHost, Interface.Proxy {
    }

    void connect(InterfaceRequest<MidisServer> interfaceRequest, MidisClient midisClient);
}

package org.chromium.IPC.mojom;

import org.chromium.mojo.bindings.Interface;

public interface GenericInterface extends Interface {
    public static final Interface.Manager<GenericInterface, Proxy> MANAGER = GenericInterface_Internal.MANAGER;

    public interface Proxy extends GenericInterface, Interface.Proxy {
    }
}

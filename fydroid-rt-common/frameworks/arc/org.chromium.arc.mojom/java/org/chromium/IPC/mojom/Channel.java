package org.chromium.IPC.mojom;

import org.chromium.mojo.bindings.AssociatedInterfaceRequestNotSupported;
import org.chromium.mojo.bindings.Interface;

public interface Channel extends Interface {
    public static final Interface.Manager<Channel, Proxy> MANAGER = Channel_Internal.MANAGER;

    public interface Proxy extends Channel, Interface.Proxy {
    }

    void getAssociatedInterface(String str, AssociatedInterfaceRequestNotSupported associatedInterfaceRequestNotSupported);

    void receive(Message message);

    void setPeerPid(int i);
}

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface OemCryptoHost extends Interface {
    public static final Interface.Manager<OemCryptoHost, Proxy> MANAGER = OemCryptoHost_Internal.MANAGER;

    public interface Proxy extends OemCryptoHost, Interface.Proxy {
    }

    void connect(InterfaceRequest<OemCryptoService> interfaceRequest);
}

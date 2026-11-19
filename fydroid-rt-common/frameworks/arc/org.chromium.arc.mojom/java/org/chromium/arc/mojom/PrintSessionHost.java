package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface PrintSessionHost extends Interface {
    public static final Interface.Manager<PrintSessionHost, Proxy> MANAGER = PrintSessionHost_Internal.MANAGER;

    public interface Proxy extends PrintSessionHost, Interface.Proxy {
    }
}

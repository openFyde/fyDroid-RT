package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface PrintSpoolerInstance extends Interface {
    public static final Interface.Manager<PrintSpoolerInstance, Proxy> MANAGER = PrintSpoolerInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends PrintSpoolerInstance, Interface.Proxy {
    }

    void init(PrintSpoolerHost printSpoolerHost, InitResponse initResponse);
}

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface OemCryptoInstance extends Interface {
    public static final Interface.Manager<OemCryptoInstance, Proxy> MANAGER = OemCryptoInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends OemCryptoInstance, Interface.Proxy {
    }

    void init(OemCryptoHost oemCryptoHost, InitResponse initResponse);
}

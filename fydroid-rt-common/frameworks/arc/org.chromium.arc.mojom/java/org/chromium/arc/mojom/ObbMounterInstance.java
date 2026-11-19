package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ObbMounterInstance extends Interface {
    public static final Interface.Manager<ObbMounterInstance, Proxy> MANAGER = ObbMounterInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends ObbMounterInstance, Interface.Proxy {
    }

    void init(ObbMounterHost obbMounterHost, InitResponse initResponse);
}

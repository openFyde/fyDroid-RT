package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface PipInstance extends Interface {
    public static final Interface.Manager<PipInstance, Proxy> MANAGER = PipInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends PipInstance, Interface.Proxy {
    }

    void closePip();

    void init(PipHost pipHost, InitResponse initResponse);

    void setPipSuppressionStatus(boolean z);
}

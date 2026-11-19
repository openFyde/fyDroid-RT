package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface BootPhaseMonitorInstance extends Interface {
    public static final Interface.Manager<BootPhaseMonitorInstance, Proxy> MANAGER = BootPhaseMonitorInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends BootPhaseMonitorInstance, Interface.Proxy {
    }

    void init(BootPhaseMonitorHost bootPhaseMonitorHost, InitResponse initResponse);
}

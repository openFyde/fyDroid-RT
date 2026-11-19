package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface BootPhaseMonitorHost extends Interface {
    public static final Interface.Manager<BootPhaseMonitorHost, Proxy> MANAGER = BootPhaseMonitorHost_Internal.MANAGER;

    public interface Proxy extends BootPhaseMonitorHost, Interface.Proxy {
    }

    void onBootCompleted();
}

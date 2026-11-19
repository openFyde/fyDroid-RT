package org.chromium.chromeos.mojo_service_manager.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ServiceObserver extends Interface {
    public static final Interface.Manager<ServiceObserver, Proxy> MANAGER = ServiceObserver_Internal.MANAGER;

    public interface Proxy extends ServiceObserver, Interface.Proxy {
    }

    void onServiceEvent(ServiceEvent serviceEvent);
}

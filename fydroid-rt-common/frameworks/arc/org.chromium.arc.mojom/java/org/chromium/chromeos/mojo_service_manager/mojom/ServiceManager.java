package org.chromium.chromeos.mojo_service_manager.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo_base.mojom.TimeDelta;

public interface ServiceManager extends Interface {
    public static final Interface.Manager<ServiceManager, Proxy> MANAGER = ServiceManager_Internal.MANAGER;

    public interface Proxy extends ServiceManager, Interface.Proxy {
    }

    public interface QueryResponse extends Callbacks.Callback1<ErrorOrServiceState> {
    }

    void addServiceObserver(ServiceObserver serviceObserver);

    void query(String str, QueryResponse queryResponse);

    void register(String str, ServiceProvider serviceProvider);

    void request(String str, TimeDelta timeDelta, MessagePipeHandle messagePipeHandle);
}

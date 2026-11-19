package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AdbdMonitorInstance extends Interface {
    public static final Interface.Manager<AdbdMonitorInstance, Proxy> MANAGER = AdbdMonitorInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends AdbdMonitorInstance, Interface.Proxy {
    }

    void init(AdbdMonitorHost adbdMonitorHost, InitResponse initResponse);
}

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AdbdMonitorHost extends Interface {
    public static final Interface.Manager<AdbdMonitorHost, Proxy> MANAGER = AdbdMonitorHost_Internal.MANAGER;

    public interface Proxy extends AdbdMonitorHost, Interface.Proxy {
    }

    void adbdStarted();

    void adbdStopped();
}

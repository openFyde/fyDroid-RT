package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SystemStateHost extends Interface {
    public static final Interface.Manager<SystemStateHost, Proxy> MANAGER = SystemStateHost_Internal.MANAGER;

    public interface Proxy extends SystemStateHost, Interface.Proxy {
    }

    void updateAppRunningState(SystemAppRunningState systemAppRunningState);
}

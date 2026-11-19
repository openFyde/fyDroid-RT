package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface PipHost extends Interface {
    public static final Interface.Manager<PipHost, Proxy> MANAGER = PipHost_Internal.MANAGER;

    public interface Proxy extends PipHost, Interface.Proxy {
    }

    void onPipEvent(int i);
}

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface CrashCollectorInstance extends Interface {
    public static final Interface.Manager<CrashCollectorInstance, Proxy> MANAGER = CrashCollectorInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends CrashCollectorInstance, Interface.Proxy {
    }

    void init(CrashCollectorHost crashCollectorHost, InitResponse initResponse);
}
